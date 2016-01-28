package utils

import org.json4s.JsonAST.{JString, JField, JObject}
import org.json4s._
import org.json4s.native.JsonMethods._

object ConfigLoader {

  val baseUrl = "http://lantern.ft.com"
  val wsBaseUrl = "wss://lantern.ft.com/socket.io"
  val perfTestID = "?PerfTestLantern"

  val rampUp = Integer.getInteger("ramp-up-seconds")
  val numUsers = Integer.getInteger("users")
  val sessionID : String = System.getenv("ET_SESSION_ID")
  val testDuration = System.getenv("ET_TEST_DURATION").toInt

  val homeUsers = math.ceil(numUsers/10).toInt
  val realtimeUsers = math.ceil(numUsers*0.6).toInt
  val historicalUsers = math.ceil(numUsers/5).toInt
  val topicsUsers = math.ceil(numUsers/20).toInt
  val sectionsUsers = math.ceil(numUsers/20).toInt


  def realtimeFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("realtimeUuid.json"))
  }

  def historicalFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("historicalUuid.json"))
  }

  def topicsFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("topicsUuid.json"))
  }

  def sectionsFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("sectionsUuid.json"))
  }

  private def generateUuidFeeder(uuidList:List[String]): Array[Map[String,String]] ={
    var array = Array[Map[String,String]]()

    uuidList.foreach{s =>
      array = array :+ Map("uuid" -> s)
    }

    return array
  }

  private def readUuidListFromFile(fileName:String): List[String] = {
    val source = scala.io.Source.fromFile(fileName)
    val lines = try source.mkString finally source.close()
    val json = parse(lines)

    for {
      JObject(documents) <- json
      JField("key", JString(uuid))  <- documents
    } yield uuid
  }

}
