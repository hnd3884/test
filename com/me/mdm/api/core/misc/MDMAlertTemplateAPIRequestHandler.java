package com.me.mdm.api.core.misc;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.alerts.MDMAlertFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MDMAlertTemplateAPIRequestHandler extends ApiRequestHandler
{
    private MDMAlertFacade alertFacade;
    
    public MDMAlertTemplateAPIRequestHandler() {
        this.alertFacade = new MDMAlertFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.alertFacade.getAlertFormat(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.alertFacade.revertAlertFormat(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            response.put("RESPONSE", (Object)this.alertFacade.modifyAlertFormat(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
