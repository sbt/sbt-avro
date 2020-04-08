sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.cavorite" % "sbt-avro" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
libraryDependencies += "org.apache.avro" % "avro-compiler" % "1.9.2"
