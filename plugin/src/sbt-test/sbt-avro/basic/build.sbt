ThisBuild / scalaVersion := "2.13.11"

lazy val basic = crossProject(
    Avro("1.12.0"),
    Avro("1.11.3"),
    Avro("1.10.0"),
    Avro("1.9.2"),
    Avro("1.8.2"),
  )
  .crossType(CrossType.Pure)
  .in(file("."))
  .avroSettings("1.9.2")(
      libraryDependencies += "joda-time" % "joda-time" % "2.12.7"
  )
  .avroSettings("1.8.2")(
    libraryDependencies += "joda-time" % "joda-time" % "2.12.7"
  )

