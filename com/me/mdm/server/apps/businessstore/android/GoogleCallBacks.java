package com.me.mdm.server.apps.businessstore.android;

import org.json.JSONException;
import com.google.api.services.androidenterprise.model.DevicesListResponse;
import com.me.mdm.server.apps.android.afw.appmgmt.PlaystoreAppDistributionConstants;
import com.google.api.services.androidenterprise.model.Device;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import com.google.api.services.androidenterprise.model.AppVersion;
import com.google.api.services.androidenterprise.model.TrackInfo;
import java.util.HashMap;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONArray;
import java.io.IOException;
import java.util.logging.Level;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.json.GoogleJsonError;
import org.json.JSONObject;
import com.google.api.services.androidenterprise.model.Product;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import java.util.logging.Logger;

public class GoogleCallBacks
{
    public Logger bslogger;
    
    public GoogleCallBacks() {
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public class ProductDetailsCallback extends JsonBatchCallback<Product>
    {
        JSONObject appDetailsJSON;
        
        public ProductDetailsCallback(final JSONObject responseJSON) {
            this.appDetailsJSON = responseJSON;
        }
        
        public void onFailure(final GoogleJsonError googleJsonError, final HttpHeaders httpHeaders) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Error in app details call back {0}", googleJsonError.toPrettyString());
        }
        
        public void onSuccess(final Product product, final HttpHeaders httpHeaders) throws IOException {
            if (product.getProductId() != null) {
                final String productID = product.getProductId().substring(4);
                final JSONObject productDetailsJSON = new JSONObject();
                productDetailsJSON.put("BUNDLE_IDENTIFIER", (Object)productID);
                try {
                    GoogleCallBacks.this.bslogger.log(Level.INFO, "Syncing app {0}", productID);
                    final int packageType = 0;
                    final String iconURL = product.getIconUrl();
                    productDetailsJSON.put("DISPLAY_IMAGE_DOWNLOAD_URL", (Object)iconURL);
                    productDetailsJSON.put("APP_NAME", (Object)product.getTitle());
                    productDetailsJSON.put("APP_TITLE", (Object)product.getTitle());
                    productDetailsJSON.put("IDENTIFIER", (Object)productID);
                    this.fillTrackInfo(product, productDetailsJSON);
                    this.fillVersionDetails(product, productDetailsJSON);
                    if (productDetailsJSON.optJSONArray("productionVersions") == null || productDetailsJSON.optJSONArray("productionVersions").length() == 0) {
                        JSONArray jsonArray = this.appDetailsJSON.optJSONArray("UnpublishedApps");
                        if (jsonArray == null) {
                            jsonArray = new JSONArray();
                        }
                        jsonArray.put((Object)productID);
                        this.appDetailsJSON.put("UnpublishedApps", (Object)jsonArray);
                        GoogleCallBacks.this.bslogger.log(Level.INFO, "App {0} is marked as unpublished", productID);
                        return;
                    }
                    productDetailsJSON.put("APP_CATEGORY_NAME", (Object)(MDMStringUtils.isEmpty(product.getCategory()) ? "Productivity" : product.getCategory()));
                    productDetailsJSON.put("PACKAGE_TYPE", packageType);
                    final JSONObject packageAppDataJSON = new JSONObject();
                    packageAppDataJSON.put("PACKAGE_TYPE", packageType);
                    packageAppDataJSON.put("DISPLAY_IMAGE_DOWNLOAD_URL", (Object)iconURL);
                    packageAppDataJSON.put("STORE_URL", (Object)product.getDetailsUrl());
                    packageAppDataJSON.put("SUPPORTED_DEVICES", (Object)new Integer(1));
                    packageAppDataJSON.put("DESCRIPTION", (Object)product.getDescription());
                    final JSONObject packageAppGroupJSON = new JSONObject();
                    int appAvailability = 0;
                    if (product.getDistributionChannel().toLowerCase().startsWith("private")) {
                        appAvailability = 1;
                    }
                    packageAppGroupJSON.put("IS_PAID_APP", false);
                    packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
                    packageAppGroupJSON.put("PRIVATE_APP_TYPE", appAvailability);
                    productDetailsJSON.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
                    productDetailsJSON.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
                    productDetailsJSON.put("permissions", (Collection)product.getPermissions());
                }
                catch (final Exception exp) {
                    GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Exception when getting app details for app " + productID, exp);
                    productDetailsJSON.put("APP_FAILURE_CASE", true);
                    productDetailsJSON.put("ERROR_CODE", 2004);
                }
                JSONArray jsonArray2 = this.appDetailsJSON.optJSONArray("Apps");
                if (jsonArray2 == null) {
                    jsonArray2 = new JSONArray();
                }
                jsonArray2.put((Object)productDetailsJSON);
                this.appDetailsJSON.put("Apps", (Object)jsonArray2);
            }
            else {
                GoogleCallBacks.this.bslogger.log(Level.SEVERE, "App identifier could not be fetched from Playstore for the app");
            }
        }
        
        private JSONObject fillVersionDetails(final Product product, final JSONObject productDetailsJSON) {
            try {
                final List<AppVersion> appVersions = product.getAppVersion();
                final String productID = productDetailsJSON.getString("BUNDLE_IDENTIFIER");
                final JSONArray productionVersions = new JSONArray();
                final JSONArray testingTrackVersions = new JSONArray();
                final List<TrackInfo> appTracks = product.getAppTracks();
                final Map appTrackMap = new HashMap();
                if (appTracks != null && !appTracks.isEmpty()) {
                    for (final TrackInfo trackInfo : appTracks) {
                        final String trackName = trackInfo.getTrackAlias();
                        final String trackId = trackInfo.getTrackId();
                        appTrackMap.put(trackId, trackName);
                    }
                }
                for (final AppVersion appVersion : appVersions) {
                    if (appVersion.getIsProduction() != null && appVersion.getIsProduction()) {
                        final int versionCode = appVersion.getVersionCode();
                        final String versionString = appVersion.getVersionString();
                        productionVersions.put((Object)new JSONObject().put("APP_VERSION", (Object)versionString).put("APP_NAME_SHORT_VERSION", versionCode));
                        if (!productDetailsJSON.has("APP_VERSION")) {
                            productDetailsJSON.put("APP_VERSION", (Object)versionString);
                            productDetailsJSON.put("APP_NAME_SHORT_VERSION", (Object)String.valueOf(versionCode));
                        }
                    }
                    if (appVersion.getTrackId() != null && !appVersion.getTrackId().isEmpty()) {
                        final int versionCode = appVersion.getVersionCode();
                        final String versionString = appVersion.getVersionString();
                        for (final String trackID : appVersion.getTrackId()) {
                            testingTrackVersions.put((Object)new JSONObject().put("APP_VERSION", (Object)versionString).put("APP_NAME_SHORT_VERSION", (Object)String.valueOf(versionCode)).put("TRACK_ID", (Object)trackID).put("TRACK_NAME", appTrackMap.get(trackID)));
                        }
                    }
                }
                if (productionVersions.length() > 0) {
                    GoogleCallBacks.this.bslogger.log(Level.INFO, "No of production versions available for the app {0} is {1}", new Object[] { productID, productionVersions.length() });
                    productDetailsJSON.put("productionVersions", (Object)productionVersions);
                }
                if (testingTrackVersions.length() > 0) {
                    GoogleCallBacks.this.bslogger.log(Level.INFO, "No of testing track versions available for the app {0} is {1}", new Object[] { productID, testingTrackVersions.length() });
                    productDetailsJSON.put("testingVersions", (Object)testingTrackVersions);
                }
            }
            catch (final Exception ex) {
                GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Exception occurred in fillVersionDetails ", ex);
            }
            return productDetailsJSON;
        }
        
        private void fillTrackInfo(final Product product, final JSONObject productDetailsJSON) {
            final List<TrackInfo> appTracks = product.getAppTracks();
            if (appTracks != null && !appTracks.isEmpty()) {
                GoogleCallBacks.this.bslogger.log(Level.INFO, "No. of testing tracks available for the app {0} is {1}", new Object[] { product.getProductId(), appTracks.size() });
                final JSONArray trackList = new JSONArray();
                for (final TrackInfo trackInfo : appTracks) {
                    final String trackName = trackInfo.getTrackAlias();
                    final String trackId = trackInfo.getTrackId();
                    final JSONObject trackObject = new JSONObject();
                    trackObject.put("TRACK_NAME", (Object)trackName);
                    trackObject.put("TRACK_ID", (Object)trackId);
                    trackList.put((Object)trackObject);
                }
                productDetailsJSON.put("trackDetails", (Object)trackList);
            }
            else {
                GoogleCallBacks.this.bslogger.log(Level.INFO, "No testing tracks available for the app {0}", new Object[] { product.getProductId() });
            }
        }
    }
    
    public class DeviceDetailsCallback extends JsonBatchCallback<Device>
    {
        Map<Long, Device> devices;
        Long resourceID;
        String userId;
        
        public DeviceDetailsCallback(final Map<Long, Device> devices, final Long resourceID, final String userId) {
            this.resourceID = null;
            this.userId = null;
            this.devices = devices;
            this.resourceID = resourceID;
            this.userId = userId;
        }
        
        public void onFailure(final GoogleJsonError googleJsonError, final HttpHeaders httpHeaders) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Error in device details call back {0}: {1}", new Object[] { this.resourceID, googleJsonError.toPrettyString() });
        }
        
        public void onSuccess(final Device device, final HttpHeaders httpHeaders) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.INFO, "Device detail obtained for resource id {0}: {1}", new Object[] { this.resourceID, device.toPrettyString() });
            if (!device.isEmpty()) {
                this.devices.put(this.resourceID, device);
            }
        }
    }
    
    public class DeviceProductPolicyDetailsCallback extends JsonBatchCallback<Device>
    {
        Long resourceID;
        JSONObject responseJSON;
        
        public DeviceProductPolicyDetailsCallback(final Long resourceID, final JSONObject responseJSON) {
            this.resourceID = null;
            this.resourceID = resourceID;
            this.responseJSON = responseJSON;
        }
        
        public void onFailure(final GoogleJsonError googleJsonError, final HttpHeaders httpHeaders) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Issue on device product policy {0}", new Object[] { this.resourceID, googleJsonError.toPrettyString() });
        }
        
        public void onSuccess(final Device device, final HttpHeaders httpHeaders) throws IOException {
            JSONArray successList = this.responseJSON.optJSONArray(PlaystoreAppDistributionConstants.SUCCESS_LIST);
            if (successList == null) {
                successList = new JSONArray();
            }
            successList.put((Object)this.resourceID);
            this.responseJSON.put(PlaystoreAppDistributionConstants.SUCCESS_LIST, (Object)successList);
            GoogleCallBacks.this.bslogger.log(Level.INFO, "Success of device product policy {0}", new Object[] { this.resourceID, device.toPrettyString() });
        }
    }
    
    private class GetUserDevicesCallback extends JsonBatchCallback<DevicesListResponse>
    {
        String userID;
        JSONObject userDeviceObject;
        
        public GetUserDevicesCallback(final String userID, final JSONObject userDeviceObject) throws JSONException {
            this.userID = null;
            this.userID = userID;
            this.userDeviceObject = userDeviceObject;
        }
        
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.INFO, "Failed User Devices {0}", gje.toPrettyString());
        }
        
        public void onSuccess(final DevicesListResponse dlr, final HttpHeaders hh) throws IOException {
            try {
                final JSONArray array = new JSONArray();
                final List<Device> devices = dlr.getDevice();
                if (devices != null) {
                    for (int i = 0; i < devices.size(); ++i) {
                        final Device d = devices.get(i);
                        array.put((Object)d.getAndroidId());
                    }
                    JSONObject jsonObject = this.userDeviceObject.optJSONObject("usersanddevices");
                    if (jsonObject == null) {
                        jsonObject = new JSONObject();
                    }
                    jsonObject.put(this.userID, (Object)array);
                    this.userDeviceObject.put("usersanddevices", (Object)jsonObject);
                }
            }
            catch (final Exception exp) {
                GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Exception in handling device list response {0}", dlr);
            }
        }
    }
    
    public class DeviceAppRemovalStatusCallback extends JsonBatchCallback<Void>
    {
        String userID;
        Long resourceID;
        String identifier;
        
        public DeviceAppRemovalStatusCallback(final Long resourceID, final String userID, final String identifier) {
            this.userID = null;
            this.resourceID = null;
            this.identifier = null;
            this.userID = userID;
            this.resourceID = resourceID;
            this.identifier = identifier;
        }
        
        public void onSuccess(final Void installStatus, final HttpHeaders responseHeaders) {
            GoogleCallBacks.this.bslogger.log(Level.INFO, "uninstall app {0} on device {1} ", new Object[] { this.identifier, this.resourceID });
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) throws IOException {
            GoogleCallBacks.this.bslogger.log(Level.SEVERE, "Cannot uninstall app {0} on device {1} : {2}", new Object[] { this.identifier, this.resourceID, e.toPrettyString() });
        }
    }
}
