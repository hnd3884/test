package com.sun.corba.se.impl.protocol;

public class RequestCanceledException extends RuntimeException
{
    private int requestId;
    
    public RequestCanceledException(final int requestId) {
        this.requestId = 0;
        this.requestId = requestId;
    }
    
    public int getRequestId() {
        return this.requestId;
    }
}
