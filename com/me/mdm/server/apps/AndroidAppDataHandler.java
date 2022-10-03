package com.me.mdm.server.apps;

import java.util.Iterator;
import java.util.Set;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import java.util.Collection;
import java.util.HashSet;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.EnterpriseAppExtractor;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.android.afw.AFWAccountRegistrationHandler;
import java.net.URLDecoder;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import org.json.JSONArray;
import com.me.mdm.server.apps.config.AndroidAppConfigDataHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.api.APIActionsHandler;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.io.File;
import org.apache.tika.Tika;
import com.me.mdm.files.FileFacade;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class AndroidAppDataHandler extends BaseAppDatahandler
{
    public static final String AFW_STORE_SYNC_PARAM = "afwFirstSyncPending";
    
    public AndroidAppDataHandler(final JSONObject params) {
        super(params);
        this.platformType = 2;
        this.defaultCategory = "Business";
        this.storeSyncKey = "afwFirstSyncPending";
    }
    
    public JSONObject modifyEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJson) throws Exception {
        if (this.appProps.has("app_config_form") && this.appProps.getJSONObject("app_config_form").has("restrictions") && this.appProps.getJSONObject("app_config_form").getJSONArray("restrictions").length() > 0) {
            jsonObject.put("APP_CONFIG_TEMPLATE", (Object)JSONUtil.toJSON("APP_CONFIG_FORM", JSONUtil.toJSON("restrictions", this.appProps.getJSONObject("app_config_form").getJSONArray("restrictions"))));
        }
        if (requestJson.has("app_configuration_data")) {
            jsonObject.put("APP_CONFIGURATION", (Object)requestJson.getJSONObject("app_configuration_data"));
        }
        if (this.appProps.has("permissions") && this.appProps.getJSONObject("permissions").has("dangerous") && this.appProps.getJSONObject("permissions").getJSONArray("dangerous").length() > 0) {
            jsonObject.put("PermissionSchema", (Object)this.appProps.getJSONObject("permissions").getJSONArray("dangerous"));
        }
        if (requestJson.has("app_permission_data")) {
            jsonObject.put("permissionConfiguration", (Object)requestJson.getJSONObject("app_permission_data"));
        }
        if (requestJson.optBoolean("displayimagedelete", false)) {
            jsonObject.put("isDisplayImageDelete", true);
        }
        if (this.appProps.has("keytool_sign") && this.appProps.getJSONObject("keytool_sign").length() > 0) {
            final JSONObject signObject = (JSONObject)this.appProps.get("keytool_sign");
            final String algorithm = signObject.optString("algorithm");
            final String sha128FingerPrint = signObject.optString("SHA1");
            final String md5FingerPrint = signObject.optString("MD5");
            final String sha256FingerPrint = signObject.optString("SHA256");
            jsonObject.put("appSignatureInfo", (Object)new JSONObject().put("FINGERPRINT_MD5", (Object)md5FingerPrint).put("FINGERPRINT_SHA128", (Object)sha128FingerPrint).put("FINGERPRINT_SHA256", (Object)sha256FingerPrint).put("SIGNATURE_ALGORITHM_NAME", (Object)algorithm));
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject updateStoreApp(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        this.validateIfStoreAppAllowedForUpdate(msgBody);
        this.updateStoreAppCheck(jsonObject);
        msgBody.put("is_paid_app", msgBody.optBoolean("is_paid_app", (boolean)this.appdetails.get("IS_PAID_APP")));
        msgBody.put("update", true);
        final JSONObject jsonObject2 = msgBody;
        final String s = "supported_devices";
        final JSONObject jsonObject3 = msgBody;
        final String s2 = "supported_devices";
        AppsUtil.getInstance();
        jsonObject2.put(s, jsonObject3.optInt(s2, AppsUtil.getAPISupportedDevicesValues(this.appdetails.get("SUPPORTED_DEVICES"), 2)));
        msgBody.put("app_category_id", msgBody.optLong("app_category_id", (long)this.appdetails.get("APP_CATEGORY_ID")));
        if (msgBody.optBoolean("displayimagedelete", false)) {
            msgBody.put("isDisplayImageDelete", true);
        }
        return this.addStoreApp(msgBody);
    }
    
    private void validateIfStoreAppAllowedForUpdate(final JSONObject msgBody) throws Exception {
        if (msgBody.getInt("requested_app_type") != 2) {
            final Long packageId = APIUtil.getResourceID(msgBody, "app_id");
            final Boolean isPortal = (Boolean)DBUtil.getValueFromDB("MdPackageToAppGroup", "PACKAGE_ID", (Object)packageId, "IS_PURCHASED_FROM_PORTAL");
            if (isPortal) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.storeapp", new Object[0]) });
            }
        }
        else {
            final String bundleIdentifier = msgBody.getString("bundle_identifier");
            final String appName = msgBody.getString("app_name");
            final String oldAppName = AppsUtil.getInstance().getAppProfileName(bundleIdentifier, this.customerID, 2);
            if (!MDMStringUtils.isEmpty(oldAppName)) {
                throw new APIHTTPException("APP0004", new Object[] { appName, oldAppName });
            }
        }
    }
    
    @Override
    public JSONObject addStoreApp(final JSONObject requestJSON) throws Exception {
        final JSONObject appJSON = this.getDefaultAppForm();
        appJSON.put("APP_NAME", (Object)String.valueOf(requestJSON.get("app_name")));
        appJSON.put("PROFILE_NAME", (Object)String.valueOf(requestJSON.get("app_name")));
        appJSON.put("BUNDLE_IDENTIFIER", (Object)String.valueOf(requestJSON.get("bundle_identifier")));
        AppsUtil.getInstance().fillMDPackageToAppGroupForm(appJSON, requestJSON.getBoolean("is_paid_app"), false);
        Long categoryId = AppsUtil.getInstance().getAppCategoryId(requestJSON.optLong("app_category_id", -1L), this.platformType);
        if (categoryId == null || categoryId == -1L) {
            categoryId = AppsUtil.getInstance().getPlatformDefaultCategory(this.platformType, this.defaultCategory);
        }
        appJSON.put("APP_CATEGORY_ID", (Object)categoryId);
        if (!requestJSON.has("supported_devices")) {
            throw new APIHTTPException("COM0009", new Object[] { "supported_devices" });
        }
        AppsUtil.getInstance();
        final Integer supportedDevices = AppsUtil.getSupportedDevicesValues(requestJSON.getInt("supported_devices"), this.platformType);
        if (supportedDevices == null || supportedDevices == -1) {
            throw new APIHTTPException("COM0005", new Object[] { "supported_devices" });
        }
        requestJSON.put("supported_devices", (Object)supportedDevices);
        AppsUtil.getInstance().fillMdPackageToAppDataFrom(appJSON, requestJSON.getInt("supported_devices"), null, null, null, null, null, null);
        appJSON.put("PLATFORM_TYPE", this.platformType);
        appJSON.put("COLLECTION_ID", 0L);
        appJSON.put("CONFIG_DATA_ITEM_ID", 0L);
        appJSON.put("PACKAGE_TYPE", requestJSON.getInt("app_type"));
        final String bundleID = (String)requestJSON.get("bundle_identifier");
        final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(2).isCurrentPackageNewToAppRepo(bundleID, this.customerID);
        if (isCurrentPackageNew) {
            appJSON.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
        }
        String tempFilePath = null;
        final FileFacade fileFacade = new FileFacade();
        if (requestJSON.has("display_image")) {
            final Tika tika = new Tika();
            tempFilePath = String.valueOf(requestJSON.get("display_image"));
            final File file = new File(tempFilePath);
            final String contentType = tika.detect(file);
            if (!APIUtil.isAllowedImageMimeType(contentType)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.image", new Object[0]) });
            }
            final File dispFile = new File(tempFilePath);
            final String imgSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(CustomerInfoUtil.getInstance().getCustomerId());
            final HashMap imgFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, imgSourceFolderDestPath, imgSourceFolderDestPath, true, false);
            final String img = imgFileSourceMap.get("destDCFileName");
            appJSON.put("DISPLAY_IMAGE", (Object)img);
        }
        if (requestJSON.optBoolean("displayimagedelete", false)) {
            appJSON.put("isDisplayImageDelete", true);
        }
        appJSON.put("user_id", requestJSON.get("userID"));
        if (requestJSON.has("default_app_configuration")) {
            appJSON.put("default_app_configuration", requestJSON.get("default_app_configuration"));
        }
        appJSON.put("CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId());
        MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(appJSON);
        if (tempFilePath != null) {
            fileFacade.deleteFile(tempFilePath);
        }
        final List profileIds = new ArrayList();
        profileIds.add(appJSON.getLong("PROFILE_ID"));
        APIActionsHandler.getInstance().invokeAPIActionProfileListener(profileIds, profileIds, null, 4);
        final JSONObject idJSON = new JSONObject();
        idJSON.put("app_id", appJSON.getJSONObject("APP_POLICY").getLong("PACKAGE_ID"));
        return idJSON;
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject jsonObject) throws Exception {
        this.packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
        this.setReleaseLabelId(jsonObject);
        this.validateIfAppAndReleaseLabelFound();
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        this.validateIfAppInTrash();
        final JSONArray appConfigurationArr = jsonObject.getJSONObject("msg_body").getJSONArray("app_configuration");
        if (appConfigurationArr.length() > 0) {
            final Long appGroupId = this.appdetails.get("APP_GROUP_ID");
            final Long appConfTempId = this.appdetails.get("APP_CONFIG_TEMPLATE_ID");
            final Long appConfigId = this.appdetails.get("APP_CONFIG_ID");
            final Long configDataItem = this.appdetails.get("CONFIG_DATA_ITEM_ID");
            AndroidAppDataHandler.logger.log(Level.INFO, "appGroupId {0}, appConfTempId {1}", new Object[] { appGroupId, appConfTempId });
            if (appGroupId != null) {
                final JSONObject restrictionSchema = new JSONObject();
                final JSONObject managedAppConfiguration = new JSONObject();
                managedAppConfiguration.put("APP_CONFIG_ID", (Object)appConfigId);
                managedAppConfiguration.put("APP_CONFIG_TEMPLATE_ID", (Object)appConfTempId);
                Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                userID = ((userID == null) ? (userID = APIUtil.getUserID(jsonObject)) : userID);
                managedAppConfiguration.put("APP_CONFIG_NAME", (Object)"Managed App Configuration");
                managedAppConfiguration.put("LAST_MODIFIED_BY", (Object)userID);
                final JSONObject managedAppConfigurationData = new JSONObject();
                managedAppConfigurationData.put("CUSTOMER_ID", (Object)this.customerID);
                managedAppConfigurationData.put("APP_CONFIG", (Object)appConfigurationArr);
                managedAppConfiguration.put("ManagedAppConfigurationData", (Object)managedAppConfigurationData);
                restrictionSchema.put("ManagedAppConfiguration", (Object)managedAppConfiguration);
                restrictionSchema.put("PUBLISH_PROFILE", (Object)Boolean.TRUE);
                restrictionSchema.put("CUSTOMER_ID", (Object)this.customerID);
                restrictionSchema.put("LAST_MODIFIED_BY", (Object)userID);
                new AndroidAppConfigDataHandler().addOrUpdateAppConfigPolicyAndInvokeCommand(configDataItem, restrictionSchema);
                return this.getAppConfiguration(jsonObject);
            }
        }
        return new JSONObject();
    }
    
    @Override
    public JSONObject updateAppConfiguration(final JSONObject jsonObject) throws Exception {
        return this.addAppConfiguration(jsonObject);
    }
    
    @Override
    public JSONObject deleteAppConfiguration(final JSONObject jsonObject) throws Exception {
        this.packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
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
        return null;
    }
    
    @Override
    protected JSONObject getAppConfigTemplate(final Long configDataItemID, final Long appID) throws Exception {
        return new JSONObject(new AppConfigDataHandler().getAppConfigTemplate(appID));
    }
    
    @Override
    public JSONObject generateSignUpURL(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        String callbackUrl = jsonObject.getJSONObject("msg_header").getJSONObject("filters").optString("callbackurl", (String)null);
        if (callbackUrl == null) {
            throw new APIHTTPException("COM0009", new Object[] { "callbackurl" });
        }
        callbackUrl = URLDecoder.decode(callbackUrl, "UTF-8");
        try {
            final String signupUrl = new AFWAccountRegistrationHandler().getSignupUrl(this.customerID, callbackUrl);
            responseJSON.put("redirect_url", (Object)signupUrl);
        }
        catch (final Exception ex) {
            if (ex instanceof RuntimeException) {
                throw new APIHTTPException("COM0015", new Object[] { ex.getMessage() });
            }
            AndroidAppDataHandler.logger.log(Level.SEVERE, "Exception while generating signup URL", ex);
            throw new APIHTTPException("COM0004", new Object[] { ex });
        }
        return responseJSON;
    }
    
    @Override
    public JSONObject getAppPermission(final JSONObject message) throws Exception {
        final JSONObject jsonObject = super.getAppPermission(message);
        if (jsonObject.has("APP_PERMISSION_DATA") && jsonObject.getJSONObject("APP_PERMISSION_DATA").has("AppPermissionConfigDetails")) {
            final JSONArray jsonArray = jsonObject.getJSONObject("APP_PERMISSION_DATA").getJSONArray("AppPermissionConfigDetails");
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonArray.getJSONObject(i).remove("PermissionConfigAppList");
                jsonArray.getJSONObject(i).remove("CONFIG_CHOICE");
                jsonArray.getJSONObject(i).remove("APP_PERMISSION_GROUP_ID");
            }
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject getAppPermissionList(final JSONObject message) throws Exception {
        final JSONObject jsonObject = super.getAppPermissionList(message);
        if (jsonObject.has("APP_PERMISSION")) {
            final JSONArray jsonArray = jsonObject.getJSONArray("APP_PERMISSION");
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonArray.getJSONObject(i).remove("APP_PERMISSION_GROUP_ID");
            }
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject getPrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final int appsNotPurchasedFromPortal = MDMAppMgmtHandler.getInstance().getAppsNotPurchasedFromPortal(2, this.customerID);
        final Boolean isAFWConfigured = GoogleForWorkSettings.isAFWSettingsConfigured(this.customerID);
        responseJSON.put("no_of_non_portal_apps", appsNotPurchasedFromPortal);
        responseJSON.put("is_afw_configure", (Object)isAFWConfigured);
        return responseJSON;
    }
    
    @Override
    public JSONObject getAppDetailsFromAppFile(final JSONObject jsonObject) throws Exception {
        final String filepath = (String)jsonObject.get("file_path");
        this.appProps = EnterpriseAppExtractor.getNewInstance(2).getAppDetails(filepath);
        if (this.appProps.has("extractError")) {
            String errorMsg = I18N.getMsg("mdm.api.error.app.reject.file", new Object[0]);
            if (this.appProps.has("errorMsg")) {
                final String errorReason = String.valueOf(this.appProps.get("errorMsg"));
                errorMsg = errorMsg.concat(" ").concat(errorReason);
            }
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EditableAppInfo")) {
                AndroidAppDataHandler.logger.log(Level.INFO, "AndroidAppDataHandler : getAppDetailsFromAppFile errorMsg :", errorMsg);
                throw new APIHTTPException("APP0001", new Object[] { errorMsg });
            }
            AndroidAppDataHandler.logger.log(Level.WARNING, "Providing editable app info. AndroidAppDataHandler : getAppDetailsFromAppFile errorMsg :", errorMsg);
            this.appProps.put("allowEditableFields", true);
            this.appProps.put("errorMsg", (Object)errorMsg);
        }
        return this.appProps;
    }
    
    public int getPortalApprovedAppsCount(final Long customerId, final int platformType) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join appToGroupJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria protalPurchasedCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        sQuery.addJoin(appToGroupJoin);
        sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID").count());
        sQuery.setCriteria(customerCriteria.and(protalPurchasedCriteria).and(platformCriteria));
        final int portalAppsCount = DBUtil.getRecordCount(sQuery, "MdAppGroupDetails", "APP_GROUP_ID");
        return portalAppsCount;
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject appJSON = super.getAppDetails(jsonObject);
        if (appJSON.optBoolean("is_purchased_from_portal")) {
            AppsUtil.getInstance().getAvailableVersions(appJSON, Long.valueOf(this.appdetails.get("APP_GROUP_ID").toString()), this.releaseLabelId);
        }
        return appJSON;
    }
    
    @Override
    public JSONArray approveStoreApps(final JSONObject jsonObject) throws Exception {
        final Long customerID = jsonObject.getLong("customerID");
        if (!GoogleForWorkSettings.isAFWSettingsConfigured(customerID)) {
            throw new APIHTTPException("COM0015", new Object[] { "AFW Not Configured" });
        }
        List<String> appsToBeApproved = JSONUtil.getInstance().convertStringJSONArrayTOList(jsonObject.getJSONArray("appsList"));
        final Set<String> appsToBeApprovedHash = new HashSet<String>(appsToBeApproved);
        appsToBeApproved = new ArrayList<String>(appsToBeApprovedHash);
        final JSONArray approvedAppsSummary = new JSONArray();
        appsToBeApproved = this.checkIfAlreadyApprovedApps(appsToBeApproved, approvedAppsSummary);
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW));
        return ebs.approveAFWApps(appsToBeApproved, approvedAppsSummary);
    }
    
    public List<String> checkIfAlreadyApprovedApps(final List<String> appsToBeApproved, final JSONArray approvedAppsSummary) throws Exception {
        final List<String> portalApprovedApps = MDMAppMgmtHandler.getInstance().getPortalApprovedAppsIdentifier(this.customerID, 2);
        final List<String> appsAlreadyApproved = new ArrayList<String>(appsToBeApproved);
        appsAlreadyApproved.retainAll(portalApprovedApps);
        for (final String appAlreadyApproved : appsAlreadyApproved) {
            final JSONObject appApprovalStatus = new JSONObject();
            appApprovalStatus.put("IDENTIFIER", (Object)appAlreadyApproved);
            appApprovalStatus.put("ApprovalStatus", (Object)Boolean.TRUE);
            approvedAppsSummary.put((Object)appApprovalStatus);
        }
        final List<String> yetToApproveApps = new ArrayList<String>(appsToBeApproved);
        yetToApproveApps.removeAll(portalApprovedApps);
        return yetToApproveApps;
    }
}
