package com.github.sbt.avro.test

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

import com.github.sbt.avro.test.A

class AvscParserSpec extends Specification {

  "A should have artifact property" >> {
    A.getClassSchema().getProp("com.github.sbt.sbt-avro.artifact") == "avscparser-test:avscparser-test:0.1.0-SNAPSHOT"
  }

}
