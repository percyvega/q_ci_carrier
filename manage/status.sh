#!/bin/bash

. setVars.sh
if [ -z "$APP_NAME" ]
then
    echo "$APP_NAME cannot be empty."
    exit
fi

PID=`ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}'`

if [ "$PID" ]
then
    echo "Process with name=$APP_NAME is running with PID=$PID."
else
    echo "Process with name=$APP_NAME is not running."
fi
