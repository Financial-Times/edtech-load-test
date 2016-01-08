import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import utils.LoadTestDefaults._

class LanternAccessSimulation extends Simulation {

  val rampUp = Integer.getInteger("ramp-up-minutes", DefaultRampUpDurationInMinutes)
  val numReadUsers = Integer.getInteger("users", DefaultNumUsers)

  object Login

  val initialPage = http
    .baseURL("http://lantern.ft.com")

  val scn = scenario("LanternAccess")
    .exec(http("Request1")
    .get("/"))
    .pause(5)

  setUp(
    scn.inject(atOnceUsers(numReadUsers))
  ).protocols(initialPage)

}
