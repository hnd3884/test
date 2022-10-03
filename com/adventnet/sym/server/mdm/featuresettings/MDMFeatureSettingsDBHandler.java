package com.adventnet.sym.server.mdm.featuresettings;

import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.api.inventory.FeatureSettingConstants;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MDMFeatureSettingsDBHandler
{
    public static Logger logger;
    
    protected static DataObject getFeatureSettingsToResourceDO(final Long customerID, final Long featureType) throws Exception {
        final Criteria customerCriteria = getCustomerCriteria(customerID);
        final Criteria featureTypeCriteria = getFeatureTypeCriteria(featureType);
        final Criteria featureEnabledCriteria = getFeatureEnabledCriteria(true);
        final Criteria overAllCriteria = customerCriteria.and(featureTypeCriteria).and(featureEnabledCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMFeatureSettings"));
        final Join join = new Join("MDMFeatureSettings", "MDMFeatureSettingsToResourceCriteria", new String[] { "SETTINGS_ID" }, new String[] { "SETTINGS_ID" }, 1);
        selectQuery.addSelectColumn(new Column("MDMFeatureSettings", "*"));
        selectQuery.addSelectColumn(new Column("MDMFeatureSettingsToResourceCriteria", "*"));
        selectQuery.addJoin(join);
        selectQuery.setCriteria(overAllCriteria);
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    protected static DataObject getMdmFeatureSettingsDO(final Long customerID, final Long featureType) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMFeatureSettings"));
        selectQuery.addSelectColumn(new Column("MDMFeatureSettings", "*"));
        final Criteria customerCriteria = getCustomerCriteria(customerID);
        final Criteria featureTypeCriteria = getFeatureTypeCriteria(featureType);
        final Criteria overAllCriteria = customerCriteria.and(featureTypeCriteria);
        selectQuery.setCriteria(overAllCriteria);
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    private static void updateMdmFeatureSettingRow(final Row settingsRow, final HashMap<String, Object> settingsMap) {
        final Long customerID = settingsMap.get("CUSTOMER_ID");
        final Long userID = settingsMap.get("user_id");
        final Long featureType = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
        final boolean isEnableFeature = settingsMap.get(FeatureSettingConstants.Api.Key.is_enabled);
        settingsRow.set("FEATURE_TYPE", (Object)featureType);
        settingsRow.set("IS_FEATURE_ENABLED", (Object)isEnableFeature);
        settingsRow.set("CUSTOMER_ID", (Object)customerID);
        settingsRow.set("LAST_MODIFIED_BY", (Object)userID);
        settingsRow.set("UPDATED_TIME", (Object)System.currentTimeMillis());
    }
    
    public static void addOrUpdateFeatureSettings(final HashMap<String, Object> settingsMap) throws Exception {
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Adding/ Updating feature settings");
        final Long customerID = settingsMap.get("CUSTOMER_ID");
        final Long userID = settingsMap.get("user_id");
        final Long featureType = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
        final DataObject dataObject = getMdmFeatureSettingsDO(customerID, featureType);
        if (!dataObject.isEmpty()) {
            MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Updating feature settings");
            final Row settingsRow = dataObject.getFirstRow("MDMFeatureSettings");
            updateMdmFeatureSettingRow(settingsRow, settingsMap);
            dataObject.updateRow(settingsRow);
            MDMUtil.getPersistence().update(dataObject);
        }
        else {
            MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Adding feature settings");
            final Row settingsRow = new Row("MDMFeatureSettings");
            updateMdmFeatureSettingRow(settingsRow, settingsMap);
            settingsRow.set("CREATED_BY", (Object)userID);
            dataObject.addRow(settingsRow);
            MDMUtil.getPersistence().add(dataObject);
        }
        settingsMap.put("SETTINGS_ID", dataObject.getRow("MDMFeatureSettings").get("SETTINGS_ID"));
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Successfully updated feature settings");
    }
    
    public static void addOrUpdateFeatureForGroups(final HashMap<String, Object> settingsMap) throws Exception {
        final Long customerID = settingsMap.get("CUSTOMER_ID");
        final Long featureType = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Updating feature settings for related groups");
        try {
            MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Deleting groups related to this feature settings");
            MDMUtil.getPersistence().delete(new Criteria(new Column("MDMFeatureSettingsToResourceCriteria", "SETTINGS_ID"), settingsMap.get("SETTINGS_ID"), 0));
            final DataObject dataObject = getFeatureSettingsToResourceDO(customerID, featureType);
            if (!dataObject.isEmpty()) {
                final DataObject dataObject2 = MDMUtil.getPersistence().constructDataObject();
                final boolean applyToAll = settingsMap.get(FeatureSettingConstants.Api.Key.apply_to_all);
                final List<Long> groups = settingsMap.get(FeatureSettingConstants.Api.Key.groups);
                final Long currentTime = System.currentTimeMillis();
                for (final Long group : groups) {
                    final Row row = new Row("MDMFeatureSettingsToResourceCriteria");
                    row.set("SETTINGS_ID", settingsMap.get("SETTINGS_ID"));
                    row.set("RESOURCE_ID", (Object)group);
                    row.set("UPDATED_TIME", (Object)currentTime);
                    if (applyToAll) {
                        row.set("CRITERIA_TYPE", (Object)2);
                    }
                    else {
                        row.set("CRITERIA_TYPE", (Object)1);
                    }
                    dataObject2.addRow(row);
                }
                MDMUtil.getPersistence().add(dataObject2);
            }
            MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Succesfully added feature settings for groups");
        }
        catch (final Exception e) {
            MDMFeatureSettingsDBHandler.logger.log(Level.WARNING, "Exception while modifying feature settings for groups");
            throw e;
        }
    }
    
    public static boolean checkIfFeatureEnabledForDevice(final int featureType, final Long resourceID) throws Exception {
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Checking if feature enabled for device");
        final JSONObject featureDetails = getFeatureDetails(featureType, CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID));
        final boolean isFeatureEnabled = featureDetails.getBoolean(FeatureSettingConstants.Api.Key.is_enabled);
        boolean isFeatureEnabledForDevice = false;
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Is this feature overall enabled : {0}", new Object[] { isFeatureEnabled });
        if (isFeatureEnabled) {
            final boolean applyToAll = featureDetails.getBoolean(FeatureSettingConstants.Api.Key.apply_to_all);
            final JSONArray groupArr = featureDetails.getJSONArray(FeatureSettingConstants.Api.Key.groups);
            final Set<Long> devices = getDevicesFromGroupArr(groupArr);
            if ((!applyToAll && devices.contains(resourceID)) || (applyToAll && !devices.contains(resourceID))) {
                isFeatureEnabledForDevice = true;
            }
        }
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Feature enabled : {0}", new Object[] { isFeatureEnabledForDevice });
        return isFeatureEnabledForDevice;
    }
    
    public static Set<Long> getDevicesFromGroupArr(final JSONArray jsonArray) throws Exception {
        MDMFeatureSettingsDBHandler.logger.log(Level.INFO, "Getting devices present in groups");
        final Set<Long> devices = new HashSet<Long>();
        final List<Long> groups = new ArrayList<Long>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            groups.add(jsonArray.getLong(i));
        }
        final Criteria criteria = new Criteria(new Column("CustomGroup", "RESOURCE_ID"), (Object)groups.toArray(), 8);
        final DataObject memberIdDO = SyMUtil.getPersistence().get("CustomGroupMemberRel", criteria);
        if (!memberIdDO.isEmpty()) {
            final Iterator memberIdItr = memberIdDO.getRows("CustomGroupMemberRel");
            while (memberIdItr.hasNext()) {
                final Row memberIdRow = memberIdItr.next();
                final Long memberId = (Long)memberIdRow.get("MEMBER_RESOURCE_ID");
                devices.add(memberId);
            }
        }
        MDMFeatureSettingsDBHandler.logger.log(Level.FINE, "Devices present in groups : {0}", new Object[] { devices });
        return devices;
    }
    
    public static JSONObject getFeatureDetails(final int featureType, final Long customerID) throws Exception {
        final JSONObject featureDetails = new JSONObject();
        final JSONArray groupsInfo = new JSONArray();
        final JSONArray groups = new JSONArray();
        final JSONObject featureResponse = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMFeatureSettings"));
        final Join join = new Join("MDMFeatureSettings", "MDMFeatureSettingsToResourceCriteria", new String[] { "SETTINGS_ID" }, new String[] { "SETTINGS_ID" }, 1);
        final Criteria featureEnabledCriteria = getFeatureEnabledCriteria(true);
        final Criteria customerCriteria = getCustomerCriteria(customerID);
        final Criteria featureTypeCriteria = getFeatureTypeCriteria(featureType);
        final Criteria overAllCriteria = customerCriteria.and(featureTypeCriteria).and(featureEnabledCriteria);
        selectQuery.addJoin(join);
        selectQuery.setCriteria(overAllCriteria);
        selectQuery.addSelectColumn(new Column("MDMFeatureSettings", "*"));
        selectQuery.addSelectColumn(new Column("MDMFeatureSettingsToResourceCriteria", "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            featureDetails.put(FeatureSettingConstants.Api.Key.is_enabled, true);
            final Iterator iterator = dataObject.getRows("MDMFeatureSettingsToResourceCriteria");
            if (iterator != null && iterator.hasNext()) {
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final int criteriaType = (int)row.get("CRITERIA_TYPE");
                    featureDetails.put(FeatureSettingConstants.Api.Key.apply_to_all, criteriaType != 1);
                    final Long groupID = (Long)row.get("RESOURCE_ID");
                    final HashMap hs = MDMGroupHandler.getInstance().getGroupDetails(groupID);
                    final JSONObject groupInfo = new JSONObject();
                    groupInfo.put(FeatureSettingConstants.Api.Key.group_id, hs.get("RESOURCE_ID"));
                    groupInfo.put(FeatureSettingConstants.Api.Key.name, hs.get("NAME"));
                    groupsInfo.put((Object)groupInfo);
                    groups.put((Object)groupID);
                }
            }
            else {
                featureDetails.put(FeatureSettingConstants.Api.Key.apply_to_all, true);
            }
            if (featureType == 1) {
                final JSONObject batterySettingsJson = MDMBatterySettingsDBHandler.getBatterySettingsJson(customerID);
                final long tracking_interval = batterySettingsJson.getLong("TRACKING_INTERVAL");
                featureResponse.put(FeatureSettingConstants.Battery.battery_tracking_interval, tracking_interval);
                featureDetails.put(FeatureSettingConstants.Api.Key.feature_response, (Object)featureResponse);
            }
        }
        else {
            featureDetails.put(FeatureSettingConstants.Api.Key.is_enabled, false);
        }
        featureDetails.put(FeatureSettingConstants.Api.Key.feature_type, featureType);
        featureDetails.put(FeatureSettingConstants.Api.Key.groups_info, (Object)groupsInfo);
        featureDetails.put(FeatureSettingConstants.Api.Key.groups, (Object)groups);
        return featureDetails;
    }
    
    public static Criteria getFeatureTypeCriteria(final long featureType) {
        return new Criteria(new Column("MDMFeatureSettings", "FEATURE_TYPE"), (Object)featureType, 0);
    }
    
    public static Criteria getCustomerCriteria(final Long customerID) {
        return new Criteria(new Column("MDMFeatureSettings", "CUSTOMER_ID"), (Object)customerID, 0);
    }
    
    public static Criteria getFeatureEnabledCriteria(final boolean isEnabled) {
        return new Criteria(new Column("MDMFeatureSettings", "IS_FEATURE_ENABLED"), (Object)isEnabled, 0);
    }
    
    static {
        MDMFeatureSettingsDBHandler.logger = Logger.getLogger("InventoryLogger");
    }
}
