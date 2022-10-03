package com.zoho.clustering.filerepl.slave.api;

public class APIResourceException extends RuntimeException
{
    public APIResourceException(final String message) {
        super(message);
    }
    
    public APIResourceException(final Throwable cause) {
        super(cause);
    }
}
