#!/bin/bash
echo "Starting..."
cd /opt/litef
# java7/bin/java -Dconfig.resource=/opt/litef/etc/application.conf -jar litef-conductor.jar &> /dev/null &
java7/bin/java -cp etc:litef-conductor.jar core.Rest  &> /dev/null &

