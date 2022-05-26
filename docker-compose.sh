#!/bin/bash

echo "Removing stopped unreferenced containers.. "
docker container rm -f $(docker container ls -aq)

echo "Building upload-service-application jar"
mvn clean install -DskipTests=true -Dmaven.javadoc.skip=true

echo "Building upload-service-application docker image.."
docker build -t upload-service-application:latest -f Dockerfile.local

echo "Starting docker compose"
docker-compose -f docker-compose.yml up --build