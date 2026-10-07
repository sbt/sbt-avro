ThisBuild / scalaVersion := "2.13.11"

lazy val `basic` = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(
    // the avro version under test is set from the scripted test
    // avro 1.8 & 1.9 generate joda-time based code for date/time logical types
    libraryDependencies += "joda-time" % "joda-time" % "2.12.7"
  )
