import sbt._
import Keys._

object build extends Build {
    val sbtAvro = Project(
        id = "sbt-avro",
        base = file("."),
        settings = Defaults.defaultSettings ++ Seq[Project.Setting[_]](
            organization := "com.cavorite",
            version := "0.3.0",
            sbtPlugin := true,
            libraryDependencies ++= Seq(
                    "org.apache.avro" % "avro" % "1.7.3",
                    "org.apache.avro" % "avro-compiler" % "1.7.3"
            ),
            scalaVersion := "2.10.0",
            scalacOptions in Compile ++= Seq("-deprecation"),
            crossScalaVersions := Seq("2.9.2", "2.10.0"),
            description := "Sbt plugin for compiling Avro sources",
            publishTo := Some(Resolver.file("file",  new File( "/opt/local/www/files.cavorite.com/maven/" )) ),
            publishMavenStyle := true))
}
