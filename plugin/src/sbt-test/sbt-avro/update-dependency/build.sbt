val checkGenerated = TaskKey[Unit]("checkGenerated")
val checkUpdated = TaskKey[Unit]("checkUpdated")

def exists(f: File): Unit = assert(f.exists(), s"$f does not exist")
def absent(f: File): Unit = assert(!f.exists(), s"$f does exists")

lazy val commonSettings = Seq(
  organization := "com.github.sbt",
  scalaVersion := "2.13.15"
)

lazy val `external`: Project = project
  .in(file("external"))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "external",
    crossPaths := false,
    autoScalaLibrary := false,
    // only create avro jar
    Compile / packageAvro / publishArtifact := true,
    Compile / packageBin / publishArtifact := false,
    Compile / packageSrc / publishArtifact := false,
    Compile / packageDoc / publishArtifact := false
  )

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(SbtAvro)
  .settings(commonSettings)
  .settings(
    name := "update-dependency-test",
    libraryDependencies ++= Seq(
      ("com.github.sbt" % "external" % (`external` / version).value % "avro").classifier("avro"),
    ),
    Compile / checkGenerated := {
       exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
    },
    Compile / checkUpdated := {
       absent(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc.java")
       exists(crossTarget.value / "src_managed" / "compiled_avro" / "main" / "com" / "github" / "sbt" / "avro" / "test" / "external" / "Avsc2.java")
    }
  )
