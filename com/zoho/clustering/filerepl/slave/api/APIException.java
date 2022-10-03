package com.zoho.clustering.filerepl.slave.api;

public class APIException extends RuntimeException
{
    public APIException(final String message) {
        super(message);
    }
    
    public APIException(final Throwable cause) {
        super(cause);
    }
}
