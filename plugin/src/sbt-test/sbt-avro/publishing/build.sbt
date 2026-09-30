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
      // when using avro scope, it won't be part of the pom dependencies -> intransitive
      // to declare transitive dependency use the compile scope
      ("com.github.sbt" % "external" % "0.0.1-SNAPSHOT").classifier("avro")
    ),
    Compile / avroDependencyIncludeFilter := artifactFilter(classifier = "avro"),
    // create a test jar with a schema as resource
    Test / packageBin / publishArtifact := true
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    crossScalaVersions := Seq("2.13.15", "2.12.21"),
    libraryDependencies ++= Seq(
      ("com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" % "avro")
        .classifier("avro"), // external as transitive
      ("com.github.sbt" % "transitive" % "0.0.1-SNAPSHOT" % "avro-test")
        .classifier("tests")
        .intransitive(),
      "org.specs2" %% "specs2-core" % "4.23.0" % Test
    ),
    // add additional avro source test jar whithout avro classifier
    Test / avroDependencyIncludeFilter := artifactFilter(name = "transitive", classifier = "tests"),
    // exclude specific avsc file
    Compile / avroUnpackDependencies / excludeFilter ~= { filter => filter || "exclude.avsc" },
    // set custom output for cross-build sbt v1 & v2
    avroUnpackDependencies / target := baseDirectory.value / "target" / "avro",
    avroGenerate / target := baseDirectory.value / "target" / "compiled_avro"
  )
