lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  publishTo := Some(Opts.resolver.sonatypeReleases),
  scalaVersion := "2.13.11",
  libraryDependencies ++= Seq(
    "org.apache.avro" % "avro" % avroCompilerVersion
  )
)

lazy val `external`: Project = project
  .in(file("external"))
  .settings(commonSettings)
  .settings(
    name := "external",
    version := "0.0.1-SNAPSHOT",
    crossPaths := false,
    autoScalaLibrary := false,
    Compile / packageAvro / publishArtifact := true
  )

lazy val `transitive`: Project = project
  .in(file("transitive"))
  .settings(commonSettings)
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
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    libraryDependencies ++= Seq(
      "com.github.sbt" %% "transitive" % "0.0.1-SNAPSHOT" classifier "avro",
      "com.github.sbt" %% "transitive" % "0.0.1-SNAPSHOT" % Test classifier "tests",
      "org.specs2" %% "specs2-core" % "4.20.4" % Test
    ),
    // add additional transitive test jar
    avroDependencyIncludeFilter := avroDependencyIncludeFilter.value || artifactFilter(name = "transitive_2.13", classifier = "tests"),
    // exclude specific avsc file
    Compile / avroUnpackDependencies / excludeFilter := (Compile / avroUnpackDependencies / excludeFilter).value || "exclude.avsc"
  )
