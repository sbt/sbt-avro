package com.github.sbt.avro.test

object Main extends App {

  // Compiled Avro classes should be on classpath
  external.Avsc.newBuilder().setStringField("external").build()
  external.Avpr.newBuilder().setStringField("external").build()
  external.Avdl.newBuilder().setStringField("external").build()
  transitive.Avsc.newBuilder().setStringField("transitive").build()

  println("success")
}