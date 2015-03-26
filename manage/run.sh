#!/bin/bash

cd ..

nohup /opt/bea/bea1033/jdk1.6.0_20/bin/java \
-Dq_ci_carrier \
-jar target/q_ci_carrier-1.0-SNAPSHOT.jar &
