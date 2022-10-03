package com.me.mdm.api.datatracking;

import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.datausage.DataUsagePolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DataTrackingProfileAPIRequestHandler extends ApiRequestHandler
{
    DataUsagePolicyFacade profile;
    private Logger logger;
    
    public DataTrackingProfileAPIRequestHandler() {
        this.profile = null;
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profile = new DataUsagePolicyFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profile.getProfiles(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception when getting Data usage profile", ex2);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profile.createDataUsageProfile(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            this.logger.log(Level.SEVERE, "Exception when adding Data usage profile", ex3);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.deleteOrTrashProfile(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception when deleting Data usage profile", ex2);
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
