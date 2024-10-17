import sbt._

object Dependencies {

  object Versions {
    val Avro = "1.12.0"
    val Specs2 = "4.20.8"
  }

  object Provided {
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "provided"
  }

  object Test {
    val Specs2Core = "org.specs2" %% "specs2-core" % Versions.Specs2 % "test"
  }
}
