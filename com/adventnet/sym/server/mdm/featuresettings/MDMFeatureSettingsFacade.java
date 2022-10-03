package com.adventnet.sym.server.mdm.featuresettings;

import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.inventory.FeatureSettingConstants;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMFeatureSettingsFacade
{
    private static Logger logger;
    
    private MDMFeatureSettingsFacade() {
    }
    
    public static void configureSettings(final JSONObject apiJson) throws JSONException, APIHTTPException {
        final HashMap<String, Object> settingsMap = convertAPIJSONToHashMap(apiJson);
        MDMFeatureSettingsHandler.configureSettings(settingsMap);
    }
    
    private static HashMap<String, Object> convertAPIJSONToHashMap(final JSONObject apiJson) throws JSONException {
        MDMFeatureSettingsFacade.logger.log(Level.INFO, "Converting api json to hash map");
        final HashMap<String, Object> settingsMap = new HashMap<String, Object>();
        final Long customerID = apiJson.getLong("CUSTOMER_ID");
        final Long userID = apiJson.getLong("user_id");
        final boolean isEnabled = apiJson.getBoolean(FeatureSettingConstants.Api.Key.is_enabled);
        final Long featureType = apiJson.getLong(FeatureSettingConstants.Api.Key.feature_type);
        List<Long> groups = null;
        boolean isApplyToAll = false;
        if (isEnabled) {
            MDMFeatureSettingsFacade.logger.log(Level.INFO, "Request to enable feature, so retrieving necessary details");
            isApplyToAll = apiJson.getBoolean(FeatureSettingConstants.Api.Key.apply_to_all);
            MDMFeatureSettingsFacade.logger.log(Level.INFO, "is apply to all :", isApplyToAll);
            groups = JSONUtil.getInstance().convertLongJSONArrayTOList(apiJson.getJSONArray(FeatureSettingConstants.Api.Key.groups));
        }
        if (featureType == 1L && isEnabled) {
            MDMFeatureSettingsFacade.logger.log(Level.INFO, "Request to enable battery settings, so retrieving necessary battery details");
            final Long batteryTrackingInterval = apiJson.getLong(FeatureSettingConstants.Battery.battery_tracking_interval);
            final int history_deletion_interval = apiJson.optInt(FeatureSettingConstants.Battery.history_deletion_interval, 7);
            settingsMap.put(FeatureSettingConstants.Battery.history_deletion_interval, history_deletion_interval);
            settingsMap.put(FeatureSettingConstants.Battery.battery_tracking_interval, batteryTrackingInterval);
        }
        settingsMap.put("CUSTOMER_ID", customerID);
        settingsMap.put("user_id", userID);
        settingsMap.put(FeatureSettingConstants.Api.Key.is_enabled, isEnabled);
        settingsMap.put(FeatureSettingConstants.Api.Key.groups, groups);
        settingsMap.put(FeatureSettingConstants.Api.Key.feature_type, featureType);
        settingsMap.put(FeatureSettingConstants.Api.Key.apply_to_all, isApplyToAll);
        return settingsMap;
    }
    
    static {
        MDMFeatureSettingsFacade.logger = Logger.getLogger("InventoryLogger");
    }
}
