package sbtavro

import java.io.File
import scala.collection.mutable
import scala.io.Source

import org.apache.avro.{Protocol, Schema}
import org.apache.avro.compiler.idl.Idl
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData.StringType

import sbt._
import sbt.ConfigKey.configurationToKey
import sbt.Keys.{cacheDirectory, classpathTypes, cleanFiles, ivyConfigurations, javaSource, libraryDependencies, managedClasspath, managedSourceDirectories, sourceDirectory, sourceGenerators, sourceManaged, streams, update, version}
import sbt.Scoped.t2ToTable2

/**
 * Simple plugin for generating the Java sources for Avro schemas and protocols.
 */
object SbtAvro extends AutoPlugin {

  object autoImport {

    val avroConfig = config("avro")

    val stringType = SettingKey[String]("string-type", "Type for representing strings. " +
      "Possible values: CharSequence, String, Utf8. Default: CharSequence.")

    val fieldVisibility = SettingKey[String]("field-visibiliy", "Field Visibility for the properties" +
      "Possible values: private, public, public_deprecated. Default: public_deprecated.")

    val generate = TaskKey[Seq[File]]("generate", "Generate the Java sources for the Avro files.")

    lazy val avroSettings: Seq[Setting[_]] = inConfig(avroConfig)(Seq[Setting[_]](
      sourceDirectory := (sourceDirectory in Compile).value / "avro",
      javaSource := (sourceManaged in Compile).value / "compiled_avro",
      stringType := "CharSequence",
      fieldVisibility := "public_deprecated",
      version := "1.8.1",

      managedClasspath := {
        Classpaths.managedJars(avroConfig, classpathTypes.value, update.value)
      },
      generate := sourceGeneratorTask.value)
    ) ++ Seq[Setting[_]](
      sourceGenerators in Compile += (generate in avroConfig).taskValue,
      managedSourceDirectories in Compile += (javaSource in avroConfig).value,
      cleanFiles += (javaSource in avroConfig).value,
      libraryDependencies += "org.apache.avro" % "avro-compiler" % (version in avroConfig).value,
      ivyConfigurations += avroConfig
    )
  }

  import autoImport._
  override def requires = sbt.plugins.JvmPlugin

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings = avroSettings

  private[this] def compile(srcDir: File, target: File, log: Logger, stringTypeName: String, fieldVisibilityName: String) = {
    val stringType = StringType.valueOf(stringTypeName);
    log.info("Avro compiler using stringType=%s".format(stringType));

    val schemaParser = new Schema.Parser();

    for (idl <- (srcDir ** "*.avdl").get) {
      log.info("Compiling Avro IDL %s".format(idl))
      val parser = new Idl(idl.asFile)
      val protocol = Protocol.parse(parser.CompilationUnit.toString)
      val compiler = new SpecificCompiler(protocol)
      compiler.setStringType(stringType)
      compiler.setFieldVisibility(SpecificCompiler.FieldVisibility.valueOf(fieldVisibilityName.toUpperCase))
      compiler.compileToDestination(null, target)
    }

    for (schemaFile <- sortSchemaFiles((srcDir ** "*.avsc").get)) {
      log.info("Compiling Avro schema %s".format(schemaFile))
      val schemaAvr = schemaParser.parse(schemaFile)
      val compiler = new SpecificCompiler(schemaAvr)
      compiler.setStringType(stringType)
      compiler.compileToDestination(null, target)
    }

    for (protocol <- (srcDir ** "*.avpr").get) {
      log.info("Compiling Avro protocol %s".format(protocol))
      SpecificCompiler.compileProtocol(protocol.asFile, target)
    }

    (target ** "*.java").get.toSet
  }

  private def sourceGeneratorTask = Def.task {
    val out = streams.value
    val srcDir = (sourceDirectory in avroConfig).value
    val cachedCompile = FileFunction.cached(out.cacheDirectory / "avro",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists) { (in: Set[File]) =>
        compile(srcDir, (javaSource in avroConfig).value, out.log, stringType.value, fieldVisibility.value)
      }
    cachedCompile((srcDir ** "*.av*").get.toSet).toSeq
  }

  def sortSchemaFiles(files: Traversable[File]): Seq[File] = {
    val reversed = mutable.MutableList.empty[File]
    var used: Traversable[File] = files
    while(!used.isEmpty) {
      val usedUnused = usedUnusedSchemas(used)
      reversed ++= usedUnused._2
      used = usedUnused._1
    }
    reversed.reverse.toSeq
  }

  def strContainsType(str: String, fullName: String): Boolean = {
    val typeRegex = "\\\"type\\\"\\s*:\\s*(\\\"" + fullName + "\\\")|(\\[[^\\]]*\\\"" + fullName + "\\\"\\])"
    typeRegex.r.findFirstIn(str).isDefined
  }

  def usedUnusedSchemas(files: Traversable[File]): (Traversable[File], Traversable[File]) = {
    val usedUnused = files.map { f =>
      val fullName = extractFullName(f)
      (f, files.count { candidate =>
        strContainsType(fileText(candidate), fullName)
      } )
    }.partition(_._2 > 0)
    (usedUnused._1.map(_._1), usedUnused._2.map(_._1))
  }

  def extractFullName(f: File): String = {
    val txt = fileText(f)
    val namespace = namespaceRegex.findFirstMatchIn(txt)
    val name = nameRegex.findFirstMatchIn(txt)
    if(namespace == None) {
      return name.get.group(1)
    } else {
      return s"${namespace.get.group(1)}.${name.get.group(1)}"
    }
  }

  def fileText(f: File): String = {
    val src = Source.fromFile(f)
    try {
      return src.getLines.mkString
    } finally {
      src.close()
    }
  }

  val namespaceRegex = "\\\"namespace\\\"\\s*:\\s*\"([^\\\"]+)\\\"".r
  val nameRegex = "\\\"name\\\"\\s*:\\s*\"([^\\\"]+)\\\"".r

}
