name := "basic-test"
scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % avroCompilerVersion,
  "joda-time" % "joda-time" % "2.7" // marked as optional in avro pom
)
