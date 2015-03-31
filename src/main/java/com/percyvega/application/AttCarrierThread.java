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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.jms.JMSException;
import java.util.Date;

/**
 * Created by pevega on 2/25/2015.
 */
@Component
public class AttCarrierThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(AttCarrierThread.class);

    public static final int SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL = 15000;
    public static final int SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE = 15000;

    private static JMSSender jmsSender = new JMSSender();
    private static RestTemplate restTemplate = new RestTemplate();

    private static String destinationUrl;
    private IntergateTransaction intergateTransaction;

    @Value("${attDestinationUrl}")
    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public AttCarrierThread() {
        super("do-not-use");
    }

    public AttCarrierThread(IntergateTransaction intergateTransaction) {
        super(intergateTransaction.getObjid().toString());
        this.intergateTransaction = intergateTransaction;
    }

    @Override
    public void run() {
        try {
            obtainResponse();
            logger.debug(JacksonUtil.fromTransactionToJson(intergateTransaction));
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
                logger.debug("Assuming we were able to connect to URL destination.");
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

    private String getUrl(String mdn) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(destinationUrl);
        builder.queryParam("mdn", mdn);

        return builder.build().toUriString();
    }

    @Override
    protected void finalize() throws Throwable {
        jmsSender = null;
        restTemplate = null;
    }
}
