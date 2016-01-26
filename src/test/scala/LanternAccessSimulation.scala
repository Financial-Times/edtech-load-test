import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import utils.ConfigLoader
import utils.LoadTestDefaults._

class LanternAccessSimulation extends Simulation {

  val baseUrl = "http://lantern.ft.com"

  val rampUp = Integer.getInteger("ramp-up-seconds", DefaultRampUpDurationInSeconds)
  val numUsers = Integer.getInteger("users", DefaultNumUsers)
  val sessionID : String = System.getenv("ET_SESSION_ID")
  val initialPage = http
    .baseURL(baseUrl)
    .wsBaseURL("wss://lantern.ft.com/socket.io")
  //    .wsBaseURL("ws://echo.websocket.org")

  val perfTestID = "?PerfTestLantern"

  val feeder = genArray().circular

  def genericTest(testName: String, testUrl: String): ChainBuilder = {
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http(testName + " Page")
        .get(testUrl)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))

    return test
  }

  def genArray(): Array[Map[String,String]] ={
    var array = Array[Map[String,String]]()

    ConfigLoader.uuidList().foreach{s =>
      array = array :+ Map("uuid" -> s)
    }

    return array
  }

  def realtimeTest(testUuid: String): ChainBuilder = {
    val testUrl = "/${uuid}" + perfTestID
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http("Realtime Page")
        .get(testUrl)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))
      .exec(http("getSID")
        .get("/socket.io/?EIO=3&transport=polling&t=L9zbYPl")
        .check(regex("\"sid\":\"(.*)\",\".*").saveAs("sid"))
        .check(bodyString.find.saveAs("bodyString"))
        .check(status.is(200)))
      .exec(http("Get?")
        .get("/socket.io/?EIO=3&transport=polling&t=L9zbYPm&sid=${sid}")
        .check(status.is(200)))
      .exec(http("Subscribe to Article")
        .post("/socket.io/?EIO=3&transport=polling&t=L9zbYPn&sid=${sid}")
        .body(StringBody("63:42[\"subscribeToArticle\",\"${uuid}\"]"))
        .check(status.is(200)))
      .exec(http("Get?")
        .get("/socket.io/?EIO=3&transport=polling&t=L9zbYPm&sid=${sid}")
        .check(status.is(200)))

      .exec(ws("Connect WS").open("/?EIO=3&transport=websocket&sid=${sid}"))
      .exec(ws("Send Probe").sendText("2probe").check(wsAwait.within(3).until(1).regex("3probe")))
      .exec(ws("Confirm Probe").sendText("5"))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("42 Responses").check(wsAwait.within(30).until(5).regex("(42.*)")))
      .exec(ws("Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      .exec(ws("Close WS").close)

    return test
  }

  object Home {
    val homeGet = "/" + perfTestID
    val getPage = genericTest("Home",homeGet)
  }

  object Historical {
    val historicalGet = "/articles/9b66e747-6da4-3d0f-a189-c38d2997df10/global/FT" + perfTestID
    val getPage = genericTest("Historical",historicalGet)
  }

  object Realtime {
    val getPage = realtimeTest("Realtime")
  }

  object Sections {
    val sectionsGet = "/sections/Companies" + perfTestID
    val getPage = genericTest("Sections",sectionsGet)
  }

  object Topics{
    val topicsGet = "/topics/Driverless%20Cars" + perfTestID
    val getPage = genericTest("Topics",topicsGet)
  }

  val scnLantern = scenario("Lantern")
    .feed(feeder)
    .roundRobinSwitch(
      Realtime.getPage,
      Historical.getPage,
      Realtime.getPage,
      Historical.getPage,
      Realtime.getPage,
      Home.getPage,
      Realtime.getPage,
      roundRobinSwitch(
        Topics.getPage,
        Sections.getPage
      ),
      Realtime.getPage,
      Realtime.getPage
    )

  setUp(
    scnLantern.inject(rampUsers(numUsers) over (rampUp seconds))
  ).protocols(initialPage)



}
