package com.me.mdm.server.apps.android;

import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.tracks.AppTrackUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.apps.BaseAppAdditionDataProvider;

public class AndroidAppAdditionDataProvider extends BaseAppAdditionDataProvider
{
    public static Logger logger;
    
    @Override
    public JSONObject modifyAppAdditionData(final JSONObject appAdditionDetails) throws Exception {
        final Boolean pfwApp = appAdditionDetails.has("MDPackageToAppGroupForm") && appAdditionDetails.getJSONObject("MDPackageToAppGroupForm").getBoolean("IS_PURCHASED_FROM_PORTAL");
        if (appAdditionDetails.getBoolean("isNewVersionAppDetected")) {
            if (pfwApp) {
                return this.modifyPfWAppAdditionData(appAdditionDetails);
            }
        }
        else {
            final Long customerId = appAdditionDetails.getLong("CUSTOMER_ID");
            final String forceSync = MDBusinessStoreUtil.getBusinessStoreParamValue("PFW_FORCE_SYNC", MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW));
            if (forceSync != null && Boolean.parseBoolean(forceSync) && pfwApp) {
                return this.modifyPfWAppAdditionData(appAdditionDetails);
            }
        }
        return appAdditionDetails;
    }
    
    private JSONObject modifyPfWAppAdditionData(final JSONObject appAdditionDetails) throws Exception {
        final String bundleId = String.valueOf(appAdditionDetails.get("BUNDLE_IDENTIFIER"));
        final Long customerId = appAdditionDetails.getLong("CUSTOMER_ID");
        final JSONObject afwSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        final GooglePlayEnterpriseBusinessStore pfwStore = new GooglePlayEnterpriseBusinessStore(afwSettings);
        final JSONObject managedConfig = new JSONObject(pfwStore.getManagedAppConfig(bundleId));
        final String formattedConfiguration = this.getFormattedConfigurationSchema(managedConfig);
        appAdditionDetails.put("APP_CONFIG_TEMPLATE", (Object)formattedConfiguration);
        JSONObject permissions;
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
            permissions = new JSONObject().put("permission", (Object)appAdditionDetails.optJSONArray("permissions"));
        }
        else {
            permissions = new JSONObject(pfwStore.getAppPermission(bundleId));
        }
        appAdditionDetails.put("PermissionSchema", (Object)this.getFormattedPermissionSchema(permissions));
        final String imagedownloadURL = appAdditionDetails.optString("DISPLAY_IMAGE_DOWNLOAD_URL");
        final UUID randomid = UUID.randomUUID();
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String iconTempPath = serverHome + File.separator + "temp_downloads" + File.separator + randomid + File.separator + "icon.png";
        DownloadManager.getInstance().downloadFile(imagedownloadURL, iconTempPath, new SSLValidationType[0]);
        appAdditionDetails.put("DISPLAY_IMAGE", (Object)iconTempPath);
        return appAdditionDetails;
    }
    
    private String getFormattedConfigurationSchema(final JSONObject managedConfig) throws JSONException {
        if (managedConfig.getJSONArray("restrictions").length() > 0) {
            final JSONObject appConfigForm = new JSONObject();
            appConfigForm.put("APP_CONFIG_FORM", (Object)managedConfig);
            appConfigForm.put("APP_CONFIG_TEMPLATE_TYPE", (Object)AppConfigDataHandler.APP_CONFIG_TYPE_FROM_BUSINESS_STORE);
            return appConfigForm.toString();
        }
        return null;
    }
    
    private JSONArray getFormattedPermissionSchema(final JSONObject permissions) throws JSONException {
        final JSONArray permissionsArray = permissions.optJSONArray("permission");
        if (permissionsArray != null) {
            final JSONArray permissionSchema = new JSONArray();
            for (int i = 0; i < permissionsArray.length(); ++i) {
                permissionSchema.put(permissionsArray.getJSONObject(i).get("permissionId"));
            }
            return permissionSchema;
        }
        return null;
    }
    
    @Override
    public Long getReleaseLabel(final JSONObject appObject) throws DataAccessException {
        final Boolean pfwApp = appObject.has("MDPackageToAppGroupForm") && appObject.getJSONObject("MDPackageToAppGroupForm").getBoolean("IS_PURCHASED_FROM_PORTAL");
        Long appReleaseLabelID = appObject.optLong("RELEASE_LABEL_ID", -1L);
        if (!pfwApp) {
            if (appReleaseLabelID.equals(-1L)) {
                appReleaseLabelID = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer((Long)appObject.get("CUSTOMER_ID"));
            }
        }
        else if (appObject.optBoolean("isProduction")) {
            appReleaseLabelID = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer((Long)appObject.get("CUSTOMER_ID"));
        }
        else {
            final String trackId = (String)appObject.get("TRACK_ID");
            final String trackName = (String)appObject.get("TRACK_NAME");
            final Long appGroupId = appObject.optLong("APP_GROUP_ID");
            final Long customerId = appObject.getLong("CUSTOMER_ID");
            appReleaseLabelID = new AppTrackUtil().getChannelForAppTrack(appGroupId, customerId, trackId, trackName);
            appObject.put("RELEASE_LABEL_DISPLAY_NAME", (Object)trackName);
            final JSONObject appTrackReleaseLabel = new JSONObject();
            appTrackReleaseLabel.put("TRACK_ID", (Object)trackId);
            appTrackReleaseLabel.put("RELEASE_LABEL_ID", (Object)appReleaseLabelID);
            appTrackReleaseLabel.put("APP_GROUP_ID", (Object)appGroupId);
            appTrackReleaseLabel.put("CUSTOMER_ID", (Object)customerId);
            appTrackReleaseLabel.put("RELEASE_LABEL_DISPLAY_NAME", (Object)trackName);
            appTrackReleaseLabel.put("PROFILE_NAME", appObject.get("PROFILE_NAME"));
            appTrackReleaseLabel.put("APP_VERSION", (Object)appObject.optString("APP_VERSION", "--"));
            new AppTrackUtil().updateReleaseLabelToAppTrack(appTrackReleaseLabel);
        }
        return appReleaseLabelID;
    }
    
    static {
        AndroidAppAdditionDataProvider.logger = Logger.getLogger("MDMConfigLogger");
    }
}
