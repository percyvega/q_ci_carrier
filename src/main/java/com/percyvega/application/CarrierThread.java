package com.percyvega.application;

//import com.percyvega.jms.JMSSender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.percyvega.jms.JMSSender;
import com.percyvega.model.IntergateTransaction;
import com.percyvega.model.Status;
import com.percyvega.util.JacksonUtil;
import com.percyvega.util.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import java.util.Date;

/**
 * Created by pevega on 2/25/2015.
 */
public class CarrierThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CarrierThread.class);

    public static final int SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL = 10000;
    public static final int SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE = 10000;

    private static RestTemplate restTemplate = new RestTemplate();

    private static JMSSender jmsSender;
    public static void setJmsSender(JMSSender jmsSender) {
        CarrierThread.jmsSender = jmsSender;
    }

    private String destinationUrl;
    private IntergateTransaction intergateTransaction;

    public CarrierThread(String destinationUrl, IntergateTransaction intergateTransaction) {
        super(intergateTransaction.getObjid().toString());

        if(jmsSender == null)
            throw new RuntimeException("jmsSender can not be null.");

        this.destinationUrl = destinationUrl;
        this.intergateTransaction = intergateTransaction;
    }

    @Override
    public void run() {
        try {
            obtainResponse();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sendToRespQueue();
        }

    }

    private void obtainResponse() {
        int destinationUrlUnavailableCount = 0;
        do {
            try {
//              String response = restTemplate.getForObject(getUrl(intergateTransaction.getMdn(), String.class);
                intergateTransaction.setResponse("Processed on " + new Date().toString());
                intergateTransaction.setStatus(Status.PROCESSED);
                break;
            } catch (ResourceAccessException e) {
                logger.debug("Destination URL unavailable #" + ++destinationUrlUnavailableCount + ". About to sleep(" + SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL + ").");
                Sleeper.sleep(SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL);
            }
        } while (true);
    }

    private void sendToRespQueue() {
        int destinationQueueUnavailableCount = 0;
        do {
            try {
                jmsSender.sendMessage(Long.toString(intergateTransaction.getObjid()), JacksonUtil.fromTransactionToJson(intergateTransaction));
                break;
            } catch (JMSException e) {
                logger.debug("Destination Queue unavailable #" + ++destinationQueueUnavailableCount + ". About to sleep(" + SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE + ").");
                Sleeper.sleep(SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                intergateTransaction.setResponse(e.getMessage());
                intergateTransaction.setStatus(Status.ERROR);
                break;
            }
        } while (true);
    }

}
