package com.me.mdm.api.datatracking;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.datausage.DataUsagePolicyFacade;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDataGroupMultiProfileAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    protected ProfileFacade profileFacade;
    
    public IndividualDataGroupMultiProfileAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileFacade = new DataUsagePolicyFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            this.profileFacade.associateProfilesToGroups(request);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            this.profileFacade.disassociateProfilesToGroups(request);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.profileFacade.getGroupProfiles(request));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
