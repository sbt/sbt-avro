package com.github.sbt.avro.test.settings

import java.io.File
import java.nio.ByteBuffer
import java.util.Optional

import org.apache.avro.specific.TestRecordWithLogicalTypes
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification
import com.github.sbt.avro.test.settings.{Avdl, Avpr, Avsc}

class SettingsSpec extends Specification {

  // avroStringType
  "avroStringType setting should be respected for *.avsc compilation" >> {
    classOf[Avsc].getDeclaredField("stringField").getType == classOf[String]
  }

  "avroStringType setting should be respected for *.avdl compilation" >> {
    classOf[Avdl].getDeclaredField("stringField").getType == classOf[String]
  }

  "avroStringType setting should be respected for *.avpr compilation" >> {
    classOf[Avpr].getDeclaredField("stringField").getType == classOf[String]
  }

  "stringField setting should be respected for recompiled record" >> {
    classOf[TestRecordWithLogicalTypes].getDeclaredField("s").getType == classOf[String]
  }

  // avroFieldVisibility
  "avroFieldVisibility setting should be respected for *.avsc compilation" >> {
    !classOf[Avsc].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "avroFieldVisibility setting should be respected for *.avdl compilation" >> {
    !classOf[Avdl].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "avroFieldVisibility setting should be respected for *.avpr compilation" >> {
    !classOf[Avpr].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "avroFieldVisibility setting should be respected for recompiled record" >> {
    !classOf[TestRecordWithLogicalTypes].getDeclaredField("s").isAnnotationPresent(classOf[Deprecated])
  }

  // avroOptionalGetters
  "avroOptionalGetters setting should be respected for *.avsc compilation" >> {
    classOf[Avsc].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }

  "avroOptionalGetters setting should be respected for *.avdl compilation" >> {
    classOf[Avdl].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }

  "avroOptionalGetters setting should be respected for *.avpr compilation" >> {
    classOf[Avpr].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }

  "avroOptionalGetters setting should be respected for recompiled record" >> {
    classOf[TestRecordWithLogicalTypes].getDeclaredMethod("getS").getReturnType == classOf[Optional[String]]
  }

  // avroEnableDecimalLogicalType
  "avroEnableDecimalLogicalType setting should be respected for recompiled record" >> {
    classOf[TestRecordWithLogicalTypes].getDeclaredField("bd").getType == classOf[ByteBuffer]
  }
}
