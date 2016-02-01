package testChains

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import utils.ConfigLoader

object Sections {

  val url = "/sections/UK" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url)

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Sections")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))
  }
}
