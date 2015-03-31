package com.percyvega.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.percyvega.model.IntergateTransaction;

import java.io.IOException;

/**
 * Created by pevega on 3/25/2015.
 */
public abstract class JacksonUtil {

    public static String fromTransactionToJson(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    public static IntergateTransaction fromJsonToTransaction(String json) throws IOException {
        return new ObjectMapper().readValue(json, IntergateTransaction.class);
    }

}
