name := "settings-test"
scalaVersion := "2.13.11"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % avroCompilerVersion,
  "org.specs2" %% "specs2-core" % "4.20.0" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
avroOptionalGetters := true
(Compile / avroSource) := (Compile / sourceDirectory).value / "avro_source"
(Compile / avroGenerate / target) := (Compile / sourceManaged).value
