package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class ApplicationRestrictionMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String ALLOW_ANDROID_MARKET = "allowAndroidMarket";
    private static final String ALLOW_NON_MARKET_APPS = "allowNonMarketApps";
    private static final String ALLOW_VOICE_DIALER = "allowVoiceDialer";
    private static final String ALLOW_YOU_TUBE = "allowYouTube";
    private static final String ALLOW_INSTALL_APP = "allowInstallApp";
    private static final String ALLOW_UNINSTALL_APP = "allowUnInstallApp";
    private static final String ALLOW_S_VOICE = "allowSVoice";
    private static final String ALLOW_APP_NOTIFICATION_MODE = "setApplicationNotificationMode";
    private static final String ALLOW_STOP_SYSTEM_APP = "allowStopSystemApp";
    private static final String ALLOW_GOOGLE_MAPS = "allowGoogleMaps";
    private static final String ALLOW_GMAIL = "allowGoogleMail";
    private static final String ALLOW_S_FINDER = "allowSFinder";
    private static final String GLOBAL_APP_RESTRICTION = "setGlobalPermissionPolicyState";
    
    public ApplicationRestrictionMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject applictionRestriction = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("ALLOW_ANDROID_MARKET", applictionRestriction.optString("allowAndroidMarket", "-1"));
            restrictionInfo.put("ALLOW_NON_MARKET_APPS", applictionRestriction.optString("allowNonMarketApps", "-1"));
            restrictionInfo.put("ALLOW_YOU_TUBE", applictionRestriction.optString("allowYouTube", "-1"));
            restrictionInfo.put("ALLOW_VOICE_DIALER", applictionRestriction.optString("allowVoiceDialer", "-1"));
            restrictionInfo.put("ALLOW_INSTALL_APP", applictionRestriction.optString("allowInstallApp", "-1"));
            restrictionInfo.put("ALLOW_UNINSTALL_APP", applictionRestriction.optString("allowUnInstallApp", "-1"));
            restrictionInfo.put("ALLOW_S_VOICE", applictionRestriction.optString("allowSVoice", "-1"));
            restrictionInfo.put("ALLOW_APP_NOTIFICATION_MODE", applictionRestriction.optString("setApplicationNotificationMode", "-1"));
            restrictionInfo.put("ALLOW_STOP_SYSTEM_APP", applictionRestriction.optString("allowStopSystemApp", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_MAPS", applictionRestriction.optString("allowGoogleMaps", "-1"));
            restrictionInfo.put("ALLOW_GMAIL", applictionRestriction.optString("allowGoogleMail", "-1"));
            restrictionInfo.put("ALLOW_S_FINDER", applictionRestriction.optString("allowSFinder", "-1"));
            restrictionInfo.put("APP_PERMISSION_POLICY", applictionRestriction.optString("setGlobalPermissionPolicyState", "-1"));
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
