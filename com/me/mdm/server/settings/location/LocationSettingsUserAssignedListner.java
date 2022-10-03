package com.me.mdm.server.settings.location;

import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class LocationSettingsUserAssignedListner extends ManagedDeviceListener
{
    @Override
    public void userAssigned(final DeviceEvent userEvent) {
        try {
            final Long userId = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(userEvent.resourceID).get("MANAGED_USER_ID");
            final ArrayList groupList = new ArrayList();
            final HashSet deviceList = new HashSet();
            deviceList.add(userEvent.resourceID);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            query.addJoin(new Join("CustomGroupMemberRel", "ManagedUser", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            query.setCriteria(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)userId, 0));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            DataObject dataObject = null;
            dataObject = DataAccess.get(query);
            final Iterator rows = dataObject.getRows("CustomGroupMemberRel");
            while (rows.hasNext()) {
                final Row row = rows.next();
                groupList.add(row.get("GROUP_RESOURCE_ID"));
            }
            final int groupInclusionStatus = LocationSettingsDataHandler.getInstance().getGroupInclusionStatus(userEvent.customerID, groupList);
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(userEvent.customerID);
            locationSettingsJSON.put("UPDATED_TIME", System.currentTimeMillis());
            final DataObject locationDeviceStatusDO = LocationSettingsDataHandler.getInstance().getLocationDeviceStatusDO(locationSettingsJSON);
            if (LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(userEvent.customerID)) {
                boolean status;
                if (groupInclusionStatus != -1) {
                    status = (groupInclusionStatus != 0);
                }
                else {
                    status = !LocationSettingsDataHandler.getInstance().isResourceIncluded(userEvent.customerID);
                }
                LocationSettingsDataHandler.getInstance().updateSelectedDeviceStatus(locationDeviceStatusDO, deviceList, status);
                MDMUtil.getPersistence().update(locationDeviceStatusDO);
                LocationSettingsRequestHandler.getInstance().locationSettingsCommandHandling(locationSettingsJSON);
            }
        }
        catch (final DataAccessException e) {
            Logger.getLogger(LocationSettingsUserAssignedListner.class.getName()).log(Level.SEVERE, "error in location setting in device listener", (Throwable)e);
        }
    }
}
