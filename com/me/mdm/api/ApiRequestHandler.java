package com.me.mdm.api;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Logger;

public class ApiRequestHandler
{
    protected Logger logger;
    final APIEndpointStratergy endpointStratergy;
    
    public ApiRequestHandler() {
        this.logger = Logger.getLogger("MDMAPILogger");
        this.endpointStratergy = new APIEndpointStratergy();
    }
    
    protected ApiRequestHandler(final APIEndpointStratergy exportDetailsStratergy) {
        this.logger = Logger.getLogger("MDMAPILogger");
        this.endpointStratergy = exportDetailsStratergy;
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        return this.endpointStratergy.doGet(apiRequest);
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        return this.endpointStratergy.doPost(apiRequest);
    }
    
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        return this.endpointStratergy.doPut(apiRequest);
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        return this.endpointStratergy.doDelete(apiRequest);
    }
    
    public Object doHead(final APIRequest apiRequest) throws APIHTTPException {
        return this.endpointStratergy.doHead(apiRequest);
    }
}
