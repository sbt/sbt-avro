import com.spotify.avro.mojo.SchemaParserBuilder
import org.apache.avro.Schema
import sbt.ModuleID

class AnnotateWithArtifactSchemaParser(
  moduleID: ModuleID,
  types: java.util.Map[String, Schema]
) extends org.apache.avro.Schema.Parser {

  addTypes(types)

  override def parse(file: java.io.File): org.apache.avro.Schema = {
    val schema = super.parse(file)
    if (schema.getType == org.apache.avro.Schema.Type.RECORD) {
      schema.addProp("com.cavorite.sbt-avro.artifact", moduleID.toString())
    }
    schema
  }

}

object AnnotateWithArtifactSchemaParser {

  case class Builder(moduleID: ModuleID, types: java.util.Map[String, Schema])
      extends SchemaParserBuilder {

    override def build(): Schema.Parser =
      new AnnotateWithArtifactSchemaParser(moduleID, types)
  }

  def newBuilder(moduleID: ModuleID): AnnotateWithArtifactSchemaParser.Builder =
    new Builder(moduleID, java.util.Collections.emptyMap())

}
