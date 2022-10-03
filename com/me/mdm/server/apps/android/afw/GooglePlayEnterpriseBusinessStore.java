package com.me.mdm.server.apps.android.afw;

import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.DevicesListResponse;
import com.adventnet.i18n.I18N;
import com.google.api.services.androidenterprise.model.AppVersion;
import com.google.api.services.androidenterprise.model.Product;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.me.mdm.server.util.MDMCustomerParamsHandler;
import com.google.api.services.androidenterprise.model.TrackInfo;
import com.google.api.services.androidenterprise.model.AdministratorWebToken;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecZeroTouch;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecWebApps;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecStoreBuilder;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecPrivateApps;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpecPlaySearch;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpec;
import com.google.api.services.androidenterprise.model.ApprovalUrlInfo;
import com.google.api.services.androidenterprise.model.ProductsApproveRequest;
import com.google.api.services.androidenterprise.model.ProductsGenerateApprovalUrlResponse;
import com.google.api.services.androidenterprise.model.StoreLayoutClustersListResponse;
import com.google.api.services.androidenterprise.model.StoreLayoutPagesListResponse;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.services.androidenterprise.model.StoreLayout;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.google.api.services.androidenterprise.model.LocalizedText;
import com.google.api.services.androidenterprise.model.StorePage;
import com.google.api.services.androidenterprise.model.GroupLicenseUsersListResponse;
import com.google.api.services.androidenterprise.model.ProductPermissions;
import com.google.api.services.androidenterprise.model.AppRestrictionsSchema;
import com.google.api.services.androidenterprise.model.EntitlementsListResponse;
import org.json.JSONException;
import java.io.IOException;
import com.google.api.services.androidenterprise.model.DeviceState;
import java.util.ArrayList;
import com.google.api.services.androidenterprise.model.ProductSet;
import com.google.api.services.androidenterprise.model.Entitlement;
import com.google.api.services.androidenterprise.model.Install;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.apps.businessstore.android.AndroidSyncAppsHandler;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.services.androidenterprise.model.GroupLicense;
import java.util.logging.Level;
import com.google.api.services.androidenterprise.model.GroupLicensesListResponse;
import org.json.JSONArray;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.json.JSONObject;
import com.google.api.services.androidenterprise.AndroidEnterprise;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.batch.BatchRequest;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseEnterpriseBusinessStore;

public class GooglePlayEnterpriseBusinessStore extends BaseEnterpriseBusinessStore
{
    protected static final String APPLICATION_NAME = "ManageEngine EMM/1.0";
    protected long customerID;
    protected String enterpriseID;
    private Logger logger;
    public Logger bslogger;
    public static final Integer MAX_RESOURCES_PER_BATCH;
    protected BatchRequest batchRequest;
    protected int batchCount;
    private static final JsonFactory JSON_FACTORY;
    HttpTransport http_Transport;
    private AndroidEnterprise androidEnterprise;
    private JSONObject responseJSON;
    JSONObject failedResources;
    
    public GooglePlayEnterpriseBusinessStore(final JSONObject jsonObject) throws Exception {
        this.customerID = -1L;
        this.enterpriseID = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
        this.batchRequest = null;
        this.batchCount = 0;
        this.http_Transport = null;
        this.androidEnterprise = null;
        this.responseJSON = null;
        this.failedResources = null;
        this.initialize(jsonObject);
    }
    
    @Deprecated
    public GooglePlayEnterpriseBusinessStore() {
        this.customerID = -1L;
        this.enterpriseID = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
        this.batchRequest = null;
        this.batchCount = 0;
        this.http_Transport = null;
        this.androidEnterprise = null;
        this.responseJSON = null;
        this.failedResources = null;
    }
    
    public Long getCustomerId() {
        return this.customerID;
    }
    
    public GooglePlayEnterpriseBusinessStore(final Long businessStoreId, final Long customerId) {
        super(businessStoreId, customerId);
        this.customerID = -1L;
        this.enterpriseID = null;
        this.logger = Logger.getLogger("MDMLogger");
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
        this.batchRequest = null;
        this.batchCount = 0;
        this.http_Transport = null;
        this.androidEnterprise = null;
        this.responseJSON = null;
        this.failedResources = null;
        this.platformType = 2;
    }
    
    protected GoogleCredential initialize(final JSONObject jsonObject) throws Exception {
        if (jsonObject.has("isConfigured") && !jsonObject.getBoolean("isConfigured")) {
            throw new RuntimeException("Initializing GooglePlayEnterpriseBusinessStore without configuring AfW");
        }
        this.customerID = jsonObject.getLong("CUSTOMER_ID");
        this.http_Transport = GoogleAPINetworkManager.getGoogleAPINetworkManager().getHttpTransportWithProxyConfigured();
        this.enterpriseID = jsonObject.optString("ENTERPRISE_ID");
        final GoogleCredential credential = this.getGoogleCredential(jsonObject);
        this.setAndroidEnterprise(new AndroidEnterprise.Builder(this.http_Transport, GooglePlayEnterpriseBusinessStore.JSON_FACTORY, (HttpRequestInitializer)credential).setApplicationName("ManageEngine EMM/1.0").build());
        return credential;
    }
    
    protected GoogleCredential getGoogleCredential(final JSONObject jsonObject) throws Exception {
        final String accessToken = jsonObject.optString("accessToken");
        final String credentialFilePath = String.valueOf(jsonObject.get("ESA_CREDENTIAL_JSON_PATH"));
        final InputStream inStream = ApiFactoryProvider.getFileAccessAPI().readFile(credentialFilePath);
        final GoogleCredential credential = GoogleCredential.fromStream(inStream, this.http_Transport, GooglePlayEnterpriseBusinessStore.JSON_FACTORY).createScoped((Collection)Collections.singleton("https://www.googleapis.com/auth/androidenterprise"));
        if (accessToken != null && !accessToken.isEmpty()) {
            credential.setAccessToken(accessToken);
        }
        return credential;
    }
    
    @Override
    public JSONObject getAppDetails(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        this.getResponseJSON().put("Apps", (Object)new JSONArray());
        final GroupLicensesListResponse groupLicensesListResponse = (GroupLicensesListResponse)this.getAndroidEnterprise().grouplicenses().list(this.enterpriseID).execute();
        final List<GroupLicense> groupLicenseList = groupLicensesListResponse.getGroupLicense();
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        if (groupLicenseList == null || groupLicenseList.isEmpty()) {
            this.bslogger.log(Level.INFO, "No group licenses found.");
        }
        else {
            for (final GroupLicense gl : groupLicenseList) {
                try {
                    this.getAndroidEnterprise().products().get(this.enterpriseID, gl.getProductId()).queue(batchRequest, (JsonBatchCallback)new AppDetailsCallback(gl));
                }
                catch (final Exception ex) {
                    this.bslogger.log(Level.SEVERE, "Exception when fetching app details for app " + gl.toPrettyString() + "\n", ex);
                }
            }
            batchRequest.execute();
        }
        if (this.responseJSON.has("UnpublishedApps")) {
            final JSONArray unPublishedApps = this.responseJSON.optJSONArray("UnpublishedApps");
            new AndroidSyncAppsHandler().handleUnpublishedApps(unPublishedApps, this.customerID);
            this.responseJSON.remove("UnpublishedApps");
        }
        return this.getResponseJSON();
    }
    
    @Override
    public JSONObject installAppsToDevices(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray apps = jsonObject.getJSONArray("apps");
        final int totalappstobeinstalled = apps.length();
        BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        int batchCount = 1;
        for (int i = 0; i < totalappstobeinstalled; ++i) {
            final JSONObject appDetailsJSON = apps.getJSONObject(i);
            final Long appID = appDetailsJSON.getLong("APP_ID");
            String productID = String.valueOf(appDetailsJSON.get("IDENTIFIER"));
            productID = "app:" + productID;
            final JSONArray usersanddevices = appDetailsJSON.getJSONArray("Users");
            for (int totaldevices = usersanddevices.length(), j = 0; j < totaldevices; ++j) {
                final JSONObject devicesJSON = usersanddevices.getJSONObject(j);
                final Long resouceID = devicesJSON.getLong("MANAGED_DEVICE_ID");
                final String userID = String.valueOf(devicesJSON.get("BS_STORE_ID"));
                final String deviceID = String.valueOf(devicesJSON.get("GOOGLE_PLAY_SERVICE_ID"));
                this.getAndroidEnterprise().installs().update(this.enterpriseID, userID, deviceID, productID, (Install)null).queue(batchRequest, (JsonBatchCallback)new AppInstallStatusCallback(resouceID, userID, appID));
                if (batchRequest.size() == 1000) {
                    this.logger.log(Level.INFO, "installAppsToDevices by batch of 1000-Batch {0}", batchCount);
                    batchRequest.execute();
                    batchRequest = this.getAndroidEnterprise().batch();
                    Thread.sleep(3000L);
                    ++batchCount;
                }
            }
        }
        final int totalResources = (batchCount - 1) * GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH + batchRequest.size();
        if (totalResources > GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            this.logger.log(Level.INFO, "Total resources in installAppsToDevices : {0}", totalResources);
        }
        if (batchRequest.size() > 0) {
            this.logger.log(Level.INFO, "installAppsToDevices from rest of the batch {0}-Batch {1}", new Object[] { batchRequest.size(), batchCount });
            batchRequest.execute();
        }
        return this.getResponseJSON();
    }
    
    @Override
    public JSONObject assignAppsToUsers(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray apps = jsonObject.getJSONArray("apps");
        final int totalappstobeinstalled = apps.length();
        BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        int batchCount = 1;
        for (int i = 0; i < totalappstobeinstalled; ++i) {
            final JSONObject appDetailsJSON = apps.getJSONObject(i);
            String productID = String.valueOf(appDetailsJSON.get("IDENTIFIER"));
            final Long appID = appDetailsJSON.getLong("APP_ID");
            productID = "app:" + productID;
            final Entitlement product = new Entitlement();
            product.setProductId(productID);
            final JSONArray users = appDetailsJSON.getJSONArray("Users");
            for (int totalusers = users.length(), j = 0; j < totalusers; ++j) {
                final String userID = String.valueOf(users.getJSONObject(j).get("BS_STORE_ID"));
                final Long resouceID = users.getJSONObject(j).getLong("MANAGED_DEVICE_ID");
                this.getAndroidEnterprise().entitlements().update(this.enterpriseID, userID, productID, product).queue(batchRequest, (JsonBatchCallback)new AppAssignStatusCallback(resouceID, userID, appID));
                if (batchRequest.size() == 1000) {
                    this.logger.log(Level.INFO, "AppAssign in a batch of 1000-Batch {0}", batchCount);
                    batchRequest.execute();
                    batchRequest = this.getAndroidEnterprise().batch();
                    Thread.sleep(3000L);
                    ++batchCount;
                }
            }
        }
        final int totalResources = (batchCount - 1) * GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH + batchRequest.size();
        if (totalResources > GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            this.logger.log(Level.INFO, "Total resources in AppAssign : {0}", totalResources);
        }
        if (batchRequest.size() > 0) {
            this.logger.log(Level.INFO, "AppAssign from rest of the batch {0}-Batch {1}", new Object[] { batchRequest.size(), batchCount });
            batchRequest.execute();
        }
        return this.getResponseJSON();
    }
    
    public JSONObject updateAvailableProductSet(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray users = jsonObject.getJSONArray("Users");
        BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        int batchCount = 1;
        for (int i = 0; i < users.length(); ++i) {
            final JSONObject userJSON = users.getJSONObject(i);
            final JSONArray appArr = userJSON.getJSONArray("apps");
            final String userId = String.valueOf(userJSON.get("userId"));
            final ProductSet productSet = new ProductSet();
            final ArrayList<String> arrayList = new ArrayList<String>();
            for (int j = 0; j < appArr.length(); ++j) {
                arrayList.add("app:" + appArr.get(j));
            }
            productSet.setProductId((List)arrayList);
            this.getAndroidEnterprise().users().setAvailableProductSet(this.enterpriseID, userId, productSet).queue(batchRequest, (JsonBatchCallback)new ProductSetStatusCallback(userId));
            if (batchRequest.size() == 1000) {
                this.logger.log(Level.INFO, "updateAvailableProductSet by batch of 1000-Batch {0}", batchCount);
                batchRequest.execute();
                batchRequest = this.getAndroidEnterprise().batch();
                Thread.sleep(3000L);
                ++batchCount;
            }
        }
        final int totalResources = (batchCount - 1) * GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH + batchRequest.size();
        if (totalResources > GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            this.logger.log(Level.INFO, "Total resources in updateAvailableProductSet : {0}", totalResources);
        }
        if (batchRequest.size() > 0) {
            this.logger.log(Level.INFO, "updateAvailableProductSet from rest of the batch {0}-Batch {1}", new Object[] { batchRequest.size(), batchCount });
            batchRequest.execute();
        }
        return this.getResponseJSON();
    }
    
    public DeviceState setStateEnabled(final String userId, final String deviceId) throws IOException, JSONException, Exception {
        final DeviceState deviceState = new DeviceState();
        deviceState.setAccountState("enabled");
        return (DeviceState)this.getAndroidEnterprise().devices().setState(this.enterpriseID, userId, deviceId, deviceState).execute();
    }
    
    public DeviceState setStateDisabled(final String userId, final String deviceId) throws IOException, JSONException, Exception {
        final DeviceState deviceState = new DeviceState();
        deviceState.setAccountState("disabled");
        return (DeviceState)this.getAndroidEnterprise().devices().setState(this.enterpriseID, userId, deviceId, deviceState).execute();
    }
    
    @Override
    public JSONObject getAppsAssignedForUser(final JSONObject jsonObject) throws Exception {
        final String userID = String.valueOf(jsonObject.get("userID"));
        final EntitlementsListResponse entitlementListResponse = (EntitlementsListResponse)this.getAndroidEnterprise().entitlements().list(this.enterpriseID, userID).execute();
        final List<Entitlement> entitlements = entitlementListResponse.getEntitlement();
        if (entitlements == null || entitlements.isEmpty()) {
            this.logger.log(Level.INFO, "No Entitlement found for user.");
        }
        else {
            this.logger.log(Level.INFO, "Entitlements :");
            for (final Entitlement entitlement : entitlements) {
                this.logger.log(Level.INFO, entitlement.toPrettyString());
            }
        }
        return null;
    }
    
    @Deprecated
    @Override
    public JSONObject getCredential(final JSONObject jsonObject) throws Exception {
        final GoogleCredential credential = this.initialize(jsonObject);
        credential.refreshToken();
        jsonObject.put("accessToken", (Object)credential.getAccessToken());
        jsonObject.put("expiryTime", (Object)credential.getExpirationTimeMilliseconds());
        jsonObject.put("CUSTOMER_ID", jsonObject.getLong("CUSTOMER_ID"));
        return jsonObject;
    }
    
    @Override
    public JSONObject inviteUsers(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject getUsers(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Users cannot be fetched with Play API. Use Directory API.");
    }
    
    @Override
    public JSONObject installAppsToUsers(final JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject getAppsAssignedForDevice() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject getDevices(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray users = jsonObject.getJSONArray("users");
        final int totalappstobeinstalled = users.length();
        final BatchRequest batchRequest = this.getAndroidEnterprise().batch();
        for (int i = 0; i < totalappstobeinstalled; ++i) {
            final String userID = String.valueOf(users.get(i));
            this.getAndroidEnterprise().devices().list(this.enterpriseID, userID).queue(batchRequest, (JsonBatchCallback)new GetUserDevicesCallback(userID));
        }
        if (batchRequest.size() > 0) {
            batchRequest.execute();
        }
        return this.getResponseJSON();
    }
    
    @Override
    public JSONObject removeAppsToUsers(final JSONObject jsonObject, final JSONArray removeResponseJSON) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray apps = jsonObject.getJSONArray("apps");
        for (int totalappstobeinstalled = apps.length(), i = 0; i < totalappstobeinstalled; ++i) {
            final JSONObject appDetailsJSON = apps.getJSONObject(i);
            final Long appID = appDetailsJSON.getLong("APP_ID");
            String productID = String.valueOf(appDetailsJSON.get("IDENTIFIER"));
            productID = "app:" + productID;
            final JSONArray users = appDetailsJSON.getJSONArray("Users");
            for (int totalusers = users.length(), j = 0; j < totalusers; ++j) {
                final String userID = String.valueOf(users.getJSONObject(j).get("BS_STORE_ID"));
                final Long resouceID = users.getJSONObject(j).getLong("MANAGED_DEVICE_ID");
                this.getAndroidEnterprise().entitlements().delete(this.enterpriseID, userID, productID).queue(this.batchRequest, (JsonBatchCallback)new DeviceAppRemovalStatusCallback(resouceID, userID, appID));
                this.executeBatchOnThreshold("Executing removeAppsToUsers in a batch of 1000-Batch");
            }
        }
        final int totalResources = (this.batchCount - 1) * GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH + this.batchRequest.size();
        if (totalResources > GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            this.logger.log(Level.INFO, "Total resources in removeAppsToUsers : {0}", totalResources);
        }
        if (this.getResponseJSON().length() > 0) {
            removeResponseJSON.put((Object)this.getResponseJSON());
        }
        return this.getResponseJSON();
    }
    
    @Override
    public JSONObject removeAppsToDevices(final JSONObject jsonObject) throws Exception {
        this.setResponseJSON(new JSONObject());
        final JSONArray apps = jsonObject.getJSONArray("apps");
        for (int totalappstobeinstalled = apps.length(), i = 0; i < totalappstobeinstalled; ++i) {
            final JSONObject appDetailsJSON = apps.getJSONObject(i);
            final Long appID = appDetailsJSON.getLong("APP_ID");
            String productID = String.valueOf(appDetailsJSON.get("IDENTIFIER"));
            productID = "app:" + productID;
            final JSONArray usersanddevices = appDetailsJSON.getJSONArray("UsersAndDevices");
            for (int totaldevices = usersanddevices.length(), j = 0; j < totaldevices; ++j) {
                final JSONObject devicesJSON = usersanddevices.getJSONObject(j);
                final String userID = String.valueOf(devicesJSON.get("BS_STORE_ID"));
                final Long resourceID = devicesJSON.getLong("MANAGED_DEVICE_ID");
                final String deviceID = String.valueOf(devicesJSON.get("GOOGLE_PLAY_SERVICE_ID"));
                this.getAndroidEnterprise().installs().delete(this.enterpriseID, userID, deviceID, productID).queue(this.batchRequest, (JsonBatchCallback)new DeviceAppRemovalStatusCallback(resourceID, userID, appID));
                this.executeBatchOnThreshold("Device app removal execute batch of 1000-Batch");
            }
        }
        final int totalResources = (this.batchCount - 1) * GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH + this.batchRequest.size();
        if (totalResources > GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            this.logger.log(Level.INFO, "Total resources in removeAppsToDevices : {0}", totalResources);
        }
        return this.getResponseJSON();
    }
    
    public String getManagedAppConfig(final String appPackageName) throws Exception {
        return ((AppRestrictionsSchema)this.getAndroidEnterprise().products().getAppRestrictionsSchema(this.enterpriseID, "app:" + appPackageName).execute()).toString();
    }
    
    public String getAppPermission(final String appPackageName) throws Exception {
        return ((ProductPermissions)this.getAndroidEnterprise().products().getPermissions(this.enterpriseID, "app:" + appPackageName).execute()).toString();
    }
    
    public JSONArray getLicenseAssignedUserList(final String appPackageName) throws Exception {
        final JSONArray userIdList = new JSONArray();
        final String response = ((GroupLicenseUsersListResponse)this.getAndroidEnterprise().grouplicenseusers().list(this.enterpriseID, "app:" + appPackageName).execute()).toString();
        final JSONObject respJSON = new JSONObject(response);
        final JSONArray userArr = respJSON.optJSONArray("user");
        if (userArr != null) {
            for (int i = 0; i < userArr.length(); ++i) {
                userIdList.put(userArr.getJSONObject(i).get("id"));
            }
        }
        return userIdList;
    }
    
    public String insertPage(final JSONObject pageJSON) throws IOException, Exception {
        final String title = String.valueOf(pageJSON.get("PAGE_NAME"));
        final String pageId = pageJSON.optString("STORE_PAGE_ID");
        final StorePage page = new StorePage();
        final List names = new ArrayList();
        names.add(new LocalizedText().setLocale("en").setText(title));
        page.setName(names);
        if (pageId != null && !pageId.isEmpty()) {
            return ((StorePage)this.getAndroidEnterprise().storelayoutpages().update(this.enterpriseID, pageId, page).execute()).getId();
        }
        return ((StorePage)this.getAndroidEnterprise().storelayoutpages().insert(this.enterpriseID, page).execute()).getId();
    }
    
    public String insertCluster(final String pageId, final JSONObject clusterJSON) throws IOException, Exception {
        final String clusterId = clusterJSON.optString("STORE_CLUSTER_ID");
        final String title = String.valueOf(clusterJSON.get("CLUSTER_NAME"));
        final String orderInPage = String.valueOf(clusterJSON.get("CLUSTER_ORDER_NUMBER"));
        final JSONArray productIdArr = clusterJSON.getJSONArray("apps");
        final List names = new ArrayList();
        names.add(new LocalizedText().setLocale("en").setText(title));
        final StoreCluster cluster = new StoreCluster();
        cluster.setName(names);
        cluster.setProductId(JSONUtil.getInstance().convertJSONArrayTOList(productIdArr));
        cluster.setOrderInPage(orderInPage);
        if (clusterId != null && !clusterId.isEmpty()) {
            return ((StoreCluster)this.getAndroidEnterprise().storelayoutclusters().update(this.enterpriseID, pageId, clusterId, cluster).execute()).getId();
        }
        return ((StoreCluster)this.getAndroidEnterprise().storelayoutclusters().insert(this.enterpriseID, pageId, cluster).execute()).getId();
    }
    
    public void addLinks(final String pageId, final String title, final JSONArray pageArr) throws IOException {
        final List names = new ArrayList();
        names.add(new LocalizedText().setLocale("en").setText(title));
        this.getAndroidEnterprise().storelayoutpages().update(this.enterpriseID, pageId, new StorePage().setName(names).setLink(JSONUtil.getInstance().convertJSONArrayTOList(pageArr))).execute();
    }
    
    public void setHomePage(final String pageId) throws IOException {
        final StoreLayout storeLayout = new StoreLayout();
        storeLayout.setHomepageId(pageId);
        this.getAndroidEnterprise().enterprises().setStoreLayout(this.enterpriseID, storeLayout).execute();
    }
    
    public void deleteStoreLayoutPage(final JSONArray pageIdArr) throws IOException, JSONException {
        final BatchRequest batch = this.getAndroidEnterprise().batch();
        for (int i = 0; i < pageIdArr.length(); ++i) {
            this.getAndroidEnterprise().storelayoutpages().delete(this.enterpriseID, String.valueOf(pageIdArr.get(i))).queue(batch, (JsonBatchCallback)new JsonBatchCallback<Void>() {
                public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
                    GooglePlayEnterpriseBusinessStore.this.logger.log(Level.WARNING, gje.toPrettyString());
                }
                
                public void onSuccess(final Void t, final HttpHeaders hh) throws IOException {
                    GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, "Deleted Store Page {0}", t);
                }
            });
        }
        if (batch.size() > 0) {
            batch.execute();
        }
    }
    
    public void deleteStoreLayoutClusters(final String pageId, final JSONArray clusterIdArr) throws IOException, JSONException {
        final BatchRequest batch = this.getAndroidEnterprise().batch();
        for (int i = 0; i < clusterIdArr.length(); ++i) {
            this.logger.log(Level.INFO, "Cluster Delete Invokde for Cluster Id : {0} of page :{1}", new Object[] { clusterIdArr.get(i), pageId });
            this.getAndroidEnterprise().storelayoutclusters().delete(this.enterpriseID, pageId, String.valueOf(clusterIdArr.get(i))).queue(batch, (JsonBatchCallback)new JsonBatchCallback<Void>() {
                public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
                    GooglePlayEnterpriseBusinessStore.this.logger.log(Level.WARNING, gje.toPrettyString());
                }
                
                public void onSuccess(final Void t, final HttpHeaders hh) throws IOException {
                    GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, "Deleted Store Cluster id {0}", t);
                }
            });
        }
        if (batch.size() > 0) {
            batch.execute();
        }
    }
    
    public List getPages() throws IOException {
        final List pageList = new ArrayList();
        final List<StorePage> pages = ((StoreLayoutPagesListResponse)this.getAndroidEnterprise().storelayoutpages().list(this.enterpriseID).execute()).getPage();
        if (pages != null) {
            for (final StorePage page : pages) {
                pageList.add(page.getId());
            }
        }
        return pageList;
    }
    
    public List getClusters(final String pageId) throws IOException {
        final List clusterList = new ArrayList();
        final List<StoreCluster> clusters = ((StoreLayoutClustersListResponse)this.getAndroidEnterprise().storelayoutclusters().list(this.enterpriseID, pageId).execute()).getCluster();
        if (clusters != null) {
            for (final StoreCluster cluster : clusters) {
                clusterList.add(cluster.getId());
            }
        }
        return clusterList;
    }
    
    public JSONObject approveNonAFWPlaystoreApps(final JSONArray appsToBeApproved, final Long customerId) throws JSONException {
        final JSONObject autoApprovalStatus = new JSONObject();
        final JSONArray appsApprovalStatus = new JSONArray();
        final int totalcount = appsToBeApproved.length();
        autoApprovalStatus.put("totalcount", totalcount);
        int successCount = 0;
        for (int i = 0; i < totalcount; ++i) {
            boolean status = false;
            status = false;
            final JSONObject app = appsToBeApproved.getJSONObject(i);
            final String productID = "app:" + app.getString("IDENTIFIER");
            final String name = app.getString("GROUP_DISPLAY_NAME");
            final Long appGroupId = app.getLong("APP_GROUP_ID");
            try {
                this.logger.log(Level.INFO, "Approving app {0} ", productID);
                final String approvalUrl = ((ProductsGenerateApprovalUrlResponse)this.getAndroidEnterprise().products().generateApprovalUrl(this.enterpriseID, productID).execute()).getUrl();
                this.getAndroidEnterprise().products().approve(this.enterpriseID, productID, new ProductsApproveRequest().setApprovalUrlInfo(new ApprovalUrlInfo().setApprovalUrl(approvalUrl)).setApprovedPermissions("allPermissions")).execute();
                status = true;
                ++successCount;
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, e, () -> "Unable to auto approve " + s);
            }
            final JSONObject appStatus = new JSONObject();
            appStatus.put("IDENTIFIER", (Object)productID);
            appStatus.put("Status", status);
            appStatus.put("name", (Object)name);
            appStatus.put("APP_GROUP_ID", (Object)appGroupId);
            appsApprovalStatus.put((Object)appStatus);
        }
        autoApprovalStatus.put("apps", (Object)appsApprovalStatus);
        autoApprovalStatus.put("successcount", successCount);
        new PlaystoreAppsAutoApprover().persistAutoApprovalStatus(customerId, successCount, totalcount);
        return autoApprovalStatus;
    }
    
    public String generateWebToken(final JSONObject options) throws IOException, JSONException {
        final String parent = String.valueOf(options.get("parent"));
        final Boolean playSearch = options.optBoolean("play_search", (boolean)Boolean.FALSE);
        final Boolean playSearchAndApprove = options.optBoolean("play_search_approve_mode", (boolean)Boolean.FALSE);
        final Boolean webApps = options.optBoolean("web_apps", (boolean)Boolean.FALSE);
        final Boolean privateApps = options.optBoolean("private_apps", (boolean)Boolean.FALSE);
        final Boolean storeBuilder = options.optBoolean("store_builder", (boolean)Boolean.FALSE);
        final Boolean zeroTouch = options.optBoolean("zeroTouch", (boolean)Boolean.FALSE);
        final AdministratorWebTokenSpec tokenSpec = new AdministratorWebTokenSpec();
        tokenSpec.setParent(parent);
        tokenSpec.setPlaySearch(new AdministratorWebTokenSpecPlaySearch().setEnabled(playSearch).setApproveApps(playSearchAndApprove));
        tokenSpec.setPrivateApps(new AdministratorWebTokenSpecPrivateApps().setEnabled(privateApps));
        tokenSpec.setStoreBuilder(new AdministratorWebTokenSpecStoreBuilder().setEnabled(storeBuilder));
        tokenSpec.setWebApps(new AdministratorWebTokenSpecWebApps().setEnabled(webApps));
        tokenSpec.setZeroTouch(new AdministratorWebTokenSpecZeroTouch().setEnabled(zeroTouch));
        return ((AdministratorWebToken)this.getAndroidEnterprise().enterprises().createWebToken(this.enterpriseID, tokenSpec).execute()).getToken();
    }
    
    public AndroidEnterprise getAndroidEnterprise() {
        return this.androidEnterprise;
    }
    
    public void setAndroidEnterprise(final AndroidEnterprise androidEnterprise) {
        this.androidEnterprise = androidEnterprise;
    }
    
    public JSONObject getResponseJSON() {
        return this.responseJSON;
    }
    
    public void setResponseJSON(final JSONObject responseJSON) {
        this.responseJSON = responseJSON;
    }
    
    private JSONObject fillTrackInfo(final JSONObject productDetailsJSON, final int productionVersionCount, final List<TrackInfo> appTracks) throws Exception {
        Boolean betaAvailable = false;
        int testingTrackCount = 0;
        if (appTracks != null) {
            for (final TrackInfo track : appTracks) {
                final String trackName = track.getTrackAlias();
                if (trackName.equalsIgnoreCase("beta")) {
                    betaAvailable = true;
                }
                else {
                    ++testingTrackCount;
                }
            }
        }
        this.bslogger.log(Level.INFO, "Track info for app - beta available - {0}, multi testing track available other than beta - {1}, production version count - {2}", new Object[] { betaAvailable, testingTrackCount, productionVersionCount });
        final AndroidSyncAppsHandler handler = new AndroidSyncAppsHandler();
        final JSONObject syncTrackParams = new JSONObject();
        if (betaAvailable) {
            syncTrackParams.put("PS_TWO_TRACK_APPS", 1);
        }
        if (testingTrackCount > 0) {
            syncTrackParams.put("PS_MULTI_TRACK_APPS", 1);
        }
        new MDMCustomerParamsHandler().incrementParameters(syncTrackParams, this.customerID);
        productDetailsJSON.put("BetaAvailable", (Object)betaAvailable);
        productDetailsJSON.put("MultiTestTrackCount", testingTrackCount);
        return productDetailsJSON;
    }
    
    @Override
    public JSONObject processAppData(final JSONObject appDetailsJSON) throws Exception {
        JSONObject appAdditionDetails = new JSONObject();
        final String bundleID = (String)appDetailsJSON.opt("BUNDLE_IDENTIFIER");
        final Long businessStoreID = appDetailsJSON.optLong("BUSINESSSTORE_ID");
        final Long storeAssetId = MDBusinessStoreAssetUtil.addMdBusinessStoreToAssetRel(businessStoreID, bundleID);
        appAdditionDetails.put("STORE_ASSET_ID", (Object)storeAssetId);
        try {
            appAdditionDetails = this.processSpecificAppFailureCase(appDetailsJSON, appAdditionDetails);
            if (!appAdditionDetails.optBoolean("success")) {
                return appAdditionDetails;
            }
            if (appDetailsJSON.getInt("PACKAGE_TYPE") == 1) {
                appAdditionDetails.put("TOTAL_APP_COUNT", appDetailsJSON.get("TOTAL_APP_COUNT"));
                appAdditionDetails.put("AVAILABLE_APP_COUNT", appDetailsJSON.get("AVAILABLE_APP_COUNT"));
                appAdditionDetails.put("ASSIGNED_APP_COUNT", appDetailsJSON.get("ASSIGNED_APP_COUNT"));
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
            final Boolean isPaid = appDetailsJSON.getJSONObject("MDPackageToAppGroupForm").getBoolean("IS_PAID_APP");
            final int isPrivate = appDetailsJSON.getJSONObject("MDPackageToAppGroupForm").optInt("PRIVATE_APP_TYPE", 0);
            appAdditionDetails.put("PACKAGE_TYPE", (int)(((boolean)isPaid) ? 1 : 0));
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(2).isCurrentPackageNewToAppRepo(bundleID, this.customerID);
            if (isCurrentPackageNew) {
                appAdditionDetails.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
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
            packageAppGroupJSON.put("IS_PAID_APP", (Object)isPaid);
            packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
            packageAppGroupJSON.put("PRIVATE_APP_TYPE", isPrivate);
            appAdditionDetails.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            appAdditionDetails.put("PackagePolicyForm", (Object)new JSONObject());
            final JSONArray mdApps = new JSONArray();
            final JSONObject curPackage = new JSONObject();
            curPackage.put("MdPackageToAppDataFrom", (Object)MdPackageToAppDataForm);
            curPackage.put("packageIdentifier", (Object)bundleID);
            curPackage.put("APP_VERSION", appDetailsJSON.get("APP_VERSION"));
            curPackage.put("APP_NAME_SHORT_VERSION", (Object)appDetailsJSON.optString("APP_NAME_SHORT_VERSION", "--"));
            mdApps.put((Object)curPackage);
            appAdditionDetails.put("MDAPPS", (Object)mdApps);
            appAdditionDetails.put("success", true);
            if (appDetailsJSON.has("applicable_versions")) {
                appAdditionDetails.put("applicable_versions", (Object)appDetailsJSON.getJSONArray("applicable_versions"));
            }
        }
        catch (final Exception e) {
            appAdditionDetails.put("success", false);
            appAdditionDetails.put("ERROR_CODE", 2002);
            this.bslogger.log(Level.SEVERE, "failed to add package during PlayStore Sync ", e);
        }
        return appAdditionDetails;
    }
    
    protected JSONObject processSpecificAppFailureCase(final JSONObject appDetailsJSON, final JSONObject appAdditionDetails) {
        final Boolean appFailureCase = appDetailsJSON.optBoolean("APP_FAILURE_CASE", false);
        if (appFailureCase) {
            appAdditionDetails.put("success", false);
            appAdditionDetails.put("ERROR_CODE", appDetailsJSON.optInt("ERROR_CODE"));
            return appAdditionDetails;
        }
        final String bundleID = (String)appDetailsJSON.opt("BUNDLE_IDENTIFIER");
        final Long customerId = appDetailsJSON.optLong("CustomerID");
        final JSONObject jsonObject = AppsUtil.getInstance().isAndroidStoreAppConversionAllowed(bundleID, customerId);
        final Boolean nonProdReleaseLabelAvailable = jsonObject.optBoolean("NON_PROD_RELEASE_LABEL_AVAILABLE", false);
        final Boolean enterprisePriorityModeSet = jsonObject.optBoolean("ENTERPRISE_PRIORITYMODE_SET", false);
        if (nonProdReleaseLabelAvailable || enterprisePriorityModeSet) {
            if (nonProdReleaseLabelAvailable) {
                appAdditionDetails.put("ERROR_CODE", 2000);
            }
            else if (enterprisePriorityModeSet) {
                appAdditionDetails.put("ERROR_CODE", 2001);
            }
            appAdditionDetails.put("success", false);
            return appAdditionDetails;
        }
        appAdditionDetails.put("success", true);
        return appAdditionDetails;
    }
    
    public JSONObject getStoreLayout() throws IOException, JSONException {
        final StoreLayout layout = (StoreLayout)this.getAndroidEnterprise().enterprises().getStoreLayout(this.enterpriseID).execute();
        final JSONObject layoutJSON = new JSONObject();
        layoutJSON.put("HomePageId", (Object)layout.getHomepageId());
        layoutJSON.put("LayoutType", (Object)layout.getStoreLayoutType());
        return layoutJSON;
    }
    
    public void updateStoreCluster(final StoreCluster cluster, final String pageId) throws IOException {
        this.getAndroidEnterprise().storelayoutclusters().update(this.enterpriseID, pageId, cluster.getId(), cluster).execute();
    }
    
    public StoreCluster getStoreCluster(final String clusterId, final String pageId) throws IOException {
        return (StoreCluster)this.getAndroidEnterprise().storelayoutclusters().get(this.enterpriseID, pageId, clusterId).execute();
    }
    
    public List<StoreCluster> getClustersAsObj(final String pageId) throws IOException {
        final List clusterList = new ArrayList();
        final List<StoreCluster> clusters = ((StoreLayoutClustersListResponse)this.getAndroidEnterprise().storelayoutclusters().list(this.enterpriseID, pageId).execute()).getCluster();
        if (clusters != null) {
            for (final StoreCluster cluster : clusters) {
                clusterList.add(cluster);
            }
        }
        return clusterList;
    }
    
    public void initializeBatch() {
        this.batchRequest = this.getAndroidEnterprise().batch();
        this.batchCount = 1;
    }
    
    private JSONObject executeBatchOnThreshold(final String msg) throws Exception {
        if (this.batchRequest.size() == GooglePlayEnterpriseBusinessStore.MAX_RESOURCES_PER_BATCH) {
            final JSONObject batchResponse = this.executeBatch(msg);
            this.batchRequest.getSleeper().sleep(3000L);
            return batchResponse;
        }
        return null;
    }
    
    private JSONObject executeBatch(final String msg) throws IOException {
        if (this.batchRequest.size() > 0) {
            ++this.batchCount;
            this.logger.log(Level.INFO, msg);
            this.logger.log(Level.INFO, "Batch size {0}", this.batchRequest.size());
            this.batchRequest.execute();
            return this.getResponseJSON();
        }
        this.batchRequest = this.getAndroidEnterprise().batch();
        return new JSONObject();
    }
    
    public JSONObject clearBatch(final String msg) throws IOException {
        return this.executeBatch(String.format("Executing %s rest of the batch - BatchCount %d", msg, this.batchCount));
    }
    
    public JSONArray approveAFWApps(final List<String> appsToBeApproved, JSONArray approvedAppsSummary) throws Exception {
        this.logger.log(Level.INFO, "Initiate generating app approval url");
        if (!appsToBeApproved.isEmpty()) {
            this.generateAppApprovalUrl(appsToBeApproved);
            final JSONObject responseJSON = this.getResponseJSON();
            if (responseJSON != null) {
                final JSONArray generateAppApprovalUrlFailedList = responseJSON.optJSONArray("FailedList");
                if (generateAppApprovalUrlFailedList != null) {
                    approvedAppsSummary = JSONUtil.mergeJSONArray(approvedAppsSummary, generateAppApprovalUrlFailedList);
                }
                final JSONArray successArr = responseJSON.optJSONArray("SuccessList");
                this.logger.log(Level.INFO, "Initiating app approval - {0}", successArr);
                this.approveProduct(successArr, approvedAppsSummary);
                final JSONArray appApprovalJSON = this.getResponseJSON().optJSONArray("appList");
                if (appApprovalJSON != null) {
                    approvedAppsSummary = JSONUtil.mergeJSONArray(approvedAppsSummary, appApprovalJSON);
                }
            }
        }
        return approvedAppsSummary;
    }
    
    private void generateAppApprovalUrl(final List<String> appsToBeApproved) throws Exception {
        this.setResponseJSON(new JSONObject());
        this.initializeBatch();
        for (final String appIdentifier : appsToBeApproved) {
            final String productID = "app:" + appIdentifier;
            this.getAndroidEnterprise().products().generateApprovalUrl(this.enterpriseID, productID).queue(this.batchRequest, (JsonBatchCallback)new ProductsGenerateApprovalUrlCallBack(appIdentifier));
            this.executeBatchOnThreshold("Generate App Approval Url execute batch of 1000-Batch");
        }
        this.clearBatch("Generate App Approval Url execute from rest of the batch");
    }
    
    private void approveProduct(final JSONArray appsToBeApproved, final JSONArray approvedAppsSummary) throws Exception {
        this.setResponseJSON(new JSONObject());
        this.initializeBatch();
        for (int totalAppsToBeApproved = appsToBeApproved.length(), i = 0; i < totalAppsToBeApproved; ++i) {
            final JSONObject appDetailsJSON = appsToBeApproved.getJSONObject(i);
            final String appIdentifier = String.valueOf(appDetailsJSON.get("IDENTIFIER"));
            final String productID = "app:" + appIdentifier;
            try {
                final String approvalUrl = appDetailsJSON.getString("url");
                this.getAndroidEnterprise().products().approve(this.enterpriseID, productID, new ProductsApproveRequest().setApprovalUrlInfo(new ApprovalUrlInfo().setApprovalUrl(approvalUrl)).setApprovedPermissions("allPermissions")).queue(this.batchRequest, (JsonBatchCallback)new ProductApprovalCallBack(appIdentifier));
                this.executeBatchOnThreshold("App Approval execute batch of 1000-Batch");
            }
            catch (final Exception ex) {
                final JSONObject failureStatusJSON = new JSONObject();
                failureStatusJSON.put("IDENTIFIER", (Object)appIdentifier);
                failureStatusJSON.put("Reason", (Object)ex.getMessage());
                failureStatusJSON.put("ApprovalStatus", (Object)Boolean.FALSE);
                approvedAppsSummary.put((Object)failureStatusJSON);
            }
        }
        this.clearBatch("App Approval execute from rest of the batch");
    }
    
    static {
        MAX_RESOURCES_PER_BATCH = 1000;
        JSON_FACTORY = (JsonFactory)JacksonFactory.getDefaultInstance();
    }
    
    private class AppDetailsCallback extends JsonBatchCallback<Product>
    {
        GroupLicense gl;
        
        public AppDetailsCallback(final GroupLicense gl) throws JSONException {
            this.gl = gl;
        }
        
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, "Error in app details call back {0}", gje.toPrettyString());
        }
        
        public void onSuccess(final Product t, final HttpHeaders hh) throws IOException {
            if (t.getProductId() != null) {
                final String productID = t.getProductId().substring(4);
                JSONObject productDetailsJSON = new JSONObject();
                productDetailsJSON.put("BUNDLE_IDENTIFIER", (Object)productID);
                try {
                    if (!this.gl.getApproval().equalsIgnoreCase("approved")) {
                        GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.INFO, "App unapproved {0}", productID);
                        return;
                    }
                    final String iconURL = t.getIconUrl();
                    productDetailsJSON.put("DISPLAY_IMAGE_DOWNLOAD_URL", (Object)iconURL);
                    final boolean isFree = this.gl.getAcquisitionKind().equals("free");
                    if (!isFree) {
                        productDetailsJSON.put("AVAILABLE_APP_COUNT", this.gl.getNumPurchased() - this.gl.getNumProvisioned());
                        productDetailsJSON.put("ASSIGNED_APP_COUNT", (Object)this.gl.getNumProvisioned());
                        productDetailsJSON.put("TOTAL_APP_COUNT", (Object)this.gl.getNumPurchased());
                    }
                    productDetailsJSON.put("APP_NAME", (Object)t.getTitle());
                    productDetailsJSON.put("APP_TITLE", (Object)t.getTitle());
                    productDetailsJSON.put("IDENTIFIER", (Object)productID);
                    GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.INFO, "Syncing app {0}", productID);
                    productDetailsJSON = this.fillVersionDetails(t, productDetailsJSON, productID);
                    if (productDetailsJSON.optString("APP_VERSION", "--").equalsIgnoreCase("--")) {
                        JSONArray jsonArray = GooglePlayEnterpriseBusinessStore.this.responseJSON.optJSONArray("UnpublishedApps");
                        if (jsonArray == null) {
                            jsonArray = new JSONArray();
                        }
                        jsonArray.put((Object)productID);
                        GooglePlayEnterpriseBusinessStore.this.responseJSON.put("UnpublishedApps", (Object)jsonArray);
                        GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.INFO, "App {0} is marked as unpublished", productID);
                        return;
                    }
                    productDetailsJSON.put("APP_CATEGORY_NAME", (Object)"Productivity");
                    final int packageType = isFree ? 0 : 1;
                    productDetailsJSON.put("PACKAGE_TYPE", packageType);
                    final JSONObject packageAppDataJSON = new JSONObject();
                    packageAppDataJSON.put("PACKAGE_TYPE", packageType);
                    packageAppDataJSON.put("DISPLAY_IMAGE_DOWNLOAD_URL", (Object)iconURL);
                    packageAppDataJSON.put("STORE_URL", (Object)t.getDetailsUrl());
                    packageAppDataJSON.put("SUPPORTED_DEVICES", (Object)new Integer(1));
                    packageAppDataJSON.put("DESCRIPTION", (Object)t.getDescription());
                    final JSONObject packageAppGroupJSON = new JSONObject();
                    packageAppGroupJSON.put("IS_PAID_APP", !isFree);
                    packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", true);
                    if (t.getDistributionChannel().toLowerCase().startsWith("private")) {
                        packageAppGroupJSON.put("PRIVATE_APP_TYPE", 1);
                    }
                    packageAppDataJSON.put("DISPLAY_IMAGE_DOWNLOAD_URL", (Object)iconURL);
                    productDetailsJSON.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
                    productDetailsJSON.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
                }
                catch (final Exception exp) {
                    GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.SEVERE, exp, () -> "Exception when getting app details for app " + product);
                    productDetailsJSON.put("APP_FAILURE_CASE", true);
                    productDetailsJSON.put("ERROR_CODE", 2004);
                }
                JSONArray jsonArray2 = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONArray("Apps");
                if (jsonArray2 == null) {
                    jsonArray2 = new JSONArray();
                }
                jsonArray2.put((Object)productDetailsJSON);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("Apps", (Object)jsonArray2);
            }
            else {
                GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.SEVERE, "App identifier could not be fetched from Playstore for the app");
            }
        }
        
        private JSONObject fillVersionDetails(final Product t, JSONObject productDetailsJSON, final String productID) {
            try {
                final List<AppVersion> appVersions = t.getAppVersion();
                Boolean isVersionFilled = false;
                int productionVersionCount = 1;
                final JSONArray allVersions = new JSONArray();
                if (appVersions != null) {
                    for (final AppVersion appVersion : appVersions) {
                        if (appVersion.getIsProduction() != null && appVersion.getIsProduction()) {
                            if (isVersionFilled) {
                                ++productionVersionCount;
                                final JSONObject appVersionDetails = new JSONObject();
                                appVersionDetails.put("APP_VERSION", (Object)appVersion.getVersionString());
                                appVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)appVersion.getVersionCode().toString());
                                allVersions.put((Object)appVersionDetails);
                            }
                            else {
                                productDetailsJSON.put("APP_VERSION", (Object)appVersion.getVersionString());
                                productDetailsJSON.put("APP_NAME_SHORT_VERSION", (Object)appVersion.getVersionCode().toString());
                                isVersionFilled = true;
                                final JSONObject appVersionDetails = new JSONObject();
                                appVersionDetails.put("APP_VERSION", (Object)appVersion.getVersionString());
                                appVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)appVersion.getVersionCode().toString());
                                allVersions.put((Object)appVersionDetails);
                                GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.INFO, "App version and Version Code of the first production track is {0} , {1}", new Object[] { appVersion.getVersionString(), appVersion.getVersionCode().toString() });
                            }
                        }
                    }
                    if (!isVersionFilled) {
                        GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.WARNING, "No production track available for {0}. Hence taking version of first non-production track", productID);
                        productDetailsJSON.put("APP_VERSION", (Object)appVersions.get(0).getVersionString());
                        productDetailsJSON.put("APP_NAME_SHORT_VERSION", (Object)appVersions.get(0).getVersionCode().toString());
                    }
                }
                else {
                    GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.INFO, "App version could not be fetched from Playstore");
                    productDetailsJSON.put("APP_VERSION", (Object)"--");
                    productDetailsJSON.put("APP_NAME_SHORT_VERSION", (Object)"--");
                }
                productDetailsJSON.put("SplitApkCount", productionVersionCount);
                if (allVersions.length() > 0) {
                    productDetailsJSON.put("applicable_versions", (Object)allVersions);
                }
                productDetailsJSON = GooglePlayEnterpriseBusinessStore.this.fillTrackInfo(productDetailsJSON, productionVersionCount, t.getAppTracks());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.bslogger.log(Level.SEVERE, "Exception occurred in fillVersionDetails ", ex);
            }
            return productDetailsJSON;
        }
    }
    
    private class DeviceAppRemovalStatusCallback extends JsonBatchCallback<Void>
    {
        String userID;
        Long resourceID;
        Long appID;
        
        public DeviceAppRemovalStatusCallback(final Long resourceID, final String userID, final Long appID) {
            this.userID = null;
            this.resourceID = null;
            this.appID = null;
            this.userID = userID;
            this.resourceID = resourceID;
            this.appID = appID;
        }
        
        public void onSuccess(final Void installStatus, final HttpHeaders responseHeaders) {
            try {
                JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject(this.appID.toString());
                if (appStatusJSON == null) {
                    appStatusJSON = new JSONObject();
                }
                JSONArray successArr = appStatusJSON.optJSONArray("SuccessList");
                if (successArr == null) {
                    successArr = new JSONArray();
                }
                successArr.put((Object)this.resourceID);
                appStatusJSON.put("SuccessList", (Object)successArr);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.appID.toString(), (Object)appStatusJSON);
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.FINE, "Deleting Portal App from device responseJSON App_id : {0} , Res_id : {1}", new Object[] { this.appID.toString(), appStatusJSON });
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                if (e.getMessage().toLowerCase().contains("no entitlement was found")) {
                    JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.responseJSON.optJSONObject(this.appID.toString());
                    if (appStatusJSON == null) {
                        appStatusJSON = new JSONObject();
                    }
                    JSONArray successArr = appStatusJSON.optJSONArray("SuccessList");
                    if (successArr == null) {
                        successArr = new JSONArray();
                    }
                    successArr.put((Object)this.resourceID);
                    appStatusJSON.put("SuccessList", (Object)successArr);
                    GooglePlayEnterpriseBusinessStore.this.responseJSON.put(this.appID.toString(), (Object)appStatusJSON);
                }
                else {
                    final JSONObject failureStatusJSON = new JSONObject();
                    failureStatusJSON.put("resourceId", (Object)this.resourceID);
                    failureStatusJSON.put("ErrorMessage", (Object)e.getMessage());
                    JSONObject appStatusJSON2 = GooglePlayEnterpriseBusinessStore.this.responseJSON.optJSONObject(this.appID.toString());
                    if (appStatusJSON2 == null) {
                        appStatusJSON2 = new JSONObject();
                    }
                    JSONArray failedArr = appStatusJSON2.optJSONArray("FailedList");
                    if (failedArr == null) {
                        failedArr = new JSONArray();
                    }
                    failedArr.put((Object)failureStatusJSON);
                    appStatusJSON2.put("FailedList", (Object)failedArr);
                    GooglePlayEnterpriseBusinessStore.this.responseJSON.put(this.appID.toString(), (Object)appStatusJSON2);
                }
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class AppAssignStatusCallback extends JsonBatchCallback<Entitlement>
    {
        String userID;
        Long resourceID;
        Long appID;
        
        public AppAssignStatusCallback(final Long resourceID, final String userID, final Long appID) {
            this.userID = null;
            this.resourceID = null;
            this.appID = null;
            this.userID = userID;
            this.resourceID = resourceID;
            this.appID = appID;
        }
        
        public void onSuccess(final Entitlement installStatus, final HttpHeaders responseHeaders) {
            try {
                JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject(this.appID.toString());
                if (appStatusJSON == null) {
                    appStatusJSON = new JSONObject();
                }
                JSONArray successArr = appStatusJSON.optJSONArray("SuccessList");
                if (successArr == null) {
                    successArr = new JSONArray();
                }
                successArr.put((Object)this.resourceID);
                appStatusJSON.put("SuccessList", (Object)successArr);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.appID.toString(), (Object)appStatusJSON);
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                final JSONObject failureStatusJSON = new JSONObject();
                failureStatusJSON.put("resourceId", (Object)this.resourceID);
                failureStatusJSON.put("ErrorMessage", (Object)e.getMessage());
                JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject(this.appID.toString());
                if (appStatusJSON == null) {
                    appStatusJSON = new JSONObject();
                }
                JSONArray failedArr = appStatusJSON.optJSONArray("FailedList");
                if (failedArr == null) {
                    failedArr = new JSONArray();
                }
                failedArr.put((Object)failureStatusJSON);
                appStatusJSON.put("FailedList", (Object)failedArr);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.appID.toString(), (Object)appStatusJSON);
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class AppInstallStatusCallback extends JsonBatchCallback<Install>
    {
        String userID;
        Long resourceID;
        Long appID;
        
        public AppInstallStatusCallback(final Long resourceID, final String userID, final Long appID) {
            this.userID = null;
            this.resourceID = null;
            this.appID = null;
            this.userID = userID;
            this.resourceID = resourceID;
            this.appID = appID;
        }
        
        public void onSuccess(final Install installStatus, final HttpHeaders responseHeaders) {
            try {
                JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject(this.appID.toString());
                if (appStatusJSON == null) {
                    appStatusJSON = new JSONObject();
                }
                JSONArray successArr = appStatusJSON.optJSONArray("SuccessList");
                if (successArr == null) {
                    successArr = new JSONArray();
                }
                successArr.put((Object)this.resourceID);
                appStatusJSON.put("SuccessList", (Object)successArr);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.appID.toString(), (Object)appStatusJSON);
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                final JSONObject failureStatusJSON = new JSONObject();
                failureStatusJSON.put("resourceId", (Object)this.resourceID);
                final String errorMsg = I18N.getMsg(GoogleAPIErrorHandler.getErrorMessage(e.getMessage()), new Object[0]);
                failureStatusJSON.put("ErrorMessage", (Object)errorMsg);
                JSONObject appStatusJSON = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject(this.appID.toString());
                if (appStatusJSON == null) {
                    appStatusJSON = new JSONObject();
                }
                JSONArray failedArr = appStatusJSON.optJSONArray("FailedList");
                if (failedArr == null) {
                    failedArr = new JSONArray();
                }
                failedArr.put((Object)failureStatusJSON);
                appStatusJSON.put("FailedList", (Object)failedArr);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.appID.toString(), (Object)appStatusJSON);
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class ProductSetStatusCallback extends JsonBatchCallback<ProductSet>
    {
        String userID;
        String deviceID;
        Long resourceID;
        
        public ProductSetStatusCallback(final String userID) throws JSONException {
            this.userID = null;
            this.deviceID = null;
            this.resourceID = null;
            this.userID = userID;
        }
        
        public void onSuccess(final ProductSet installStatus, final HttpHeaders responseHeaders) {
            try {
                final JSONObject installStatusJSON = new JSONObject();
                installStatusJSON.put("Status", (Object)"Success");
                installStatusJSON.put("product", (Collection)installStatus.getProductId());
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put(this.userID, (Object)installStatusJSON);
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, "Failed success handling for ProductSetStatusCallback", ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final IOException ex) {
                Logger.getLogger(GooglePlayEnterpriseBusinessStore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class GetUserDevicesCallback extends JsonBatchCallback<DevicesListResponse>
    {
        String userID;
        
        public GetUserDevicesCallback(final String userID) throws JSONException {
            this.userID = null;
            this.userID = userID;
        }
        
        public void onFailure(final GoogleJsonError gje, final HttpHeaders hh) throws IOException {
            GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, "Failed User Devices {0}", gje.toPrettyString());
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
                    JSONObject jsonObject = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONObject("usersanddevices");
                    if (jsonObject == null) {
                        jsonObject = new JSONObject();
                    }
                    jsonObject.put(this.userID, (Object)array);
                    GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("usersanddevices", (Object)jsonObject);
                }
            }
            catch (final Exception exp) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, "Exception in handling device list response {0}", dlr);
            }
        }
    }
    
    private class ProductsGenerateApprovalUrlCallBack extends JsonBatchCallback<ProductsGenerateApprovalUrlResponse>
    {
        String appIdentifier;
        
        public ProductsGenerateApprovalUrlCallBack(final String appIdentifier) {
            this.appIdentifier = null;
            this.appIdentifier = appIdentifier;
        }
        
        public void onSuccess(final ProductsGenerateApprovalUrlResponse productsGenerateApprovalUrlResponse, final HttpHeaders httpHeaders) {
            try {
                final JSONObject successJSON = new JSONObject();
                successJSON.put("IDENTIFIER", (Object)this.appIdentifier);
                successJSON.put("url", (Object)productsGenerateApprovalUrlResponse.getUrl());
                JSONArray successArr = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONArray("SuccessList");
                if (successArr == null) {
                    successArr = new JSONArray();
                }
                successArr.put((Object)successJSON);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("SuccessList", (Object)successArr);
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                final JSONObject failureStatusJSON = new JSONObject();
                failureStatusJSON.put("IDENTIFIER", (Object)this.appIdentifier);
                failureStatusJSON.put("Reason", (Object)e.getMessage());
                failureStatusJSON.put("ApprovalStatus", (Object)Boolean.FALSE);
                JSONArray failedArr = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONArray("FailedList");
                if (failedArr == null) {
                    failedArr = new JSONArray();
                }
                failedArr.put((Object)failureStatusJSON);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("FailedList", (Object)failedArr);
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private class ProductApprovalCallBack extends JsonBatchCallback<Void>
    {
        String appIdentifier;
        
        public ProductApprovalCallBack(final String appIdentifier) {
            this.appIdentifier = null;
            this.appIdentifier = appIdentifier;
        }
        
        public void onSuccess(final Void productApproval, final HttpHeaders httpHeaders) {
            try {
                final JSONObject successJSON = new JSONObject();
                successJSON.put("IDENTIFIER", (Object)this.appIdentifier);
                successJSON.put("ApprovalStatus", (Object)Boolean.TRUE);
                JSONArray appList = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONArray("appList");
                if (appList == null) {
                    appList = new JSONArray();
                }
                appList.put((Object)successJSON);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("appList", (Object)appList);
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
        
        public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
            try {
                final JSONObject failureStatusJSON = new JSONObject();
                failureStatusJSON.put("IDENTIFIER", (Object)this.appIdentifier);
                failureStatusJSON.put("Reason", (Object)e.getMessage());
                failureStatusJSON.put("ApprovalStatus", (Object)Boolean.FALSE);
                JSONArray appList = GooglePlayEnterpriseBusinessStore.this.getResponseJSON().optJSONArray("appList");
                if (appList == null) {
                    appList = new JSONArray();
                }
                appList.put((Object)failureStatusJSON);
                GooglePlayEnterpriseBusinessStore.this.getResponseJSON().put("appList", (Object)appList);
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.INFO, e.toPrettyString());
            }
            catch (final Exception ex) {
                GooglePlayEnterpriseBusinessStore.this.logger.log(Level.SEVERE, null, ex);
            }
        }
    }
}
