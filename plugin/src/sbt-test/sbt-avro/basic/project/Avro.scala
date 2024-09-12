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
      // favor shared folder if any
      Compile / avroSource := (crossProjectCrossType.value.sharedSrcDir(baseDirectory.value, "main") match {
        case Some(src) => src.getParentFile / "avro"
        case None => (Compile / avroSource).value
      }),
      Test / avroSource := (crossProjectCrossType.value.sharedSrcDir(baseDirectory.value, "test") match {
        case Some(src) => src.getParentFile / "avro"
        case None => (Compile / avroSource).value
      })
    )
}
