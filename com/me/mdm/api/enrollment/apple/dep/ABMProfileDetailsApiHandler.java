package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.AppleDEPProfileFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ABMProfileDetailsApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject profileDetails = AppleDEPProfileFacade.getInstance().getDEPProfile(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)profileDetails);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while retrieving ABM profile..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while retrieving ABM profile..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponse = new JSONObject();
            final JSONObject response = AppleDEPProfileFacade.getInstance().createOrModifyDEPProfile(apiRequest.toJSONObject());
            apiResponse.put("status", 200);
            apiResponse.put("RESPONSE", (Object)response);
            return apiResponse;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while creating ABM profile..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating ABM profile..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponse = new JSONObject();
            final JSONObject response = AppleDEPProfileFacade.getInstance().createOrModifyDEPProfile(apiRequest.toJSONObject());
            apiResponse.put("status", 200);
            apiResponse.put("RESPONSE", (Object)response);
            return apiResponse;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while modifying ABM profile..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while modifying ABM profile..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
