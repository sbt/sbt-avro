// plugin version
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / version := {
  val orig = (ThisBuild / version).value
  if (orig.endsWith("-SNAPSHOT")) "4.0.1-SNAPSHOT" else orig
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
lazy val scala3 = "3.8.4"
lazy val scala212 = "2.12.21"
ThisBuild / scalaVersion := scala3
ThisBuild / crossScalaVersions := Seq(scala3, scala212)
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("compile", "test", "scripted"))
)
ThisBuild / githubWorkflowTargetBranches := Seq("main")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
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
ThisBuild / javacOptions ++= Seq("--release", "17")
ThisBuild / scalacOptions ++= Seq("-release", "17")

lazy val javaOnlySettings: Seq[Setting[?]] = Seq(
  crossPaths := false,
  autoScalaLibrary := false,
  crossScalaVersions := Seq(scala3),
  dependencyOverrides ++= Seq(
    // tests are written in scala.
    // pin the library to the compiler version so the transitive (older) one from
    // the test framework does not end up alone on the classpath
    "org.scala-lang" %% "scala3-library" % scalaVersion.value
  )
)

lazy val `sbt-avro-parent`: Project = project
  .in(file("."))
  .settings(
    publish / skip := true,
    crossScalaVersions := Nil
  )
  .aggregate(
    `sbt-avro-compiler-api`,
    `sbt-avro-compiler-bridge`,
    `sbt-avro`
  )

lazy val `sbt-avro-compiler-api`: Project = project
  .in(file("api"))
  .settings(javaOnlySettings)

lazy val `sbt-avro-compiler-bridge`: Project = project
  .in(file("bridge"))
  .dependsOn(`sbt-avro-compiler-api` % "provided")
  .settings(javaOnlySettings)
  .settings(
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
        case _      => "2.0.0"
      }
    },
    scriptedSbt := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.10.7"
        case _      => "2.0.7"
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
