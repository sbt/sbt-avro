sys.props.get("plugin.version") match {
  case Some(x) =>
    addSbtPlugin("com.github.sbt" % "sbt-avro" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro-compiler" % "1.12.0",
  // depend on test jar to get some generated records in the build
  "org.apache.avro" % "avro" % "1.11.3" classifier "tests"
)
