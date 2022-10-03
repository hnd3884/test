package com.me.mdm.api.core.profiles.config.payloads;

import java.util.logging.Level;
import com.me.mdm.server.payload.PayloadException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesBulkPayloadItemsAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public ProfilesBulkPayloadItemsAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseJSON.put("RESPONSE", (Object)this.profile.addPayloadItems(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
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
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseJSON.put("RESPONSE", (Object)this.profile.modifyPayloadItems(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
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
