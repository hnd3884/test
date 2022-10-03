package com.adventnet.sym.server.mdm.inv.android;

import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class SoftwareDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    
    public SoftwareDetailsMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject wrappedJSON = new JSONObject(inventoryObject.strData);
            final JSONArray appListArray = wrappedJSON.getJSONArray("AppList");
            final AppDataHandler appHandler = new AppDataHandler();
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(inventoryObject.resourceId);
            Integer scope = inventoryObject.scope;
            if (ManagedDeviceHandler.getInstance().isProfileOwner(inventoryObject.resourceId)) {
                scope = 1;
            }
            appHandler.processAndroidSoftwares(inventoryObject.resourceId, customerId, appListArray, scope, 1);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
