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

import java.nio.file.Path

import better.files.File

import scala.util.Try
import scala.xml.{ Elem, Node, NodeSeq }

object FilesXml {

  private def filesXml(newItem: Node, items: NodeSeq): Elem =
    <files xmlns:dcterms="http://purl.org/dc/terms/"
           xmlns="http://easy.dans.knaw.nl/schemas/bag/metadata/files/"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://easy.dans.knaw.nl/schemas/bag/metadata/files/ https://easy.dans.knaw.nl/schemas/bag/metadata/files/files.xsd"
    >
    { items }
    { newItem }
    </files>

  /**
   * @param oldFilesXml the old metadata/dataset.xml of the bag
   * @param rights      from CVS input
   * @param datasetXml  default if nothing in input
   * @return
   */
  def apply(oldFilesXml: Elem, rights: String, path: Path, datasetXml: File): Try[Node] = Try {
    val visibleTo = "ANONYMOUS" // TODO if right is empty then from dataset
    val accessibleTo = "ANONYMOUS" // TODO what?
    val newItem =
      <file filepath={ "data/" + path }>
        <dcterms:title>{ path.getFileName }</dcterms:title>
        <!-- <dcterms:format>{ ??? }</dcterms:format> -->
        <accessibleToRights>{ accessibleTo }</accessibleToRights>
        <visibleToRights>{ visibleTo }</visibleToRights>
      </file>
    filesXml(newItem, oldFilesXml \ "file")
  }
}
