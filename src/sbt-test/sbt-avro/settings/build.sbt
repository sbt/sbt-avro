name := "settings-test"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.6.4" % "test"
)

(stringType in avroConfig) := "String"
(fieldVisibility in avroConfig) := "public"
