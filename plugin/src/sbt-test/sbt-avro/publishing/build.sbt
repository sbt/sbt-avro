val checkUnpacked = TaskKey[Unit]("checkUnpacked")
val checkGenerated = TaskKey[Unit]("checkGenerated")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")
def absent(f: File): Unit = assert(!f.exists(), s"$f does exists")

lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  scalaVersion := "2.13.15"
)

lazy val avroOnlySettings = Seq(
  crossScalaVersions := Seq.empty,
  crossPaths := false,
  autoScalaLibrary := false,
  // only create avro jar
  Compile / packageAvro / publishArtifact := true,
  Compile / packageBin / publishArtifact := false,
  Compile / packageSrc / publishArtifact := false,
  Compile / packageDoc / publishArtifact := false,
)

lazy val `external`: Project = project
  .in(file("external"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroOnlySettings)
  .settings(
    name := "external",
    version := "0.0.1-SNAPSHOT",
  )

lazy val `transitive`: Project = project
  .in(file("transitive"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroOnlySettings)
  .settings(
    name := "transitive",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      // when using avro scope, it won't be part of the pom dependencies -> intransitive
      // to declare transitive dependency use the compile scope
      ("com.github.sbt" % "external" % "0.0.1-SNAPSHOT").classifier("avro")
    ),
    Compile / avroDependencyIncludeFilter := artifactFilter(classifier = "avro"),
    // create a test jar with a schema as resource
    Test / packageBin / publishArtifact := true,
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    crossScalaVersions := Seq("2.13.15", "2.12.20"),
    libraryDependencies ++= Seq(
      ("com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" % "avro").classifier("avro"), // external as transitive
      ("com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" % "avro-test").classifier("tests").intransitive(),
      "org.specs2" %% "specs2-core" % "4.20.9" % Test
    ),
    // add additional avro source test jar whithout avro classifier
    Test / avroDependencyIncludeFilter := artifactFilter(name = "transitive", classifier = "tests"),
    // exclude specific avsc file
    Compile / avroUnpackDependencies / excludeFilter ~= { filter => filter || "exclude.avsc" },
    Compile / checkUnpacked := {
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "external-avro" / "avdl.avdl")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "external-avro" / "avpr.avpr")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "external-avro" / "avsc.avsc")
      absent(crossTarget.value / "src_managed" / "avro" / "main" / "external-avro" / "exclude.avsc")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "transitive-avro" / "avsc.avsc")
    },
    Compile / checkGenerated := {
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avdl.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avpr.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Avsc.java")
    },
    Test / checkUnpacked := {
      exists(crossTarget.value / "src_managed" / "avro" / "test" / "transitive-tests" / "test.avsc")
    },
    Test / checkGenerated := {
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "test" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Test.java")
    }
  )
