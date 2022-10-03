package com.me.mdm.server.apps.android.afw;

import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.google.api.services.androidenterprise.AndroidEnterprise;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.google.api.services.androidenterprise.model.Device;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.android.GoogleCallBacks;

public class AdvGooglePlayEnterpriseBusinessStore extends GooglePlayEnterpriseBusinessStore
{
    private GoogleCallBacks callbacks;
    
    public AdvGooglePlayEnterpriseBusinessStore(final Long businessStoreId, final Long customerId) {
        super(businessStoreId, customerId);
        this.customerID = customerId;
        this.platformType = 2;
        this.callbacks = new GoogleCallBacks();
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject appObject) throws Exception {
        final JSONObject appDetails = new JSONObject();
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        final JSONArray inputIdentifiers = appObject.optJSONArray("identifiers");
        List identifiers;
        if (inputIdentifiers != null) {
            identifiers = JSONUtil.convertJSONArrayToList(inputIdentifiers);
        }
        else {
            identifiers = AppsUtil.getInstance().getPortalApprovedAppIdentifiers(this.customerID, this.platformType, this.businessStoreID);
        }
        if (identifiers != null && !identifiers.isEmpty()) {
            final Iterator iterator = identifiers.iterator();
            while (iterator.hasNext()) {
                final String productId = "app:".concat(iterator.next());
                this.getAndroidEnterprise().products().get(this.enterpriseID, productId).queue(batchRequest, (JsonBatchCallback)this.callbacks.new ProductDetailsCallback(appDetails));
            }
            batchRequest.execute();
        }
        return appDetails;
    }
    
    @Override
    public JSONObject installAppsToUsers(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public JSONObject installAppsToDevices(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    public JSONObject updateDevicePolicy(final Map<Long, Device> resToDeviceMap, final JSONArray ebsDeviceDetails) throws Exception {
        return this.updateDevicePolicy(resToDeviceMap, ebsDeviceDetails, "policy");
    }
    
    public JSONObject updateDevicePolicy(final Map<Long, Device> resToDeviceMap, final JSONArray ebsDeviceDetails, final String updateMask) throws Exception {
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        final JSONObject responseJSON = new JSONObject();
        for (int i = 0; i < ebsDeviceDetails.length(); ++i) {
            final JSONObject ebsDevice = (JSONObject)ebsDeviceDetails.get(i);
            final String ebsUserId = (String)ebsDevice.get("BS_STORE_ID");
            final String ebsDeviceId = (String)ebsDevice.get("GOOGLE_PLAY_SERVICE_ID");
            final Long resId = ebsDevice.getLong("MANAGED_DEVICE_ID");
            final Device device = resToDeviceMap.get(resId);
            final AndroidEnterprise.Devices.Update deviceUpdateObject = this.getAndroidEnterprise().devices().update(this.enterpriseID, ebsUserId, ebsDeviceId, device);
            if (!MDMStringUtils.isEmpty(updateMask)) {
                deviceUpdateObject.setUpdateMask(updateMask);
            }
            deviceUpdateObject.queue(batchRequest, (JsonBatchCallback)this.callbacks.new DeviceProductPolicyDetailsCallback(resId, responseJSON));
        }
        batchRequest.execute();
        return responseJSON;
    }
    
    @Override
    public JSONObject assignAppsToUsers(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public JSONObject getAppsAssignedForUser(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public JSONObject getAppsAssignedForDevice() throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public JSONObject removeAppsToUsers(final JSONObject jsonObject, final JSONArray responseAray) throws Exception {
        throw new UnsupportedOperationException("Unsupported Operation");
    }
    
    @Override
    public JSONObject removeAppsToDevices(final JSONObject appRemovalObject) throws Exception {
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        final JSONArray ebsDeviceDetails = appRemovalObject.optJSONArray("deviceDetails");
        final JSONArray appList = appRemovalObject.optJSONArray("appList");
        for (int i = 0; i < ebsDeviceDetails.length(); ++i) {
            final JSONObject ebsDevice = (JSONObject)ebsDeviceDetails.get(i);
            final String ebsUserId = (String)ebsDevice.get("BS_STORE_ID");
            final String ebsDeviceId = (String)ebsDevice.get("GOOGLE_PLAY_SERVICE_ID");
            final Long resId = ebsDevice.getLong("MANAGED_DEVICE_ID");
            for (int j = 0; j < appList.length(); ++j) {
                final String productID = (String)appList.get(j);
                this.getAndroidEnterprise().installs().delete(this.enterpriseID, ebsUserId, ebsDeviceId, productID).queue(batchRequest, (JsonBatchCallback)this.callbacks.new DeviceAppRemovalStatusCallback(resId, ebsUserId, productID));
            }
        }
        batchRequest.execute();
        return new JSONObject();
    }
    
    @Override
    public JSONObject processAppData(final JSONObject appDetailsJSON) throws Exception {
        JSONObject appAdditionDetails = new JSONObject();
        final String bundleID = (String)appDetailsJSON.opt("BUNDLE_IDENTIFIER");
        final Long businessStoreID = appDetailsJSON.optLong("BUSINESSSTORE_ID");
        final Long storeAssetId = MDBusinessStoreAssetUtil.addMdBusinessStoreToAssetRel(businessStoreID, bundleID);
        final Long customerId = appDetailsJSON.optLong("CustomerID");
        appAdditionDetails.put("STORE_ASSET_ID", (Object)storeAssetId);
        appAdditionDetails.put("CustomerID", (Object)customerId);
        try {
            appAdditionDetails = this.processSpecificAppFailureCase(appDetailsJSON, appAdditionDetails);
            if (!appAdditionDetails.optBoolean("success")) {
                return appAdditionDetails;
            }
            appAdditionDetails.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appAdditionDetails.put("BUNDLE_IDENTIFIER", (Object)bundleID);
            appAdditionDetails.put("APP_NAME", appDetailsJSON.get("APP_NAME"));
            String appDescription = appDetailsJSON.getJSONObject("MdPackageToAppDataFrom").optString("DESCRIPTION", "");
            appDescription = MDMStringUtils.trimStringLength(appDescription, 2996, "...");
            appAdditionDetails.put("PROFILE_DESCRIPTION", (Object)appDescription);
            appAdditionDetails.put("APP_CATEGORY_NAME", appDetailsJSON.get("APP_CATEGORY_NAME"));
            appAdditionDetails.put("DISPLAY_IMAGE_DOWNLOAD_URL", appDetailsJSON.get("DISPLAY_IMAGE_DOWNLOAD_URL"));
            appAdditionDetails.put("PROFILE_NAME", appDetailsJSON.get("APP_NAME"));
            appAdditionDetails.put("PLATFORM_TYPE", 2);
            appAdditionDetails.put("APP_TITLE", appDetailsJSON.get("APP_TITLE"));
            appAdditionDetails.put("PROFILE_TYPE", 2);
            appAdditionDetails.put("SECURITY_TYPE", -1);
            appAdditionDetails.put("APP_CONFIG", true);
            appAdditionDetails.put("PROFILE_ID", -1L);
            appAdditionDetails.put("COLLECTION_ID", -1L);
            appAdditionDetails.put("DESCRIPTION", (Object)appDescription);
            appAdditionDetails.put("APP_GROUP_ID", (Object)AppsUtil.getInstance().getAppGroupIdFromMDPackage(bundleID, 2, this.customerID));
            final int isPrivate = appDetailsJSON.getJSONObject("MDPackageToAppGroupForm").optInt("PRIVATE_APP_TYPE", 0);
            appAdditionDetails.put("PACKAGE_TYPE", 0);
            final JSONObject MdPackageToAppDataForm = appDetailsJSON.getJSONObject("MdPackageToAppDataFrom");
            MdPackageToAppDataForm.put("DISPLAY_IMAGE_DOWNLOAD_URL", appDetailsJSON.get("DISPLAY_IMAGE_DOWNLOAD_URL"));
            appAdditionDetails.put("MdPackageToAppDataForm", (Object)MdPackageToAppDataForm);
            appAdditionDetails.put("MdPackageToAppDataFrom", (Object)MdPackageToAppDataForm);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            appAdditionDetails.put("APP_POLICY", (Object)appPolicyJSON);
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", false);
            packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
            packageAppGroupJSON.put("PRIVATE_APP_TYPE", isPrivate);
            appAdditionDetails.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            appAdditionDetails.put("PackagePolicyForm", (Object)new JSONObject());
            appAdditionDetails.put("success", true);
            appAdditionDetails.put("PACKAGE_ADDED_BY", (Object)appDetailsJSON.get("USER_ID"));
            appAdditionDetails.put("doNotRestore", (Object)Boolean.TRUE);
            appAdditionDetails.put("CustomerID", this.customerID);
            appAdditionDetails.put("BUSINESSSTORE_ID", (Object)businessStoreID);
            appAdditionDetails.put("CUSTOMER_ID", this.customerID);
            appAdditionDetails.put("permissions", appDetailsJSON.get("permissions"));
        }
        catch (final Exception e) {
            appAdditionDetails.put("success", false);
            appAdditionDetails.put("ERROR_CODE", 2002);
            this.bslogger.log(Level.SEVERE, "failed to add package during PlayStore Sync ", e);
        }
        return appAdditionDetails;
    }
    
    public Map<Long, Device> getDeviceDetails(final JSONArray ebsDeviceDetails) throws Exception {
        final Map<Long, Device> resToDevicePolicy = new HashMap<Long, Device>();
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        for (int i = 0; i < ebsDeviceDetails.length(); ++i) {
            final JSONObject ebsDevice = (JSONObject)ebsDeviceDetails.get(i);
            final String ebsUserId = (String)ebsDevice.get("BS_STORE_ID");
            final String ebsDeviceId = (String)ebsDevice.get("GOOGLE_PLAY_SERVICE_ID");
            final Long resId = ebsDevice.getLong("MANAGED_DEVICE_ID");
            this.getAndroidEnterprise().devices().get(this.enterpriseID, ebsUserId, ebsDeviceId).queue(batchRequest, (JsonBatchCallback)new GoogleCallBacks.DeviceDetailsCallback(resToDevicePolicy, resId, ebsUserId));
        }
        batchRequest.execute();
        return resToDevicePolicy;
    }
    
    @Override
    public boolean isAccountActive() throws Exception {
        this.getAndroidEnterprise().enterprises().get(this.enterpriseID).execute();
        return true;
    }
}
