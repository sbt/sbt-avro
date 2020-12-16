import java.util.Collections.{singletonMap, singletonList}
import org.apache.avro.Schema

name := "avscparser-test"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.10.1",
  "org.specs2" %% "specs2-core" % "4.9.4" % Test
)

avroSchemaParserBuilder := AnnotateWithArtifactSchemaParser
  .newBuilder(projectID.value)
  .copy(types = singletonMap(
    "B", Schema.createEnum("B", null, "com.cavorite.test.avscparser", singletonList("B1"))
  ))
