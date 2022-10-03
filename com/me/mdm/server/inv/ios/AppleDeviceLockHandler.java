package com.me.mdm.server.inv.ios;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.DeviceDetails;

public class AppleDeviceLockHandler
{
    public static final String DEVICE_LOCK_PIN_ALERT = "MDM_DEVICE_LOCK_PIN_ALERT";
    protected static final String MANAGED_USER_DETAIL_TABLE = "userResourceDetail";
    
    public static AppleDeviceLockHandler getDeviceLockHandler(final Long resourceId) {
        AppleDeviceLockHandler appleDeviceLockHandler = null;
        final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
        switch (deviceDetails.modelType) {
            case 3: {
                appleDeviceLockHandler = new MacDeviceLockHandler();
                break;
            }
            default: {
                appleDeviceLockHandler = new AppleDeviceLockHandler();
                break;
            }
        }
        return appleDeviceLockHandler;
    }
    
    public void checkAndSendEmail(final JSONObject emailDetails) {
    }
    
    protected SelectQuery getLockscreenMessageQuery(final Long resourceId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceLockMessage"));
        selectQuery.addJoin(new Join("MdDeviceLockMessage", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "userResourceDetail", 2));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(criteria.and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(new Column("MdDeviceLockMessage", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("MdDeviceLockMessage", "DEVICE_LOCK_MESSAGE_ID"));
        selectQuery.addSelectColumn(new Column("MdDeviceLockMessage", "SEND_MAIL_TO_USER"));
        selectQuery.addSelectColumn(new Column("MdDeviceLockMessage", "UNLOCK_PIN"));
        selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("Resource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(new Column("Resource", "NAME"));
        selectQuery.addSelectColumn(new Column("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(new Column("ManagedUserToDevice", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(new Column("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(new Column("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(new Column("userResourceDetail", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("userResourceDetail", "NAME"));
        return selectQuery;
    }
}
