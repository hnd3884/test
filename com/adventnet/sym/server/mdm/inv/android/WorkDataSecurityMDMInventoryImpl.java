package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class WorkDataSecurityMDMInventoryImpl implements MDMInventory
{
    private final Logger logger;
    private static final String ALLOW_SHARE_DOC_TO_PERSONAL_APPS = "ShareDocsToPersonalApps";
    private static final String ALLOW_SHARE_DOC_TO_WORK_PROFILE = "ShareDocsToWorkProfile";
    private static final String ALLOW_PROFILE_CONTENT_TO_OTHER_APPS = "AllowProfileContentsToOtherApps";
    private static final String ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH = "ShareWorkProfileContactOverBluetooth";
    private static final String ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN = "AllowWorkProfileAppWidgetToHomeScreen";
    private static final String ALLOW_CONTACT_IN_PERSONAL_PROFILE = "AllowWorkContactDetailsInPersonalProfile";
    private static final String ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS = "AllowWorkContactAccessToPersonalApps";
    
    public WorkDataSecurityMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final JSONObject workDataSecurityInfoJson = new JSONObject();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject workDataSecurityObject = new JSONObject(inventoryObject.strData);
            workDataSecurityInfoJson.put("ALLOW_SHARE_DOC_TO_WORK_PROFILE", workDataSecurityObject.optInt("ShareDocsToWorkProfile", 0) == 1);
            workDataSecurityInfoJson.put("ALLOW_SHARE_DOC_TO_PERSONAL_APPS", workDataSecurityObject.optInt("ShareDocsToPersonalApps", 0) == 1);
            workDataSecurityInfoJson.put("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS", workDataSecurityObject.optInt("AllowProfileContentsToOtherApps", 0) == 1);
            workDataSecurityInfoJson.put("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH", workDataSecurityObject.optInt("ShareWorkProfileContactOverBluetooth", 0) == 1);
            workDataSecurityInfoJson.put("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN", workDataSecurityObject.optInt("AllowWorkProfileAppWidgetToHomeScreen", 0) == 1);
            workDataSecurityInfoJson.put("ALLOW_CONTACT_IN_PERSONAL_PROFILE", workDataSecurityObject.optBoolean("AllowWorkContactDetailsInPersonalProfile", false));
            workDataSecurityInfoJson.put("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS", workDataSecurityObject.optInt("AllowWorkContactAccessToPersonalApps", 0) == 1);
            final MDMInvDataPopulator mdmInvDataPopulator = MDMInvDataPopulator.getInstance();
            mdmInvDataPopulator.addOrUpdateWorkDataSecurityInfo(inventoryObject.resourceId, workDataSecurityInfoJson);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating work data security details from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
