name := "settings-test"
scalaVersion := "2.13.6"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.10.2",
  "org.specs2" %% "specs2-core" % "4.12.4-js-ec" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
avroOptionalGetters := true
(Compile / avroSource) := (Compile / sourceDirectory).value / "avro_source"
(Compile / avroGenerate / target) := (Compile / sourceManaged).value / "avro"
