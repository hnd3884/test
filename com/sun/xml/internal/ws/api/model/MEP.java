package com.sun.xml.internal.ws.api.model;

public enum MEP
{
    REQUEST_RESPONSE(false), 
    ONE_WAY(false), 
    ASYNC_POLL(true), 
    ASYNC_CALLBACK(true);
    
    public final boolean isAsync;
    
    private MEP(final boolean async) {
        this.isAsync = async;
    }
    
    public final boolean isOneWay() {
        return this == MEP.ONE_WAY;
    }
}
