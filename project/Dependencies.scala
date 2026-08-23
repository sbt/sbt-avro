import sbt._

object Dependencies {

  object Versions {
    val Avro = "1.12.2"
    val Specs2 = "4.23.0"
    val Sbt2Compat = "0.2.0"
  }

  object Provided {
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "provided"
  }

  object Test {
    val Specs2Core = "org.specs2" %% "specs2-core" % Versions.Specs2 % "test"
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "test"
  }

  object SbtPlugin {
    val Sbt2Compat = "com.github.sbt" % "sbt2-compat" % Versions.Sbt2Compat
  }
}
