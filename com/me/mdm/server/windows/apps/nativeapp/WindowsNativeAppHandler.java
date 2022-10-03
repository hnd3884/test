package com.me.mdm.server.windows.apps.nativeapp;

import java.util.Hashtable;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import java.util.Properties;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Logger;

public class WindowsNativeAppHandler
{
    public static final String APP_CONFIG_DATA = "AppConfigData";
    public static final String ENROLLMENT_REQUEST_ID = "EnrollmentRequestID";
    public static final String SERVER_URL = "ServerURL";
    public static final String UDID = "UDID";
    public static final String MANAGED_DEVICE_ID = "ManagedDeviceID";
    public static final String EMAIL_ADDRESS = "EmailAddress";
    public static final String SERVICES = "Services";
    public static final String TOKEN_NAME = "TokenName";
    public static final String ENCAPIKEY = "encapiKey";
    public static final String TOKEN_VALUE = "TokenValue";
    public Logger logger;
    private static WindowsNativeAppHandler settingsHandler;
    
    public WindowsNativeAppHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public static WindowsNativeAppHandler getInstance() {
        if (WindowsNativeAppHandler.settingsHandler == null) {
            WindowsNativeAppHandler.settingsHandler = new WindowsNativeAppHandler();
        }
        return WindowsNativeAppHandler.settingsHandler;
    }
    
    public boolean isWindowsNativeAgentEnable(final Long customerId) {
        boolean isNativeAgentEnable = true;
        try {
            final Row windowsSettingsRow = DBUtil.getRowFromDB("WPClientSettings", "CUSTOMER_ID", (Object)customerId);
            if (windowsSettingsRow != null) {
                isNativeAgentEnable = (boolean)windowsSettingsRow.get("IS_NATIVE_APP_ENABLE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in isIOSNativeAgentEnable ", ex);
        }
        return isNativeAgentEnable;
    }
    
    public Properties getWindowsNativeAppProfileDetails(final Long customerId, final List resourceList) throws JSONException, DataAccessException {
        final Long appId = WpAppSettingsHandler.getInstance().getWindowsUWPAgentAppID(customerId);
        final HashMap profileCollectionMap = MDMUtil.getInstance().getProfiletoCollectionMap(appId);
        this.logger.log(Level.INFO, "distributeAndInstallIOSNativeAgent: Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { profileCollectionMap, resourceList });
        final Properties properties = new Properties();
        final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileCollectionMap.keySet().iterator().next());
        ((Hashtable<String, HashMap>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, String>)properties).put("commandName", "InstallApplication");
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, List>)properties).put("resourceList", resourceList);
        ((Hashtable<String, Integer>)properties).put("platformtype", 3);
        ((Hashtable<String, Long>)properties).put("customerId", customerId);
        ((Hashtable<String, Object>)properties).put("loggedOnUserName", createdUserDetailsJSON.get("FIRST_NAME"));
        ((Hashtable<String, Object>)properties).put("loggedOnUser", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Object>)properties).put("UserId", createdUserDetailsJSON.get("USER_ID"));
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", true);
        ((Hashtable<String, Boolean>)properties).put("isNotify", false);
        this.logger.log(Level.INFO, "getWindowsNativeAppProfileDetails :: Going to assign app for devices: collectionList: {0} resourceList: {1}", new Object[] { ((Hashtable<K, Object>)properties).get("profileCollectionList"), resourceList });
        return properties;
    }
    
    public JSONArray getNativeAppConfigurationJSON() throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final JSONObject finalJSON = new JSONObject();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("EnrollmentRequestID", (Object)"%erid%");
        jsonObject.put("ServerURL", (Object)"https://%ServerName%:%ServerPort%");
        jsonObject.put("UDID", (Object)"%udid%");
        jsonObject.put("ManagedDeviceID", (Object)"%resourceid%");
        final JSONObject services = new JSONObject();
        services.put("TokenName", (Object)"encapiKey");
        services.put("TokenValue", (Object)"%authtoken%");
        jsonObject.put("Services", (Object)services);
        jsonObject.put("ManagedDeviceID", (Object)"%resourceid%");
        finalJSON.put("key", (Object)"AppConfigData");
        finalJSON.put("value", (Object)jsonObject.toString());
        jsonArray.put((Object)finalJSON);
        return jsonArray;
    }
    
    static {
        WindowsNativeAppHandler.settingsHandler = null;
    }
}
