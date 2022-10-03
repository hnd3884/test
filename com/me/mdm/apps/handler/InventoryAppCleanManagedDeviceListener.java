package com.me.mdm.apps.handler;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class InventoryAppCleanManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        final Long deviceID = deviceEvent.resourceID;
        if (ManagedDeviceHandler.getInstance().isProfileOwner(deviceID)) {
            this.removeAndroidPersonalAppsForPODevices(deviceID);
        }
    }
    
    private void removeAndroidPersonalAppsForPODevices(final Long deviceID) {
        try {
            final SelectQuery personalAppsDeleteQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdInstalledAppResourceRel"));
            personalAppsDeleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            personalAppsDeleteQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria profileOwnerCriteria = new Criteria(new Column("MdDeviceInfo", "IS_PROFILEOWNER"), (Object)true, 0);
            final Criteria deviceScopeCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "SCOPE"), (Object)0, 0);
            final Criteria platformCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria resourceIdCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceID, 0);
            personalAppsDeleteQuery.setCriteria(profileOwnerCriteria.and(deviceScopeCriteria).and(platformCriteria).and(resourceIdCriteria));
            personalAppsDeleteQuery.addSelectColumn(new Column("MdInstalledAppResourceRel", "*"));
            final DataObject dataObject = DataAccess.get(personalAppsDeleteQuery);
            if (!dataObject.isEmpty()) {
                InventoryAppCleanManagedDeviceListener.mdmlogger.log(Level.INFO, "Going to delete existing personal apps for device {0}", deviceID);
                dataObject.deleteRows("MdInstalledAppResourceRel", (Criteria)null);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final DataAccessException e) {
            InventoryAppCleanManagedDeviceListener.mdmlogger.log(Level.SEVERE, "Couldnot delete personal apps for the PO resource ", (Throwable)e);
        }
    }
}
