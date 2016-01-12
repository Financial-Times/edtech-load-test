#!/bin/bash

LANTERN_USERS=10
RAMP_UP_MINUTES=1
SOAK_DURATION_MINUTES=5

TEXT="In 60 seconds: Running $LANTERN_USERS users through Lantern over $RAMP_UP_MINUTES min"
USERNAME="gatling-bot"
ICON_EMOJI=":gatling:"

CT="\"Content-Type: application/json\""
JSON="'"{\"text\":\""$TEXT"\",\"username\":\""$USERNAME"\",\"icon_emoji\":\""$ICON_EMOJI"\"}"'"
SLACK_CHANNEL_URL="https://hooks.slack.com$HOME_CHANNEL"

echo curl -H $CT -X POST -d $JSON $SLACK_CHANNEL_URL
sleep 60
mvn gatling:execute -Dgatling.simulationClass=LanternAccessSimulation -Dusers=$LANTERN_USERS -Dramp-up-minutes=$RAMP_UP_MINUTES -Dsoak-duration-minutes=$SOAK_DURATION_MINUTES
