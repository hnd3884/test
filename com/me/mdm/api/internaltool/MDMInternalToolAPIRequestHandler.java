package com.me.mdm.api.internaltool;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MDMInternalToolAPIRequestHandler extends ApiRequestHandler
{
    InternalToolFacade internalToolFacade;
    
    public MDMInternalToolAPIRequestHandler() {
        this.internalToolFacade = new InternalToolFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final String requestName = this.getRequestName(apiRequest);
        final JSONObject requestJson = apiRequest.toJSONObject();
        final JSONObject response = new JSONObject();
        try {
            final String s = requestName;
            switch (s) {
                case "simulate_user": {
                    this.internalToolFacade.createDemoUser(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "refresh_queue": {
                    this.internalToolFacade.refreshQueue(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "suspend_queue": {
                    this.internalToolFacade.suspendQueue(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "resume_queue": {
                    this.internalToolFacade.resumeQueue(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "simulate_device": {
                    this.internalToolFacade.simulateDevices(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "simulate_group": {
                    this.internalToolFacade.simulateGroups(requestJson);
                    response.put("status", 202);
                    return response;
                }
                case "simulate_scan": {
                    this.internalToolFacade.simulateScanDevices(requestJson);
                    response.put("status", 202);
                    return response;
                }
                default: {
                    throw new APIHTTPException("COM0014", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.WARNING, "Issue on adding demo user", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    private String getRequestName(final APIRequest apiRequest) throws APIHTTPException {
        try {
            return apiRequest.pathInfo.split("/")[2];
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in getRequestName()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
