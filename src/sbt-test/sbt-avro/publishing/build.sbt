import sbt.Keys.scalaVersion

lazy val commonSettings = Seq(
  organization := "com.cavorite",
  publishTo := Some(Opts.resolver.sonatypeReleases),
  libraryDependencies ++= Seq(
    "org.apache.avro" % "avro" % "1.9.2"
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
    scalaVersion := "2.12.11",
    Compile / packageAvro / publishArtifact := true,
    libraryDependencies ++= Seq(
      "com.cavorite" % "external" % "0.0.1-SNAPSHOT" classifier "avro",
    )
  )

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    scalaVersion := "2.12.11",
    avroDependencyIncludeFilter := avroDependencyIncludeFilter.value ||
      // add avro jar to unpack its json avsc schema
      moduleFilter(organization = "org.apache.avro", name = "avro"),
    libraryDependencies ++= Seq(
      "com.cavorite" %% "transitive" % "0.0.1-SNAPSHOT" classifier "avro",
      "org.specs2" %% "specs2-core" % "3.10.0" % "test"
    )
  )