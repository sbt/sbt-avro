name := "sbt-avro-1.8"
organization := "com.cavorite"
description := "Sbt plugin for compiling Avro sources"

version := "1.1.4-SNAPSHOT"

sbtPlugin := true

scalaVersion := appConfiguration.value.provider.scalaProvider.version
scalacOptions in Compile ++= Seq("-deprecation")
crossSbtVersions := Seq("0.13.16", "1.0.0")

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.2",
  "org.apache.avro" % "avro" % "1.8.2",
  "org.apache.avro" % "avro-compiler" % "1.8.2",
  "org.specs2" %% "specs2-core" % "3.9.4" % "test"
)

licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/master/LICENSE"))
publishMavenStyle := false
bintrayOrganization := Some("sbt")
bintrayRepository := "sbt-plugin-releases"
bintrayPackage := name.value
bintrayReleaseOnPublish := false

ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.name=" + name.value.replace('.', '-'), "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false
