
lazy val avroVersion = "1.9.2"

def specs2Version(binaryVersion: String): String = binaryVersion match {
  case "2.10" => "3.10.0"
  case _ => "4.7.1"
}

// Provided configuration includes the lib at Runtime
// for testing purpose, we don't want sbt to have the
// compileonly libs anywhere in the classpath except for compile
// MANIFEST will however not make mention of the compileonly libs
val CompileOnly = config("compileonly").hide

lazy val `sbt-avro`: Project = project
    .in(file("."))
    .enablePlugins(SbtPlugin)
    .settings(
      organization := "com.cavorite",
      description := "Sbt plugin for compiling Avro sources",
      homepage := Some(url("https://github.com/sbt/sbt-avro")),

      version := "1.1.10-SNAPSHOT",

      sbtPlugin := true,

      scalaVersion := appConfiguration.value.provider.scalaProvider.version,
      scalacOptions in Compile ++= Seq("-deprecation"),

      ivyConfigurations += CompileOnly,
      libraryDependencies ++= Seq(
        "org.apache.avro" % "avro-compiler" % avroVersion % "compileonly,test",
        "org.specs2" %% "specs2-core" % specs2Version(scalaBinaryVersion.value) % Test
      ),
      unmanagedClasspath in Compile ++= update.value.select(configurationFilter(CompileOnly.name)),

      licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/master/LICENSE")),
      publishMavenStyle := false,
      bintrayOrganization := Some("sbt"),
      bintrayRepository := "sbt-plugin-releases",
      bintrayPackage := name.value,
      bintrayReleaseOnPublish := false,

      scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
        "-Xmx1024M",
        "-Dplugin.version=" + version.value,
      ),
      scriptedBufferLog := false,
    )

