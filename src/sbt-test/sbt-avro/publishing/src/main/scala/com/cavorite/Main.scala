import com.cavorite.external
import com.cavorite.transitive


object Main extends App {

  external.Avsc.newBuilder().setStringField("external").build()
  transitive.Avsc.newBuilder().setStringField("transitive").build()

  println("success")
}