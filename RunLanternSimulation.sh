#!/bin/bash

export $(cat .env)

SLEEP_TIME=10
TOTAL_USERS=$(($ET_HOME_USERS + $ET_HISTORICAL_USERS + $ET_REALTIME_USERS + $ET_REALTIME48_USERS + $ET_SECTIONS_USERS + $ET_TOPICS_USERS))
TEXT="In $SLEEP_TIME seconds: Running $TOTAL_USERS users through Lantern over $ET_RAMP_UP_SECONDS secs. Expect a report in approximately $ET_TEST_DURATION seconds."
USERNAME="gatling-bot"
ICON_EMOJI=":gatling:"

SLACK_CHANNEL_URL="https://hooks.slack.com$ET_HOME_CHANNEL"

sleep 2

curl -X POST -H "Content-Type:application/json" \
    --data '{ "text":"'"$TEXT"'", "username":"'"$USERNAME"'", "icon_emoji":"'"$ICON_EMOJI"'" }' $SLACK_CHANNEL_URL

sleep $SLEEP_TIME
mvn gatling:execute -Dgatling.simulationClass=LanternSimulation
