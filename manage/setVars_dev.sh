#!/bin/bash

APP_NAME=q_ci_carrier
export APP_NAME

attDestinationUrl=http://localhost:8585/actions/post/
export attDestinationUrl
jms_qcfName=jms/myConnectionFactory
export jms_qcfName
jms_providerUrl=t3://localhost:8001
export jms_providerUrl
jms_icfName=weblogic.jndi.WLInitialContextFactory
export jms_icfName
jms_sourceQueueName=jms/percyvegaQueue
export jms_sourceQueueName
jms_destinationQueueName=jms/percyvegaRespQueue
export jms_destinationQueueName
