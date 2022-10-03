package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class NetworkUsageDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String INCOMING_NETWORK_USAGE = "IncomingNetworkUsage";
    private static final String OUT_GOING_NETWORK_USAGE = "OutGoingNetworkUsage";
    private static final String INCOMING_WIFI_USAGE = "IncomingWiFiUsage";
    private static final String OUT_GOING_WIFI_USAGE = "OutGoingWiFiUsage";
    
    public NetworkUsageDetailsMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> networkUsageInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            networkUsageInfo.put("RESOURCE_ID", inventoryObject.resourceId + "");
            networkUsageInfo.put("INCOMING_NETWORK_USAGE", inventoryData.optString("IncomingNetworkUsage", "0.0"));
            networkUsageInfo.put("OUTGOING_NETWORK_USAGE", inventoryData.optString("OutGoingNetworkUsage", "0.0"));
            networkUsageInfo.put("INCOMING_WIFI_USAGE", inventoryData.optString("IncomingWiFiUsage", "0.0"));
            networkUsageInfo.put("OUTGOING_WIFI_USAGE", inventoryData.optString("OutGoingWiFiUsage", "0.0"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateNetworkUsageInfo(inventoryObject.resourceId, networkUsageInfo);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
