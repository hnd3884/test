package com.me.mdm.server.apps.windows;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.server.windows.apps.nativeapp.WindowsNativeAppHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.net.URLEncoder;
import java.util.HashSet;
import org.json.JSONArray;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseEnterpriseBusinessStore;

public class WindowsEnterpriseBusinessStore extends BaseEnterpriseBusinessStore
{
    private BusinessStoreAPIAccess businessStoreAPIAccess;
    Logger logger;
    
    public WindowsEnterpriseBusinessStore() {
        this.businessStoreAPIAccess = null;
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    @Override
    public JSONObject getCredential(final JSONObject jsonObject) throws Exception {
        this.logger.log(Level.INFO, "Getting Credentials to Sync Windows Business Store");
        this.businessStoreAPIAccess = new BusinessStoreAPIAccess();
        final JSONObject response = new JSONObject();
        if (this.businessStoreAPIAccess.initialise(jsonObject)) {
            response.put("Success", true);
        }
        else {
            this.logger.log(Level.INFO, "Getting credentials failed");
            response.put("Success", false);
            response.put("Error", (Object)"Couldn't fetch Access Token");
        }
        return response;
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
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        this.logger.log(Level.INFO, "List of Apps being Queried");
        final JSONObject params = new JSONObject();
        params.put("Type", (Object)"Inventory");
        String continutionToken = null;
        final JSONArray appsArray = new JSONArray();
        final JSONObject listofApps = new JSONObject();
        final HashSet<String> productIdSet = new HashSet<String>();
        do {
            final JSONObject apps = this.businessStoreAPIAccess.getDataFromBusinessStore(params);
            final String message = apps.optString("Message");
            final String code = apps.optString("code");
            if (message != null && message.contains("denied")) {
                listofApps.put("Success", false);
                listofApps.put("Error", (Object)message);
                return listofApps;
            }
            if (message != null && message.contains("InternalServerError")) {
                listofApps.put("Success", false);
                listofApps.put("Error", (Object)"InternalServerError");
                this.logger.log(Level.WARNING, "Exception from BusinessStore : {0}", code);
                return listofApps;
            }
            final JSONArray curAppsArray = apps.optJSONArray("inventoryEntries");
            if (curAppsArray != null) {
                this.validateAndPopulateApps(appsArray, curAppsArray, productIdSet);
            }
            continutionToken = apps.optString("continuationToken");
            if (continutionToken == null || continutionToken.equals("")) {
                continue;
            }
            params.put("continuationToken", (Object)URLEncoder.encode(continutionToken, "UTF-8"));
        } while (continutionToken != null && !continutionToken.equals(""));
        if (appsArray == null) {
            listofApps.put("Success", false);
            listofApps.put("Error", (Object)"Error in Fetching Apps");
        }
        else {
            listofApps.put("Success", true);
            listofApps.put("Apps", (Object)appsArray);
        }
        return listofApps;
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
    public JSONObject removeAppsToUsers(final JSONObject jsonObject, final JSONArray responseArray) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject removeAppsToDevices(final JSONObject jsonObject) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject processAppData(final JSONObject jsonObject) throws Exception {
        this.logger.log(Level.INFO, "inside ProcessAppData for Windows :");
        this.logger.log(Level.INFO, "data passed to ProcessAppData : {0}", jsonObject);
        final JSONObject appJSON = new JSONObject();
        JSONObject windowsAppsJSON = null;
        final JSONObject MdPackagetoAppdata = null;
        try {
            final JSONObject productJSON = jsonObject.getJSONObject("productKey");
            final String productID = String.valueOf(productJSON.get("productId"));
            final String skuID = String.valueOf(productJSON.get("skuId"));
            final String distributionPolicy = String.valueOf(jsonObject.get("distributionPolicy"));
            final String licenseType = String.valueOf(jsonObject.get("licenseType"));
            final Long customerID = jsonObject.getLong("CustomerID");
            boolean isPaid = false;
            final String productKind = jsonObject.optString("productKind");
            Boolean isApp = Boolean.TRUE;
            if (!MDMStringUtils.isEmpty(productKind) && !productKind.equalsIgnoreCase("Application") && !productKind.equalsIgnoreCase("Game")) {
                isApp = Boolean.FALSE;
            }
            if (!isApp) {
                appJSON.put("MDAPPS", (Object)new JSONArray());
                appJSON.put("success", (Object)Boolean.FALSE);
                appJSON.put("reason", (Object)"SubscriptionSynced");
                this.logger.log(Level.INFO, "The current item is not and app hence not attempting to sync");
                return appJSON;
            }
            if (!distributionPolicy.contains("open")) {
                isPaid = true;
                final int availableSeats = jsonObject.getInt("seatCapacity");
                final int totalSeats = jsonObject.getInt("seatCapacity");
                final int provisionedSeats = totalSeats - availableSeats;
                appJSON.put("TOTAL_APP_COUNT", totalSeats);
                appJSON.put("AVAILABLE_APP_COUNT", availableSeats);
                appJSON.put("ASSIGNED_APP_COUNT", provisionedSeats);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncPaidBstoreApps")) {
                    appJSON.put("MDAPPS", (Object)new JSONArray());
                    appJSON.put("success", (Object)Boolean.FALSE);
                    this.logger.log(Level.INFO, "The current item a paid app not syncing until feature param is enabled");
                    return appJSON;
                }
            }
            final JSONObject params = new JSONObject();
            params.put("Type", (Object)"PackageBasicInfo");
            params.put("PackageID", (Object)productID);
            params.put("SKUID", (Object)skuID);
            JSONObject basicDetails = this.businessStoreAPIAccess.getDataFromBusinessStore(params);
            final String packageFamilyName = String.valueOf(basicDetails.get("packageFamilyName"));
            final String category = String.valueOf(basicDetails.get("category"));
            params.put("Type", (Object)"LocalisedDetails");
            params.put("Locale", (Object)"en-us");
            basicDetails = this.businessStoreAPIAccess.getDataFromBusinessStore(params);
            final String name = String.valueOf(basicDetails.get("displayName"));
            String description = String.valueOf(basicDetails.get("description"));
            if (description.length() > 3000) {
                description = description.substring(0, 2997).concat("...");
            }
            final JSONObject imageDetails = this.getImageDetails(basicDetails);
            final String imgURL = String.valueOf(imageDetails.get("location"));
            final String imgBGColor = String.valueOf(imageDetails.get("backgroundColor"));
            params.put("Type", (Object)"PackageDetails");
            final JSONObject packageDetails = this.businessStoreAPIAccess.getDataFromBusinessStore(params);
            this.logger.log(Level.INFO, "Current App being processed : {0}", name);
            final JSONArray compatiblePackages = this.getCompatiblePackages(packageDetails);
            final JSONArray mdApps = new JSONArray();
            for (int i = 0; i < compatiblePackages.length(); ++i) {
                final JSONObject curPackage = (JSONObject)compatiblePackages.get(i);
                final JSONObject packageToappData = curPackage.getJSONObject("MdPackageToAppDataFrom");
                packageToappData.put("DESCRIPTION", (Object)description);
                appJSON.put("MdPackageToAppDataFrom", (Object)packageToappData);
                windowsAppsJSON = curPackage.getJSONObject("WindowsAppData");
                windowsAppsJSON.put("IMG_BG", (Object)imgBGColor);
                windowsAppsJSON.put("ProductID", (Object)productID);
                windowsAppsJSON.put("SKUID", (Object)skuID);
                if (licenseType.contains("offline") && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncOfflineBstoreLicense")) {
                    params.put("Type", (Object)"OfflineLicense");
                    params.put("ContentID", windowsAppsJSON.get("contentId"));
                    final JSONObject licenseDetails = this.businessStoreAPIAccess.getDataFromBusinessStore(params);
                    windowsAppsJSON.put("licenseBlob", licenseDetails.get("licenseBlob"));
                    windowsAppsJSON.put("offline", true);
                }
                mdApps.put((Object)curPackage);
            }
            appJSON.put("BUNDLE_IDENTIFIER", (Object)packageFamilyName);
            appJSON.put("PROFILE_DESCRIPTION", (Object)"App Description");
            appJSON.put("APP_NAME", (Object)name);
            appJSON.put("APP_CATEGORY_NAME", (Object)category);
            appJSON.put("MDAPPS", (Object)mdApps);
            appJSON.put("DISPLAY_IMAGE_LOC", (Object)imgURL);
            appJSON.put("PROFILE_NAME", (Object)name);
            appJSON.put("PLATFORM_TYPE", 3);
            appJSON.put("APP_TITLE", (Object)name);
            appJSON.put("COUNTRY_CODE", (Object)"US");
            final JSONObject packagePolicyJSON = new JSONObject();
            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", true);
            packagePolicyJSON.put("PREVENT_BACKUP", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appJSON.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", isPaid);
            packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
            appJSON.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            appJSON.put("PackagePolicyForm", (Object)packagePolicyJSON);
            appJSON.put("APP_POLICY", (Object)appPolicyJSON);
            appJSON.put("isAppConfigUpdate", (Object)Boolean.FALSE);
            appJSON.put("PROFILE_TYPE", 2);
            appJSON.put("SECURITY_TYPE", -1);
            appJSON.put("APP_CONFIG", true);
            appJSON.put("PROFILE_ID", -1L);
            appJSON.put("COLLECTION_ID", -1L);
            appJSON.put("DESCRIPTION", (Object)description);
            appJSON.put("APP_VERSION", (Object)"0");
            appJSON.put("PACKAGE_TYPE", (int)(isPaid ? 1 : 0));
            appJSON.put("success", true);
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(3).isCurrentPackageNewToAppRepo(packageFamilyName, customerID);
            if (isCurrentPackageNew) {
                appJSON.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
            if (packageFamilyName.equalsIgnoreCase("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                this.processNativeAppData(appJSON, jsonObject.optLong("CustomerID", -1L));
            }
        }
        catch (final Exception e) {
            appJSON.put("success", false);
            this.logger.log(Level.SEVERE, "failed to add package during Business Store Sync ", e);
        }
        return appJSON;
    }
    
    private JSONArray getCompatiblePackages(final JSONObject packages) throws JSONException {
        final JSONArray resultPackages = new JSONArray();
        final JSONArray packageArray = packages.getJSONArray("productPackages");
        final HashMap<String, ArrayList<WindowsAppsObject>> requiredPackages = new HashMap<String, ArrayList<WindowsAppsObject>>();
        for (int i = 0; i < packageArray.length(); ++i) {
            final JSONObject curPackage = packageArray.getJSONObject(i);
            JSONObject platformCompatibility = curPackage.getJSONArray("platforms").getJSONObject(0);
            final String platformType = String.valueOf(platformCompatibility.get("platformName"));
            platformCompatibility = platformCompatibility.getJSONObject("minVersion");
            final String minOsVersion = platformCompatibility.optString("major") + "." + platformCompatibility.optString("minor") + "." + platformCompatibility.optString("build") + "." + platformCompatibility.optString("revision") + ".";
            final JSONObject version = curPackage.getJSONObject("version");
            final String osVersion = version.optString("major") + "." + version.optString("minor") + "." + version.optString("build") + "." + version.optString("revision");
            final JSONArray architecturesArray = curPackage.getJSONArray("architectures");
            final String[] archStringArr = new String[architecturesArray.length()];
            for (int j = 0; j < architecturesArray.length(); ++j) {
                archStringArr[j] = architecturesArray.optString(j);
            }
            ArrayList arrayList = requiredPackages.get(platformType);
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            final WindowsAppsObject appObj = new WindowsAppsObject();
            appObj.setArchitecuture(archStringArr);
            appObj.setPackageJson(curPackage);
            appObj.setPlatFormType(platformType);
            appObj.setVersion(osVersion);
            appObj.setMinVersion(minOsVersion);
            arrayList.add(appObj);
            requiredPackages.put(platformType, arrayList);
        }
        this.setRequiredPackages(requiredPackages, resultPackages);
        return resultPackages;
    }
    
    private void setRequiredPackages(final HashMap<String, ArrayList<WindowsAppsObject>> requiredPackages, final JSONArray resultPackages) throws JSONException {
        ArrayList<WindowsAppsObject> arrayList = null;
        final Boolean[] obtainedmMap = { Boolean.FALSE, Boolean.FALSE, Boolean.FALSE };
        arrayList = requiredPackages.get("Windows.Universal");
        if (arrayList != null && arrayList.size() != 0) {
            Collections.sort(arrayList, Collections.reverseOrder());
            for (final WindowsAppsObject windowsAppsObject : arrayList) {
                final String[] arch = windowsAppsObject.getArchitecuture();
                for (int i = 0; i < arch.length; ++i) {
                    if (arch[i].toLowerCase().equals("arm")) {
                        obtainedmMap[0] = Boolean.TRUE;
                    }
                    else if (arch[i].toLowerCase().equals("x64")) {
                        obtainedmMap[1] = Boolean.TRUE;
                    }
                    else if (arch[i].toLowerCase().equals("x86")) {
                        obtainedmMap[2] = Boolean.TRUE;
                    }
                }
                resultPackages.put((Object)new JSONObject(windowsAppsObject.toString()));
                if (obtainedmMap[0] && obtainedmMap[1] && obtainedmMap[2]) {
                    break;
                }
            }
        }
        else {
            arrayList = requiredPackages.get("Windows.Mobile");
            if (arrayList != null && arrayList.size() != 0) {
                Collections.sort(arrayList, Collections.reverseOrder());
                resultPackages.put((Object)new JSONObject(arrayList.get(0).toString()));
            }
            else {
                arrayList = requiredPackages.get("Windows.WindowsPhone8x");
                if (arrayList != null && arrayList.size() != 0) {
                    Collections.sort(arrayList, Collections.reverseOrder());
                    resultPackages.put((Object)new JSONObject(arrayList.get(0).toString()));
                }
            }
            arrayList = requiredPackages.get("Windows.Desktop");
            if (arrayList != null && arrayList.size() != 0) {
                Collections.sort(arrayList, Collections.reverseOrder());
                resultPackages.put((Object)new JSONObject(arrayList.get(0).toString()));
            }
            else {
                arrayList = requiredPackages.get("Windows.Windows8x");
                if (arrayList != null && arrayList.size() != 0) {
                    Collections.sort(arrayList, Collections.reverseOrder());
                    resultPackages.put((Object)new JSONObject(arrayList.get(0).toString()));
                }
            }
        }
    }
    
    private JSONObject getImageDetails(final JSONObject basicDetails) throws JSONException {
        final JSONObject imageDetails = new JSONObject();
        final JSONArray images = basicDetails.getJSONArray("images");
        int height = 0;
        for (int i = 0; i < images.length(); ++i) {
            final JSONObject image = images.getJSONObject(i);
            if (String.valueOf(image.get("purpose")).toLowerCase().contains("logo")) {
                final int curHeight = Integer.parseInt(String.valueOf(image.get("height")));
                if (curHeight > height) {
                    imageDetails.put("location", (Object)String.valueOf(image.get("location")));
                    imageDetails.put("backgroundColor", (Object)String.valueOf(image.get("backgroundColor")));
                    height = curHeight;
                }
            }
        }
        return imageDetails;
    }
    
    private void processNativeAppData(final JSONObject appJSON, final Long customerID) throws Exception {
        final JSONArray jsonArray = WindowsNativeAppHandler.getInstance().getNativeAppConfigurationJSON();
        appJSON.put("APP_CONFIGURATION", (Object)jsonArray.toString());
        appJSON.put("APP_CONFIG_TEMPLATE", (Object)WpAppSettingsHandler.getInstance().createTemplateForConfigValues(jsonArray, customerID));
        MessageProvider.getInstance().hideMessage("WP_APP_NOT_PURCHASED", customerID);
    }
    
    private void validateAndPopulateApps(final JSONArray appsArray, final JSONArray curAppsArray, final HashSet<String> productIdSet) throws JSONException {
        for (int i = 0; i < curAppsArray.length(); ++i) {
            final JSONObject currentApp = curAppsArray.getJSONObject(i);
            final String distributionPolicy = currentApp.optString("distributionPolicy", "");
            if (distributionPolicy.equalsIgnoreCase("open") || (distributionPolicy.equalsIgnoreCase("restricted") && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncPaidBstoreApps"))) {
                final String productId = this.getProductKey(currentApp);
                if (productId != null && !productIdSet.contains(productId)) {
                    productIdSet.add(productId);
                    appsArray.put((Object)currentApp);
                }
            }
        }
    }
    
    private String getProductKey(final JSONObject inventoryDetail) throws JSONException {
        if (inventoryDetail.has("productKey")) {
            final JSONObject productKey = inventoryDetail.getJSONObject("productKey");
            if (productKey.has("productId")) {
                return productKey.optString("productId", (String)null);
            }
        }
        return null;
    }
}
