name := "settings-test"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.10.2",
  "org.specs2" %% "specs2-core" % "4.10.6" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
avroOptionalGetters := true
(Compile / avroSource) := (Compile / sourceDirectory).value / "avro_source"
(Compile / avroGenerate / target) := (Compile / sourceManaged).value / "avro"
