import sbt._

object Dependencies {

  object Versions {
    val Avro = "1.12.0"
    val Specs2 = "4.20.9"
    val SbtUtilInterface = "1.10.1"
  }

  object Provided {
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "provided"
    val SbtUtilInterface =
      "org.scala-sbt" % "util-interface" % Versions.SbtUtilInterface % "provided"
  }

  object Test {
    val Specs2Core = "org.specs2" %% "specs2-core" % Versions.Specs2 % "test"
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "test"
  }
}
