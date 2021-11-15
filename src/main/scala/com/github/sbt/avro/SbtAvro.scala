package com.github.sbt.avro

import org.apache.avro.Protocol
import org.apache.avro.compiler.idl.Idl
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import sbt.Keys._
import sbt._
import CrossVersion.partialVersion
import Path.relativeTo
import com.github.sbt.avro.mojo.{AvroFileRef, SchemaParserBuilder}
import sbt.librarymanagement.DependencyFilter

import java.io.File

/**
 * Simple plugin for generating the Java sources for Avro schemas and protocols.
 */
object SbtAvro extends AutoPlugin {

  val AvroClassifier = "avro"

  private val avroCompilerVersion = classOf[SpecificCompiler].getPackage.getImplementationVersion

  private val AvroAvrpFilter: NameFilter = "*.avpr"
  private val AvroAvdlFilter: NameFilter = "*.avdl"
  private val AvroAvscFilter: NameFilter = "*.avsc"
  private val AvroFilter: NameFilter = AvroAvscFilter | AvroAvdlFilter | AvroAvrpFilter

  private val JavaFileFilter: NameFilter = "*.java"

  object autoImport {

    import Defaults._

    // format: off
    val avroStringType = settingKey[String]("Type for representing strings. Possible values: CharSequence, String, Utf8. Default: CharSequence.")
    val avroEnableDecimalLogicalType = settingKey[Boolean]("Set to true to use java.math.BigDecimal instead of java.nio.ByteBuffer for logical type \"decimal\".")
    val avroFieldVisibility = settingKey[String]("Field visibility for the properties. Possible values: private, public, public_deprecated. Default: public_deprecated.")
    val avroUseNamespace = settingKey[Boolean]("Validate that directory layout reflects namespaces, i.e. src/main/avro/com/myorg/MyRecord.avsc.")
    val avroOptionalGetters = settingKey[Boolean]("Set to true to generate getters that return Optional for nullable fields")
    val avroCreateSetters = settingKey[Boolean]("Set to false to not generate setters. Default: true")
    val avroSource = settingKey[File]("Default Avro source directory.")
    val avroIncludes = settingKey[Seq[File]]("Avro schema includes.")
    val avroSchemaParserBuilder = settingKey[SchemaParserBuilder](".avsc schema parser builder")
    val avroUnpackDependencies = taskKey[Seq[File]]("Unpack avro dependencies.")
    val avroDependencyIncludeFilter = settingKey[DependencyFilter]("Filter for including modules containing avro dependencies.")

    val avroGenerate = taskKey[Seq[File]]("Generate Java sources for Avro schemas.")
    val packageAvro = taskKey[File]("Produces an avro artifact, such as a jar containing avro schemas.")
    // format: on

    lazy val avroArtifactTasks: Seq[TaskKey[File]] = Seq(Compile, Test).map(_ / packageAvro)

    lazy val defaultSettings: Seq[Setting[_]] = Seq(
      avroDependencyIncludeFilter := artifactFilter(`type` = Artifact.SourceType, classifier = AvroClassifier),
      avroIncludes := Seq(),
      // addArtifact doesn't take publishArtifact setting in account
      artifacts ++= Classpaths.artifactDefs(avroArtifactTasks).value,
      packagedArtifacts ++= Classpaths.packaged(avroArtifactTasks).value,
    )

    // settings to be applied for both Compile and Test
    lazy val configScopedSettings: Seq[Setting[_]] = Seq(
      avroSource := sourceDirectory.value / "avro",
      // dependencies
      avroUnpackDependencies / includeFilter := AllPassFilter,
      avroUnpackDependencies / excludeFilter := HiddenFileFilter,
      avroUnpackDependencies / target := sourceManaged.value / "avro",
      avroUnpackDependencies := unpackDependenciesTask(avroUnpackDependencies).value,
      // source generation
      avroGenerate / target := sourceManaged.value / "compiled_avro",
      managedSourceDirectories += (avroGenerate / target).value,
      avroGenerate := sourceGeneratorTask(avroGenerate).dependsOn(avroUnpackDependencies).value,
      sourceGenerators += avroGenerate.taskValue,
      compile := compile.dependsOn(avroGenerate).value,
      // packaging
      packageAvro / artifactClassifier := Some(AvroClassifier),
      packageAvro / publishArtifact := false,
    ) ++ packageTaskSettings(packageAvro, packageAvroMappings) ++ Seq(
      packageAvro / artifact := (packageAvro / artifact).value.withType(Artifact.SourceType)
    )
  }

  import autoImport._

  def packageAvroMappings = Def.task {
    (avroSource.value ** AvroFilter) pair relativeTo(avroSource.value)
  }

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = sbt.plugins.JvmPlugin

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    avroStringType := "CharSequence",
    avroFieldVisibility := "public_deprecated",
    avroEnableDecimalLogicalType := true,
    avroUseNamespace := false,
    avroOptionalGetters := false,
    avroCreateSetters := true,
    avroSchemaParserBuilder := DefaultSchemaParserBuilder.default()
  )

  override lazy val projectSettings: Seq[Setting[_]] = defaultSettings ++
    Seq(Compile, Test).flatMap(c => inConfig(c)(configScopedSettings))

  private def unpack(deps: Seq[File],
                     extractTarget: File,
                     includeFilter: FileFilter,
                     excludeFilter: FileFilter,
                     streams: TaskStreams): Seq[File] = {
    def cachedExtractDep(jar: File): Seq[File] = {
      val cached = FileFunction.cached(
        streams.cacheDirectory / jar.name,
        inStyle = FilesInfo.lastModified,
        outStyle = FilesInfo.exists
      ) { deps =>
        IO.createDirectory(extractTarget)
        deps.flatMap { dep =>
          val filter = includeFilter -- excludeFilter
          val (avroSpecs, filtered) = IO
            .unzip(dep, extractTarget, AvroFilter)
            .partition(filter.accept)
          IO.delete(filtered)
          if (avroSpecs.nonEmpty) {
            streams.log.info("Extracted from " + dep + avroSpecs.mkString(":\n * ", "\n * ", ""))
          } else {
            streams.log.info(s"No Avro specification extracted from $dep")
          }
          avroSpecs
        }
      }
      cached(Set(jar)).toSeq
    }

    deps.flatMap(cachedExtractDep)
  }

  private def unpackDependenciesTask(key: TaskKey[Seq[File]]) = Def.task {
    val avroArtifacts = update
      .value
      .filter((key / avroDependencyIncludeFilter).value)
      .toSeq.map { case (_, _, _, file) => file }.distinct

    unpack(
      deps = avroArtifacts,
      extractTarget = (key / target).value,
      includeFilter = (key / includeFilter).value,
      excludeFilter = (key / excludeFilter).value,
      streams = (key / streams).value,
    )
  }

  def compileIdls(idls: Seq[File], target: File, log: Logger, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean, optionalGetters: Option[Boolean], createSetters: Boolean) = {
    idls.foreach { idl =>
      log.info(s"Compiling Avro IDL $idl")
      val parser = new Idl(idl)
      val protocol = Protocol.parse(parser.CompilationUnit.toString)
      val compiler = new SpecificCompiler(protocol)
      compiler.setStringType(stringType)
      compiler.setFieldVisibility(fieldVisibility)
      compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
      compiler.setCreateSetters(createSetters)
      optionalGetters.foreach(compiler.setGettersReturnOptional)
      optionalGetters.foreach(compiler.setOptionalGettersForNullableFieldsOnly)
      compiler.compileToDestination(null, target)
    }
  }

  def compileAvscs(refs: Seq[AvroFileRef], target: File, log: Logger, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean, useNamespace: Boolean, optionalGetters: Option[Boolean], createSetters: Boolean, builder: SchemaParserBuilder) = {
    import com.github.sbt.avro.mojo._

    import scala.collection.JavaConverters._
    val compiler = new AvscFilesCompiler(builder)
    compiler.setStringType(stringType)
    compiler.setFieldVisibility(fieldVisibility)
    compiler.setUseNamespace(useNamespace)
    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
    compiler.setCreateSetters(createSetters)
    optionalGetters.foreach(compiler.setOptionalGetters)
    compiler.setLogCompileExceptions(true)
    compiler.setTemplateDirectory("/org/apache/avro/compiler/specific/templates/java/classic/")

    refs.foreach { avsc =>
      log.info(s"Compiling Avro schemas $avsc")
    }
    compiler.compileFiles(refs.toSet.asJava, target)
  }

  def compileAvprs(avprs: Seq[File], target: File, log: Logger, stringType: StringType, fieldVisibility: FieldVisibility, enableDecimalLogicalType: Boolean, optionalGetters: Option[Boolean], createSetters: Boolean) = {
    avprs.foreach { avpr =>
      log.info(s"Compiling Avro protocol $avpr")
      val protocol = Protocol.parse(avpr)
      val compiler = new SpecificCompiler(protocol)
      compiler.setStringType(stringType)
      compiler.setFieldVisibility(fieldVisibility)
      compiler.setEnableDecimalLogicalType(enableDecimalLogicalType)
      compiler.setCreateSetters(createSetters)
      optionalGetters.foreach(compiler.setGettersReturnOptional)
      optionalGetters.foreach(compiler.setOptionalGettersForNullableFieldsOnly)
      compiler.compileToDestination(null, target)
    }
  }

  private[this] def compileAvroSchema(srcDirs: Seq[File],
                                      target: File,
                                      log: Logger,
                                      stringType: StringType,
                                      fieldVisibility: FieldVisibility,
                                      enableDecimalLogicalType: Boolean,
                                      useNamespace: Boolean,
                                      optionalGetters: Option[Boolean],
                                      createSetters: Boolean,
                                      builder: SchemaParserBuilder): Set[File] = {
    val avdls = srcDirs.flatMap(d => (d ** AvroAvdlFilter).get)
    val avscs = srcDirs.flatMap(d => (d ** AvroAvscFilter).get.map(avsc => new AvroFileRef(d, avsc.relativeTo(d).get.toString)))
    val avprs = srcDirs.flatMap(d => (d ** AvroAvrpFilter).get)

    compileIdls(avdls, target, log, stringType, fieldVisibility, enableDecimalLogicalType, optionalGetters, createSetters)
    compileAvscs(avscs, target, log, stringType, fieldVisibility, enableDecimalLogicalType, useNamespace, optionalGetters, createSetters, builder)
    compileAvprs(avprs, target, log, stringType, fieldVisibility, enableDecimalLogicalType, optionalGetters, createSetters)

    (target ** JavaFileFilter).get.toSet
  }

  private def sourceGeneratorTask(key: TaskKey[Seq[File]]) = Def.task {

    val out = (key / streams).value
    val srcDir = avroSource.value
    val externalSrcDir = (avroUnpackDependencies / target).value
    val includes = avroIncludes.value
    val srcDirs = Seq(externalSrcDir, srcDir) ++ includes
    val outDir = (key / target).value
    val strType = StringType.valueOf(avroStringType.value)
    val fieldVis = SpecificCompiler.FieldVisibility.valueOf(avroFieldVisibility.value.toUpperCase)
    val enbDecimal = avroEnableDecimalLogicalType.value
    val useNs = avroUseNamespace.value
    val createSetters = avroCreateSetters.value
    val optionalGetters = partialVersion(avroCompilerVersion) match {
      case Some((1, minor)) if minor >= 10 => Some(avroOptionalGetters.value)
      case _ => None
    }
    val builder = avroSchemaParserBuilder.value
    val cachedCompile = {
      FileFunction.cached(out.cacheDirectory / "avro", FilesInfo.lastModified, FilesInfo.exists) { _ =>
        out.log.info(s"Avro compiler $avroCompilerVersion using stringType=$strType")
        compileAvroSchema(srcDirs, outDir, out.log, strType, fieldVis, enbDecimal, useNs, optionalGetters, createSetters, builder)
      }
    }

    cachedCompile((srcDirs ** AvroFilter).get.toSet).toSeq
  }

}
