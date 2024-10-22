enablePlugins(SbtAvro)

name := "recompile-test"
scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  // depend on test jar to get some generated records in the build
  "org.apache.avro" % "avro" % avroVersion.value % "avro" classifier "tests",
  "org.specs2" %% "specs2-core" % "4.20.9" % Test
)

// sut custom output for cross-build sbt v1 & v2
avroGenerate / target := file("target") / "compiled_avro"
avroStringType := "String"
avroFieldVisibility := "public"
avroOptionalGetters := true
avroEnableDecimalLogicalType := false
Compile / avroSpecificRecords += "org.apache.avro.specific.TestRecordWithLogicalTypes"
