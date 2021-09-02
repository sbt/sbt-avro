import sbt._

object Dependencies {

  object Versions {
    val Avro = "1.10.2"
    val Specs2 = "4.12.7"
  }

  object Provided {
    val AvroCompiler =  "org.apache.avro" % "avro-compiler" % Versions.Avro % "provided"
  }

  object Test {
    val Spec2 = "org.specs2" %% "specs2-core" % Versions.Specs2 % "test"
  }
}
