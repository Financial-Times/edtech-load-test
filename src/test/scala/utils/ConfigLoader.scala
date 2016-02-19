package utils

import org.json4s.JsonAST.{JString, JField, JObject}
import org.json4s._
import org.json4s.native.JsonMethods._

object ConfigLoader {

  val baseUrl = "http://lantern.ft.com"
  val wsBaseUrl = "wss://lantern.ft.com/socket.io"
  val perfTestID = "?PerfTestLantern"

//  val numUsers = System.getenv("ET_LANTERN_USERS").toInt
  val rampUp = System.getenv("ET_RAMP_UP_SECONDS").toInt
  val testDuration = System.getenv("ET_TEST_DURATION").toInt
  val sessionID : String = System.getenv("ET_SESSION_ID")

  val homeUsers = System.getenv("ET_HOME_USERS").toInt
  val historicalUsers = System.getenv("ET_HISTORICAL_USERS").toInt
  val pickOfTheDayUsers = System.getenv("ET_PICKOFTHEDAY_USERS").toInt
  val realtimeUsers = System.getenv("ET_REALTIME_USERS").toInt
  val realtime48Users = System.getenv("ET_REALTIME48_USERS").toInt
  val sectionsUsers = System.getenv("ET_SECTIONS_USERS").toInt
  val topicsUsers = System.getenv("ET_TOPICS_USERS").toInt


  def historicalFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("./src/test/resources/uuid/historicalUuid.json"))
  }

  def realtimeFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("./src/test/resources/uuid/realtimeUuid.json"))
  }

  def sectionsFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("./src/test/resources/uuid/sectionsUuid.json"))
  }

  def topicsFeeder() : Array[Map[String,String]] = {
    generateUuidFeeder(readUuidListFromFile("./src/test/resources/uuid/topicsUuid.json"))
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
