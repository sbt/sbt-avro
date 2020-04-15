name := "settings-test"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro-compiler" % "1.9.2",
  "org.specs2" %% "specs2-core" % "4.9.3" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
