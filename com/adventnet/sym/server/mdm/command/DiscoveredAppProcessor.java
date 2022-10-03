package com.adventnet.sym.server.mdm.command;

import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class DiscoveredAppProcessor extends DCQueueDataProcessor
{
    public static Logger logger;
    
    public void processData(final DCQueueData qData) {
        String appName = null;
        try {
            final Long startTime = System.currentTimeMillis();
            final String strData = (String)qData.queueData;
            final JSONObject jsonObject = new JSONObject(strData);
            final Long customerID = JSONUtil.optLongForUVH(jsonObject, "CUSTOMER_ID", Long.valueOf(-1L));
            final Long resourceID = JSONUtil.optLongForUVH(jsonObject, "RESOURCE_ID", Long.valueOf(-1L));
            final Integer platformType = jsonObject.getInt("PLATFORM_TYPE");
            final Integer scope = jsonObject.getInt("SCOPE");
            final AppDataHandler handler = new AppDataHandler();
            appName = jsonObject.getString("APP_NAME");
            final Properties appPropery = this.getAppPropertyAsProperties(jsonObject);
            handler.addNewlyDiscoveredApp(appPropery, resourceID, customerID, platformType, scope);
            DiscoveredAppProcessor.logger.log(Level.INFO, "Newly discovered app processed in {0}", System.currentTimeMillis() - startTime);
        }
        catch (final Exception e) {
            DiscoveredAppProcessor.logger.log(Level.SEVERE, "Failed to process newly discovered app: " + appName, e);
        }
    }
    
    private Properties getAppPropertyAsProperties(final JSONObject jsonObject) throws Exception {
        final Properties appRepMap = new Properties();
        ((Hashtable<String, String>)appRepMap).put("APP_NAME", jsonObject.getString("APP_NAME"));
        ((Hashtable<String, String>)appRepMap).put("APP_VERSION", jsonObject.getString("APP_VERSION"));
        ((Hashtable<String, String>)appRepMap).put("APP_NAME_SHORT_VERSION", jsonObject.getString("APP_NAME_SHORT_VERSION"));
        ((Hashtable<String, String>)appRepMap).put("IDENTIFIER", jsonObject.getString("IDENTIFIER"));
        ((Hashtable<String, Long>)appRepMap).put("BUNDLE_SIZE", jsonObject.getLong("BUNDLE_SIZE"));
        ((Hashtable<String, Long>)appRepMap).put("RESOURCE_ID", jsonObject.getLong("RESOURCE_ID"));
        ((Hashtable<String, Long>)appRepMap).put("DYNAMIC_SIZE", jsonObject.getLong("DYNAMIC_SIZE"));
        ((Hashtable<String, Long>)appRepMap).put("CUSTOMER_ID", jsonObject.getLong("CUSTOMER_ID"));
        ((Hashtable<String, Integer>)appRepMap).put("APP_TYPE", jsonObject.getInt("APP_TYPE"));
        ((Hashtable<String, Boolean>)appRepMap).put("NOTIFY_ADMIN", jsonObject.getBoolean("NOTIFY_ADMIN"));
        ((Hashtable<String, Integer>)appRepMap).put("PLATFORM_TYPE", jsonObject.getInt("PLATFORM_TYPE"));
        ((Hashtable<String, Integer>)appRepMap).put("SCOPE", jsonObject.getInt("SCOPE"));
        ((Hashtable<String, Long>)appRepMap).put("EXTERNAL_APP_VERSION_ID", jsonObject.getLong("EXTERNAL_APP_VERSION_ID"));
        ((Hashtable<String, Boolean>)appRepMap).put("HAS_UPDATE_AVAILABLE", jsonObject.getBoolean("HAS_UPDATE_AVAILABLE"));
        if (jsonObject.has("USER_INSTALLED_APPS")) {
            ((Hashtable<String, Integer>)appRepMap).put("USER_INSTALLED_APPS", jsonObject.getInt("USER_INSTALLED_APPS"));
        }
        if (jsonObject.has("IS_MODERN_APP")) {
            ((Hashtable<String, Boolean>)appRepMap).put("IS_MODERN_APP", jsonObject.getBoolean("IS_MODERN_APP"));
        }
        if (jsonObject.has("PACKAGE_TYPE")) {
            ((Hashtable<String, Integer>)appRepMap).put("PACKAGE_TYPE", jsonObject.getInt("PACKAGE_TYPE"));
        }
        return appRepMap;
    }
    
    static {
        DiscoveredAppProcessor.logger = Logger.getLogger("MDMConfigLogger");
    }
}
