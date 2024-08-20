package com.github.sbt.avro

import com.github.sbt.avro.mojo.SchemaParserBuilder
import org.apache.avro.Schema

import scala.annotation.nowarn
import scala.collection.JavaConverters.*

// used until avro 1.11
// for avro 2.12+ use NameValidatorSchemaParserBuilder
case class LegacySchemaParserBuilder(
  types: Iterable[Schema] = LegacySchemaParserBuilder.DefaultTypes,
  validate: Boolean = LegacySchemaParserBuilder.DefaultValidate,
  validateDefaults: Boolean = LegacySchemaParserBuilder.DefaultValidateDefaults)
extends SchemaParserBuilder {

  override def build(): Schema.Parser = {
    val parser = new Schema.Parser
    // addTypes(Map<String, Schema> types) is the only API available in 1.8
    parser.addTypes(types.map(el => el.getFullName -> el).toMap.asJava): @nowarn
    LegacySchemaParserBuilder.setValidate(parser)(validate)
    parser.setValidateDefaults(validateDefaults)
    parser
  }
}

object LegacySchemaParserBuilder {
  // validate hase been removed in 1.12 in favor of a NameValidator
  private def setValidate(parser: Schema.Parser)(validate: Boolean): Schema.Parser =
    classOf[Schema.Parser]
      .getMethod("setValidate", classOf[Boolean])
      .invoke(parser, validate: java.lang.Boolean)
      .asInstanceOf[Schema.Parser]

  private def getValidate(parser: Schema.Parser): Boolean =
      classOf[Schema.Parser]
        .getMethod("getValidate")
        .invoke(parser)
        .asInstanceOf[Boolean]

  private val defaultParser = new Schema.Parser

  private val DefaultTypes: Iterable[Schema] = defaultParser.getTypes.values().asScala
  private val DefaultValidate: Boolean = getValidate(defaultParser)
  private val DefaultValidateDefaults: Boolean = defaultParser.getValidateDefaults
}
