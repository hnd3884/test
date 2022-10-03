package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class DeviceManagedDetailsHistoryListener extends ManagedDeviceListener
{
    private static Logger logger;
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Entering DeviceManagedDetailsHistoryListener:deviceManaged");
            final JSONObject deviceDetails = deviceEvent.resourceJSON;
            final Long resourceID = deviceDetails.optLong("RESOURCE_ID");
            Long customerID = deviceDetails.optLong("CUSTOMER_ID");
            if (customerID == 0L) {
                customerID = deviceEvent.customerID;
                deviceDetails.put("CUSTOMER_ID", (Object)customerID);
            }
            if (resourceID != null && customerID != null) {
                final Long registeredTime = SyMUtil.getCurrentTimeInMillis();
                deviceDetails.put("REGISTERED_TIME", (Object)registeredTime);
                final DeviceManagedDetailsHandler deviceDetailsObject = new DeviceManagedDetailsHandler();
                final Long existingResourceID = deviceDetailsObject.getDuplicateDeviceID(deviceDetails);
                if (existingResourceID > 0L) {
                    deviceDetails.put("newResourceID", (Object)resourceID);
                    DeviceManagedDetailsHistoryListener.logger.log(Level.INFO, "Existing device entry available in DeviceManagedDetails. existingID:{0} newID:{1}", new Object[] { existingResourceID, resourceID });
                    deviceDetailsObject.addOrUpdateDeviceHandling(existingResourceID, deviceDetails);
                }
                else {
                    deviceDetailsObject.addOrUpdateDeviceHandling(resourceID, deviceDetails);
                }
            }
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Exiting DeviceManagedDetailsHistoryListener:deviceManaged");
        }
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Entering DeviceManagedDetailsHistoryListener:deviceManaged");
            final Long resourceID = deviceEvent.resourceID;
            final Long customerID = deviceEvent.customerID;
            if (resourceID != null && customerID != null) {
                final Long registeredTime = SyMUtil.getCurrentTimeInMillis();
                final JSONObject deviceDetailsJSON = this.getDeviceDetailsForHistory(resourceID, customerID);
                if (deviceDetailsJSON.length() > 1) {
                    deviceDetailsJSON.put("REGISTERED_TIME", (Object)registeredTime);
                    final DeviceManagedDetailsHandler deviceDetailsObject = new DeviceManagedDetailsHandler();
                    final Long existingResourceID = deviceDetailsObject.getDuplicateDeviceID(deviceDetailsJSON);
                    if (existingResourceID > 0L) {
                        deviceDetailsJSON.put("newResourceID", (Object)resourceID);
                        DeviceManagedDetailsHistoryListener.logger.log(Level.INFO, "Existing device entry available in DeviceManagedDetails. existingID:{0} newID:{1}", new Object[] { existingResourceID, resourceID });
                        deviceDetailsObject.addOrUpdateDeviceHandling(existingResourceID, deviceDetailsJSON);
                    }
                    else {
                        deviceDetailsObject.addOrUpdateDeviceHandling(resourceID, deviceDetailsJSON);
                    }
                }
            }
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Exiting DeviceManagedDetailsHistoryListener:deviceManaged");
        }
    }
    
    private JSONObject getDeviceDetailsForHistory(final Long resourceID, final Long customerId) {
        final JSONObject deviceDetails = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.setCriteria(deviceCriteria.and(customerCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row managedDeviceRow = dataObject.getRow("ManagedDevice");
                final String udid = (String)managedDeviceRow.get("UDID");
                deviceDetails.put("RESOURCE_ID", (Object)resourceID);
                deviceDetails.put("UDID", (Object)udid);
                final Row deviceInfoRow = dataObject.getRow("MdDeviceInfo");
                final String serialNo = (String)deviceInfoRow.get("SERIAL_NUMBER");
                final String imei = (String)deviceInfoRow.get("IMEI");
                deviceDetails.put("SERIAL_NUMBER", (Object)serialNo);
                deviceDetails.put("IMEI", (Object)imei);
                final Row deviceExtnRow = dataObject.getRow("ManagedDeviceExtn");
                final String deviceName = (String)deviceExtnRow.get("NAME");
                deviceDetails.put("NAME", (Object)deviceName);
                deviceDetails.put("CUSTOMER_ID", (Object)customerId);
            }
        }
        catch (final Exception e) {
            DeviceManagedDetailsHistoryListener.logger.log(Level.SEVERE, "Exception in DeviceManagedDetailsHistoryListener - getDeviceDetailsForHistory : ", e);
        }
        return deviceDetails;
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Entering DeviceManagedDetailsHistoryListener:deviceDeprovisioned");
            this.updateUnenrolledTimeToHistoryTable(deviceEvent);
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Exiting DeviceManagedDetailsHistoryListener:deviceDeprovisioned");
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        if (CustomerInfoUtil.getInstance().isMSP()) {
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Entering DeviceManagedDetailsHistoryListener:deviceUnmanaged");
            this.updateUnenrolledTimeToHistoryTable(deviceEvent);
            DeviceManagedDetailsHistoryListener.mdmlogger.info("Exiting DeviceManagedDetailsHistoryListener:deviceUnmanaged");
        }
    }
    
    private void updateUnenrolledTimeToHistoryTable(final DeviceEvent deviceEvent) {
        final Long resourceID = deviceEvent.resourceID;
        final Long customerID = deviceEvent.customerID;
        if (resourceID != null && customerID != null) {
            final Long unenrolledTime = SyMUtil.getCurrentTimeInMillis();
            final JSONObject deviceDetailsJSON = new JSONObject();
            deviceDetailsJSON.put("RESOURCE_ID", (Object)resourceID);
            deviceDetailsJSON.put("CUSTOMER_ID", (Object)customerID);
            deviceDetailsJSON.put("UNENROLLED_TIME", (Object)unenrolledTime);
            new DeviceManagedDetailsHandler().addOrUpdateDeviceHandling(resourceID, deviceDetailsJSON);
        }
    }
    
    static {
        DeviceManagedDetailsHistoryListener.logger = Logger.getLogger("MDMEnrollment");
    }
}
