package com.adventnet.sym.webclient.mdm.encryption.ios;

import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;

public class MDMDeviceRecentUserViewHandler
{
    public void addRecentUsersTableJoin(final SelectQuery viewQuery, final String viewName) {
        final ArrayList<String> viewWithRecentUsersList = new ArrayList<String>();
        viewWithRecentUsersList.add("DevicesByEncryption");
        viewWithRecentUsersList.add("mdmGroupDevices");
        viewWithRecentUsersList.add("mdmMgmtDevice");
        viewWithRecentUsersList.add("mdmDevicesAssociatedwithProfile");
        if (viewWithRecentUsersList.contains(viewName) && viewQuery.getTableList().contains(Table.getTable("ManagedDevice")) && !viewQuery.getTableList().contains(Table.getTable("MdDeviceRecentUsersInfo"))) {
            final SelectQuery recentUsersSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceRecentUsersInfo"));
            recentUsersSelectQuery.addJoin(new Join("MdDeviceRecentUsersInfo", "MdDeviceRecentUsersInfoExtn", new String[] { "DEVICE_RECENT_USER_ID" }, new String[] { "DEVICE_RECENT_USER_ID" }, 2));
            recentUsersSelectQuery.setCriteria(new Criteria(new Column("MdDeviceRecentUsersInfo", "ORDER"), (Object)1, 0));
            recentUsersSelectQuery.addSelectColumn(new Column("MdDeviceRecentUsersInfo", "RESOURCE_ID"));
            recentUsersSelectQuery.addSelectColumn(new Column("MdDeviceRecentUsersInfoExtn", "LOGON_USER_NAME", "MdDeviceRecentUsersInfoExtn.LOGON_USER_NAME"));
            recentUsersSelectQuery.addSelectColumn(new Column("MdDeviceRecentUsersInfoExtn", "LOGIN_TIME", "MdDeviceRecentUsersInfoExtn.LOGIN_TIME"));
            final DerivedTable recentUsersDerivedTable = new DerivedTable("MdDeviceRecentUsersInfo", (Query)recentUsersSelectQuery);
            viewQuery.addJoin(new Join(Table.getTable("ManagedDevice"), (Table)recentUsersDerivedTable, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        }
    }
}
