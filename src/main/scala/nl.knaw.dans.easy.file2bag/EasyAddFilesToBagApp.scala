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

import java.io.InputStream
import java.nio.charset.Charset.defaultCharset
import java.nio.file.Paths
import java.util.UUID

import better.files.File
import nl.knaw.dans.bag.v0.DansV0Bag
import nl.knaw.dans.easy.file2bag.Command.FeedBackMessage
import org.apache.commons.csv.{ CSVFormat, CSVParser, CSVRecord }
import resource.managed

import scala.collection.JavaConverters._
import scala.util.{ Success, Try }
import scala.xml.XML

class EasyAddFilesToBagApp(configuration: Configuration) {
  def addFiles(bags: File, files: File, metadataCSV: File, datasetsCSV: File): Try[FeedBackMessage] = {

    def metadata2newPayload(datasets: Map[String, UUID])
                         (record: CSVRecord): Try[Unit] = {
      val bagDir = bags / datasets(record.get(4)).toString
      val filesXmlPath = Paths.get("metadata/files.xml")
      record.get(1).toUpperCase() match {
        case "YES" =>
          val rights = record.get(3)
          val payloadPath = record.get(0)
          for {
            bag <- DansV0Bag.read(bagDir)
            _ <- bag.addPayloadFile(files / payloadPath, Paths.get(payloadPath))
            filesXml <- Try(XML.loadFile((bagDir / "metadata/files.xml").toString()))
            newFilesXml: InputStream = ??? // FileItem(???, ???) // TODO add to filesXML
            _ <- bag.removeTagFile(filesXmlPath)
         //   _ <- bag.addTagFile(newFilesXml, filesXmlPath)
            _ <- bag.save
          } yield ()
        case _ => Success(())
      }
    }

    for {
      datasetMap <- parse(datasetsCSV, fedoraId2uuid).map(_.toMap)
      results <- parse(metadataCSV, metadata2newPayload(datasetMap))
    } yield ???
    ???
  }

  private def fedoraId2uuid(record: CSVRecord): (String, UUID) = {
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
