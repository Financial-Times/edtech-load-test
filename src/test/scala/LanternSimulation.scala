import io.gatling.core.scenario.Simulation

import testChains._
import utils.ConfigLoader._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LanternSimulation extends Simulation {

  val scnHistorical = scenario("Historical View")
    .forever() {
      feed(historicalFeeder.circular)
        .exec(Historical.runner())
        .pause(5, 10)
    }

  val scnHome = scenario("Home View")
    .forever() {
      exec(Home.runner())
        .pause(5, 10)
    }

  val scnRealtime = scenario("Realtime View")
    .feed(realtimeFeeder.circular)
    .exec(Realtime.runner())

  val scnSections = scenario("Sections View")
    .forever() {
      feed(sectionsFeeder.circular)
      exec(Sections.runner())
        .pause(5, 10)
    }

  val scnTopics = scenario("Topics View")
    .forever() {
      feed(topicsFeeder.circular)
      exec(Topics.runner())
        .pause(5, 10)
    }

  val initialPage = http
    .baseURL(baseUrl)
    .wsBaseURL(wsBaseUrl)

  setUp(
    scnHome.inject(rampUsers(homeUsers) over (rampUp seconds)),
    scnHistorical.inject(rampUsers(historicalUsers) over (rampUp seconds)),
    scnRealtime.inject(rampUsers(realtimeUsers) over (rampUp seconds)),
    scnSections.inject(rampUsers(sectionsUsers) over (rampUp seconds)),
    scnTopics.inject(rampUsers(topicsUsers) over (rampUp seconds))
  ).protocols(initialPage).maxDuration(testDuration seconds)

}
