package com.me.mdm.server.apps.provisioningprofiles;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProvisioningProfileFacade
{
    private Logger logger;
    private static final String PROV_ID_KEY = "provisioningprofile_id";
    public static final String PROV_START_URL_KEY = "provisioningprofiles";
    
    public ProvisioningProfileFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAppleProvisioningProfilesDetails(final JSONObject request) {
        try {
            final Long customerID = APIUtil.getCustomerID(request);
            final Long provID = APIUtil.getResourceID(request, "provisioningprofile_id");
            return new ProvisioningProfilesDataHandler().getAppleProvProfilesDetails(provID, customerID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to get provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addOrUpdateProvisioningProfile(final JSONObject request) {
        try {
            final Long customerID = APIUtil.getCustomerID(request);
            final Long provID = APIUtil.getResourceID(request, "provisioningprofile_id");
            JSONObject message = request.getJSONObject("msg_body");
            message.put("PROV_ID", (Object)provID);
            message.put("CUSTOMER_ID", (Object)customerID);
            message = JSONUtil.getInstance().changeJSONKeyCase(message, 1);
            return new ProvisioningProfilesDataHandler().addOrUpdateAppleProvProfiles(message);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to perform add/update provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to perform add/update provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteProvisioningProfile(final JSONObject request) {
        try {
            final Long customerID = APIUtil.getCustomerID(request);
            final Long provID = APIUtil.getResourceID(request, "provisioningprofile_id");
            new ProvisioningProfilesDataHandler().deleteAppleProvisioningProfile(provID, customerID);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Unable to delete provisioning profile", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Unable to delete provisioning profile", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
