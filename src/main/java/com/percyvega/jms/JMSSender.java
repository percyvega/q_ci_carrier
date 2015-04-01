package com.percyvega.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

/**
 * Created by pevega on 1/21/2015.
 */
@Component
public class JMSSender {

    private static final Logger logger = LoggerFactory.getLogger(JMSSender.class);

    private InitialContext initialContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private Queue queue;
    private QueueSender queueSender;

    private long messageCounter = 0;

    @Value("${jms.icfName}")
    private String icfName;

    @Value("${jms.qcfName}")
    private String qcfName;

    @Value("${jms.destinationQueueName}")
    private String destinationQueueName;

    @Value("${jms.providerUrl}")
    private String providerUrl;

    public void init() {
        try {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, icfName);
            properties.put(Context.PROVIDER_URL, providerUrl);
            initialContext = new InitialContext(properties);
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(qcfName);
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, 0);
            queue = (Queue) initialContext.lookup(destinationQueueName);
            queueSender = queueSession.createSender(queue);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }

    public void sendMessage(String correlationId, String messageText) throws JMSException {
        TextMessage textMessage = queueSession.createTextMessage();
        textMessage.setJMSCorrelationID(correlationId);
        textMessage.setText(messageText);

        queueSender.send(textMessage);

        logger.debug("Sent JMS message #" + ++messageCounter + ": " + messageText);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        queueSender.close();
        queueSession.close();
        queueConnection.close();
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug(this.toString());
    }

    @Override
    public String toString() {
        return "JMSSender [icfName=" + icfName + ", providerUrl=" + providerUrl + ", qcfName=" + qcfName + ", destinationQueueName=" + destinationQueueName + "]";
    }

}
