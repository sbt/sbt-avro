package com.github.sbt.avro

import com.github.sbt.avro.mojo.SchemaParserBuilder
import org.apache.avro.{NameValidator, Schema}

import scala.collection.JavaConverters._

case class NameValidatorSchemaParserBuilder(
  types: Iterable[Schema] = Iterable.empty,
  validation: NameValidator = NameValidator.UTF_VALIDATOR,
  validateDefaults: Boolean = true
) extends SchemaParserBuilder {

  override def build(): Schema.Parser = {
    val parser = new Schema.Parser(validation)
    parser.addTypes(types.asJava)
    parser.setValidateDefaults(validateDefaults)
  }
}
