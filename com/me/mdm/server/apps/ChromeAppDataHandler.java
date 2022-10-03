package com.me.mdm.server.apps;

import org.json.JSONException;
import com.me.mdm.server.tracker.mics.MICSAppRepositoryFeatureController;
import java.util.List;
import com.me.mdm.api.APIActionsHandler;
import java.util.ArrayList;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.apache.tika.Tika;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import com.me.mdm.server.apps.config.AndroidAppConfigDataHandler;
import org.json.JSONArray;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public class ChromeAppDataHandler extends BaseAppDatahandler
{
    public ChromeAppDataHandler(final JSONObject params) {
        super(params);
        this.platformType = 4;
        this.defaultCategory = "Productivity";
    }
    
    @Override
    protected JSONObject modifyEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJson) throws Exception {
        return jsonObject;
    }
    
    @Override
    public JSONObject addStoreApp(final JSONObject requestJson) throws Exception {
        requestJson.put("storeapp", true);
        return this.addChromeApp(requestJson);
    }
    
    @Override
    public JSONObject addEnterpriseApp(final JSONObject requestJson) throws Exception {
        if (!requestJson.has("app_url")) {
            throw new APIHTTPException("COM0009", new Object[] { "app_url" });
        }
        requestJson.put("APP_FILE_LOC", (Object)String.valueOf(requestJson.get("app_url")));
        return this.addChromeApp(requestJson);
    }
    
    @Override
    public JSONObject updateStoreApp(final JSONObject jsonObject) throws Exception {
        this.updateStoreAppCheck(jsonObject);
        return this.addChromeApp(jsonObject.getJSONObject("msg_body").put("update", true).put("storeapp", true));
    }
    
    @Override
    public JSONObject updateEnterpriseApp(final JSONObject jsonObject) throws Exception {
        this.validateIfAppFound(this.packageId = APIUtil.getResourceID(jsonObject, "app_id"), this.customerID);
        this.setReleaseLabelId(jsonObject);
        final HashMap appDetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        final String identifier = appDetails.get("IDENTIFIER");
        final String appName = appDetails.get("APP_NAME");
        if (!jsonObject.optString("bundle_identifier", identifier).equals(identifier)) {
            throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.bundleidinvalid", new Object[0]) });
        }
        jsonObject.put("bundle_identifier", (Object)identifier);
        if (!jsonObject.has("app_name") || (jsonObject.has("app_name") && MDMStringUtils.isEmpty(jsonObject.getString("app_name")))) {
            jsonObject.put("app_name", (Object)appName);
        }
        return this.addChromeApp(jsonObject.put("update", true));
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject jsonObject) throws Exception {
        final JSONObject messageBody = jsonObject.getJSONObject("msg_body");
        final Long appId = APIUtil.getResourceID(jsonObject, "app_id");
        this.validateIfAppFound(appId, this.customerID);
        this.validateIfAppAndReleaseLabelFound();
        JSONArray appConfigurationArr = new JSONArray();
        if (messageBody.has("app_configuration")) {
            final String appconfig = messageBody.getJSONArray("app_configuration").toString();
            if (!appconfig.isEmpty()) {
                messageBody.put("APP_CONFIGURATION", (Object)appconfig);
                appConfigurationArr = messageBody.getJSONArray("app_configuration");
            }
            this.getAppDetails(jsonObject);
            final JSONObject managedAppConfiguration = new JSONObject();
            this.validateIfAppInTrash();
            managedAppConfiguration.put("APP_CONFIG_ID", this.appdetails.get("APP_CONFIG_ID"));
            managedAppConfiguration.put("APP_CONFIG_TEMPLATE_ID", this.appdetails.get("APP_CONFIG_TEMPLATE_ID"));
            managedAppConfiguration.put("APP_CONFIG_NAME", (Object)"Managed App Configuration");
            managedAppConfiguration.put("LAST_MODIFIED_BY", (Object)this.userID);
            final JSONObject managedAppConfigurationData = new JSONObject();
            managedAppConfigurationData.put("CUSTOMER_ID", (Object)this.customerID);
            managedAppConfigurationData.put("APP_CONFIG", (Object)appConfigurationArr);
            managedAppConfiguration.put("ManagedAppConfigurationData", (Object)managedAppConfigurationData);
            final JSONObject restrictionSchema = new JSONObject();
            restrictionSchema.put("ManagedAppConfiguration", (Object)managedAppConfiguration);
            restrictionSchema.put("PUBLISH_PROFILE", (Object)Boolean.TRUE);
            restrictionSchema.put("CUSTOMER_ID", (Object)this.customerID);
            restrictionSchema.put("LAST_MODIFIED_BY", (Object)this.userID);
            new AndroidAppConfigDataHandler().addOrUpdateAppConfigPolicyAndInvokeCommand(Long.valueOf(this.appdetails.get("CONFIG_DATA_ITEM_ID").toString()), restrictionSchema);
            return this.getAppConfiguration(jsonObject);
        }
        throw new APIHTTPException("COM0009", new Object[] { "app_configuration" });
    }
    
    @Override
    public JSONObject updateAppConfiguration(final JSONObject jsonObject) throws Exception {
        return this.addAppConfiguration(jsonObject);
    }
    
    @Override
    public JSONObject deleteAppConfiguration(final JSONObject jsonObject) throws Exception {
        this.packageId = APIUtil.getResourceID(jsonObject, "app_id");
        this.setReleaseLabelId(jsonObject);
        this.validateIfAppAndReleaseLabelFound();
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        final Long configDataItem = this.appdetails.get("CONFIG_DATA_ITEM_ID");
        jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItem);
        if (configDataItem != null) {
            final JSONObject configJson = new JSONObject();
            configJson.put("CUSTOMER_ID", (Object)this.customerID);
            configJson.put("LAST_MODIFIED_BY", (Object)this.userID);
            new AppConfigDataPolicyHandler().resetAppConfigAndInvokeCommand(configDataItem, configJson);
        }
        return new JSONObject();
    }
    
    @Override
    protected JSONObject getAppConfigTemplate(final Long configDataItemID, final Long appID) throws Exception {
        return new JSONObject(new AppConfigDataHandler().getAppConfigTemplate(appID));
    }
    
    private JSONObject addChromeApp(final JSONObject requestJson) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        try {
            final Tika tika = new Tika();
            String dispFilePathDM = null;
            if (requestJson.has("display_image")) {
                dispFilePathDM = (String)requestJson.get("display_image");
            }
            final String identifier = String.valueOf(requestJson.get("bundle_identifier"));
            final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(identifier, this.platformType, this.customerID);
            if (appGroupID != null) {
                jsonObject.put("APP_GROUP_ID", (Object)appGroupID);
            }
            if (!requestJson.optBoolean("update", false) && !new AppTrashModeHandler().isAppGroupIdMovedToTrash(appGroupID) && new AppsUtil().isAppExistsInPackage(identifier, this.platformType, this.customerID)) {
                throw new APIHTTPException("COM0010", new Object[] { ", app bundle identifier - " + identifier });
            }
            if (dispFilePathDM != null) {
                final File tFile = new File(dispFilePathDM);
                final String contentType = tika.detect(tFile);
                if (!APIUtil.isAllowedImageMimeType(contentType)) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
                }
                final File dispFile = new File(dispFilePathDM);
                if (dispFile.length() > 50000L) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app_reject_image.size", new Object[0]) });
                }
                final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(this.customerID);
                final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
                final String img = imgFileSourceMap.get("destDCFileName");
                jsonObject.put("DISPLAY_IMAGE", (Object)img);
            }
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)identifier);
            jsonObject.put("APP_VERSION", (Object)"--");
            jsonObject.put("APP_NAME", (Object)String.valueOf(requestJson.get("app_name")));
            jsonObject.put("APP_TITLE", (Object)String.valueOf(requestJson.get("app_name")));
            jsonObject.put("PROFILE_NAME", (Object)String.valueOf(requestJson.get("app_name")));
            Long categoryId = AppsUtil.getInstance().getAppCategoryId(requestJson.optLong("app_category_id", -1L), this.platformType);
            if (categoryId == null || categoryId == -1L) {
                categoryId = AppsUtil.getInstance().getPlatformDefaultCategory(this.platformType, this.defaultCategory);
            }
            jsonObject.put("APP_CATEGORY_ID", (Object)categoryId);
            final String description = requestJson.optString("description");
            jsonObject.put("PLATFORM_TYPE", this.platformType);
            jsonObject.put("COLLECTION_ID", 0L);
            jsonObject.put("CONFIG_DATA_ITEM_ID", 0L);
            if (requestJson.has("storeapp") && requestJson.getBoolean("storeapp")) {
                requestJson.put("app_type", 0);
            }
            jsonObject.put("PACKAGE_TYPE", requestJson.getInt("app_type"));
            if (requestJson.has("APP_FILE_LOC")) {
                jsonObject.put("APP_FILE_LOC", requestJson.get("APP_FILE_LOC"));
            }
            if (requestJson.optBoolean("displayimagedelete", false)) {
                jsonObject.put("isDisplayImageDelete", true);
            }
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("APP_CONFIG", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            jsonObject.put("RELEASE_LABEL_ID", (Object)AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(this.customerID));
            if (requestJson.has("version_label")) {
                jsonObject.put("RELEASE_LABEL_DISPLAY_NAME", requestJson.get("version_label"));
            }
            final JSONObject packageJSON = new JSONObject();
            AppsUtil.getInstance();
            final Integer supportedDevices = AppsUtil.getSupportedDevicesValues(requestJson.optInt("supported_devices", 1), this.platformType);
            if (supportedDevices == null || supportedDevices == -1) {
                throw new APIHTTPException("COM0008", new Object[] { "Supported devices" });
            }
            packageJSON.put("SUPPORTED_DEVICES", (Object)supportedDevices);
            if (!MDMStringUtils.isEmpty(description)) {
                packageJSON.put("DESCRIPTION", (Object)description);
                jsonObject.put("PROFILE_DESCRIPTION", (Object)description);
            }
            else {
                jsonObject.put("PROFILE_DESCRIPTION", (Object)"");
            }
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageJSON);
            jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(4).isCurrentPackageNewToAppRepo(identifier, this.customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
            MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(jsonObject);
            final List profileIds = new ArrayList();
            profileIds.add(jsonObject.getLong("PROFILE_ID"));
            APIActionsHandler.getInstance().invokeAPIActionProfileListener(profileIds, profileIds, null, 4);
            jsonObject.put("app_id", jsonObject.getJSONObject("APP_POLICY").getLong("PACKAGE_ID"));
            if (requestJson.getInt("app_type") == 2) {
                MICSAppRepositoryFeatureController.addTrackingData(4, MICSAppRepositoryFeatureController.AppOperation.ADD_APP, true, false);
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[] { "" });
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject generateSignUpURL(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
}
