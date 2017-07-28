package sbtavro

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

/**
 * Created by jeromewacongne on 06/08/2015.
 */
class SbtAvroSpec extends Specification {
  val sourceDir = new File(getClass.getClassLoader.getResource("avro").toURI)
  val targetDir = new File(sourceDir.getParentFile, "generated")
  val sourceFiles = Seq(new File(sourceDir, "a.avsc"), new File(sourceDir, "b.avsc"), new File(sourceDir, "c.avsc"))

  "Schema files should be sorted with re-used types schemas first, whatever input order" >> {
    SbtAvro.sortSchemaFiles(sourceFiles) must beEqualTo(Seq(new File(sourceDir, "c.avsc"), new File(sourceDir, "b.avsc"), new File(sourceDir, "a.avsc")))
    SbtAvro.sortSchemaFiles(sourceFiles.reverse) must beEqualTo(Seq(new File(sourceDir, "c.avsc"), new File(sourceDir, "b.avsc"), new File(sourceDir, "a.avsc")))
  }

  "It should be possible to compile types depending on others if source files are provided in right order" >> {
    val parser = new Schema.Parser()
    val packageDir = new File(targetDir, "com/cavorite")
    val aJavaFile = new File(packageDir, "A.java")
    val bJavaFile = new File(packageDir, "B.java")
    val cJavaFile = new File(packageDir, "C.java")
    aJavaFile.delete()
    bJavaFile.delete()
    cJavaFile.delete()

    for(schemaFile <- SbtAvro.sortSchemaFiles(sourceFiles)) {
      SbtAvro.compileAvsc(schemaFile, targetDir, StringType.CharSequence, FieldVisibility.PUBLIC_DEPRECATED)
    }

    aJavaFile.isFile must beTrue
    bJavaFile.isFile must beTrue
    cJavaFile.isFile must beTrue
  }
}
