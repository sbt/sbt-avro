name := "settings-test"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "4.9.3" % Test
)

avroStringType := "String"
avroFieldVisibility := "public"
