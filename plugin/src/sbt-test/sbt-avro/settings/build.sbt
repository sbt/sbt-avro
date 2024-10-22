enablePlugins(SbtAvro)

name := "settings-test"
scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  // depend on test jar to get some generated records in the build
  "org.apache.avro" % "avro" % avroVersion.value % "avro" classifier "tests",
  "org.specs2" %% "specs2-core" % "4.20.9" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
avroOptionalGetters := true
avroEnableDecimalLogicalType := false
Compile / avroSpecificRecords += "org.apache.avro.specific.TestRecordWithLogicalTypes"
Compile / avroSource := (Compile / sourceDirectory).value / "avro_source"
Compile / avroGenerate / target := (Compile / sourceManaged).value
