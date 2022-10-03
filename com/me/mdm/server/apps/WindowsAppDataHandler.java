package com.me.mdm.server.apps;

import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public class WindowsAppDataHandler extends BaseAppDatahandler
{
    public static final String BSTORE_STORE_SYNC_PARAM = "bstoreFirstSyncPending";
    
    public WindowsAppDataHandler(final JSONObject params) {
        super(params);
        this.platformType = 3;
        this.defaultCategory = "Business";
        this.storeSyncKey = "bstoreFirstSyncPending";
    }
    
    public JSONObject modifyEnterpriseAppData(final JSONObject jsonObject, final JSONObject requestJson) throws Exception {
        final String appFile = jsonObject.optString("APP_FILE_LOC", (String)null);
        final String filename = jsonObject.optString("APP_FILE", appFile);
        if (requestJson.optString("command_line", (String)null) != null) {
            jsonObject.getJSONObject("MdPackageToAppDataFrom").put("COMMAND_LINE", (Object)requestJson.optString("command_line"));
        }
        else if (jsonObject.optString("command_line") != null) {
            jsonObject.getJSONObject("MdPackageToAppDataFrom").put("COMMAND_LINE", (Object)jsonObject.optString("command_line"));
        }
        if (filename == null) {
            throw new APIHTTPException("APP0001", new Object[] { "no file name is provided" });
        }
        if (!filename.toLowerCase().endsWith("msi")) {
            final JSONObject jsonObject2 = new AppFacade().getWindowsToken(this.customerID);
            if (jsonObject2.length() == 0) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.upload.aet.csc.plain", new Object[0]) });
            }
            final String appName = jsonObject.optString("APP_EXTRACT_NAME");
            if (!MDMStringUtils.isEmpty(appName)) {
                jsonObject.put("APP_NAME", (Object)appName);
            }
            if (!requestJson.has("bundle_identifier") && !jsonObject.has("BUNDLE_IDENTIFIER")) {
                throw new APIHTTPException("COM0009", new Object[] { "bundle_identifier" });
            }
            final String identifier = requestJson.optString("bundle_identifier", String.valueOf(jsonObject.get("BUNDLE_IDENTIFIER")));
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)identifier);
            if (this.appProps != null && this.appProps.opt("MIN_OS") != null) {
                final JSONObject packageJSON = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                if (packageJSON != null) {
                    packageJSON.put("MIN_OS", this.appProps.get("MIN_OS"));
                }
            }
            else if (this.appdetails != null) {
                final String minos = this.appdetails.get("MIN_OS");
                if (!MDMStringUtils.isEmpty(minos)) {
                    final JSONObject packageJSON2 = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                    if (packageJSON2 != null) {
                        packageJSON2.put("MIN_OS", this.appdetails.get("MIN_OS"));
                    }
                }
            }
            if (this.appProps != null && this.appProps.opt("SUPPORTED_ARCH") != null) {
                final JSONObject packageJSON = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                if (packageJSON != null) {
                    packageJSON.put("SUPPORTED_ARCH", this.appProps.get("SUPPORTED_ARCH"));
                }
            }
            else if (this.appdetails != null) {
                final JSONObject packageJSON = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                if (packageJSON != null) {
                    packageJSON.put("SUPPORTED_ARCH", this.appdetails.get("SUPPORTED_ARCH"));
                }
            }
            if (requestJson.opt("dependency_ids") != "" && requestJson.opt("dependency_ids") != null && !requestJson.opt("dependency_ids").toString().equals("[]")) {
                final JSONArray jsonArray = new JSONArray(requestJson.optString("dependency_ids"));
                final ArrayList dependencyList = new ArrayList();
                for (int i = 0; i < jsonArray.length(); ++i) {
                    dependencyList.add(jsonArray.get(i));
                }
                jsonObject.put("DependencyIds", (Collection)dependencyList);
            }
            else if (this.appdetails.get("dependency") != null) {
                final JSONArray jsonArray = this.appdetails.get("dependency");
                final ArrayList dependencyList = new ArrayList();
                for (int i = 0; i < jsonArray.length(); ++i) {
                    dependencyList.add(jsonArray.getJSONObject(i).get("dependencyID"));
                }
                jsonObject.put("DependencyIds", (Collection)dependencyList);
            }
            final JSONArray deplist = (JSONArray)this.appProps.opt("dependencies");
            if (deplist != null) {
                jsonObject.put("DependencyList", (Object)deplist);
            }
        }
        else {
            final JSONObject packageJSON3 = jsonObject.getJSONObject("MdPackageToAppDataFrom");
            packageJSON3.put("SUPPORTED_DEVICES", 16);
            packageJSON3.put("MIN_OS", (Object)"10.0");
            jsonObject.put("IS_MODERN_APP", (Object)Boolean.FALSE);
        }
        if (requestJson.optBoolean("displayimagedelete", false)) {
            jsonObject.put("isDisplayImageDelete", true);
        }
        return jsonObject;
    }
    
    @Override
    public JSONObject addStoreApp(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Adding Store Apps in Windows not Available");
    }
    
    @Override
    public JSONObject updateStoreApp(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Adding Store Apps in Windows not Available");
    }
    
    @Override
    public JSONObject addAppConfiguration(final JSONObject jsonObject) throws Exception {
        this.packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
        this.setReleaseLabelId(jsonObject);
        this.validateIfAppAndReleaseLabelFound();
        this.appdetails = MDMAppMgmtHandler.getInstance().getAppDetailsMap(this.packageId, this.releaseLabelId);
        this.validateIfAppInTrash();
        if (this.appdetails.containsKey("APP_FILE_LOC") && this.appdetails.get("APP_FILE_LOC") != null) {
            final String appFile = this.appdetails.get("APP_FILE_LOC").toString();
            if (appFile.contains(".") && !appFile.endsWith(".")) {
                final String fileExtn = appFile.substring(appFile.lastIndexOf(46) + 1);
                if (fileExtn.equalsIgnoreCase("msi")) {
                    throw new APIHTTPException("COM0015", new Object[] { "Configuration not supported for MSI" });
                }
            }
        }
        Long configDataItem = this.appdetails.get("CONFIG_DATA_ITEM_ID");
        if (configDataItem == null) {
            configDataItem = -1L;
        }
        jsonObject.put("CONFIG_DATA_ITEM_ID", (Object)configDataItem);
        final JSONArray appconfig = jsonObject.getJSONObject("msg_body").getJSONArray("app_configuration");
        if (appconfig.length() > 0) {
            final String appconfigString = appconfig.toString();
            jsonObject.put("APP_CONFIGURATION", (Object)appconfigString);
            jsonObject.put("APP_CONFIG_TEMPLATE", (Object)WpAppSettingsHandler.getInstance().createTemplateForConfigValues(appconfig));
        }
        jsonObject.put("PACKAGE_MODIFIED_BY", (Object)this.userID);
        jsonObject.put("PLATFORM_TYPE", this.platformType);
        jsonObject.put("CUSTOMER_ID", (Object)this.customerID);
        jsonObject.put("APP_GROUP_ID", this.appdetails.get("APP_GROUP_ID"));
        jsonObject.put("APP_CONFIG_TEMPLATE_ID", (Object)new AppConfigDataHandler().getAppConfigTemplateIDFromConfigDataItemID(configDataItem));
        jsonObject.put("PUBLISH_PROFILE", (Object)Boolean.TRUE);
        new AppConfigDataHandler().saveAppConfigData(jsonObject, Boolean.TRUE);
        return this.getAppConfiguration(jsonObject);
    }
    
    @Override
    public JSONObject updateAppConfiguration(final JSONObject jsonObject) throws Exception {
        return this.addAppConfiguration(jsonObject);
    }
    
    @Override
    public JSONObject getAppDetailsFromAppFile(final JSONObject jsonObject) throws Exception {
        final JSONObject wrapperJSON = super.getAppDetailsFromAppFile(jsonObject);
        if (wrapperJSON.has("dependencies")) {
            final JSONArray depList = (JSONArray)wrapperJSON.get("dependencies");
            final ArrayList<JSONObject> dependencyList = new ArrayList<JSONObject>();
            if (depList != null) {
                for (int i = 0; i < depList.length(); ++i) {
                    dependencyList.add((JSONObject)depList.get(i));
                }
            }
            if (dependencyList.size() != 0) {
                JSONArray availableDependency = new JSONArray();
                if (wrapperJSON.has("availableDependencies")) {
                    availableDependency = wrapperJSON.getJSONArray("availableDependencies");
                }
                final String[] supportArch = AppsUtil.getInstance().getArchitecture(wrapperJSON.getLong("SUPPORTED_ARCH"));
                final JSONObject dependency = new AppDependencyHandler(3).validateAndReturnDependencyCompatibility(supportArch, dependencyList, availableDependency);
                final JSONArray missingdep = (JSONArray)dependency.get("missingDependencies");
                if (missingdep.length() != 0) {
                    wrapperJSON.put("missingDependencies", (Object)dependency.get("missingDependencies").toString());
                }
                wrapperJSON.put("dependency_ids", (Object)dependency.get("availableDependencies").toString());
                wrapperJSON.put("supportArch", (Object)Arrays.toString(supportArch));
            }
        }
        final String filePath = (String)jsonObject.get("file_path");
        Boolean isMsi = Boolean.FALSE;
        if (filePath.lastIndexOf(".") != filePath.length() - 1 && "msi".equalsIgnoreCase(filePath.substring(filePath.lastIndexOf(".") + 1))) {
            isMsi = Boolean.TRUE;
        }
        wrapperJSON.put("is_msi", (Object)isMsi);
        return wrapperJSON;
    }
    
    @Override
    public boolean allowPackageUpdate(final String fileValue, final String providedValue) {
        return providedValue.contains(fileValue);
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject appJSON = super.getAppDetails(jsonObject);
        appJSON.put("MIN_OS", this.appdetails.get("MIN_OS"));
        if (this.appdetails.containsKey("SMARTPHONE_VERSION")) {
            appJSON.put("smartphone_version", this.appdetails.get("SMARTPHONE_VERSION"));
        }
        if (this.appdetails.containsKey("TABLET_VERSION")) {
            appJSON.put("tablet_version", this.appdetails.get("TABLET_VERSION"));
        }
        if (this.appdetails.containsKey("dependencyList")) {
            final JSONArray availableDependency = this.appdetails.get("dependency");
            final JSONArray depList = this.appdetails.get("dependencyList");
            final ArrayList<JSONObject> dependencyList = new ArrayList<JSONObject>();
            if (depList != null) {
                for (int i = 0; i < depList.length(); ++i) {
                    dependencyList.add((JSONObject)depList.get(i));
                }
            }
            if (dependencyList.size() != 0) {
                appJSON.put("required_dependency", (Collection)dependencyList);
                final String[] supportArch = AppsUtil.getInstance().getArchitecture(this.appdetails.get("SUPPORTED_ARCH"));
                final JSONObject dependency = new AppDependencyHandler(3).validateAndReturnDependencyCompatibility(supportArch, dependencyList, availableDependency);
                final JSONArray missingdep = (JSONArray)dependency.get("missingDependencies");
                if (missingdep.length() != 0) {
                    appJSON.put("missingDependencies", (Object)missingdep);
                }
                if (availableDependency != null) {
                    appJSON.put("availableDependencies", (Object)availableDependency);
                }
                appJSON.put("required_dependency", (Object)missingdep);
            }
        }
        appJSON.put("dependency", this.appdetails.get("dependency"));
        appJSON.put("MIN_OS", this.appdetails.get("MIN_OS"));
        if (this.appdetails.containsKey("APP_FILE_LOC") && this.appdetails.get("APP_FILE_LOC") != null) {
            final String appFile = this.appdetails.get("APP_FILE_LOC").toString();
            if (appFile.contains(".") && !appFile.endsWith(".")) {
                appJSON.put("windows_app_type", (Object)appFile.substring(appFile.lastIndexOf(46) + 1));
            }
            if (appFile.endsWith("msi") && this.appdetails.containsKey("COMMAND_LINE")) {
                appJSON.put("command_line", this.appdetails.get("COMMAND_LINE"));
            }
        }
        if (this.appdetails.containsKey("IMG_BG")) {
            appJSON.put("img_bg", this.appdetails.get("IMG_BG"));
        }
        if (this.appdetails.containsKey("STORE_ID")) {
            appJSON.put("store_id", this.appdetails.get("STORE_ID"));
        }
        if (this.appdetails.containsKey("IS_OFFLINE_APP")) {
            appJSON.put("offline_app", (Object)"IS_OFFLINE_APP");
        }
        return appJSON;
    }
    
    @Override
    protected void validateEnterpriseAppData(final JSONObject requestJSON, final JSONObject packageJSON) throws Exception {
        final String filePath = packageJSON.optString("file_name");
        Boolean isMSI = Boolean.FALSE;
        if (filePath != null && filePath.endsWith(".msi")) {
            isMSI = Boolean.TRUE;
        }
        if (!packageJSON.has("PackageName")) {
            if (!requestJSON.has("app_name") || !requestJSON.has("app_version") || !requestJSON.has("bundle_identifier")) {
                throw new APIHTTPException("COM0009", new Object[] { "Provide app_name, app_version & bundle_identifier" });
            }
            this.appProps.put("PackageName", (Object)String.valueOf(requestJSON.get("bundle_identifier")));
            this.appProps.put("VersionName", (Object)String.valueOf(requestJSON.get("app_version")));
        }
        final String packageID = requestJSON.optString("bundle_identifier", (String)null);
        if (packageID != null && !isMSI) {
            if (!this.allowPackageUpdate((String)this.appProps.get("PackageName"), packageID)) {
                throw new APIHTTPException("APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.bundleidinvalid", new Object[0]) });
            }
            this.appProps.put("PackageName", (Object)packageID);
        }
        if (packageJSON.has("PackageName") && packageID == null && !isMSI) {
            throw new APIHTTPException("COM0009", new Object[] { "Provide app_name, app_version & bundle_identifier" });
        }
    }
    
    @Override
    public JSONObject generateSignUpURL(final JSONObject jsonObject) throws Exception {
        final Long userID = APIUtil.getUserID(jsonObject);
        final Long customerID = APIUtil.getCustomerID(jsonObject);
        final JSONObject response = MDMApiFactoryProvider.getBusinessStoreAccess().getBusinessStoreRedirectURL(customerID, userID);
        String redirectURL = String.valueOf(response.get("redirect_url"));
        final List list = new ArrayList();
        list.add("serurl");
        list.add("state");
        redirectURL = MDMUtil.getInstance().removeParamsFromURL(redirectURL, list);
        response.put("redirect_url", (Object)redirectURL);
        return response;
    }
    
    @Override
    public JSONObject getPrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final int appsNotPurchasedFromPortal = MDMAppMgmtHandler.getInstance().getAppsNotPurchasedFromPortal(3, this.customerID);
        responseJSON.put("no_of_non_portal_apps", appsNotPurchasedFromPortal);
        responseJSON.put("is_windows_store_configure", WpAppSettingsHandler.getInstance().isBstoreConfigured(this.customerID));
        return responseJSON;
    }
}
