package nl.knaw.dans.easy.file2bag

import java.nio.file.{ Path, Paths }
import java.util.UUID

import cats.syntax.either._
import org.apache.commons.csv.CSVRecord
case class MetadataRecord(fedoraId: String, bagId: UUID, path: Path, rights: String)

object MetadataRecord {
  def apply(datasets: Map[String, UUID], input: CSVRecord): Either[LogRecord,MetadataRecord] = {
    val path = Paths.get(input.get(0))
    val skip = input.get(1).toUpperCase()
    val rights = input.get(3)
    val fedoraId = input.get(4)

    def create = datasets.get(fedoraId).map(uuid =>
      new MetadataRecord(fedoraId, uuid, path, rights).asRight
    ).getOrElse(LogRecord(path, rights, fedoraId, "no bag found").asLeft)

    skip match {
      case "YES" => create
      case _ => LogRecord(path,rights,fedoraId,"skipped").asLeft
    }
  }
}
