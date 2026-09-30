enablePlugins(SbtAvro)

name := "caching"
scalaVersion := "2.13.11"
// set custom output for cross-build sbt v1 & v2
avroGenerate / target := file("target") / "compiled_avro"
