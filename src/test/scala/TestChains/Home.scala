package testChains

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import utils.ConfigLoader

object Home {

  val url = "/" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url)

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Home")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))
  }
}
