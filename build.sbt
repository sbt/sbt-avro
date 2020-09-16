
lazy val avroVersion = "1.10.0"
lazy val specs2Version = "4.9.3"

// Provided configuration includes the lib at Runtime
// for testing purpose, we don't want sbt to have the
// compileonly libs anywhere in the classpath except for compile
// MANIFEST will however not make mention of the compileonly libs
val CompileOnly = config("compileonly").hide

ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / version := {
  val orig = (ThisBuild / version).value
  if (orig.endsWith("-SNAPSHOT")) "3.1.1-SNAPSHOT"
  else orig
}

lazy val `sbt-avro`: Project = project
    .in(file("."))
    .enablePlugins(SbtPlugin)
    .settings(
      organization := "com.cavorite",
      description := "Sbt plugin for compiling Avro sources",
      homepage := Some(url("https://github.com/sbt/sbt-avro")),

      sbtPlugin := true,
      pluginCrossBuild / sbtVersion := "1.2.8",

      scalaVersion := appConfiguration.value.provider.scalaProvider.version,
      scalacOptions in Compile ++= Seq("-deprecation"),

      ivyConfigurations += CompileOnly,
      libraryDependencies ++= Seq(
        "org.apache.avro" % "avro-compiler" % avroVersion % Provided,
        "org.specs2" %% "specs2-core" % specs2Version % Test
      ),
      unmanagedClasspath in Compile ++= update.value.select(configurationFilter(CompileOnly.name)),

      licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/master/LICENSE")),
      publishTo := (bintray / publishTo).value,
      publishMavenStyle := false,
      bintrayOrganization := Some("sbt"),
      bintrayRepository := "sbt-plugin-releases",
      bintrayPackage := "sbt-avro2",

      scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq(
        "-Xmx1024M",
        "-Dplugin.version=" + version.value,
      ),
      scriptedBufferLog := false,
    )
