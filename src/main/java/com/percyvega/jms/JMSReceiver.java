package com.percyvega.jms;

import com.percyvega.application.CarrierThread;
import com.percyvega.model.Carrier;
import com.percyvega.model.IntergateTransaction;
import com.percyvega.model.Status;
import com.percyvega.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by pevega on 1/21/2015.
 */
@Component
public class JMSReceiver implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(JMSReceiver.class);

    private InitialContext initialContext;
    private QueueConnectionFactory queueConnectionFactory;
    private QueueConnection queueConnection;
    private QueueSession queueSession;
    private QueueReceiver queueReceiver;
    private Queue queue;

    private long messageCounter = 0;

    @Value("${jms.icfName}")
    private String icfName;

    @Value("${jms.qcfName}")
    private String qcfName;

    @Value("${jms.sourceQueueName}")
    private String sourceQueueName;

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
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = (Queue) initialContext.lookup(sourceQueueName);
            queueReceiver = queueSession.createReceiver(queue);
            queueReceiver.setMessageListener(this);
            queueConnection.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            String messageText;
            if (msg instanceof TextMessage) {
                messageText = ((TextMessage) msg).getText();
            } else {
                messageText = msg.toString();
            }

            logger.debug("Received JMS message #" + ++messageCounter + ": " + messageText);

            try {
                IntergateTransaction intergateTransaction = JacksonUtil.fromJsonToTransaction(messageText);
                intergateTransaction.setStatus(Status.PROCESSING);
                processTransaction(intergateTransaction);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    @Value("${attDestinationUrl}")
    private String attDestinationUrl;

    private void processTransaction(IntergateTransaction intergateTransaction) {
        String carrierName = intergateTransaction.getCarrierName();
        switch (Carrier.getByName(carrierName)) {
            case ATT:
                new CarrierThread(attDestinationUrl, intergateTransaction).start();
                break;
//            case SPR:
//                break;
//            case TMO:
//                break;
//            case SMO:
//                break;
//            case VZW:
//                break;
            default:
                try {
                    throw new IllegalArgumentException("Invalid carrier name: " + carrierName);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        queueReceiver.close();
        queueSession.close();
        queueConnection.close();
    }

    @PostConstruct
    public void postConstruct() {
        logger.debug(this.toString());
    }

    @Override
    public String toString() {
        return "JMSReceiver [icfName=" + icfName + ", providerUrl=" + providerUrl + ", qcfName=" + qcfName + ", sourceQueueName=" + sourceQueueName + "]";
    }

}
