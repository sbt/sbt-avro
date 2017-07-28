package sbtavro

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

import com.cavorite.test.settings.{Avsc, Avdl, Avpr}

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

}
