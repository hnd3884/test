package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class OsUpdateDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String SECURITY_PATCH_LEVEL = "SecurityPatchLevel";
    
    public OsUpdateDetailsMDMInventoryImpl() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            MDMInvDataPopulator.getInstance().updateDeviceInfo(inventoryObject.resourceId, "SECURITY_PATCH_VERSION", inventoryData.optString("SecurityPatchLevel", "--"));
            isDataPopulationSuccess = true;
        }
        catch (final Exception e) {
            DMSecurityLogger.info(this.logger, "OsUpdateDetailsMDMInventoryImpl", "populateInventoryData", "Exception occurred on populating device details from response data {0}", (Object)inventoryObject.strData);
            this.logger.log(Level.INFO, "Exception in populateInventoryData", e);
        }
        return isDataPopulationSuccess;
    }
}
