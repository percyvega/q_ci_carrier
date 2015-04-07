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
    echo $PID | xargs kill -9
    echo "Process with name=$APP_NAME and PID=$PID was killed."
else
    echo "Process with name=$APP_NAME was not found."
fi
