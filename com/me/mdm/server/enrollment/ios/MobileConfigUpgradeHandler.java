package com.me.mdm.server.enrollment.ios;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;

public class MobileConfigUpgradeHandler
{
    private static MobileConfigUpgradeHandler mobileConfigUpgradeHandler;
    
    public static MobileConfigUpgradeHandler getInstance() {
        if (MobileConfigUpgradeHandler.mobileConfigUpgradeHandler == null) {
            MobileConfigUpgradeHandler.mobileConfigUpgradeHandler = new MobileConfigUpgradeHandler();
        }
        return MobileConfigUpgradeHandler.mobileConfigUpgradeHandler;
    }
    
    public void addOrUpdateMobileConfigUpgradeRequest(final String udid) throws Exception {
        final Long managedDeviceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        if (managedDeviceID != null) {
            DataObject DO = DBUtil.getDataObjectFromDB("MobileConfigUpgradeRequest", "MANAGED_DEVICE_ID", (Object)managedDeviceID);
            if (DO == null || DO.isEmpty()) {
                DO = (DataObject)new WritableDataObject();
                final Row deviceRow = new Row("MobileConfigUpgradeRequest");
                deviceRow.set("MANAGED_DEVICE_ID", (Object)managedDeviceID);
                DO.addRow(deviceRow);
                MDMUtil.getPersistence().add(DO);
            }
        }
    }
    
    public boolean hasMobileConfigUpgradeRequest(final String udid) throws Exception {
        final Long managedDeviceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        if (managedDeviceID != null) {
            final DataObject DO = DBUtil.getDataObjectFromDB("MobileConfigUpgradeRequest", "MANAGED_DEVICE_ID", (Object)managedDeviceID);
            if (DO != null && !DO.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public void removeMobileConfigUpgradeRequest(final String udid) throws Exception {
        final Long managedDeviceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        if (managedDeviceID != null) {
            MDMUtil.getPersistence().delete(new Criteria(Column.getColumn("MobileConfigUpgradeRequest", "MANAGED_DEVICE_ID"), (Object)managedDeviceID, 0));
        }
    }
    
    static {
        MobileConfigUpgradeHandler.mobileConfigUpgradeHandler = null;
    }
}
