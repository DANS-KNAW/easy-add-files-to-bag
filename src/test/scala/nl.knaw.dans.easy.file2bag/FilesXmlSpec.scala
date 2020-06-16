package nl.knaw.dans.easy.file2bag

import java.nio.file.Paths

import better.files.File
import nl.knaw.dans.easy.file2bag.fixture.CustomMatchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Success

class FilesXmlSpec extends AnyFlatSpec
  with Matchers
  with CustomMatchers {

  private val oldFilesXml =
      <files xmlns:dcterms="http://purl.org/dc/terms/" xmlns="http://easy.dans.knaw.nl/schemas/bag/metadata/files/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://purl.org/dc/terms/ http://dublincore.org/schemas/xmls/qdc/2008/02/11/dcterms.xsd http://easy.dans.knaw.nl/schemas/bag/metadata/files/ http://easy.dans.knaw.nl/schemas/bag/metadata/files/files.xsd">
          <file filepath="data/path/to/file.txt">
              <dcterms:format>text/plain</dcterms:format>
              <accessibleToRights>NONE</accessibleToRights>
              <visibleToRights>RESTRICTED_REQUEST</visibleToRights>
          </file>
          <file filepath="data/quicksort.hs">
              <dcterms:format>text/plain</dcterms:format>
          </file>
      </files>

  "apply" should "add an item to files.xml" in {
    val triedNode = FilesXml(oldFilesXml,
      rights = "",
      path = Paths.get("blabla.txt"),
      datasetXml = File("src/test/resources/samples/dataset.xml")
    )
    triedNode shouldBe a[Success[_]]
    (triedNode.get \ "file").theSeq.size shouldBe
      1 + (oldFilesXml \ "file").theSeq.size
  }
}
