package com.me.mdm.server.apps;

import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.Iterator;
import java.util.Date;
import com.dd.plist.NSNumber;
import com.dd.plist.NSArray;
import com.dd.plist.NSDate;
import com.adventnet.ds.query.Join;
import com.dd.plist.NSString;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.apps.ios.IosIPAExtractor;
import java.util.Collection;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.apps.AppleAppStoreSearchHandler;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.api.APIActionsHandler;
import java.util.ArrayList;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.APIUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.apache.tika.Tika;
import com.me.mdm.files.FileFacade;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import java.util.Arrays;
import com.me.mdm.server.apps.ios.ContentMetaDataAPIHandler;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public class IOSAppDatahandler extends BaseAppDatahandler
{
    public IOSAppDatahandler(final JSONObject params) {
        super(params);
        this.platformType = 1;
        this.defaultCategory = "Business";
    }
    
    @Override
    protected JSONObject modifyEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJson) throws Exception {
        final Boolean removeAppWithProfile = requestJson.optBoolean("remove_app_with_profile");
        final Boolean preventBackup = requestJson.optBoolean("prevent_backup");
        final JSONObject packageJSON = new JSONObject();
        final String appName = jsonObject.optString("APP_EXTRACT_NAME");
        if (appName != null) {
            jsonObject.put("APP_NAME", (Object)appName);
        }
        if (removeAppWithProfile != null) {
            packageJSON.put("REMOVE_APP_WITH_PROFILE", (Object)removeAppWithProfile);
        }
        if (preventBackup != null) {
            packageJSON.put("PREVENT_BACKUP", (Object)preventBackup);
        }
        jsonObject.put("PackagePolicyForm", (Object)packageJSON);
        try {
            if (this.appProps.has("IconName")) {
                jsonObject.put("DISPLAY_IMAGE_IN_IPA", (Object)String.valueOf(this.appProps.get("smallAppIcon")));
                jsonObject.put("FULL_IMAGE_IN_IPA", (Object)String.valueOf(this.appProps.get("largeAppIcon")));
            }
            else if (jsonObject.getBoolean("ipaimagesavailable")) {
                if (requestJson.has("display_image")) {
                    jsonObject.remove("DISPLAY_IMAGE_LOC");
                }
                if (requestJson.has("full_image")) {
                    jsonObject.remove("FULL_IMAGE_LOC");
                }
            }
            else {
                jsonObject.put("DISPLAY_IMAGE", (Object)String.valueOf(requestJson.get("display_image")));
                jsonObject.put("FULL_IMAGE", (Object)String.valueOf(requestJson.get("full_image")));
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[] { "display image" });
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject updateStoreApp(final JSONObject jsonObject) throws Exception {
        this.updateStoreAppCheck(jsonObject);
        this.setReleaseLabelId(jsonObject);
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        this.preFillData(true, msgBody);
        return this.addStoreApp(msgBody);
    }
    
    @Override
    public JSONObject addStoreApp(final JSONObject requestJSON) throws Exception {
        final JSONObject appJSON = this.getDefaultAppForm();
        final JSONObject appAssignableDetailsForm = new JSONObject();
        appAssignableDetailsForm.put("TABLE_NAME", (Object)"MDAppAssignableDetails");
        appAssignableDetailsForm.put("IS_DEVICE_ASSIGNABLE", (Object)Boolean.TRUE);
        appJSON.put("MdAppAssignableDetailsForm", (Object)appAssignableDetailsForm);
        final String appName = requestJSON.optString("app_name", (String)null);
        final String countryCode = requestJSON.optString("country_code", (String)null);
        final String bundleIdentifierFromRequest = requestJSON.optString("bundle_identifier", (String)null);
        boolean isMigrated = false;
        boolean vppFlag = false;
        final boolean update = requestJSON.optBoolean("update", false);
        Boolean isPaidApp = false;
        String trackViewUrl = null;
        String trackIdFromRequest = null;
        String minimumOsVersion = null;
        String appCategory = null;
        String trackName = null;
        String version = null;
        String appIcon = null;
        String description = null;
        int appSupportedDevices = 0;
        ContentMetaDataAppDetails appDetailsObject = null;
        String bundleId = null;
        Integer trackId = null;
        Long externalId = null;
        final ContentMetaDataAPIHandler contentMetaDataAPIHandler = new ContentMetaDataAPIHandler();
        trackIdFromRequest = requestJSON.optString("track_id");
        if (trackIdFromRequest == null) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final List adamId = Arrays.asList(trackIdFromRequest);
        final HashMap AppResponseHashMap = contentMetaDataAPIHandler.getNonVPPAppDetails(adamId, countryCode);
        appDetailsObject = AppResponseHashMap.get(Integer.parseInt(trackIdFromRequest));
        if (appDetailsObject == null) {
            throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.app.not.found", new Object[0]) });
        }
        isPaidApp = appDetailsObject.getIsPaidApp();
        trackViewUrl = appDetailsObject.getAppStoreURL();
        minimumOsVersion = appDetailsObject.getMinimumOSVersion();
        appCategory = appDetailsObject.getPrimaryGenreName();
        trackName = appDetailsObject.getAppName();
        version = appDetailsObject.getAppVersion();
        appIcon = appDetailsObject.getAppIconImageURL();
        appSupportedDevices = appDetailsObject.getSupportDevice();
        description = appDetailsObject.getAppDescription();
        bundleId = appDetailsObject.getBundleId();
        trackId = Integer.valueOf(appDetailsObject.getAdamId());
        externalId = appDetailsObject.getExternalAppVersionID();
        JSONObject tempJSON = new JSONObject();
        tempJSON = new IOSStoreHandler(null, this.customerID).getAllStoreDetails();
        final JSONArray resultArray = tempJSON.getJSONArray("VPP_TOKEN_DETAILS");
        JSONObject resultJSON = new JSONObject();
        resultJSON = resultArray.optJSONObject(0);
        if (isPaidApp) {
            if (!requestJSON.has("vpp_file_source") && !resultJSON.has("BUSINESSSTORE_ID") && !update) {
                final boolean allowPaidAppsWithoutRedeptionOrVPP = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotValidateIOSPaidApp");
                if (!allowPaidAppsWithoutRedeptionOrVPP) {
                    throw new APIHTTPException("COM0009", new Object[] { "License source for Paid app needed" });
                }
            }
            else {
                if (requestJSON.has("is_migrated") && update && !resultJSON.has("BUSINESSSTORE_ID")) {
                    throw new APIHTTPException("COM0009", new Object[] { "Migration cannot be done due to absence of ABM configuration" });
                }
                if (requestJSON.optBoolean("is_migrated") && update && requestJSON.optInt("license_type") == 1) {
                    isMigrated = true;
                }
                else if (requestJSON.has("vpp_file_source") || (update && requestJSON.has("license_type") && requestJSON.getInt("license_type") == 1)) {
                    vppFlag = false;
                }
            }
        }
        appJSON.put("DISPLAY_IMAGE_LOC", (Object)appIcon);
        if (!MDMStringUtils.isEmpty(description) && description.length() > 4800) {
            description = description.substring(0, 4795).concat("...");
        }
        if (!vppFlag && requestJSON.has("is_purchased_from_portal") && requestJSON.getBoolean("is_purchased_from_portal") && update) {
            vppFlag = true;
        }
        String tempFilePath = null;
        final FileFacade fileFacade = new FileFacade();
        if (requestJSON.has("vpp_file_source")) {
            final Tika tika = new Tika();
            final String dispFilePath = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(requestJSON.get("vpp_file_source").toString()))).get("file_path"));
            tempFilePath = fileFacade.getTempLocation(dispFilePath);
            fileFacade.writeFile(tempFilePath, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePath));
            final File file = new File(tempFilePath);
            final String contentType = tika.detect(file);
            if (!APIUtil.isAllowedContentType(contentType)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.vpp.invalid", new Object[0]) });
            }
            final File dispFile = new File(tempFilePath);
            final String xlsSourceFolderDestPath = MDMAppMgmtHandler.getInstance().getAppRepositoryTempSourceFolderPath(CustomerInfoUtil.getInstance().getCustomerId());
            final HashMap xlsFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(dispFile, xlsSourceFolderDestPath, xlsSourceFolderDestPath, true, false);
            final String xls = xlsFileSourceMap.get("destDCFileName");
            requestJSON.put("vppFileSource", (Object)xls);
        }
        final int userDefinedSupportedDevices = requestJSON.getInt("supported_devices");
        appSupportedDevices |= userDefinedSupportedDevices;
        AppsUtil.getInstance().fillMdPackageToAppDataFrom(appJSON, appSupportedDevices, trackViewUrl, description, String.valueOf(trackId), null, null, minimumOsVersion);
        AppsUtil.getInstance().fillMDPackageToAppGroupForm(appJSON, isPaidApp, vppFlag);
        appJSON.put("BUNDLE_IDENTIFIER", (Object)bundleId);
        final String bundleIdentifier = bundleId;
        final String oldAppName = AppsUtil.getInstance().getAppProfileName(bundleIdentifier, this.customerID, 1);
        if (!update) {
            if (oldAppName != null && oldAppName.length() > 0) {
                throw new APIHTTPException("APP0004", new Object[] { appName, oldAppName });
            }
            if (AppsUtil.getInstance().isAppExistsInPackage(bundleIdentifier, this.platformType, this.customerID, true)) {
                throw new APIHTTPException("APP0006", new Object[] { APIUtil.getPortalString(this.platformType) });
            }
            if (AppsUtil.getInstance().isAppExistsInPackage(bundleIdentifier, this.platformType, this.customerID, false)) {
                throw new APIHTTPException("APP0005", new Object[0]);
            }
        }
        appJSON.put("APP_CATEGORY_NAME", (Object)appCategory);
        appJSON.put("APP_NAME", (Object)trackName);
        appJSON.put("PROFILE_NAME", (Object)trackName);
        appJSON.put("APP_VERSION", (Object)version);
        appJSON.put("COUNTRY_CODE", (Object)countryCode);
        AppsUtil.getInstance().fillMdAppAssignableDetailsForm(appJSON, 0);
        appJSON.put("PLATFORM_TYPE", this.platformType);
        appJSON.put("COLLECTION_ID", 0L);
        appJSON.put("CONFIG_DATA_ITEM_ID", 0L);
        appJSON.put("PACKAGE_TYPE", requestJSON.getInt("app_type"));
        appJSON.put("EXTERNAL_APP_VERSION_ID", (Object)externalId);
        appJSON.put("user_id", requestJSON.get("userID"));
        appJSON.put("CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId());
        final Boolean removeappWithProfile = requestJSON.optBoolean("remove_app_with_profile");
        final Boolean preventBackup = requestJSON.optBoolean("prevent_backup");
        final JSONObject packagePolicyJSON = new JSONObject();
        packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", (Object)removeappWithProfile);
        packagePolicyJSON.put("PREVENT_BACKUP", (Object)preventBackup);
        appJSON.put("PackagePolicyForm", (Object)packagePolicyJSON);
        if (vppFlag) {
            appJSON.put("licenseType", 2);
        }
        else {
            appJSON.put("licenseType", 1);
        }
        if (requestJSON.has("vppFileSource")) {
            appJSON.put("vppFileSource", requestJSON.get("vppFileSource"));
        }
        if (isMigrated) {
            appJSON.put("IS_MIGRATED", isMigrated);
        }
        final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo(bundleId, this.customerID);
        if (isCurrentPackageNew) {
            appJSON.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
        }
        MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(appJSON);
        final List profileIds = new ArrayList();
        profileIds.add(appJSON.getLong("PROFILE_ID"));
        APIActionsHandler.getInstance().invokeAPIActionProfileListener(profileIds, profileIds, null, 4);
        requestJSON.put("app_id", appJSON.getJSONObject("APP_POLICY").getLong("PACKAGE_ID"));
        return requestJSON;
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject requestJSON) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        this.validateIfAppFound(this.packageId = APIUtil.getResourceID(requestJSON, "app_id"), this.customerID);
        this.setReleaseLabelId(requestJSON);
        this.validateIfAppAndReleaseLabelFound();
        final HashMap appDetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        this.appdetails = appDetails;
        this.validateIfAppInTrash();
        if (appDetails.get("BUNDLE_IDENTIFIER").toString().contains("com.manageengine.mdm.iosagent")) {
            throw new APIHTTPException("COM0014", new Object[] { "Configuration cannot be added/updated for MDM app catalog" });
        }
        Long configDataItem = appDetails.get("CONFIG_DATA_ITEM_ID");
        if (configDataItem == null) {
            configDataItem = -1L;
        }
        AppConfigDataPolicyHandler.getInstance(1).populateAppConfigJSON(requestJSON.getJSONObject("msg_body"), jsonObject);
        jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
        jsonObject.put("PACKAGE_MODIFIED_BY", (Object)this.userID);
        jsonObject.put("PLATFORM_TYPE", this.platformType);
        jsonObject.put("APP_GROUP_ID", appDetails.get("APP_GROUP_ID"));
        jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItem);
        jsonObject.put("APP_CONFIG_TEMPLATE_ID", (Object)new AppConfigDataHandler().getAppConfigTemplateIDFromConfigDataItemID(configDataItem));
        jsonObject.put("APP_CONFIG_ID", appDetails.get("APP_CONFIG_ID"));
        jsonObject.put("PUBLISH_PROFILE", (Object)Boolean.TRUE);
        new AppConfigDataHandler().saveAppConfigData(jsonObject, Boolean.TRUE);
        return this.getAppConfiguration(requestJSON);
    }
    
    @Override
    public JSONObject updateAppConfiguration(final JSONObject jsonObject) throws Exception {
        return this.addAppConfiguration(jsonObject);
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject appJSON = super.getAppDetails(jsonObject);
        if (appJSON.getInt("app_type") == 2) {
            final Long expiryDate = this.getAppExpiryDate(JSONUtil.optLong(appJSON, "app_id", -1L));
            appJSON.put("expiryDate", (Object)expiryDate);
        }
        appJSON.put("prevent_backup", this.appdetails.get("PREVENT_BACKUP"));
        appJSON.put("remove_app_with_profile", this.appdetails.get("REMOVE_APP_WITH_PROFILE"));
        final Long appGroupID = (Long)appJSON.get("app_group_id");
        final boolean isVPPApp = appJSON.getBoolean("is_purchased_from_portal");
        if (isVPPApp) {
            appJSON.put("app_location_details", (Object)new AppLicenseMgmtHandler().getABMAppLicenseCountSummary(appGroupID, null));
        }
        return appJSON;
    }
    
    @Override
    public JSONObject getAppSuggestion(final JSONObject requestJSON) throws Exception {
        final String appName = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("appname", (String)null);
        final String countryCode = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("country", (String)null);
        final AppleAppStoreSearchHandler appSearch = new AppleAppStoreSearchHandler();
        final JSONArray jsonarray = appSearch.getAppSuggestData(appName, countryCode);
        if (jsonarray != null && jsonarray.length() > 0 && (jsonarray.getJSONObject(0).has("errorMsg") || jsonarray.getJSONObject(0).has("error"))) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final JSONObject resp = new JSONObject();
        resp.put("apps", (Object)jsonarray.toString());
        return resp;
    }
    
    @Override
    public JSONObject addEnterpriseApp(final JSONObject requestJson) throws Exception {
        return super.addEnterpriseApp(requestJson);
    }
    
    @Override
    public JSONObject updateEnterpriseApp(final JSONObject requestJson) throws Exception {
        this.setReleaseLabelId(requestJson);
        this.preFillData(false, requestJson);
        return super.updateEnterpriseApp(requestJson);
    }
    
    @Override
    public Object getCountryCode(final JSONObject requestJSON) throws Exception {
        return super.getCountryCode(requestJSON);
    }
    
    @Override
    protected boolean validateEnterpriseSupport(final Integer supportedDevices) throws Exception {
        return true;
    }
    
    @Override
    protected void validateEnterpriseAppData(final JSONObject requestJSON, final JSONObject packageJSON) throws Exception {
        if (packageJSON.has("error") && String.valueOf(packageJSON.get("error")).equalsIgnoreCase("dc.mdm.ipavalidation.IPA_EXPIRED")) {
            throw new APIHTTPException("COM0005", new Object[] { I18N.getMsg("dc.mdm.ipavalidation.IPA_EXPIRED", new Object[0]) });
        }
        if (packageJSON.has("error") && String.valueOf(packageJSON.get("error")).contains("dc.mdm.ipavalidation.INFO_PLIST_NOT_EXIST")) {
            throw new APIHTTPException("COM0005", new Object[] { I18N.getMsg("dc.mdm.ipavalidation.INFO_PLIST_NOT_EXIST", new Object[0]) });
        }
        if (packageJSON.has("error")) {
            throw new APIHTTPException("COM0005", new Object[] { "IPA file is expired or not available" });
        }
    }
    
    @Override
    public JSONObject generateSignUpURL(final JSONObject jsonObject) throws Exception {
        throw new APIHTTPException("COM0014", new Object[0]);
    }
    
    @Override
    public JSONObject deleteAppConfiguration(final JSONObject jsonObject) throws Exception {
        final Long packageId = APIUtil.getResourceID(jsonObject, "app_id");
        this.setReleaseLabelId(jsonObject);
        this.validateIfAppFound(packageId, this.customerID);
        final HashMap appDetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, this.releaseLabelId);
        if (appDetails.get("BUNDLE_IDENTIFIER").toString().contains("com.manageengine.mdm.iosagent")) {
            throw new APIHTTPException("COM0014", new Object[] { "Configuration cannot be removed for MDM app catalog" });
        }
        return super.deleteAppConfiguration(jsonObject);
    }
    
    private void preFillData(final boolean storeApp, final JSONObject msgBody) throws Exception {
        Long releaseLabelIDForCloning;
        if (msgBody.has("force_update_in_label") && msgBody.getBoolean("force_update_in_label") == Boolean.FALSE) {
            releaseLabelIDForCloning = AppVersionDBUtil.getInstance().getApprovedReleaseLabelForGivePackage(this.packageId, this.customerID);
        }
        else {
            this.validateIfAppAndReleaseLabelFound();
            releaseLabelIDForCloning = this.releaseLabelId;
        }
        msgBody.put("update", true);
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, releaseLabelIDForCloning);
        final Long appGroupId = (Long)DBUtil.getValueFromDB("MdPackageToAppGroup", "PACKAGE_ID", (Object)this.packageId, "APP_GROUP_ID");
        final HashMap appLicenseDetails = new AppleAppLicenseMgmtHandler().getAppLicenseDetails(appGroupId, null);
        final String countryCode = AppsUtil.getInstance().getAppStoreRegionValue();
        if (storeApp) {
            msgBody.put("app_name", appDetailsMap.get("PROFILE_NAME"));
            msgBody.put("country_code", (Object)(MDMStringUtils.isEmpty(countryCode) ? "US" : countryCode));
            msgBody.put("prevent_backup", msgBody.optBoolean("prevent_backup", (boolean)appDetailsMap.get("PREVENT_BACKUP")));
            msgBody.put("remove_app_with_profile", msgBody.optBoolean("remove_app_with_profile", (boolean)appDetailsMap.get("REMOVE_APP_WITH_PROFILE")));
            AppsUtil.getInstance();
            final int supportedDevices = AppsUtil.getAPISupportedDevicesValues(appDetailsMap.get("SUPPORTED_DEVICES"), this.platformType);
            if (msgBody.has("supported_devices") && msgBody.getInt("supported_devices") != 16) {
                msgBody.put("supported_devices", msgBody.getInt("supported_devices"));
            }
            else {
                msgBody.put("supported_devices", supportedDevices);
            }
            msgBody.put("is_purchased_from_portal", msgBody.optBoolean("is_purchased_from_portal", (boolean)appDetailsMap.get("IS_PURCHASED_FROM_PORTAL")));
            if (appLicenseDetails != null) {
                msgBody.put("license_type", appLicenseDetails.get("appLicenseType"));
            }
            msgBody.put("isPaidApp", appDetailsMap.get("IS_PAID_APP"));
            msgBody.put("trackViewUrl", appDetailsMap.get("STORE_URL"));
            msgBody.put("track_id", appDetailsMap.get("STORE_ID"));
            msgBody.put("minimumOsVersion", appDetailsMap.get("APP_NAME"));
            msgBody.put("appCategory", appDetailsMap.get("APP_CATEGORY_NAME"));
            msgBody.put("trackName", appDetailsMap.get("APP_NAME"));
            msgBody.put("version", appDetailsMap.get("APP_VERSION"));
            msgBody.put("appIcon", appDetailsMap.get("DISPLAY_IMAGE_LOC"));
            msgBody.put("description", appDetailsMap.get("DESCRIPTION"));
        }
        else {
            msgBody.put("app_name", (Object)msgBody.optString("app_name", (String)appDetailsMap.get("PROFILE_NAME")));
            final String s = "supported_devices";
            final String s2 = "supported_devices";
            AppsUtil.getInstance();
            msgBody.put(s, msgBody.optInt(s2, AppsUtil.getAPISupportedDevicesValues(appDetailsMap.get("SUPPORTED_DEVICES"), this.platformType)));
            msgBody.put("prevent_backup", msgBody.optBoolean("prevent_backup", (boolean)appDetailsMap.get("PREVENT_BACKUP")));
            msgBody.put("remove_app_with_profile", msgBody.optBoolean("remove_app_with_profile", (boolean)appDetailsMap.get("REMOVE_APP_WITH_PROFILE")));
            msgBody.put("description", (Object)msgBody.optString("description", (String)appDetailsMap.get("DESCRIPTION")));
        }
    }
    
    public void saveMetaData(final JSONObject jsonObject) {
        try {
            IOSAppDatahandler.logger.log(Level.INFO, "Inside saveMetaData for appID: {0}", new Object[] { jsonObject.get("APP_ID") });
            final Object metaObj = jsonObject.opt("metaData");
            final JSONArray metaData = (JSONArray)((metaObj instanceof JSONArray) ? metaObj : ((metaObj instanceof ArrayList) ? new JSONArray((Collection)metaObj) : null));
            final String metaDataLoc = (String)jsonObject.opt("metaDataLoc");
            JSONObject fileDetailsJson = new JSONObject();
            fileDetailsJson.put("customerId", jsonObject.get("CUSTOMER_ID"));
            fileDetailsJson.put("packageId", jsonObject.get("PACKAGE_ID"));
            fileDetailsJson.put("appId", jsonObject.get("APP_ID"));
            if (metaData != null) {
                for (int i = 0; i < metaData.length(); ++i) {
                    final JSONObject json = (JSONObject)metaData.get(i);
                    if (json.opt("FILE_LOCATION") != null) {
                        final String ppName = (String)json.get("PP");
                        fileDetailsJson.put("sourceFile", (Object)(metaDataLoc + File.separator + ppName));
                        fileDetailsJson = this.copyProvFileToServerPath(fileDetailsJson);
                        final NSDictionary dict = new IosIPAExtractor().decryptProvProfile(metaDataLoc + File.separator + ppName);
                        final Long provProv_d = this.addOrUpdateProvDetails(dict, fileDetailsJson);
                        IOSAppDatahandler.logger.log(Level.INFO, "Updated provisioning ID: {0}", new Object[] { provProv_d });
                        final String infoPlist = (String)json.get("INFO_PLIST");
                        JSONObject ipaProp = new JSONObject();
                        final IosIPAExtractor ipaExtraxtor = new IosIPAExtractor();
                        final String infoplistPath = ipaExtraxtor.decryptInfoPlist(metaDataLoc + File.separator + infoPlist);
                        ipaProp = ipaExtraxtor.getPropertiesFromPlist(infoplistPath, ipaProp);
                        final Long appExtensionId = this.addOrUpdateAppExtensionDetails(ipaProp, fileDetailsJson);
                        IOSAppDatahandler.logger.log(Level.INFO, "Updated appExtensionId ID: {0}", new Object[] { appExtensionId });
                        this.addorUpdateAppToExtnRelDetails((Long)jsonObject.get("APP_ID"), appExtensionId);
                        this.addorUpdateAppExtenToProvDetails(provProv_d, appExtensionId);
                    }
                    else if (json.opt("MAIN_FILE_LOCATION") != null) {
                        IOSAppDatahandler.logger.log(Level.INFO, "Beginning to process Main file location details");
                        final String ppName = (String)json.get("PP");
                        fileDetailsJson.put("sourceFile", (Object)(metaDataLoc + File.separator + ppName));
                        fileDetailsJson = this.copyProvFileToServerPath(fileDetailsJson);
                        final NSDictionary dict = new IosIPAExtractor().decryptProvProfile(metaDataLoc + File.separator + ppName);
                        final Long provProv_d = this.addOrUpdateProvDetails(dict, fileDetailsJson);
                        IOSAppDatahandler.logger.log(Level.INFO, "Updated provisioning ID: {0}", new Object[] { provProv_d });
                        this.addOrUpdateAppToProvProfileRel(provProv_d, (Long)jsonObject.get("APP_ID"));
                        this.addorUpdateAppGroupToProvDetails(provProv_d, (Long)jsonObject.get("PACKAGE_ID"), (Long)jsonObject.get("APP_GROUP_ID"));
                        IOSAppDatahandler.logger.log(Level.INFO, "Processing Main file location details completed");
                    }
                }
            }
        }
        catch (final Exception ex) {
            IOSAppDatahandler.logger.log(Level.SEVERE, "Exception in saveMetaData", ex);
        }
    }
    
    public void addOrUpdateAppToProvProfileRel(final Long provID, final Long appID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToProvProfileRel"));
            sQuery.addSelectColumn(new Column("MdAppToProvProfileRel", "*"));
            final Criteria appIdCri = new Criteria(new Column("MdAppToProvProfileRel", "APP_ID"), (Object)appID, 0);
            sQuery.setCriteria(appIdCri);
            final DataObject appToProvDO = MDMUtil.getPersistence().get(sQuery);
            if (appToProvDO.isEmpty()) {
                final Row appToProvRow = new Row("MdAppToProvProfileRel");
                appToProvRow.set("APP_ID", (Object)appID);
                appToProvRow.set("PROV_ID", (Object)provID);
                appToProvDO.addRow(appToProvRow);
            }
            else {
                final Row appToProvRow = appToProvDO.getFirstRow("MdAppToProvProfileRel");
                appToProvRow.set("APP_ID", (Object)appID);
                appToProvRow.set("PROV_ID", (Object)provID);
                appToProvDO.updateRow(appToProvRow);
            }
            MDMUtil.getPersistence().update(appToProvDO);
            IOSAppDatahandler.logger.log(Level.INFO, "MDAPPTOPROVPROFILEREL updated: appID: {0}, provID: {1}", new Object[] { appID, provID });
        }
        catch (final Exception e) {
            IOSAppDatahandler.logger.log(Level.SEVERE, "Exception in addOrUpdateAppToProvProfileRel", e);
        }
    }
    
    public void addorUpdateAppToExtnRelDetails(final Long appId, final Long appExtnId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToExtnRel"));
        sQuery.addSelectColumn(new Column("MdAppToExtnRel", "*"));
        final Criteria appExtnIdCri = new Criteria(new Column("MdAppToExtnRel", "APP_EXTN_ID"), (Object)appExtnId, 0);
        final Criteria appIdCri = new Criteria(new Column("MdAppToExtnRel", "APP_ID"), (Object)appId, 0);
        sQuery.setCriteria(appExtnIdCri.and(appIdCri));
        final DataObject appToExtnRelDO = MDMUtil.getPersistence().get(sQuery);
        if (appToExtnRelDO.isEmpty()) {
            final Row appToExtnRelRow = new Row("MdAppToExtnRel");
            appToExtnRelRow.set("APP_EXTN_ID", (Object)appExtnId);
            appToExtnRelRow.set("APP_ID", (Object)appId);
            appToExtnRelDO.addRow(appToExtnRelRow);
            MDMUtil.getPersistence().update(appToExtnRelDO);
        }
    }
    
    public Long addOrUpdateAppExtensionDetails(final JSONObject ipaProp, final JSONObject fileDetailsJson) throws Exception {
        final String bundleId = (String)ipaProp.get("PackageName");
        final Long customerId = (Long)fileDetailsJson.get("customerId");
        final String appExtnName = (String)ipaProp.get("APP_NAME");
        IOSAppDatahandler.logger.log(Level.INFO, "Inside addOrUpdateAppExtensionDetails of app: {0} for customer: {1}", new Object[] { bundleId, customerId });
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppExtension"));
        sQuery.addSelectColumn(new Column("MdAppExtension", "*"));
        final Criteria appExtnIdCri = new Criteria(new Column("MdAppExtension", "IDENTIFIER"), (Object)bundleId, 0);
        final Criteria customerIdCri = new Criteria(new Column("MdAppExtension", "CUSTOMER_ID"), (Object)customerId, 0);
        sQuery.setCriteria(appExtnIdCri.and(customerIdCri));
        final DataObject appExtensioDO = MDMUtil.getPersistence().get(sQuery);
        if (appExtensioDO.isEmpty()) {
            final Row appExtensionRow = new Row("MdAppExtension");
            appExtensionRow.set("EXTN_NAME", (Object)appExtnName);
            appExtensionRow.set("IDENTIFIER", (Object)bundleId);
            appExtensionRow.set("CUSTOMER_ID", (Object)customerId);
            appExtensioDO.addRow(appExtensionRow);
        }
        else {
            final Row appExtensionRow = appExtensioDO.getRow("MdAppExtension");
            appExtensionRow.set("EXTN_NAME", (Object)appExtnName);
            appExtensioDO.updateRow(appExtensionRow);
        }
        MDMUtil.getPersistence().update(appExtensioDO);
        return (Long)appExtensioDO.getValue("MdAppExtension", "APP_EXTN_ID", (Criteria)null);
    }
    
    public void addorUpdateAppExtenToProvDetails(final Long provId, final Long appExtensionId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppExtnToProvProfileRel"));
        sQuery.addSelectColumn(new Column("MdAppExtnToProvProfileRel", "*"));
        final Criteria appExtnIdCri = new Criteria(new Column("MdAppExtnToProvProfileRel", "APP_EXTN_ID"), (Object)appExtensionId, 0);
        sQuery.setCriteria(appExtnIdCri);
        final DataObject appExtnToProvProfileDO = MDMUtil.getPersistence().get(sQuery);
        if (appExtnToProvProfileDO.isEmpty()) {
            final Row appExtnToProvProfileRow = new Row("MdAppExtnToProvProfileRel");
            appExtnToProvProfileRow.set("APP_EXTN_ID", (Object)appExtensionId);
            appExtnToProvProfileRow.set("PROV_ID", (Object)provId);
            appExtnToProvProfileDO.addRow(appExtnToProvProfileRow);
        }
        else {
            final Row appExtnToProvProfileRow = appExtnToProvProfileDO.getRow("MdAppExtnToProvProfileRel");
            appExtnToProvProfileRow.set("PROV_ID", (Object)provId);
            appExtnToProvProfileDO.updateRow(appExtnToProvProfileRow);
        }
        MDMUtil.getPersistence().update(appExtnToProvProfileDO);
    }
    
    public void addorUpdateAppGroupToProvDetails(final Long provId, final Long packageId, final Long appGroupId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToProvProfileRel"));
        sQuery.addSelectColumn(new Column("MdPackageToProvProfileRel", "*"));
        final Criteria appGroupCri = new Criteria(new Column("MdPackageToProvProfileRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria packageCri = new Criteria(new Column("MdPackageToProvProfileRel", "PACKAGE_ID"), (Object)packageId, 0);
        sQuery.setCriteria(appGroupCri.and(packageCri));
        final DataObject packageToProvProfileDO = MDMUtil.getPersistence().get(sQuery);
        if (packageToProvProfileDO.isEmpty()) {
            final Row packageToProvProfileRow = new Row("MdPackageToProvProfileRel");
            packageToProvProfileRow.set("APP_GROUP_ID", (Object)appGroupId);
            packageToProvProfileRow.set("PACKAGE_ID", (Object)packageId);
            packageToProvProfileRow.set("PROV_ID", (Object)provId);
            packageToProvProfileDO.addRow(packageToProvProfileRow);
        }
        else {
            final Row packageToProvProfileRow = packageToProvProfileDO.getRow("MdPackageToProvProfileRel");
            packageToProvProfileRow.set("PROV_ID", (Object)provId);
            packageToProvProfileDO.updateRow(packageToProvProfileRow);
        }
        MDMUtil.getPersistence().update(packageToProvProfileDO);
    }
    
    public Long addOrUpdateProvDetails(final NSDictionary dict, final JSONObject fileDetailsJson) throws Exception {
        final Long customerId = (Long)fileDetailsJson.get("customerId");
        final NSString udid = (NSString)dict.objectForKey("UUID");
        IOSAppDatahandler.logger.log(Level.INFO, "Beginning to add/update app provisioning details of profile with uuid: {0} for customer: {1}", new Object[] { udid, customerId });
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesDetails"));
        sQuery.addJoin(new Join("AppleProvProfilesDetails", "AppleProvProfilesExtn", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 2));
        sQuery.addJoin(new Join("AppleProvProfilesDetails", "AppleProvProfilesToUDID", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 1));
        sQuery.addSelectColumn(new Column("AppleProvProfilesDetails", "*"));
        sQuery.addSelectColumn(new Column("AppleProvProfilesExtn", "*"));
        sQuery.addSelectColumn(new Column("AppleProvProfilesToUDID", "*"));
        final Criteria provUdidCri = new Criteria(new Column("AppleProvProfilesDetails", "PROV_UUID"), (Object)udid.toString(), 0);
        sQuery.setCriteria(provUdidCri);
        final DataObject appDo = MDMUtil.getPersistence().get(sQuery);
        final NSString provName = (NSString)dict.objectForKey("AppIDName");
        final NSDictionary entitlements = (NSDictionary)dict.objectForKey("Entitlements");
        final NSString provAppId = (NSString)entitlements.objectForKey("application-identifier");
        final NSDate expirationDate = (NSDate)dict.objectForKey("ExpirationDate");
        final NSDate creationDate = (NSDate)dict.objectForKey("CreationDate");
        final NSArray teamIdentifierArray = (NSArray)dict.objectForKey("TeamIdentifier");
        final NSString teamIdentifier = (NSString)teamIdentifierArray.objectAtIndex(0);
        final Date expiryDt = expirationDate.getDate();
        final Long provProfileExpiryDateLong = expiryDt.getTime();
        final Date creationDt = creationDate.getDate();
        final Long provProfileCreationDateLong = creationDt.getTime();
        final NSArray udidList = (NSArray)dict.objectForKey("ProvisionedDevices");
        final NSNumber provAllDevices = (NSNumber)dict.objectForKey("ProvisionsAllDevices");
        final NSNumber taskAllow = (NSNumber)entitlements.objectForKey("get-task-allow");
        Integer provProfileSignedType;
        if (udidList != null && udidList.count() > 0 && (taskAllow == null || !taskAllow.boolValue())) {
            IOSAppDatahandler.logger.log(Level.INFO, "Provisioning profile is an Ad-Hoc signed");
            provProfileSignedType = 1;
        }
        else if (provAllDevices != null && provAllDevices.boolValue()) {
            IOSAppDatahandler.logger.log(Level.INFO, "Provisioning profile is an distribution signed");
            provProfileSignedType = 3;
        }
        else {
            IOSAppDatahandler.logger.log(Level.INFO, "Provisioning profile is an developer signed");
            provProfileSignedType = 2;
        }
        final int provManagementType = 1;
        final StringBuilder platformType = new StringBuilder();
        final NSArray platformArray = (NSArray)dict.objectForKey("Platform");
        for (int i = 0; i < platformArray.count(); ++i) {
            platformType.append(platformArray.objectAtIndex(i));
        }
        if (appDo.isEmpty()) {
            final Row appleProvProfileRow = new Row("AppleProvProfilesDetails");
            appleProvProfileRow.set("PROV_NAME", (Object)provName.toString());
            appleProvProfileRow.set("PROV_UUID", (Object)udid.toString());
            appleProvProfileRow.set("CUSTOMER_ID", (Object)customerId);
            appleProvProfileRow.set("PROV_EXPIRY_DATE", (Object)provProfileExpiryDateLong);
            appleProvProfileRow.set("PROV_PROFILE_TYPE", (Object)provManagementType);
            appleProvProfileRow.set("PROV_PROFILE_PLATFORM", (Object)platformType.toString());
            appDo.addRow(appleProvProfileRow);
            final Row appleProvProfileExtn = new Row("AppleProvProfilesExtn");
            appleProvProfileExtn.set("PROV_ID", appleProvProfileRow.get("PROV_ID"));
            appleProvProfileExtn.set("PROV_APP_ID", (Object)provAppId.toString());
            appleProvProfileExtn.set("CREATED_TIME", (Object)provProfileCreationDateLong);
            appleProvProfileExtn.set("TEAM_ID", (Object)teamIdentifier.toString());
            appleProvProfileExtn.set("PROV_PROFILE_PATH", (Object)fileDetailsJson.get("destFileName"));
            appleProvProfileExtn.set("PROV_PROV_SIGNED_TYPE", (Object)provProfileSignedType);
            appDo.addRow(appleProvProfileExtn);
            if (udidList != null) {
                final Object provID = appleProvProfileRow.get("PROV_ID");
                for (int j = 0; j < udidList.count(); ++j) {
                    final Row appleProvUdid = new Row("AppleProvProfilesToUDID");
                    appleProvUdid.set("PROV_ID", provID);
                    appleProvUdid.set("UDID", (Object)udidList.objectAtIndex(j).toString());
                    appDo.addRow(appleProvUdid);
                }
            }
        }
        else {
            final Row appleProvProfileRow = appDo.getRow("AppleProvProfilesDetails");
            appleProvProfileRow.set("PROV_NAME", (Object)provName.toString());
            appleProvProfileRow.set("PROV_UUID", (Object)udid.toString());
            appleProvProfileRow.set("CUSTOMER_ID", (Object)customerId);
            appleProvProfileRow.set("PROV_EXPIRY_DATE", (Object)provProfileExpiryDateLong);
            appleProvProfileRow.set("PROV_PROFILE_TYPE", (Object)provManagementType);
            appleProvProfileRow.set("PROV_PROFILE_PLATFORM", (Object)platformType.toString());
            appDo.updateRow(appleProvProfileRow);
            final Row appleProvProfileExtn = appDo.getRow("AppleProvProfilesExtn");
            appleProvProfileExtn.set("PROV_ID", appleProvProfileRow.get("PROV_ID"));
            appleProvProfileExtn.set("PROV_APP_ID", (Object)provAppId.toString());
            appleProvProfileExtn.set("CREATED_TIME", (Object)provProfileCreationDateLong);
            appleProvProfileExtn.set("TEAM_ID", (Object)teamIdentifier.toString());
            appleProvProfileExtn.set("PROV_PROFILE_PATH", (Object)fileDetailsJson.get("destFileName"));
            appleProvProfileExtn.set("PROV_PROV_SIGNED_TYPE", (Object)provProfileSignedType);
            appDo.updateRow(appleProvProfileExtn);
            if (udidList != null) {
                final List toBeRemovedUDID = new ArrayList();
                final Iterator udidIter = appDo.getRows("AppleProvProfilesToUDID");
                while (udidIter.hasNext()) {
                    final Row udidRow = udidIter.next();
                    final Object deviceUDID = udidRow.get("UDID");
                    if (!udidList.containsObject(deviceUDID)) {
                        toBeRemovedUDID.add(deviceUDID);
                    }
                    else {
                        final int k = udidList.indexOfObject(deviceUDID);
                        udidList.remove(k);
                    }
                }
                for (int l = 0; l < udidList.count(); ++l) {
                    final Object provID2 = appleProvProfileRow.get("PROV_ID");
                    final Row appleProvUdid2 = new Row("AppleProvProfilesToUDID");
                    appleProvUdid2.set("PROV_ID", provID2);
                    appleProvUdid2.set("UDID", (Object)udidList.objectAtIndex(l).toString());
                    appDo.addRow(appleProvUdid2);
                }
                if (!toBeRemovedUDID.isEmpty()) {
                    appDo.deleteRows("AppleProvProfilesToUDID", new Criteria(Column.getColumn("AppleProvProfilesToUDID", "UDID"), (Object)toBeRemovedUDID.toArray(), 8));
                }
            }
        }
        MDMUtil.getPersistence().update(appDo);
        return (Long)appDo.getValue("AppleProvProfilesDetails", "PROV_ID", (Criteria)null);
    }
    
    public JSONObject copyProvFileToServerPath(final JSONObject json) throws Exception {
        final Long customerId = (Long)json.get("customerId");
        final Long packageId = (Long)json.get("packageId");
        final Long appId = (Long)json.get("appId");
        final String sourceFile = (String)json.get("sourceFile");
        final String appRepositoryFilePath = MDMAppMgmtHandler.getInstance().getAppRepositoryFolderPath(customerId, packageId, appId);
        final String destFileDCPath = MDMAppMgmtHandler.getInstance().getAppRepositoryFolderDBPath(customerId, packageId, appId);
        final HashMap appFileSourceMap = MDMAppMgmtHandler.getInstance().copyAppRepositoryFiles(sourceFile, appRepositoryFilePath, destFileDCPath, false, false);
        json.put("destFileName", appFileSourceMap.get("destDCFileName"));
        return json;
    }
    
    @Override
    public JSONObject addProvProfileForApp(final JSONObject requestJSON) throws APIHTTPException, JSONException, Exception {
        JSONObject responseJson = new JSONObject();
        this.setReleaseLabelId(requestJSON);
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final FileFacade fileFacade = new FileFacade();
        if (!requestJSON.getJSONObject("msg_body").has("app_file")) {
            throw new APIHTTPException("5001", new Object[] { "app_file" });
        }
        final Long fileId = Long.valueOf(requestJSON.getJSONObject("msg_body").get("app_file").toString());
        final String fileName = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", fileId)).get("file_path"));
        final String tempFileName = fileFacade.getTempLocation(fileName);
        new FileFacade().writeFile(tempFileName, ApiFactoryProvider.getFileAccessAPI().readFile(fileName));
        final Long packageID = APIUtil.getResourceID(requestJSON, "app_id");
        final NSDictionary dict = new IosIPAExtractor().decryptProvProfile(tempFileName);
        final NSDictionary entitlements = (NSDictionary)dict.objectForKey("Entitlements");
        final NSString provAppId = (NSString)entitlements.objectForKey("application-identifier");
        JSONObject json = new JSONObject();
        final Long appID = (Long)DBUtil.getValueFromDB("MdPackageToAppData", "PACKAGE_ID", (Object)this.packageId, "APP_ID");
        json.put("appId", (Object)appID);
        json.put("customerId", (Object)customerId);
        json.put("packageId", (Object)this.packageId);
        json.put("sourceFile", (Object)tempFileName);
        json = this.copyProvFileToServerPath(json);
        final DataObject provDO = this.getProvProfileDetailsDO(appID, provAppId.toString(), null, this.releaseLabelId);
        if (!provDO.isEmpty()) {
            final Long provProvId = this.addOrUpdateProvDetails(dict, json);
            final Row packageRow = provDO.getRow("MdPackageToProvProfileRel");
            if (packageRow != null) {
                packageRow.set("PROV_ID", (Object)provProvId);
                provDO.updateRow(packageRow);
            }
            else {
                final Row appExtenRelRow = provDO.getRow("MdAppExtnToProvProfileRel");
                if (appExtenRelRow != null) {
                    appExtenRelRow.set("PROV_ID", (Object)provProvId);
                    provDO.updateRow(appExtenRelRow);
                }
            }
            MDMUtil.getPersistence().update(provDO);
            final JSONObject finalJson = this.getProvProfileDetailsAsJSON(packageID, provAppId.toString(), null, this.releaseLabelId);
            responseJson = (JSONObject)finalJson.get(provProvId.toString());
            return responseJson;
        }
        IOSAppDatahandler.logger.log(Level.SEVERE, "Given provisioning profile is invalid");
        throw new APIHTTPException("PROV001", new Object[0]);
    }
    
    public DataObject getProvProfileDetailsDO(final Long appId, final String provAppId, final Long provId, final Long releaseLabelID) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleProvProfilesDetails"));
        sQuery.addJoin(new Join("AppleProvProfilesDetails", "AppleProvProfilesExtn", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 1));
        sQuery.addJoin(new Join("AppleProvProfilesDetails", "MdPackageToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 1));
        sQuery.addJoin(new Join("MdPackageToProvProfileRel", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 1));
        sQuery.addJoin(new Join("AppleProvProfilesDetails", "MdAppExtnToProvProfileRel", new String[] { "PROV_ID" }, new String[] { "PROV_ID" }, 1));
        sQuery.addJoin(new Join("MdAppExtnToProvProfileRel", "MdAppToExtnRel", new String[] { "APP_EXTN_ID" }, new String[] { "APP_EXTN_ID" }, 1));
        sQuery.addJoin(new Join("MdPackageToAppData", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        Criteria finalCri;
        final Criteria appIdCri = finalCri = new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
        if (provAppId != null) {
            finalCri = finalCri.and(new Criteria(new Column("AppleProvProfilesExtn", "PROV_APP_ID"), (Object)provAppId, 0));
        }
        if (provId != null) {
            finalCri = finalCri.and(new Criteria(new Column("AppleProvProfilesDetails", "PROV_ID"), (Object)provId, 0));
        }
        if (releaseLabelID != null) {
            finalCri = finalCri.and(new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 0));
        }
        sQuery.setCriteria(finalCri);
        final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
        return dataObject;
    }
    
    private JSONObject getProvProfileDetailsAsJSON(final Long packageId, final String provAppIdStr, final Long provIdentifier, final Long releaseLabelID) throws Exception {
        final JSONObject responseJson = new JSONObject();
        final Long appId = (packageId == null) ? null : ((Long)DBUtil.getValueFromDB("MdPackageToAppData", "PACKAGE_ID", (Object)packageId, "APP_ID"));
        final DataObject provDO = this.getProvProfileDetailsDO(appId, provAppIdStr, provIdentifier, releaseLabelID);
        if (!provDO.isEmpty()) {
            final Iterator iter = provDO.getRows("AppleProvProfilesDetails");
            while (iter.hasNext()) {
                final JSONObject json = new JSONObject();
                final Row appleProvProfileRow = iter.next();
                final Long provId = (Long)appleProvProfileRow.get("PROV_ID");
                final Long expiryDate = (Long)appleProvProfileRow.get("PROV_EXPIRY_DATE");
                final String SupportedPlatform = (String)appleProvProfileRow.get("PROV_PROFILE_PLATFORM");
                final String provName = (String)appleProvProfileRow.get("PROV_NAME");
                final String udid = (String)appleProvProfileRow.get("PROV_UUID");
                final Row appleProvProfilExtneRow = provDO.getRow("AppleProvProfilesExtn", new Criteria(new Column("AppleProvProfilesExtn", "PROV_ID"), (Object)provId, 0));
                final String teamId = (String)appleProvProfilExtneRow.get("TEAM_ID");
                final String provAppId = (String)appleProvProfilExtneRow.get("PROV_APP_ID");
                final Long createdTime = (Long)appleProvProfilExtneRow.get("CREATED_TIME");
                json.put("PROV_ID", (Object)provId);
                json.put("PROV_EXPIRY_DATE", (Object)expiryDate);
                json.put("PROV_PROFILE_PLATFORM", (Object)SupportedPlatform);
                json.put("PROV_NAME", (Object)provName);
                json.put("PROV_UUID", (Object)udid);
                json.put("TEAM_ID", (Object)teamId);
                json.put("PROV_APP_ID", (Object)provAppId);
                json.put("CREATED_TIME", (Object)createdTime);
                responseJson.put(provId.toString(), (Object)json);
            }
        }
        return responseJson;
    }
    
    @Override
    public JSONObject getProvProfileDetails(final JSONObject requestJSON) throws APIHTTPException, JSONException, Exception {
        JSONObject responseJson = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        this.setReleaseLabelId(requestJSON);
        final Long provId = APIUtil.getResourceID(requestJSON, "prov_id");
        responseJson = this.getProvProfileDetailsAsJSON(null, null, provId, this.releaseLabelId);
        responseJson = (JSONObject)responseJson.get(provId.toString());
        return responseJson;
    }
    
    @Override
    public JSONObject getProvProfileDetailsFromAppId(final JSONObject requestJSON) throws APIHTTPException, JSONException, Exception {
        JSONObject responseJson = new JSONObject();
        this.setReleaseLabelId(requestJSON);
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        final Long appId = APIUtil.getResourceID(requestJSON, "app_id");
        responseJson = this.getProvProfileDetailsAsJSON(appId, null, null, this.releaseLabelId);
        return responseJson;
    }
    
    private Long getAppExpiryDate(final Long packageId) throws Exception {
        final JSONObject responseJson = this.getProvProfileDetailsAsJSON(packageId, null, null, null);
        Long expiryDate = -1L;
        final Iterator keyIterator = responseJson.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            final Long provExpiryDt = (Long)((JSONObject)responseJson.get(key)).get("PROV_EXPIRY_DATE");
            if (expiryDate == -1L) {
                expiryDate = provExpiryDt;
            }
            else {
                if (provExpiryDt >= expiryDate) {
                    continue;
                }
                expiryDate = provExpiryDt;
            }
        }
        return expiryDate;
    }
    
    @Override
    public JSONObject getPrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final String isVppSkippedWhenAddingApp = CustomerParamsHandler.getInstance().getParameterValue("isVppSkippedWhenAddingApp", (long)this.customerID);
        final int iosAppsNotPurchasedFromPortal = VPPAppMgmtHandler.getInstance().getVPPAppsCountNotPurchasedFromPortal(this.customerID);
        final Integer globalVppAppAssignmentType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(this.customerID);
        responseJSON.put("is_vpp_benefits_skipped", Boolean.parseBoolean(isVppSkippedWhenAddingApp));
        responseJSON.put("no_of_non_portal_apps", iosAppsNotPurchasedFromPortal);
        responseJSON.put("vpp_type", (Object)globalVppAppAssignmentType);
        return responseJSON;
    }
    
    @Override
    public void updatePrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        final String isVppSkipped = requestJSON.getJSONObject("msg_body").get("is_vpp_skipped").toString();
        if (isVppSkipped != null && isVppSkipped.equalsIgnoreCase("true")) {
            CustomerParamsHandler.getInstance().addOrUpdateParameter("isVppSkippedWhenAddingApp", "true", (long)this.customerID);
            final int iosAppsNotPurchasedFromPortal = VPPAppMgmtHandler.getInstance().getVPPAppsCountNotPurchasedFromPortal(this.customerID);
            CustomerParamsHandler.getInstance().addOrUpdateParameter("iosAppsNotPurchasedFromPortal", String.valueOf(iosAppsNotPurchasedFromPortal), (long)this.customerID);
        }
    }
    
    @Override
    public JSONObject getModifiedAppProps(final boolean isEnterpriseApp, final String identifier, final String appName) {
        JSONObject appMDMProps = new JSONObject();
        if (this.platformType == 1 && isEnterpriseApp && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
            appMDMProps = IOSModifiedEnterpriseAppsUtil.getMDMPropsForApp(identifier, appName, Boolean.TRUE, this.customerID);
        }
        return appMDMProps;
    }
}
