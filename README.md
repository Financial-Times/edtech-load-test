# Editorial Technology Load Tests

![image](http://gatling.io/images/gatling-logo.png)

## Why?
We need load tests for our products and services. This aims to provide.

## Pre-Requisites
Git, Java, Maven. Everything else should download the first time you run the test, thanks to Maven.

## How?
- Login to http://lantern.ft.com/ manually to get your session ID from cookies
- Create an `.env` file in the project's home directory
- Populate the `.env` file (see below)
- Run `sh RunLanternAccessSimulation.sh`
- Reports can be found in `target/gatling/results`

### Env File Population
- `ET_HOME_CHANNEL` - the Slack channel to alert. This should not change often
- `ET_RAMP_UP_SECONDS` - amount of time users will take to login. Input into Jenkins build without `.env`
- `ET_TEST_DURATION` - time for the tests to run, in seconds. Input into Jenkins build without `.env` 
- `ET_SESSION_ID` - login to http://lantern.ft.com/ get your session ID from cookies. Input into Jenkins build without `.env`
- `ET_HOME_USERS` - number of users to access home pages. Input into Jenkins build without `.env`
- `ET_HISTORICAL_USERS` - number of users to access historical pages. Input into Jenkins build without `.env`
- `ET_PICKOFTHEDAY_USERS` - number of users to access the pick of the day page. Input into Jenkins build without `.env` 
- `ET_REALTIME_USERS` - number of users to access realtime pages. Input into Jenkins build without `.env`
- `ET_SECTIONS_USERS` - number of users to access sections pages. Input into Jenkins build without `.env`
- `ET_TOPICS_USERS` - number of users to access topics pages. Input into Jenkins build without `.env`

## Obsolete File Population
- `ET_LANTERN_USERS` - number of users to connect to Lantern. Input into Jenkins build without `.env` 

## Todo
- Jenkins: if build fail, run Slack message `build failed!`
- Celebrate

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
- Put `*Uuid.json` into their own folder for neatness
- New http websocket subscription
- User and ramp up time option in Jenkins
- Now using sections and topics generated from uuid
- Put number of users for each type into `.env`
- New 48 hour view for realtime
- Pick of the Day page
- Combined 1h and 48h realtime views

## Notes
- A group of `*Uuid.json` files should generate themselves due to a jenkins shell script. If not, you need to create `historicalUuid.json`, `realtimeUuid.json`, `sectionsUuid.json`, and `topicsUuid.json`
