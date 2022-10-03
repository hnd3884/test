package com.me.mdm.api.core.apps.provisioning;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.provisioningprofiles.ProvisioningProfileFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualProvProfilesRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "provisioningprofiles";
            response.put("RESPONSE", (Object)new ProvisioningProfileFacade().getAppleProvisioningProfilesDetails(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to fetch the provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to fetch the provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            request.urlStartKey = "provisioningprofiles";
            response.put("RESPONSE", (Object)new ProvisioningProfileFacade().addOrUpdateProvisioningProfile(request.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to modify the provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to modify the provisioning profile", e2);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest request) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            request.urlStartKey = "provisioningprofiles";
            new ProvisioningProfileFacade().deleteProvisioningProfile(request.toJSONObject());
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Failed to delete the provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Failed to delete the provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
