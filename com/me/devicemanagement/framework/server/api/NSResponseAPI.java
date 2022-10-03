package com.me.devicemanagement.framework.server.api;

public interface NSResponseAPI
{
    void parseResponse(final byte[] p0) throws Exception;
    
    String getData(final String p0);
    
    void updateHash(final String p0, final int p1);
    
    void setResponseStatus(final long p0, final int p1);
    
    boolean isEmpty();
    
    int getResponseStatus(final Long p0);
}
