package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class BrowserRestrictionMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String BROWSER_ALLOW_AUTOFILL = "browserAllowAutoFill";
    private static final String BROWSER_ALLOW_COOKIES = "browserAllowCookies";
    private static final String BROWSER_ALLOW_JAVASCRIPT = "browserAllowJavaScript";
    private static final String BROWSER_ALLOW_POPUPS = "browserAllowPopups";
    private static final String BROWSER_ALLOW_FRAUD_WARNING = "browserAllowFraudWarning";
    private static final String ALLOW_ANDROID_BROWSER = "allowAndroidBrowser";
    
    public BrowserRestrictionMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject browserRestriciton = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("BROWSER_ALLOW_FRAUD_WARNING", browserRestriciton.optString("browserAllowFraudWarning", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_POPUPS", browserRestriciton.optString("browserAllowPopups", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_JAVASCRIPT", browserRestriciton.optString("browserAllowJavaScript", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_AUTOFILL", browserRestriciton.optString("browserAllowAutoFill", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_COOKIES", browserRestriciton.optString("browserAllowCookies", "-1"));
            restrictionInfo.put("ALLOW_ANDROID_BROWSER", browserRestriciton.optString("allowAndroidBrowser", "-1"));
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
