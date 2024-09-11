sys.props.get("plugin.version") match {
  case Some(x) =>
    addSbtPlugin("com.github.sbt" % "sbt-avro" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

val avroVersion = "1.12.0"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro-compiler" % avroVersion,
  // depend on test jar to get some generated records in the build
  "org.apache.avro" % "avro" % avroVersion classifier "tests"
)
