package sbtavro

import java.io.File

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData.StringType
import org.specs2.mutable.Specification

import com.cavorite.test.avscparser.A

class AvscParserSpec extends Specification {

  "A should have artifact property" >> {
    println(A.getClassSchema().getProp("com.cavorite.sbt-avro.artifact"))
    A.getClassSchema().getProp("com.cavorite.sbt-avro.artifact") == "avscparser-test:avscparser-test:0.1.0-SNAPSHOT"
  }

}
