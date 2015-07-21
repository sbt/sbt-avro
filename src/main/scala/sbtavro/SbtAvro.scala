package sbtavro;

import java.io.File

import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.compiler.idl.Idl
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData.StringType

import sbt.Classpaths
import sbt.Compile
import sbt.ConfigKey.configurationToKey
import sbt.FileFunction
import sbt.FilesInfo
import sbt.Keys.cacheDirectory
import sbt.Keys.classpathTypes
import sbt.Keys.cleanFiles
import sbt.Keys.ivyConfigurations
import sbt.Keys.javaSource
import sbt.Keys.libraryDependencies
import sbt.Keys.managedClasspath
import sbt.Keys.managedSourceDirectories
import sbt.Keys.sourceDirectory
import sbt.Keys.sourceGenerators
import sbt.Keys.sourceManaged
import sbt.Keys.streams
import sbt.Keys.update
import sbt.Keys.version
import sbt.Logger
import sbt.Plugin
import sbt.Scoped.t2ToTable2
import sbt.Scoped.t5ToTable5
import sbt.Setting
import sbt.SettingKey
import sbt.TaskKey
import sbt.config
import sbt.globFilter
import sbt.inConfig
import sbt.richFile
import sbt.singleFileFinder
import sbt.toGroupID

/**
 * Simple plugin for generating the Java sources for Avro schemas and protocols.
 */
object SbtAvro extends Plugin {
  val avroConfig = config("avro")

  val stringType = SettingKey[String]("string-type", "Type for representing strings. " +
    "Possible values: CharSequence, String, Utf8. Default: CharSequence.")

  val fieldVisibility = SettingKey[String]("field-visibiliy", "Field Visibility for the properties" +
    "Possible values: private, public, public_deprecated. Default: public_deprecated.")

  val generate = TaskKey[Seq[File]]("generate", "Generate the Java sources for the Avro files.")

  lazy val avroSettings: Seq[Setting[_]] = inConfig(avroConfig)(Seq[Setting[_]](
    sourceDirectory <<= (sourceDirectory in Compile) { _ / "avro" },
    javaSource <<= (sourceManaged in Compile) { _ / "compiled_avro" },
    stringType := "CharSequence",
    fieldVisibility := "public_deprecated",
    version := "1.7.3",

    managedClasspath <<= (classpathTypes, update) map { (ct, report) =>
      Classpaths.managedJars(avroConfig, ct, report)
    },

    generate <<= sourceGeneratorTask)) ++ Seq[Setting[_]](
    sourceGenerators in Compile <+= (generate in avroConfig),
    managedSourceDirectories in Compile <+= (javaSource in avroConfig),
    cleanFiles <+= (javaSource in avroConfig),
    libraryDependencies <+= (version in avroConfig)("org.apache.avro" % "avro-compiler" % _),
    ivyConfigurations += avroConfig)

  private def compile(srcDir: File, target: File, log: Logger, stringTypeName: String, fieldVisibilityName: String) = {
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

    for (schema <- (srcDir ** "*.avsc").get) {
      log.info("Compiling Avro schema %s".format(schema))
      val schemaAvr = schemaParser.parse(schema.asFile)
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

  private def sourceGeneratorTask = (streams,
    sourceDirectory in avroConfig,
    javaSource in avroConfig,
    stringType,
    fieldVisibility,
    cacheDirectory) map {
      (out, srcDir, targetDir, stringTypeName, fieldVisibilityName, cache) =>
        val cachedCompile = FileFunction.cached(cache / "avro",
          inStyle = FilesInfo.lastModified,
          outStyle = FilesInfo.exists) { (in: Set[File]) =>
            compile(srcDir, targetDir, out.log, stringTypeName, fieldVisibilityName)
          }
        cachedCompile((srcDir ** "*.av*").get.toSet).toSeq
    }

}
