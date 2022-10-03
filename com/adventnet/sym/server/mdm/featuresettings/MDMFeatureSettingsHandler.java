package com.adventnet.sym.server.mdm.featuresettings;

import org.json.JSONArray;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.inventory.FeatureSettingConstants;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMFeatureSettingsHandler
{
    public static Logger logger;
    
    public static JSONObject getSettings(final JSONObject json) {
        try {
            final Long customerID = json.getLong("CUSTOMER_ID");
            final int featureType = json.getInt(FeatureSettingConstants.Api.Key.feature_type);
            final JSONObject featureDetails = MDMFeatureSettingsDBHandler.getFeatureDetails(featureType, customerID);
            return featureDetails;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static void configureSettings(final HashMap<String, Object> settingsMap) {
        try {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Going to add or modify feature settings");
            MDMFeatureSettingsHandler.logger.log(Level.FINE, "Already applied groups -> {0}", new Object[] { settingsMap });
            final Long customerID = settingsMap.get("CUSTOMER_ID");
            final Long featureType = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
            final boolean isEnabled = settingsMap.get(FeatureSettingConstants.Api.Key.is_enabled);
            List<Long> groupsSelectedFromUI = new ArrayList<Long>();
            if (isEnabled) {
                groupsSelectedFromUI = settingsMap.get(FeatureSettingConstants.Api.Key.groups);
            }
            final HashSet<Long> groupsToBeNotified = new HashSet<Long>(groupsSelectedFromUI);
            boolean is_previously_enabled_for_all = false;
            final DataObject settingsDO = MDMFeatureSettingsDBHandler.getFeatureSettingsToResourceDO(customerID, featureType);
            if (!settingsDO.isEmpty()) {
                MDMFeatureSettingsHandler.logger.log(Level.INFO, "Feature previously enabled. Checking whether it is previously enabled for all or for certain groups");
                is_previously_enabled_for_all = isFeatureEnabledPreviouslyForAllDevices(settingsDO);
                MDMFeatureSettingsHandler.logger.log(Level.INFO, "Feature previously enabled for all? : ", is_previously_enabled_for_all);
                if (!is_previously_enabled_for_all) {
                    MDMFeatureSettingsHandler.logger.log(Level.INFO, "Not enabled for all, so getting the groups previously involved");
                    final HashSet<Long> previously_involved_groups = getGroupsInvolvedForFeature(settingsDO);
                    groupsToBeNotified.addAll((Collection<?>)previously_involved_groups);
                }
            }
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Number of groups to notify: ", groupsToBeNotified.size());
            settingsMap.put(FeatureSettingConstants.Api.Key.groups_to_be_notified, groupsToBeNotified);
            settingsMap.put(FeatureSettingConstants.Api.Key.is_previously_enabled_for_all, is_previously_enabled_for_all);
            MDMFeatureSettingsDBHandler.addOrUpdateFeatureSettings(settingsMap);
            MDMFeatureSettingsDBHandler.addOrUpdateFeatureForGroups(settingsMap);
            addOrUpdateRelatedSettings(settingsMap);
            addFeatureSettingCommandForDevices(settingsMap);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private static boolean isFeatureEnabledPreviouslyForAllDevices(final DataObject settingsDO) {
        boolean isPreviouslyEnabledForAll = true;
        try {
            if (settingsDO.getRow("MDMFeatureSettingsToResourceCriteria") != null) {
                isPreviouslyEnabledForAll = false;
            }
        }
        catch (final Exception e) {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Exception while getting rows from MDMFEATURESETTINGSTORESOURCECRITERIA table");
        }
        return isPreviouslyEnabledForAll;
    }
    
    private static HashSet<Long> getGroupsInvolvedForFeature(final DataObject settingsDO) {
        final HashSet<Long> previously_involved_groups = new HashSet<Long>();
        try {
            final Iterator rowIterator = settingsDO.getRows("MDMFeatureSettingsToResourceCriteria");
            while (rowIterator.hasNext()) {
                final Row groupRow = rowIterator.next();
                final Long group = (Long)groupRow.get("RESOURCE_ID");
                previously_involved_groups.add(group);
            }
        }
        catch (final Exception e) {
            MDMFeatureSettingsHandler.logger.log(Level.WARNING, "Exception while retrieving previously involved groups: ", e);
        }
        return previously_involved_groups;
    }
    
    private static void addOrUpdateRelatedSettings(final HashMap<String, Object> settingsMap) {
        MDMFeatureSettingsHandler.logger.log(Level.INFO, "Adding/ updating related settings");
        final Long feature_type = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
        try {
            if (feature_type == 1L) {
                MDMBatterySettingsDBHandler.addOrUpdateBatterySettings(settingsMap);
            }
            updateAuditLogsForSettingsChange(settingsMap);
        }
        catch (final Exception e) {
            MDMFeatureSettingsHandler.logger.log(Level.WARNING, "Exception while updating related settings ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        MDMFeatureSettingsHandler.logger.log(Level.INFO, "Successfully updated related settings");
    }
    
    private static void updateAuditLogsForSettingsChange(final HashMap<String, Object> settingsMap) {
        final Long userId = settingsMap.get("user_id");
        final boolean isEnabled = settingsMap.get(FeatureSettingConstants.Api.Key.is_enabled);
        final Long customerID = settingsMap.get("CUSTOMER_ID");
        final Long featureType = settingsMap.get(FeatureSettingConstants.Api.Key.feature_type);
        String eventRemark = null;
        String userName = null;
        try {
            if (userId != null) {
                userName = DMUserHandler.getUserNameFromUserID(userId);
            }
            if (featureType == 1L) {
                eventRemark = getAuditLogRemarkForBattery(isEnabled);
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2162, null, userName, eventRemark, "", customerID);
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Audit logs added");
        }
        catch (final Exception e) {
            MDMFeatureSettingsHandler.logger.log(Level.WARNING, "Exception while adding audit logs ", e);
        }
    }
    
    private static String getAuditLogRemarkForBattery(final boolean isEnabled) {
        String eventRemark;
        if (isEnabled) {
            eventRemark = "mdm.batterysetting.enabled";
        }
        else {
            eventRemark = "mdm.batterysetting.disabled";
        }
        return eventRemark;
    }
    
    public static void addFeatureSettingCommandForDevices(final HashMap<String, Object> settingsMap) throws Exception {
        MDMFeatureSettingsHandler.logger.log(Level.INFO, "Going to send feature settings commands to devices");
        final boolean isEnabled = settingsMap.get(FeatureSettingConstants.Api.Key.is_enabled);
        final boolean is_previously_applied_for_all = settingsMap.get(FeatureSettingConstants.Api.Key.is_previously_enabled_for_all);
        boolean is_apply_to_all = false;
        if (isEnabled) {
            is_apply_to_all = settingsMap.get(FeatureSettingConstants.Api.Key.apply_to_all);
        }
        final HashSet<Long> devices_to_be_notified = new HashSet<Long>();
        if (is_apply_to_all || is_previously_applied_for_all) {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "All devices involved. So sending commands to all available devices.");
            MDMFeatureSettingsHandler.logger.log(Level.FINE, "is previously applied for all {0}", new Object[] { is_previously_applied_for_all });
            final ArrayList<Long> managedDevices = ManagedDeviceHandler.getInstance().getManagedDeviceResourceIDs();
            devices_to_be_notified.addAll((Collection<?>)managedDevices);
        }
        else {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Not all the devices are involved. Getting devices from involved groups");
            final HashSet<Long> groups_involved = settingsMap.get(FeatureSettingConstants.Api.Key.groups_to_be_notified);
            final Criteria criteria = new Criteria(new Column("CustomGroup", "RESOURCE_ID"), (Object)groups_involved.toArray(), 8);
            final DataObject groupsToDevicesDO = SyMUtil.getPersistence().get("CustomGroupMemberRel", criteria);
            if (!groupsToDevicesDO.isEmpty()) {
                final Iterator devicesRowsIterator = groupsToDevicesDO.getRows("CustomGroupMemberRel");
                while (devicesRowsIterator.hasNext()) {
                    final Row resourceIdRow = devicesRowsIterator.next();
                    final Long resourceId = (Long)resourceIdRow.get("MEMBER_RESOURCE_ID");
                    devices_to_be_notified.add(resourceId);
                }
            }
            else {
                MDMFeatureSettingsHandler.logger.log(Level.INFO, "No devices found in the given groups");
            }
        }
        if (!devices_to_be_notified.isEmpty()) {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "Going to notify the devices");
            final DataObject devicesDO = getMdDeviceDo(devices_to_be_notified);
            addFeatureSettingsCommandsToCommandRepo(devicesDO);
        }
        else {
            MDMFeatureSettingsHandler.logger.log(Level.INFO, "No devices to notify.");
        }
    }
    
    public static DataObject getMdDeviceDo(final HashSet<Long> devices) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Criteria resourceCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)devices.toArray(), 8);
        final Criteria managedStatusCriteira = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria overAllCriteria = resourceCriteria.and(managedStatusCriteira);
        selectQuery.addSelectColumn(new Column("ManagedDevice", "*"));
        selectQuery.setCriteria(overAllCriteria);
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public static void addFeatureSettingsCommandsToCommandRepo(final DataObject dataObject) throws Exception {
        MDMFeatureSettingsHandler.logger.log(Level.INFO, "Adding commands...");
        final HashSet<Long> iosDevices = new HashSet<Long>();
        final HashSet<Long> androidDevices = new HashSet<Long>();
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("ManagedDevice");
            while (iterator.hasNext()) {
                final Row settingsRow = iterator.next();
                final Long deviceId = (Long)settingsRow.get("RESOURCE_ID");
                final int platformType = (int)settingsRow.get("PLATFORM_TYPE");
                final long agent_version_code = (long)settingsRow.get("AGENT_VERSION_CODE");
                if (platformType == 1) {
                    iosDevices.add(deviceId);
                }
                else {
                    if (platformType != 2 || agent_version_code % 10000L < 514L) {
                        continue;
                    }
                    androidDevices.add(deviceId);
                }
            }
            if (!iosDevices.isEmpty()) {
                final ArrayList<Long> iosDevicesList = new ArrayList<Long>(iosDevices);
                DeviceCommandRepository.getInstance().addBatteryConfigurationCommand(iosDevicesList, 2);
                MDMFeatureSettingsHandler.logger.log(Level.INFO, "ios commands successfully added...");
            }
            if (!androidDevices.isEmpty()) {
                final ArrayList<Long> androidDevicesList = new ArrayList<Long>(androidDevices);
                DeviceCommandRepository.getInstance().addBatteryConfigurationCommand(androidDevicesList, 1);
                MDMFeatureSettingsHandler.logger.log(Level.INFO, "android commands successfully added...");
                NotificationHandler.getInstance().SendNotification(androidDevicesList, 2);
                MDMFeatureSettingsHandler.logger.log(Level.FINE, "Notifications sent to android devices...");
            }
        }
    }
    
    public static boolean isGroupInvolved(final Long customerID, final Long group, final int featureType) throws Exception {
        final JSONObject jsonObject = MDMFeatureSettingsDBHandler.getFeatureDetails(featureType, customerID);
        final boolean isFeatureEnabled = jsonObject.getBoolean(FeatureSettingConstants.Api.Key.is_enabled);
        boolean isGroupIncluded = false;
        final HashSet<Long> groups = new HashSet<Long>();
        if (isFeatureEnabled) {
            final JSONArray jsonArray = jsonObject.getJSONArray(FeatureSettingConstants.Api.Key.groups);
            for (int i = 0; i < jsonArray.length(); ++i) {
                groups.add(jsonArray.getLong(i));
            }
            if (groups.contains(group)) {
                isGroupIncluded = true;
            }
        }
        return isGroupIncluded;
    }
    
    public static HashMap<Integer, String> getFeatureSettingsAndTypes() {
        final HashMap<Integer, String> featureSettingAndTypeMap = new HashMap<Integer, String>();
        featureSettingAndTypeMap.put(1, "BATTERY_SETTING");
        return featureSettingAndTypeMap;
    }
    
    static {
        MDMFeatureSettingsHandler.logger = Logger.getLogger("InventoryLogger");
    }
}
