# Editorial Technology Load Tests

## Why?
We need load tests for our products and services. This aims to provide.

## Pre-requisites
Git, Java, Maven. Everything else should download the first time you run the test, thanks to Maven.

## How?
- Login to http://lantern.ft.com/ get your session ID from cookies
- Place session ID into `src/test/scala/utils/ArticleValues.scala`
- Modify `RunLanternAccessSimulation.sh` if you need to change how many users login, ramp-up time, etc 
- Run `sh RunLanternAccessSimulation.sh`

## Todo
- Set up ramp-up time functionality
- Add in search, historical, real-time, and section views
- Celebrate