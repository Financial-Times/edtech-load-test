package testChains

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import utils.ConfigLoader

object Sections {

  val url = "/sections/${urlPage}/168" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url.replaceAll(" ","%20"))

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Sections")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(css("#react-app"))
        .check(status.is(200)))
  }
}
