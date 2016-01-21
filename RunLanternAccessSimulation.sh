#!/bin/bash

export $(cat .env)

LANTERN_USERS=10
RAMP_UP_SECONDS=20
SOAK_DURATION_MINUTES=5

SLEEP_TIME=10
TEXT="In $SLEEP_TIME seconds: Running $LANTERN_USERS users through Lantern over $RAMP_UP_SECONDS secs"
USERNAME="gatling-bot"
ICON_EMOJI=":gatling:"

SLACK_CHANNEL_URL="https://hooks.slack.com$ET_HOME_CHANNEL"

sleep 2

curl -X POST -H "Content-Type:application/json" \
    --data '{ "text":"'"$TEXT"'", "username":"'"$USERNAME"'", "icon_emoji":"'"$ICON_EMOJI"'" }' $SLACK_CHANNEL_URL

curl -X POST -H "Content-Type:application/json" \
    --data '{"size":0,"sort":{"initial_publish_date":"desc"},"query":{"filtered":{"query":{"query_string":{"query":"event_timestamp:[now-1d/d TO now] AND last_publish_date:[now-1d/d TO now] LIMIT event_type:page AND event_category:view","analyze_wildcard":true}}}},"aggs":{"topic_views":{"terms":{"field":"article_uuid","size":5}}}}' https://search-editorial-lantern-hjxj7io5fcw4k4rx7uxm6s3paq.eu-west-1.es.amazonaws.com/realtime*/_search


sleep $SLEEP_TIME
mvn gatling:execute -Dgatling.simulationClass=LanternAccessSimulation -Dusers=$LANTERN_USERS -Dramp-up-seconds=$RAMP_UP_SECONDS -Dsoak-duration-minutes=$SOAK_DURATION_MINUTES
