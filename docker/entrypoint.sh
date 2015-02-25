#!/bin/bash
set -e

GLASSFISH="/opt/glassfish4"
GLASSFISH_BIN="$GLASSFISH/bin/asadmin"
GLASSFISH_DOMAIN="$GLASSFISH/glassfish/domains/domain1"
GLASSFISH_OPTS="--user admin --passwordfile /opt/glassfish4/pwdfile"

shopt -s nullglob
files=(*)
if [[ ${#files[@]} -eq 0 && $REPO ]]; then
    git clone $REPO .
    if [[ -e "domain.xml" ]]; then
        echo "Copying domain.xml to $GLASSFISH_DOMAIN/config"
        cp domain.xml $GLASSFISH_DOMAIN/config
    fi
    mvn package -Dmaven.test.skip=true
    yes | $GLASSFISH_BIN $GLASSFISH_OPTS start-domain
    yes | $GLASSFISH_BIN $GLASSFISH_OPTS deploy --contextroot / target/*.war
    yes | $GLASSFISH_BIN $GLASSFISH_OPTS stop-domain
fi

if [ "$1" == "start" ]; then
    $GLASSFISH_BIN $GLASSFISH_OPTS start-domain --verbose=true
fi

exec "$@"
