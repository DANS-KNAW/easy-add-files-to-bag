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
