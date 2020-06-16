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

import java.nio.file.Paths

import better.files.File
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.{ Success, Try }
import scala.xml.XML

class FilesXmlSpec extends AnyFlatSpec with Matchers {

  private val oldFilesXml = Try {
    XML.loadString(File("src/test/resources/samples/files.xml").contentAsString)
  }.getOrElse(fail("could not load test data"))

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
