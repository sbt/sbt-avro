// plugin version
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / version := {
  val orig = (ThisBuild / version).value
  if (orig.endsWith("-SNAPSHOT")) "4.0.0-SNAPSHOT" else orig
}

// metadata
ThisBuild / organization := "com.github.sbt"
ThisBuild / organizationName := "sbt"
ThisBuild / organizationHomepage := Some(url("https://www.scala-sbt.org/"))
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-avro"))
ThisBuild / licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/main/LICENSE"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/sbt/sbt-avro"), "scm:git:git@github.com:sbt/sbt-avro.git")
)
ThisBuild / developers := List(
  Developer(
    id = "nevillelyh",
    name = "Neville Li",
    email = "@nevillelyh",
    url = url("https://www.lyh.me/")
  ),
  Developer(
    id = "RustedBones",
    name = "Michel Davit",
    email = "michel@davit.fr",
    url = url("https://michel.davit.fr")
  )
)

// sbt-github-actions
lazy val scala3 = "3.6.2"
lazy val scala212 = "2.12.20"
ThisBuild / scalaVersion := scala3
ThisBuild / crossScalaVersions := Seq(scala3, scala212)
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("compile", "test", "scripted"))
)
ThisBuild / githubWorkflowTargetBranches := Seq("main")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("11"))
ThisBuild / githubWorkflowTargetTags := Seq("v*")
ThisBuild / githubWorkflowBuildPreamble := Seq(
  WorkflowStep.Sbt(
    name = Some("Check formatting"),
    commands = List("scalafmtSbtCheck", "scalafmtCheckAll")
  )
)
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    name = Some("Release"),
    commands = List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

// compilers
ThisBuild / javacOptions ++= Seq("--release", "8")
ThisBuild / scalacOptions ++= Seq("-release", "8")

lazy val `sbt-avro-parent`: Project = project
  .in(file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(
    `sbt-avro-compiler-api`,
    `sbt-avro-compiler-bridge`,
    `sbt-avro`
  )

lazy val `sbt-avro-compiler-api`: Project = project
  .in(file("api"))
  .settings(
    crossPaths := false,
    autoScalaLibrary := false
  )

lazy val `sbt-avro-compiler-bridge`: Project = project
  .in(file("bridge"))
  .dependsOn(`sbt-avro-compiler-api`)
  .settings(
    crossPaths := false,
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
      Dependencies.Provided.AvroCompiler,
      Dependencies.Test.Specs2Core
    )
  )

lazy val `sbt-avro`: Project = project
  .in(file("plugin"))
  .dependsOn(
    `sbt-avro-compiler-api`,
    `sbt-avro-compiler-bridge` % "test"
  )
  .enablePlugins(BuildInfoPlugin, SbtPlugin)
  .settings(
    description := "Sbt plugin for compiling Avro sources",
    (pluginCrossBuild / sbtVersion) := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.5.0"
        case _      => "2.0.0-M3"
      }
    },
    scriptedSbt := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.10.7"
        case _      => "2.0.0-M3"
      }
    },
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.github.sbt.avro",
    scriptedLaunchOpts ++= Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    ),
    scriptedBufferLog := false
  )
