package com.percyvega.jms;

import com.percyvega.application.AttCarrierThread;
import com.percyvega.model.Carrier;
import com.percyvega.model.IntergateTransaction;
import com.percyvega.model.Status;
import com.percyvega.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private boolean quit = false;
    private long count = 0;

    public boolean isQuit() {
        return quit;
    }

    private static String qcfName;
    @Value("${jms.qcfName}")
    public void setQcfName(String qcfName) {
        this.qcfName = qcfName;
    }

    private static String queueName;
    @Value("${jms.sourceQueueName}")
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

    public void init() {
        try {
            Hashtable properties = new Hashtable();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, icfName);
            properties.put(Context.PROVIDER_URL, providerUrl);
            initialContext = new InitialContext(properties);
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(qcfName);
            queueConnection = queueConnectionFactory.createQueueConnection();
            queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = (Queue) initialContext.lookup(queueName);
            queueReceiver = queueSession.createReceiver(queue);
            queueReceiver.setMessageListener(this);
            queueConnection.start();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            logger.warn(e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        queueReceiver.close();
        queueSession.close();
        queueConnection.close();
    }

    @Override
    public void onMessage(Message msg) {
        try {
            String msgText;
            if (msg instanceof TextMessage) {
                msgText = ((TextMessage) msg).getText();
            } else {
                msgText = msg.toString();
            }

            logger.debug("JMS Message #" + ++count + ": " + msgText);
            IntergateTransaction intergateTransaction;
            try {
                intergateTransaction = JacksonUtil.fromJsonToTransaction(msgText);
                intergateTransaction.setStatus(Status.PROCESSING);
                processTransaction(intergateTransaction);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    private void processTransaction(IntergateTransaction intergateTransaction) {
        String carrierName = intergateTransaction.getCarrierName();
        switch (Carrier.getByName(carrierName)) {
            case ATT:
                new AttCarrierThread(intergateTransaction).start();
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
    public String toString() {
        return "JMSSender [icfName=" + icfName + ", providerUrl=" + providerUrl + ", qcfName=" + qcfName + ", queueName=" + queueName + "]";
    }

}
