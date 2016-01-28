# Editorial Technology Load Tests

![image](http://gatling.io/images/gatling-logo.png)

## Why?
We need load tests for our products and services. This aims to provide.

## Pre-Requisites
Git, Java, Maven. Everything else should download the first time you run the test, thanks to Maven.

## How?
- Create an `.env` file in the project's home directory
- Populate the `.env` file (see below)
- Login to http://lantern.ft.com/ get your session ID from cookies
- Place session ID into `src/test/scala/utils/ArticleValues.scala`
- Modify `RunLanternAccessSimulation.sh` if you need to change how many users login, ramp-up time, etc 
- Run `sh RunLanternAccessSimulation.sh`
- Reports can be found in `target/gatling/results`

### Env File Population
- `ET_HOME_CHANNEL` - the Slack channel to alert. This should not change often
- `ET_TEST_DURATION` - time for the tests to run, in seconds. Usually input into the Jenkins build
- `ET_SESSION_ID` - login to http://lantern.ft.com/ get your session ID from cookies. This is created currently by Jenkins, and does not need to be populated

## Todo
- Celebrate
- Figure out why all users have to be in multiples of 20
- Put *Uuid.json into their own folder for neatness
- Jenkins: if build fail, run Slack message `build failed!`

## Done
- Ramp-up time functionality
- Send Slack notification on lantern assault
- Change minutes to seconds fro ramp up
- Hear response from Gatling: what webpage am I on?
- Add in search, historical, real-time, and section views
- Create .env file for dealing with variables
- Randomise pause times
- Jenkins setup (awscli)
- Remove `ET_SESSION_ID` from .env file and readme : replace with manual parameter in Jenkins
- Add reaction to slack post
- Automated pull-top-article for realtime
- Random article find (create list)
- perfTestID
- Web socket for realtime articles
- Turn web socket request into random string to avoid status 1006
- Config and logback to fix Websocket 1006
- Forever loops on everything
- Test duration input
- Generate Historical uuids randomly
- Complete refactor of code!

## Notes
- A bunch of `uuid.json` should generate itself due to a jenkins shell script. If not, you need to create `historicalUuid.json` and `realtimeUuid.json`
