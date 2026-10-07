package com.github.sbt.avro

import sjsonnew.*
import sjsonnew.BasicJsonProtocol.*

private case class AvroCompilerSettings(
  classpath: Seq[sbt.File],
  compiler: String,
  version: String,
  stringType: String,
  fieldVisibility: String,
  enableDecimalLogicalType: Boolean,
  createSetters: Boolean,
  optionalGetters: Boolean,
  specificRecords: Seq[String]
)

private object AvroCompilerSettings {

  implicit val format: JsonFormat[AvroCompilerSettings] =
    caseClass(AvroCompilerSettings.apply _, AvroCompilerSettings.unapply _)(
      "classpath",
      "compiler",
      "version",
      "stringType",
      "fieldVisibility",
      "enableDecimalLogicalType",
      "createSetters",
      "optionalGetters",
      "specificRecords"
    )
}
