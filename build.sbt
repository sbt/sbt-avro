name := "sbt-avro-1.9"
organization := "com.cavorite"
description := "Sbt plugin for compiling Avro sources"
homepage := Some(url("https://github.com/sbt/sbt-avro"))

version := "1.1.10-SNAPSHOT"

sbtPlugin := true

scalaVersion := appConfiguration.value.provider.scalaProvider.version
scalacOptions in Compile ++= Seq("-deprecation")
crossSbtVersions := Seq("0.13.18", "1.3.4")


libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.5",
  "org.apache.avro" % "avro" % "1.9.1",
  "org.apache.avro" % "avro-compiler" % "1.9.1",
  {
    val v = if (scalaBinaryVersion.value == "2.10") "3.10.0" else "4.7.1"
    "org.specs2" %% "specs2-core" % v % "test"
  }
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
