package com.github.sbt.avro.test

object Main extends App {

  other.Other
  external.Avsc.newBuilder().setStringField("external").build()
  external.Avpr.newBuilder().setStringField("external").build()
  external.Avdl.newBuilder().setStringField("external").build()
  transitive.Avsc.newBuilder().setStringField("transitive").build()

  println("success")
}
