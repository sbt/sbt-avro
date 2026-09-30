lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  scalaVersion := "2.13.15",
  crossScalaVersions := Seq("2.13.15", "2.12.21")
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
    libraryDependencies ++= Seq(
      ("com.github.sbt" % "external" % "0.0.1-SNAPSHOT" % "avro").classifier("avro").intransitive()
    ),
    // set custom output for cross-build sbt v1 & v2
    avroUnpackDependencies / target := baseDirectory.value / "target" / "avro"
  )

lazy val `other`: Project = project
  .in(file("other"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "other"
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .dependsOn(
    `transitive` % "avro;avro-test->test",
    `other`, // compile scope only
  )
  .settings(commonSettings)
  .settings(
    name := "local-dependency",
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "4.23.0" % Test
    ),
    // set custom output for cross-build sbt v1 & v2
    avroGenerate / target := baseDirectory.value / "target" / "compiled_avro"
  )
