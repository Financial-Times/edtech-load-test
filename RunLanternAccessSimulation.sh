#!/bin/bash

LANTERN_USERS=10
RAMP_UP_MINUTES=1
SOAK_DURATION_MINUTES=5

SLEEP_TIME=10
TEXT="In $SLEEP_TIME seconds: Running $LANTERN_USERS users through Lantern over $RAMP_UP_MINUTES min"
USERNAME="gatling-bot"
ICON_EMOJI=":gatling:"

SLACK_CHANNEL_URL="https://hooks.slack.com$HOME_CHANNEL"

sleep 2

curl -X POST -H "Content-Type:application/json" \
    --data '{ "text":"'"$TEXT"'", "username":"'"$USERNAME"'", "icon_emoji":"'"$ICON_EMOJI"'" }' $SLACK_CHANNEL_URL


sleep $SLEEP_TIME
mvn gatling:execute -Dgatling.simulationClass=LanternAccessSimulation -Dusers=$LANTERN_USERS -Dramp-up-minutes=$RAMP_UP_MINUTES -Dsoak-duration-minutes=$SOAK_DURATION_MINUTES
