// plugin version
ThisBuild / dynverSonatypeSnapshots := true
ThisBuild / version := {
  val orig = (ThisBuild / version).value
  if (orig.endsWith("-SNAPSHOT")) "3.2.0-SNAPSHOT"
  else orig
}

// sbt-github-actions
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("compile", "test", "scripted"))
)
ThisBuild / githubWorkflowTargetBranches := Seq("master")
ThisBuild / githubWorkflowJavaVersions := Seq("1.8", "1.11")
ThisBuild / githubWorkflowTargetTags := Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(
  commands = List("ci-release"),
  env = Map(
    "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
    "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
    "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
    "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
  )
))

lazy val `sbt-avro`: Project = project
    .in(file("."))
    .enablePlugins(SbtPlugin)
    .settings(
      organization := "com.github.sbt",
      description := "Sbt plugin for compiling Avro sources",
      homepage := Some(url("https://github.com/sbt/sbt-avro")),
      pluginCrossBuild / sbtVersion := "1.2.8",
      Compile / scalacOptions ++= Seq("-deprecation"),
      libraryDependencies ++= Seq(
        Dependencies.Provided.AvroCompiler,
        Dependencies.Test.Spec2
      ),
      licenses += ("BSD 3-Clause", url("https://github.com/sbt/sbt-avro/blob/master/LICENSE")),
      scriptedLaunchOpts ++= Seq(
        "-Xmx1024M",
        "-Dplugin.version=" + version.value,
      ),
      scriptedBufferLog := false,
    )
