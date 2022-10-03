package com.me.mdm.server.device;

import com.me.mdm.server.device.api.model.DeviceUnlockSettingsModel;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DeviceUnlockPinHandler
{
    protected static Logger logger;
    private static DeviceUnlockPinHandler deviceUnlockPinHandler;
    public static final String DEVICE_WIPE_PIN = "device_wipe_pin";
    public static final String DEVICE_LOCK_PIN = "device_lock_pin";
    public static final String DEVICE_WIPE_PIN_STATE = "device_wipe_pin_state";
    public static final String DEVICE_LOCK_PIN_STATE = "device_lock_pin_state";
    public static final int DEVICE_WIPE_PIN_ID = 1;
    public static final int DEVICE_LOCK_PIN_ID = 2;
    public static final int DEVICE_ALL_PIN_ID = 0;
    
    public static DeviceUnlockPinHandler getInstance() {
        if (DeviceUnlockPinHandler.deviceUnlockPinHandler == null) {
            DeviceUnlockPinHandler.deviceUnlockPinHandler = new DeviceUnlockPinHandler();
        }
        return DeviceUnlockPinHandler.deviceUnlockPinHandler;
    }
    
    private DataObject getDataObject(final Long customerID, final Long resourceID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ManagedDevice", "MdDeviceLockMessage", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            query.addJoin(new Join("ManagedDevice", "MdDeviceWipeOptions", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            query.addSelectColumn(Column.getColumn("MdDeviceLockMessage", "*"));
            query.addSelectColumn(Column.getColumn("MdDeviceWipeOptions", "*"));
            Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            query.setCriteria(criteria);
            return MDMUtil.getPersistence().get(query);
        }
        catch (final Exception e) {
            DeviceUnlockPinHandler.logger.log(Level.SEVERE, "Failed to fetch data for Unlock PIN of the device", e);
            return null;
        }
    }
    
    public void setDeviceUnlockPIN(final DeviceUnlockSettingsModel deviceUnlockSettingsModel, final Integer pinType) {
        deviceUnlockSettingsModel.setDeviceLockPinState(Boolean.FALSE);
        deviceUnlockSettingsModel.setDeviceWipePinState(Boolean.FALSE);
        try {
            final DataObject dataObject = this.getDataObject(deviceUnlockSettingsModel.getCustomerId(), deviceUnlockSettingsModel.getResourceID());
            if (dataObject == null) {
                return;
            }
            if (dataObject.containsTable("MdDeviceWipeOptions") && (pinType.equals(1) || pinType.equals(0))) {
                deviceUnlockSettingsModel.setDeviceWipePin((String)dataObject.getRow("MdDeviceWipeOptions").get("WIPE_LOCK_PIN"));
                deviceUnlockSettingsModel.setDeviceWipePinState(Boolean.TRUE);
            }
            else if (dataObject.containsTable("MdDeviceLockMessage") && (pinType.equals(2) || pinType.equals(0))) {
                deviceUnlockSettingsModel.setDeviceLockPin((String)dataObject.getRow("MdDeviceLockMessage").get("UNLOCK_PIN"));
                deviceUnlockSettingsModel.setDeviceLockPinState(Boolean.TRUE);
            }
        }
        catch (final Exception e) {
            DeviceUnlockPinHandler.logger.log(Level.SEVERE, "Unable to fetch the Unlock PIN of the device", e);
        }
    }
    
    static {
        DeviceUnlockPinHandler.logger = Logger.getLogger("MDMDeviceSecurityLogger");
        DeviceUnlockPinHandler.deviceUnlockPinHandler = null;
    }
}
