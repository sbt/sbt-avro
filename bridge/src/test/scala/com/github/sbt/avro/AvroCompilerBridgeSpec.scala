package com.github.sbt.avro

import com.github.sbt.avro.test.{TestSpecificRecord, TestSpecificRecordParent}
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

import java.io.File
import java.nio.file.Files

class AvroCompilerBridgeSpec extends Specification {
  val sourceDir = new File(getClass.getClassLoader.getResource("avro").toURI)

  val targetDir = Files.createTempDirectory("sbt-avro-compiler-bridge").toFile
  val packageDir = new File(targetDir, "com/github/sbt/avro/test")

  "It should be possible to compile types depending on others if source files are provided in right order" >> {
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

    val aJavaFile = new File(packageDir, "A.java")
    val bJavaFile = new File(packageDir, "B.java")
    val cJavaFile = new File(packageDir, "C.java")
    val dJavaFile = new File(packageDir, "D.java")
    val eJavaFile = new File(packageDir, "E.java")

    val _aJavaFile = new File(packageDir, "_A.java")
    val _bJavaFile = new File(packageDir, "_B.java")
    val _cJavaFile = new File(packageDir, "_C.java")
    val _dJavaFile = new File(packageDir, "_D.java")
    val _eJavaFile = new File(packageDir, "_E.java")

    aJavaFile.delete()
    bJavaFile.delete()
    cJavaFile.delete()
    dJavaFile.delete()
    eJavaFile.delete()

    _aJavaFile.delete()
    _bJavaFile.delete()
    _cJavaFile.delete()
    _dJavaFile.delete()
    _eJavaFile.delete()

    val refs = sourceFiles.map(s => new AvroFileRef(sourceDir, s.getName))
    val compiler = new AvroCompilerBridge
    compiler.compileAvscs(
      refs.toArray,
      targetDir,
      StringType.CharSequence,
      FieldVisibility.PRIVATE,
      true,
      false,
      false,
      true
    )

    aJavaFile.isFile must beTrue
    bJavaFile.isFile must beTrue
    cJavaFile.isFile must beTrue
    dJavaFile.isFile must beTrue
    eJavaFile.isFile must beTrue

    _aJavaFile.isFile must beTrue
    _bJavaFile.isFile must beTrue
    _cJavaFile.isFile must beTrue
    _dJavaFile.isFile must beTrue
    _eJavaFile.isFile must beTrue
  }

  "It should be possible to compile types depending on others if classes are provided in right order" >> {
    // TestSpecificRecordParent and TestSpecificRecord were previously generated from test_records.avsc
    val compiler = new AvroCompilerBridge
    compiler.recompile(
      Array(
        // put parent 1st
        classOf[TestSpecificRecordParent],
        classOf[TestSpecificRecord]
      ),
      targetDir,
      StringType.CharSequence,
      FieldVisibility.PRIVATE,
      true,
      false,
      false,
      true
    )

    val record = new File(packageDir, "TestSpecificRecord.java")
    val recordParent = new File(packageDir, "TestSpecificRecordParent.java")

    record.isFile must beTrue
    recordParent.isFile must beTrue
  }

}
