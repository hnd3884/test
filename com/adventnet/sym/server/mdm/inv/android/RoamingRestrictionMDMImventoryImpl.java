package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class RoamingRestrictionMDMImventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String ALLOW_ROAMING_DATA = "allowRoamingData";
    private static final String ALLOW_ROAMING_PUSH = "allowRoamingPush";
    private static final String ALLOW_ROAMING_SYNC = "allowRoamingSync";
    private static final String ALLOW_ROAMING_VOICE_CALLS = "allowRoamingVoiceCall";
    
    public RoamingRestrictionMDMImventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("ALLOW_ROAMING_SYNC", inventoryData.optString("allowRoamingSync", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_VOICE_CALLS", inventoryData.optString("allowRoamingVoiceCall", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_PUSH", inventoryData.optString("allowRoamingPush", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_DATA", inventoryData.optString("allowRoamingData", "-1"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateAndroidSamsungRestriction(inventoryObject.resourceId, restrictionInfo, inventoryObject.scope);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating device details from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
