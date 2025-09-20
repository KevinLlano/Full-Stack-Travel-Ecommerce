package com.assessment.demo.controllers;

import java.util.HashMap;
import java.util.Map;

public final class ApiResponseHelper {

    private ApiResponseHelper() {}

    public static Map<String, Object> wrapEmbedded(String key, Object value) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put(key, value);
        response.put("_embedded", embedded);
        return response;
    }
}

