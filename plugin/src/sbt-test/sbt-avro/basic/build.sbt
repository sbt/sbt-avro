ThisBuild / scalaVersion := "2.13.11"

lazy val files = Seq(
  "A", "B", "C", "D", "E", "_A", "_B", "_C", "_D", "_E", "LogicalTypesTest", "LocalDateTimeTest"
)

lazy val testFiles = Seq(
  "X", "Y", "Z"
)

val checkGenerated = TaskKey[Unit]("checkGenerated")
val checkCompiled = TaskKey[Unit]("checkCompiled")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")

val checkSettings = inConfig(Compile)(Def.settings(
  checkGenerated := {
    files.foreach { f =>
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" /  s"$f.java")
    }
  },
  checkCompiled := {
    files.foreach { f =>
      exists(classDirectory.value / "com" / "github" / "sbt" / "avro" / "test" /  s"$f.class")
    }
  }
)) ++ inConfig(Test)(Def.settings(
  checkGenerated := {
    testFiles.foreach { f =>
      exists(crossTarget.value / "src_managed" / "compiled_avro" / "test" / "com" / "github" / "sbt" / "avro" / "test" /  s"$f.java")
    }
  },
  checkCompiled := {
    testFiles.foreach { f =>
      exists(classDirectory.value / "com" / "github" / "sbt" / "avro" / "test" /  s"$f.class")
    }
  }
))


lazy val `basic` = project
  .in(file("."))
  .aggregate(
    `basic8`,
    `basic9`,
    `basic10`,
    `basic11`,
    `basic12`
  )

lazy val `basic8` = project
  .in(file(".basic_8"))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    avroVersion := "1.8.2",
    libraryDependencies += "joda-time" % "joda-time" % "2.12.7",
    sourceDirectory := file(".") / "src"
  )

lazy val `basic9` = project
  .in(file(".basic_9"))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    avroVersion := "1.9.2",
    libraryDependencies += "joda-time" % "joda-time" % "2.12.7",
    sourceDirectory := file(".") / "src"
  )

lazy val `basic10` = project
  .in(file(".basic_10"))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    avroVersion := "1.10.0",
    sourceDirectory := file(".") / "src"
  )

lazy val `basic11` = project
  .in(file(".basic_11"))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    avroVersion := "1.11.3",
    sourceDirectory := file(".") / "src"
  )

lazy val `basic12` = project
  .in(file(".basic_12"))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    avroVersion := "1.12.0",
    sourceDirectory := file(".") / "src"
  )
