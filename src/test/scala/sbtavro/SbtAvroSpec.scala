package sbtavro

import java.io.File

import com.spotify.avro.mojo.AvroFileRef
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

/**
  * Created by jeromewacongne on 06/08/2015.
  */
class SbtAvroSpec extends Specification {
  val builder = DefaultSchemaParserBuilder.default()
  val sourceDir = new File(getClass.getClassLoader.getResource("avro").toURI)
  val targetDir = new File(sourceDir.getParentFile, "generated")

  val fullyQualifiedNames = Seq(
    new File(sourceDir, "a.avsc"),
    new File(sourceDir, "b.avsc"),
    new File(sourceDir, "c.avsc"),
    new File(sourceDir, "d.avsc"),
    new File(sourceDir, "e.avsc"))

  val simpleNames = Seq(
    new File(sourceDir, "_a.avsc"),
    new File(sourceDir, "_b.avsc"),
    new File(sourceDir, "_c.avsc"),
    new File(sourceDir, "_d.avsc"),
    new File(sourceDir, "_e.avsc"))

  val expectedOrderFullyQualifiedNames = Seq(
    new File(sourceDir, "c.avsc"),
    new File(sourceDir, "e.avsc"),
    new File(sourceDir, "d.avsc"),
    new File(sourceDir, "b.avsc"),
    new File(sourceDir, "a.avsc"))

  val expectedOrderSimpleNames = Seq(
    new File(sourceDir, "_c.avsc"),
    new File(sourceDir, "_e.avsc"),
    new File(sourceDir, "_d.avsc"),
    new File(sourceDir, "_b.avsc"),
    new File(sourceDir, "_a.avsc"))

  val sourceFiles = fullyQualifiedNames ++ simpleNames

  "It should be possible to compile types depending on others if source files are provided in right order" >> {
    val packageDir = new File(targetDir, "com/cavorite")

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
    SbtAvro.compileAvscs(refs, targetDir, StringType.CharSequence, FieldVisibility.PUBLIC_DEPRECATED, true, false, builder)

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

}
