#!/bin/bash

# create
./gradlew clean && ./gradlew stage

./gradlew flywayInfo && ./gradlew flywayMigrate

JAVA_OPTS="-Xms28g -Xmx28g" ./build/stage/main/bin/main
