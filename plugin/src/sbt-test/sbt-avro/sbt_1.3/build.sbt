enablePlugins(SbtAvro)

name := "basic-test"
scalaVersion := "2.13.11"
libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value
