/*
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

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import better.files.Dispose
import org.apache.commons.csv.{ CSVFormat, CSVPrinter }

import scala.util.Try

case class LogRecord(path: Path, rights: String, fedoraId: String, comment: String) {
  def print(printer: CSVPrinter): Try[Unit] = Try {
    printer.printRecord(path, rights, fedoraId, comment)
  }
}

object LogRecord {
  private val csvFormat: CSVFormat = CSVFormat.RFC4180
    .withHeader("path", "rights", "fedoraId", "comment")
    .withDelimiter(',')
    .withRecordSeparator('\n')
    .withAutoFlush(true)

  def disposablePrinter(logFile: Path): Dispose[CSVPrinter] = {
    new Dispose(LogRecord.csvFormat.print(logFile, StandardCharsets.UTF_8))
  }
}
