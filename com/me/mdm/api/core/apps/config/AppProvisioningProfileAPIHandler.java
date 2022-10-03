package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppProvisioningProfileAPIHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppProvisioningProfileAPIHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", (Object)this.appFacade.addProvProfileDetails(apiRequest.toJSONObject(), 1));
            responseDetails.put("status", 200);
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "APIHTTPException in AppProvisioningProfileAPIHandler doPost method", ex);
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in AppProvisioningProfileAPIHandler doPost method", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.appFacade.getProvProfileDetailsFromAppId(apiRequest.toJSONObject(), 1));
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException in AppProvisioningProfileAPIHandler doGet method", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "APIHTTPException in AppProvisioningProfileAPIHandler doGet method", ex);
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in AppProvisioningProfileAPIHandler doGet method", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
