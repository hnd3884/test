package com.me.mdm.server.conditionalaccess;

import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AzureWinCEAListener extends ManagedDeviceListener
{
    private HashMap getManagedUserEmailAddressForDevice(final DeviceEvent event) {
        return ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(event.resourceID);
    }
    
    private void diffSyncDomain(final DeviceEvent deviceEvent) {
        final HashMap enrolledDeviceDetails = this.getManagedUserEmailAddressForDevice(deviceEvent);
        final String domainName = enrolledDeviceDetails.get("DOMAIN_NETBIOS_NAME");
        if (!SyMUtil.isStringEmpty(domainName) && !domainName.equalsIgnoreCase("MDM")) {
            DirectoryUtil.getInstance().syncDomain(domainName, deviceEvent.customerID, Integer.valueOf(3), Boolean.valueOf(false));
        }
    }
    
    @Override
    public void devicePostScan(final DeviceEvent deviceEvent) {
        try {
            this.diffSyncDomain(deviceEvent);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred while parsing JSON", e);
        }
    }
    
    public static void markDeviceStatusUnmanaged(final List resourceIdList) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DirResRel"));
            query.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            query.addSelectColumn(Column.getColumn("DirResRel", "GUID"));
            query.addSelectColumn(Column.getColumn("DirResRel", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("DirResRel", "DM_DOMAIN_ID"));
            query.setCriteria(criteria);
            final DataObject dobj = MDMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Iterator rows = dobj.getRows("DirResRel");
                while (rows.hasNext()) {
                    final Row r = rows.next();
                    final String oguid = (String)r.get("GUID");
                    final Long domainid = (Long)r.get("DM_DOMAIN_ID");
                    final Long resourceId = (Long)r.get("RESOURCE_ID");
                    final String domainUserName = DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUsername(domainid);
                    final List<Integer> syncObjects = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(domainid);
                    if (syncObjects.contains(205)) {
                        AzureWinCEA.getInstance().markDeviceStatus(domainid, oguid, resourceId, false, false, domainUserName);
                    }
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred while markDeviceStatusUnmanaged", e);
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        try {
            final Long resourceId = deviceEvent.resourceID;
            final List l = new ArrayList();
            l.add(resourceId);
            markDeviceStatusUnmanaged(l);
            this.diffSyncDomain(deviceEvent);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred while parsing JSON", e);
        }
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        try {
            this.diffSyncDomain(userEvent);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception occurred while parsing JSON", e);
        }
    }
}
