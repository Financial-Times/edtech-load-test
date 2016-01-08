#!/usr/bin/env bash

mvn gatling:execute -Dgatling.simulationClass=LanternAccessSimulation -Dusers=100 -Dramp-up-minutes=1 -Dsoak-duration-minutes=5
