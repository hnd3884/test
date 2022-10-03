package com.me.mdm.server.apps.permission.config;

import com.me.mdm.server.apps.permission.PermissionHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.AppDelegateScopeManagement.AppDelegateScopeHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONArray;
import java.util.Iterator;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;

public class PermissionConfigDataPolicyHandler
{
    public Long addOrUpdateAppConfigPolicyAndInvokeCommand(final Long configDataItemId, final JSONObject appConfigJSON) throws DataAccessException, JSONException, Exception {
        final Long permissionConfigId = this.addOrUpdateAppPermissionPolicy(configDataItemId, appConfigJSON);
        this.invokeCommandForAssociatedApp(configDataItemId);
        return permissionConfigId;
    }
    
    private Long addOrUpdateAppPermissionPolicy(final Long configDataItemId, final JSONObject permissionConfigJSON) throws DataAccessException, JSONException {
        final Long permissionConfigId = new PermissionConfigDataHandler().addOrUpdatePermissionConfig(permissionConfigJSON);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppPermissionPolicy"));
        sQuery.setCriteria(new Criteria(new Column("AppPermissionPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        sQuery.addSelectColumn(new Column("AppPermissionPolicy", "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("AppPermissionPolicy");
            row.set("CONFIG_DATA_ITEM_ID", (Object)configDataItemId);
            row.set("PERMISSION_CONFIG_ID", (Object)permissionConfigId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("AppPermissionPolicy");
            row.set("PERMISSION_CONFIG_ID", (Object)permissionConfigId);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
        return permissionConfigId;
    }
    
    private void invokeCommandForAssociatedApp(final Long configDataItemId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppPermissionPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("Collection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        final ArrayList resourceList = new ArrayList();
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("RecentProfileForResource");
            while (it.hasNext()) {
                final Row row = it.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                resourceList.add(resourceId);
            }
            DeviceCommandRepository.getInstance().addAppPermissionPolicyCommand(resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
    }
    
    public Long getAppPermissionConfigId(final Long configDataItemId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppPermissionPolicy"));
        sQuery.setCriteria(new Criteria(new Column("AppPermissionPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        sQuery.addSelectColumn(new Column("AppPermissionPolicy", "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            return (Long)dO.getFirstValue("AppPermissionPolicy", "PERMISSION_CONFIG_ID");
        }
        return -1L;
    }
    
    public Long getAppPermissionConfigIdForCollectionId(final Long collectionId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppPermissionPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Long appConfigId = (Long)dO.getFirstValue("AppPermissionPolicy", "PERMISSION_CONFIG_ID");
            return appConfigId;
        }
        return -1L;
    }
    
    public JSONObject getAppPermissionConif(final Long configDataItemId) throws DataAccessException, JSONException, Exception {
        final JSONObject appPermissionJSON = new JSONObject();
        final Long permissionConfigId = this.getAppPermissionConfigId(configDataItemId);
        JSONArray permissionConfig = new JSONArray();
        if (permissionConfigId != -1L) {
            permissionConfig = new PermissionConfigDataHandler().getAppPermissionConfig(permissionConfigId);
        }
        appPermissionJSON.put("APP_PERMISSION_CONFIG_ID", (Object)permissionConfigId.toString());
        appPermissionJSON.put("AppPermissionConfigDetails", (Object)permissionConfig);
        return appPermissionJSON;
    }
    
    public JSONArray getAppPermissionConfigForResource(final Long resourceId) throws DataAccessException, JSONException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        sQuery.addJoin(new Join("RecentProfileForResource", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final JSONArray arr = new JSONArray();
            final Iterator it = dO.getRows("InstallAppPolicy");
            while (it.hasNext()) {
                final Row row = it.next();
                final Long configDataItemId = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Long appId = (Long)row.get("APP_ID");
                JSONObject appJSON = new JSONObject();
                appJSON.put("Identifier", (Object)AppsUtil.getInstance().getAppIdentifier(appId));
                final JSONArray permissionConfigArr = new PermissionConfigDataHandler().getConfiguredAppPermissionDetails(this.getAppPermissionConfigId(configDataItemId));
                appJSON = this.constructPermissionConfigJSON(permissionConfigArr, appJSON);
                final AppDelegateScopeHandler appDelegateScopeHandler = new AppDelegateScopeHandler();
                appDelegateScopeHandler.constructDelegateScopeConfigJSON(appJSON, configDataItemId);
                arr.put((Object)appJSON);
            }
            return arr;
        }
        return new JSONArray();
    }
    
    private JSONObject constructPermissionConfigJSON(final JSONArray permissionConfigArr, final JSONObject appJSON) throws JSONException {
        final JSONArray grantedPermissionArr = new JSONArray();
        final JSONArray deniedPermissionArr = new JSONArray();
        final JSONArray userControlledPermissionArr = new JSONArray();
        final JSONArray grantedPermissionGroupArr = new JSONArray();
        final JSONArray deniedPermissionGroupArr = new JSONArray();
        final JSONArray userControlledPermissionGroupArr = new JSONArray();
        for (int i = 0; i < permissionConfigArr.length(); ++i) {
            final JSONObject permissionJSON = permissionConfigArr.getJSONObject(i);
            final int grantState = permissionJSON.getInt("APP_PERMISSION_GRANT_STATE");
            final JSONArray permissions = permissionJSON.getJSONArray("AppPermissions");
            if (grantState == PermissionConfigDataHandler.APP_PERMISSION_GRANT_STATE_ALLOWED) {
                grantedPermissionGroupArr.put((Object)String.valueOf(permissionJSON.get("APP_PERMISSION_GROUP_NAME")));
                for (int j = 0; j < permissions.length(); ++j) {
                    grantedPermissionArr.put(permissions.get(j));
                }
            }
            else if (grantState == PermissionConfigDataHandler.APP_PERMISSION_GRANT_STATE_DENYED) {
                deniedPermissionGroupArr.put((Object)String.valueOf(permissionJSON.get("APP_PERMISSION_GROUP_NAME")));
                for (int j = 0; j < permissions.length(); ++j) {
                    deniedPermissionArr.put(permissions.get(j));
                }
            }
            else if (grantState == PermissionConfigDataHandler.APP_PERMISSION_GRANT_STATE_USER_CONTROLLED) {
                userControlledPermissionGroupArr.put((Object)String.valueOf(permissionJSON.get("APP_PERMISSION_GROUP_NAME")));
                for (int j = 0; j < permissions.length(); ++j) {
                    userControlledPermissionArr.put(permissions.get(j));
                }
            }
        }
        final JSONObject permissionPolicy = new JSONObject();
        permissionPolicy.put("GrantedPermissions", (Object)grantedPermissionGroupArr);
        permissionPolicy.put("DeniedPermissions", (Object)deniedPermissionGroupArr);
        permissionPolicy.put("UserPromptPermissions", (Object)userControlledPermissionGroupArr);
        final JSONObject individualPermissionPolicy = new JSONObject();
        individualPermissionPolicy.put("GrantedPermissions", (Object)grantedPermissionArr);
        individualPermissionPolicy.put("DeniedPermissions", (Object)deniedPermissionArr);
        individualPermissionPolicy.put("UserPromptPermissions", (Object)userControlledPermissionArr);
        appJSON.put("PermissionPolicy", (Object)permissionPolicy);
        appJSON.put("IndividualPermissionPolicy", (Object)individualPermissionPolicy);
        return appJSON;
    }
    
    public void cloneAppPermssionData(final Long oldCollectionId, final Long newCollectionId) throws Exception {
        final JSONObject appPermissionJSON = this.getAppPermissionConfigFromCollectionId(oldCollectionId);
        if (appPermissionJSON.has("AppPermissionConfigDetails") && appPermissionJSON.getJSONArray("AppPermissionConfigDetails").length() > 0) {
            final Long configDataItemId = this.getInstallAppConfigIdForCollectionId(newCollectionId);
            this.addOrUpdateAppConfigPolicyAndInvokeCommand(configDataItemId, appPermissionJSON);
        }
    }
    
    private JSONObject getAppPermissionConfigFromCollectionId(final Long collectionId) throws DataAccessException, JSONException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppPermissionPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("AppPermissionPolicy", "AppPermissionConfig", new String[] { "PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        sQuery.addJoin(new Join("AppPermissionConfig", "AppPermissionConfigDetails", new String[] { "APP_PERMISSION_CONFIG_ID" }, new String[] { "APP_PERMISSION_CONFIG_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        sQuery.setCriteria(new Criteria(Column.getColumn("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        final JSONArray appPermissionConfig = new JSONArray();
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
        final JSONObject appPermissionJSON = new JSONObject();
        appPermissionJSON.put("AppPermissionConfigDetails", (Object)appPermissionConfig);
        return appPermissionJSON;
    }
    
    private Long getInstallAppConfigIdForCollectionId(final Long collectionId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Long configDataItemId = (Long)dO.getFirstValue("InstallAppPolicy", "CONFIG_DATA_ITEM_ID");
            return configDataItemId;
        }
        return -1L;
    }
}
