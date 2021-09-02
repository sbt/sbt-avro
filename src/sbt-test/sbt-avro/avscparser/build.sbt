import java.util.Collections.{singletonMap, singletonList}
import org.apache.avro.Schema

name := "avscparser-test"
scalaVersion := "2.13.6"
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.10.2",
  "org.specs2" %% "specs2-core" % "4.12.8" % Test
)
avroSchemaParserBuilder := AnnotateWithArtifactSchemaParser
  .newBuilder(projectID.value)
  .copy(
    types = singletonMap("B", Schema.createEnum("B", null, "com.github.sbt.avro.test", singletonList("B1"))
  ))
