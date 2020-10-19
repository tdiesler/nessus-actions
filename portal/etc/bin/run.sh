#!/bin/sh

PRG="$0"

# Get absolute path of the HOMEDIR
HOMEDIR=`dirname $PRG`/..
HOMEDIR=`cd $HOMEDIR; pwd`

JAVA_OPTS="-server"

java $JAVA_OPTS -Dlog4j.configuration=file://$HOMEDIR/config/log4j.properties \
     -jar $HOMEDIR/lib/@project.artifactId@-@project.version@.jar "$@" 
