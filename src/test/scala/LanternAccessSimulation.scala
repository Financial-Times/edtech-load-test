import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import utils.{RandomGenerator, ConfigLoader}
import utils.LoadTestDefaults._

class LanternAccessSimulation extends Simulation {

  val baseUrl = "http://lantern.ft.com"

  val rampUp = Integer.getInteger("ramp-up-seconds", DefaultRampUpDurationInSeconds)
  val numUsers = Integer.getInteger("users", DefaultNumUsers)
  val sessionID : String = System.getenv("ET_SESSION_ID")
  val initialPage = http
    .baseURL(baseUrl)
    .wsBaseURL("wss://lantern.ft.com/socket.io")

  val perfTestID = "?PerfTestLantern"
  val feeder = genArray().circular

  def genericTest(testName: String, testUrl: String): ChainBuilder = {
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http(testName)
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

  def realtimeTest(testName: String): ChainBuilder = {
    val testUrl = "/${uuid}" + perfTestID
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http(testName)
        .get(testUrl)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))
      .exec(http("HTTP: getSID")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4))
        .check(regex("\"sid\":\"(.*)\",\".*").saveAs("sid"))
        .check(bodyString.find.saveAs("bodyString"))
        .check(status.is(200)))
      .exec(http("HTTP: Confirm SID")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .check(status.is(200)))
      .exec(http("HTTP: Subscribe to Article")
        .post("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .body(StringBody("63:42[\"subscribeToArticle\",\"${uuid}\"]"))
        .check(status.is(200)))
      .exec(http("HTTP: Confirm Subscription")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .check(status.is(200)))

      .exec(ws("Web Socket: Connect").open("/?EIO=3&transport=websocket&sid=${sid}"))
      .exec(ws("Web Socket: Send 2probe").sendText("2probe").check(wsAwait.within(3).until(1).regex("3probe")))
      .exec(ws("Web Socket: Send 5").sendText("5"))
      .repeat(2){
        exec(repeat(5) {
          exec(ws("Web Socket: 42 Response").check(wsAwait.within(20).until(1).regex("(42.*)")))
        })
          .exec(ws("Web Socket: Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      }
      .exec(ws("Web Socket: Close").close)

    return test
  }

  object Home {
    val homeGet = "/" + perfTestID
    val getPage = genericTest("Home",homeGet)
  }

  object Historical {
    val historicalGet = "/articles/9b66e747-6da4-3d0f-a189-c38d2997df10/global/FT" + perfTestID
    val getPage = genericTest("Page: Historical",historicalGet)
  }

  object Realtime {
    val getPage = realtimeTest("Page: Realtime")
  }

  object Sections {
    val sectionsGet = "/sections/Companies" + perfTestID
    val getPage = genericTest("Page: Sections",sectionsGet)
  }

  object Topics{
    val topicsGet = "/topics/Driverless%20Cars" + perfTestID
    val getPage = genericTest("Page: Topics",topicsGet)
  }

  val scnLantern = scenario("Lantern")
    .feed(feeder)
    .roundRobinSwitch(
      Realtime.getPage,
      Realtime.getPage,
      Realtime.getPage,
      Realtime.getPage,
      Realtime.getPage,
      Realtime.getPage,
      Historical.getPage,
      Historical.getPage,
      Home.getPage,
      roundRobinSwitch(
        Topics.getPage,
        Sections.getPage
      )
    )

  setUp(
    scnLantern.inject(rampUsers(numUsers) over (rampUp seconds))
  ).protocols(initialPage)



}
