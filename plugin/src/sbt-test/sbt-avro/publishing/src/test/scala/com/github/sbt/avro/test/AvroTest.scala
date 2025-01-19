package com.github.sbt.avro.test

import com.github.sbt.avro.test.transitive.Test

object AvroTest extends App {

  Test.newBuilder().setStringField("external").build()

  println("success")
}
