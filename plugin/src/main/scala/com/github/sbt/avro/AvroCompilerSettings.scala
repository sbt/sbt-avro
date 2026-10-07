package com.github.sbt.avro

import sjsonnew.*
import sjsonnew.BasicJsonProtocol.*
import java.net.URL

private case class AvroCompilerSettings(
  classpath: Seq[URL],
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
    BasicJsonProtocol.caseClass(
      AvroCompilerSettings.apply _,
      (s: AvroCompilerSettings) =>
        Some(
          (
            s.classpath,
            s.compiler,
            s.version,
            s.stringType,
            s.fieldVisibility,
            s.enableDecimalLogicalType,
            s.createSetters,
            s.optionalGetters,
            s.specificRecords
          )
        )
    )(
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
