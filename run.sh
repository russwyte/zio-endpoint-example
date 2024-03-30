#!/bin/sh
echo "starting the service..."
java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /home/appuser/server.jar