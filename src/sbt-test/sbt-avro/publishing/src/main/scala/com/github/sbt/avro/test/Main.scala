import com.github.sbt.avro.test.external
import com.github.sbt.avro.test.transitive


object Main extends App {

  external.Avsc.newBuilder().setStringField("external").build()
  transitive.Avsc.newBuilder().setStringField("transitive").build()

  println("success")
}