package com.me.mdm.server.apps.ios.vpp;

import java.util.Hashtable;
import java.util.Map;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.apps.AppleAppStoreSearchHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import com.me.mdm.server.apps.ios.ContentMetaDataAPIHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import com.google.gson.Gson;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseEnterpriseBusinessStore;

public class IOSVPPEnterpriseBusinessStore extends BaseEnterpriseBusinessStore
{
    Logger logger;
    VPPAssetsAPIHandler vppAssetsAPIHandler;
    
    public IOSVPPEnterpriseBusinessStore(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.platformType = 1;
        this.defaultCategory = "Business";
    }
    
    public VPPAssetsAPIHandler getVppAssetsAPIHandler() {
        return this.vppAssetsAPIHandler;
    }
    
    @Override
    public JSONObject inviteUsers(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getUsers(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getDevices(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    protected void updateSyncProgressTime() throws Exception {
        MDBusinessStoreUtil.updateCurrentSyncLastProgress(this.businessStoreID);
    }
    
    private int getAppPackageType(final Object price) {
        final Double sPrice = (Double)price;
        if (sPrice == 0.0) {
            return 0;
        }
        return 1;
    }
    
    private Boolean isPaidApp(final Object price) {
        final Double sPrice = (Double)price;
        if (sPrice == 0.0) {
            return false;
        }
        return true;
    }
    
    @Override
    public JSONObject processAppData(final JSONObject appDetails) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject appJSON = (JSONObject)appDetails.get("appDetailsObject");
            final Gson gson = new Gson();
            final ContentMetaDataAppDetails appDetailsObject = (ContentMetaDataAppDetails)gson.fromJson(String.valueOf(appJSON), (Class)ContentMetaDataAppDetails.class);
            final Long userId = appDetails.getLong("USER_ID");
            jsonObject.put("APP_NAME", (Object)appDetailsObject.getAppName());
            jsonObject.put("APP_VERSION", (Object)appDetailsObject.getAppVersion());
            jsonObject.put("EXTERNAL_APP_VERSION_ID", (Object)appDetailsObject.getExternalAppVersionID());
            jsonObject.put("PLATFORM_TYPE", 1);
            jsonObject.put("APP_TITLE", (Object)"");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)appDetailsObject.getBundleId());
            jsonObject.put("APP_CATEGORY_NAME", (Object)appDetailsObject.getPrimaryGenreName());
            jsonObject.put("COUNTRY_CODE", appDetails.get("COUNTRY_CODE"));
            jsonObject.put("PACKAGE_TYPE", this.getAppPackageType(appDetailsObject.getAppPrice()));
            jsonObject.put("licenseType", 2);
            jsonObject.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
            jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
            final JSONObject packageAppDataJSON = new JSONObject();
            packageAppDataJSON.put("STORE_ID", (Object)appDetailsObject.getAdamId());
            packageAppDataJSON.put("SUPPORTED_DEVICES", (Object)appDetailsObject.getSupportDevice());
            final String storeUrl = appDetailsObject.getAppStoreURL();
            if (storeUrl != null) {
                packageAppDataJSON.put("STORE_URL", (Object)storeUrl);
            }
            packageAppDataJSON.put("DESCRIPTION", (Object)appDetailsObject.getAppDescription());
            final String minOS = appDetailsObject.getMinimumOSVersion();
            if (!MDMStringUtils.isEmpty(minOS)) {
                packageAppDataJSON.put("MIN_OS", (Object)minOS);
            }
            final String displayImage = appDetailsObject.getAppIconImageURL();
            if (displayImage != null) {
                jsonObject.put("DISPLAY_IMAGE_LOC", (Object)displayImage);
            }
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", (Object)appDetailsObject.getIsPaidApp());
            packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
            final Integer private_app_type = appDetails.optInt("privateApp");
            if (private_app_type != null && private_app_type != 0) {
                packageAppGroupJSON.put("PRIVATE_APP_TYPE", (Object)private_app_type);
            }
            final JSONObject packagePolicyJSON = new JSONObject();
            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", true);
            packagePolicyJSON.put("PREVENT_BACKUP", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            final JSONObject appAssignableDetails = new JSONObject();
            if (appDetails.has("appCountDetails")) {
                final JSONObject appCountDetails = (JSONObject)appDetails.get("appCountDetails");
                final Boolean isDeviceAssignable = (Boolean)appCountDetails.get("IS_DEVICE_ASSIGNABLE");
                if (isDeviceAssignable) {
                    appAssignableDetails.put("IS_DEVICE_ASSIGNABLE", (Object)isDeviceAssignable);
                    appAssignableDetails.put("LICENSE_TYPE", appDetails.get("LICENSE_TYPE"));
                }
                jsonObject.put("MdAppAssignableDetailsForm", (Object)appAssignableDetails);
            }
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            jsonObject.put("PROFILE_ID", -1);
            jsonObject.put("CREATED_BY", (Object)userId);
            jsonObject.put("PROFILE_NAME", (Object)appDetailsObject.getAppName());
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"");
            jsonObject.put("PROFILE_TYPE", 2);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", true);
            jsonObject.put("PACKAGE_ADDED_BY", (Object)userId);
            final JSONArray mdApps = new JSONArray();
            jsonObject.put("success", true);
            final Object isNewVersion = appDetailsObject.getIsNewVersion();
            if (isNewVersion != null) {
                jsonObject.put("isNewVersion", (Object)isNewVersion);
            }
            final Row pkgToAppDataRow = this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(Column.getColumn("MdPackageToAppData", "STORE_ID"), (Object)appDetailsObject.getAdamId(), 0));
            if (pkgToAppDataRow != null) {
                final Long packageID = (Long)pkgToAppDataRow.get("PACKAGE_ID");
                final JSONObject policyJSON = AppsUtil.getInstance().getPackagePolicy(packageID);
                final Boolean preventBackup = policyJSON.optBoolean("prevent_backup", (boolean)Boolean.TRUE);
                final Boolean removeAppWithProfile = policyJSON.optBoolean("remove_app_with_profile", (boolean)Boolean.TRUE);
                final int oldSupportedDevices = (int)pkgToAppDataRow.get("SUPPORTED_DEVICES");
                final int newSupportedDevices = packageAppDataJSON.getInt("SUPPORTED_DEVICES");
                packageAppDataJSON.put("SUPPORTED_DEVICES", newSupportedDevices | oldSupportedDevices);
                packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", (Object)removeAppWithProfile);
                packagePolicyJSON.put("PREVENT_BACKUP", (Object)preventBackup);
                final Long appGroupId = (Long)pkgToAppDataRow.get("APP_GROUP_ID");
                final String appStoreID = pkgToAppDataRow.get("STORE_ID").toString();
                packageAppGroupJSON.put("STORE_ID", (Object)appStoreID);
                jsonObject.put("APP_GROUP_ID", (Object)appGroupId);
            }
            if (this.getVppAssetsAPIHandler().toBeMigratedListContainsAdamID(appDetailsObject.getAdamId())) {
                jsonObject.put("isFreeToVppMigrated", (Object)Boolean.TRUE);
            }
            else {
                jsonObject.put("isFreeToVppMigrated", (Object)Boolean.FALSE);
            }
            final String bundledId = appDetailsObject.getBundleId();
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo(bundledId, this.customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
            jsonObject.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            jsonObject.put("PackagePolicyForm", (Object)packagePolicyJSON);
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
            final JSONObject curPackage = new JSONObject();
            curPackage.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
            curPackage.put("packageIdentifier", (Object)appDetailsObject.getBundleId());
            curPackage.put("APP_VERSION", (Object)appDetailsObject.getAppVersion());
            mdApps.put((Object)curPackage);
            jsonObject.put("MDAPPS", (Object)mdApps);
        }
        catch (final Exception e) {
            jsonObject.put("MDAPPS", (Object)new JSONArray());
            jsonObject.put("success", false);
            this.logger.log(Level.SEVERE, "Exception in processAppData", e);
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject getCredential(final JSONObject jsonObject) throws Exception {
        final JSONObject response = new JSONObject();
        try {
            this.vppAssetsAPIHandler = new VPPAssetsAPIHandler(this.businessStoreID, this.customerID);
            MDBusinessStoreUtil.updateCurrentSyncLastProgress(this.businessStoreID);
            final JSONObject clientContextJson = VPPClientConfigHandler.getInstance().checkAndUpdateVPPClientConfig(this.businessStoreID, this.customerID);
            final String resultJsonStatus = (String)clientContextJson.get("status");
            if (!resultJsonStatus.contains("error")) {
                response.put("Success", true);
            }
            else {
                String remarks = null;
                Integer errorNumber = Integer.parseInt(clientContextJson.get("errorNumber").toString());
                String remarksParams = null;
                remarks = clientContextJson.getString("errorMessage");
                if (errorNumber == null) {
                    errorNumber = 888800;
                    remarks = MDMI18N.getI18Nmsg("mdm.vpp.sync.failureCommonMessage");
                }
                else if (errorNumber == 888801) {
                    remarksParams = clientContextJson.getString("clientContext");
                }
                else if (errorNumber == 9621) {
                    errorNumber = 888805;
                }
                else if (errorNumber == 9625) {
                    errorNumber = 888806;
                }
                else {
                    errorNumber = 888800;
                }
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, errorNumber, remarks, remarksParams, null);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getCredential", e);
        }
        return response;
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject allApps = new JSONObject();
        JSONArray appsArray = new JSONArray();
        final Boolean isFirstSync = jsonObject.getBoolean("isFirstSync");
        if (jsonObject.optBoolean("Success")) {
            MDBusinessStoreUtil.setInitialSyncDetails(this.businessStoreID);
            final JSONObject resultJSON = this.vppAssetsAPIHandler.syncVPPAssets(isFirstSync);
            if (resultJSON.has("error")) {
                final Properties prop = (Properties)jsonObject.get("error");
                final Integer errorCode = Integer.parseInt(((Hashtable<K, Object>)prop).get("errorNumber").toString());
                final String remarks = ((Hashtable<K, Object>)prop).get("errorMessage").toString();
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, errorCode, remarks, "", null);
            }
            else if (!this.vppAssetsAPIHandler.toBeSyncedAdamIDList.isEmpty() && this.vppAssetsAPIHandler.toBeSyncedAdamIDList != null) {
                this.setStoreAppsPkgToAppDO(new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)this.vppAssetsAPIHandler.toBeSyncedAdamIDList.toArray(), 8));
                final List<List> storeList = MDMUtil.getInstance().splitListIntoSubLists(this.vppAssetsAPIHandler.toBeSyncedAdamIDList, 300);
                for (final List subList : storeList) {
                    final HashMap allAppDetails = new ContentMetaDataAPIHandler().getVPPAppDetails(subList, this.businessStoreID);
                    appsArray = this.verifyAppAndSetVppDetails(subList, allAppDetails, appsArray);
                }
            }
            allApps.put("Apps", (Object)appsArray);
        }
        else {
            allApps.put("Success", false);
        }
        return allApps;
    }
    
    private JSONArray verifyAppAndSetVppDetails(final List appList, final HashMap appDetails, final JSONArray appsArray) throws Exception {
        final List tempAppList = new ArrayList(appList);
        for (int i = 0; i < appList.size(); ++i) {
            final int adamID = Integer.parseInt(tempAppList.get(i));
            try {
                final HashMap singleAppDetails = new HashMap();
                ContentMetaDataAppDetails appDetailsObject = null;
                if (!appDetails.containsKey(adamID)) {
                    this.logger.log(Level.INFO, "AppDetails not available from contentMetaDataAPI for adamID: {0}. check app details in itunesLookupAPI. Hence getting app details individually", adamID);
                    appDetailsObject = new AppleAppStoreSearchHandler().getCompleteAppDetailsObject(adamID, Boolean.TRUE, this.vppAssetsAPIHandler.vppCountrycode);
                    if (appDetailsObject == null) {
                        this.logger.log(Level.INFO, "AppDetails not available in Itunes API for adamID: {0}.App may not be available in appstore.", adamID);
                        continue;
                    }
                }
                else {
                    appDetailsObject = appDetails.get(adamID);
                }
                final boolean isError = VPPAssetsAPIHandler.checkIfError(appDetailsObject, "errorCode");
                if (!isError) {
                    singleAppDetails.put("COUNTRY_CODE", this.vppAssetsAPIHandler.vppCountrycode);
                    final Properties appCountDetails = this.vppAssetsAPIHandler.vppAssetMap.get(String.valueOf(adamID));
                    singleAppDetails.put("appCountDetails", appCountDetails);
                    singleAppDetails.put("LICENSE_TYPE", this.vppAssetsAPIHandler.appAssignmentType);
                }
                final Row pkgToAppDataRow = this.pkgToAppDO.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "STORE_ID"), (Object)String.valueOf(adamID), 0));
                Long appGroupId = null;
                boolean isVppApp = false;
                if (pkgToAppDataRow != null) {
                    appGroupId = (Long)pkgToAppDataRow.get("APP_GROUP_ID");
                    final JSONObject perAppJson = new JSONObject();
                    perAppJson.put("APP_VERSION", (Object)appDetailsObject.getAppVersion());
                    perAppJson.put("EXTERNAL_APP_VERSION_ID", (Object)appDetailsObject.getExternalAppVersionID());
                    if (AppsUtil.getInstance().isNewVersion(appGroupId, perAppJson, this.customerID, this.platformType)) {
                        appDetailsObject.setIsNewVersion(true);
                    }
                    else {
                        appDetailsObject.setIsNewVersion(false);
                    }
                    final Row packageToAppGrpRow = this.pkgToAppDO.getRow("MdPackageToAppGroup", new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0));
                    isVppApp = (boolean)packageToAppGrpRow.get("IS_PURCHASED_FROM_PORTAL");
                }
                else {
                    appDetailsObject.setIsNewVersion(true);
                }
                final String bundleID = appDetailsObject.getBundleId();
                if (bundleID != null && bundleID.equalsIgnoreCase("com.manageengine.mdm.iosagent") && (appGroupId == null || isVppApp)) {
                    this.logger.log(Level.INFO, "ME MDM app found in VPP token with businessStoreID {0}. Hence hiding the SILENT_INSTALL_ME_MDM_APPS message", new Object[] { this.businessStoreID });
                    MessageProvider.getInstance().hideMessage("SILENT_INSTALL_ME_MDM_APPS", this.customerID);
                }
                if (bundleID != null) {
                    singleAppDetails.put("appDetailsObject", appDetailsObject);
                    appsArray.put((Map)singleAppDetails);
                }
                else {
                    this.logger.log(Level.INFO, "App with storeID: {0} is not found in App Store", adamID);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in obtaining app details for app: {0}", adamID);
                this.logger.log(Level.SEVERE, "Exception: ", e);
            }
        }
        return appsArray;
    }
    
    @Override
    public JSONObject installAppsToUsers(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject installAppsToDevices(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject assignAppsToUsers(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getAppsAssignedForUser(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getAppsAssignedForDevice() throws Exception {
        return null;
    }
    
    @Override
    public JSONObject removeAppsToUsers(final JSONObject jsonObject, final JSONArray responseAray) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject removeAppsToDevices(final JSONObject jsonObject) throws Exception {
        return null;
    }
}
