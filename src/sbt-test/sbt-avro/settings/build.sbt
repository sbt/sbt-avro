name := "settings-test"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro-compiler" % "1.9.2",
  "org.specs2" %% "specs2-core" % "4.9.4" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
(Compile / avroSource) := (Compile / sourceDirectory).value / "avro_source"
avroGeneratedSource := sourceManaged.value / "avro"
