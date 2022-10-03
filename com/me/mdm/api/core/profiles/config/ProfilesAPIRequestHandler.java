package com.me.mdm.api.core.profiles.config;

import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public ProfilesAPIRequestHandler() {
        this.profile = null;
        this.profile = new ProfileFacade();
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
            this.logger.log(Level.SEVERE, "Exception while profiles get", ex2);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profile.createProfile(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            this.logger.log(Level.SEVERE, "Exception while creating profiles", ex3);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            message.put("permanent_delete", true);
            this.profile.deleteOrTrashProfile(message);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception while deleting the profiles", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while deleting the profiles", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
