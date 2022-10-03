package com.me.devicemanagement.framework.server.pushnotification.device;

import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;

public class DeviceTableHandler
{
    public long addDevice(final String deviceToken, final NotificationPlatform platform, final String model, final String deviceName) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        final Row actionRow = new Row("NotificationDevice");
        actionRow.set("DEVICE_TOKEN", (Object)deviceToken);
        actionRow.set("PLATFORM", (Object)platform.id);
        actionRow.set("MODEL", (Object)model);
        actionRow.set("DEVICE_NAME", (Object)deviceName);
        dataObject.addRow(actionRow);
        SyMUtil.getPersistence().add(dataObject);
        return (long)actionRow.get("NOTIFICATION_DEVICE_ID");
    }
    
    public void updateDeviceToken(final String oldDeviceToken, final String newDeviceToken) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("NotificationDevice"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("NotificationDevice", "DEVICE_TOKEN"), (Object)oldDeviceToken, 0, false));
        final DataObject resultDO = SyMUtil.getPersistence().get(selectQuery);
        final Row managedDevice = resultDO.getFirstRow("NotificationDevice");
        managedDevice.set("DEVICE_TOKEN", (Object)newDeviceToken);
        resultDO.updateRow(managedDevice);
        SyMUtil.getPersistence().update(resultDO);
    }
    
    public ArrayList<String> getDeviceTokens(final ArrayList<Long> deviceIDs, final NotificationPlatform platform) throws Exception {
        Criteria criteria = new Criteria(Column.getColumn("NotificationDevice", "PLATFORM"), (Object)platform.id, 0, false);
        if (deviceIDs.size() > 0) {
            final Criteria deviceIDCriteria = new Criteria(Column.getColumn("NotificationDevice", "NOTIFICATION_DEVICE_ID"), (Object)deviceIDs.toArray(), 8, false);
            criteria = criteria.and(deviceIDCriteria);
        }
        final DataObject dataObject = SyMUtil.getPersistence().get("NotificationDevice", criteria);
        final ArrayList<String> deviceTokens = new ArrayList<String>();
        final Iterator<Row> rows = dataObject.getRows("NotificationDevice");
        while (rows.hasNext()) {
            deviceTokens.add((String)rows.next().get("DEVICE_TOKEN"));
        }
        return deviceTokens;
    }
    
    public ArrayList<NotificationDevice> getDevices(final ArrayList<Long> deviceIDs, final NotificationPlatform... platforms) throws Exception {
        final ArrayList<NotificationDevice> devices = new ArrayList<NotificationDevice>();
        Criteria criteria = new Criteria(Column.getColumn("NotificationDevice", "NOTIFICATION_DEVICE_ID"), (Object)deviceIDs.toArray(), 8);
        if (platforms != null && platforms.length > 0) {
            final NotificationPlatform[] mobilePlatforms = NotificationPlatform.getMobileOnlyPlatforms(platforms);
            final ArrayList<Integer> platformIDs = new ArrayList<Integer>();
            if (mobilePlatforms.length <= 0) {
                return devices;
            }
            for (final NotificationPlatform platform : mobilePlatforms) {
                platformIDs.add(platform.id);
            }
            final Criteria platformCriteria = new Criteria(Column.getColumn("NotificationDevice", "PLATFORM"), (Object)platformIDs.toArray(), 8);
            criteria = criteria.and(platformCriteria);
        }
        final DataObject devicesDataObject = SyMUtil.getPersistence().get("NotificationDevice", criteria);
        final Iterator<Row> deviceRows = devicesDataObject.getRows("NotificationDevice");
        while (deviceRows.hasNext()) {
            final Row currentRow = deviceRows.next();
            final Long deviceID = (Long)currentRow.get("NOTIFICATION_DEVICE_ID");
            final NotificationPlatform platform2 = NotificationPlatform.getPlatform((int)currentRow.get("PLATFORM"));
            final String deviceToken = (String)currentRow.get("DEVICE_TOKEN");
            final NotificationDevice notificationDevice = new NotificationDevice(deviceID, platform2, deviceToken);
            devices.add(notificationDevice);
        }
        return devices;
    }
    
    public void removeDevice(final String deviceToken) throws Exception {
        final DataObject dataObject = SyMUtil.getPersistence().get("NotificationDevice", (Criteria)null);
        final Criteria criteria = new Criteria(Column.getColumn("NotificationDevice", "DEVICE_TOKEN"), (Object)deviceToken, 0, false);
        dataObject.deleteRows("NotificationDevice", criteria);
        SyMUtil.getPersistence().update(dataObject);
    }
    
    public Long getDeviceID(final String deviceToken) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("NotificationDevice", "DEVICE_TOKEN"), (Object)deviceToken, 0, false);
        final DataObject dataObject = SyMUtil.getPersistence().get("NotificationDevice", criteria);
        final Row row = dataObject.getRow("NotificationDevice");
        if (row != null) {
            return (Long)row.get("NOTIFICATION_DEVICE_ID");
        }
        return null;
    }
}
