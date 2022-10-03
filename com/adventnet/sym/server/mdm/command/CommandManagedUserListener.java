package com.adventnet.sym.server.mdm.command;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import java.util.Arrays;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.UserEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedUserListener;

public class CommandManagedUserListener implements ManagedUserListener
{
    public static Logger logger;
    
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userDetailsModified(final UserEvent userEvent) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
            sQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria userCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)userEvent.resourceID, 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            sQuery.setCriteria(userCriteria.and(managedDeviceCriteria).and(userNotInTrashCriteria));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (!dobj.isEmpty()) {
                final Iterator<Row> iter = dobj.getRows("ManagedDevice");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    if ((int)row.get("PLATFORM_TYPE") == 2) {
                        final Long resourceId = (Long)row.get("RESOURCE_ID");
                        DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "UpdateUserInfo");
                        NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceId), 2);
                    }
                    else {
                        if ((int)row.get("PLATFORM_TYPE") != 3) {
                            continue;
                        }
                        final Long resourceId = (Long)row.get("RESOURCE_ID");
                        if (!ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceId, 10.0f)) {
                            continue;
                        }
                        DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "UpdateUserInfo");
                        NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceId), 3);
                    }
                }
            }
        }
        catch (final Exception ex) {
            CommandManagedUserListener.logger.log(Level.SEVERE, "Exeception in command Listener during user Details modification ", ex);
        }
    }
    
    @Override
    public void userTrashed(final UserEvent userEvent) {
    }
    
    static {
        CommandManagedUserListener.logger = Logger.getLogger("MDMLogger");
    }
}
