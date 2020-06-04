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

import better.files.File
import org.rogach.scallop.{ ScallopConf, ScallopOption, ValueConverter, singleArgConverter }

class CommandLineOptions(args: Array[String], configuration: Configuration) extends ScallopConf(args) {
  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))
  printedName = "easy-add-files-to-bag"
  version(configuration.version)
  val description: String = s"""add files to existing bags"""
  val synopsis: String =
    s"""
       |  $printedName -b <bags-dir> -f <files-dir> -m <metadata-csv-file> -d <dataset-csv-file>""".stripMargin

  version(s"$printedName v${ configuration.version }")
  banner(
    s"""
       |  $description
       |
       |Usage:
       |
       |$synopsis
       |
       |Options:
       |""".stripMargin)

  private implicit val fileConverter: ValueConverter[File] = singleArgConverter[File](s =>
    File(s.replaceAll("^~", System.getProperty("user.home")))
  )

  val bags: ScallopOption[File] = opt[File](
    "bags", noshort = false, required = true,
    descr = "Directory containing existing bags"
  )
  val files: ScallopOption[File] = opt[File](
    "files", noshort = false, required = true,
    descr = "Directory containing files specified in the path column of the metadata CSV"
  )
  val metadata: ScallopOption[File] = opt[File](
    "metadata", noshort = false, required = true,
    descr = "Existing CSV file specifying the files and metadata to add to the bags"
  )
  val datasets: ScallopOption[File] = opt[File](
    "datasets", noshort = false, required = true,
    descr = "Existing CSV file mapping fedora-IDs to UUID-s"
  )
  Seq(bags, files).foreach(fileOption =>
    validateFileIsDirectory(fileOption.map(_.toJava))
  )
  Seq(metadata, datasets).foreach(fileOption =>
    validateFileIsDirectory(fileOption.map(_.toJava))
  )

  footer("")
}
