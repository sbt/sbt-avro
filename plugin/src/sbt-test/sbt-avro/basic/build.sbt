ThisBuild / scalaVersion := "2.13.11"

lazy val files = Seq(
  "A",
  "B",
  "C",
  "D",
  "E",
  "_A",
  "_B",
  "_C",
  "_D",
  "_E",
  "LogicalTypesTest",
  "LocalDateTimeTest"
)

lazy val testFiles = Seq(
  "X",
  "Y",
  "Z"
)

val checkGenerated = TaskKey[Unit]("checkGenerated")
val checkCompiled = TaskKey[Unit]("checkCompiled")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")

val checkSettings = inConfig(Compile)(
  Def.settings(
    checkGenerated := {
      files.foreach { f =>
        exists(
          crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / s"$f.java"
        )
      }
    },
    checkCompiled := {
      files.foreach { f =>
        exists(classDirectory.value / "com" / "github" / "sbt" / "avro" / "test" / s"$f.class")
      }
    }
  )
) ++ inConfig(Test)(
  Def.settings(
    checkGenerated := {
      testFiles.foreach { f =>
        exists(
          crossTarget.value / "src_managed" / "compiled_avro" / "test" / "com" / "github" / "sbt" / "avro" / "test" / s"$f.java"
        )
      }
    },
    checkCompiled := {
      testFiles.foreach { f =>
        exists(classDirectory.value / "com" / "github" / "sbt" / "avro" / "test" / s"$f.class")
      }
    }
  )
)

lazy val `basic` = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(checkSettings)
  .settings(
    // the avro version under test is set from the scripted test
    // avro 1.8 & 1.9 generate joda-time based code for date/time logical types
    libraryDependencies += "joda-time" % "joda-time" % "2.12.7"
  )
