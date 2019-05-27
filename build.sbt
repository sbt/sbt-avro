name := "sbt-avro-1.9"
organization := "com.cavorite"
description := "Sbt plugin for compiling Avro sources"

version := "1.1.7-SNAPSHOT"

sbtPlugin := true

scalaVersion := appConfiguration.value.provider.scalaProvider.version
scalacOptions in Compile ++= Seq("-deprecation")
crossSbtVersions := Seq("0.13.18", "1.2.8")

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.5",
  "org.apache.avro" % "avro" % "1.9.0",
  "org.apache.avro" % "avro-compiler" % "1.9.0",
  "org.specs2" %% "specs2-core" % "3.10.0" % "test"
)

licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/master/LICENSE"))
publishMavenStyle := false
bintrayOrganization := Some("sbt")
bintrayRepository := "sbt-plugin-releases"
bintrayPackage := name.value
bintrayReleaseOnPublish := false

enablePlugins(SbtPlugin)
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.name=" + name.value.replace('.', '-'), "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false
