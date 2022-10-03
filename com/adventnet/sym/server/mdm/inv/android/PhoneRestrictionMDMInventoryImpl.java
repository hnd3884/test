package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class PhoneRestrictionMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String ALLOW_MMS = "allowMms";
    private static final String ALLOW_INCOMING_MMS = "allowIncomingMms";
    private static final String ALLOW_OUTGOING_MMS = "allowOutgoingMms";
    private static final String ALLOW_SMS = "allowSms";
    private static final String ALLOW_INCOMING_SMS = "allowIncomingSms";
    private static final String ALLOW_OUTGOING_SMS = "allowOutgoingSms";
    private static final String ALLOW_CALL = "allowCall";
    private static final String ALLOW_INCOMING_CALL = "allowIncomingCall";
    private static final String ALLOW_OUTGOING_CALL = "allowOutgoingCall";
    
    public PhoneRestrictionMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject phoneRestriction = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("ALLOW_MMS", phoneRestriction.optString("allowMms", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_MMS", phoneRestriction.optString("allowIncomingMms", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_MMS", phoneRestriction.optString("allowOutgoingMms", "-1"));
            restrictionInfo.put("ALLOW_SMS", phoneRestriction.optString("allowSms", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_SMS", phoneRestriction.optString("allowIncomingSms", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_SMS", phoneRestriction.optString("allowOutgoingSms", "-1"));
            restrictionInfo.put("ALLOW_CALL", phoneRestriction.optString("allowCall", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_CALL", phoneRestriction.optString("allowOutgoingCall", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_CALL", phoneRestriction.optString("allowIncomingCall", "-1"));
            restrictionInfo.put("ALLOW_SMS", phoneRestriction.optString("allowSms", "-1"));
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
