#!/bin/sh
mvn clean
mvn package
java -jar target/dependency/webapp-runner.jar target/*.war