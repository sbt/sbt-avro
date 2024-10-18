import scala.language.implicitConversions

import sbt._
import sbtcrossproject._

object AvroCrossPlugin extends sbt.AutoPlugin {
  object autoImport {

    implicit def AvroCrossProjectBuilderOps(builder: CrossProject.Builder): AvroCrossProjectOps =
      new AvroCrossProjectOps(builder.crossType(CrossType.Full))

    implicit class AvroCrossProjectOps(project: CrossProject) {
      def avro(version: String): Project = project.projects(Avro(version))

      def avroSettings(version: String)(ss: Def.SettingsDefinition*): CrossProject =
        avroConfigure(version)(_.settings(ss: _*))

      def avroEnablePlugins(version: String)(plugins: Plugins*): CrossProject =
        avroConfigure(version)(_.enablePlugins(plugins: _*))

      def avroConfigure(version: String)(transformer: Project => Project): CrossProject =
        project.configurePlatform(Avro(version))(transformer)
    }
  }
}
