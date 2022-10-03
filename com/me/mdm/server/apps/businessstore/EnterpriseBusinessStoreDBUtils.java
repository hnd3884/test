package com.me.mdm.server.apps.businessstore;

import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import org.json.JSONObject;

public class EnterpriseBusinessStoreDBUtils
{
    public static EnterpriseBusinessStoreDBUtils enterpriseBusinessStoreDBUtils;
    
    public static EnterpriseBusinessStoreDBUtils getInstance() {
        if (EnterpriseBusinessStoreDBUtils.enterpriseBusinessStoreDBUtils == null) {
            EnterpriseBusinessStoreDBUtils.enterpriseBusinessStoreDBUtils = new EnterpriseBusinessStoreDBUtils();
        }
        return EnterpriseBusinessStoreDBUtils.enterpriseBusinessStoreDBUtils;
    }
    
    public JSONObject getBusinessStoreDetails(final int platformType, final Long businessStoreID, final Long customerID, final Long userID) throws Exception {
        JSONObject response = null;
        if (platformType == 3) {
            response = MDBusinessStoreUtil.getBusinessStoreDetails(customerID, ManagedBusinessStoreHandler.BS_SERVICE_WBS);
            WpAppSettingsHandler.getInstance().putStoreDetails(response, customerID, userID);
        }
        else if (platformType == 2) {
            response = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        }
        else if (platformType == 1) {
            response = new VPPTokenDataHandler().getVppTokenDetails(businessStoreID);
        }
        return response;
    }
    
    static {
        EnterpriseBusinessStoreDBUtils.enterpriseBusinessStoreDBUtils = null;
    }
}
