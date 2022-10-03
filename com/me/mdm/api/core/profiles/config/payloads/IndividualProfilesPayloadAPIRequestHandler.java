package com.me.mdm.api.core.profiles.config.payloads;

import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualProfilesPayloadAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public IndividualProfilesPayloadAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseDetails.put("RESPONSE", (Object)this.profile.getPayloads(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while getting payload details", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.profile.deletePayloads(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while bulking deleting payloads", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseJSON.put("RESPONSE", (Object)this.profile.addPayload(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while adding the payload", ex2);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
