import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import utils.ArticleValues
import utils.LoadTestDefaults._

class LanternAccessSimulation extends Simulation {

  val rampUp = Integer.getInteger("ramp-up-minutes", DefaultRampUpDurationInMinutes)
  val numUsers = Integer.getInteger("users", DefaultNumUsers)
  val sessionID : String = ArticleValues.SessionID

  object Login

  val initialPage = http
    .baseURL("http://lantern.ft.com")

  val scn = scenario("LanternAccess")
    .exec(addCookie(Cookie("connect.sid",sessionID)))
    .exec(http("Request1")
    .get("/realtime/articles/704162f8-b5f1-11e5-b147-e5e5bba42e51"))
    .pause(5)

  setUp(

    scn.inject(
      rampUsers(numUsers) over (rampUp minutes))
  ).protocols(initialPage)

}
