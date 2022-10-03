package com.me.mdm.chrome.agent.commands.profiles;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import java.util.logging.Level;
import com.google.chromedevicemanagement.v1.model.OpenNetworkConfig;
import java.io.IOException;
import org.json.JSONException;
import com.me.mdm.chrome.agent.Context;

public class DeviceONCPayload extends ONCPayload
{
    public DeviceONCPayload(final Context context) throws JSONException, IOException {
        super(context);
    }
    
    @Override
    protected OpenNetworkConfig getOpenNetworkConfig(final Context context) throws IOException {
        this.logger.log(Level.INFO, context.getCMPAEnterpriseAndUDID());
        try {
            final DevicePolicy devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
            if (devicePolicy != null) {
                final OpenNetworkConfig openNetworkConfig = devicePolicy.getOpenNetworkConfig();
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
        DevicePolicy devicePolicy = new DevicePolicy();
        devicePolicy.setOpenNetworkConfig(openNetworkConfig);
        this.logger.log(Level.INFO, openNetworkConfig.toPrettyString());
        devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask("openNetworkConfig").execute();
        this.logger.log(Level.INFO, devicePolicy.toPrettyString());
    }
}
