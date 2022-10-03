package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class SecurityDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String STORAGE_ENCRYPTION = "StorageEncryption";
    private static final String EXTERNAL_STORAGE_ENCRYPTION = "ExternalStorageEncryption";
    private static final String PASSCODE_COMPLIANT_WITH_PROFILE = "PasscodeCompliantWithProfiles";
    private static final String DEVICE_ROOTED = "DeviceRooted";
    private static final String PASSCODE_ENABLED = "PasscodePresent";
    private static final String EFRP_STATUS = "EFRPStatus";
    private static final String PLAY_PROTECT = "PlayProtect";
    public static final String IS_LOST_MODE_ENABLED = "IsLostModeEnabled";
    
    public SecurityDetailsMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> securityInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            securityInfo.put("StorageEncryption", inventoryData.optString("StorageEncryption", "false"));
            securityInfo.put("ExternalStorageEncryption", inventoryData.optString("ExternalStorageEncryption", "-1"));
            securityInfo.put("PasscodeCompliantWithProfiles", inventoryData.optString("PasscodeCompliantWithProfiles", "false"));
            securityInfo.put("DeviceRooted", inventoryData.optString("DeviceRooted", "false"));
            securityInfo.put("PasscodePresent", inventoryData.optString("PasscodePresent", "false"));
            securityInfo.put("EFRPStatus", inventoryData.optString("EFRPStatus", "3"));
            securityInfo.put("PlayProtect", inventoryData.optString("PlayProtect", "false"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateIOSSecurityInfo(inventoryObject.resourceId, securityInfo);
            if (inventoryData.has("IsLostModeEnabled")) {
                invDataPopulator.handleLostModeData(inventoryObject.resourceId, inventoryData.optBoolean("IsLostModeEnabled"));
            }
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
