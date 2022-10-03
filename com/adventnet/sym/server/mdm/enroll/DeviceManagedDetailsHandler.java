package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeviceManagedDetailsHandler
{
    private static Logger logger;
    
    public void updateDeviceManagedHistoryTable(final JSONObject deviceHistoryParams) {
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of generateBillingHistoryJson - updateDeviceHistoryTable...");
            final Long customerId = deviceHistoryParams.optLong("customerID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REGISTERED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final Criteria statusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(statusCriteria.and(customerCriteria));
            int startRange = 1;
            final int valueCount = 500;
            final int endRange = MDMUtil.getPersistence().get(selectQuery).size("ManagedDevice");
            DataObject dataObject = null;
            while (startRange <= endRange) {
                selectQuery.setRange(new Range(startRange, valueCount));
                dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator dataItr = dataObject.getRows("ManagedDevice");
                final HashMap<Long, JSONObject> deviceDetailsMap = new HashMap<Long, JSONObject>();
                final List<Long> deviceIDList = new ArrayList<Long>();
                while (dataItr.hasNext()) {
                    final JSONObject deviceDetailsJSON = new JSONObject();
                    final Row managedDeviceRow = dataItr.next();
                    final Long resourceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                    final String udid = (String)managedDeviceRow.get("UDID");
                    final Long registeredTime = (Long)managedDeviceRow.get("REGISTERED_TIME");
                    deviceDetailsJSON.put("RESOURCE_ID", (Object)resourceId);
                    deviceDetailsJSON.put("UDID", (Object)udid);
                    deviceDetailsJSON.put("REGISTERED_TIME", (Object)registeredTime);
                    Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Row deviceDetailsRow = dataObject.getRow("MdDeviceInfo", resourceCriteria);
                    final String serialNo = (String)deviceDetailsRow.get("SERIAL_NUMBER");
                    final String imei = (String)deviceDetailsRow.get("IMEI");
                    deviceDetailsJSON.put("SERIAL_NUMBER", (Object)serialNo);
                    deviceDetailsJSON.put("IMEI", (Object)imei);
                    resourceCriteria = new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
                    final Row deviceExtnRow = dataObject.getRow("ManagedDeviceExtn", resourceCriteria);
                    final String deviceName = (String)deviceExtnRow.get("NAME");
                    deviceDetailsJSON.put("NAME", (Object)deviceName);
                    deviceDetailsJSON.put("CUSTOMER_ID", (Object)customerId);
                    deviceDetailsMap.put(resourceId, deviceDetailsJSON);
                    deviceIDList.add(resourceId);
                }
                this.addOrUpdateMultipleDevicesHandling(deviceIDList, deviceDetailsMap, customerId);
                startRange += valueCount;
            }
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - updateDeviceHistoryTable : ", e);
        }
    }
    
    public void addOrUpdateMultipleDevicesHandling(final List<Long> resourceList, final HashMap<Long, JSONObject> deviceDetailsMap, final Long customerID) {
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of DeviceManagedDetailsHandler - addOrUpdateMultipleDevicesHandling...");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria custCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria criteria = resourceCriteria.and(custCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get("DeviceManagedDetails", criteria);
            final Iterator dataItr = dataObject.getRows("DeviceManagedDetails");
            while (dataItr.hasNext()) {
                final Row deviceHistoryRow = dataItr.next();
                final Long resourceID = (Long)deviceHistoryRow.get("RESOURCE_ID");
                final JSONObject deviceData = deviceDetailsMap.get(resourceID);
                final Long enrolledTime = deviceData.optLong("REGISTERED_TIME");
                if (enrolledTime != 0L) {
                    deviceHistoryRow.set("ENROLLED_TIME", (Object)deviceData.optLong("REGISTERED_TIME"));
                }
                final String deviceName = deviceData.optString("NAME");
                final String existingDeviceName = (String)deviceHistoryRow.get("NAME");
                if (!deviceName.isEmpty() && !deviceName.equals(existingDeviceName)) {
                    deviceHistoryRow.set("NAME", (Object)deviceName);
                }
                final String udid = deviceData.optString("UDID");
                if (!udid.isEmpty()) {
                    deviceHistoryRow.set("UDID", (Object)udid);
                }
                final String imei = deviceData.optString("IMEI");
                if (!imei.isEmpty()) {
                    deviceHistoryRow.set("IMEI", (Object)imei);
                }
                final String serialNo = deviceData.optString("SERIAL_NUMBER");
                if (!serialNo.isEmpty()) {
                    deviceHistoryRow.set("SERIAL_NUMBER", (Object)serialNo);
                }
                deviceHistoryRow.set("UNENROLLED_TIME", (Object)(-1));
                dataObject.updateRow(deviceHistoryRow);
                resourceList.remove(resourceID);
            }
            for (int i = 0; i < resourceList.size(); ++i) {
                final Long resourceID = resourceList.get(i);
                final JSONObject deviceData = deviceDetailsMap.get(resourceID);
                final Row deviceHistoryRow2 = new Row("DeviceManagedDetails");
                deviceHistoryRow2.set("RESOURCE_ID", (Object)resourceID);
                deviceHistoryRow2.set("ENROLLED_TIME", (Object)deviceData.optLong("REGISTERED_TIME"));
                deviceHistoryRow2.set("NAME", (Object)deviceData.optString("NAME"));
                deviceHistoryRow2.set("UDID", (Object)deviceData.optString("UDID"));
                deviceHistoryRow2.set("IMEI", (Object)deviceData.optString("IMEI"));
                deviceHistoryRow2.set("SERIAL_NUMBER", (Object)deviceData.optString("SERIAL_NUMBER"));
                deviceHistoryRow2.set("CUSTOMER_ID", (Object)customerID);
                dataObject.addRow(deviceHistoryRow2);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - addOrUpdateMultipleDevicesHandling : ", e);
        }
    }
    
    public Long getNoOfDevicesConsumedBetweenMillis(final JSONObject billingParams) {
        Long noOfDevices = 0L;
        final Long customerID = billingParams.optLong("customerID");
        final Long startTime = billingParams.optLong("startTime");
        final Long endTime = billingParams.optLong("endTime");
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of DeviceManagedDetailsHandler - getNoOfDevicesConsumedBetweenMillis...");
            final String[] durationDate = { startTime.toString(), endTime.toString() };
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceManagedDetails"));
            final Criteria unenrolledCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)durationDate, 14);
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)(-1), 0).and(new Criteria(Column.getColumn("DeviceManagedDetails", "ENROLLED_TIME"), (Object)endTime, 6));
            final Criteria unenrolledInNextCycleCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)startTime, 4).and(new Criteria(Column.getColumn("DeviceManagedDetails", "ENROLLED_TIME"), (Object)endTime, 6));
            Criteria criteria = unenrolledCriteria.or(enrolledCriteria).or(unenrolledInNextCycleCriteria);
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(new Column("DeviceManagedDetails", "RESOURCE_ID").distinct().count());
            noOfDevices = (long)DBUtil.getRecordCount(query);
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - getNoOfDevicesConsumedBetweenMillis : ", e);
        }
        return noOfDevices;
    }
    
    public void addOrUpdateDeviceHandling(final Long resourceID, final JSONObject deviceData) {
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of DeviceManagedDetailsHandler - addDeviceHandling...");
            final Long customerID = deviceData.optLong("CUSTOMER_ID");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria custCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria criteria = resourceCriteria.and(custCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get("DeviceManagedDetails", criteria);
            if (dataObject != null && dataObject.isEmpty()) {
                final Row historyRow = new Row("DeviceManagedDetails");
                historyRow.set("RESOURCE_ID", (Object)resourceID);
                final Long enrolledTime = deviceData.optLong("REGISTERED_TIME");
                if (enrolledTime == 0L) {
                    return;
                }
                historyRow.set("ENROLLED_TIME", (Object)enrolledTime);
                historyRow.set("NAME", (Object)deviceData.optString("NAME"));
                historyRow.set("UDID", (Object)deviceData.optString("UDID"));
                String imei = deviceData.optString("IMEI");
                if (imei != null) {
                    imei = imei.replace(" ", "");
                }
                historyRow.set("IMEI", (Object)imei);
                historyRow.set("SERIAL_NUMBER", (Object)deviceData.optString("SERIAL_NUMBER"));
                historyRow.set("CUSTOMER_ID", (Object)customerID);
                dataObject.addRow(historyRow);
            }
            else if (deviceData.has("UNENROLLED_TIME")) {
                final Row historyRow = dataObject.getRow("DeviceManagedDetails");
                historyRow.set("UNENROLLED_TIME", (Object)deviceData.optLong("UNENROLLED_TIME"));
                dataObject.updateRow(historyRow);
            }
            else {
                final Row historyRow = dataObject.getRow("DeviceManagedDetails");
                final Boolean isduplicateData = deviceData.has("newResourceID");
                if (isduplicateData) {
                    historyRow.set("RESOURCE_ID", (Object)deviceData.optLong("newResourceID"));
                }
                final Long enrolledTime2 = deviceData.optLong("REGISTERED_TIME");
                if (enrolledTime2 != 0L & !isduplicateData) {
                    historyRow.set("ENROLLED_TIME", (Object)deviceData.optLong("REGISTERED_TIME"));
                }
                final String deviceName = deviceData.optString("NAME");
                final String existingDeviceName = (String)historyRow.get("NAME");
                if (!deviceName.isEmpty() && !deviceName.equals(existingDeviceName)) {
                    historyRow.set("NAME", (Object)deviceName);
                }
                final String udid = deviceData.optString("UDID");
                if (!udid.isEmpty()) {
                    historyRow.set("UDID", (Object)udid);
                }
                String imei2 = deviceData.optString("IMEI");
                if (!imei2.isEmpty()) {
                    imei2 = imei2.replace(" ", "");
                    historyRow.set("IMEI", (Object)imei2);
                }
                final String serialNo = deviceData.optString("SERIAL_NUMBER");
                if (!serialNo.isEmpty()) {
                    historyRow.set("SERIAL_NUMBER", (Object)serialNo);
                }
                historyRow.set("UNENROLLED_TIME", (Object)(-1));
                dataObject.updateRow(historyRow);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - addDeviceHandling : ", e);
        }
    }
    
    public JSONArray getDevicesHistoryForBilling(final JSONObject billingParams) {
        final JSONArray deviceArray = new JSONArray();
        final Long customerID = billingParams.optLong("customerID");
        final Long startTime = billingParams.optLong("startTime");
        final Long endTime = billingParams.optLong("endTime");
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of DeviceManagedDetailsHandler - getDevicesHistoryForBilling...");
            final String[] durationDate = { startTime.toString(), endTime.toString() };
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceManagedDetails"));
            final Criteria unenrolledCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)durationDate, 14);
            final Criteria enrolledCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)(-1), 0).and(new Criteria(Column.getColumn("DeviceManagedDetails", "ENROLLED_TIME"), (Object)endTime, 6));
            final Criteria unenrolledInNextCycleCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UNENROLLED_TIME"), (Object)startTime, 4).and(new Criteria(Column.getColumn("DeviceManagedDetails", "ENROLLED_TIME"), (Object)endTime, 6));
            Criteria criteria = unenrolledCriteria.or(enrolledCriteria).or(unenrolledInNextCycleCriteria);
            criteria = criteria.and(new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("DeviceManagedDetails", "*"));
            int startRange = 1;
            final int valueCount = 500;
            final int endRange = MDMUtil.getPersistence().get(query).size("DeviceManagedDetails");
            DataObject dataObject = null;
            while (startRange <= endRange) {
                query.setRange(new Range(startRange, valueCount));
                dataObject = SyMUtil.getPersistence().get(query);
                final Iterator dataItr = dataObject.getRows("DeviceManagedDetails");
                while (dataItr.hasNext()) {
                    final JSONObject deviceDetailsJSON = new JSONObject();
                    final Row deviceHistoryRow = dataItr.next();
                    final Long resourceId = (Long)deviceHistoryRow.get("RESOURCE_ID");
                    final String udid = (String)deviceHistoryRow.get("UDID");
                    final Long enrolledTime = (Long)deviceHistoryRow.get("ENROLLED_TIME");
                    final Long unenrolledTime = (Long)deviceHistoryRow.get("UNENROLLED_TIME");
                    final String serialNo = (String)deviceHistoryRow.get("SERIAL_NUMBER");
                    final String imei = (String)deviceHistoryRow.get("IMEI");
                    final String deviceName = (String)deviceHistoryRow.get("NAME");
                    deviceDetailsJSON.put("resourceID", (Object)resourceId);
                    deviceDetailsJSON.put("udid", (Object)udid);
                    deviceDetailsJSON.put("enrolled_time", (Object)enrolledTime);
                    deviceDetailsJSON.put("unenrolled_time", (Object)unenrolledTime);
                    deviceDetailsJSON.put("sl_no", (Object)serialNo);
                    deviceDetailsJSON.put("imei", (Object)imei);
                    deviceDetailsJSON.put("device_name", (Object)deviceName);
                    deviceArray.put((Object)deviceDetailsJSON);
                }
                startRange += valueCount;
            }
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - getDevicesHistoryForBilling : ", e);
        }
        return deviceArray;
    }
    
    public void updateDeviceNameInHistoryTable(final Long customerID, final ArrayList<Long> resourceList, final HashMap<Long, String> resourceDeviceNameMap) {
        try {
            DeviceManagedDetailsHandler.logger.log(Level.INFO, "Start of DeviceManagedDetailsHandler - updateDeviceNameInHistoryTable...");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria custCriteria = new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria criteria = resourceCriteria.and(custCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get("DeviceManagedDetails", criteria);
            final Iterator dataItr = dataObject.getRows("DeviceManagedDetails");
            while (dataItr.hasNext()) {
                final Row deviceHistoryRow = dataItr.next();
                final Long resourceId = (Long)deviceHistoryRow.get("RESOURCE_ID");
                final String existingDeviceName = (String)deviceHistoryRow.get("NAME");
                final String newDeviceName = resourceDeviceNameMap.get(resourceId);
                if (!newDeviceName.isEmpty() && !newDeviceName.equals(existingDeviceName)) {
                    deviceHistoryRow.set("NAME", (Object)newDeviceName);
                }
                dataObject.updateRow(deviceHistoryRow);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            DeviceManagedDetailsHandler.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHandler - updateDeviceNameInHistoryTable : ", e);
        }
    }
    
    public Long getDuplicateDeviceID(final JSONObject deviceInfo) {
        DMSecurityLogger.info(DeviceManagedDetailsHandler.logger, "DeviceManagedDetailsHandler", "getDuplicateDeviceID", "Duplicate Device Handling Begins in getDuplicateDeviceID. Data : {0}", (Object)deviceInfo);
        Long resourceID = -1L;
        final Long customerID = deviceInfo.getLong("CUSTOMER_ID");
        final String imei = deviceInfo.optString("IMEI", (String)null);
        final String serialNumber = deviceInfo.optString("SERIAL_NUMBER", (String)null);
        final String udid = deviceInfo.optString("UDID", (String)null);
        Criteria criteria = null;
        try {
            final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
            if (allowDuplicateSerialNumber && MDMStringUtils.isValidDeviceIdentifier(udid)) {
                criteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UDID"), (Object)udid, 0);
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(imei)) {
                criteria = new Criteria(Column.getColumn("DeviceManagedDetails", "IMEI"), (Object)imei, 0);
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(serialNumber)) {
                criteria = new Criteria(Column.getColumn("DeviceManagedDetails", "SERIAL_NUMBER"), (Object)serialNumber, 0);
            }
            else if (MDMStringUtils.isValidDeviceIdentifier(udid)) {
                criteria = new Criteria(Column.getColumn("DeviceManagedDetails", "UDID"), (Object)udid, 0);
            }
            if (criteria != null) {
                final SelectQuery sql = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceManagedDetails"));
                sql.addSelectColumn(new Column("DeviceManagedDetails", "RESOURCE_ID"));
                sql.addSelectColumn(new Column("DeviceManagedDetails", "DETAILS_ID"));
                sql.addSelectColumn(new Column("DeviceManagedDetails", "CUSTOMER_ID"));
                criteria = criteria.and(new Criteria(Column.getColumn("DeviceManagedDetails", "CUSTOMER_ID"), (Object)customerID, 0));
                sql.setCriteria(criteria);
                final DataObject deviceDO = MDMUtil.getPersistence().get(sql);
                if (deviceDO != null && !deviceDO.isEmpty()) {
                    final Row row = deviceDO.getFirstRow("DeviceManagedDetails");
                    resourceID = (Long)row.get("RESOURCE_ID");
                    DeviceManagedDetailsHandler.logger.log(Level.INFO, "Duplicate Device Found in DeviceManagedDetails: Resource ID : {0}", resourceID);
                }
            }
        }
        catch (final Exception ex) {
            DeviceManagedDetailsHandler.logger.log(Level.WARNING, "Exception occurred while getDuplicateDeviceID", ex);
        }
        DeviceManagedDetailsHandler.logger.log(Level.INFO, "Duplicate Device Handling Completed in getDuplicateDeviceID");
        return resourceID;
    }
    
    static {
        DeviceManagedDetailsHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
