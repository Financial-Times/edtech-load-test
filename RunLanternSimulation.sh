#!/bin/bash

export $(cat .env)

SLEEP_TIME=10
ET_TOTAL_USERS=$(($ET_HOME_USERS + $ET_HISTORICAL_USERS + $ET_PICKOFTHEDAY_USERS + $ET_REALTIME_USERS + $ET_SECTIONS_USERS + $ET_TOPICS_USERS))
ET_TOTAL_TIME=$(($ET_RAMP_UP_SECONDS + $ET_TEST_DURATION))
TEXT="In $SLEEP_TIME seconds: Running $ET_TOTAL_USERS users through $ET_LANTERN_URL over $ET_RAMP_UP_SECONDS secs. Expect a report in approximately $ET_TOTAL_TIME seconds."
USERNAME="gatling-bot"
ICON_EMOJI=":gatling:"

SLACK_CHANNEL_URL="https://hooks.slack.com$ET_HOME_CHANNEL"

sleep 2

curl -X POST -H "Content-Type:application/json" \
    --data '{ "text":"'"$TEXT"'", "username":"'"$USERNAME"'", "icon_emoji":"'"$ICON_EMOJI"'" }' $SLACK_CHANNEL_URL

sleep $SLEEP_TIME
mvn gatling:execute -Dgatling.simulationClass=LanternSimulation
