package com.me.mdm.server.apps.permission;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONException;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;

public class PermissionHandler
{
    public void addOrUpdatePermissionsForApp(final Long appId, final JSONArray arr) throws DataAccessException, Exception {
        final SelectQuery permissionQuery = (SelectQuery)new SelectQueryImpl(new Table("AppPermissions"));
        permissionQuery.addSelectColumn(new Column("AppPermissions", "*"));
        final DataObject permissionDO = MDMUtil.getPersistence().get(permissionQuery);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToPermissions"));
        sQuery.setCriteria(new Criteria(new Column("MdAppToPermissions", "APP_ID"), (Object)appId, 0));
        sQuery.addSelectColumn(new Column("MdAppToPermissions", "*"));
        final DataObject dO = DataAccess.get(sQuery);
        for (int i = 0; i < arr.length(); ++i) {
            final Row permissionRow = permissionDO.getRow("AppPermissions", new Criteria(new Column("AppPermissions", "APP_PERMISSION_NAME"), (Object)String.valueOf(arr.get(i)), 0));
            if (permissionRow != null) {
                final Long permissionId = (Long)permissionRow.get("APP_PERMISSION_ID");
                Row row = dO.getRow("MdAppToPermissions", new Criteria(new Column("MdAppToPermissions", "APP_PERMISSION_ID"), (Object)permissionId, 0));
                if (row == null) {
                    row = new Row("MdAppToPermissions");
                    row.set("APP_ID", (Object)appId);
                    row.set("APP_PERMISSION_ID", (Object)permissionId);
                    dO.addRow(row);
                }
            }
        }
        DataAccess.update(dO);
    }
    
    public JSONArray getRequestedPermissionForApp(final Long appId) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToPermissions"));
        sQuery.addJoin(new Join("MdAppToPermissions", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppToPermissions", "AppPermissions", new String[] { "APP_PERMISSION_ID" }, new String[] { "APP_PERMISSION_ID" }, 2));
        sQuery.addJoin(new Join("AppPermissions", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
        final Column permissionGrpNameColumn = new Column("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME");
        final Column permissionGrpIdColumn = new Column("AppPermissionGroups", "APP_PERMISSION_GROUP_ID");
        final Column appIdColumn = new Column("MdAppDetails", "APP_ID");
        final List grpByList = new ArrayList();
        grpByList.add(permissionGrpNameColumn);
        grpByList.add(permissionGrpIdColumn);
        grpByList.add(appIdColumn);
        final List selectList = new ArrayList();
        selectList.add(permissionGrpNameColumn);
        selectList.add(permissionGrpIdColumn);
        selectList.add(appIdColumn);
        final GroupByClause gbc = new GroupByClause(grpByList);
        sQuery.setGroupByClause(gbc);
        sQuery.addSelectColumns(selectList);
        sQuery.setCriteria(new Criteria(appIdColumn, (Object)appId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final JSONArray arr = new JSONArray();
            final Iterator iter = dO.getRows("AppPermissionGroups");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final String permission = (String)row.get("APP_PERMISSION_GROUP_NAME");
                if (!this.isAppPermissionExist(arr, permission)) {
                    final JSONObject permissionJSON = new JSONObject();
                    permissionJSON.put("APP_PERMISSION_GROUP_NAME", row.get("APP_PERMISSION_GROUP_NAME"));
                    permissionJSON.put("APP_PERMISSION_GROUP_ID", row.get("APP_PERMISSION_GROUP_ID"));
                    arr.put((Object)permissionJSON);
                }
            }
            return arr;
        }
        return new JSONArray();
    }
    
    public boolean isPermissionRequested(final Long appId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToPermissions"));
        sQuery.addJoin(new Join("MdAppToPermissions", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Column appIdColumn = new Column("MdAppDetails", "APP_ID");
        sQuery.setCriteria(new Criteria(appIdColumn, (Object)appId, 0));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        return !dO.isEmpty();
    }
    
    private boolean isAppPermissionExist(final JSONArray permissionArray, final String permission) {
        return permissionArray.toString().contains(permission);
    }
    
    public Long getPermissionIdFromPermissionName(final String permissionName) throws Exception {
        return (Long)DBUtil.getValueFromDB("AppPermissions", "APP_PERMISSION_NAME", (Object)permissionName, "APP_PERMISSION_ID");
    }
    
    public Long getPermissionGroupIdFromPermissionName(final String permissionName) throws Exception {
        return (Long)DBUtil.getValueFromDB("AppPermissions", "APP_PERMISSION_NAME", (Object)permissionName, "APP_PERMISSION_GROUP_ID");
    }
    
    public String getPermissionGroupNameFromId(final Long permissionGroupId) throws Exception {
        return (String)DBUtil.getValueFromDB("AppPermissionGroups", "APP_PERMISSION_GROUP_ID", (Object)permissionGroupId, "APP_PERMISSION_GROUP_NAME");
    }
    
    public List<String> getRequestedPermissionListForApp(final Long appId) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToPermissions"));
        sQuery.addJoin(new Join("MdAppToPermissions", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppToPermissions", "AppPermissions", new String[] { "APP_PERMISSION_ID" }, new String[] { "APP_PERMISSION_ID" }, 2));
        sQuery.addJoin(new Join("AppPermissions", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2));
        final Column permissionGrpNameColumn = new Column("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME");
        final Column permissionGrpIdColumn = new Column("AppPermissionGroups", "APP_PERMISSION_GROUP_ID");
        final Column appIdColumn = new Column("MdAppDetails", "APP_ID");
        final List grpByList = new ArrayList();
        grpByList.add(permissionGrpNameColumn);
        grpByList.add(permissionGrpIdColumn);
        grpByList.add(appIdColumn);
        final List selectList = new ArrayList();
        selectList.add(permissionGrpNameColumn);
        selectList.add(permissionGrpIdColumn);
        selectList.add(appIdColumn);
        final GroupByClause gbc = new GroupByClause(grpByList);
        sQuery.setGroupByClause(gbc);
        sQuery.addSelectColumns(selectList);
        sQuery.setCriteria(new Criteria(appIdColumn, (Object)appId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        final Set arr = new HashSet();
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("AppPermissionGroups");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final String permission = (String)row.get("APP_PERMISSION_GROUP_NAME");
                arr.add(row.get("APP_PERMISSION_GROUP_NAME"));
            }
        }
        return new ArrayList<String>(arr);
    }
}
