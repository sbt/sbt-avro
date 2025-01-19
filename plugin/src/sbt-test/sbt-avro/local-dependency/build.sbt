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
  Compile / packageDoc / publishArtifact := false
)

lazy val `external`: Project = project
  .in(file("external"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroOnlySettings)
  .settings(
    name := "external",
    version := "0.0.1-SNAPSHOT"
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
      ("com.github.sbt" % "external" % "0.0.1-SNAPSHOT" % "avro").classifier("avro").intransitive()
    )
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .dependsOn(`transitive` % "avro")
  .settings(commonSettings)
  .settings(
    name := "local-dependency",
    crossScalaVersions := Seq("2.13.15", "2.12.20"),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "4.20.9" % Test
    ),
    Compile / checkUnpacked := {
      exists(
        (`transitive` / crossTarget).value / "src_managed" / "avro" / "main" / "external-avro" / "avdl.avdl"
      )
      exists(
        (`transitive` / crossTarget).value / "src_managed" / "avro" / "main" / "external-avro" / "avpr.avpr"
      )
      exists(
        (`transitive` / crossTarget).value / "src_managed" / "avro" / "main" / "external-avro" / "avsc.avsc"
      )
    },
    Compile / checkGenerated := {
      exists(
        crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avdl.java"
      )
      exists(
        crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avpr.java"
      )
      exists(
        crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java"
      )
      exists(
        crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Avsc.java"
      )
    }
  )
