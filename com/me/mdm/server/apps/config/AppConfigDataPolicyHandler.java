package com.me.mdm.server.apps.config;

import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import org.apache.commons.lang.StringUtils;
import com.me.mdm.api.error.APIHTTPException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.files.FileFacade;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONArray;
import com.adventnet.ds.query.DerivedColumn;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import java.util.ArrayList;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppConfigDataPolicyHandler
{
    public Logger logger;
    
    public AppConfigDataPolicyHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppConfigDataPolicyHandler getInstance(final int platform) {
        AppConfigDataPolicyHandler appConfigHandler = null;
        switch (platform) {
            case 1: {
                appConfigHandler = new IOSAppConfigDataHandler();
                break;
            }
            case 3: {
                appConfigHandler = new WindowsAppConfigDataHandler();
                break;
            }
            default: {
                appConfigHandler = new AndroidAppConfigDataHandler();
                break;
            }
        }
        return appConfigHandler;
    }
    
    public Long addOrUpdateAppConfigPolicy(final Long configDataItemId, final JSONObject appConfigJSON) throws DataAccessException, JSONException, Exception {
        this.logger.log(Level.INFO, "Saving app config policy {0}", configDataItemId);
        if (appConfigJSON.has("AppConfigTemplate") && !appConfigJSON.getJSONObject("AppConfigTemplate").has("APP_ID")) {
            appConfigJSON.getJSONObject("AppConfigTemplate").put("APP_ID", (Object)AppVersionDBUtil.getInstance().getAppIdFromConfigDataItemId(configDataItemId));
        }
        final Long appConfigId = new AppConfigDataHandler().persistAppConfig(appConfigJSON);
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigPolicy"));
        sQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        sQuery.addSelectColumn(new Column("AppConfigPolicy", "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("AppConfigPolicy");
            row.set("CONFIG_DATA_ITEM_ID", (Object)configDataItemId);
            row.set("APP_CONFIG_ID", (Object)appConfigId);
            dO.addRow(row);
        }
        else {
            final Row row = dO.getFirstRow("AppConfigPolicy");
            row.set("APP_CONFIG_ID", (Object)appConfigId);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
        return appConfigId;
    }
    
    public void deleteAppConfig(final Long configDataItemId) throws DataAccessException {
        DataAccess.delete("AppConfigPolicy", new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
    }
    
    public Long addOrUpdateAppConfigPolicyAndInvokeCommand(final Long configDataItemId, final JSONObject appConfigJSON) throws DataAccessException, JSONException, Exception {
        final Long appConfigId = this.addOrUpdateAppConfigPolicy(configDataItemId, appConfigJSON);
        this.addAppConfigCommand(configDataItemId, appConfigJSON);
        return appConfigId;
    }
    
    public void resetAppConfigAndInvokeCommand(final Long configDataItemId, final JSONObject configJSON) throws DataAccessException, Exception {
        DataAccess.delete("AppConfigPolicy", new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        this.resetAppConfigCommand(configDataItemId, configJSON);
    }
    
    public void deleteAppConfigAndInvokeCommand(final Long configDataItemId, final JSONObject configJSON) throws Exception {
        final Long appId = AppVersionDBUtil.getInstance().getAppIdFromConfigDataItemId(configDataItemId);
        final Long appGroupId = configJSON.optLong("APP_GROUP_ID");
        final Long customerId = configJSON.optLong("CUSTOMER_ID");
        new AppConfigDataHandler().deleteAppConfigDetails(configDataItemId, customerId);
        this.deleteAppConfigCommand(configDataItemId, configJSON);
    }
    
    protected ArrayList getAssociatedResourceList(final Long configDataItemId, final Boolean installedInstallingResList) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("Collection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("RecentProfileForResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria cItemId = new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
        final Criteria cMarkedForDelete = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        Criteria criteria = cItemId.and(cMarkedForDelete);
        if (installedInstallingResList) {
            final Criteria cSuccessStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
            final Criteria cInProgressStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)3, 0);
            criteria = criteria.and(cSuccessStatus.or(cInProgressStatus));
        }
        sQuery.setCriteria(criteria);
        final ArrayList resourceList = new ArrayList();
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Iterator it = dO.getRows("RecentProfileForResource");
            while (it.hasNext()) {
                final Row row = it.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                resourceList.add(resourceId);
            }
        }
        return resourceList;
    }
    
    public Long getAppConfigIdForCollectionId(final Long collectionId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Long appConfigId = (Long)dO.getFirstValue("AppConfigPolicy", "APP_CONFIG_ID");
            return appConfigId;
        }
        return -1L;
    }
    
    public String getAppConfigurationForConfigId(final Long configDataItemId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigPolicy"));
        sQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Long appConfigId = (Long)dO.getFirstValue("AppConfigPolicy", "APP_CONFIG_ID");
            return new AppConfigDataHandler().getAppConfig(appConfigId);
        }
        return "";
    }
    
    public String getAppConfigurationForCollectionId(final Long collectionId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)collectionId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            final Long appConfigId = (Long)dO.getFirstValue("AppConfigPolicy", "APP_CONFIG_ID");
            return new AppConfigDataHandler().getAppConfig(appConfigId);
        }
        return "";
    }
    
    private Column getDerivedColumn() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedAppConfigurationPolicy"));
        selectQuery.addSelectColumn(new Column("ManagedAppConfigurationPolicy", "APP_GROUP_ID"));
        final Column derivedTable = (Column)new DerivedColumn("DC", selectQuery);
        return derivedTable;
    }
    
    public JSONArray getAppConfigurationForResource(final Long resourceId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        sQuery.addJoin(new Join("RecentProfileForResource", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("InstallAppPolicy", "APP_GROUP_ID"), (Object)this.getDerivedColumn(), 9);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0));
        sQuery.setCriteria(sQuery.getCriteria().and(criteria));
        sQuery.setDistinct(true);
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)sQuery);
        final JSONArray arr = new JSONArray();
        while (ds.next()) {
            final Long configDataItemId = (Long)ds.getValue("CONFIG_DATA_ITEM_ID");
            final Long appId = (Long)ds.getValue("APP_ID");
            final JSONObject appJSON = new JSONObject();
            appJSON.put("Identifier", (Object)AppsUtil.getInstance().getAppIdentifier(appId));
            final String managedAppConfiguration = this.getAppConfigurationForConfigId(configDataItemId);
            if (!managedAppConfiguration.equals("")) {
                appJSON.put("Configuration", (Object)new JSONArray(managedAppConfiguration));
            }
            else {
                appJSON.put("Configuration", (Object)new JSONArray());
            }
            arr.put((Object)appJSON);
        }
        return arr;
    }
    
    public JSONArray getAppConfigurationsForCollectionID(final Long collectionID, final String command) throws DataAccessException, Exception {
        final JSONArray arr = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedAppConfigurationPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
        selectQuery.addSelectColumn(Column.getColumn("ManagedAppConfigurationPolicy", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("ManagedAppConfigurationPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long configDataItemId = (Long)row.get("CONFIG_DATA_ITEM_ID");
                final Long appId = (Long)row.get("APP_ID");
                final JSONObject appJSON = new JSONObject();
                appJSON.put("Identifier", (Object)AppsUtil.getInstance().getAppIdentifier(appId));
                if (command.equals("InstallApplicationConfiguration")) {
                    final String managedAppConfiguration = this.getAppConfigurationForConfigId(configDataItemId);
                    if (!managedAppConfiguration.equals("")) {
                        appJSON.put("Configuration", (Object)new JSONArray(managedAppConfiguration));
                    }
                    else {
                        appJSON.put("Configuration", (Object)new JSONArray());
                    }
                }
                else {
                    appJSON.put("Configuration", (Object)new JSONArray());
                }
                arr.put((Object)appJSON);
            }
        }
        return arr;
    }
    
    public JSONObject getAppConfiguration(final JSONObject configJSON) throws Exception {
        final Long configDataItem = APIUtil.getResourceID(configJSON, "payloaditem_id");
        final JSONObject configurationJSON = new JSONObject();
        final DataObject dataObject = DataAccess.get("ManagedAppConfigurationPolicy", new Criteria(Column.getColumn("ManagedAppConfigurationPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItem, 0));
        if (!dataObject.isEmpty()) {
            final Row appConfigPolicy = dataObject.getFirstRow("ManagedAppConfigurationPolicy");
            final Long appID = (Long)appConfigPolicy.get("APP_ID");
            final Long appGroupID = (Long)appConfigPolicy.get("APP_GROUP_ID");
            final Long appConfigID = new AppConfigDataHandler().getAppConfigIDFromConfigDataItemID(configDataItem);
            final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
            final JSONObject configTemplate = new JSONObject(appConfigDataHandler.getAppConfigTemplateFromConfigDataItemID(configDataItem));
            JSONArray configData = new JSONArray();
            if (appConfigID != -1L) {
                final String config = appConfigDataHandler.getAppConfig((long)appConfigID);
                if (!MDMStringUtils.isEmpty(config)) {
                    configData = new JSONArray(config);
                }
            }
            final JSONObject appGroupDetails = AppConfigPolicyDBHandler.getInstance().getAppDetailsForAppGroupID(appGroupID);
            configurationJSON.put("APP_CONFIG_FORM", (Object)configTemplate);
            configurationJSON.put("APP_CONFIG_DATA", (Object)configData);
            configurationJSON.put("payloaditem_id", (Object)configDataItem);
            configurationJSON.put("APP_ID", (Object)appID);
            configurationJSON.put("APP_GROUP_ID", (Object)appGroupID);
            configurationJSON.put("app_name", appGroupDetails.get("GROUP_DISPLAY_NAME"));
            configurationJSON.put("IDENTIFIER", appGroupDetails.get("IDENTIFIER"));
        }
        return configurationJSON;
    }
    
    public void populateAppConfigJSON(final JSONObject requestJSON, final JSONObject jsonObject) throws Exception {
        if (requestJSON.has("config_file")) {
            final FileFacade fileFacade = new FileFacade();
            final String appConfigFilePath = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(requestJSON.get("config_file").toString()))).get("file_path"));
            final String tempappConfigFilePath = fileFacade.getTempLocation(appConfigFilePath);
            new FileFacade().writeFile(tempappConfigFilePath, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(appConfigFilePath));
            FileInputStream stream = null;
            JSONObject appConfigjson = new JSONObject();
            try {
                stream = new FileInputStream(new File(tempappConfigFilePath));
                appConfigjson = new AppConfigDataHandler().parseIosAppConfig(stream);
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
                if (tempappConfigFilePath != null) {
                    fileFacade.deleteFile(tempappConfigFilePath);
                }
            }
            if (appConfigjson.has("Error")) {
                throw new APIHTTPException(400, "Invalid Managed App cnfiguration", new Object[0]);
            }
            jsonObject.put("APP_CONFIG_TEMPLATE", (Object)appConfigjson);
            final JSONArray restrictions = appConfigjson.getJSONObject("APP_CONFIG_FORM").getJSONArray("restrictions");
            final JSONArray appConfiguration = new JSONArray();
            for (int i = 0; i < restrictions.length(); ++i) {
                final JSONObject json = new JSONObject();
                final String type = String.valueOf(restrictions.getJSONObject(i).get("restrictionType"));
                json.put("type", (Object)type);
                json.put("key", (Object)String.valueOf(restrictions.getJSONObject(i).get("key")));
                json.put("value", (Object)String.valueOf(restrictions.getJSONObject(i).getJSONObject("defaultValue").get("value" + StringUtils.capitalize(type))));
                appConfiguration.put((Object)json);
            }
            jsonObject.put("APP_CONFIGURATION", (Object)appConfiguration.toString());
            jsonObject.put("configFile", (Object)appConfigFilePath);
        }
        if (requestJSON.has("app_configuration")) {
            final JSONArray appconfig = requestJSON.getJSONArray("app_configuration");
            if (appconfig.length() > 0) {
                jsonObject.put("APP_CONFIGURATION", (Object)appconfig.toString());
                jsonObject.put("APP_CONFIG_TEMPLATE", (Object)WpAppSettingsHandler.getInstance().createTemplateForConfigValues(appconfig));
            }
        }
    }
    
    public JSONObject addAppConfiguration(final JSONObject configJSON) throws Exception {
        return null;
    }
    
    public JSONObject modifyAppConfiguration(final JSONObject configJSON) throws Exception {
        return null;
    }
    
    public void addAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
    }
    
    public void deleteAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
    }
    
    public void resetAppConfigCommand(final Long configDataItemId, final JSONObject appConfigJSON) {
    }
}
