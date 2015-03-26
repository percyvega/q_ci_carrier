package com.percyvega.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.percyvega.q_ci_carrier.model.Carrier;
import com.percyvega.q_ci_carrier.model.IntergateTransaction;

import java.io.IOException;

/**
 * Created by pevega on 3/25/2015.
 */
public abstract class JacksonUtil {

    public static String toJson(Object o) {
        String jsonString = null;

        try {
            jsonString = new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public static IntergateTransaction fromJson(String json) {
        IntergateTransaction intergateTransaction = null;

        try {
            intergateTransaction = new ObjectMapper().readValue(json, IntergateTransaction.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return intergateTransaction;
    }

    public static void main(String[] args) throws JsonProcessingException {
        IntergateTransaction intergateTransaction = new IntergateTransaction("9547325664", Carrier.ATT);
        System.out.println(toJson(intergateTransaction));

        String json = "{\"objid\":143,\"mdn\":\"8087475399\",\"carrierName\":\"ATT\",\"orderType\":\"I\",\"status\":\"PICKED_UP\",\"tryCount\":30,\"creationDate\":1427310044000,\"updateDate\":1427310063142,\"response\":null}";
        System.out.println("fromJson:\n" + fromJson(json));
    }

}
