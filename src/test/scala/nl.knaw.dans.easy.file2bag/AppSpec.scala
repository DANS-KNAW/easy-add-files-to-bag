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
import java.util.UUID

import better.files.StringExtensions
import nl.knaw.dans.bag.v0.DansV0Bag
import nl.knaw.dans.easy.file2bag.fixture.FileSystemSupport
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Success

class AppSpec extends AnyFlatSpec with Matchers with FileSystemSupport {
  private val uuid = UUID.randomUUID()

  "addFiles" should "report saved file" in {
    val bag = createBagWithEmptyFilesXml

    new EasyAddFilesToBagApp(null).addFiles(
      bag.baseDir / "..",
      ((testDir / "twister-files").createDirectories() / "some.txt").writeText("") / "..",
      (testDir / "input.csv").writeText("p,-,s,r,id\nsome.txt,,NO,OPEN_ACCESS,easy-dataset:16"),
      (testDir / "f2v-output.csv").writeText(s"id,uuid\neasy-dataset:16,$uuid"),
      (testDir / "log.csv").path,
    ) shouldBe Success(s"1 records written to $testDir/log.csv")

    (testDir / "log.csv").contentAsString shouldBe
      s"""path,rights,fedoraId,comment
         |some.txt,OPEN_ACCESS,easy-dataset:16,saved with rights[] in $testDir/bags/$uuid/data/some.txt
         |""".stripMargin
    bag.data.list.toSeq.map(_.name) shouldBe Seq("some.txt")
  }

  private def createBagWithEmptyFilesXml = {
    for {
      bag <- DansV0Bag.empty(testDir / "bags" / uuid.toString)
      _ <- bag.addTagFile("<files/>".inputStream, Paths.get("metadata/files.xml"))
      _ <- bag.save()
    } yield bag
  }.getOrElse(fail("could not create test bag"))
}