#!/bin/bash
echo "Starting..."
cd /opt/litef
# java7/bin/java -Dconfig.resource=/opt/litef/etc/application.conf -jar litef-conductor.jar &> /dev/null &

# java7/bin/java -cp etc:litef-conductor.jar core.Rest  &> /dev/null &

CPATH="etc:litef-conductor.jar"

for dep in deps/*jar;
do
    CPATH=$CPATH:$dep
done

echo $CPATH

java7/bin/java -cp $CPATH core.Rest  &> /var/log/litef.log-`date +%Y%m%d` &

