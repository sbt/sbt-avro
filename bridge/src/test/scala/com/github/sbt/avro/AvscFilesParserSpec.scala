package com.github.sbt.avro

import org.apache.avro.Schema
import org.specs2.mutable.Specification

import java.io.File
import java.nio.file.Files
import scala.collection.JavaConverters._

class AvscFilesParserSpec extends Specification {
  val sourceDir = new File(getClass.getClassLoader.getResource("avro").toURI)

  val targetDir = Files.createTempDirectory("sbt-avro-compiler-bridge").toFile
  val packageDir = new File(targetDir, "com/github/sbt/avro/test")

  "It should be possible to compile types depending on others if source files are provided in right order" >> {
    val parser = new AvscFilesParser()
    val fullyQualifiedNames = Seq(
      new File(sourceDir, "a.avsc"),
      new File(sourceDir, "b.avsc"),
      new File(sourceDir, "c.avsc"),
      new File(sourceDir, "d.avsc"),
      new File(sourceDir, "e.avsc")
    )

    val simpleNames = Seq(
      new File(sourceDir, "_a.avsc"),
      new File(sourceDir, "_b.avsc"),
      new File(sourceDir, "_c.avsc"),
      new File(sourceDir, "_d.avsc"),
      new File(sourceDir, "_e.avsc")
    )

    val sourceFiles = fullyQualifiedNames ++ simpleNames
    val schemas = parser.parseFiles(sourceFiles.asJava)
    val names = schemas.asScala.map(_.getFullName)
    names must contain(
      exactly(
        "com.github.sbt.avro.test.A",
        "com.github.sbt.avro.test._A",
        "com.github.sbt.avro.test.B",
        "com.github.sbt.avro.test._B",
        "com.github.sbt.avro.test.C",
        "com.github.sbt.avro.test._C",
        "com.github.sbt.avro.test.D",
        "com.github.sbt.avro.test._D",
        "com.github.sbt.avro.test.E",
        "com.github.sbt.avro.test._E"
      )
    )
  }

  "It should be possible to compile types depending on others if classes" >> {
    val parser = new AvscFilesParser()
    // TestSpecificRecordParent depends on TestSpecificRecord
    val dependant = new Schema.Parser().parse(
      """{
          |  "name": "TestSpecificRecord",
          |  "namespace": "com.github.sbt.avro",
          |  "type": "record",
          |  "fields": [
          |    {
          |      "name": "value",
          |      "type": "string"
          |    }
          |  ]
          |}""".stripMargin
    )
    val parent = new File(sourceDir, "test_records.avsc")
    parser.addTypes(Seq(dependant).asJava)
    val schemas = parser.parseFiles(Seq(parent).asJava)
    val names = schemas.asScala.map(_.getFullName)
    names must contain(exactly("com.github.sbt.avro.TestSpecificRecordParent"))
  }

}
