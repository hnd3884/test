package com.me.mdm.api.core.profiles.config.payloads;

import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesPayloadItemsAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public ProfilesPayloadItemsAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseDetails.put("RESPONSE", (Object)this.profile.getPayload(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while getting the details of the payload", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.profile.deletePayload(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while deleting the payload", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseJSON.put("RESPONSE", (Object)this.profile.modifyPayload(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            this.logger.log(Level.SEVERE, "Exception while updating the payload");
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
