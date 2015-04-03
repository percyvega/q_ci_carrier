package com.percyvega.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by pevega on 3/25/2015.
 */
public abstract class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

}
