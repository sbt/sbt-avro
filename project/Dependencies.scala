import sbt._

object Dependencies {

  object Versions {
    val Avro = "1.12.2"
    val Sbt2Compat = "0.2.0"
    val Slf4j = "2.0.19"
  }

  object Provided {
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "provided"
  }

  object Test {
    val AvroCompiler = "org.apache.avro" % "avro-compiler" % Versions.Avro % "test"
    val Slf4jSimple = "org.slf4j" % "slf4j-simple" % Versions.Slf4j % "test"
    def jupiter(version: String) = "com.github.sbt.junit" % "jupiter-interface" % version % "test"
  }

  object SbtPlugin {
    val Sbt2Compat = "com.github.sbt" % "sbt2-compat" % Versions.Sbt2Compat
  }
}
