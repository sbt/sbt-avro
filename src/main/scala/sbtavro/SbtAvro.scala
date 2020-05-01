package sbtavro

import java.io.File
import java.util.concurrent.atomic.AtomicReference

import org.apache.avro.compiler.idl.Idl
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import org.apache.avro.{Protocol, Schema}
import sbt.Keys._
import sbt._

/**
 * Simple plugin for generating the Java sources for Avro schemas and protocols.
 */
object SbtAvro extends AutoPlugin {

  private val AvroAvrpFilter: NameFilter = "*.avpr"
  private val AvroAvdlFilter: NameFilter = "*.avdl"
  private val AvroAvscFilter: NameFilter = "*.avsc"
  private val AvroFilter: NameFilter = AvroAvscFilter | AvroAvdlFilter | AvroAvrpFilter

  object autoImport {
    // format: off
    val avroStringType = settingKey[String]("Type for representing strings. Possible values: CharSequence, String, Utf8. Default: CharSequence.")
    val avroEnableDecimalLogicalType = settingKey[Boolean]("Set to true to use java.math.BigDecimal instead of java.nio.ByteBuffer for logical type \"decimal\".")
    val avroFieldVisibility = settingKey[String]("Field visibility for the properties. Possible values: private, public, public_deprecated. Default: public_deprecated.")
    val avroUseNamespace = settingKey[Boolean]("Validate that directory layout reflects namespaces, i.e. src/main/avro/com/myorg/MyRecord.avsc.")
    val avroSource = settingKey[File]("Default Avro source directory.")

    val avroGenerate = taskKey[Seq[File]]("Generate Java sources for Avro schemas.")

    // settings to be applied for both Compile and Test
    lazy val configScopedSettings: Seq[Setting[_]] = Seq(
      avroSource := sourceDirectory.value / "avro",
      avroGenerate / target := sourceManaged.value / "compiled_avro",
      sourceDirectories += (avroGenerate / target).value,

      // source generation
      avroGenerate := sourceGeneratorTask(avroGenerate).value,
      sourceGenerators += avroGenerate.taskValue,
      compile := compile.dependsOn(avroGenerate).value,
      // clean
      clean := {
        schemaParser.set(new Schema.Parser())
        clean.value
      }
    )
  }

  import autoImport._

  override def trigger = allRequirements

  override def requires = sbt.plugins.JvmPlugin

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    avroStringType := "CharSequence",
    avroFieldVisibility := "public_deprecated",
    avroEnableDecimalLogicalType := true,
    avroUseNamespace := false
  )

  override lazy val projectSettings: Seq[Setting[_]] =
    Seq(Compile, Test).flatMap(c => inConfig(c)(configScopedSettings))

  def compileIdl(idl: File, target: File, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean) {
    val parser = new Idl(idl)
    val protocol = Protocol.parse(parser.CompilationUnit.toString)
    val compiler = new SpecificCompiler(protocol)
    compiler.setStringType(stringType)
    compiler.setFieldVisibility(fieldVisibility)
    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
    compiler.compileToDestination(null, target)
  }

  val schemaParser = new AtomicReference(new Schema.Parser())

  def compileAvscs(srcDir: File, target: File, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean, useNamespace: Boolean) {
    import com.spotify.avro.mojo._
    val refs = (srcDir ** AvroAvscFilter).get.map { avsc =>
      sbt.ConsoleLogger().info("Compiling Avro schemas %s".format(avsc))
      new AvroFileRef(srcDir, avsc.relativeTo(srcDir).get.toString)
    }

    val global = schemaParser.get()
    // copy of global schemaParser to avoid race condition
    val parser = new Schema.Parser()
      .addTypes(global.getTypes)
      .setValidate(global.getValidate)
      .setValidateDefaults(global.getValidateDefaults)
    val compiler = new AvscFilesCompiler(parser)
    compiler.setStringType(stringType)
    compiler.setFieldVisibility(fieldVisibility)
    compiler.setUseNamespace(useNamespace)
    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
    compiler.setCreateSetters(true)
    compiler.setLogCompileExceptions(true)
    compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/")

    import scala.collection.JavaConverters._
    compiler.compileFiles(refs.toSet.asJava, target)
  }

  def compileAvpr(avpr: File, target: File, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean) {
    val protocol = Protocol.parse(avpr)
    val compiler = new SpecificCompiler(protocol)
    compiler.setStringType(stringType)
    compiler.setFieldVisibility(fieldVisibility)
    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
    compiler.compileToDestination(null, target)
  }

  private[this] def compileAvroSchema(srcDir: File, target: File, log: Logger, stringTypeName: String, fieldVisibilityName: String, enableDecimalLogicalType: Boolean, useNamespace: Boolean): Set[File] = {
    val stringType = StringType.valueOf(stringTypeName)
    val fieldVisibility = SpecificCompiler.FieldVisibility.valueOf(fieldVisibilityName.toUpperCase)
    log.info("Avro compiler using stringType=%s".format(stringType))

    for (idl <- (srcDir ** AvroAvdlFilter).get) {
      log.info("Compiling Avro IDL %s".format(idl))
      compileIdl(idl, target, stringType, fieldVisibility, enableDecimalLogicalType)
    }

    compileAvscs(srcDir, target, stringType, fieldVisibility, enableDecimalLogicalType, useNamespace)

    for (avpr <- (srcDir ** AvroAvrpFilter).get) {
      log.info("Compiling Avro protocol %s".format(avpr))
      compileAvpr(avpr, target, stringType, fieldVisibility, enableDecimalLogicalType)
    }

    (target ** "*.java").get.toSet
  }

  private def sourceGeneratorTask(key: TaskKey[Seq[File]]) = Def.task {
    val out = (key / streams).value
    val srcDir = (key / avroSource).value
    val outDir = (key / avroGenerate / target).value
    val strType = avroStringType.value
    val fieldVis = avroFieldVisibility.value
    val enbDecimal = avroEnableDecimalLogicalType.value
    val useNs = avroUseNamespace.value
    val cachedCompile = FileFunction.cached(out.cacheDirectory / "avro",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.exists) { (in: Set[File]) =>
        compileAvroSchema(srcDir, outDir, out.log, strType, fieldVis, enbDecimal, useNs)
      }
    cachedCompile((srcDir ** AvroFilter).get.toSet).toSeq
  }

}
