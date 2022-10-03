package com.me.mdm.api;

import com.me.mdm.api.error.APIHTTPException;

public class APIEndpointStratergy
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        throw new APIHTTPException("COM0001", new Object[0]);
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        throw new APIHTTPException("COM0001", new Object[0]);
    }
    
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        throw new APIHTTPException("COM0001", new Object[0]);
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        throw new APIHTTPException("COM0001", new Object[0]);
    }
    
    public Object doHead(final APIRequest apiRequest) throws APIHTTPException {
        throw new APIHTTPException("COM0001", new Object[0]);
    }
}
