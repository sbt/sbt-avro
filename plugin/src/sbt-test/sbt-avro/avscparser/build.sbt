lazy val parser = project
  .in(file("parser"))
  .settings(
    crossPaths := false,
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
      "com.github.sbt" % "sbt-avro-compiler-api" % sys.props("plugin.version") % "provided",
      "com.github.sbt" % "sbt-avro-compiler-bridge" % sys.props("plugin.version"),
      "org.apache.avro" % "avro-compiler" % "1.12.0"
    )
  )

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .dependsOn(parser % "avro-compiler")
  .settings(
    avroCompiler := "com.github.sbt.avro.CustomAvroCompiler"
  )
