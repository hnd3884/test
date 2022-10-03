package com.me.mdm.api.core.apppermission;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.AppPermissionFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppPermissionRequestHandler extends ApiRequestHandler
{
    private AppPermissionFacade handler;
    
    public IndividualAppPermissionRequestHandler() {
        this.handler = new AppPermissionFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.handler.getAppPermissionDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to get permission details", e);
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Failed to get permission details", (Throwable)e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.handler.addOrModifyAppPermission(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to modify the permission", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to modify the permission", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            this.handler.deleteAppPermissionDetails(apiRequest.toJSONObject());
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to delete the permission", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to delete the permission", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
