package com.github.sbt.avro.test.settings

import java.io.File
import java.util.Optional

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification
import com.github.sbt.avro.test.settings.{Avdl, Avpr, Avsc}

class SettingsSpec extends Specification {

  "stringField setting should be respected for *.avsc compilation" >> {
    classOf[Avsc].getDeclaredField("stringField").getType == classOf[String]
  }

  "stringField setting should be respected for *.avdl compilation" >> {
    classOf[Avdl].getDeclaredField("stringField").getType == classOf[String]
  }

  "stringField setting should be respected for *.avpr compilation" >> {
    classOf[Avpr].getDeclaredField("stringField").getType == classOf[String]
  }

  "fieldVisibility setting should be respected for *.avsc compilation" >> {
    !classOf[Avsc].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "fieldVisibility setting should be respected for *.avdl compilation" >> {
    !classOf[Avdl].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "fieldVisibility setting should be respected for *.avpr compilation" >> {
    !classOf[Avpr].getDeclaredField("stringField").isAnnotationPresent(classOf[Deprecated])
  }

  "avroOptionalGetters setting should be respected for *.avsc compilation" >> {
    classOf[Avsc].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }

  "avroOptionalGetters setting should be respected for *.avdl compilation" >> {
    classOf[Avdl].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }

  "avroOptionalGetters setting should be respected for *.avpr compilation" >> {
    classOf[Avpr].getDeclaredMethod("getStringField").getReturnType == classOf[Optional[String]]
  }
}
