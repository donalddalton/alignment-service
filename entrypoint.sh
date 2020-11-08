#!/bin/bash

# create
./gradlew clean && ./gradlew stage

./gradlew flywayInfo && ./gradlew flywayMigrate

JAVA_OPTS="-Xms8g -Xmx12g" ./build/stage/main/bin/main
