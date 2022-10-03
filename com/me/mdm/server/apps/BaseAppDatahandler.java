package com.me.mdm.server.apps;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.Arrays;
import java.util.Set;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import org.json.JSONException;
import com.me.mdm.server.tracker.mics.MICSAppRepositoryFeatureController;
import com.me.mdm.api.APIActionsHandler;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import java.io.File;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.EnterpriseAppExtractor;
import org.apache.tika.Tika;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.apps.AppsLicensesUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import com.me.mdm.server.apps.permission.config.PermissionConfigDataHandler;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.server.apps.permission.config.PermissionConfigDataPolicyHandler;
import com.me.mdm.server.apps.permission.PermissionHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.APIUtil;
import java.util.HashMap;
import java.util.logging.Logger;
import org.json.JSONObject;

public abstract class BaseAppDatahandler implements AppDataHandlerInterface
{
    int appType;
    Long customerID;
    int platformType;
    JSONObject appProps;
    Long userID;
    protected static Logger logger;
    HashMap appdetails;
    Long packageId;
    String defaultCategory;
    Long releaseLabelId;
    public String storeSyncKey;
    String userName;
    
    @Override
    public JSONObject getAppPermission(final JSONObject message) throws Exception {
        final Long packageID = APIUtil.getResourceID(message, "app_id");
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        final HashMap hashMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, this.releaseLabelId);
        final Long appConfigDatItemId = hashMap.get("CONFIG_DATA_ITEM_ID");
        final Long appGroupID = hashMap.get("APP_GROUP_ID");
        final Long appId = hashMap.get("APP_ID");
        final JSONObject retData = new JSONObject();
        JSONArray appPemissions = new JSONArray();
        JSONObject permissionData = new JSONObject();
        if (appConfigDatItemId != null && !appId.equals("")) {
            appPemissions = new PermissionHandler().getRequestedPermissionForApp((long)appId);
            if (appPemissions.length() > 0 && appConfigDatItemId != null && !appConfigDatItemId.equals("")) {
                permissionData = new PermissionConfigDataPolicyHandler().getAppPermissionConif((long)appConfigDatItemId);
            }
        }
        retData.put("APP_PERMISSION_DATA", (Object)permissionData);
        return retData;
    }
    
    @Override
    public JSONObject modifyAppPermission(final JSONObject message) throws Exception {
        final Long packageID = APIUtil.getResourceID(message, "app_id");
        this.packageId = packageID;
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, this.releaseLabelId);
        this.validateIfAppInTrash();
        final List<String> appPermissionsList = new ArrayList<String>(new PermissionHandler().getRequestedPermissionListForApp(this.appdetails.get("APP_ID")));
        final Map<String, Integer> appPermissions = new HashMap<String, Integer>();
        for (final String s : appPermissionsList) {
            appPermissions.put(s, 2);
        }
        if (appPermissions == null || appPermissions.size() == 0) {
            throw new APIHTTPException("COM0005", new Object[] { "PERMISSION" });
        }
        Long configDataItem = this.appdetails.get("CONFIG_DATA_ITEM_ID");
        final Long appPermissionConfigID = new PermissionConfigDataPolicyHandler().getAppPermissionConfigId(configDataItem);
        final JSONObject wrapperPermission = new JSONObject();
        final JSONArray permissionList = message.getJSONObject("msg_body").getJSONArray("permissions");
        wrapperPermission.put("APP_PERMISSION_CONFIG_ID", (Object)appPermissionConfigID);
        final JSONArray newPermissionJSON = new JSONArray();
        for (int i = 0; i < permissionList.length(); ++i) {
            final JSONObject jsonObject = permissionList.getJSONObject(i);
            final Integer grantState = (Integer)jsonObject.get("app_permission_grant_state");
            if (!appPermissions.containsKey(jsonObject.get("group_name")) || (grantState != 1 && grantState != 3 && grantState != 1 && grantState != 2)) {
                throw new APIHTTPException("COM0005", new Object[] { "Permission or Grant state" });
            }
            appPermissions.put(String.valueOf(jsonObject.get("group_name")), grantState);
        }
        JSONObject curPermission = null;
        for (int j = 0; j < appPermissionsList.size(); ++j) {
            curPermission = new JSONObject();
            final String groupname = appPermissionsList.get(j);
            final Integer permissionGroupID = new PermissionConfigDataHandler().getAppPermisionGroupIdFromName(groupname);
            curPermission.put("APP_PERMISSION_GROUP_NAME", (Object)groupname);
            curPermission.put("APP_PERMISSION_GROUP_ID", (Object)permissionGroupID);
            curPermission.put("APP_PERMISSION_GRANT_STATE", (Object)appPermissions.get(groupname));
            curPermission.put("CONFIG_CHOICE", 1);
            final JSONArray appGroupIdList = new JSONArray();
            appGroupIdList.put(this.appdetails.get("APP_GROUP_ID"));
            newPermissionJSON.put((Object)curPermission);
        }
        wrapperPermission.put("AppPermissionConfigDetails", (Object)newPermissionJSON);
        if (configDataItem != null) {
            configDataItem = new PermissionConfigDataPolicyHandler().addOrUpdateAppConfigPolicyAndInvokeCommand((long)configDataItem, wrapperPermission);
        }
        return null;
    }
    
    @Override
    public JSONObject getAppConfiguration(final JSONObject message) throws Exception {
        final Long packageID = APIUtil.getResourceID(message, "app_id");
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        final HashMap hashMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, this.releaseLabelId);
        final Long appConfigID = hashMap.get("APP_CONFIG_ID");
        final Long appGroupID = hashMap.get("APP_GROUP_ID");
        final Long appId = hashMap.get("APP_ID");
        final Long configDataItemId = hashMap.get("CONFIG_DATA_ITEM_ID");
        final JSONObject appRestrictionSchema = new JSONObject();
        try {
            if (appGroupID != null) {
                final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
                final JSONObject configTemplate = this.getAppConfigTemplate(configDataItemId, appId);
                JSONArray configData = new JSONArray();
                if (appConfigID != null) {
                    final String config = appConfigDataHandler.getAppConfig((long)appConfigID);
                    if (!MDMStringUtils.isEmpty(config)) {
                        configData = new JSONArray(config);
                    }
                }
                appRestrictionSchema.put("APP_CONFIG_FORM", (Object)configTemplate);
                appRestrictionSchema.put("APP_CONFIG_DATA", (Object)configData);
            }
        }
        catch (final RuntimeException e) {
            BaseAppDatahandler.logger.log(Level.FINE, "no config defined");
        }
        return appRestrictionSchema;
    }
    
    protected JSONObject getAppConfigTemplate(final Long configDataItemID, final Long appID) throws Exception {
        return new JSONObject(new AppConfigDataHandler().getAppConfigTemplateFromConfigDataItemID(configDataItemID));
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        this.customerID = APIUtil.getCustomerID(jsonObject);
        this.validateIfAppFound(this.packageId = APIUtil.getResourceID(jsonObject, "app_id"), this.customerID);
        this.setReleaseLabelId(jsonObject);
        this.validateIfAppAndReleaseLabelFound();
        Long businessStoreID = null;
        if (jsonObject.getJSONObject("msg_header").getJSONObject("filters").has("businessstore_id")) {
            businessStoreID = jsonObject.getJSONObject("msg_header").getJSONObject("filters").getLong("businessstore_id");
        }
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId, businessStoreID);
        final JSONObject appJSON = new JSONObject();
        appJSON.put("app_id", (Object)this.appdetails.get("PACKAGE_ID").toString());
        appJSON.put("app_group_id", this.appdetails.get("APP_GROUP_ID"));
        appJSON.put("app_name", this.appdetails.get("APP_NAME"));
        appJSON.put("app_title", this.appdetails.get("APP_TITLE"));
        appJSON.put("APP_CONFIG_TEMPLATE_ID", this.appdetails.get("APP_CONFIG_TEMPLATE_ID"));
        final String countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
        if (!MDMStringUtils.isEmpty(countryCode)) {
            appJSON.put("country_code", (Object)countryCode);
        }
        else {
            appJSON.put("country_code", (Object)"US");
        }
        if (businessStoreID == null) {
            final List businessStoreIDs = MDBusinessStoreAssetUtil.getBusinessStoresWithApp(this.appdetails.get("APP_GROUP_ID"));
            if (businessStoreIDs != null && !businessStoreIDs.isEmpty() && businessStoreIDs.size() == 1) {
                businessStoreID = businessStoreIDs.get(0);
            }
        }
        appJSON.put("platform_type", this.appdetails.get("PLATFORM_TYPE"));
        appJSON.put("app_type", this.appdetails.get("PACKAGE_TYPE"));
        appJSON.put("modified_time", this.appdetails.get("PACKAGE_MODIFIED_TIME"));
        appJSON.put("added_time", this.appdetails.get("PACKAGE_ADDED_TIME"));
        appJSON.put("bundle_identifier", this.appdetails.get("IDENTIFIER"));
        appJSON.put("app_category_name", this.appdetails.get("APP_CATEGORY_NAME"));
        appJSON.put("app_category_id", this.appdetails.get("APP_CATEGORY_ID"));
        appJSON.put("profile_name", this.appdetails.get("PROFILE_NAME"));
        appJSON.put("version", (Object)AppsUtil.getValidVersion(this.appdetails.get("APP_VERSION")));
        appJSON.put("is_distributable", this.appdetails.get("is_distributable"));
        appJSON.put("app_version_code", (Object)this.appdetails.getOrDefault("APP_NAME_SHORT_VERSION", "--"));
        final JSONObject jsonObject2 = appJSON;
        final String s = "supported_devices";
        AppsUtil.getInstance();
        jsonObject2.put(s, AppsUtil.getAPISupportedDevicesValues(this.appdetails.get("SUPPORTED_DEVICES"), this.platformType));
        appJSON.put("description", this.appdetails.get("DESCRIPTION"));
        appJSON.put("store_url", this.appdetails.get("STORE_URL"));
        appJSON.put("store_id", this.appdetails.get("STORE_ID"));
        appJSON.put("is_paid_app", this.appdetails.get("IS_PAID_APP"));
        appJSON.put("private_app", this.appdetails.get("PRIVATE_APP_TYPE"));
        final Integer appVersionStatus = this.appdetails.get("APP_VERSION_STATUS");
        appJSON.put("is_approved", appVersionStatus != null);
        appJSON.put("RELEASE_LABEL_ID".toLowerCase(), this.appdetails.get("RELEASE_LABEL_ID"));
        final String versionLabel = this.appdetails.get("RELEASE_LABEL_DISPLAY_NAME");
        appJSON.put("RELEASE_LABEL_DISPLAY_NAME".toLowerCase(), (Object)((versionLabel != null) ? versionLabel : ""));
        final Long appGroupId = Long.valueOf(this.appdetails.get("APP_GROUP_ID").toString());
        appJSON.put("APP_CHECKSUM", this.appdetails.get("APP_CHECKSUM"));
        final AppLicenseMgmtHandler appLicenseMgmtHandler = AppsLicensesUtil.getInstance(this.platformType);
        final HashMap appLicenseDetails = appLicenseMgmtHandler.getAppLicenseDetails(appGroupId, businessStoreID);
        if (appLicenseDetails != null) {
            final JSONObject licenseJSON = JSONUtil.mapToJSON(appLicenseDetails);
            final JSONObject licenseDetails = new AppLicenseMgmtHandler().getStoreAppLicenseDetails(appGroupId);
            if (licenseDetails.length() > 0) {
                licenseJSON.put("purchased_store_license", licenseDetails.optInt("PURCHASED_COUNT", 0));
                licenseJSON.put("available_store_license", licenseDetails.optInt("PURCHASED_COUNT", 0) - licenseDetails.optInt("PROVISIONED_COUNT", 0));
            }
            appJSON.put("license", (Object)licenseJSON);
        }
        appJSON.put("is_purchased_from_portal", new ManagedAppDataHandler().isAppPurchasedFromPortal(appGroupId));
        if (!MDMStringUtils.isEmpty(this.appdetails.get("DISPLAY_IMAGE_LOC"))) {
            final String displayImageLoc = String.valueOf(this.appdetails.get("DISPLAY_IMAGE_LOC"));
            if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                if (!displayImageLoc.startsWith("http")) {
                    appJSON.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                }
                else {
                    appJSON.put("icon", (Object)displayImageLoc);
                }
            }
        }
        if (!MDMStringUtils.isEmpty(this.appdetails.get("APP_FILE_LOC"))) {
            final String appLoc = String.valueOf(this.appdetails.get("APP_FILE_LOC"));
            if (!appLoc.equalsIgnoreCase("Not Available")) {
                if (!appLoc.startsWith("http")) {
                    appJSON.put("app_file", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getDownloadableFileURL(appLoc));
                }
                else {
                    appJSON.put("app_file", (Object)appLoc);
                }
            }
        }
        final String customizedAppURL = String.valueOf(this.appdetails.get("CUSTOMIZED_APP_URL"));
        if (!MDMStringUtils.isEmpty(customizedAppURL)) {
            appJSON.put("CUSTOMIZED_APP_URL", (Object)customizedAppURL);
        }
        else {
            appJSON.put("CUSTOMIZED_APP_URL", (Object)"");
        }
        appJSON.put("app_distributed_devices_count", DBUtil.getRecordCount("MdAppCatalogToResource", "RESOURCE_ID", new Criteria(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), this.appdetails.get("APP_ID"), 0).and(new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)new Integer[] { 3, 4 }, 9))));
        appJSON.put("app_installed_mobile_count", DBUtil.getRecordCount("MdAppCatalogToResource", "RESOURCE_ID", new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), this.appdetails.get("APP_ID"), 0)));
        appJSON.put("app_installed_tablet_count", 0);
        appJSON.put("yet_to_update_device_count", this.appdetails.get("YET_TO_UPDATE_DEVICE_COUNT"));
        appJSON.put("distributed_device_count", this.appdetails.get("DISTRIBUTED_DEVICE_COUNT"));
        appJSON.put("APP_INSTALLATION_FAILED_DEVICE_COUNT".toLowerCase(), this.appdetails.get("APP_INSTALLATION_FAILED_DEVICE_COUNT"));
        appJSON.put("APP_INSTALLATION_PROGRESS_DEVICE_COUNT".toLowerCase(), this.appdetails.get("APP_INSTALLATION_PROGRESS_DEVICE_COUNT"));
        appJSON.put("APP_DISTRIBUTED_GROUP_COUNT".toLowerCase(), this.appdetails.get("APP_DISTRIBUTED_GROUP_COUNT"));
        appJSON.put("APP_UPDATE_SCHEDULED".toLowerCase(), this.appdetails.get("APP_UPDATE_SCHEDULED"));
        if (!MDMStringUtils.isEmpty(this.appdetails.get("FULL_IMAGE_LOC"))) {
            final String fullImageLoc = String.valueOf(this.appdetails.get("FULL_IMAGE_LOC"));
            if (!fullImageLoc.equalsIgnoreCase("Not Available")) {
                if (!fullImageLoc.startsWith("http")) {
                    appJSON.put("FULL_IMAGE_LOC", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(fullImageLoc));
                }
                else {
                    appJSON.put("FULL_IMAGE_LOC", (Object)fullImageLoc);
                }
            }
        }
        final Map<Long, Map<String, String>> availableRelLabelDetails = this.appdetails.get("releaseLabelDetails");
        appJSON.put("release_labels", (Object)AppVersionDBUtil.getInstance().convertMapOfReleaseLabelToJSONArray(availableRelLabelDetails));
        appJSON.put("is_delete_applicable", AppVersionDBUtil.getInstance().isAppVersionAllowedToDelete(this.appdetails));
        final JSONObject isUpgradeDowngradeAvailable = AppVersionDBUtil.getInstance().validateIfUpgradeDowngradeAvailableForAppVersion(this.packageId, this.releaseLabelId);
        appJSON.put("is_upgrade_available", isUpgradeDowngradeAvailable.get("is_upgrade_available"));
        appJSON.put("is_downgrade_available", isUpgradeDowngradeAvailable.get("is_downgrade_available"));
        final JSONObject isConfigurationApplicable = AppConfigPolicyDBHandler.getInstance().validateIfAppConfigurationInsideAppRepoApplicable(this.appdetails);
        appJSON.put("is_oem_app", isConfigurationApplicable.get("is_oem_app"));
        appJSON.put("is_configured_under_profiles", isConfigurationApplicable.get("is_configured_under_profiles"));
        appJSON.put("is_for_all_customers", this.appdetails.get("APP_SHARED_SCOPE") == 1);
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            appJSON.put("is_user_in_role", DMUserHandler.isUserInAdminRole(loginID));
        }
        else {
            appJSON.put("is_user_in_role", true);
        }
        return appJSON;
    }
    
    public BaseAppDatahandler(final JSONObject apiRequest) {
        this.appdetails = new HashMap();
        this.packageId = null;
        this.defaultCategory = null;
        this.releaseLabelId = null;
        this.storeSyncKey = null;
        try {
            this.appType = apiRequest.optInt("app_type", -1);
            this.customerID = apiRequest.getLong("customerID");
            this.userID = apiRequest.getLong("userID");
            this.userName = String.valueOf(apiRequest.get("userName"));
            try {
                this.packageId = APIUtil.getResourceID(apiRequest, "app_id");
                this.releaseLabelId = APIUtil.getResourceID(apiRequest, "label_id");
            }
            catch (final NumberFormatException ex) {}
            this.appProps = new JSONObject();
        }
        catch (final Exception e) {
            BaseAppDatahandler.logger.log(Level.WARNING, "failed to initialize handler");
        }
    }
    
    @Override
    public JSONObject addEnterpriseApp(final JSONObject requestJson) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
            final Tika tika = new Tika();
            final String tempFilePathDM = (String)requestJson.get("app_file");
            String dispFilePathDM = null;
            String fullDispFilePathDM = null;
            if (requestJson.has("display_image")) {
                dispFilePathDM = (String)requestJson.get("display_image");
            }
            if (requestJson.has("full_image")) {
                fullDispFilePathDM = (String)requestJson.get("full_image");
            }
            jsonObject.put("parent_app_path", (Object)requestJson.optString("parent_app_path", (String)null));
            jsonObject.put("parent_full_image_path", (Object)requestJson.optString("parent_full_image_path", (String)null));
            jsonObject.put("parent_display_image_path", (Object)requestJson.optString("parent_display_image_path", (String)null));
            final long size = ApiFactoryProvider.getFileAccessAPI().getFileSize(tempFilePathDM);
            if (requestJson.has("app_info")) {
                this.appProps = requestJson.getJSONObject("app_info");
            }
            else {
                if (this.appProps.optBoolean("allowEditableFields", false)) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
                this.appProps = EnterpriseAppExtractor.getNewInstance(this.platformType).getAppDetails(tempFilePathDM);
            }
            if (this.appProps.has("extractError") && (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EditableAppInfo") || !this.appProps.optBoolean("allowEditableFields", false))) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.file", new Object[0]) });
            }
            this.validateEnterpriseAppData(requestJson, this.appProps);
            String identifier = String.valueOf(this.appProps.get("PackageName")).trim();
            String appName = String.valueOf(requestJson.get("app_name"));
            final JSONObject appMDMProps = this.getModifiedAppProps(Boolean.TRUE, identifier, appName);
            BaseAppDatahandler.logger.log(Level.INFO, "BaseAppDataHandler: addEnterpriseApp appMDMProps: {0}", new Object[] { null });
            if (appMDMProps != null && appMDMProps.has("IDENTIFIER")) {
                identifier = appMDMProps.getString("IDENTIFIER");
                if (this.appProps.has("PackageName")) {
                    this.appProps.put("PackageName", (Object)appMDMProps.getString("APP_NAME"));
                }
            }
            if (appMDMProps != null && appMDMProps.has("APP_NAME")) {
                appName = appMDMProps.getString("APP_NAME");
                if (this.appProps.has("APP_NAME")) {
                    this.appProps.put("APP_NAME", (Object)appMDMProps.getString("APP_NAME"));
                }
            }
            final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(identifier, this.platformType, this.customerID);
            if (appGroupID != null) {
                jsonObject.put("APP_GROUP_ID", (Object)appGroupID);
            }
            if (this.appProps.has("metaData")) {
                jsonObject.put("metaData", this.appProps.get("metaData"));
            }
            if (this.appProps.has("metaDataLoc")) {
                jsonObject.put("metaDataLoc", this.appProps.get("metaDataLoc"));
            }
            if (!new AppTrashModeHandler().isAppGroupIdMovedToTrash(appGroupID) && new AppsUtil().isAppExistsInPackage(identifier, this.platformType, this.customerID)) {
                throw new APIHTTPException("COM0010", new Object[] { ", app bundle identifier - " + this.appProps.get("PackageName") });
            }
            final File file = new File(tempFilePathDM);
            final String appSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(this.customerID);
            BaseAppDatahandler.logger.log(Level.INFO, "BaseAppDataHandler: addEnterpriseApp:Copying tempFile to AppSource Path:");
            final Long tempFileSize = file.length();
            BaseAppDatahandler.logger.log(Level.INFO, "FILESIZELOG: BaseAppDataHandler: addEnterpriseApp: TempFileSize: {0}", tempFileSize);
            final HashMap appFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(file, appSourceFolderDestPath, appSourceFolderDestPath, true, false);
            final String appFilePath = appFileSourceMap.get("destDCFileName");
            final String fileCheckSum = appFileSourceMap.get("fileCheckSum");
            jsonObject.put("APP_FILE", (Object)appFilePath);
            final Long appSourceFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(appFilePath);
            BaseAppDatahandler.logger.log(Level.INFO, "FILESIZELOG: BaseAppDataHandler: addEnterpriseApp: AppSourceFileSize: {0}", appSourceFileSize);
            if (!tempFileSize.equals(appSourceFileSize)) {
                BaseAppDatahandler.logger.log(Level.WARNING, "FILESIZELOG: BaseAppDataHandler: addEnterpriseApp: ***File Size Differs*** -> TempFileSize: {0} AppSourceFileSize: {1}", new Object[] { tempFileSize, appSourceFileSize });
            }
            if (dispFilePathDM != null) {
                final File tFile = new File(dispFilePathDM);
                final String contentType = tika.detect(tFile);
                if (!APIUtil.isAllowedImageMimeType(contentType)) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
                }
                final File dispFile = new File(dispFilePathDM);
                if (dispFile.length() > 50000L) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image.size", new Object[0]) });
                }
                final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(this.customerID);
                final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
                final String img = imgFileSourceMap.get("destDCFileName");
                jsonObject.put("DISPLAY_IMAGE", (Object)img);
            }
            if (fullDispFilePathDM != null) {
                final File tFile = new File(fullDispFilePathDM);
                final String contentType = tika.detect(tFile);
                if (!APIUtil.isAllowedImageMimeType(contentType)) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
                }
                final File dispFile = new File(fullDispFilePathDM);
                if (dispFile.length() > 50000L) {
                    throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image.size", new Object[0]) });
                }
                final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(this.customerID);
                final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
                final String img = imgFileSourceMap.get("destDCFileName");
                jsonObject.put("FULL_IMAGE", (Object)img);
            }
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)identifier);
            jsonObject.put("APP_VERSION", (Object)String.valueOf(this.appProps.get("VersionName")));
            jsonObject.put("APP_NAME_SHORT_VERSION", (Object)this.appProps.optString("version_code", "--"));
            if (this.appProps.has("APP_NAME")) {
                jsonObject.put("APP_EXTRACT_NAME", (Object)this.appProps.optString("APP_NAME"));
            }
            jsonObject.put("APP_NAME", (Object)appName);
            jsonObject.put("APP_TITLE", (Object)appName);
            jsonObject.put("PROFILE_NAME", (Object)appName);
            Long categoryId = AppsUtil.getInstance().getAppCategoryId(requestJson.optLong("app_category_id", -1L), this.platformType);
            if (categoryId == null || categoryId == -1L) {
                categoryId = AppsUtil.getInstance().getPlatformDefaultCategory(this.platformType, this.defaultCategory);
            }
            jsonObject.put("APP_CATEGORY_ID", (Object)categoryId);
            final String description = requestJson.optString("description");
            jsonObject.put("PLATFORM_TYPE", this.platformType);
            jsonObject.put("COLLECTION_ID", 0L);
            jsonObject.put("CONFIG_DATA_ITEM_ID", 0L);
            jsonObject.put("PACKAGE_TYPE", requestJson.getInt("app_type"));
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            jsonObject.put("APP_CONFIG", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            final JSONObject packageJSON = new JSONObject();
            AppsUtil.getInstance();
            final Integer supportedDevices = AppsUtil.getSupportedDevicesValues(requestJson.getInt("supported_devices"), this.platformType);
            if (!this.validateEnterpriseSupport(supportedDevices) || supportedDevices == null || supportedDevices == -1) {
                throw new APIHTTPException("COM0005", new Object[] { "Supported_devices" });
            }
            packageJSON.put("SUPPORTED_DEVICES", (Object)supportedDevices);
            if (!MDMStringUtils.isEmpty(description)) {
                packageJSON.put("DESCRIPTION", (Object)description);
                jsonObject.put("PROFILE_DESCRIPTION", (Object)description);
            }
            else {
                jsonObject.put("PROFILE_DESCRIPTION", (Object)"");
            }
            packageJSON.put("FILE_UPLOAD_SIZE", size);
            packageJSON.put("APP_CHECKSUM", (Object)fileCheckSum);
            if (!MDMStringUtils.isEmpty(requestJson.optString("app_checksum"))) {
                packageJSON.put("APP_CHECKSUM", (Object)requestJson.get("app_checksum"));
            }
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageJSON);
            jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
            jsonObject.put("PACKAGE_ADDED_BY", (Object)this.userID);
            if (requestJson.has("CUSTOMIZED_APP_URL".toLowerCase())) {
                jsonObject.put("CUSTOMIZED_APP_URL", (Object)requestJson.optString("CUSTOMIZED_APP_URL".toLowerCase()));
            }
            jsonObject.put("RELEASE_LABEL_ID", requestJson.optLong("label_id", (long)AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(this.customerID)));
            if (requestJson.has("version_label")) {
                jsonObject.put("RELEASE_LABEL_DISPLAY_NAME", requestJson.get("version_label"));
            }
            final Boolean isForAllCustomers = requestJson.optBoolean("is_for_all_customers");
            jsonObject.put("APP_SHARED_SCOPE", (int)(((boolean)isForAllCustomers) ? 1 : 0));
            jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            jsonObject = this.modifyEnterpriseAppData(jsonObject, requestJson);
            jsonObject.put("hasAppFile", (Object)Boolean.TRUE);
            MDMRestAPIFactoryProvider.getMdmAppMgmtHandlerAPI().addOrUpdatePackageInRepository(jsonObject);
            final List profileIds = new ArrayList();
            profileIds.add(jsonObject.getLong("PROFILE_ID"));
            APIActionsHandler.getInstance().invokeAPIActionProfileListener(profileIds, profileIds, null, 4);
            MICSAppRepositoryFeatureController.addTrackingData(this.platformType, MICSAppRepositoryFeatureController.AppOperation.ADD_APP, true, tempFilePathDM.toLowerCase().endsWith("msi"));
        }
        catch (final JSONException e) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "Exception in adding enterprise app", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[] { "" });
        }
        return jsonObject;
    }
    
    protected abstract JSONObject modifyEnterpriseAppData(final JSONObject p0, final JSONObject p1) throws Exception;
    
    protected void validateEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJSON) throws Exception {
    }
    
    protected boolean validateEnterpriseSupport(final Integer supportedDevice) throws Exception {
        return true;
    }
    
    @Override
    public JSONObject deleteAppConfiguration(final JSONObject jsonObject) throws Exception {
        final JSONObject configJSON = new JSONObject();
        configJSON.put("CUSTOMER_ID", (Object)this.customerID);
        this.packageId = APIUtil.getResourceID(jsonObject, "app_id");
        this.setReleaseLabelId(jsonObject);
        final HashMap hashMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        final Long appConfigID = hashMap.get("CONFIG_DATA_ITEM_ID");
        final Long appGroupID = hashMap.get("APP_GROUP_ID");
        configJSON.put("APP_GROUP_ID", (Object)appGroupID);
        configJSON.put("LAST_MODIFIED_BY", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        configJSON.put("CONFIG_DATA_ITEM_ID", (Object)appConfigID);
        AppConfigDataPolicyHandler.getInstance(this.platformType).deleteAppConfigAndInvokeCommand(appConfigID, configJSON);
        final JSONObject response = new JSONObject();
        response.put("app_id", (Object)this.packageId);
        return response;
    }
    
    @Override
    public JSONObject updateEnterpriseApp(final JSONObject message) throws Exception {
        final APIUtil apiUtil = APIUtil.getNewInstance();
        final JSONObject jsonObject = new JSONObject();
        this.packageId = APIUtil.getResourceID(message, "app_id");
        this.releaseLabelId = APIUtil.getResourceID(message, "label_id");
        if (this.releaseLabelId == -1L) {
            this.releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(this.customerID);
        }
        this.releaseLabelId = AppsUtil.getInstance().validateIfAppIsToBeConvertedfromPlaystore(message, this.releaseLabelId, this.customerID, this.platformType);
        Long releaseLabelIDForCloning = this.releaseLabelId;
        this.validateIfAppFound(this.packageId, this.customerID);
        if (!message.has("app_file") || !message.has("force_update_in_label") || message.getBoolean("force_update_in_label") == Boolean.TRUE) {
            this.validateIfAppAndReleaseLabelFound();
        }
        if (message.has("app_file") && message.has("force_update_in_label") && message.getBoolean("force_update_in_label") == Boolean.FALSE) {
            releaseLabelIDForCloning = AppVersionDBUtil.getInstance().getApprovedReleaseLabelForGivePackage(this.packageId, this.customerID);
        }
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, releaseLabelIDForCloning);
        jsonObject.put("RELEASE_LABEL_ID", (Object)this.releaseLabelId);
        if (message.has("force_update_in_label")) {
            jsonObject.put("force_update_as_beta", message.get("force_update_in_label"));
        }
        jsonObject.put("parent_app_path", (Object)message.optString("parent_app_path", (String)null));
        jsonObject.put("parent_full_image_path", (Object)message.optString("parent_full_image_path", (String)null));
        jsonObject.put("parent_display_image_path", (Object)message.optString("parent_display_image_path", (String)null));
        final int appType = Integer.valueOf(String.valueOf(this.appdetails.get("PACKAGE_TYPE")));
        final int platform = Integer.valueOf(String.valueOf(this.appdetails.get("PLATFORM_TYPE")));
        String identifier = this.appdetails.get("IDENTIFIER");
        String versionName = this.appdetails.get("APP_VERSION");
        String versionCode = this.appdetails.getOrDefault("APP_NAME_SHORT_VERSION", "--");
        long size = this.appdetails.get("FILE_UPLOAD_SIZE");
        String fileCheckSum = this.appdetails.getOrDefault("APP_CHECKSUM", "");
        final JSONObject requestJSON = message;
        this.validateandModifyAppType(message, this.appdetails);
        if (requestJSON.has("app_file")) {
            Boolean forceUpdateAsBeta = Boolean.TRUE;
            if (requestJSON.has("force_update_in_label")) {
                forceUpdateAsBeta = requestJSON.getBoolean("force_update_in_label");
            }
            final String tempFilePath = (String)requestJSON.get("app_file");
            if (message.has("app_info")) {
                this.appProps = message.getJSONObject("app_info");
            }
            else {
                if (this.appProps.optBoolean("allowEditableFields", false)) {
                    throw new APIHTTPException("COM0009", new Object[0]);
                }
                this.appProps = EnterpriseAppExtractor.getNewInstance(this.platformType).getAppDetails(tempFilePath);
            }
            if (this.appProps.has("extractError") && (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EditableAppInfo") || !this.appProps.optBoolean("allowEditableFields", false))) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.file", new Object[0]) });
            }
            if (requestJSON.has("app_name")) {
                final String appName = (String)requestJSON.get("app_name");
                final JSONObject appMDMProps = this.getModifiedAppProps(Boolean.TRUE, identifier, appName);
                BaseAppDatahandler.logger.log(Level.INFO, "BaseAppDataHandler: updateEnterpriseApp appMDMProps: {0}", new Object[] { appMDMProps });
                if (appMDMProps != null && appMDMProps.has("IDENTIFIER")) {
                    this.appProps.put("PackageName", (Object)appMDMProps.getString("IDENTIFIER"));
                    identifier = appMDMProps.getString("IDENTIFIER");
                }
                if (appMDMProps != null && appMDMProps.has("APP_NAME")) {
                    if (requestJSON.has("app_name") && !requestJSON.getString("app_name").equals(appMDMProps.getString("APP_NAME"))) {
                        requestJSON.put("app_name", (Object)appMDMProps.getString("APP_NAME"));
                    }
                    if (this.appProps.has("APP_EXTRACT_NAME")) {
                        this.appProps.put("APP_EXTRACT_NAME", (Object)appMDMProps.getString("APP_NAME"));
                    }
                }
            }
            size = ApiFactoryProvider.getFileAccessAPI().getFileSize(tempFilePath);
            final File file = new File(tempFilePath);
            final String appSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(CustomerInfoUtil.getInstance().getCustomerId());
            BaseAppDatahandler.logger.log(Level.INFO, "BaseAppDataHandler: updateEnterpriseApp: Copying tempFile to AppSource Path:");
            final Long tempFileSize = file.length();
            BaseAppDatahandler.logger.log(Level.INFO, "FILESIZELOG: BaseAppDataHandler: updateEnterpriseApp: TempFileSize: {0}", tempFileSize);
            final HashMap appFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(file, appSourceFolderDestPath, appSourceFolderDestPath, true, false);
            final String appFilePath = appFileSourceMap.get("destDCFileName");
            jsonObject.put("APP_FILE", (Object)appFilePath);
            fileCheckSum = appFileSourceMap.get("fileCheckSum");
            final Long appSourceFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(appFilePath);
            BaseAppDatahandler.logger.log(Level.INFO, "FILESIZELOG: BaseAppDataHandler: updateEnterpriseApp: AppSourceFileSize: {0}", appSourceFileSize);
            if (!tempFileSize.equals(appSourceFileSize)) {
                BaseAppDatahandler.logger.log(Level.WARNING, "FILESIZELOG: BaseAppDataHandler: updateEnterpriseApp: ***File Size Differs*** -> TempFileSize: {0} AppSourceFileSize: {1}", new Object[] { tempFileSize, appSourceFileSize });
            }
            this.validateEnterpriseAppData(requestJSON, this.appProps);
            if (this.appProps.has("metaData")) {
                jsonObject.put("metaData", this.appProps.get("metaData"));
            }
            if (this.appProps.has("metaDataLoc")) {
                jsonObject.put("metaDataLoc", this.appProps.get("metaDataLoc"));
            }
            if (!this.allowPackageUpdate(String.valueOf(this.appProps.get("PackageName")), identifier)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.bundleid", new Object[0]) });
            }
            identifier = String.valueOf(this.appProps.get("PackageName"));
            versionName = String.valueOf(this.appProps.get("VersionName"));
            versionCode = this.appProps.optString("version_code", "--");
            final JSONObject validationJSON = new JSONObject().put("PLATFORM_TYPE", platform).put("CUSTOMER_ID", (Object)this.customerID).put("packagename", (Object)identifier).put("APP_VERSION", (Object)versionName).put("APP_NAME_SHORT_VERSION", (Object)versionCode).put("app_name", this.appdetails.get("APP_NAME")).put("force_update_in_label", (Object)forceUpdateAsBeta).put("RELEASE_LABEL_ID", (Object)this.releaseLabelId).put("app_id", (Object)this.packageId);
            AppVersionHandler.getInstance(this.platformType).validateAppVersionForUploadWithReleaseLabel(validationJSON);
            this.appdetails.put("APP_VERSION", versionName);
            this.appdetails.put("APP_NAME_SHORT_VERSION", versionCode);
            jsonObject.put("RELEASE_LABEL_ID", (Object)this.releaseLabelId);
            final Long appConfigID = this.appdetails.get("APP_CONFIG_ID");
            final Long appID = this.appdetails.get("APP_ID");
            JSONArray configData = new JSONArray();
            final AppConfigDataHandler appConfigDataHandler = new AppConfigDataHandler();
            JSONObject configTemplate = null;
            try {
                final Long configDataItemId = this.appdetails.get("CONFIG_DATA_ITEM_ID");
                configTemplate = new JSONObject(appConfigDataHandler.getAppConfigTemplateFromConfigDataItemID(configDataItemId));
            }
            catch (final RuntimeException e) {
                BaseAppDatahandler.logger.log(Level.FINE, "No app config template defined ");
            }
            if (appConfigID != null) {
                final String config = appConfigDataHandler.getAppConfig((long)appConfigID);
                if (!MDMStringUtils.isEmpty(config)) {
                    configData = new JSONArray(config);
                }
            }
            if (configTemplate != null && configData.length() > 0) {
                jsonObject.put("APP_CONFIGURATION", (Object)configData);
                jsonObject.put("APP_CONFIG_TEMPLATE", (Object)new JSONObject().put("APP_CONFIG_FORM", (Object)configTemplate));
            }
        }
        if (requestJSON.has("display_image")) {
            final Tika tika = new Tika();
            final String dispFilePath = (String)requestJSON.get("display_image");
            final File file = new File(dispFilePath);
            final String contentType = tika.detect(file);
            if (!APIUtil.isAllowedImageMimeType(contentType)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
            }
            final File dispFile = new File(dispFilePath);
            final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(CustomerInfoUtil.getInstance().getCustomerId());
            final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
            final String img = imgFileSourceMap.get("destDCFileName");
            jsonObject.put("DISPLAY_IMAGE", (Object)img);
        }
        if (requestJSON.has("full_image")) {
            final Tika tika = new Tika();
            final String dispFilePath = (String)requestJSON.get("full_image");
            final File file = new File(dispFilePath);
            final String contentType = tika.detect(file);
            if (!APIUtil.isAllowedImageMimeType(contentType)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
            }
            final File dispFile = new File(dispFilePath);
            final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(CustomerInfoUtil.getInstance().getCustomerId());
            final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
            final String img = imgFileSourceMap.get("destDCFileName");
            jsonObject.put("FULL_IMAGE", (Object)img);
        }
        if (requestJSON.has("app_name") && !MDMStringUtils.isEmpty(requestJSON.getString("app_name"))) {
            jsonObject.put("APP_NAME", (Object)String.valueOf(requestJSON.get("app_name")));
            jsonObject.put("APP_TITLE", (Object)String.valueOf(requestJSON.get("app_name")));
            jsonObject.put("PROFILE_NAME", (Object)String.valueOf(requestJSON.get("app_name")));
        }
        else {
            requestJSON.put("app_name", this.appdetails.get("PROFILE_NAME"));
        }
        Integer supportedDevices;
        if (requestJSON.has("supported_devices") && this.validateEnterpriseSupport(requestJSON.getInt("supported_devices"))) {
            AppsUtil.getInstance();
            supportedDevices = AppsUtil.getSupportedDevicesValues(requestJSON.optInt("supported_devices"), platform);
        }
        else {
            supportedDevices = Integer.valueOf(this.appdetails.get("SUPPORTED_DEVICES").toString());
        }
        if (requestJSON.has("app_category_id")) {
            Long categoryId = AppsUtil.getInstance().getAppCategoryId(requestJSON.optLong("app_category_id", -1L), this.platformType);
            if (categoryId == null || categoryId == -1L) {
                categoryId = AppsUtil.getInstance().getPlatformDefaultCategory(this.platformType, this.defaultCategory);
            }
            jsonObject.put("APP_CATEGORY_ID", (Object)categoryId);
        }
        else {
            jsonObject.put("APP_CATEGORY_ID", this.appdetails.get("APP_CATEGORY_ID"));
        }
        jsonObject.put("command_line", (Object)requestJSON.optString("command_line", String.valueOf(this.appdetails.get("COMMAND_LINE"))));
        jsonObject.put("BUNDLE_IDENTIFIER", (Object)identifier);
        jsonObject.put("APP_VERSION", (Object)versionName);
        jsonObject.put("APP_NAME_SHORT_VERSION", (Object)versionCode);
        jsonObject.put("APP_NAME", requestJSON.get("app_name"));
        jsonObject.put("APP_TITLE", requestJSON.get("app_name"));
        jsonObject.put("PROFILE_NAME", requestJSON.get("app_name"));
        final String description = requestJSON.optString("description", String.valueOf(this.appdetails.get("DESCRIPTION")));
        jsonObject.put("PLATFORM_TYPE", this.platformType);
        jsonObject.put("COLLECTION_ID", this.appdetails.get("COLLECTION_ID"));
        jsonObject.put("PROFILE_ID", this.appdetails.get("PROFILE_ID"));
        jsonObject.put("SECURITY_TYPE", -1);
        jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
        jsonObject.put("APP_CONFIG", true);
        final JSONObject appPolicyJSON = new JSONObject();
        appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
        appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
        appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
        jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
        jsonObject.put("APP_EXTRACT_NAME", (Object)this.appProps.optString("APP_EXTRACT_NAME", (String)requestJSON.get("app_name")));
        jsonObject.put("PACKAGE_TYPE", this.appdetails.get("PACKAGE_TYPE"));
        if (message.has("app_file")) {
            jsonObject.put("CUSTOMIZED_APP_URL", (Object)requestJSON.optString("CUSTOMIZED_APP_URL".toLowerCase()));
        }
        else {
            jsonObject.put("CUSTOMIZED_APP_URL", (Object)requestJSON.optString("CUSTOMIZED_APP_URL".toLowerCase(), (String)this.appdetails.get("CUSTOMIZED_APP_URL")));
        }
        final JSONObject packageJSON = new JSONObject();
        if (supportedDevices != null) {
            packageJSON.put("SUPPORTED_DEVICES", (Object)supportedDevices);
        }
        if (!MDMStringUtils.isEmpty(description)) {
            packageJSON.put("DESCRIPTION", (Object)description);
            jsonObject.put("PROFILE_DESCRIPTION", (Object)description);
        }
        else {
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"");
        }
        packageJSON.put("FILE_UPLOAD_SIZE", size);
        packageJSON.put("APP_CHECKSUM", (Object)fileCheckSum);
        if (!MDMStringUtils.isEmpty(requestJSON.optString("app_checksum"))) {
            packageJSON.put("APP_CHECKSUM", (Object)requestJSON.get("app_checksum"));
        }
        packageJSON.put("APP_FILE_LOC", this.appdetails.get("APP_FILE_LOC"));
        jsonObject.put("MdPackageToAppDataFrom", (Object)packageJSON);
        jsonObject.put("CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId());
        jsonObject.put("APP_FILE_LOC", this.appdetails.get("APP_FILE_LOC"));
        boolean ipaImgavailable = false;
        if (this.appdetails.get("DISPLAY_IMAGE_LOC") != null && this.appdetails.get("FULL_IMAGE_LOC") != null) {
            ipaImgavailable = true;
            jsonObject.put("FULL_IMAGE_LOC", this.appdetails.get("FULL_IMAGE_LOC"));
            jsonObject.put("DISPLAY_IMAGE_LOC", this.appdetails.get("DISPLAY_IMAGE_LOC"));
            packageJSON.put("FULL_IMAGE_LOC", this.appdetails.get("FULL_IMAGE_LOC"));
            packageJSON.put("DISPLAY_IMAGE_LOC", this.appdetails.get("DISPLAY_IMAGE_LOC"));
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageJSON);
        }
        if (!MDMStringUtils.isEmpty(String.valueOf(this.appdetails.get("DISPLAY_IMAGE_LOC"))) && MDMStringUtils.isEmpty(requestJSON.optString("display_image")) && !requestJSON.optBoolean("displayimagedelete", false)) {
            packageJSON.put("DISPLAY_IMAGE_LOC", this.appdetails.get("DISPLAY_IMAGE_LOC"));
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageJSON);
        }
        jsonObject.put("ipaimagesavailable", ipaImgavailable);
        this.modifyEnterpriseAppData(jsonObject, requestJSON);
        jsonObject.put("PACKAGE_ADDED_BY", (Object)this.userID);
        jsonObject.put("hasAppFile", message.has("app_file"));
        if (message.has("version_label")) {
            jsonObject.put("RELEASE_LABEL_DISPLAY_NAME", message.get("version_label"));
        }
        MDMRestAPIFactoryProvider.getMdmAppMgmtHandlerAPI().addOrUpdatePackageInRepository(jsonObject);
        final List profileIds = new ArrayList();
        profileIds.add(jsonObject.getLong("PROFILE_ID"));
        APIActionsHandler.getInstance().invokeAPIActionProfileListener(profileIds, profileIds, null, 4);
        AppsUtil.validateAndPerformUpdateAllOnAppUpdate(requestJSON, jsonObject);
        return jsonObject;
    }
    
    @Override
    public JSONObject getAppDetailsFromAppFile(final JSONObject jsonObject) throws Exception {
        final String filepath = (String)jsonObject.get("file_path");
        this.appProps = EnterpriseAppExtractor.getNewInstance(this.platformType).getAppDetails(filepath);
        if (this.appProps.has("extractError")) {
            String errorMsg = "";
            if (this.appProps.has("errorMsg")) {
                errorMsg = String.valueOf(this.appProps.get("errorMsg"));
            }
            BaseAppDatahandler.logger.log(Level.INFO, "BaseAppDataHandler : getAppDetailsFromAppFile errorMsg :", I18N.getMsg(errorMsg, new Object[0]));
            throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.file", new Object[0]) });
        }
        return this.appProps;
    }
    
    public void validateIfAppFound(final Long app, final Long customerID) throws APIHTTPException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)app, 0).and(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdPackage");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("PACKAGE_ID"))));
            }
            if (apps.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { app });
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AppsUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public boolean allowPackageUpdate(final String fileValue, final String providedValue) {
        return fileValue.equals(providedValue);
    }
    
    @Override
    public Object getCategoryCode(final JSONObject object) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("categories", (Object)AppsUtil.getInstance().getAPIAppCategoryNames(this.platformType).toString());
        return jsonObject;
    }
    
    @Override
    public JSONObject getAppSuggestion(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    public void updateStoreAppCheck(final JSONObject message) throws Exception {
        final Long packageId = APIUtil.getResourceID(message, "app_id");
        this.validateIfAppFound(packageId, this.customerID);
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, this.releaseLabelId);
        final String identifier = this.appdetails.get("IDENTIFIER");
        final String appName = this.appdetails.get("APP_NAME");
        if (message.getJSONObject("msg_body").has("bundle_identifier") && !message.getJSONObject("msg_body").get("bundle_identifier").equals(identifier)) {
            throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.bundleidinvalid", new Object[0]) });
        }
        message.getJSONObject("msg_body").put("bundle_identifier", (Object)identifier);
        if (!message.getJSONObject("msg_body").has("app_name") || (message.getJSONObject("msg_body").has("app_name") && MDMStringUtils.isEmpty(message.getJSONObject("msg_body").getString("app_name")))) {
            message.getJSONObject("msg_body").put("app_name", (Object)appName);
        }
    }
    
    @Override
    public Object getCountryCode(final JSONObject requestJSON) throws Exception {
        final JSONArray jsonarray = MDMRestAPIFactoryProvider.getAPIUtil().getCountryNames();
        final JSONObject resp = new JSONObject();
        resp.put("countries", (Object)jsonarray.toString());
        return resp;
    }
    
    @Override
    public JSONObject getAppPermissionList(final JSONObject message) throws Exception {
        final Long packageID = APIUtil.getResourceID(message, "app_id");
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        final HashMap hashMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageID, this.releaseLabelId);
        final Long appId = hashMap.get("APP_ID");
        final JSONObject retData = new JSONObject();
        JSONArray appPermissions = new JSONArray();
        if (appId != null) {
            appPermissions = new PermissionHandler().getRequestedPermissionForApp((long)appId);
        }
        retData.put("APP_PERMISSION", (Object)appPermissions);
        return retData;
    }
    
    @Override
    public JSONObject updateAppsForAllDevices(final JSONObject message) throws Exception {
        this.packageId = APIUtil.getResourceID(message, "app_id");
        this.setReleaseLabelId(message);
        this.validateIfAppAndReleaseLabelFound();
        final JSONObject messageBody = message.getJSONObject("msg_body");
        final Properties properties = new Properties();
        final Boolean isSilentInstall = messageBody.optBoolean("silent_install");
        final Boolean isNotify = messageBody.optBoolean("notify_user_via_email");
        final Boolean isForceUpdate = messageBody.optBoolean("force_update");
        final Long appGroupId = messageBody.getLong("appGroupId");
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
        ((Hashtable<String, Boolean>)properties).put("isNotify", isNotify);
        ((Hashtable<String, Boolean>)properties).put("forceUpdate", isForceUpdate);
        ((Hashtable<String, Long>)properties).put("RELEASE_LABEL_ID", this.releaseLabelId);
        MDMAppMgmtHandler.getInstance().performUpdateAllForAppGroup(appGroupId, this.customerID, this.userID, properties);
        return null;
    }
    
    @Override
    public JSONObject addProvProfileForApp(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject getProvProfileDetailsFromAppId(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject getProvProfileDetails(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public void updatePrerequsiteForAddApp(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject getPrerequsiteForAddApp(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    protected void setReleaseLabelId(final JSONObject requestJson) throws JSONException, DataAccessException {
        this.customerID = APIUtil.getCustomerID(requestJson);
        if (APIUtil.getResourceID(requestJson, "label_id") != -1L) {
            this.releaseLabelId = APIUtil.getResourceID(requestJson, "label_id");
        }
        else {
            this.releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(this.customerID);
        }
    }
    
    public void validateIfAppAndReleaseLabelFound() {
        try {
            final Long appPackageId = this.packageId;
            final Long releaseLabelId = this.releaseLabelId;
            final SelectQuery packageExistsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            packageExistsQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            packageExistsQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria packageIdCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appPackageId, 0);
            final Criteria releaseLabelIdCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            packageExistsQuery.setCriteria(packageIdCriteria.and(releaseLabelIdCriteria));
            packageExistsQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
            final DataObject packageReleaseLabelDO = MDMUtil.getPersistence().get(packageExistsQuery);
            final Iterator<Row> mdPackageRows = packageReleaseLabelDO.getRows("MdPackage");
            final List<Long> packageIds = new ArrayList<Long>();
            while (mdPackageRows.hasNext()) {
                final Row mdPackageRow = mdPackageRows.next();
                final Long appPackageIdFromDB = (Long)mdPackageRow.get("PACKAGE_ID");
                if (appPackageIdFromDB != null && appPackageId.equals(appPackageIdFromDB)) {
                    packageIds.add(appPackageIdFromDB);
                }
            }
            if (packageIds.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { String.valueOf(releaseLabelId) });
            }
        }
        catch (final DataAccessException ex) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "DataAccessException while validating app id and given release label id exists", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void validateIfAppReleaseLabelIsNotInProductionLabel() {
        try {
            AppVersionDBUtil.getInstance().validateIfAppReleaseLabelIsNotInProductionLabel(this.releaseLabelId);
        }
        catch (final DataAccessException ex) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "DataAccessException while validating if given release label is not in a production release label", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Deprecated
    @Override
    public JSONObject markAppAsStable(final JSONObject apiRequestJson) throws DataAccessException, JSONException, Exception {
        this.validateIfAppAndReleaseLabelFound();
        this.validateIfAppReleaseLabelIsNotInProductionLabel();
        final int platformType = AppsUtil.getInstance().getPlatformTypeFromPackageID(this.packageId);
        final JSONObject markAsStableRequestJson = new JSONObject();
        markAsStableRequestJson.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequestJson));
        markAsStableRequestJson.put("USER_ID", (Object)APIUtil.getUserID(apiRequestJson));
        markAsStableRequestJson.put("PACKAGE_ID", (Object)this.packageId);
        markAsStableRequestJson.put("RELEASE_LABEL_ID", (Object)this.releaseLabelId);
        final JSONArray releaseLabelIDsToMerge = apiRequestJson.getJSONObject("msg_body").getJSONArray("release_label_ids");
        final List<Long> releaseLabelsToMergeList = JSONUtil.getInstance().convertLongJSONArrayTOList(releaseLabelIDsToMerge);
        final HashMap releaseChannelIDToNameMap = AppVersionDBUtil.getInstance().getChannelNameMap(releaseLabelsToMergeList);
        final Set releaseLabelNameSet = new HashSet(releaseChannelIDToNameMap.values());
        final String releaseLabelName = AppVersionDBUtil.getInstance().getChannelName(this.releaseLabelId);
        markAsStableRequestJson.put("release_labels_to_merge", (Object)releaseLabelIDsToMerge);
        final SelectQuery appDetailsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        appDetailsQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appDetailsQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appDetailsQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)this.packageId, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)this.releaseLabelId, 0);
        appDetailsQuery.setCriteria(packageCriteria.and(releaseLabelCriteria));
        appDetailsQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        appDetailsQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        appDetailsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        appDetailsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
        appDetailsQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        final DataObject dao = MDMUtil.getPersistence().get(appDetailsQuery);
        final JSONObject remarkArgsJSON = new JSONObject();
        if (!dao.isEmpty()) {
            final Row appGroupToCollectionRow = dao.getFirstRow("AppGroupToCollection");
            markAsStableRequestJson.put("APP_GROUP_ID", appGroupToCollectionRow.get("APP_GROUP_ID"));
            markAsStableRequestJson.put("COLLECTION_ID", appGroupToCollectionRow.get("COLLECTION_ID"));
            final Row mdAppDetailsRow = dao.getFirstRow("MdAppDetails");
            remarkArgsJSON.put("APP_NAME", (Object)mdAppDetailsRow.get("APP_NAME"));
            remarkArgsJSON.put("APP_VERSION", (Object)mdAppDetailsRow.get("APP_VERSION"));
        }
        AppVersionHandler.getInstance(platformType).checkIfChannelAllowedToBeMerged(markAsStableRequestJson);
        final JSONObject statusJson = AppsUtil.getInstance().handleDbChangesForMarkAsStable(markAsStableRequestJson);
        if (statusJson.get("status").equals(200)) {
            final String remarksArgString = remarkArgsJSON.get("APP_NAME") + "@@@" + remarkArgsJSON.get("APP_VERSION") + "@@@" + releaseLabelName + "@@@" + MDMUtil.getInstance().convertSetToString(releaseLabelNameSet);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2038, null, APIUtil.getUserName(apiRequestJson), "mdm.evt.appmgmt.app_merged", remarksArgString, APIUtil.getCustomerID(apiRequestJson));
        }
        if (statusJson.get("status").equals(200) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotDistributeAppOnMarkAsStable")) {
            final JSONObject messageBody = apiRequestJson.getJSONObject("msg_body");
            final Boolean distributeUpdate = messageBody.optBoolean("distribute_update", (boolean)Boolean.FALSE);
            if (distributeUpdate) {
                final Properties properties = new Properties();
                final Boolean isSilentInstall = messageBody.optBoolean("silent_install");
                final Boolean isNotify = messageBody.optBoolean("notify_user_via_email");
                ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
                ((Hashtable<String, Boolean>)properties).put("isNotify", isNotify);
                ((Hashtable<String, Object>)properties).put("RELEASE_LABEL_ID", statusJson.get("channel_retained"));
                final Long appGroupId = markAsStableRequestJson.getLong("APP_GROUP_ID");
                final HashMap resourceMap = MDMAppMgmtHandler.getInstance().performUpdateAllForAppGroup(appGroupId, this.customerID, this.userID, properties);
                final List groupList = resourceMap.get("groupList");
                final List resList = resourceMap.get("resourceList");
                final String remarksArgString2 = remarkArgsJSON.get("APP_NAME") + "@@@" + remarkArgsJSON.get("APP_VERSION") + "@@@" + releaseLabelName + "@@@" + MDMUtil.getInstance().convertSetToString(releaseLabelNameSet) + "@@@" + groupList.size() + "@@@" + resList.size();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2039, null, APIUtil.getUserName(apiRequestJson), "mdm.evt.appmgmt.merged_app_auto_distributed", remarksArgString2, APIUtil.getCustomerID(apiRequestJson));
            }
            else {
                Logger.getLogger("MDMProfileDistributionLog").log(Level.INFO, "Stable app distribution skipped as user selected to skip app distribution - {0}", new Object[] { messageBody });
            }
        }
        else {
            Logger.getLogger("MDMProfileDistributionLog").log(Level.INFO, "Stable app distribution skipped as markAsStable response is error (or) global feature param disables app distribution on mark as stable - {0}", new Object[] { statusJson });
        }
        return statusJson;
    }
    
    @Override
    @Deprecated
    public JSONObject getChannelsToMerge(final JSONObject apiRequestJson) throws Exception {
        this.validateIfAppAndReleaseLabelFound();
        this.validateIfAppReleaseLabelIsNotInProductionLabel();
        final int platformType = AppsUtil.getInstance().getPlatformTypeFromPackageID(this.packageId);
        final JSONObject channelsToMergeDetails = AppVersionHandler.getInstance(platformType).getPossibleChannelsToMergeApp(apiRequestJson);
        return channelsToMergeDetails;
    }
    
    @Override
    public JSONObject getAvailableChannels(final JSONObject apiRequestJSON) throws Exception {
        final JSONObject availableChannelJSON = AppVersionDBUtil.getInstance().getAvailableReleaseLabelIDSForNewVersionOfApp(apiRequestJSON);
        return availableChannelJSON;
    }
    
    @Override
    public JSONObject verifyAccountRemoval() throws Exception {
        final List<Long> accountApps = MDMAppMgmtHandler.getInstance().getAccountApps(this.platformType, this.customerID, Boolean.FALSE);
        final int count = accountApps.size();
        JSONObject responseJSON = new JSONObject();
        try {
            if (count > 0) {
                final Long[] accountAppsArray = accountApps.toArray(new Long[0]);
                final Long[] profileIds = AppsUtil.getInstance().getProfileIDSFromAppGroup(accountAppsArray);
                final HashMap params = new HashMap();
                params.put("appGroupIds", accountApps);
                params.put("CustomerID", this.customerID);
                params.put("profileIds", Arrays.asList(profileIds));
                params.put("packageIds", new ArrayList());
                final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
                final JSONObject errorMessages = appTrashModeHandler.checkMoveAppsToTrashFesability(params);
                responseJSON = MDMAppMgmtHandler.getInstance().getMaxDistributedCountForAccountApps(accountAppsArray);
                if (errorMessages != null && errorMessages.has("ErrorMessage")) {
                    responseJSON.put("error_message", errorMessages.get("ErrorMessage"));
                }
            }
        }
        catch (final Exception e) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "Exception while fetching verification msg for account removal", e);
        }
        return responseJSON;
    }
    
    @Override
    public JSONObject getAutoAppUpdateConfig(final JSONObject messageBody) throws Exception {
        messageBody.put("customerId", (Object)this.customerID);
        return MDMAppMgmtHandler.getInstance().getAutoAppUpdateConfig(messageBody);
    }
    
    @Override
    public JSONObject addAutoAppUpdateConfig(final JSONObject messageBody) throws Exception {
        messageBody.put("customerId", (Object)this.customerID);
        messageBody.put("userId", (Object)this.userID);
        return JSONUtil.toJSON("app_update_conf_id", MDMAppMgmtHandler.getInstance().addAutoAppUpdateConfig(messageBody));
    }
    
    @Override
    public JSONObject updateAutoAppUpdateConfig(final JSONObject messageBody) throws Exception {
        messageBody.put("customerId", (Object)this.customerID);
        messageBody.put("userId", (Object)this.userID);
        MDMAppMgmtHandler.getInstance().updateAutoAppUpdateConfig(messageBody);
        return null;
    }
    
    @Override
    public JSONObject deleteAutoAppUpdateConfig(final JSONObject messageBody) throws Exception {
        messageBody.put("customerId", (Object)this.customerID);
        MDMAppMgmtHandler.getInstance().deleteAutoAppUpdateConfig(messageBody);
        return null;
    }
    
    @Override
    public JSONObject getAutoAppUpdateInfoForApp(final JSONObject jsonObject) throws Exception {
        final Long packageId = APIUtil.getResourceID(jsonObject, "app_id");
        return MDMAppMgmtHandler.getInstance().getUpdateConfigurationForApp(packageId, this.customerID);
    }
    
    @Override
    public JSONObject getAutoAppUpdateConfigList() throws Exception {
        return MDMAppMgmtHandler.getInstance().getAutoAppUpdateConfigList(JSONUtil.toJSON("customerId", this.customerID));
    }
    
    protected void validateIfAppInTrash() {
        Boolean isAppMovedToTrash = Boolean.FALSE;
        try {
            isAppMovedToTrash = this.appdetails.get("IS_MOVED_TO_TRASH");
        }
        catch (final Exception ex) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "Exception in validateIfAppInTrash method", ex);
        }
        if (isAppMovedToTrash) {
            throw new APIHTTPException("APP0018", new Object[0]);
        }
    }
    
    public JSONObject getDefaultAppForm() {
        final JSONObject appJSON = new JSONObject();
        appJSON.put("APP_CONFIG", (Object)Boolean.TRUE);
        appJSON.put("CONFIG_ID", 301);
        appJSON.put("UPLOAD_TYPE", -1L);
        appJSON.put("UPLOADID", -1L);
        appJSON.put("CONFIG_TYPE", 3);
        appJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
        appJSON.put("CURRENT_CONFIG", (Object)"APP_POLICY");
        appJSON.put("TABLE_NAME", (Object)"MdPackage");
        appJSON.put("PACKAGE_ID", -1L);
        appJSON.put("PROFILE_ID", -1L);
        appJSON.put("PROFILE_DESCRIPTION", (Object)"App Description");
        appJSON.put("CUSTOMER_ID", -1L);
        appJSON.put("PROFILE_TYPE", 2);
        appJSON.put("CREATED_BY", -1L);
        appJSON.put("PLATFORM_TYPE", -1);
        appJSON.put("APP_CATEGORY_ID", -1L);
        appJSON.put("SECURITY_TYPE", -1);
        appJSON.put("COLLECTION_TYPE", 3);
        appJSON.put("COUNTRY_CODE", (Object)"US");
        appJSON.put("APP_CATEGORY_NAME", (Object)"");
        appJSON.put("APP_TITLE", (Object)"");
        appJSON.put("BUNDLE_IDENTIFIER", (Object)"");
        appJSON.put("IS_APP_CONFIG_DELETED", (Object)Boolean.FALSE);
        appJSON.put("APP_CONFIG_TEMPLATE_ID", -1L);
        appJSON.put("APP_CONFIG_ID", -1L);
        appJSON.put("licenseType", 1);
        appJSON.put("IS_MIGRATED", (Object)Boolean.FALSE);
        appJSON.put("IS_MODERN_APP", (Object)Boolean.TRUE);
        appJSON.put("ATTACH_ID", -1L);
        appJSON.put("isDisplayImageDelete", (Object)Boolean.FALSE);
        final JSONObject packagePolicyForm = new JSONObject();
        packagePolicyForm.put("TABLE_NAME", (Object)"MdPackagePolicy");
        packagePolicyForm.put("PACKAGE_ID", -1L);
        packagePolicyForm.put("REMOVE_APP_WITH_PROFILE", (Object)Boolean.TRUE);
        packagePolicyForm.put("PREVENT_BACKUP", (Object)Boolean.TRUE);
        appJSON.put("PackagePolicyForm", (Object)packagePolicyForm);
        final JSONObject packageToAppDataForm = new JSONObject();
        packageToAppDataForm.put("TABLE_NAME", (Object)"MdPackageToAppData");
        packageToAppDataForm.put("PACKAGE_ID", -1L);
        packageToAppDataForm.put("FILE_UPLOAD_SIZE", -1L);
        packageToAppDataForm.put("COMMAND_LINE", (Object)"");
        appJSON.put("MdPackageToAppDataFrom", (Object)packageToAppDataForm);
        final JSONObject packageToAppGroupForm = new JSONObject();
        packageToAppGroupForm.put("TABLE_NAME", (Object)"MdPackageToAppGroup");
        packageToAppGroupForm.put("IS_PAID_APP", (Object)Boolean.FALSE);
        packageToAppGroupForm.put("IS_PURCHASED_FROM_PORTAL", (Object)Boolean.FALSE);
        packageToAppGroupForm.put("PRIVATE_APP_TYPE", 0);
        appJSON.put("MDPackageToAppGroupForm", (Object)packageToAppGroupForm);
        final JSONObject appPolicy = new JSONObject();
        appPolicy.put("TABLE_NAME", (Object)"InstallAppPolicy");
        appPolicy.put("PACKAGE_ID", -1L);
        appPolicy.put("CONFIG_DATA_IDENTIFIER", (Object)"apppolicy");
        appPolicy.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
        appJSON.put("APP_POLICY", (Object)appPolicy);
        return appJSON;
    }
    
    @Override
    public void deleteSpecificAppVersion(final JSONObject requestJSON) throws DataAccessException {
        this.validateIfAppAndReleaseLabelFound();
        final HashMap hashMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        final Long collectionId = hashMap.get("COLLECTION_ID");
        DataAccess.delete("AppGroupToCollection", new Criteria(new Column("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionId, 0));
    }
    
    @Override
    public void approveAppVersion(final JSONObject requestJSON) throws Exception {
        this.validateIfAppAndReleaseLabelFound();
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        final String appName = String.valueOf(appDetailsMap.get("PROFILE_NAME"));
        final String appVersion = String.valueOf(appDetailsMap.get("APP_VERSION"));
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final String userName = APIUtil.getUserName(requestJSON);
        final JSONObject approveAppJSON = new JSONObject();
        approveAppJSON.put("PACKAGE_ID", (Object)this.packageId);
        approveAppJSON.put("RELEASE_LABEL_ID", (Object)this.releaseLabelId);
        approveAppJSON.put("APP_GROUP_ID", appDetailsMap.get("APP_GROUP_ID"));
        approveAppJSON.put("APPROVED_APP_ID", appDetailsMap.get("APP_ID"));
        approveAppJSON.put("COLLECTION_ID", appDetailsMap.get("COLLECTION_ID"));
        AppVersionHandler.getInstance(appDetailsMap.get("PLATFORM_TYPE")).handleDBChangesForAppApproval(approveAppJSON);
        final JSONObject messageBody = requestJSON.getJSONObject("msg_body");
        final String versionLabel = messageBody.optString("version_label", I18N.getMsg(String.valueOf(appDetailsMap.get("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]));
        try {
            final Boolean distributeUpdate = messageBody.optBoolean("distribute_update", (boolean)Boolean.FALSE);
            if (distributeUpdate) {
                final Properties properties = new Properties();
                final Boolean isSilentInstall = messageBody.optBoolean("silent_install");
                final Boolean isNotify = messageBody.optBoolean("notify_user_via_email");
                ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
                ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
                ((Hashtable<String, Boolean>)properties).put("isNotify", isNotify);
                ((Hashtable<String, Long>)properties).put("RELEASE_LABEL_ID", this.releaseLabelId);
                final Long appGroupId = appDetailsMap.get("APP_GROUP_ID");
                final HashMap resourceMap = MDMAppMgmtHandler.getInstance().performUpdateAllForAppGroup(appGroupId, this.customerID, this.userID, properties);
                final List grpList = resourceMap.get("groupList");
                final List deviceList = resourceMap.get("resourceList");
                final int eventLogConstants = 2039;
                final String eventLogRemarks = "mdm.appmgmt.version.approved.and.distributed";
                final String eventLogRemarksArgs = appName + "@@@" + appVersion + "@@@" + versionLabel + "@@@" + grpList.size() + "@@@" + deviceList.size();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstants, null, userName, eventLogRemarks, eventLogRemarksArgs, customerId);
            }
            else {
                final int eventLogConstants = 2038;
                final String eventLogRemarks = "mdm.appmgmt.version.approved";
                final String eventLogRemarksArgs = appName + "@@@" + appVersion + "@@@" + versionLabel;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstants, null, userName, eventLogRemarks, eventLogRemarksArgs, customerId);
            }
        }
        catch (final Exception ex) {
            BaseAppDatahandler.logger.log(Level.SEVERE, "Exception while distributing app to resources after app approval", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final Boolean isRetireOlderVersions = messageBody.optBoolean("retire_old_version", (boolean)Boolean.TRUE);
        if (!isRetireOlderVersions) {
            final List channelsWithLowerVersionApp = AppsUtil.getInstance().getListOfReleaseLabelsWithAppVersionLowerThanGivenApp(this.packageId, this.releaseLabelId, Boolean.FALSE);
            BaseAppDatahandler.logger.log(Level.INFO, "Deleting app versions in following labels as higher version approved {0}", new Object[] { channelsWithLowerVersionApp });
            final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), appDetailsMap.get("APP_GROUP_ID"), 0);
            final Criteria releaseLabelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)channelsWithLowerVersionApp.toArray(), 8);
            DataAccess.delete("AppGroupToCollection", appGroupCriteria.and(releaseLabelCriteria));
            final int eventLogConstants = 72512;
            final String eventLogRemarks = "mdm.appmgmt.app.older.versions.deleted";
            final String eventLogRemarksArgs = appName + "@@@" + appVersion + "@@@" + versionLabel;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstants, null, userName, eventLogRemarks, eventLogRemarksArgs, customerId);
        }
        AppVersionDBUtil.getInstance().updateChannel(this.releaseLabelId, versionLabel);
    }
    
    @Override
    public void updateStoreSyncKey() throws Exception {
        CustomerParamsHandler.getInstance().addOrUpdateParameter(this.storeSyncKey, "true", (long)this.customerID);
    }
    
    public void validateandModifyAppType(final JSONObject message, final HashMap appdetails) {
        if (message.optBoolean("convertPlayStoreToEnterprise")) {
            appdetails.put("PACKAGE_TYPE", 2);
            if (message.has("version_label")) {
                message.remove("version_label");
            }
        }
    }
    
    @Override
    public JSONArray approveStoreApps(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    public JSONObject getModifiedAppProps(final boolean isEnterpriseApp, final String identifier, final String appName) {
        return null;
    }
    
    public Object getAppsFailureDetails(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    static {
        BaseAppDatahandler.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
