#!/bin/bash

. setVars_dev.sh
if [ -z "$APP_NAME" ]
then
    echo "$APP_NAME cannot be empty."
    exit
fi

cd ..

java \
-D$APP_NAME \
-jar target/$APP_NAME-1.0-SNAPSHOT.jar \
    $attDestinationUrl \
    $jms_qcfName \
    $jms_providerUrl \
    $jms_icfName \
    $jms_sourceQueueName \
    $jms_destinationQueueName
