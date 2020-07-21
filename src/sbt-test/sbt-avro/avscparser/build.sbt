name := "avscparser-test"

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.10.0",
  "org.specs2" %% "specs2-core" % "4.9.4" % Test
)

(Compile / avroSchemaParser) := {
  class AnnotateWithArtifactSchemaParser(moduleID: ModuleID)
      extends org.apache.avro.Schema.Parser {
    override def parse(file: java.io.File): org.apache.avro.Schema = {
      val schema = super.parse(file)
      if (schema.getType == org.apache.avro.Schema.Type.RECORD) {
        schema.addProp("com.cavorite.sbt-avro.artifact", moduleID.toString())
      }
      schema
    }
  }

  () => {
    val parser = new AnnotateWithArtifactSchemaParser(projectID.value)
    val b = org.apache.avro.Schema.createEnum(
        "B", null, "com.cavorite.test.avscparser", java.util.Collections.singletonList("B1"))
    parser.addTypes(java.util.Collections.singletonMap("B", b))
    parser
  }
}
