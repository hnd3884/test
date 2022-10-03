package com.me.mdm.server.settings.battery;

import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsDBHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import java.util.logging.Logger;

public class MdDeviceBatteryDetailsDBHandler
{
    public static Logger logger;
    public static MdDeviceBatteryDetailsDBHandler mdDeviceBatteryDetailsDBHandler;
    
    public static MdDeviceBatteryDetailsDBHandler getInstance() {
        if (MdDeviceBatteryDetailsDBHandler.mdDeviceBatteryDetailsDBHandler == null) {
            MdDeviceBatteryDetailsDBHandler.mdDeviceBatteryDetailsDBHandler = new MdDeviceBatteryDetailsDBHandler();
        }
        return MdDeviceBatteryDetailsDBHandler.mdDeviceBatteryDetailsDBHandler;
    }
    
    public static void addOrUpdateBatteryDetails(final Long resourceID, final JSONArray deviceBatteryDetailsArr) {
        try {
            MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Adding battery details. Checking if battery tracking is enabled for this device");
            final boolean isEnabled = MDMFeatureSettingsDBHandler.checkIfFeatureEnabledForDevice(1, resourceID);
            if (isEnabled) {
                MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Battery tracking enabled for this device");
                JSONArray batteryDetails = prepareRequiredDetails(deviceBatteryDetailsArr);
                final DataObject batteryDetailsDO = getLastUpdatedBatteryDetails(resourceID);
                Long lastUpdatedTime_ms = -1L;
                Double lastUpdatedBatteryLevel = -1.0;
                if (!batteryDetailsDO.isEmpty()) {
                    lastUpdatedTime_ms = (Long)batteryDetailsDO.getFirstRow("MdDeviceBatteryDetails").get("DEVICE_UTC_TIME");
                    lastUpdatedBatteryLevel = (Double)batteryDetailsDO.getFirstRow("MdDeviceBatteryDetails").get("BATTERY_LEVEL");
                }
                batteryDetails = removeOlderBatteryDetails(batteryDetails, lastUpdatedTime_ms, lastUpdatedBatteryLevel);
                if (batteryDetails.length() > 0) {
                    removeDuplicateBatteryDetails(batteryDetails);
                }
                if (batteryDetails.length() > 0) {
                    addNewerBatteryDetails(resourceID, batteryDetails);
                }
                MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Battery details successfully added");
            }
        }
        catch (final Exception e) {
            MdDeviceBatteryDetailsDBHandler.logger.log(Level.WARNING, "Exception while populating battery status", e);
        }
    }
    
    private static JSONArray prepareRequiredDetails(final JSONArray deviceBatteryDetailsArr) throws Exception {
        final JSONArray batteryDetails = new JSONArray();
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Preparing the required battery details");
        for (int i = 0; i < deviceBatteryDetailsArr.length(); ++i) {
            final JSONObject batteryDetail = deviceBatteryDetailsArr.getJSONObject(i);
            final String batteryLevel = batteryDetail.getString("BATTERY_LEVEL");
            final int batteryState = batteryDetail.optInt("BATTERY_STATE", 0);
            final String deviceLocalTime_date = batteryDetail.getString("DEVICE_LOCAL_TIME");
            final long deviceTimeDifference_ms = Long.parseLong(batteryDetail.optString("DEVICE_LOCAL_TIME_DIFFERENCE", "0"));
            long deviceLocalTime_ms = convertDateToMilliseconds(deviceLocalTime_date);
            if (deviceTimeDifference_ms != 0L) {
                deviceLocalTime_ms += deviceTimeDifference_ms;
            }
            final JSONObject incomingBatteryDetail = new JSONObject();
            incomingBatteryDetail.put("BATTERY_LEVEL", (Object)batteryLevel);
            incomingBatteryDetail.put("BATTERY_STATE", batteryState);
            incomingBatteryDetail.put("DEVICE_LOCAL_TIME", (Object)deviceLocalTime_date);
            incomingBatteryDetail.put("DEVICE_LOCAL_TIME_DIFFERENCE", deviceTimeDifference_ms);
            incomingBatteryDetail.put("DEVICE_UTC_TIME", deviceLocalTime_ms);
            batteryDetails.put((Object)incomingBatteryDetail);
        }
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Required battery details are prepared successfully.");
        return batteryDetails;
    }
    
    private static Long reduceFiveMinutesFromOriginalTime(Long originalTime_ms) {
        final long deviceTradeOffInterval_ms = 30000L;
        final int acceptableInterval_mins = 5;
        originalTime_ms -= acceptableInterval_mins * 60 * 1000 + deviceTradeOffInterval_ms;
        return originalTime_ms;
    }
    
    private static DataObject getLastUpdatedBatteryDetails(final Long resourceID) throws DataAccessException {
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Getting the last updated battery details");
        final SelectQuery lastUpdatedTimeQuery = getMdDeviceBatteryDetailsSelectQuery();
        final Criteria resourceCriteria = new Criteria(new Column("MdDeviceBatteryDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        final SortColumn sortColumn = new SortColumn("MdDeviceBatteryDetails", "DEVICE_UTC_TIME", false);
        lastUpdatedTimeQuery.setCriteria(resourceCriteria);
        lastUpdatedTimeQuery.addSortColumn(sortColumn);
        return MDMUtil.getPersistence().get(lastUpdatedTimeQuery);
    }
    
    private static JSONArray removeOlderBatteryDetails(final JSONArray batteryDetailsArr, final Long lastUpdatedTime_ms, final Double lastUpdatedBatteryLevel) {
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Removing battery details lesser than the last known updated time");
        final JSONArray validBatteryDetails = new JSONArray();
        for (int i = 0; i < batteryDetailsArr.length(); ++i) {
            final JSONObject batteryDetail = batteryDetailsArr.getJSONObject(i);
            long deviceTime_ms = batteryDetail.getLong("DEVICE_UTC_TIME");
            final double batteryLevel = Double.parseDouble(batteryDetail.getString("BATTERY_LEVEL"));
            deviceTime_ms = reduceFiveMinutesFromOriginalTime(deviceTime_ms);
            if (deviceTime_ms > lastUpdatedTime_ms) {
                validBatteryDetails.put((Object)batteryDetail);
            }
            else if (deviceTime_ms < lastUpdatedTime_ms && batteryLevel != lastUpdatedBatteryLevel) {
                validBatteryDetails.put((Object)batteryDetail);
            }
        }
        return validBatteryDetails;
    }
    
    private static void removeDuplicateBatteryDetails(final JSONArray batteryDetails) {
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Removing duplicate battery details");
        for (int i = 0; i < batteryDetails.length(); ++i) {
            final JSONObject batteryDetail1 = batteryDetails.getJSONObject(i);
            final double batteryLevel1 = Double.parseDouble(batteryDetail1.getString("BATTERY_LEVEL"));
            final long deviceTime1 = batteryDetail1.getLong("DEVICE_UTC_TIME");
            boolean startOver;
            do {
                startOver = false;
                for (int j = i + 1; j < batteryDetails.length(); ++j) {
                    final JSONObject batteryDetail2 = batteryDetails.getJSONObject(j);
                    final double batteryLevel2 = Double.parseDouble(batteryDetail2.getString("BATTERY_LEVEL"));
                    long deviceTime2 = batteryDetail2.getLong("DEVICE_UTC_TIME");
                    deviceTime2 = reduceFiveMinutesFromOriginalTime(deviceTime2);
                    if (deviceTime2 < deviceTime1 && batteryLevel2 == batteryLevel1) {
                        batteryDetails.remove(j);
                        startOver = true;
                        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Starting over again");
                        break;
                    }
                }
            } while (startOver);
        }
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Duplicate battery details removed");
    }
    
    private static void addNewerBatteryDetails(final Long resourceID, final JSONArray batteryDetails) throws Exception {
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Newer battery details are about to be added");
        final DataObject newBatteryDetails = MDMUtil.getPersistence().constructDataObject();
        for (int i = 0; i < batteryDetails.length(); ++i) {
            final JSONObject batteryDetail = batteryDetails.getJSONObject(i);
            final Row row = new Row("MdDeviceBatteryDetails");
            row.set("RESOURCE_ID", (Object)resourceID);
            row.set("BATTERY_LEVEL", (Object)batteryDetail.getString("BATTERY_LEVEL"));
            row.set("BATTERY_STATE", (Object)batteryDetail.getInt("BATTERY_STATE"));
            row.set("DEVICE_LOCAL_TIME", (Object)batteryDetail.getString("DEVICE_LOCAL_TIME"));
            row.set("DEVICE_UTC_TIME", (Object)batteryDetail.getLong("DEVICE_UTC_TIME"));
            row.set("DEVICE_LOCAL_TIME_DIFFERENCE", (Object)batteryDetail.optString("DEVICE_LOCAL_TIME_DIFFERENCE"));
            newBatteryDetails.addRow(row);
        }
        MDMUtil.getPersistence().add(newBatteryDetails);
        MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Newer battery details added successfully.");
    }
    
    public static Long convertDateToMilliseconds(final String deviceDateTime) throws ParseException {
        if (deviceDateTime != null) {
            final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            final Date date = formatter.parse(deviceDateTime);
            MdDeviceBatteryDetailsDBHandler.logger.log(Level.INFO, "Converted date to milliseconds");
            return date.getTime();
        }
        return null;
    }
    
    public static String convertMillisecondsToDate(final Long deviceTime) {
        if (deviceTime != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            return dateFormat.format(new Date(deviceTime));
        }
        return null;
    }
    
    private static SelectQuery getMdDeviceBatteryDetailsSelectQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceBatteryDetails"));
        selectQuery.addSelectColumn(new Column("MdDeviceBatteryDetails", "*"));
        return selectQuery;
    }
    
    static {
        MdDeviceBatteryDetailsDBHandler.logger = Logger.getLogger("MDMLogger");
    }
}
