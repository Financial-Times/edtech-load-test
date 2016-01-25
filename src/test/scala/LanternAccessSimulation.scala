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
  val initialPage = http.baseURL(baseUrl)

  val feeder = genArray().circular

  def genericTest(testName: String, testUrl: String): ChainBuilder = {
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http(testName)
        .get(testUrl)
        .check(currentLocation.is(urlConcat)))

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
    val testUrl = "/realtime/articles/${uuid}?PerfTest"
    val urlConcat = baseUrl.concat(testUrl)

    val test = exec(addCookie(Cookie("connect.sid",sessionID)))
      .exec(http("Realtime")
        .get(testUrl)
        .check(currentLocation.is(urlConcat)))

    return test
  }

  object Home {
    val homeGet = "/" + "?PerfTest"
    val getPage = genericTest("Home",homeGet)
  }

  object Historical {
    val historicalGet = "/articles/9b66e747-6da4-3d0f-a189-c38d2997df10/global/FT" + "?PerfTest"
    val getPage = genericTest("Historical",historicalGet)
  }

  object Realtime {
    val getPage = realtimeTest("Realtime")
  }

  object Sections {
    val sectionsGet = "/sections/Companies" + "?PerfTest"
    val getPage = genericTest("Sections",sectionsGet)
  }

  object Topics{
    val topicsGet = "/topics/Driverless%20Cars" + "?PerfTest"
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
