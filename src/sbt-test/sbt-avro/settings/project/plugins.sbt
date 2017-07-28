{
  val pluginName = System.getProperty("plugin.name")
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginName == null)
    throw new RuntimeException("""|The system property 'plugin.name' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else if (pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else addSbtPlugin("com.cavorite" % pluginName % pluginVersion)
}
