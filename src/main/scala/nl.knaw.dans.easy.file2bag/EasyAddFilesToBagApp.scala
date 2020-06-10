/**
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.file2bag

import java.nio.charset.Charset.defaultCharset
import java.nio.file.{ Path, Paths }
import java.util.UUID

import better.files.{ File, StringExtensions }
import nl.knaw.dans.bag.v0.DansV0Bag
import nl.knaw.dans.easy.file2bag.Command.FeedBackMessage
import org.apache.commons.csv.{ CSVFormat, CSVParser, CSVPrinter, CSVRecord }
import resource.managed

import scala.collection.JavaConverters._
import scala.util.Try
import scala.xml.XML

class EasyAddFilesToBagApp(configuration: Configuration) {

  def addFiles(bags: File,
               files: File,
               metadataCSV: File,
               datasetsCSV: File,
               csvLogFile: Path,
              ): Try[FeedBackMessage] = {

    def addPayloadWithRights(input: MetadataRecord): LogRecord = {
      val bagDir = bags / input.bagId.toString
      val payloadSource = files / input.path.toString
      val filesXmlPath = Paths.get("metadata/files.xml")
      val ddmFile = bagDir / "metadata/dataset.xml"
      val triedString = for {
        bag <- DansV0Bag.read(bagDir)
        filesXml <- Try(XML.loadFile((bagDir / filesXmlPath.toString).toString()))
        _ <- bag.addPayloadFile(payloadSource, input.path)
        newFilesXml <- FileItem(filesXml, input.rights, ddmFile)
        _ <- bag.removeTagFile(filesXmlPath)
        _ <- bag.addTagFile(newFilesXml.serialize.inputStream, filesXmlPath)
        _ <- bag.save
      } yield s"saved with rights[] in $bagDir/data/${ input.path }"
      val comment = triedString.toEither.fold(s"FAILED: " + _, identity)
      LogRecord(input.path, input.rights, input.fedoraId, comment)
    }

    def execute(datasets: Map[String, UUID], printer: CSVPrinter)
               (inputCsvRecord: CSVRecord): Try[Unit] = {
      MetadataRecord(datasets, inputCsvRecord)
        .fold(identity, addPayloadWithRights)
        .print(printer)
    }

    LogRecord.disposablePrinter(csvLogFile).apply { printer =>
      for {
        datasets <- parse(datasetsCSV, fedoraToUuid)
        rows <- parse(metadataCSV, execute(datasets.toMap, printer))
      } yield s"${ rows.size } records written to ${ csvLogFile.toAbsolutePath }"
    }
  }

  private def fedoraToUuid(record: CSVRecord): (String, UUID) = {
    record.get(0) -> UUID.fromString(record.get(1))
  }

  def parse[T](file: File, extract: CSVRecord => T): Try[Seq[T]] = {
    managed(CSVParser.parse(file.toJava, defaultCharset(), CSVFormat.RFC4180))
      .map(parseCsv(_).map(extract))
      .tried
  }

  private def parseCsv(parser: CSVParser): Seq[CSVRecord] = {
    parser.asScala.toSeq.filter(_.asScala.nonEmpty).drop(1)
  }
}
