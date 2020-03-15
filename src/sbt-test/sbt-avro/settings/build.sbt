name := "settings-test"
scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.10.0" % "test"
)

avroStringType := "String"
avroFieldVisibility := "public"
