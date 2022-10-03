package com.me.devicemanagement.framework.server.pushnotification.device.devicemap;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.pushnotification.device.DeviceTableHandler;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;

public class UserDeviceMapper implements DeviceMapper<Long>
{
    @Override
    public ArrayList<NotificationDevice> getMappedDevices(final Long mappingID, final NotificationPlatform... platforms) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("UserToNotificationDeviceRel", "USER_ID"), (Object)mappingID, 0, false);
        final DataObject dataObject = SyMUtil.getPersistence().get("UserToNotificationDeviceRel", criteria);
        final Iterator<Row> rows = dataObject.getRows("UserToNotificationDeviceRel");
        final ArrayList<Long> deviceIDs = new ArrayList<Long>();
        while (rows.hasNext()) {
            deviceIDs.add((Long)rows.next().get("NOTIFICATION_DEVICE_ID"));
        }
        return new DeviceTableHandler().getDevices(deviceIDs, platforms);
    }
    
    @Override
    public Long mapNewDevice(final Long userID, final String deviceToken, final NotificationPlatform platform, final String model, final String deviceName) throws Exception {
        final DeviceTableHandler deviceTableHandler = new DeviceTableHandler();
        Long deviceID = deviceTableHandler.getDeviceID(deviceToken);
        if (deviceID == null) {
            deviceID = new DeviceTableHandler().addDevice(deviceToken, platform, model, deviceName);
        }
        else {
            this.checkIfDeviceIsAlreadyMapped(userID, deviceID);
        }
        this.mapDeviceToUser(userID, deviceID);
        return deviceID;
    }
    
    public void mapDeviceToUser(final Long userID, final Long deviceID) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("UserToNotificationDeviceRel");
        actionRow.set("USER_ID", (Object)userID);
        actionRow.set("NOTIFICATION_DEVICE_ID", (Object)deviceID);
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
    }
    
    private void checkIfDeviceIsAlreadyMapped(final long userID, final long deviceID) throws Exception {
        final Long oldUser = this.getUser(deviceID);
        if (oldUser != null && !oldUser.equals(userID)) {
            this.removeUserToDeviceRelation(oldUser, deviceID);
        }
    }
    
    private Long getUser(final Long deviceID) {
        Long userID = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("UserToNotificationDeviceRel", "NOTIFICATION_DEVICE_ID"), (Object)deviceID, 0, false);
            final DataObject dataObject = SyMUtil.getPersistence().get("UserToNotificationDeviceRel", criteria);
            final Row row = dataObject.getRow("UserToNotificationDeviceRel");
            userID = (Long)row.get("USER_ID");
        }
        catch (final Exception ex) {}
        return userID;
    }
    
    private void removeUserToDeviceRelation(final Long userID, final Long deviceID) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get("UserToNotificationDeviceRel", (Criteria)null);
            final Criteria criteria = new Criteria(Column.getColumn("UserToNotificationDeviceRel", "USER_ID"), (Object)userID, 0, false);
            criteria.and(new Criteria(Column.getColumn("UserToNotificationDeviceRel", "NOTIFICATION_DEVICE_ID"), (Object)deviceID, 0, false));
            dataObject.deleteRows("UserToNotificationDeviceRel", criteria);
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception ex) {}
    }
}
