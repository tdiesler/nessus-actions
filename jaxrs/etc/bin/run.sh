#!/bin/bash

PRG="$0"

# Get absolute path of the HOMEDIR
HOMEDIR=`dirname $PRG`/..
HOMEDIR=`cd $HOMEDIR; pwd`

#######################################################
#
# KEYCLOAK CONFIG
#
if [[ -z "${KEYCLOAK_URL}" ]]; then
    export KEYCLOAK_URL="http://keycloak:8080/auth"
fi
if [[ -z "${KEYCLOAK_USER}" ]]; then
    export KEYCLOAK_USER="admin"
fi
if [[ -z "${KEYCLOAK_PASSWORD}" ]]; then
    export KEYCLOAK_PASSWORD="admin"
fi

#######################################################
#
# TLS CONFIG
#
if [[ -z "${JAXRS_TLS_PORT}" ]]; then
    export JAXRS_TLS_PORT=9443
fi
export JAXRS_TLS_CRT=/etc/x509/https/tls.crt
export JAXRS_TLS_KEY=/etc/x509/https/tls.key

JAVA_OPTS="-server"

cd $HOMEDIR
java $JAVA_OPTS -Dlog4j.configuration=file://$HOMEDIR/config/log4j.properties \
     -jar $HOMEDIR/lib/@project.artifactId@-@project.version@.jar "$@" 
