import sbt.Keys.scalaVersion

lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  publishTo := Some(Opts.resolver.sonatypeReleases),
  scalaVersion := "2.13.6",
  libraryDependencies ++= Seq(
    "org.apache.avro" % "avro" % "1.11.0"
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
    libraryDependencies ++= Seq(
      "com.github.sbt" % "external" % "0.0.1-SNAPSHOT" classifier "avro",
    )
  )

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "publishing-test",
    avroDependencyIncludeFilter := avroDependencyIncludeFilter.value ||
      // add avro jar to unpack its json avsc schema
      moduleFilter(organization = "org.apache.avro", name = "avro"),
    libraryDependencies ++= Seq(
      "com.github.sbt" %% "transitive" % "0.0.1-SNAPSHOT" classifier "avro",
      "org.specs2" %% "specs2-core" % "4.12.12" % Test
    )
  )