package com.adventnet.sym.server.mdm.featuresettings.battery;

import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.me.mdm.api.inventory.FeatureSettingConstants;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsDBHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.logging.Logger;

public class MDMBatterySettingsDBHandler
{
    private static Logger logger;
    private static MDMBatterySettingsDBHandler mdmBatterySettingsDBHandler;
    
    public static MDMBatterySettingsDBHandler getInstance() {
        if (MDMBatterySettingsDBHandler.mdmBatterySettingsDBHandler == null) {
            MDMBatterySettingsDBHandler.mdmBatterySettingsDBHandler = new MDMBatterySettingsDBHandler();
        }
        return MDMBatterySettingsDBHandler.mdmBatterySettingsDBHandler;
    }
    
    public JSONObject getBatteryConfigurationForDevice(final DeviceDetails deviceDetails) {
        final JSONObject jsonObject = new JSONObject();
        MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Getting battery configuration for device");
        try {
            if (MDMFeatureSettingsDBHandler.checkIfFeatureEnabledForDevice(1, deviceDetails.resourceId)) {
                jsonObject.put("Battery_level_tracking_enabled", true);
                final JSONObject batterySettingsJson = getBatterySettingsJson(deviceDetails.customerId);
                final Integer trackingInterval = batterySettingsJson.getInt("TRACKING_INTERVAL");
                jsonObject.put("Battery_level_tracking_interval", (Object)trackingInterval);
            }
            else {
                jsonObject.put("Battery_level_tracking_enabled", false);
            }
        }
        catch (final Exception e) {
            MDMBatterySettingsDBHandler.logger.log(Level.SEVERE, "Exception while getting battery configuration for device");
        }
        return jsonObject;
    }
    
    public static void addOrUpdateBatterySettings(final HashMap<String, Object> settingsMap) throws Exception {
        MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Adding/ Updating battery settings");
        final Long settingID = settingsMap.get("SETTINGS_ID");
        final Long customerID = settingsMap.get("CUSTOMER_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMFeatureSettings"));
        final Join join = new Join("MDMFeatureSettings", "MdDeviceBatterySettings", new String[] { "SETTINGS_ID" }, new String[] { "SETTINGS_ID" }, 1);
        selectQuery.addJoin(join);
        selectQuery.addSelectColumn(new Column("MDMFeatureSettings", "*"));
        selectQuery.addSelectColumn(new Column("MdDeviceBatterySettings", "*"));
        final Criteria customerCriteria = MDMFeatureSettingsDBHandler.getCustomerCriteria(customerID);
        final Criteria featureTypeCriteria = MDMFeatureSettingsDBHandler.getFeatureTypeCriteria(1L);
        final Criteria featureEnabledCriteria = MDMFeatureSettingsDBHandler.getFeatureEnabledCriteria(true);
        final Criteria overAllCriteria = customerCriteria.and(featureTypeCriteria).and(featureEnabledCriteria);
        selectQuery.setCriteria(overAllCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final DataObject dataObject2 = MDMUtil.getPersistence().constructDataObject();
            final Long trackingInterval = settingsMap.get(FeatureSettingConstants.Battery.battery_tracking_interval);
            final int history_deletion_interval = settingsMap.get(FeatureSettingConstants.Battery.history_deletion_interval);
            Row row = dataObject.getRow("MdDeviceBatterySettings");
            if (row != null) {
                MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Updating battery settings");
                row.set("SETTINGS_ID", (Object)settingID);
                row.set("TRACKING_INTERVAL", (Object)trackingInterval);
                row.set("HISTORY_DELETION_INTERVAL", (Object)history_deletion_interval);
                dataObject2.updateBlindly(row);
            }
            else {
                MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Adding battery settings");
                row = new Row("MdDeviceBatterySettings");
                row.set("SETTINGS_ID", (Object)settingID);
                row.set("TRACKING_INTERVAL", (Object)trackingInterval);
                row.set("HISTORY_DELETION_INTERVAL", (Object)history_deletion_interval);
                dataObject2.addRow(row);
            }
            MDMUtil.getPersistence().update(dataObject2);
            MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Successfully updated battery settings");
        }
        else {
            MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Battery settings disabled. So related entry removed from table");
            MDMUtil.getPersistence().delete(new Criteria(new Column("MdDeviceBatterySettings", "SETTINGS_ID"), settingsMap.get("SETTINGS_ID"), 0));
        }
    }
    
    public static Long getBatteryLastUpdatedTime(final Long resourceID) {
        Long deviceTime = null;
        MDMBatterySettingsDBHandler.logger.log(Level.INFO, "getting device last sync time");
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceBatteryDetails"));
            selectQuery.addSelectColumn(new Column("MdDeviceBatteryDetails", "*"));
            final Column column = new Column("MdDeviceBatteryDetails", "DEVICE_UTC_TIME");
            final SortColumn sortColumn = new SortColumn(column, false);
            selectQuery.addSortColumn(sortColumn);
            final Criteria criteria = new Criteria(new Column("MdDeviceBatteryDetails", "RESOURCE_ID"), (Object)resourceID, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.setRange(new Range(0, 1));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            String deviceLastSynctime = null;
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdDeviceBatteryDetails");
                deviceLastSynctime = (String)row.get("DEVICE_LOCAL_TIME");
            }
            if (deviceLastSynctime != null && !deviceLastSynctime.isEmpty()) {
                deviceTime = MdDeviceBatteryDetailsDBHandler.convertDateToMilliseconds(deviceLastSynctime);
            }
        }
        catch (final Exception e) {
            MDMBatterySettingsDBHandler.logger.log(Level.INFO, "Exception while getting device last sync time", e);
        }
        return deviceTime;
    }
    
    public static DataObject getMDDeviceBatterySettingsDO(final Long customerID) throws Exception {
        return getMDDeviceBatterySettingsDO(customerID, true);
    }
    
    public static DataObject getMDDeviceBatterySettingsDO(final Long customerID, final boolean onDemand) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMFeatureSettings"));
        final Join join = new Join("MDMFeatureSettings", "MdDeviceBatterySettings", new String[] { "SETTINGS_ID" }, new String[] { "SETTINGS_ID" }, 2);
        final Criteria customerCriteria = new Criteria(new Column("MDMFeatureSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria featureEnabledCriteria = new Criteria(new Column("MDMFeatureSettings", "IS_FEATURE_ENABLED"), (Object)true, 0);
        final Criteria featureTypeCriteria = new Criteria(new Column("MDMFeatureSettings", "FEATURE_TYPE"), (Object)1, 0);
        final Criteria overAllCriteria = customerCriteria.and(featureTypeCriteria).and(featureEnabledCriteria);
        selectQuery.addJoin(join);
        selectQuery.setCriteria(overAllCriteria);
        selectQuery.addSelectColumn(new Column("MdDeviceBatterySettings", "*"));
        if (!onDemand) {
            return MDMUtil.getReadOnlyPersistence().get(selectQuery);
        }
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public static JSONObject getBatterySettingsJson(final Long customerID) throws Exception {
        final DataObject dataObject = getMDDeviceBatterySettingsDO(customerID);
        final JSONObject jsonObject = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MdDeviceBatterySettings");
            final long tracking_interval = (long)row.get("TRACKING_INTERVAL");
            final int history_deletion_interval = (int)row.get("HISTORY_DELETION_INTERVAL");
            jsonObject.put("TRACKING_INTERVAL", tracking_interval);
            jsonObject.put("HISTORY_DELETION_INTERVAL", history_deletion_interval);
        }
        return jsonObject;
    }
    
    static {
        MDMBatterySettingsDBHandler.logger = Logger.getLogger("InventoryLogger");
        MDMBatterySettingsDBHandler.mdmBatterySettingsDBHandler = null;
    }
}
