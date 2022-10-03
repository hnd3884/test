package com.me.mdm.server.apps.permission.config;

import com.me.mdm.server.apps.permission.PermissionHandler;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;

public class PermissionConfigDataHandler
{
    public static final Integer APP_PERMISSION_GRANT_STATE_ALLOWED;
    public static final Integer APP_PERMISSION_GRANT_STATE_USER_CONTROLLED;
    public static final Integer APP_PERMISSION_GRANT_STATE_DENYED;
    
    public Long addOrUpdatePermissionConfig(final JSONObject permissionConfig) throws DataAccessException, JSONException {
        final String sPermissionConfigID = permissionConfig.optString("APP_PERMISSION_CONFIG_ID", "-1");
        Long permissionConfigId = Long.parseLong(sPermissionConfigID);
        if (permissionConfigId == -1L) {
            DataObject dO = (DataObject)new WritableDataObject();
            final Row row = new Row("AppPermissionConfig");
            dO.addRow(row);
            dO = DataAccess.add(dO);
            permissionConfigId = (Long)dO.getFirstValue("AppPermissionConfig", "APP_PERMISSION_CONFIG_ID");
        }
        final JSONArray permissionConfigDetailsArr = permissionConfig.getJSONArray("AppPermissionConfigDetails");
        this.addOrUpdatePermissionConfigDetails(permissionConfigId, permissionConfigDetailsArr);
        return permissionConfigId;
    }
    
    private void addOrUpdatePermissionConfigDetails(final Long permissionConfigId, final JSONArray permissionConfigDetails) throws JSONException, DataAccessException {
        final Criteria permissionConfigIdCriteria = new Criteria(new Column("AppPermissionConfigDetails", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigId, 0);
        for (int i = 0; i < permissionConfigDetails.length(); ++i) {
            final JSONObject configDetails = permissionConfigDetails.getJSONObject(i);
            final Long permissionGroupId = configDetails.getLong("APP_PERMISSION_GROUP_ID");
            final Integer permissionGrantState = configDetails.getInt("APP_PERMISSION_GRANT_STATE");
            final Integer choice = configDetails.getInt("CONFIG_CHOICE");
            final JSONArray appList = configDetails.optJSONArray("PermissionConfigAppList");
            final Criteria permissionGroupIdCriteria = new Criteria(new Column("AppPermissionConfigDetails", "APP_PERMISSION_GROUP_ID"), (Object)permissionGroupId, 0);
            DataObject dO = DataAccess.get("AppPermissionConfigDetails", permissionGroupIdCriteria.and(permissionConfigIdCriteria));
            if (dO.isEmpty()) {
                final Row row = new Row("AppPermissionConfigDetails");
                row.set("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigId);
                row.set("APP_PERMISSION_GROUP_ID", (Object)permissionGroupId);
                row.set("APP_PERMISSION_GRANT_STATE", (Object)permissionGrantState);
                row.set("CONFIG_CHOICE", (Object)choice);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getFirstRow("AppPermissionConfigDetails");
                row.set("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigId);
                row.set("APP_PERMISSION_GROUP_ID", (Object)permissionGroupId);
                row.set("APP_PERMISSION_GRANT_STATE", (Object)permissionGrantState);
                row.set("CONFIG_CHOICE", (Object)choice);
                dO.updateRow(row);
            }
            dO = DataAccess.update(dO);
            if (appList != null && appList.length() > 0) {
                final Long permissionConfigDetailsId = (Long)dO.getFirstValue("AppPermissionConfigDetails", "APP_PERMISSION_CONFIG_DTLS_ID");
                this.addOrUpdatePermissionConfigAppList(permissionConfigDetailsId, appList);
            }
        }
    }
    
    private void addOrUpdatePermissionConfigAppList(final Long permissonConfigDetailsId, final JSONArray appList) throws DataAccessException, JSONException {
        final Criteria permissionConfigDetailsIdCriteria = new Criteria(new Column("PermissionConfigAppList", "APP_PERMISSION_CONFIG_DTLS_ID"), (Object)permissonConfigDetailsId, 0);
        DataAccess.delete(permissionConfigDetailsIdCriteria);
        final DataObject dO = (DataObject)new WritableDataObject();
        for (int i = 0; i < appList.length(); ++i) {
            final Row row = new Row("PermissionConfigAppList");
            row.set("APP_PERMISSION_CONFIG_DTLS_ID", (Object)permissonConfigDetailsId);
            row.set("APP_GROUP_ID", appList.get(i));
            dO.addRow(row);
        }
        DataAccess.add(dO);
    }
    
    public JSONArray getConfiguredAppPermissionDetails(final Long permissionConfigId) throws Exception {
        final JSONArray appPermissionConfig = new JSONArray();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppPermissionConfig"));
        final Join configDetailsJoin = new Join("AppPermissionConfig", "AppPermissionConfigDetails", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2);
        final Join appPermissionGroupJoin = new Join("AppPermissionConfigDetails", "AppPermissionGroups", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2);
        final Join appPermissionJoin = new Join("AppPermissionGroups", "AppPermissions", new String[] { "APP_PERMISSION_GROUP_ID" }, new String[] { "APP_PERMISSION_GROUP_ID" }, 2);
        sQuery.addJoin(configDetailsJoin);
        sQuery.addJoin(appPermissionGroupJoin);
        sQuery.addJoin(appPermissionJoin);
        sQuery.addJoin(new Join("AppPermissionConfigDetails", "PermissionConfigAppList", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 1));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("AppPermissionConfig", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("AppPermissionConfigDetails");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final JSONObject permissionJSON = new JSONObject();
                final Long appPermissionGroupId = (Long)row.get("APP_PERMISSION_GROUP_ID");
                Criteria permissionGroupC = new Criteria(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"), (Object)appPermissionGroupId, 0);
                final Row permissionGroupRow = dO.getRow("AppPermissionGroups", permissionGroupC);
                permissionGroupC = new Criteria(Column.getColumn("AppPermissions", "APP_PERMISSION_GROUP_ID"), (Object)appPermissionGroupId, 0);
                final Iterator permissionsRowItr = dO.getRows("AppPermissions", permissionGroupC);
                final JSONArray permissionsForGroup = new JSONArray();
                while (permissionsRowItr.hasNext()) {
                    final Row permissionRow = permissionsRowItr.next();
                    permissionsForGroup.put(permissionRow.get("APP_PERMISSION_NAME"));
                }
                permissionJSON.put("AppPermissions", (Object)permissionsForGroup);
                permissionJSON.put("APP_PERMISSION_GROUP_NAME", permissionGroupRow.get("APP_PERMISSION_GROUP_NAME"));
                permissionJSON.put("APP_PERMISSION_GROUP_ID", (Object)appPermissionGroupId);
                permissionJSON.put("APP_PERMISSION_GRANT_STATE", row.get("APP_PERMISSION_GRANT_STATE"));
                permissionJSON.put("CONFIG_CHOICE", row.get("CONFIG_CHOICE"));
                final Iterator appIter = dO.getRows("PermissionConfigAppList", new Criteria(new Column("PermissionConfigAppList", "APP_PERMISSION_CONFIG_DTLS_ID"), row.get("APP_PERMISSION_CONFIG_DTLS_ID"), 0));
                final JSONArray appArr = new JSONArray();
                while (appIter.hasNext()) {
                    final Row appListRow = appIter.next();
                    appArr.put(appListRow.get("APP_GROUP_ID"));
                }
                permissionJSON.put("PermissionConfigAppList", (Object)appArr);
                appPermissionConfig.put((Object)permissionJSON);
            }
        }
        return appPermissionConfig;
    }
    
    public JSONArray getAppPermissionConfig(final Long permissionConfigId) throws DataAccessException, JSONException, Exception {
        final JSONArray appPermissionConfig = new JSONArray();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppPermissionConfig"));
        sQuery.addJoin(new Join("AppPermissionConfig", "AppPermissionConfigDetails", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        sQuery.addJoin(new Join("AppPermissionConfigDetails", "PermissionConfigAppList", new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, new String[] { "APP_PERMISSION_CONFIG_DTLS_ID" }, 1));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("AppPermissionConfig", "APP_PERMISSION_CONFIG_ID"), (Object)permissionConfigId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("AppPermissionConfigDetails");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final JSONObject permissionJSON = new JSONObject();
                permissionJSON.put("APP_PERMISSION_GROUP_ID", row.get("APP_PERMISSION_GROUP_ID"));
                permissionJSON.put("APP_PERMISSION_GROUP_NAME", (Object)new PermissionHandler().getPermissionGroupNameFromId((Long)row.get("APP_PERMISSION_GROUP_ID")));
                permissionJSON.put("APP_PERMISSION_GRANT_STATE", row.get("APP_PERMISSION_GRANT_STATE"));
                permissionJSON.put("CONFIG_CHOICE", row.get("CONFIG_CHOICE"));
                final Iterator appIter = dO.getRows("PermissionConfigAppList", new Criteria(new Column("PermissionConfigAppList", "APP_PERMISSION_CONFIG_DTLS_ID"), row.get("APP_PERMISSION_CONFIG_DTLS_ID"), 0));
                final JSONArray appArr = new JSONArray();
                while (appIter.hasNext()) {
                    final Row appListRow = appIter.next();
                    appArr.put(appListRow.get("APP_GROUP_ID"));
                }
                permissionJSON.put("PermissionConfigAppList", (Object)appArr);
                appPermissionConfig.put((Object)permissionJSON);
            }
        }
        return appPermissionConfig;
    }
    
    public Integer getAppPermisionGroupIdFromName(final String permissionName) {
        Integer permissionID = null;
        if (permissionName.equalsIgnoreCase("android.permission-group.CALENDAR")) {
            permissionID = 1;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.CAMERA")) {
            permissionID = 2;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.CONTACTS")) {
            permissionID = 3;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.LOCATION")) {
            permissionID = 4;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.MICROPHONE")) {
            permissionID = 5;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.PHONE")) {
            permissionID = 6;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.SENSORS")) {
            permissionID = 7;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.SMS")) {
            permissionID = 8;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.STORAGE")) {
            permissionID = 9;
        }
        else if (permissionName.equalsIgnoreCase("android.permission-group.CALL_LOG")) {
            permissionID = 10;
        }
        return permissionID;
    }
    
    static {
        APP_PERMISSION_GRANT_STATE_ALLOWED = 1;
        APP_PERMISSION_GRANT_STATE_USER_CONTROLLED = 2;
        APP_PERMISSION_GRANT_STATE_DENYED = 3;
    }
}
