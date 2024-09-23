package com.github.sbt.avro

import sbt.Keys.*
import sbt.*
import Path.relativeTo
import sbt.librarymanagement.DependencyFilter

import java.io.File
import java.net.URLClassLoader

/** Simple plugin for generating the Java sources for Avro schemas and protocols. */
object SbtAvro extends AutoPlugin {

  val Avro: Configuration = config("avro")
  val AvroClassifier = "avro"

  private[avro] val AvroAvrpFilter: NameFilter = "*.avpr"
  private[avro] val AvroAvdlFilter: NameFilter = "*.avdl"
  private[avro] val AvroAvscFilter: NameFilter = "*.avsc"
  private[avro] val AvroFilter: NameFilter = AvroAvscFilter | AvroAvdlFilter | AvroAvrpFilter

  private[avro] val JavaFileFilter: NameFilter = "*.java"

  object autoImport {

    import Defaults._

    // format: off
    val avroAdditionalDependencies = settingKey[Seq[ModuleID]]("Additional dependencies to be added to library dependencies.")
    val avroCompiler = settingKey[String]("Sbt avro compiler class. Default: com.github.sbt.avro.AvroCompilerBridge")
    val avroCreateSetters = settingKey[Boolean]("Generate setters. Default: true")
    val avroDependencyIncludeFilter = settingKey[DependencyFilter]("Filter for including modules containing avro dependencies.")
    val avroEnableDecimalLogicalType = settingKey[Boolean]("Use java.math.BigDecimal instead of java.nio.ByteBuffer for logical type \"decimal\". Default: true.")
    val avroFieldVisibility = settingKey[String]("Field visibility for the properties. Possible values: private, public. Default: public.")
    val avroOptionalGetters = settingKey[Boolean]("Generate getters that return Optional for nullable fields. Default: false.")
    val avroSpecificRecords = settingKey[Seq[String]]("List of avro records to recompile with current avro version and settings. Classes must be part of the Avro library dependencies.")
    val avroSources = settingKey[Seq[File]]("Avro source directories.")
    val avroStringType = settingKey[String]("Type for representing strings. Possible values: CharSequence, String, Utf8. Default: CharSequence.")
    val avroUnpackDependencies = taskKey[Seq[File]]("Unpack avro dependencies.")
    val avroUseNamespace = settingKey[Boolean]("Validate that directory layout reflects namespaces, i.e. src/main/avro/com/myorg/MyRecord.avsc. Default: false.")
    val avroVersion = settingKey[String]("Avro version to use in the project. default: 1.12.0")

    val avroGenerate = taskKey[Seq[File]]("Generate Java sources for Avro schemas.")
    val packageAvro = taskKey[File]("Produces an avro artifact, such as a jar containing avro schemas.")
    // format: on

    lazy val avroArtifactTasks: Seq[TaskKey[File]] = Seq(Compile, Test).map(_ / packageAvro)

    lazy val defaultSettings: Seq[Setting[_]] = Seq(
      // compiler
      avroCompiler := "com.github.sbt.avro.AvroCompilerBridge",
      avroCreateSetters := true,
      avroEnableDecimalLogicalType := true,
      avroFieldVisibility := "public",
      avroOptionalGetters := false,
      avroStringType := "CharSequence",
      avroUseNamespace := false,

      // dependency management
      avroDependencyIncludeFilter := artifactFilter(
        `type` = Artifact.SourceType,
        classifier = AvroClassifier
      ),
      // addArtifact doesn't take publishArtifact setting in account
      artifacts ++= Classpaths.artifactDefs(avroArtifactTasks).value,
      packagedArtifacts ++= Classpaths.packaged(avroArtifactTasks).value,
      // use a custom folders to avoid potential conflict with other generators
      avroUnpackDependencies / target := sourceManaged.value / "avro",
      avroGenerate / target := sourceManaged.value / "compiled_avro",
      // setup avro configuration. Use library management to fetch the compiler
      ivyConfigurations ++= Seq(Avro),
      avroVersion := "1.12.0",
      avroAdditionalDependencies := Seq(
        "com.github.sbt" % "sbt-avro-compiler-bridge" % BuildInfo.version % Avro,
        "org.apache.avro" % "avro-compiler" % avroVersion.value % Avro,
        "org.apache.avro" % "avro" % avroVersion.value
      ),
      libraryDependencies ++= avroAdditionalDependencies.value
    )

    // settings to be applied for both Compile and Test
    lazy val configScopedSettings: Seq[Setting[_]] = Seq(
      avroSources := Seq(sourceDirectory.value / "avro"),
      avroSpecificRecords := Seq.empty,
      // dependencies
      avroUnpackDependencies / includeFilter := AllPassFilter,
      avroUnpackDependencies / excludeFilter := HiddenFileFilter,
      avroUnpackDependencies / target := configSrcSub(avroUnpackDependencies / target).value,
      avroUnpackDependencies := unpackDependenciesTask(avroUnpackDependencies).value,
      // source generation
      avroGenerate / target := configSrcSub(avroGenerate / target).value,
      managedSourceDirectories += (avroGenerate / target).value,
      avroGenerate := sourceGeneratorTask(avroGenerate).dependsOn(avroUnpackDependencies).value,
      sourceGenerators += avroGenerate.taskValue,
      compile := compile.dependsOn(avroGenerate).value,
      // packaging
      packageAvro / artifactClassifier := Some(AvroClassifier),
      packageAvro / publishArtifact := false
    ) ++ packageTaskSettings(packageAvro, packageAvroMappings) ++ Seq(
      packageAvro / artifact := (packageAvro / artifact).value.withType(Artifact.SourceType)
    )
  }

  import autoImport._

  def packageAvroMappings: Def.Initialize[Task[Seq[(File, String)]]] = Def.task {
    avroSources.value.flatMap(src => (src ** AvroFilter).pair(relativeTo(src)))
  }

  override def trigger: PluginTrigger = noTrigger

  override def requires: Plugins = sbt.plugins.JvmPlugin

  override lazy val projectSettings: Seq[Setting[_]] = defaultSettings ++
    inConfig(Avro)(Defaults.configSettings) ++
    Seq(Compile, Test).flatMap(c => inConfig(c)(configScopedSettings))

  private def unpack(
    cacheBaseDirectory: File,
    deps: Seq[File],
    extractTarget: File,
    includeFilter: FileFilter,
    excludeFilter: FileFilter,
    streams: TaskStreams
  ): Seq[File] = {
    def cachedExtractDep(jar: File): Seq[File] = {
      val cached = FileFunction.cached(
        cacheBaseDirectory / jar.name,
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
    val cacheBaseDirectory = Defaults.makeCrossTarget(
      streams.value.cacheDirectory,
      scalaBinaryVersion.value,
      (pluginCrossBuild / sbtBinaryVersion).value,
      sbtPlugin.value,
      crossPaths.value
    )
    val conf = configuration.value.toConfigRef
    val avroArtifacts = update.value
      .filter(avroDependencyIncludeFilter.value)
      .toSeq
      .collect { case (`conf`, _, _, file) => file }

    unpack(
      cacheBaseDirectory = cacheBaseDirectory,
      deps = avroArtifacts,
      extractTarget = (key / target).value,
      includeFilter = (key / includeFilter).value,
      excludeFilter = (key / excludeFilter).value,
      streams = (key / streams).value
    )
  }

  private def sourceGeneratorTask(key: TaskKey[Seq[File]]) = Def.task {
    val out = (key / streams).value
    val externalSrcDir = (avroUnpackDependencies / target).value
    val srcDirs = Seq(externalSrcDir) ++ avroSources.value
    val outDir = (key / target).value

    val cachedCompile = {
      import sbt.util.CacheStoreFactory
      import sbt.util.CacheImplicits._

      val cacheStoreFactory = CacheStoreFactory(out.cacheDirectory / "avro")
      val lastCache = { (action: Option[Set[File]] => Set[File]) =>
        Tracked
          .lastOutput[Unit, Set[File]](cacheStoreFactory.make("last-cache")) { case (_, l) =>
            action(l)
          }
          .apply(())
      }
      val inCache = Difference.inputs(cacheStoreFactory.make("in-cache"), FileInfo.lastModified)
      val outCache = Difference.outputs(cacheStoreFactory.make("out-cache"), FileInfo.exists)

      (inputs: Set[File], records: Seq[String]) =>
        lastCache { lastCache =>
          inCache(inputs) { inReport =>
            outCache { outReport =>
              if (
                (lastCache.isEmpty && records.nonEmpty) || inReport.modified.nonEmpty || outReport.modified.nonEmpty
              ) {
                // compile if
                // - no previous cache and we have records to recompile
                // - input files have changed
                // - output files are missing

                // TODO Cache class loader
                val avroClassLoader = new URLClassLoader(
                  "AvroClassLoader",
                  (Avro / dependencyClasspath).value.map(_.data.toURI.toURL).toArray,
                  this.getClass.getClassLoader
                )

                val compiler = avroClassLoader
                  .loadClass(avroCompiler.value)
                  .getDeclaredConstructor()
                  .newInstance()
                  .asInstanceOf[AvroCompiler]

                compiler.setStringType(avroStringType.value)
                compiler.setFieldVisibility(avroFieldVisibility.value.toUpperCase)
                compiler.setUseNamespace(avroUseNamespace.value)
                compiler.setEnableDecimalLogicalType(avroEnableDecimalLogicalType.value)
                compiler.setCreateSetters(avroOptionalGetters.value)
                compiler.setOptionalGetters(avroCreateSetters.value)

                try {
                  val recs = records.map(avroClassLoader.loadClass)
                  val avdls = srcDirs.flatMap(d => (d ** AvroAvdlFilter).get)
                  val avscs = srcDirs.flatMap(d =>
                    (d ** AvroAvscFilter).get.map(avsc =>
                      new AvroFileRef(d, avsc.relativeTo(d).get.toString)
                    )
                  )
                  val avprs = srcDirs.flatMap(d => (d ** AvroAvrpFilter).get)

                  out.log.info(
                    s"Avro compiler ${avroVersion.value} using stringType=${avroStringType.value}"
                  )

                  compiler.recompile(recs.toArray, outDir)
                  compiler.compileIdls(avdls.toArray, outDir)
                  compiler.compileAvscs(avscs.toArray, outDir)
                  compiler.compileAvprs(avprs.toArray, outDir)

                  (outDir ** SbtAvro.JavaFileFilter).get.toSet
                } finally {
                  avroClassLoader.close()
                }
              } else {
                outReport.checked
              }
            }
          }
        }
    }

    cachedCompile((srcDirs ** AvroFilter).get.toSet, avroSpecificRecords.value).toSeq
  }

}
