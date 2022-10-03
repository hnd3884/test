package com.me.mdm.server.apps;

import org.json.JSONArray;
import org.json.JSONObject;

public interface AppDataHandlerInterface
{
    JSONObject addEnterpriseApp(final JSONObject p0) throws Exception;
    
    JSONObject addStoreApp(final JSONObject p0) throws Exception;
    
    JSONObject updateEnterpriseApp(final JSONObject p0) throws Exception;
    
    JSONObject updateStoreApp(final JSONObject p0) throws Exception;
    
    JSONObject addAppConfiguration(final JSONObject p0) throws Exception;
    
    JSONObject updateAppConfiguration(final JSONObject p0) throws Exception;
    
    JSONObject deleteAppConfiguration(final JSONObject p0) throws Exception;
    
    JSONObject getAppConfiguration(final JSONObject p0) throws Exception;
    
    JSONObject getAppDetailsFromAppFile(final JSONObject p0) throws Exception;
    
    JSONObject getAppDetails(final JSONObject p0) throws Exception;
    
    JSONObject modifyAppPermission(final JSONObject p0) throws Exception;
    
    JSONObject getAppPermission(final JSONObject p0) throws Exception;
    
    Object getCategoryCode(final JSONObject p0) throws Exception;
    
    JSONObject getAppSuggestion(final JSONObject p0) throws Exception;
    
    Object getCountryCode(final JSONObject p0) throws Exception;
    
    JSONObject getAppPermissionList(final JSONObject p0) throws Exception;
    
    JSONObject updateAppsForAllDevices(final JSONObject p0) throws Exception;
    
    JSONObject generateSignUpURL(final JSONObject p0) throws Exception;
    
    JSONObject addProvProfileForApp(final JSONObject p0) throws Exception;
    
    JSONObject getProvProfileDetailsFromAppId(final JSONObject p0) throws Exception;
    
    JSONObject getProvProfileDetails(final JSONObject p0) throws Exception;
    
    JSONObject getPrerequsiteForAddApp(final JSONObject p0) throws Exception;
    
    void updatePrerequsiteForAddApp(final JSONObject p0) throws Exception;
    
    @Deprecated
    JSONObject markAppAsStable(final JSONObject p0) throws Exception;
    
    @Deprecated
    JSONObject getChannelsToMerge(final JSONObject p0) throws Exception;
    
    JSONObject getAvailableChannels(final JSONObject p0) throws Exception;
    
    void approveAppVersion(final JSONObject p0) throws Exception;
    
    JSONObject verifyAccountRemoval() throws Exception;
    
    boolean allowPackageUpdate(final String p0, final String p1);
    
    JSONObject getAutoAppUpdateConfig(final JSONObject p0) throws Exception;
    
    JSONObject updateAutoAppUpdateConfig(final JSONObject p0) throws Exception;
    
    JSONObject deleteAutoAppUpdateConfig(final JSONObject p0) throws Exception;
    
    JSONObject addAutoAppUpdateConfig(final JSONObject p0) throws Exception;
    
    JSONObject getAutoAppUpdateInfoForApp(final JSONObject p0) throws Exception;
    
    JSONObject getAutoAppUpdateConfigList() throws Exception;
    
    void deleteSpecificAppVersion(final JSONObject p0) throws Exception;
    
    void updateStoreSyncKey() throws Exception;
    
    JSONArray approveStoreApps(final JSONObject p0) throws Exception;
}
