package com.me.mdm.chrome.agent.commands.profiles;

import java.util.logging.Logger;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import java.util.logging.Level;
import com.google.chromedevicemanagement.v1.model.OpenNetworkConfig;
import java.io.IOException;
import org.json.JSONException;
import com.me.mdm.chrome.agent.Context;

public class UserONCPayload extends ONCPayload
{
    public UserONCPayload(final Context context) throws JSONException, IOException {
        super(context);
    }
    
    @Override
    protected OpenNetworkConfig getOpenNetworkConfig(final Context context) throws IOException {
        this.logger.log(Level.INFO, context.getCMPAEnterpriseAndUDID());
        try {
            final UserPolicy userPolicy = (UserPolicy)context.getCMPAService().enterprises().users().getUserPolicy(context.getCMPAEnterpriseAndUDID()).execute();
            if (userPolicy != null) {
                final OpenNetworkConfig openNetworkConfig = userPolicy.getOpenNetworkConfig();
                return openNetworkConfig;
            }
            return new OpenNetworkConfig();
        }
        catch (final GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 404) {
                return new OpenNetworkConfig();
            }
            throw ex;
        }
    }
    
    @Override
    protected void setOpenNetworkConfig(final Context context, final OpenNetworkConfig openNetworkConfig) throws IOException {
        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setOpenNetworkConfig(openNetworkConfig);
        Logger.getLogger(ONCPayload.class.getName()).log(Level.INFO, openNetworkConfig.toPrettyString());
        userPolicy = (UserPolicy)context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask("openNetworkConfig").execute();
        this.logger.log(Level.INFO, userPolicy.toPrettyString());
    }
}
