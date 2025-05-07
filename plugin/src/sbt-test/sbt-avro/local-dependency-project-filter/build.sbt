val checkUnpacked = TaskKey[Unit]("checkUnpacked")
val checkGenerated = TaskKey[Unit]("checkGenerated")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")
def absent(f: File): Unit = assert(!f.exists(), s"$f does exists")

lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  scalaVersion := "2.13.15"
)

lazy val avroSettings = Seq(
  crossScalaVersions := Seq.empty,
  crossPaths := false,
  autoScalaLibrary := false,
  Compile / packageAvro / publishArtifact := true
)

lazy val `external`: Project = project
  .in(file("external"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroSettings)
  .settings(
    name := "external",
    version := "0.0.1-SNAPSHOT"
  )

lazy val `external2`: Project = project
  .in(file("external2"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroSettings)
  .settings(
    name := "external2",
    version := "0.0.1-SNAPSHOT"
  )

lazy val `transitive`: Project = project
  .in(file("transitive"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroSettings)
  .settings(
    name := "transitive",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies += ("com.github.sbt" % "external" % "0.0.1-SNAPSHOT" % "avro").classifier("avro").intransitive()
  )

lazy val `transitive2`: Project = project
  .in(file("transitive2"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(avroSettings)
  .settings(
    name := "transitive2",
    version := "0.0.1-SNAPSHOT"
  )


lazy val app: Project = project
  .in(file("app"))
  .enablePlugins(SbtAvro)
  .dependsOn(
    `transitive`,
    `transitive2` % "avro"
  )
  .settings(commonSettings)
  .settings(
    name := "app",
    crossScalaVersions := Seq("2.13.15", "2.12.20"),
    avroProjectIncludeFilter := inProjects(thisProjectRef.value, `transitive2`),
    libraryDependencies += ("com.github.sbt" % "external2" % "0.0.1-SNAPSHOT" % "avro").classifier("avro").intransitive(),
    Compile / checkGenerated := {
      // Check that transitive deps have not been unpacked or generated in `app` project
      absent(crossTarget.value / "src_managed" / "avro" / "main" / "external-avro" / "avsc.avsc")
      absent(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
      absent(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Avsc.java")

      // Compiled classes from `transitive` should still be available on classpath
      exists((`transitive` / crossTarget).value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
      exists((`transitive` / crossTarget).value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive" / "Avsc.java")

      // Check that external2 has been unpacked into `app` project
      exists(crossTarget.value / "src_managed" / "avro" / "main" / "external2-avro" / "avsc.avsc")
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external2" / "Avsc.java")

      // Check that transitive2 has been recompiled in `app` project
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "transitive2" / "Avsc.java")
    }
  )
