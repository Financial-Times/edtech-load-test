package utils

import org.json4s.JsonAST.{JString, JField, JObject}
import org.json4s._
import org.json4s.native.JsonMethods._

object ConfigLoader {

  def uuidList(): List[String] = {
    val source = scala.io.Source.fromFile("uuid.json")
    val lines = try source.mkString finally source.close()
    val json = parse(lines)

    for {
      JObject(documents) <- json
      JField("key", JString(uuid))  <- documents
    } yield uuid
  }

}
