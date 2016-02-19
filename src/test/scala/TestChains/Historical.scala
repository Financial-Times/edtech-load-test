package testChains

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import utils.ConfigLoader

object Historical {

  val url = "/articles/${uuid}/global/FT" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url)

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Historical")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(css("#react-app"))
        .check(status.is(200)))
  }
}
