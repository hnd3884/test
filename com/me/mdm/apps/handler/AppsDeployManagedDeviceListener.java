package com.me.mdm.apps.handler;

import java.util.List;
import java.util.logging.Level;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AppsDeployManagedDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public AppsDeployManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        try {
            AppsDeployManagedDeviceListener.mdmlogger.info("Entering AppsDeployManagedDeviceListener : DeviceManaged UDID:" + deviceEvent.udid + ", resourceID:" + deviceEvent.resourceID);
            final Boolean isEnabled = (Boolean)MDMDBUtil.getFirstRow("IOSAgentSettings", new Object[][] { { "CUSTOMER_ID", deviceEvent.customerID } }).get("IS_NATIVE_APP_ENABLE");
            final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacMDMAgent");
            if (isEnabled && isFeatureEnabled) {
                final List resourceList = new ArrayList();
                resourceList.add(deviceEvent.resourceID);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("IS_NATIVE_APP_ENABLE", (Object)Boolean.TRUE);
                jsonObject.put("RESOURCE_LIST", (Object)new JSONArray((Collection)resourceList));
                jsonObject.put("CUSTOMER_ID", (Object)deviceEvent.customerID);
                jsonObject.put("AGENT_TYPE", 2);
                AppsAutoDeployment.getInstance().handleNativeAgent(jsonObject);
            }
            else {
                AppsDeployManagedDeviceListener.mdmlogger.info("AppsDeployManagedDeviceListener : Not distributing macOS because: : feature:" + isFeatureEnabled + " ,agentSettingsEnabled:" + isEnabled);
            }
            AppsDeployManagedDeviceListener.mdmlogger.info("Exiting AppsDeployManagedDeviceListener : DeviceManaged UDID:" + deviceEvent.udid + ", resourceID:" + deviceEvent.resourceID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occured in distributing native agents to newly enrolled device", e);
        }
    }
}
