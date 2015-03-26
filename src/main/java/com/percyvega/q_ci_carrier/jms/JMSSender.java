package com.percyvega.q_ci_carrier.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private TextMessage textMessage;

    private static String qcfName;
    @Value("${jms.qcfName}")
    public void setQcfName(String qcfName) {
        this.qcfName = qcfName;
    }

    private static String queueName;
    @Value("${jms.destinationQueueName}")
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    private static String providerUrl;
    @Value("${jms.providerUrl}")
    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    private static String icfName;
    @Value("${jms.icfName}")
    public void setIcfName(String icfName) {
        this.icfName = icfName;
    }

    @Override
    public String toString() {
        return "JMSSender [icfName=" + icfName + ", providerUrl=" + providerUrl + ", qcfName=" + qcfName + ", queueName=" + queueName + "]";
    }

    private boolean initialized = false;

    public void init() {
        initialized = true;
        try {
            Hashtable properties = new Hashtable();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, icfName);
            properties.put(Context.PROVIDER_URL, providerUrl);
            initialContext = new InitialContext(properties);
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(qcfName);
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, 0);
            queue = (Queue) initialContext.lookup(queueName);
            queueSender = queueSession.createSender(queue);
            textMessage = queueSession.createTextMessage();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }

    public void sendMessage(String messageText) throws JMSException {
        if(!initialized)
            init();

        textMessage.setText(messageText);

        queueSender.send(textMessage);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        queueSender.close();
        queueSession.close();
        queueConnection.close();
    }

    public static void main(String args[]) throws JMSException {
        JMSSender jmsSender = new JMSSender();
        for (int i = 1; i <= 10; i++)
            jmsSender.sendMessage("This is my JMS message #" + i + "!");
    }
}