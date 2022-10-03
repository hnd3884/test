package com.adventnet.sym.server.mdm.apps;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;

public class AppPermissionHandler
{
    private static HashMap permissionNameToGroupMap;
    
    public HashMap getAppPermissions(final Long appID) throws DataAccessException {
        final HashMap map = new HashMap();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToPermissions"));
        query.addJoin(new Join("MdAppToPermissions", "AppPermissions", new String[] { "APP_PERMISSION_ID" }, new String[] { "APP_PERMISSION_ID" }, 2));
        query.addJoin(new Join("AppPermissions", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
        query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"));
        query.setCriteria(new Criteria(Column.getColumn("MdAppToPermissions", "APP_ID"), (Object)appID, 0));
        final Iterator<Row> iterattor = MDMUtil.getPersistence().get(query).getRows("AppPermissionGroups");
        while (iterattor.hasNext()) {
            final Row row = iterattor.next();
            map.put(row.get("APP_PERMISSION_GROUP_NAME"), row.get("APP_PERMISSION_GROUP_ID"));
        }
        return map;
    }
    
    private static void fillAppPermissions() throws DataAccessException {
        AppPermissionHandler.permissionNameToGroupMap = new HashMap();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AppPermissions"));
        query.addJoin(new Join("AppPermissions", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
        query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"));
        query.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"));
        query.addSelectColumn(Column.getColumn("AppPermissions", "APP_PERMISSION_NAME"));
        query.addSelectColumn(Column.getColumn("AppPermissions", "APP_PERMISSION_ID"));
        query.addSelectColumn(Column.getColumn("AppPermissions", "APP_PERMISSION_GROUP_ID"));
        final Iterator<Row> iterator = MDMUtil.getPersistence().get(query).getRows("AppPermissions");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            AppPermissionHandler.permissionNameToGroupMap.put(row.get("APP_PERMISSION_NAME"), row.get("APP_PERMISSION_GROUP_ID"));
        }
    }
    
    public HashMap getPermissionNameToGroupMap() throws DataAccessException {
        if (AppPermissionHandler.permissionNameToGroupMap == null) {
            fillAppPermissions();
        }
        return AppPermissionHandler.permissionNameToGroupMap;
    }
    
    static {
        AppPermissionHandler.permissionNameToGroupMap = null;
    }
}
