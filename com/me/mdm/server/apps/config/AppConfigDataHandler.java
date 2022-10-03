package com.me.mdm.server.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.dd.plist.XMLPropertyListParser;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.dd.plist.NSObject;
import com.dd.plist.NSNumber;
import com.dd.plist.NSString;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSDictionary;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppConfigDataHandler
{
    public static final Integer APP_CONFIG_TYPE_RAW;
    public static final Integer APP_CONFIG_TYPE_CUSTOMER_BUILT;
    public static final Integer APP_CONFIG_TYPE_FROM_BUSINESS_STORE;
    public Logger logger;
    
    public AppConfigDataHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public Long persistAppConfig(final JSONObject dataJSON) throws JSONException, DataAccessException, Exception {
        final JSONObject appConfigJSON = dataJSON.getJSONObject("ManagedAppConfiguration");
        if (appConfigJSON.optLong("APP_CONFIG_TEMPLATE_ID", -1L) == -1L) {
            final JSONObject appConfigTemplateJSON = dataJSON.getJSONObject("AppConfigTemplate");
            final Long templateId = this.addOrUpdateAppConfigTemplate(appConfigTemplateJSON);
            appConfigJSON.put("APP_CONFIG_TEMPLATE_ID", (Object)templateId);
        }
        else {
            final JSONObject appConfigTemplateJSON = dataJSON.optJSONObject("AppConfigTemplate");
            if (appConfigTemplateJSON != null) {
                final JSONObject appConfigTemplateExtnJSON = appConfigTemplateJSON.getJSONObject("AppConfigTemplateExtn");
                appConfigTemplateExtnJSON.put("APP_CONFIG_TEMPLATE_ID", appConfigJSON.optLong("APP_CONFIG_TEMPLATE_ID", -1L));
                this.addorUpdateAppConfigTemplateExtn(appConfigTemplateExtnJSON);
            }
        }
        return this.addOrUpdateAppConfig(appConfigJSON);
    }
    
    public JSONObject getAppConfigDetails(final Long appConfigId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedAppConfiguration"));
        sQuery.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("ManagedAppConfiguration", "APP_CONFIG_ID"), (Object)appConfigId, 0));
        JSONObject appConfigDetails = null;
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            appConfigDetails = new JSONObject();
            String filePath = (String)dO.getFirstValue("ManagedAppConfigurationData", "APP_CONFIG_PATH");
            filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
            appConfigDetails.put("APP_CONFIG", (Object)new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath)));
            final Long userId = (Long)dO.getFirstValue("ManagedAppConfiguration", "LAST_MODIFIED_BY");
            appConfigDetails.put("LAST_MODIFIED_BY", (Object)userId);
        }
        return appConfigDetails;
    }
    
    public String getAppConfig(final Long appConfigId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedAppConfiguration"));
        sQuery.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.setCriteria(new Criteria(new Column("ManagedAppConfiguration", "APP_CONFIG_ID"), (Object)appConfigId, 0));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            String filePath = (String)dO.getFirstValue("ManagedAppConfigurationData", "APP_CONFIG_PATH");
            filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
            return new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
        }
        return "";
    }
    
    public Long addOrUpdateAppConfig(final JSONObject appConfigJSON) throws JSONException, DataAccessException, Exception {
        final Long templateId = appConfigJSON.optLong("APP_CONFIG_TEMPLATE_ID", -1L);
        Long appConfigId = appConfigJSON.optLong("APP_CONFIG_ID", -1L);
        final String configName = appConfigJSON.optString("APP_CONFIG_NAME");
        final Long userId = appConfigJSON.getLong("LAST_MODIFIED_BY");
        if (appConfigId == -1L) {
            DataObject dO = (DataObject)new WritableDataObject();
            final Row row = new Row("ManagedAppConfiguration");
            row.set("APP_CONFIG_NAME", (Object)configName);
            row.set("APP_CONFIG_TEMPLATE_ID", (Object)templateId);
            row.set("CREATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("LAST_MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("LAST_MODIFIED_BY", (Object)userId);
            dO.addRow(row);
            dO = DataAccess.add(dO);
            appConfigId = (Long)dO.getFirstValue("ManagedAppConfiguration", "APP_CONFIG_ID");
        }
        else {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("ManagedAppConfiguration");
            uQuery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            uQuery.setUpdateColumn("LAST_MODIFIED_BY", (Object)userId);
            uQuery.setCriteria(new Criteria(new Column("ManagedAppConfiguration", "APP_CONFIG_ID"), (Object)appConfigId, 0));
            DataAccess.update(uQuery);
        }
        final JSONObject appConfigExtnJSON = appConfigJSON.getJSONObject("ManagedAppConfigurationData");
        appConfigExtnJSON.put("APP_CONFIG_ID", (Object)appConfigId);
        this.addOrUpdateAppConfigExtn(appConfigExtnJSON);
        return appConfigId;
    }
    
    private void addOrUpdateAppConfigExtn(final JSONObject appConfigExtnJSON) throws JSONException, DataAccessException, Exception {
        final Long appConfigId = appConfigExtnJSON.optLong("APP_CONFIG_ID", -1L);
        final String managedAppConfiguration = appConfigExtnJSON.optString("APP_CONFIG");
        final String fullFilePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("managedappconfiguration") + File.separator + appConfigExtnJSON.getLong("CUSTOMER_ID") + File.separator + appConfigId.toString() + File.separator + "app_config.json";
        final String filePath = "mdm" + File.separator + "managedappconfiguration" + File.separator + appConfigExtnJSON.getLong("CUSTOMER_ID") + File.separator + appConfigId.toString() + File.separator + "app_config.json";
        ApiFactoryProvider.getFileAccessAPI().writeFile(fullFilePath, managedAppConfiguration.getBytes());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedAppConfigurationData"));
        sQuery.setCriteria(new Criteria(new Column("ManagedAppConfigurationData", "APP_CONFIG_ID"), (Object)appConfigId, 0));
        sQuery.addSelectColumn(new Column("ManagedAppConfigurationData", "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("ManagedAppConfigurationData");
            row.set("APP_CONFIG_ID", (Object)appConfigId);
            row.set("APP_CONFIG_PATH", (Object)filePath);
            dO.addRow(row);
            DataAccess.add(dO);
        }
    }
    
    public Long addOrUpdateAppConfigTemplate(final JSONObject templateJSON) throws JSONException, DataAccessException, Exception {
        final Long appGroupId = JSONUtil.optLongForUVH(templateJSON, "APP_GROUP_ID", (Long)null);
        final Long appId = JSONUtil.optLongForUVH(templateJSON, "APP_ID", (Long)null);
        final Long appConfigTemplateId = templateJSON.optLong("APP_CONFIG_TEMPLATE_ID", -1L);
        final Integer templateType = templateJSON.getInt("APP_CONFIG_TEMPLATE_TYPE");
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigTemplate"));
        sQuery.addSelectColumn(new Column("AppConfigTemplate", "*"));
        if (appConfigTemplateId != -1L) {
            sQuery.setCriteria(new Criteria(new Column("AppConfigTemplate", "APP_CONFIG_TEMPLATE_ID"), (Object)appConfigTemplateId, 0));
        }
        DataObject dO = DataAccess.get(sQuery);
        if (appConfigTemplateId == -1L) {
            dO = (DataObject)new WritableDataObject();
            final Row row = new Row("AppConfigTemplate");
            row.set("APP_CONFIG_TEMPLATE_TYPE", (Object)templateType);
            row.set("APP_GROUP_ID", (Object)appGroupId);
            row.set("APP_ID", (Object)appId);
            dO.addRow(row);
            dO = DataAccess.add(dO);
        }
        if (templateJSON.has("AppConfigTemplateExtn")) {
            final JSONObject appConfigTemplateExtnJSON = templateJSON.getJSONObject("AppConfigTemplateExtn");
            appConfigTemplateExtnJSON.put("APP_CONFIG_TEMPLATE_ID", (Object)dO.getFirstValue("AppConfigTemplate", "APP_CONFIG_TEMPLATE_ID"));
            this.addorUpdateAppConfigTemplateExtn(appConfigTemplateExtnJSON);
        }
        return (Long)dO.getFirstValue("AppConfigTemplate", "APP_CONFIG_TEMPLATE_ID");
    }
    
    public boolean isAppConfigTemplateExist(final Long appId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigTemplate"));
        sQuery.addSelectColumn(new Column("AppConfigTemplate", "*"));
        final Criteria appCriteria = new Criteria(new Column("AppConfigTemplate", "APP_ID"), (Object)appId, 0);
        sQuery.setCriteria(appCriteria);
        final DataObject dO = DataAccess.get(sQuery);
        return !dO.isEmpty();
    }
    
    public Long getAppConfigTemplateIDFromAppId(final Long appId) throws DataAccessException {
        Long appConfigTemplateID = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigTemplate"));
        selectQuery.setCriteria(new Criteria(new Column("AppConfigTemplate", "APP_ID"), (Object)appId, 0));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("AppConfigTemplate");
            appConfigTemplateID = (Long)row.get("APP_CONFIG_TEMPLATE_ID");
        }
        return appConfigTemplateID;
    }
    
    public Long getAppConfigTemplateIDFromConfigDataItemID(final Long configDataItemID) throws DataAccessException {
        Long appConfigTemplateID = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigPolicy"));
        selectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
        selectQuery.addSelectColumn(new Column("ManagedAppConfiguration", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ManagedAppConfiguration");
            appConfigTemplateID = (Long)row.get("APP_CONFIG_TEMPLATE_ID");
        }
        return appConfigTemplateID;
    }
    
    public String getAppConfigTemplate(final Long appId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigTemplate"));
        sQuery.addJoin(new Join("AppConfigTemplate", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 2));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria appCriteria = new Criteria(new Column("AppConfigTemplate", "APP_ID"), (Object)appId, 0);
        sQuery.setCriteria(appCriteria);
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            String filePath = (String)dO.getFirstValue("AppConfigTemplateExtn", "APP_CONFIG_TEMPLATE_PATH");
            filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
            return new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
        }
        throw new RuntimeException("AppConfigTemplate is not defined for the App Id " + appId);
    }
    
    public String getAppConfigTemplateFromConfigDataItemID(final Long configDataItemId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigPolicy"));
        selectQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedAppConfiguration", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        selectQuery.addSelectColumn(new Column("AppConfigTemplateExtn", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            String filepath = (String)dataObject.getFirstValue("AppConfigTemplateExtn", "APP_CONFIG_TEMPLATE_PATH");
            filepath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filepath;
            return new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filepath));
        }
        throw new RuntimeException("AppConfigTemplate is not defined for the Config Data Item ID " + configDataItemId);
    }
    
    private void addorUpdateAppConfigTemplateExtn(final JSONObject templateExtnJSON) throws JSONException, DataAccessException, Exception {
        final Long templateId = templateExtnJSON.getLong("APP_CONFIG_TEMPLATE_ID");
        final String templateConfig = templateExtnJSON.optString("APP_CONFIG_TEMPLATE");
        final String fullFilePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataBasePath("appconfigtemplate") + File.separator + templateExtnJSON.getLong("CUSTOMER_ID") + File.separator + templateId.toString() + File.separator + "app_config_template.json";
        final String filePath = "mdm" + File.separator + "appconfigtemplate" + File.separator + templateExtnJSON.getLong("CUSTOMER_ID") + File.separator + templateId.toString() + File.separator + "app_config_template.json";
        ApiFactoryProvider.getFileAccessAPI().writeFile(fullFilePath, templateConfig.getBytes());
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigTemplateExtn"));
        sQuery.addSelectColumn(new Column("AppConfigTemplateExtn", "*"));
        final Criteria templateCriteria = new Criteria(new Column("AppConfigTemplateExtn", "APP_CONFIG_TEMPLATE_ID"), (Object)templateId, 0);
        sQuery.setCriteria(templateCriteria);
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("AppConfigTemplateExtn");
            row.set("APP_CONFIG_TEMPLATE_ID", (Object)templateId);
            row.set("APP_CONFIG_TEMPLATE_PATH", (Object)filePath);
            dO.addRow(row);
            DataAccess.add(dO);
        }
    }
    
    public JSONObject parseIosAppConfig(final InputStream inputStream) throws Exception {
        final JSONObject configJson = new JSONObject();
        try {
            final NSDictionary configDic = (NSDictionary)DMSecurityUtil.parsePropertyList(inputStream);
            final JSONArray configJSONArray = new JSONArray();
            for (int i = 0; i < configDic.allKeys().length; ++i) {
                final JSONObject configJsonTemp = new JSONObject();
                final String key = configDic.allKeys()[i];
                configJsonTemp.put("key", (Object)key);
                configJsonTemp.put("title", (Object)key);
                final Object value = configDic.get((Object)key);
                final JSONObject defaultJson = new JSONObject();
                if (value instanceof NSString) {
                    configJsonTemp.put("restrictionType", (Object)"string");
                    defaultJson.put("type", (Object)"string");
                    defaultJson.put("valueString", value);
                }
                else if (value instanceof NSNumber) {
                    if (value.toString().equalsIgnoreCase("false") || value.toString().equalsIgnoreCase("true")) {
                        configJsonTemp.put("restrictionType", (Object)"bool");
                        defaultJson.put("type", (Object)"bool");
                        defaultJson.put("valueBool", value);
                    }
                    else {
                        configJsonTemp.put("restrictionType", (Object)"integer");
                        defaultJson.put("type", (Object)"integer");
                        defaultJson.put("valueInteger", value);
                    }
                }
                else {
                    configJsonTemp.put("restrictionType", (Object)value.getClass().getName());
                    defaultJson.put("type", (Object)value.getClass().getName());
                    defaultJson.put("valueOther", (Object)((NSObject)value).toXMLPropertyList());
                }
                configJsonTemp.put("defaultValue", (Object)defaultJson);
                configJSONArray.put((Object)configJsonTemp);
            }
            configJson.put("APP_CONFIG_FORM", (Object)new JSONObject().put("restrictions", (Object)configJSONArray));
        }
        catch (final Exception e) {
            configJson.put("Error", (Object)I18N.getMsg("dc.mdm.app.app_config_invalid_file", new Object[0]));
            this.logger.log(Level.SEVERE, "Exception in uploadIosAppConfigFile ", e);
        }
        return configJson;
    }
    
    public void saveAppConfigData(final JSONObject appJson, final Boolean shouldAssignCommand) throws Exception {
        final JSONObject restrictionSchema = new JSONObject();
        final JSONObject managedAppConfiguration = new JSONObject();
        final long appConfigId = appJson.optLong("APP_CONFIG_ID", -1L);
        final long customerId = appJson.getLong("CUSTOMER_ID");
        final long userId = appJson.getLong("PACKAGE_MODIFIED_BY");
        final int platform = appJson.getInt("PLATFORM_TYPE");
        if (appConfigId != -1L) {
            managedAppConfiguration.put("APP_CONFIG_ID", appConfigId);
        }
        final long appConfTempId = appJson.optLong("APP_CONFIG_TEMPLATE_ID", -1L);
        if (appConfTempId != -1L) {
            managedAppConfiguration.put("APP_CONFIG_TEMPLATE_ID", appConfTempId);
        }
        final String configTemplateStr = String.valueOf(appJson.get("APP_CONFIG_TEMPLATE"));
        final JSONObject configTemplateJson = new JSONObject(configTemplateStr);
        final JSONObject configTemplate = new JSONObject();
        final int appConfigTemplateType = configTemplateJson.optInt("APP_CONFIG_TEMPLATE_TYPE", (int)AppConfigDataHandler.APP_CONFIG_TYPE_CUSTOMER_BUILT);
        configTemplate.put("APP_CONFIG_TEMPLATE_TYPE", appConfigTemplateType);
        configTemplate.put("APP_GROUP_ID", appJson.getLong("APP_GROUP_ID"));
        if (appJson.has("APP_ID")) {
            configTemplate.put("APP_ID", appJson.get("APP_ID"));
        }
        final JSONObject configTemplateExtn = new JSONObject();
        configTemplateExtn.put("APP_CONFIG_TEMPLATE", configTemplateJson.get("APP_CONFIG_FORM"));
        configTemplateExtn.put("CUSTOMER_ID", customerId);
        configTemplate.put("AppConfigTemplateExtn", (Object)configTemplateExtn);
        restrictionSchema.put("AppConfigTemplate", (Object)configTemplate);
        managedAppConfiguration.put("APP_CONFIG_NAME", (Object)"Managed App Configuration");
        managedAppConfiguration.put("LAST_MODIFIED_BY", userId);
        final JSONObject managedAppConfigurationData = new JSONObject();
        managedAppConfigurationData.put("CUSTOMER_ID", customerId);
        final String configStr = String.valueOf(appJson.get("APP_CONFIGURATION"));
        final JSONArray appConfigurationArr = new JSONArray(configStr);
        managedAppConfigurationData.put("APP_CONFIG", (Object)appConfigurationArr);
        managedAppConfiguration.put("ManagedAppConfigurationData", (Object)managedAppConfigurationData);
        restrictionSchema.put("ManagedAppConfiguration", (Object)managedAppConfiguration);
        restrictionSchema.put("PUBLISH_PROFILE", appJson.optBoolean("PUBLISH_PROFILE", (boolean)Boolean.FALSE));
        restrictionSchema.put("CUSTOMER_ID", customerId);
        restrictionSchema.put("LAST_MODIFIED_BY", userId);
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
        final long configDataItemId = appJson.getLong("CONFIG_DATA_ITEM_ID");
        if (shouldAssignCommand) {
            appConfigHandler.addOrUpdateAppConfigPolicyAndInvokeCommand(configDataItemId, restrictionSchema);
        }
        else {
            appConfigHandler.addOrUpdateAppConfigPolicy(configDataItemId, restrictionSchema);
        }
    }
    
    public NSDictionary parseJsonToIosAppConfig(final JSONArray configJSONArray) throws Exception {
        final NSDictionary configurationDict = new NSDictionary();
        try {
            for (int i = 0; i < configJSONArray.length(); ++i) {
                final JSONObject configJsonTemp = configJSONArray.getJSONObject(i);
                final String key = String.valueOf(configJsonTemp.get("key"));
                final String type = String.valueOf(configJsonTemp.get("type"));
                final String value = String.valueOf(configJsonTemp.get("value"));
                if (type.contains("string")) {
                    configurationDict.put(key, (Object)value);
                }
                else if (type.contains("bool")) {
                    configurationDict.put(key, (Object)Boolean.parseBoolean(value));
                }
                else if (type.contains("integer")) {
                    configurationDict.put(key, (Object)Integer.parseInt(value));
                }
                else {
                    final NSObject confObject = XMLPropertyListParser.parse(value.getBytes());
                    configurationDict.put(key, confObject);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in parseJsonToIosAppConfig ", e);
        }
        return configurationDict;
    }
    
    public NSDictionary getIosAppConfig(final Long configDataItemId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppConfigPolicy"));
        sQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
        sQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
        sQuery.addSelectColumn(new Column("ManagedAppConfigurationData", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        final Row appConfigRow = DO.getFirstRow("ManagedAppConfigurationData");
        String filePath = (String)appConfigRow.get("APP_CONFIG_PATH");
        filePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + filePath;
        final String configData = new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
        return this.parseJsonToIosAppConfig(new JSONArray(configData));
    }
    
    public void deleteAppConfigDetails(final Long configItem, final Long customerId) {
        this.deleteAppConfigDetails(new Long[] { configItem }, customerId);
    }
    
    public void deleteAppConfigDetails(final Long[] configItemIds, final Long customerId) {
        try {
            final DataObject dataObject = AppConfigPolicyDBHandler.getInstance().getAppConfigDO(configItemIds);
            if (!dataObject.isEmpty()) {
                final List templateIds = new ArrayList();
                final Iterator<Row> iterator = dataObject.getRows("ManagedAppConfiguration");
                while (iterator.hasNext()) {
                    final Row appConfigRow = iterator.next();
                    templateIds.add(appConfigRow.get("APP_CONFIG_TEMPLATE_ID"));
                    final Row appConfigDataRow = dataObject.getRow("ManagedAppConfigurationData", new Criteria(new Column("ManagedAppConfigurationData", "APP_CONFIG_ID"), appConfigRow.get("APP_CONFIG_ID"), 0));
                    String appConfigPath = (String)appConfigDataRow.get("APP_CONFIG_PATH");
                    appConfigPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + appConfigPath;
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(appConfigPath);
                    final Row appConfigTemplateRow = dataObject.getRow("AppConfigTemplateExtn", new Criteria(new Column("AppConfigTemplateExtn", "APP_CONFIG_TEMPLATE_ID"), appConfigRow.get("APP_CONFIG_TEMPLATE_ID"), 0));
                    String appConfigTemplatePath = (String)appConfigTemplateRow.get("APP_CONFIG_TEMPLATE_PATH");
                    appConfigTemplatePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + appConfigTemplatePath;
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(appConfigTemplatePath);
                }
                DataAccess.delete("AppConfigPolicy", new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configItemIds, 8));
                DataAccess.delete("AppConfigTemplate", new Criteria(new Column("AppConfigTemplate", "APP_CONFIG_TEMPLATE_ID"), (Object)templateIds.toArray(), 8));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteAppConfigDetails ", e);
        }
    }
    
    public void cloneAppConigurationData(final Long oldCollectionId, final Long newCollectionId, final Long appConfigTemplateId) throws Exception {
        final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
        final AppConfigDataPolicyHandler appConfigDataPolicyHandler = new AppConfigDataPolicyHandler();
        final Long oldAppConfigId = appConfigDataPolicyHandler.getAppConfigIdForCollectionId(oldCollectionId);
        if (oldAppConfigId != -1L) {
            SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Collection"));
            final Join cfgDataJoin = new Join("Collection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join configDataJoin = new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
            final Join configDataItemJoin = new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2);
            final Join installPolicyJoin = new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2);
            sQuery.addJoin(cfgDataJoin);
            sQuery.addJoin(configDataJoin);
            sQuery.addJoin(configDataItemJoin);
            sQuery.addJoin(installPolicyJoin);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            sQuery.setCriteria(new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)newCollectionId, 0));
            DataObject dO = DataAccess.get(sQuery);
            if (!dO.isEmpty()) {
                final Long appId = (Long)dO.getFirstValue("InstallAppPolicy", "APP_ID");
                final Long configDataItemId = (Long)dO.getFirstValue("InstallAppPolicy", "CONFIG_DATA_ITEM_ID");
                final Long customerId = (Long)DBUtil.getValueFromDB("MdAppDetails", "APP_ID", (Object)appId, "CUSTOMER_ID");
                final JSONObject appJSON = new JSONObject();
                final JSONObject appConfigJSON = new JSONObject();
                final JSONObject appConfigExtnJSON = new JSONObject();
                appConfigJSON.put("APP_CONFIG_TEMPLATE_ID", (Object)appConfigTemplateId);
                appConfigJSON.put("APP_CONFIG_NAME", (Object)"App Config");
                final JSONObject appConfigDetails = appConfigDataHandler.getAppConfigDetails(oldAppConfigId);
                if (appConfigDetails != null) {
                    appConfigJSON.put("LAST_MODIFIED_BY", appConfigDetails.get("LAST_MODIFIED_BY"));
                    appConfigExtnJSON.put("APP_CONFIG", (Object)appConfigDetails.optString("APP_CONFIG", ""));
                    appConfigExtnJSON.put("CUSTOMER_ID", (Object)customerId);
                    appConfigJSON.put("ManagedAppConfigurationData", (Object)appConfigExtnJSON);
                    appJSON.put("ManagedAppConfiguration", (Object)appConfigJSON);
                    final Long appConfigId = this.addOrUpdateAppConfig(appJSON.getJSONObject("ManagedAppConfiguration"));
                    sQuery = (SelectQuery)new SelectQueryImpl(new Table("AppConfigPolicy"));
                    sQuery.setCriteria(new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                    sQuery.addSelectColumn(new Column("AppConfigPolicy", "*"));
                    dO = DataAccess.get(sQuery);
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
                }
            }
        }
    }
    
    public Long getAppConfigIDFromConfigDataItemID(final Long configDataItemID) {
        Long appConfigID = -1L;
        try {
            appConfigID = (Long)DBUtil.getValueFromDB("AppConfigPolicy", "CONFIG_DATA_ITEM_ID", (Object)configDataItemID, "APP_CONFIG_ID");
            appConfigID = ((appConfigID == null) ? -1L : appConfigID);
        }
        catch (final Exception ex) {}
        return appConfigID;
    }
    
    public Long getAppConfigDataItemId(final Long releaseLabelId, final Long packageId) throws Exception {
        Long configDataItemId = -1L;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ConfigDataItem"));
            query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("Collection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria cri1 = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            query.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageId, 0).and(cri1));
            query.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            query.addSelectColumn(new Column("Collection", "COLLECTION_ID"));
            query.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
            query.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            query.addSelectColumn(new Column("MdPackageToAppGroup", "PACKAGE_ID"));
            final DataObject dataObject = SyMUtil.getPersistenceLite().get(query);
            final Row row = dataObject.getRow("ConfigDataItem");
            if (row != null) {
                configDataItemId = (Long)row.get("CONFIG_DATA_ITEM_ID");
            }
            if (configDataItemId == -1L) {
                throw new Exception("ConfigDataItem ID not found fot the given packageID -> " + packageId + " and Release Label Id ->" + releaseLabelId);
            }
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception while fetching configDataItemID ", exception);
            throw exception;
        }
        return configDataItemId;
    }
    
    public void validateIfAppInTrash(final Long configDataItemId) throws DataAccessException {
        boolean isAppMovedToTrash = false;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ConfigDataItem"));
            query.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            query.addJoin(new Join("CfgDataToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("Collection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            query.setCriteria(new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
            query.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            query.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            query.addSelectColumn(new Column("Collection", "COLLECTION_ID"));
            query.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            query.addSelectColumn(new Column("ProfileToCollection", "COLLECTION_ID"));
            query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            query.addSelectColumn(new Column("Profile", "IS_MOVED_TO_TRASH"));
            final DataObject doj = SyMUtil.getPersistenceLite().get(query);
            final Row row = doj.getFirstRow("Profile");
            if (row != null) {
                isAppMovedToTrash = (boolean)row.get("IS_MOVED_TO_TRASH");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in validateIfAppInTrash method", ex);
            throw ex;
        }
        if (isAppMovedToTrash) {
            throw new APIHTTPException("APP0018", new Object[0]);
        }
    }
    
    static {
        APP_CONFIG_TYPE_RAW = 1;
        APP_CONFIG_TYPE_CUSTOMER_BUILT = 2;
        APP_CONFIG_TYPE_FROM_BUSINESS_STORE = 3;
    }
}
