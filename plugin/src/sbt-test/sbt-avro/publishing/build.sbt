val checkUnpacked = TaskKey[Unit]("checkUnpacked")
val checkGenerated = TaskKey[Unit]("checkGenerated")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")
def absent(f: File): Unit = assert(!f.exists(), s"$f does exists")

lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  scalaVersion := "2.13.15"
)

lazy val javaOnlySettings = Seq(
  crossScalaVersions := Seq.empty,
  crossPaths := false,
  autoScalaLibrary := false,
)

lazy val `external`: Project = project
  .in(file("external"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(javaOnlySettings)
  .settings(
    name := "external",
    version := "0.0.1-SNAPSHOT",
    crossScalaVersions := Seq.empty,
    crossPaths := false,
    autoScalaLibrary := false,
    Compile / packageAvro / publishArtifact := true
  )

lazy val `transitive`: Project = project
  .in(file("transitive"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(javaOnlySettings)
  .settings(
    name := "transitive",
    version := "0.0.1-SNAPSHOT",
    Compile / packageAvro / publishArtifact := true,
    Test / publishArtifact := true,
    libraryDependencies ++= Seq(
      "com.github.sbt" % "external" % "0.0.1-SNAPSHOT" classifier "avro",
    )
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    crossScalaVersions := Seq("2.13.15", "2.12.20"),
    libraryDependencies ++= Seq(
      "com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" classifier "avro",
      "com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" % Test classifier "tests",
      "org.specs2" %% "specs2-core" % "4.20.9" % Test
    ),
    // add additional transitive test jar
    avroDependencyIncludeFilter := avroDependencyIncludeFilter.value || artifactFilter(name = "transitive", classifier = "tests"),
    // exclude specific avsc file
    Compile / avroUnpackDependencies / excludeFilter := (Compile / avroUnpackDependencies / excludeFilter).value || "exclude.avsc",

    Compile / checkUnpacked := {
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "avdl.avdl")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "avpr.avpr")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "avsc.avsc")
      absent(crossTarget.value / "src_managed" / "avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "exclude.avsc")
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "avsc.avsc")
    },
    Compile / checkGenerated := {
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avdl.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avpr.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Avsc.java")
    },
    Test / checkUnpacked := {
      exists(crossTarget.value / "src_managed" / "avro" / "test" / "test.avsc")
    },
    Test / checkGenerated := {
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "test" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Test.java")
    }
  )
