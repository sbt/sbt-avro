ThisBuild / scalaVersion := "2.13.11"

lazy val basic: Project = project
  .in(file("."))
  .settings(
    libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value
  )

lazy val `avro-11`: Project = project
  .in(file(".avro_11"))
  .settings(
    avroVersion := "1.11.3",
    libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value,
    sourceDirectory := (basic / sourceDirectory).value
  )

lazy val `avro-10`: Project = project
  .in(file(".avro_10"))
  .settings(
    avroVersion := "1.10.2",
    libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value,
    sourceDirectory := (basic / sourceDirectory).value
  )

lazy val `avro-9`: Project = project
  .in(file(".avro_9"))
  .settings(
    avroVersion := "1.9.2",
    libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value,
    sourceDirectory := (basic / sourceDirectory).value
  )

lazy val `avro-8`: Project = project
  .in(file(".avro_8"))
  .settings(
    avroVersion := "1.8.2",
    libraryDependencies += "org.apache.avro" % "avro" % avroVersion.value,
    sourceDirectory := (basic / sourceDirectory).value
  )


