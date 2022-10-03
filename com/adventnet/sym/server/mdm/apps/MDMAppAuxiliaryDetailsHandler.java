package com.adventnet.sym.server.mdm.apps;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import java.util.Iterator;
import java.util.HashMap;
import com.me.mdm.server.apps.permission.PermissionHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.me.mdm.server.apps.permission.config.PermissionConfigDataPolicyHandler;
import java.util.List;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMAppAuxiliaryDetailsHandler
{
    public Logger logger;
    
    public MDMAppAuxiliaryDetailsHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    void saveManagedAppConfiguration(final JSONObject jsonObject, final Long appGroupId, final Long userId) throws Exception {
        this.logger.log(Level.INFO, "Going to save managed app configuration data");
        final Long appId = jsonObject.getLong("AppConfigTemplate.APP_ID");
        final int platformType = jsonObject.optInt("PLATFORM_TYPE");
        final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
        if (platformType == 4) {
            jsonObject.put("APP_ID", (Object)appId);
            this.addDefaultManagedConfiguration(appGroupId, jsonObject);
        }
        else if (platformType == 2 && jsonObject.has("default_app_configuration")) {
            jsonObject.put("APP_ID", (Object)appId);
            this.addAndroidManagedConfiguration(appGroupId, jsonObject);
        }
        else if ((jsonObject.has("APP_CONFIG_TEMPLATE") && !jsonObject.optString("APP_CONFIG_TEMPLATE").isEmpty()) || (jsonObject.has("APP_CONFIGURATION") && !jsonObject.optString("APP_CONFIGURATION").isEmpty())) {
            jsonObject.put("PACKAGE_MODIFIED_BY", (Object)userId);
            jsonObject.put("HAS_APP_CONFIGURATION", (Object)Boolean.TRUE);
            Long configTemplateId = jsonObject.optLong("APP_CONFIG_TEMPLATE_ID", -1L);
            final boolean isNewVersionAppDetected = jsonObject.optBoolean("isNewVersionAppDetected", false);
            final int packageType = jsonObject.optInt("PACKAGE_TYPE", 2);
            if (this.needAppConfigUpdate(isNewVersionAppDetected, platformType, packageType)) {
                if (jsonObject.has("APP_CONFIG_TEMPLATE") && !jsonObject.optString("APP_CONFIG_TEMPLATE").isEmpty() && (!jsonObject.has("APP_CONFIGURATION") || jsonObject.optString("APP_CONFIGURATION").isEmpty())) {
                    this.logger.log(Level.INFO, "Config schema alone need to be saved");
                    final JSONObject templateJSON = new JSONObject();
                    templateJSON.put("APP_GROUP_ID", (Object)appGroupId);
                    templateJSON.put("APP_CONFIG_TEMPLATE_TYPE", (Object)AppConfigDataHandler.APP_CONFIG_TYPE_FROM_BUSINESS_STORE);
                    templateJSON.put("APP_ID", (Object)appId);
                    templateJSON.put("APP_CONFIG_TEMPLATE_ID", (Object)configTemplateId);
                    final JSONObject templateExtnJSON = new JSONObject();
                    final JSONObject appConfigTemplateData = new JSONObject(String.valueOf(jsonObject.get("APP_CONFIG_TEMPLATE"))).getJSONObject("APP_CONFIG_FORM");
                    templateExtnJSON.put("APP_CONFIG_TEMPLATE", (Object)appConfigTemplateData);
                    templateExtnJSON.put("CUSTOMER_ID", jsonObject.optLong("CUSTOMER_ID"));
                    templateJSON.put("AppConfigTemplateExtn", (Object)templateExtnJSON);
                    configTemplateId = appConfigDataHandler.addOrUpdateAppConfigTemplate(templateJSON);
                }
                if (jsonObject.has("APP_CONFIGURATION") && !jsonObject.optString("APP_CONFIGURATION").isEmpty()) {
                    this.logger.log(Level.INFO, "App config data need to be saved");
                    jsonObject.put("APP_CONFIG_TEMPLATE_ID", (Object)configTemplateId);
                    appConfigDataHandler.saveAppConfigData(jsonObject, Boolean.TRUE);
                }
                else if (jsonObject.has("APP_CONFIG_TEMPLATE") && !jsonObject.optString("APP_CONFIG_TEMPLATE").isEmpty()) {
                    this.logger.log(Level.INFO, "Config schema alone is saved. Duplicating config data from previous colection");
                    final Long oldCollectionId = jsonObject.optLong("oldCollectionId", -1L);
                    final Long newCollectionId = jsonObject.optLong("newCollectionId", -1L);
                    if (oldCollectionId != -1L && newCollectionId != -1L && !oldCollectionId.equals(newCollectionId)) {
                        appConfigDataHandler.cloneAppConigurationData(oldCollectionId, newCollectionId, configTemplateId);
                    }
                }
            }
        }
        if (jsonObject.optBoolean("IS_APP_CONFIG_DELETED", false)) {
            this.logger.log(Level.INFO, "App config is deleted");
            final Long configDataItemId = jsonObject.getLong("CONFIG_DATA_ITEM_ID");
            appConfigDataHandler.deleteAppConfigDetails(configDataItemId, jsonObject.getLong("CUSTOMER_ID"));
        }
    }
    
    void saveAppPermissionData(final JSONObject jsonObject, final Long appGroupId, final List appIdList) throws JSONException, Exception {
        final boolean isNewVersionAppDetected = jsonObject.optBoolean("isNewVersionAppDetected", false);
        if (isNewVersionAppDetected) {
            this.logger.log(Level.INFO, "Going to save permission data");
            final PermissionConfigDataPolicyHandler permissionPolicy = new PermissionConfigDataPolicyHandler();
            if (jsonObject.has("permissionConfiguration")) {
                final HashMap map = new AppPermissionHandler().getPermissionNameToGroupMap();
                final JSONObject appPermissions = jsonObject.getJSONObject("permission");
                final JSONArray appPermissionConfigDetails = new JSONArray();
                final JSONArray appList = new JSONArray();
                appList.put((Object)appGroupId);
                final Iterator keyIter = appPermissions.keys();
                while (keyIter.hasNext()) {
                    final String keyName = keyIter.next();
                    if (map.get(keyName) != null) {
                        final int value = appPermissions.getInt(keyName);
                        final JSONObject json = new JSONObject();
                        json.put("PermissionConfigAppList", (Object)appList);
                        json.put("CONFIG_CHOICE", 1);
                        json.put("APP_PERMISSION_GROUP_NAME", (Object)("android.permission-group." + keyName));
                        json.put("APP_PERMISSION_GRANT_STATE", value);
                        json.put("APP_PERMISSION_GROUP_ID", map.get(keyName));
                        appPermissionConfigDetails.put((Object)json);
                    }
                }
                if (appPermissionConfigDetails.length() > 0) {
                    permissionPolicy.addOrUpdateAppConfigPolicyAndInvokeCommand(jsonObject.getLong("CONFIG_DATA_ITEM_ID"), JSONUtil.toJSON("AppPermissionConfigDetails", appPermissionConfigDetails));
                }
            }
            else if (jsonObject.has("PermissionSchema")) {
                final Long oldCollectionId = jsonObject.optLong("oldCollectionId", -1L);
                final Long newCollectionId = jsonObject.optLong("newCollectionId", -1L);
                if (oldCollectionId != -1L && newCollectionId != -1L && !oldCollectionId.equals(newCollectionId)) {
                    permissionPolicy.cloneAppPermssionData(oldCollectionId, newCollectionId);
                }
            }
            if (jsonObject.has("PermissionSchema")) {
                final PermissionHandler permissionHandler = new PermissionHandler();
                final JSONArray permissionObjectArray = jsonObject.getJSONArray("PermissionSchema");
                final JSONArray permissionArray = new JSONArray();
                for (int i = 0; i < permissionObjectArray.length(); ++i) {
                    permissionArray.put((Object)String.valueOf(permissionObjectArray.get(i)));
                }
                for (final Object appId : appIdList) {
                    permissionHandler.addOrUpdatePermissionsForApp((Long)appId, permissionArray);
                }
            }
        }
    }
    
    private boolean needAppConfigUpdate(final boolean newVersionDetected, final int platform, final int packageType) {
        if (platform == 2) {
            return newVersionDetected;
        }
        return platform != 3 || packageType == 2 || newVersionDetected;
    }
    
    private void addDefaultManagedConfiguration(final Long appGroupId, final JSONObject jsonObject) throws Exception {
        final JSONObject templateJSON = new JSONObject();
        templateJSON.put("APP_GROUP_ID", (Object)appGroupId);
        templateJSON.put("APP_CONFIG_TEMPLATE_TYPE", (Object)AppConfigDataHandler.APP_CONFIG_TYPE_CUSTOMER_BUILT);
        templateJSON.put("APP_ID", jsonObject.getLong("APP_ID"));
        templateJSON.put("APP_CONFIG_TEMPLATE_ID", jsonObject.optLong("APP_CONFIG_TEMPLATE_ID", -1L));
        final JSONObject dummyRestriction = new JSONObject();
        dummyRestriction.put("restrictionType", (Object)"textarea");
        dummyRestriction.put("title", (Object)"App config data");
        dummyRestriction.put("key", (Object)"chromeAppConfigData");
        final JSONArray restrictions = new JSONArray();
        restrictions.put((Object)dummyRestriction);
        final JSONObject appConfigTemplateData = new JSONObject();
        appConfigTemplateData.put("restrictions", (Object)restrictions);
        final JSONObject templateExtnJSON = new JSONObject();
        templateExtnJSON.put("APP_CONFIG_TEMPLATE", (Object)appConfigTemplateData);
        templateExtnJSON.put("CUSTOMER_ID", (Object)jsonObject.opt("CUSTOMER_ID"));
        templateJSON.put("AppConfigTemplateExtn", (Object)templateExtnJSON);
        final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
        appConfigDataHandler.addOrUpdateAppConfigTemplate(templateJSON);
    }
    
    private void addAndroidManagedConfiguration(final Long appGroupId, final JSONObject jsonObject) throws Exception {
        final JSONObject templateJSON = new JSONObject();
        templateJSON.put("APP_GROUP_ID", (Object)appGroupId);
        templateJSON.put("APP_CONFIG_TEMPLATE_TYPE", (Object)AppConfigDataHandler.APP_CONFIG_TYPE_CUSTOMER_BUILT);
        templateJSON.put("APP_ID", jsonObject.getLong("APP_ID"));
        final JSONObject udidConfig = new JSONObject();
        udidConfig.put("key", (Object)"deviceUDID");
        udidConfig.put("value", (Object)"%udid%");
        udidConfig.put("type", (Object)"string");
        final JSONArray restrictions = new JSONArray();
        restrictions.put((Object)udidConfig);
        final JSONObject appConfigTemplateData = new JSONObject();
        appConfigTemplateData.put("restrictions", (Object)restrictions);
        final JSONObject templateExtnJSON = new JSONObject();
        templateExtnJSON.put("APP_CONFIG_TEMPLATE", (Object)appConfigTemplateData);
        templateExtnJSON.put("CUSTOMER_ID", (Object)jsonObject.opt("CUSTOMER_ID"));
        templateJSON.put("AppConfigTemplateExtn", (Object)templateExtnJSON);
        final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
        appConfigDataHandler.addOrUpdateAppConfigTemplate(templateJSON);
    }
    
    void addAuxillaryDetails(final JSONObject jsonObject, final Long appGroupId, final Long userId, final List appIdList) throws Exception {
        this.saveManagedAppConfiguration(jsonObject, appGroupId, userId);
        this.saveAppPermissionData(jsonObject, appGroupId, appIdList);
        this.saveAppSignatureData(jsonObject, appGroupId);
    }
    
    private void saveAppSignatureData(final JSONObject jsonObject, final Long appGroupId) {
        if (jsonObject.has("appSignatureInfo")) {
            this.logger.log(Level.INFO, "Going to update app signature details");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupSignatureDetails"));
            selectQuery.setCriteria(new Criteria(new Column("AppGroupSignatureDetails", "APP_GROUP_ID"), (Object)appGroupId, 0));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            try {
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final JSONObject appSignatureInfo = jsonObject.getJSONObject("appSignatureInfo");
                final Long packageId = (Long)jsonObject.get("PACKAGE_ID");
                if (dataObject.isEmpty()) {
                    final Row row = new Row("AppGroupSignatureDetails");
                    row.set("PACKAGE_ID", (Object)packageId);
                    row.set("APP_GROUP_ID", (Object)appGroupId);
                    row.set("SIGNATURE_ALGORITHM_NAME", appSignatureInfo.get("SIGNATURE_ALGORITHM_NAME"));
                    row.set("FINGERPRINT_MD5", appSignatureInfo.get("FINGERPRINT_MD5"));
                    row.set("FINGERPRINT_SHA128", appSignatureInfo.get("FINGERPRINT_SHA128"));
                    row.set("FINGERPRINT_SHA256", appSignatureInfo.get("FINGERPRINT_SHA256"));
                    dataObject.addRow(row);
                }
                else {
                    final Row row = dataObject.getRow("AppGroupSignatureDetails");
                    row.set("PACKAGE_ID", (Object)packageId);
                    row.set("APP_GROUP_ID", (Object)appGroupId);
                    row.set("SIGNATURE_ALGORITHM_NAME", appSignatureInfo.get("SIGNATURE_ALGORITHM_NAME"));
                    row.set("FINGERPRINT_MD5", appSignatureInfo.get("FINGERPRINT_MD5"));
                    row.set("FINGERPRINT_SHA128", appSignatureInfo.get("FINGERPRINT_SHA128"));
                    row.set("FINGERPRINT_SHA256", appSignatureInfo.get("FINGERPRINT_SHA256"));
                    dataObject.updateRow(row);
                }
                MDMUtil.getPersistence().update(dataObject);
            }
            catch (final DataAccessException e) {
                this.logger.log(Level.WARNING, "Could not update signature info", (Throwable)e);
            }
        }
    }
}
