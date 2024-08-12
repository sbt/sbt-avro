package com.github.sbt.avro

import com.github.sbt.avro.mojo.SchemaParserBuilder
import org.apache.avro.Schema

object DefaultSchemaParserBuilder {

  def default(): SchemaParserBuilder = {
    val Array(1, minor, _) = classOf[Schema].getPackage.getImplementationVersion.split("\\.").take(3).map(_.toInt)
    if (minor >= 12) {
      NameValidatorSchemaParserBuilder()
    } else {
      LegacySchemaParserBuilder()
    }
  }
}
