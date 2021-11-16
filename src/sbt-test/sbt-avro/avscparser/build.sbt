import java.util.Collections.{singletonMap, singletonList}
import org.apache.avro.Schema

name := "avscparser-test"
scalaVersion := "2.13.6"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.11.0",
  "org.specs2" %% "specs2-core" % "4.13.0" % Test
)
avroSchemaParserBuilder := AnnotateWithArtifactSchemaParser
  .newBuilder(projectID.value)
  .copy(
    types = singletonMap("B", Schema.createEnum("B", null, "com.github.sbt.avro.test", singletonList("B1"))
  ))
