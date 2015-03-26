package com.percyvega.q_ci_carrier.application;

//import com.percyvega.q_ci_carrier.jms.JMSSender;

import com.percyvega.q_ci_carrier.jms.JMSSender;
import com.percyvega.q_ci_carrier.model.IntergateTransaction;
import com.percyvega.q_ci_carrier.model.Status;
import com.percyvega.util.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.jms.JMSException;

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
        int destinationUrlUnavailableCount = 0;
        int destinationQueueUnavailableCount = 0;
        String url = getUrl(intergateTransaction.getMdn());

        try {
            do {
                try {
//                    intergateTransaction.setResponse(restTemplate.getForObject(url, String.class));
                    logger.debug("Assuming we were able to connect.");
                    intergateTransaction.setStatus(Status.PROCESSED);
                    break;
                } catch (ResourceAccessException e) {
                    logger.debug("Destination URL unavailable #" + ++destinationUrlUnavailableCount + ". About to sleep(" + SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL + ").");
                    Sleeper.sleep(SLEEP_WHEN_UNAVAILABLE_DESTINATION_URL);
                }
            } while (true);

            logger.debug(intergateTransaction.toString());

            do {
                try {
                    jmsSender.sendMessage(intergateTransaction.toString());
                    break;
                } catch (JMSException e) {
                    logger.debug("Destination Queue unavailable #" + ++destinationQueueUnavailableCount + ". About to sleep(" + SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE + ").");
                    Sleeper.sleep(SLEEP_WHEN_UNAVAILABLE_DESTINATION_QUEUE);
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
