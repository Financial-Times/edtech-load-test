package testChains

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.StringBody
import utils.{ConfigLoader, RandomGenerator}

object Realtime48hr {

  val url = "/realtime/articles/${uuid}/48h" + ConfigLoader.perfTestID
  val urlConcat = ConfigLoader.baseUrl.concat(url)

  def runner(): ChainBuilder = {
    exec(addCookie(Cookie("connect.sid", ConfigLoader.sessionID)))
      .exec(http("Page: Realtime (48hr)")
        .get(url)
        .check(currentLocation.is(urlConcat))
        .check(css("#react-app"))
        .check(status.is(200)))
      .exec(http("HTTP: Get Timespan (48hr)")
        .get("https://lantern.ft.com/api/v0/realtime/articles/${uuid}?timespan=48h")
        .check(status.is(200)))
      .exec(http("HTTP: Get SID (48hr)")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA1" + RandomGenerator.string(4))
        .check(regex("\"sid\":\"(.*)\",\".*").saveAs("sid"))
        .check(status.is(200)))
      .exec(http("HTTP: Confirm SID (48hr)")
        .get("/socket.io/?EIO=3&transport=polling&t=" + "LA1" + RandomGenerator.string(4) + "&sid=${sid}")
        .check(status.is(200)))
        .exec(http("HTTP: POST Subscribe to article (48hr)")
          .post("/socket.io/?EIO=3&transport=polling&t=" + "LA1" + RandomGenerator.string(4) + "&sid=${sid}")
          .body(StringBody("89:42[\"subscribeToArticle\",{\"uuid\":\"${uuid}\",\"timespan\":\"48h\"}]"))
          .check(status.is(200)))
        .exec(http("HTTP: Confirm subscription (48hr)")
          .get("/socket.io/?EIO=3&transport=polling&t=" + "LA1" + RandomGenerator.string(4) + "&sid=${sid}")
          .check(status.is(200)))

        .exec(ws("Web Socket: Connect (48hr)").open("/?EIO=3&transport=websocket&sid=${sid}"))
        .exec(ws("Web Socket: Send 2probe (48hr)").sendText("2probe").check(wsAwait.within(3).until(1).regex("3probe")))
        .exec(ws("Web Socket: Send 5 (48hr)").sendText("5"))

        .forever() {
          exec(ws("Web Socket: Send 2, Receive 3 (48hr)").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
            .exec(pause(20))
            .exec(ws("Web Socket: Send 2, Receive 3 (48hr)").sendText("2").check(wsAwait.within(3).until(1).regex("3")))
            .exec(ws("Web Socket: 42 Response (48hr)").check(wsAwait.within(120).until(1).regex("(42.*)")))
        }
  }
}
