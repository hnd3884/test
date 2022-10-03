package com.me.mdm.api.datatracking;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.datausage.DataUsagePolicyFacade;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.core.profiles.config.IndividualProfilesAPIRequestHandler;

public class IndividualDatatrackingAPIRequestHandler extends IndividualProfilesAPIRequestHandler
{
    private ProfileFacade profile;
    
    public IndividualDatatrackingAPIRequestHandler() {
        this.profile = new DataUsagePolicyFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseDetails.put("RESPONSE", (Object)this.profile.getProfile(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while get details of data tracking", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            apiRequest.urlStartKey = "profiles";
            this.profile.modifyProfile(apiRequest.toJSONObject());
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while updating data tracking", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.profile.deleteOrTrashProfile(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while deleting the data tracking", ex2);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
