package com.sun.xml.internal.ws.client;

public enum ContentNegotiation
{
    none, 
    pessimistic, 
    optimistic;
    
    public static final String PROPERTY = "com.sun.xml.internal.ws.client.ContentNegotiation";
    
    public static ContentNegotiation obtainFromSystemProperty() {
        try {
            final String value = System.getProperty("com.sun.xml.internal.ws.client.ContentNegotiation");
            if (value == null) {
                return ContentNegotiation.none;
            }
            return valueOf(value);
        }
        catch (final Exception e) {
            return ContentNegotiation.none;
        }
    }
}
