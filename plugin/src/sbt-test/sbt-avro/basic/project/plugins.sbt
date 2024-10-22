sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.github.sbt" % "sbt-avro" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}

addSbtPlugin("org.portable-scala" % "sbt-crossproject" % "1.3.2")
