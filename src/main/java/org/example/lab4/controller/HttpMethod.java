package org.example.lab4.controller;

public enum HttpMethod {
    DELETE("DELETE"),
    HEAD("HEAD"),
    GET("GET"),
    OPTIONS("OPTIONS"),
    POST("POST"),
    PUT("PUT"),
    TRACE("TRACE");

    private final String stringValue;

    HttpMethod(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
