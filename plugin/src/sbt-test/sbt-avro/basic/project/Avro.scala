import sbt._
import Keys._
import sbtcrossproject._
import com.github.sbt.avro.SbtAvro

case class Avro(version: String) extends Platform {

  import CrossPlugin.autoImport._
  import SbtAvro.autoImport._

  def identifier: String = "avro-" + version.split('.')(1)
  def sbtSuffix: String  = "Avro_" + version.split('.')(1)
  def enable(project: Project): Project = project
    .enablePlugins(SbtAvro)
    .settings(
      avroVersion := version,
      Compile / avroSources ++= crossProjectCrossType.value
        .sharedSrcDir(baseDirectory.value, "main")
        .map(_.getParentFile / "avro"),
      Test / avroSources ++= crossProjectCrossType.value
        .sharedSrcDir(baseDirectory.value, "test")
        .map(_.getParentFile / "avro"),
    )
}
