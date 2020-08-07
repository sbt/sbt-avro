package sbtavro

import com.spotify.avro.mojo.SchemaParserBuilder
import org.apache.avro.Schema
import scala.collection.JavaConverters._

case class DefaultSchemaParserBuilder(types: Iterable[Schema],
                                      validate: Boolean,
                                      validateDefaults: Boolean)
    extends SchemaParserBuilder {

  override def build(): Schema.Parser = {
    val parser = new Schema.Parser
    parser.addTypes(types.map(el => el.getFullName() -> el).toMap.asJava)
    parser.setValidate(validate)
    parser.setValidateDefaults(validateDefaults)
    parser
  }
}

object DefaultSchemaParserBuilder {
  def default(): DefaultSchemaParserBuilder = {
    template(new Schema.Parser())
  }

  def template(template: Schema.Parser): DefaultSchemaParserBuilder = {
    DefaultSchemaParserBuilder(
      template.getTypes.values().asScala,
      template.getValidate,
      template.getValidateDefaults
    )
  }
}
