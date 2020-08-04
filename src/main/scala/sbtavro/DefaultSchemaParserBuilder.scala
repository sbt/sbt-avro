package sbtavro

import java.util

import com.spotify.avro.mojo.SchemaParserBuilder
import org.apache.avro.Schema

case class DefaultSchemaParserBuilder(types: util.Map[String, Schema],
                                      validate: Boolean,
                                      validateDefaults: Boolean)
    extends SchemaParserBuilder {

  override def build(): Schema.Parser = {
    val parser = new Schema.Parser
    parser.addTypes(types)
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
      template.getTypes,
      template.getValidate,
      template.getValidateDefaults
    )
  }
}
