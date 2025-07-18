#!/bin/bash
echo "Deploying Flink connector..."
gradle clean build
gradle clean shadowJar
curl -X POST -F "jarfile=@/Users/manmar/Personal/cp-all-in-one/cp-all-in-one/raw/flink-connector/build/libs/flink-connector-1.0-SNAPSHOT-all.jar" http://localhost:8081/jars/upload
echo "Deploying done"