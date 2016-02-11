package testChains

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.StringBody
import utils.ConfigLoader
import utils.RandomGenerator

object Realtime {

  val url = "/realtime/articles/${uuid}" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url)

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Realtime")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(status.is(200)))
      .exec(http("HTTP: Get Timespan")
        .get("https://lantern.ft.com/api/v0/realtime/articles/${uuid}?timespan=1h")
        .check(status.is(200)))
      .exec(http("HTTP: Get SID")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4))
        .check(regex("\"sid\":\"(.*)\",\".*").saveAs("sid"))
        .check(status.is(200)))
      .exec(http("HTTP: Confirm SID")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .check(status.is(200)))
      .exec(http("HTTP: POST Subscribe to article")
        .post("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .body(StringBody("88:42[\"subscribeToArticle\",{\"uuid\":\"${uuid}\",\"timespan\":\"1h\"}]"))
        .check(status.is(200)))
      .exec(http("HTTP: Confirm subscription")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA2" + RandomGenerator.string(4) + "&sid=${sid}")
        .check(status.is(200)))

      .exec(ws("Web Socket: Connect").open("/?EIO=3&transport=websocket&sid=${sid}"))
      .exec(ws("Web Socket: Send 2probe").sendText("2probe").check(wsAwait.within(3).until(1).regex("3probe")))
      .exec(ws("Web Socket: Send 5").sendText("5"))

      .forever() {
        exec(repeat(5) {
          exec(ws("Web Socket: 42 Response").check(wsAwait.within(30).until(1).regex("(42.*)")))
        })
          .exec(ws("Web Socket: Send 2, Receive 3").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
      }
  }
}
