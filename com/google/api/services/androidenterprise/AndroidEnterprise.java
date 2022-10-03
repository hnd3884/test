package com.google.api.services.androidenterprise;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.services.androidenterprise.model.WebAppsListResponse;
import com.google.api.services.androidenterprise.model.WebApp;
import com.google.api.services.androidenterprise.model.UsersListResponse;
import com.google.api.services.androidenterprise.model.AuthenticationToken;
import com.google.api.services.androidenterprise.model.ProductSet;
import com.google.api.services.androidenterprise.model.User;
import com.google.api.services.androidenterprise.model.StoreLayoutPagesListResponse;
import com.google.api.services.androidenterprise.model.StorePage;
import com.google.api.services.androidenterprise.model.StoreLayoutClustersListResponse;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.google.api.services.androidenterprise.model.ServiceAccountKeysListResponse;
import com.google.api.services.androidenterprise.model.ServiceAccountKey;
import com.google.api.services.androidenterprise.model.ProductsListResponse;
import com.google.api.services.androidenterprise.model.ProductPermissions;
import com.google.api.services.androidenterprise.model.AppRestrictionsSchema;
import com.google.api.services.androidenterprise.model.Product;
import com.google.api.services.androidenterprise.model.ProductsGenerateApprovalUrlResponse;
import com.google.api.services.androidenterprise.model.ProductsApproveRequest;
import com.google.api.services.androidenterprise.model.Permission;
import com.google.api.services.androidenterprise.model.ManagedConfigurationsSettingsListResponse;
import com.google.api.services.androidenterprise.model.ManagedConfigurationsForUserListResponse;
import com.google.api.services.androidenterprise.model.ManagedConfigurationsForDeviceListResponse;
import com.google.api.services.androidenterprise.model.ManagedConfiguration;
import com.google.api.services.androidenterprise.model.InstallsListResponse;
import com.google.api.services.androidenterprise.model.Install;
import com.google.api.services.androidenterprise.model.GroupLicenseUsersListResponse;
import com.google.api.services.androidenterprise.model.GroupLicensesListResponse;
import com.google.api.services.androidenterprise.model.GroupLicense;
import com.google.api.services.androidenterprise.model.EntitlementsListResponse;
import com.google.api.services.androidenterprise.model.Entitlement;
import com.google.api.services.androidenterprise.model.EnterprisesSendTestPushNotificationResponse;
import com.google.api.services.androidenterprise.model.NotificationSet;
import com.google.api.services.androidenterprise.model.EnterprisesListResponse;
import com.google.api.services.androidenterprise.model.ServiceAccount;
import com.google.api.services.androidenterprise.model.SignupInfo;
import com.google.api.services.androidenterprise.model.AdministratorWebToken;
import com.google.api.services.androidenterprise.model.StoreLayout;
import com.google.api.services.androidenterprise.model.EnterpriseAccount;
import com.google.api.services.androidenterprise.model.Enterprise;
import com.google.api.services.androidenterprise.model.AdministratorWebTokenSpec;
import com.google.api.services.androidenterprise.model.DevicesListResponse;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.util.Key;
import com.google.api.services.androidenterprise.model.Device;
import com.google.api.services.androidenterprise.model.DeviceState;
import com.google.api.client.util.Preconditions;
import com.google.api.client.googleapis.GoogleUtils;
import java.io.IOException;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

public class AndroidEnterprise extends AbstractGoogleJsonClient
{
    public static final String DEFAULT_ROOT_URL = "https://androidenterprise.googleapis.com/";
    public static final String DEFAULT_MTLS_ROOT_URL = "https://androidenterprise.mtls.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "";
    public static final String DEFAULT_BATCH_PATH = "batch";
    public static final String DEFAULT_BASE_URL = "https://androidenterprise.googleapis.com/";
    
    public AndroidEnterprise(final HttpTransport transport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }
    
    AndroidEnterprise(final Builder builder) {
        super((AbstractGoogleJsonClient.Builder)builder);
    }
    
    protected void initialize(final AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize((AbstractGoogleClientRequest)httpClientRequest);
    }
    
    public Devices devices() {
        return new Devices();
    }
    
    public Enterprises enterprises() {
        return new Enterprises();
    }
    
    public Entitlements entitlements() {
        return new Entitlements();
    }
    
    public Grouplicenses grouplicenses() {
        return new Grouplicenses();
    }
    
    public Grouplicenseusers grouplicenseusers() {
        return new Grouplicenseusers();
    }
    
    public Installs installs() {
        return new Installs();
    }
    
    public Managedconfigurationsfordevice managedconfigurationsfordevice() {
        return new Managedconfigurationsfordevice();
    }
    
    public Managedconfigurationsforuser managedconfigurationsforuser() {
        return new Managedconfigurationsforuser();
    }
    
    public Managedconfigurationssettings managedconfigurationssettings() {
        return new Managedconfigurationssettings();
    }
    
    public Permissions permissions() {
        return new Permissions();
    }
    
    public Products products() {
        return new Products();
    }
    
    public Serviceaccountkeys serviceaccountkeys() {
        return new Serviceaccountkeys();
    }
    
    public Storelayoutclusters storelayoutclusters() {
        return new Storelayoutclusters();
    }
    
    public Storelayoutpages storelayoutpages() {
        return new Storelayoutpages();
    }
    
    public Users users() {
        return new Users();
    }
    
    public Webapps webapps() {
        return new Webapps();
    }
    
    static {
        Preconditions.checkState(GoogleUtils.MAJOR_VERSION == 1 && (GoogleUtils.MINOR_VERSION >= 32 || (GoogleUtils.MINOR_VERSION == 31 && GoogleUtils.BUGFIX_VERSION >= 1)), "You are currently running with version %s of google-api-client. You need at least version 1.31.1 of google-api-client to run version 1.31.5 of the Google Play EMM API library.", new Object[] { GoogleUtils.VERSION });
    }
    
    public class Devices
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public ForceReportUpload forceReportUpload(final String enterpriseId, final String userId, final String deviceId) throws IOException {
            final ForceReportUpload result = new ForceReportUpload(enterpriseId, userId, deviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId, final String deviceId) throws IOException {
            final Get result = new Get(enterpriseId, userId, deviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetState getState(final String enterpriseId, final String userId, final String deviceId) throws IOException {
            final GetState result = new GetState(enterpriseId, userId, deviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String userId) throws IOException {
            final List result = new List(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SetState setState(final String enterpriseId, final String userId, final String deviceId, final DeviceState content) throws IOException {
            final SetState result = new SetState(enterpriseId, userId, deviceId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final String deviceId, final Device content) throws IOException {
            final Update result = new Update(enterpriseId, userId, deviceId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class ForceReportUpload extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/forceReportUpload";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected ForceReportUpload(final String enterpriseId, final String userId, final String deviceId) {
                super(Devices.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/forceReportUpload", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            @Override
            public ForceReportUpload set$Xgafv(final String $Xgafv) {
                return (ForceReportUpload)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public ForceReportUpload setAccessToken(final String accessToken) {
                return (ForceReportUpload)super.setAccessToken(accessToken);
            }
            
            @Override
            public ForceReportUpload setAlt(final String alt) {
                return (ForceReportUpload)super.setAlt(alt);
            }
            
            @Override
            public ForceReportUpload setCallback(final String callback) {
                return (ForceReportUpload)super.setCallback(callback);
            }
            
            @Override
            public ForceReportUpload setFields(final String fields) {
                return (ForceReportUpload)super.setFields(fields);
            }
            
            @Override
            public ForceReportUpload setKey(final String key) {
                return (ForceReportUpload)super.setKey(key);
            }
            
            @Override
            public ForceReportUpload setOauthToken(final String oauthToken) {
                return (ForceReportUpload)super.setOauthToken(oauthToken);
            }
            
            @Override
            public ForceReportUpload setPrettyPrint(final Boolean prettyPrint) {
                return (ForceReportUpload)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public ForceReportUpload setQuotaUser(final String quotaUser) {
                return (ForceReportUpload)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public ForceReportUpload setUploadType(final String uploadType) {
                return (ForceReportUpload)super.setUploadType(uploadType);
            }
            
            @Override
            public ForceReportUpload setUploadProtocol(final String uploadProtocol) {
                return (ForceReportUpload)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public ForceReportUpload setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public ForceReportUpload setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public ForceReportUpload setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public ForceReportUpload set(final String parameterName, final Object value) {
                return (ForceReportUpload)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<Device>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected Get(final String enterpriseId, final String userId, final String deviceId) {
                super(Devices.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}", null, Device.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Get setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class GetState extends AndroidEnterpriseRequest<DeviceState>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/state";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected GetState(final String enterpriseId, final String userId, final String deviceId) {
                super(Devices.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/state", null, DeviceState.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetState set$Xgafv(final String $Xgafv) {
                return (GetState)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetState setAccessToken(final String accessToken) {
                return (GetState)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetState setAlt(final String alt) {
                return (GetState)super.setAlt(alt);
            }
            
            @Override
            public GetState setCallback(final String callback) {
                return (GetState)super.setCallback(callback);
            }
            
            @Override
            public GetState setFields(final String fields) {
                return (GetState)super.setFields(fields);
            }
            
            @Override
            public GetState setKey(final String key) {
                return (GetState)super.setKey(key);
            }
            
            @Override
            public GetState setOauthToken(final String oauthToken) {
                return (GetState)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetState setPrettyPrint(final Boolean prettyPrint) {
                return (GetState)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetState setQuotaUser(final String quotaUser) {
                return (GetState)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetState setUploadType(final String uploadType) {
                return (GetState)super.setUploadType(uploadType);
            }
            
            @Override
            public GetState setUploadProtocol(final String uploadProtocol) {
                return (GetState)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetState setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public GetState setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public GetState setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public GetState set(final String parameterName, final Object value) {
                return (GetState)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<DevicesListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected List(final String enterpriseId, final String userId) {
                super(Devices.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices", null, DevicesListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public List setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class SetState extends AndroidEnterpriseRequest<DeviceState>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/state";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected SetState(final String enterpriseId, final String userId, final String deviceId, final DeviceState content) {
                super(Devices.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/state", content, DeviceState.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            @Override
            public SetState set$Xgafv(final String $Xgafv) {
                return (SetState)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SetState setAccessToken(final String accessToken) {
                return (SetState)super.setAccessToken(accessToken);
            }
            
            @Override
            public SetState setAlt(final String alt) {
                return (SetState)super.setAlt(alt);
            }
            
            @Override
            public SetState setCallback(final String callback) {
                return (SetState)super.setCallback(callback);
            }
            
            @Override
            public SetState setFields(final String fields) {
                return (SetState)super.setFields(fields);
            }
            
            @Override
            public SetState setKey(final String key) {
                return (SetState)super.setKey(key);
            }
            
            @Override
            public SetState setOauthToken(final String oauthToken) {
                return (SetState)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SetState setPrettyPrint(final Boolean prettyPrint) {
                return (SetState)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SetState setQuotaUser(final String quotaUser) {
                return (SetState)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SetState setUploadType(final String uploadType) {
                return (SetState)super.setUploadType(uploadType);
            }
            
            @Override
            public SetState setUploadProtocol(final String uploadProtocol) {
                return (SetState)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public SetState setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public SetState setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public SetState setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public SetState set(final String parameterName, final Object value) {
                return (SetState)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<Device>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String updateMask;
            
            protected Update(final String enterpriseId, final String userId, final String deviceId, final Device content) {
                super(Devices.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}", content, Device.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Update setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getUpdateMask() {
                return this.updateMask;
            }
            
            public Update setUpdateMask(final String updateMask) {
                this.updateMask = updateMask;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Enterprises
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public AcknowledgeNotificationSet acknowledgeNotificationSet() throws IOException {
            final AcknowledgeNotificationSet result = new AcknowledgeNotificationSet();
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public CompleteSignup completeSignup() throws IOException {
            final CompleteSignup result = new CompleteSignup();
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public CreateWebToken createWebToken(final String enterpriseId, final AdministratorWebTokenSpec content) throws IOException {
            final CreateWebToken result = new CreateWebToken(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Enroll enroll(final String token, final Enterprise content) throws IOException {
            final Enroll result = new Enroll(token, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GenerateSignupUrl generateSignupUrl() throws IOException {
            final GenerateSignupUrl result = new GenerateSignupUrl();
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId) throws IOException {
            final Get result = new Get(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetServiceAccount getServiceAccount(final String enterpriseId) throws IOException {
            final GetServiceAccount result = new GetServiceAccount(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetStoreLayout getStoreLayout(final String enterpriseId) throws IOException {
            final GetStoreLayout result = new GetStoreLayout(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String domain) throws IOException {
            final List result = new List(domain);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public PullNotificationSet pullNotificationSet() throws IOException {
            final PullNotificationSet result = new PullNotificationSet();
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SendTestPushNotification sendTestPushNotification(final String enterpriseId) throws IOException {
            final SendTestPushNotification result = new SendTestPushNotification(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SetAccount setAccount(final String enterpriseId, final EnterpriseAccount content) throws IOException {
            final SetAccount result = new SetAccount(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SetStoreLayout setStoreLayout(final String enterpriseId, final StoreLayout content) throws IOException {
            final SetStoreLayout result = new SetStoreLayout(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Unenroll unenroll(final String enterpriseId) throws IOException {
            final Unenroll result = new Unenroll(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class AcknowledgeNotificationSet extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/acknowledgeNotificationSet";
            @Key
            private String notificationSetId;
            
            protected AcknowledgeNotificationSet() {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/acknowledgeNotificationSet", null, Void.class);
            }
            
            @Override
            public AcknowledgeNotificationSet set$Xgafv(final String $Xgafv) {
                return (AcknowledgeNotificationSet)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public AcknowledgeNotificationSet setAccessToken(final String accessToken) {
                return (AcknowledgeNotificationSet)super.setAccessToken(accessToken);
            }
            
            @Override
            public AcknowledgeNotificationSet setAlt(final String alt) {
                return (AcknowledgeNotificationSet)super.setAlt(alt);
            }
            
            @Override
            public AcknowledgeNotificationSet setCallback(final String callback) {
                return (AcknowledgeNotificationSet)super.setCallback(callback);
            }
            
            @Override
            public AcknowledgeNotificationSet setFields(final String fields) {
                return (AcknowledgeNotificationSet)super.setFields(fields);
            }
            
            @Override
            public AcknowledgeNotificationSet setKey(final String key) {
                return (AcknowledgeNotificationSet)super.setKey(key);
            }
            
            @Override
            public AcknowledgeNotificationSet setOauthToken(final String oauthToken) {
                return (AcknowledgeNotificationSet)super.setOauthToken(oauthToken);
            }
            
            @Override
            public AcknowledgeNotificationSet setPrettyPrint(final Boolean prettyPrint) {
                return (AcknowledgeNotificationSet)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public AcknowledgeNotificationSet setQuotaUser(final String quotaUser) {
                return (AcknowledgeNotificationSet)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public AcknowledgeNotificationSet setUploadType(final String uploadType) {
                return (AcknowledgeNotificationSet)super.setUploadType(uploadType);
            }
            
            @Override
            public AcknowledgeNotificationSet setUploadProtocol(final String uploadProtocol) {
                return (AcknowledgeNotificationSet)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getNotificationSetId() {
                return this.notificationSetId;
            }
            
            public AcknowledgeNotificationSet setNotificationSetId(final String notificationSetId) {
                this.notificationSetId = notificationSetId;
                return this;
            }
            
            @Override
            public AcknowledgeNotificationSet set(final String parameterName, final Object value) {
                return (AcknowledgeNotificationSet)super.set(parameterName, value);
            }
        }
        
        public class CompleteSignup extends AndroidEnterpriseRequest<Enterprise>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/completeSignup";
            @Key
            private String completionToken;
            @Key
            private String enterpriseToken;
            
            protected CompleteSignup() {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/completeSignup", null, Enterprise.class);
            }
            
            @Override
            public CompleteSignup set$Xgafv(final String $Xgafv) {
                return (CompleteSignup)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public CompleteSignup setAccessToken(final String accessToken) {
                return (CompleteSignup)super.setAccessToken(accessToken);
            }
            
            @Override
            public CompleteSignup setAlt(final String alt) {
                return (CompleteSignup)super.setAlt(alt);
            }
            
            @Override
            public CompleteSignup setCallback(final String callback) {
                return (CompleteSignup)super.setCallback(callback);
            }
            
            @Override
            public CompleteSignup setFields(final String fields) {
                return (CompleteSignup)super.setFields(fields);
            }
            
            @Override
            public CompleteSignup setKey(final String key) {
                return (CompleteSignup)super.setKey(key);
            }
            
            @Override
            public CompleteSignup setOauthToken(final String oauthToken) {
                return (CompleteSignup)super.setOauthToken(oauthToken);
            }
            
            @Override
            public CompleteSignup setPrettyPrint(final Boolean prettyPrint) {
                return (CompleteSignup)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public CompleteSignup setQuotaUser(final String quotaUser) {
                return (CompleteSignup)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public CompleteSignup setUploadType(final String uploadType) {
                return (CompleteSignup)super.setUploadType(uploadType);
            }
            
            @Override
            public CompleteSignup setUploadProtocol(final String uploadProtocol) {
                return (CompleteSignup)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCompletionToken() {
                return this.completionToken;
            }
            
            public CompleteSignup setCompletionToken(final String completionToken) {
                this.completionToken = completionToken;
                return this;
            }
            
            public String getEnterpriseToken() {
                return this.enterpriseToken;
            }
            
            public CompleteSignup setEnterpriseToken(final String enterpriseToken) {
                this.enterpriseToken = enterpriseToken;
                return this;
            }
            
            @Override
            public CompleteSignup set(final String parameterName, final Object value) {
                return (CompleteSignup)super.set(parameterName, value);
            }
        }
        
        public class CreateWebToken extends AndroidEnterpriseRequest<AdministratorWebToken>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/createWebToken";
            @Key
            private String enterpriseId;
            
            protected CreateWebToken(final String enterpriseId, final AdministratorWebTokenSpec content) {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/createWebToken", content, AdministratorWebToken.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public CreateWebToken set$Xgafv(final String $Xgafv) {
                return (CreateWebToken)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public CreateWebToken setAccessToken(final String accessToken) {
                return (CreateWebToken)super.setAccessToken(accessToken);
            }
            
            @Override
            public CreateWebToken setAlt(final String alt) {
                return (CreateWebToken)super.setAlt(alt);
            }
            
            @Override
            public CreateWebToken setCallback(final String callback) {
                return (CreateWebToken)super.setCallback(callback);
            }
            
            @Override
            public CreateWebToken setFields(final String fields) {
                return (CreateWebToken)super.setFields(fields);
            }
            
            @Override
            public CreateWebToken setKey(final String key) {
                return (CreateWebToken)super.setKey(key);
            }
            
            @Override
            public CreateWebToken setOauthToken(final String oauthToken) {
                return (CreateWebToken)super.setOauthToken(oauthToken);
            }
            
            @Override
            public CreateWebToken setPrettyPrint(final Boolean prettyPrint) {
                return (CreateWebToken)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public CreateWebToken setQuotaUser(final String quotaUser) {
                return (CreateWebToken)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public CreateWebToken setUploadType(final String uploadType) {
                return (CreateWebToken)super.setUploadType(uploadType);
            }
            
            @Override
            public CreateWebToken setUploadProtocol(final String uploadProtocol) {
                return (CreateWebToken)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public CreateWebToken setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public CreateWebToken set(final String parameterName, final Object value) {
                return (CreateWebToken)super.set(parameterName, value);
            }
        }
        
        public class Enroll extends AndroidEnterpriseRequest<Enterprise>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/enroll";
            @Key
            private String token;
            
            protected Enroll(final String token, final Enterprise content) {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/enroll", content, Enterprise.class);
                this.token = (String)Preconditions.checkNotNull((Object)token, (Object)"Required parameter token must be specified.");
            }
            
            @Override
            public Enroll set$Xgafv(final String $Xgafv) {
                return (Enroll)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Enroll setAccessToken(final String accessToken) {
                return (Enroll)super.setAccessToken(accessToken);
            }
            
            @Override
            public Enroll setAlt(final String alt) {
                return (Enroll)super.setAlt(alt);
            }
            
            @Override
            public Enroll setCallback(final String callback) {
                return (Enroll)super.setCallback(callback);
            }
            
            @Override
            public Enroll setFields(final String fields) {
                return (Enroll)super.setFields(fields);
            }
            
            @Override
            public Enroll setKey(final String key) {
                return (Enroll)super.setKey(key);
            }
            
            @Override
            public Enroll setOauthToken(final String oauthToken) {
                return (Enroll)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Enroll setPrettyPrint(final Boolean prettyPrint) {
                return (Enroll)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Enroll setQuotaUser(final String quotaUser) {
                return (Enroll)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Enroll setUploadType(final String uploadType) {
                return (Enroll)super.setUploadType(uploadType);
            }
            
            @Override
            public Enroll setUploadProtocol(final String uploadProtocol) {
                return (Enroll)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getToken() {
                return this.token;
            }
            
            public Enroll setToken(final String token) {
                this.token = token;
                return this;
            }
            
            @Override
            public Enroll set(final String parameterName, final Object value) {
                return (Enroll)super.set(parameterName, value);
            }
        }
        
        public class GenerateSignupUrl extends AndroidEnterpriseRequest<SignupInfo>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/signupUrl";
            @Key
            private String callbackUrl;
            
            protected GenerateSignupUrl() {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/signupUrl", null, SignupInfo.class);
            }
            
            @Override
            public GenerateSignupUrl set$Xgafv(final String $Xgafv) {
                return (GenerateSignupUrl)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GenerateSignupUrl setAccessToken(final String accessToken) {
                return (GenerateSignupUrl)super.setAccessToken(accessToken);
            }
            
            @Override
            public GenerateSignupUrl setAlt(final String alt) {
                return (GenerateSignupUrl)super.setAlt(alt);
            }
            
            @Override
            public GenerateSignupUrl setCallback(final String callback) {
                return (GenerateSignupUrl)super.setCallback(callback);
            }
            
            @Override
            public GenerateSignupUrl setFields(final String fields) {
                return (GenerateSignupUrl)super.setFields(fields);
            }
            
            @Override
            public GenerateSignupUrl setKey(final String key) {
                return (GenerateSignupUrl)super.setKey(key);
            }
            
            @Override
            public GenerateSignupUrl setOauthToken(final String oauthToken) {
                return (GenerateSignupUrl)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GenerateSignupUrl setPrettyPrint(final Boolean prettyPrint) {
                return (GenerateSignupUrl)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GenerateSignupUrl setQuotaUser(final String quotaUser) {
                return (GenerateSignupUrl)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GenerateSignupUrl setUploadType(final String uploadType) {
                return (GenerateSignupUrl)super.setUploadType(uploadType);
            }
            
            @Override
            public GenerateSignupUrl setUploadProtocol(final String uploadProtocol) {
                return (GenerateSignupUrl)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCallbackUrl() {
                return this.callbackUrl;
            }
            
            public GenerateSignupUrl setCallbackUrl(final String callbackUrl) {
                this.callbackUrl = callbackUrl;
                return this;
            }
            
            @Override
            public GenerateSignupUrl set(final String parameterName, final Object value) {
                return (GenerateSignupUrl)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<Enterprise>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}";
            @Key
            private String enterpriseId;
            
            protected Get(final String enterpriseId) {
                super(Enterprises.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}", null, Enterprise.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class GetServiceAccount extends AndroidEnterpriseRequest<ServiceAccount>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccount";
            @Key
            private String enterpriseId;
            @Key
            private String keyType;
            
            protected GetServiceAccount(final String enterpriseId) {
                super(Enterprises.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccount", null, ServiceAccount.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetServiceAccount set$Xgafv(final String $Xgafv) {
                return (GetServiceAccount)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetServiceAccount setAccessToken(final String accessToken) {
                return (GetServiceAccount)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetServiceAccount setAlt(final String alt) {
                return (GetServiceAccount)super.setAlt(alt);
            }
            
            @Override
            public GetServiceAccount setCallback(final String callback) {
                return (GetServiceAccount)super.setCallback(callback);
            }
            
            @Override
            public GetServiceAccount setFields(final String fields) {
                return (GetServiceAccount)super.setFields(fields);
            }
            
            @Override
            public GetServiceAccount setKey(final String key) {
                return (GetServiceAccount)super.setKey(key);
            }
            
            @Override
            public GetServiceAccount setOauthToken(final String oauthToken) {
                return (GetServiceAccount)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetServiceAccount setPrettyPrint(final Boolean prettyPrint) {
                return (GetServiceAccount)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetServiceAccount setQuotaUser(final String quotaUser) {
                return (GetServiceAccount)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetServiceAccount setUploadType(final String uploadType) {
                return (GetServiceAccount)super.setUploadType(uploadType);
            }
            
            @Override
            public GetServiceAccount setUploadProtocol(final String uploadProtocol) {
                return (GetServiceAccount)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetServiceAccount setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getKeyType() {
                return this.keyType;
            }
            
            public GetServiceAccount setKeyType(final String keyType) {
                this.keyType = keyType;
                return this;
            }
            
            @Override
            public GetServiceAccount set(final String parameterName, final Object value) {
                return (GetServiceAccount)super.set(parameterName, value);
            }
        }
        
        public class GetStoreLayout extends AndroidEnterpriseRequest<StoreLayout>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout";
            @Key
            private String enterpriseId;
            
            protected GetStoreLayout(final String enterpriseId) {
                super(Enterprises.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout", null, StoreLayout.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetStoreLayout set$Xgafv(final String $Xgafv) {
                return (GetStoreLayout)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetStoreLayout setAccessToken(final String accessToken) {
                return (GetStoreLayout)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetStoreLayout setAlt(final String alt) {
                return (GetStoreLayout)super.setAlt(alt);
            }
            
            @Override
            public GetStoreLayout setCallback(final String callback) {
                return (GetStoreLayout)super.setCallback(callback);
            }
            
            @Override
            public GetStoreLayout setFields(final String fields) {
                return (GetStoreLayout)super.setFields(fields);
            }
            
            @Override
            public GetStoreLayout setKey(final String key) {
                return (GetStoreLayout)super.setKey(key);
            }
            
            @Override
            public GetStoreLayout setOauthToken(final String oauthToken) {
                return (GetStoreLayout)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetStoreLayout setPrettyPrint(final Boolean prettyPrint) {
                return (GetStoreLayout)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetStoreLayout setQuotaUser(final String quotaUser) {
                return (GetStoreLayout)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetStoreLayout setUploadType(final String uploadType) {
                return (GetStoreLayout)super.setUploadType(uploadType);
            }
            
            @Override
            public GetStoreLayout setUploadProtocol(final String uploadProtocol) {
                return (GetStoreLayout)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetStoreLayout setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public GetStoreLayout set(final String parameterName, final Object value) {
                return (GetStoreLayout)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<EnterprisesListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises";
            @Key
            private String domain;
            
            protected List(final String domain) {
                super(Enterprises.this.this$0, "GET", "androidenterprise/v1/enterprises", null, EnterprisesListResponse.class);
                this.domain = (String)Preconditions.checkNotNull((Object)domain, (Object)"Required parameter domain must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getDomain() {
                return this.domain;
            }
            
            public List setDomain(final String domain) {
                this.domain = domain;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class PullNotificationSet extends AndroidEnterpriseRequest<NotificationSet>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/pullNotificationSet";
            @Key
            private String requestMode;
            
            protected PullNotificationSet() {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/pullNotificationSet", null, NotificationSet.class);
            }
            
            @Override
            public PullNotificationSet set$Xgafv(final String $Xgafv) {
                return (PullNotificationSet)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public PullNotificationSet setAccessToken(final String accessToken) {
                return (PullNotificationSet)super.setAccessToken(accessToken);
            }
            
            @Override
            public PullNotificationSet setAlt(final String alt) {
                return (PullNotificationSet)super.setAlt(alt);
            }
            
            @Override
            public PullNotificationSet setCallback(final String callback) {
                return (PullNotificationSet)super.setCallback(callback);
            }
            
            @Override
            public PullNotificationSet setFields(final String fields) {
                return (PullNotificationSet)super.setFields(fields);
            }
            
            @Override
            public PullNotificationSet setKey(final String key) {
                return (PullNotificationSet)super.setKey(key);
            }
            
            @Override
            public PullNotificationSet setOauthToken(final String oauthToken) {
                return (PullNotificationSet)super.setOauthToken(oauthToken);
            }
            
            @Override
            public PullNotificationSet setPrettyPrint(final Boolean prettyPrint) {
                return (PullNotificationSet)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public PullNotificationSet setQuotaUser(final String quotaUser) {
                return (PullNotificationSet)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public PullNotificationSet setUploadType(final String uploadType) {
                return (PullNotificationSet)super.setUploadType(uploadType);
            }
            
            @Override
            public PullNotificationSet setUploadProtocol(final String uploadProtocol) {
                return (PullNotificationSet)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getRequestMode() {
                return this.requestMode;
            }
            
            public PullNotificationSet setRequestMode(final String requestMode) {
                this.requestMode = requestMode;
                return this;
            }
            
            @Override
            public PullNotificationSet set(final String parameterName, final Object value) {
                return (PullNotificationSet)super.set(parameterName, value);
            }
        }
        
        public class SendTestPushNotification extends AndroidEnterpriseRequest<EnterprisesSendTestPushNotificationResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/sendTestPushNotification";
            @Key
            private String enterpriseId;
            
            protected SendTestPushNotification(final String enterpriseId) {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/sendTestPushNotification", null, EnterprisesSendTestPushNotificationResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public SendTestPushNotification set$Xgafv(final String $Xgafv) {
                return (SendTestPushNotification)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SendTestPushNotification setAccessToken(final String accessToken) {
                return (SendTestPushNotification)super.setAccessToken(accessToken);
            }
            
            @Override
            public SendTestPushNotification setAlt(final String alt) {
                return (SendTestPushNotification)super.setAlt(alt);
            }
            
            @Override
            public SendTestPushNotification setCallback(final String callback) {
                return (SendTestPushNotification)super.setCallback(callback);
            }
            
            @Override
            public SendTestPushNotification setFields(final String fields) {
                return (SendTestPushNotification)super.setFields(fields);
            }
            
            @Override
            public SendTestPushNotification setKey(final String key) {
                return (SendTestPushNotification)super.setKey(key);
            }
            
            @Override
            public SendTestPushNotification setOauthToken(final String oauthToken) {
                return (SendTestPushNotification)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SendTestPushNotification setPrettyPrint(final Boolean prettyPrint) {
                return (SendTestPushNotification)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SendTestPushNotification setQuotaUser(final String quotaUser) {
                return (SendTestPushNotification)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SendTestPushNotification setUploadType(final String uploadType) {
                return (SendTestPushNotification)super.setUploadType(uploadType);
            }
            
            @Override
            public SendTestPushNotification setUploadProtocol(final String uploadProtocol) {
                return (SendTestPushNotification)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public SendTestPushNotification setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public SendTestPushNotification set(final String parameterName, final Object value) {
                return (SendTestPushNotification)super.set(parameterName, value);
            }
        }
        
        public class SetAccount extends AndroidEnterpriseRequest<EnterpriseAccount>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/account";
            @Key
            private String enterpriseId;
            
            protected SetAccount(final String enterpriseId, final EnterpriseAccount content) {
                super(Enterprises.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/account", content, EnterpriseAccount.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public SetAccount set$Xgafv(final String $Xgafv) {
                return (SetAccount)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SetAccount setAccessToken(final String accessToken) {
                return (SetAccount)super.setAccessToken(accessToken);
            }
            
            @Override
            public SetAccount setAlt(final String alt) {
                return (SetAccount)super.setAlt(alt);
            }
            
            @Override
            public SetAccount setCallback(final String callback) {
                return (SetAccount)super.setCallback(callback);
            }
            
            @Override
            public SetAccount setFields(final String fields) {
                return (SetAccount)super.setFields(fields);
            }
            
            @Override
            public SetAccount setKey(final String key) {
                return (SetAccount)super.setKey(key);
            }
            
            @Override
            public SetAccount setOauthToken(final String oauthToken) {
                return (SetAccount)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SetAccount setPrettyPrint(final Boolean prettyPrint) {
                return (SetAccount)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SetAccount setQuotaUser(final String quotaUser) {
                return (SetAccount)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SetAccount setUploadType(final String uploadType) {
                return (SetAccount)super.setUploadType(uploadType);
            }
            
            @Override
            public SetAccount setUploadProtocol(final String uploadProtocol) {
                return (SetAccount)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public SetAccount setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public SetAccount set(final String parameterName, final Object value) {
                return (SetAccount)super.set(parameterName, value);
            }
        }
        
        public class SetStoreLayout extends AndroidEnterpriseRequest<StoreLayout>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout";
            @Key
            private String enterpriseId;
            
            protected SetStoreLayout(final String enterpriseId, final StoreLayout content) {
                super(Enterprises.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout", content, StoreLayout.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public SetStoreLayout set$Xgafv(final String $Xgafv) {
                return (SetStoreLayout)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SetStoreLayout setAccessToken(final String accessToken) {
                return (SetStoreLayout)super.setAccessToken(accessToken);
            }
            
            @Override
            public SetStoreLayout setAlt(final String alt) {
                return (SetStoreLayout)super.setAlt(alt);
            }
            
            @Override
            public SetStoreLayout setCallback(final String callback) {
                return (SetStoreLayout)super.setCallback(callback);
            }
            
            @Override
            public SetStoreLayout setFields(final String fields) {
                return (SetStoreLayout)super.setFields(fields);
            }
            
            @Override
            public SetStoreLayout setKey(final String key) {
                return (SetStoreLayout)super.setKey(key);
            }
            
            @Override
            public SetStoreLayout setOauthToken(final String oauthToken) {
                return (SetStoreLayout)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SetStoreLayout setPrettyPrint(final Boolean prettyPrint) {
                return (SetStoreLayout)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SetStoreLayout setQuotaUser(final String quotaUser) {
                return (SetStoreLayout)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SetStoreLayout setUploadType(final String uploadType) {
                return (SetStoreLayout)super.setUploadType(uploadType);
            }
            
            @Override
            public SetStoreLayout setUploadProtocol(final String uploadProtocol) {
                return (SetStoreLayout)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public SetStoreLayout setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public SetStoreLayout set(final String parameterName, final Object value) {
                return (SetStoreLayout)super.set(parameterName, value);
            }
        }
        
        public class Unenroll extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/unenroll";
            @Key
            private String enterpriseId;
            
            protected Unenroll(final String enterpriseId) {
                super(Enterprises.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/unenroll", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public Unenroll set$Xgafv(final String $Xgafv) {
                return (Unenroll)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Unenroll setAccessToken(final String accessToken) {
                return (Unenroll)super.setAccessToken(accessToken);
            }
            
            @Override
            public Unenroll setAlt(final String alt) {
                return (Unenroll)super.setAlt(alt);
            }
            
            @Override
            public Unenroll setCallback(final String callback) {
                return (Unenroll)super.setCallback(callback);
            }
            
            @Override
            public Unenroll setFields(final String fields) {
                return (Unenroll)super.setFields(fields);
            }
            
            @Override
            public Unenroll setKey(final String key) {
                return (Unenroll)super.setKey(key);
            }
            
            @Override
            public Unenroll setOauthToken(final String oauthToken) {
                return (Unenroll)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Unenroll setPrettyPrint(final Boolean prettyPrint) {
                return (Unenroll)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Unenroll setQuotaUser(final String quotaUser) {
                return (Unenroll)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Unenroll setUploadType(final String uploadType) {
                return (Unenroll)super.setUploadType(uploadType);
            }
            
            @Override
            public Unenroll setUploadProtocol(final String uploadProtocol) {
                return (Unenroll)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Unenroll setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Unenroll set(final String parameterName, final Object value) {
                return (Unenroll)super.set(parameterName, value);
            }
        }
    }
    
    public class Entitlements
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String userId, final String entitlementId) throws IOException {
            final Delete result = new Delete(enterpriseId, userId, entitlementId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId, final String entitlementId) throws IOException {
            final Get result = new Get(enterpriseId, userId, entitlementId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String userId) throws IOException {
            final List result = new List(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final String entitlementId, final Entitlement content) throws IOException {
            final Update result = new Update(enterpriseId, userId, entitlementId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String entitlementId;
            
            protected Delete(final String enterpriseId, final String userId, final String entitlementId) {
                super(Entitlements.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.entitlementId = (String)Preconditions.checkNotNull((Object)entitlementId, (Object)"Required parameter entitlementId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Delete setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getEntitlementId() {
                return this.entitlementId;
            }
            
            public Delete setEntitlementId(final String entitlementId) {
                this.entitlementId = entitlementId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<Entitlement>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String entitlementId;
            
            protected Get(final String enterpriseId, final String userId, final String entitlementId) {
                super(Entitlements.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}", null, Entitlement.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.entitlementId = (String)Preconditions.checkNotNull((Object)entitlementId, (Object)"Required parameter entitlementId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getEntitlementId() {
                return this.entitlementId;
            }
            
            public Get setEntitlementId(final String entitlementId) {
                this.entitlementId = entitlementId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<EntitlementsListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected List(final String enterpriseId, final String userId) {
                super(Entitlements.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements", null, EntitlementsListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public List setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<Entitlement>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String entitlementId;
            @Key
            private Boolean install;
            
            protected Update(final String enterpriseId, final String userId, final String entitlementId, final Entitlement content) {
                super(Entitlements.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/entitlements/{entitlementId}", content, Entitlement.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.entitlementId = (String)Preconditions.checkNotNull((Object)entitlementId, (Object)"Required parameter entitlementId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getEntitlementId() {
                return this.entitlementId;
            }
            
            public Update setEntitlementId(final String entitlementId) {
                this.entitlementId = entitlementId;
                return this;
            }
            
            public Boolean getInstall() {
                return this.install;
            }
            
            public Update setInstall(final Boolean install) {
                this.install = install;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Grouplicenses
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Get get(final String enterpriseId, final String groupLicenseId) throws IOException {
            final Get result = new Get(enterpriseId, groupLicenseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId) throws IOException {
            final List result = new List(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Get extends AndroidEnterpriseRequest<GroupLicense>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses/{groupLicenseId}";
            @Key
            private String enterpriseId;
            @Key
            private String groupLicenseId;
            
            protected Get(final String enterpriseId, final String groupLicenseId) {
                super(Grouplicenses.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses/{groupLicenseId}", null, GroupLicense.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.groupLicenseId = (String)Preconditions.checkNotNull((Object)groupLicenseId, (Object)"Required parameter groupLicenseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getGroupLicenseId() {
                return this.groupLicenseId;
            }
            
            public Get setGroupLicenseId(final String groupLicenseId) {
                this.groupLicenseId = groupLicenseId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<GroupLicensesListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses";
            @Key
            private String enterpriseId;
            
            protected List(final String enterpriseId) {
                super(Grouplicenses.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses", null, GroupLicensesListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Grouplicenseusers
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public List list(final String enterpriseId, final String groupLicenseId) throws IOException {
            final List result = new List(enterpriseId, groupLicenseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class List extends AndroidEnterpriseRequest<GroupLicenseUsersListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses/{groupLicenseId}/users";
            @Key
            private String enterpriseId;
            @Key
            private String groupLicenseId;
            
            protected List(final String enterpriseId, final String groupLicenseId) {
                super(Grouplicenseusers.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/groupLicenses/{groupLicenseId}/users", null, GroupLicenseUsersListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.groupLicenseId = (String)Preconditions.checkNotNull((Object)groupLicenseId, (Object)"Required parameter groupLicenseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getGroupLicenseId() {
                return this.groupLicenseId;
            }
            
            public List setGroupLicenseId(final String groupLicenseId) {
                this.groupLicenseId = groupLicenseId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Installs
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String userId, final String deviceId, final String installId) throws IOException {
            final Delete result = new Delete(enterpriseId, userId, deviceId, installId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId, final String deviceId, final String installId) throws IOException {
            final Get result = new Get(enterpriseId, userId, deviceId, installId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String userId, final String deviceId) throws IOException {
            final List result = new List(enterpriseId, userId, deviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final String deviceId, final String installId, final Install content) throws IOException {
            final Update result = new Update(enterpriseId, userId, deviceId, installId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String installId;
            
            protected Delete(final String enterpriseId, final String userId, final String deviceId, final String installId) {
                super(Installs.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.installId = (String)Preconditions.checkNotNull((Object)installId, (Object)"Required parameter installId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Delete setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Delete setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getInstallId() {
                return this.installId;
            }
            
            public Delete setInstallId(final String installId) {
                this.installId = installId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<Install>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String installId;
            
            protected Get(final String enterpriseId, final String userId, final String deviceId, final String installId) {
                super(Installs.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}", null, Install.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.installId = (String)Preconditions.checkNotNull((Object)installId, (Object)"Required parameter installId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Get setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getInstallId() {
                return this.installId;
            }
            
            public Get setInstallId(final String installId) {
                this.installId = installId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<InstallsListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected List(final String enterpriseId, final String userId, final String deviceId) {
                super(Installs.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs", null, InstallsListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public List setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public List setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<Install>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String installId;
            
            protected Update(final String enterpriseId, final String userId, final String deviceId, final String installId, final Install content) {
                super(Installs.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/installs/{installId}", content, Install.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.installId = (String)Preconditions.checkNotNull((Object)installId, (Object)"Required parameter installId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Update setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getInstallId() {
                return this.installId;
            }
            
            public Update setInstallId(final String installId) {
                this.installId = installId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Managedconfigurationsfordevice
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId) throws IOException {
            final Delete result = new Delete(enterpriseId, userId, deviceId, managedConfigurationForDeviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId) throws IOException {
            final Get result = new Get(enterpriseId, userId, deviceId, managedConfigurationForDeviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String userId, final String deviceId) throws IOException {
            final List result = new List(enterpriseId, userId, deviceId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId, final ManagedConfiguration content) throws IOException {
            final Update result = new Update(enterpriseId, userId, deviceId, managedConfigurationForDeviceId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String managedConfigurationForDeviceId;
            
            protected Delete(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId) {
                super(Managedconfigurationsfordevice.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.managedConfigurationForDeviceId = (String)Preconditions.checkNotNull((Object)managedConfigurationForDeviceId, (Object)"Required parameter managedConfigurationForDeviceId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Delete setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Delete setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getManagedConfigurationForDeviceId() {
                return this.managedConfigurationForDeviceId;
            }
            
            public Delete setManagedConfigurationForDeviceId(final String managedConfigurationForDeviceId) {
                this.managedConfigurationForDeviceId = managedConfigurationForDeviceId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<ManagedConfiguration>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String managedConfigurationForDeviceId;
            
            protected Get(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId) {
                super(Managedconfigurationsfordevice.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}", null, ManagedConfiguration.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.managedConfigurationForDeviceId = (String)Preconditions.checkNotNull((Object)managedConfigurationForDeviceId, (Object)"Required parameter managedConfigurationForDeviceId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Get setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getManagedConfigurationForDeviceId() {
                return this.managedConfigurationForDeviceId;
            }
            
            public Get setManagedConfigurationForDeviceId(final String managedConfigurationForDeviceId) {
                this.managedConfigurationForDeviceId = managedConfigurationForDeviceId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<ManagedConfigurationsForDeviceListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            
            protected List(final String enterpriseId, final String userId, final String deviceId) {
                super(Managedconfigurationsfordevice.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice", null, ManagedConfigurationsForDeviceListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public List setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public List setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<ManagedConfiguration>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String deviceId;
            @Key
            private String managedConfigurationForDeviceId;
            
            protected Update(final String enterpriseId, final String userId, final String deviceId, final String managedConfigurationForDeviceId, final ManagedConfiguration content) {
                super(Managedconfigurationsfordevice.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/devices/{deviceId}/managedConfigurationsForDevice/{managedConfigurationForDeviceId}", content, ManagedConfiguration.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                this.managedConfigurationForDeviceId = (String)Preconditions.checkNotNull((Object)managedConfigurationForDeviceId, (Object)"Required parameter managedConfigurationForDeviceId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Update setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getManagedConfigurationForDeviceId() {
                return this.managedConfigurationForDeviceId;
            }
            
            public Update setManagedConfigurationForDeviceId(final String managedConfigurationForDeviceId) {
                this.managedConfigurationForDeviceId = managedConfigurationForDeviceId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Managedconfigurationsforuser
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String userId, final String managedConfigurationForUserId) throws IOException {
            final Delete result = new Delete(enterpriseId, userId, managedConfigurationForUserId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId, final String managedConfigurationForUserId) throws IOException {
            final Get result = new Get(enterpriseId, userId, managedConfigurationForUserId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String userId) throws IOException {
            final List result = new List(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final String managedConfigurationForUserId, final ManagedConfiguration content) throws IOException {
            final Update result = new Update(enterpriseId, userId, managedConfigurationForUserId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String managedConfigurationForUserId;
            
            protected Delete(final String enterpriseId, final String userId, final String managedConfigurationForUserId) {
                super(Managedconfigurationsforuser.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.managedConfigurationForUserId = (String)Preconditions.checkNotNull((Object)managedConfigurationForUserId, (Object)"Required parameter managedConfigurationForUserId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Delete setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getManagedConfigurationForUserId() {
                return this.managedConfigurationForUserId;
            }
            
            public Delete setManagedConfigurationForUserId(final String managedConfigurationForUserId) {
                this.managedConfigurationForUserId = managedConfigurationForUserId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<ManagedConfiguration>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String managedConfigurationForUserId;
            
            protected Get(final String enterpriseId, final String userId, final String managedConfigurationForUserId) {
                super(Managedconfigurationsforuser.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}", null, ManagedConfiguration.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.managedConfigurationForUserId = (String)Preconditions.checkNotNull((Object)managedConfigurationForUserId, (Object)"Required parameter managedConfigurationForUserId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getManagedConfigurationForUserId() {
                return this.managedConfigurationForUserId;
            }
            
            public Get setManagedConfigurationForUserId(final String managedConfigurationForUserId) {
                this.managedConfigurationForUserId = managedConfigurationForUserId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<ManagedConfigurationsForUserListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected List(final String enterpriseId, final String userId) {
                super(Managedconfigurationsforuser.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser", null, ManagedConfigurationsForUserListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public List setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<ManagedConfiguration>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            @Key
            private String managedConfigurationForUserId;
            
            protected Update(final String enterpriseId, final String userId, final String managedConfigurationForUserId, final ManagedConfiguration content) {
                super(Managedconfigurationsforuser.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/managedConfigurationsForUser/{managedConfigurationForUserId}", content, ManagedConfiguration.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
                this.managedConfigurationForUserId = (String)Preconditions.checkNotNull((Object)managedConfigurationForUserId, (Object)"Required parameter managedConfigurationForUserId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            public String getManagedConfigurationForUserId() {
                return this.managedConfigurationForUserId;
            }
            
            public Update setManagedConfigurationForUserId(final String managedConfigurationForUserId) {
                this.managedConfigurationForUserId = managedConfigurationForUserId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Managedconfigurationssettings
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public List list(final String enterpriseId, final String productId) throws IOException {
            final List result = new List(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class List extends AndroidEnterpriseRequest<ManagedConfigurationsSettingsListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/managedConfigurationsSettings";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            
            protected List(final String enterpriseId, final String productId) {
                super(Managedconfigurationssettings.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/managedConfigurationsSettings", null, ManagedConfigurationsSettingsListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public List setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Permissions
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Get get(final String permissionId) throws IOException {
            final Get result = new Get(permissionId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Get extends AndroidEnterpriseRequest<Permission>
        {
            private static final String REST_PATH = "androidenterprise/v1/permissions/{permissionId}";
            @Key
            private String permissionId;
            @Key
            private String language;
            
            protected Get(final String permissionId) {
                super(Permissions.this.this$0, "GET", "androidenterprise/v1/permissions/{permissionId}", null, Permission.class);
                this.permissionId = (String)Preconditions.checkNotNull((Object)permissionId, (Object)"Required parameter permissionId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getPermissionId() {
                return this.permissionId;
            }
            
            public Get setPermissionId(final String permissionId) {
                this.permissionId = permissionId;
                return this;
            }
            
            public String getLanguage() {
                return this.language;
            }
            
            public Get setLanguage(final String language) {
                this.language = language;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
    }
    
    public class Products
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Approve approve(final String enterpriseId, final String productId, final ProductsApproveRequest content) throws IOException {
            final Approve result = new Approve(enterpriseId, productId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GenerateApprovalUrl generateApprovalUrl(final String enterpriseId, final String productId) throws IOException {
            final GenerateApprovalUrl result = new GenerateApprovalUrl(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String productId) throws IOException {
            final Get result = new Get(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetAppRestrictionsSchema getAppRestrictionsSchema(final String enterpriseId, final String productId) throws IOException {
            final GetAppRestrictionsSchema result = new GetAppRestrictionsSchema(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetPermissions getPermissions(final String enterpriseId, final String productId) throws IOException {
            final GetPermissions result = new GetPermissions(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId) throws IOException {
            final List result = new List(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Unapprove unapprove(final String enterpriseId, final String productId) throws IOException {
            final Unapprove result = new Unapprove(enterpriseId, productId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Approve extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/approve";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            
            protected Approve(final String enterpriseId, final String productId, final ProductsApproveRequest content) {
                super(Products.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/approve", content, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            @Override
            public Approve set$Xgafv(final String $Xgafv) {
                return (Approve)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Approve setAccessToken(final String accessToken) {
                return (Approve)super.setAccessToken(accessToken);
            }
            
            @Override
            public Approve setAlt(final String alt) {
                return (Approve)super.setAlt(alt);
            }
            
            @Override
            public Approve setCallback(final String callback) {
                return (Approve)super.setCallback(callback);
            }
            
            @Override
            public Approve setFields(final String fields) {
                return (Approve)super.setFields(fields);
            }
            
            @Override
            public Approve setKey(final String key) {
                return (Approve)super.setKey(key);
            }
            
            @Override
            public Approve setOauthToken(final String oauthToken) {
                return (Approve)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Approve setPrettyPrint(final Boolean prettyPrint) {
                return (Approve)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Approve setQuotaUser(final String quotaUser) {
                return (Approve)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Approve setUploadType(final String uploadType) {
                return (Approve)super.setUploadType(uploadType);
            }
            
            @Override
            public Approve setUploadProtocol(final String uploadProtocol) {
                return (Approve)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Approve setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public Approve setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            @Override
            public Approve set(final String parameterName, final Object value) {
                return (Approve)super.set(parameterName, value);
            }
        }
        
        public class GenerateApprovalUrl extends AndroidEnterpriseRequest<ProductsGenerateApprovalUrlResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/generateApprovalUrl";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            @Key
            private String languageCode;
            
            protected GenerateApprovalUrl(final String enterpriseId, final String productId) {
                super(Products.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/generateApprovalUrl", null, ProductsGenerateApprovalUrlResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            @Override
            public GenerateApprovalUrl set$Xgafv(final String $Xgafv) {
                return (GenerateApprovalUrl)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GenerateApprovalUrl setAccessToken(final String accessToken) {
                return (GenerateApprovalUrl)super.setAccessToken(accessToken);
            }
            
            @Override
            public GenerateApprovalUrl setAlt(final String alt) {
                return (GenerateApprovalUrl)super.setAlt(alt);
            }
            
            @Override
            public GenerateApprovalUrl setCallback(final String callback) {
                return (GenerateApprovalUrl)super.setCallback(callback);
            }
            
            @Override
            public GenerateApprovalUrl setFields(final String fields) {
                return (GenerateApprovalUrl)super.setFields(fields);
            }
            
            @Override
            public GenerateApprovalUrl setKey(final String key) {
                return (GenerateApprovalUrl)super.setKey(key);
            }
            
            @Override
            public GenerateApprovalUrl setOauthToken(final String oauthToken) {
                return (GenerateApprovalUrl)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GenerateApprovalUrl setPrettyPrint(final Boolean prettyPrint) {
                return (GenerateApprovalUrl)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GenerateApprovalUrl setQuotaUser(final String quotaUser) {
                return (GenerateApprovalUrl)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GenerateApprovalUrl setUploadType(final String uploadType) {
                return (GenerateApprovalUrl)super.setUploadType(uploadType);
            }
            
            @Override
            public GenerateApprovalUrl setUploadProtocol(final String uploadProtocol) {
                return (GenerateApprovalUrl)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GenerateApprovalUrl setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public GenerateApprovalUrl setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            public String getLanguageCode() {
                return this.languageCode;
            }
            
            public GenerateApprovalUrl setLanguageCode(final String languageCode) {
                this.languageCode = languageCode;
                return this;
            }
            
            @Override
            public GenerateApprovalUrl set(final String parameterName, final Object value) {
                return (GenerateApprovalUrl)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<Product>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            @Key
            private String language;
            
            protected Get(final String enterpriseId, final String productId) {
                super(Products.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}", null, Product.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public Get setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            public String getLanguage() {
                return this.language;
            }
            
            public Get setLanguage(final String language) {
                this.language = language;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class GetAppRestrictionsSchema extends AndroidEnterpriseRequest<AppRestrictionsSchema>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/appRestrictionsSchema";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            @Key
            private String language;
            
            protected GetAppRestrictionsSchema(final String enterpriseId, final String productId) {
                super(Products.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/appRestrictionsSchema", null, AppRestrictionsSchema.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetAppRestrictionsSchema set$Xgafv(final String $Xgafv) {
                return (GetAppRestrictionsSchema)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetAppRestrictionsSchema setAccessToken(final String accessToken) {
                return (GetAppRestrictionsSchema)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetAppRestrictionsSchema setAlt(final String alt) {
                return (GetAppRestrictionsSchema)super.setAlt(alt);
            }
            
            @Override
            public GetAppRestrictionsSchema setCallback(final String callback) {
                return (GetAppRestrictionsSchema)super.setCallback(callback);
            }
            
            @Override
            public GetAppRestrictionsSchema setFields(final String fields) {
                return (GetAppRestrictionsSchema)super.setFields(fields);
            }
            
            @Override
            public GetAppRestrictionsSchema setKey(final String key) {
                return (GetAppRestrictionsSchema)super.setKey(key);
            }
            
            @Override
            public GetAppRestrictionsSchema setOauthToken(final String oauthToken) {
                return (GetAppRestrictionsSchema)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetAppRestrictionsSchema setPrettyPrint(final Boolean prettyPrint) {
                return (GetAppRestrictionsSchema)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetAppRestrictionsSchema setQuotaUser(final String quotaUser) {
                return (GetAppRestrictionsSchema)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetAppRestrictionsSchema setUploadType(final String uploadType) {
                return (GetAppRestrictionsSchema)super.setUploadType(uploadType);
            }
            
            @Override
            public GetAppRestrictionsSchema setUploadProtocol(final String uploadProtocol) {
                return (GetAppRestrictionsSchema)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetAppRestrictionsSchema setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public GetAppRestrictionsSchema setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            public String getLanguage() {
                return this.language;
            }
            
            public GetAppRestrictionsSchema setLanguage(final String language) {
                this.language = language;
                return this;
            }
            
            @Override
            public GetAppRestrictionsSchema set(final String parameterName, final Object value) {
                return (GetAppRestrictionsSchema)super.set(parameterName, value);
            }
        }
        
        public class GetPermissions extends AndroidEnterpriseRequest<ProductPermissions>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/permissions";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            
            protected GetPermissions(final String enterpriseId, final String productId) {
                super(Products.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/permissions", null, ProductPermissions.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetPermissions set$Xgafv(final String $Xgafv) {
                return (GetPermissions)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetPermissions setAccessToken(final String accessToken) {
                return (GetPermissions)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetPermissions setAlt(final String alt) {
                return (GetPermissions)super.setAlt(alt);
            }
            
            @Override
            public GetPermissions setCallback(final String callback) {
                return (GetPermissions)super.setCallback(callback);
            }
            
            @Override
            public GetPermissions setFields(final String fields) {
                return (GetPermissions)super.setFields(fields);
            }
            
            @Override
            public GetPermissions setKey(final String key) {
                return (GetPermissions)super.setKey(key);
            }
            
            @Override
            public GetPermissions setOauthToken(final String oauthToken) {
                return (GetPermissions)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetPermissions setPrettyPrint(final Boolean prettyPrint) {
                return (GetPermissions)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetPermissions setQuotaUser(final String quotaUser) {
                return (GetPermissions)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetPermissions setUploadType(final String uploadType) {
                return (GetPermissions)super.setUploadType(uploadType);
            }
            
            @Override
            public GetPermissions setUploadProtocol(final String uploadProtocol) {
                return (GetPermissions)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetPermissions setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public GetPermissions setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            @Override
            public GetPermissions set(final String parameterName, final Object value) {
                return (GetPermissions)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<ProductsListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products";
            @Key
            private String enterpriseId;
            @Key
            private Boolean approved;
            @Key
            private String language;
            @Key
            private Long maxResults;
            @Key
            private String query;
            @Key
            private String token;
            
            protected List(final String enterpriseId) {
                super(Products.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/products", null, ProductsListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public Boolean getApproved() {
                return this.approved;
            }
            
            public List setApproved(final Boolean approved) {
                this.approved = approved;
                return this;
            }
            
            public String getLanguage() {
                return this.language;
            }
            
            public List setLanguage(final String language) {
                this.language = language;
                return this;
            }
            
            public Long getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Long maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public List setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getToken() {
                return this.token;
            }
            
            public List setToken(final String token) {
                this.token = token;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Unapprove extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/unapprove";
            @Key
            private String enterpriseId;
            @Key
            private String productId;
            
            protected Unapprove(final String enterpriseId, final String productId) {
                super(Products.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/products/{productId}/unapprove", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.productId = (String)Preconditions.checkNotNull((Object)productId, (Object)"Required parameter productId must be specified.");
            }
            
            @Override
            public Unapprove set$Xgafv(final String $Xgafv) {
                return (Unapprove)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Unapprove setAccessToken(final String accessToken) {
                return (Unapprove)super.setAccessToken(accessToken);
            }
            
            @Override
            public Unapprove setAlt(final String alt) {
                return (Unapprove)super.setAlt(alt);
            }
            
            @Override
            public Unapprove setCallback(final String callback) {
                return (Unapprove)super.setCallback(callback);
            }
            
            @Override
            public Unapprove setFields(final String fields) {
                return (Unapprove)super.setFields(fields);
            }
            
            @Override
            public Unapprove setKey(final String key) {
                return (Unapprove)super.setKey(key);
            }
            
            @Override
            public Unapprove setOauthToken(final String oauthToken) {
                return (Unapprove)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Unapprove setPrettyPrint(final Boolean prettyPrint) {
                return (Unapprove)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Unapprove setQuotaUser(final String quotaUser) {
                return (Unapprove)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Unapprove setUploadType(final String uploadType) {
                return (Unapprove)super.setUploadType(uploadType);
            }
            
            @Override
            public Unapprove setUploadProtocol(final String uploadProtocol) {
                return (Unapprove)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Unapprove setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getProductId() {
                return this.productId;
            }
            
            public Unapprove setProductId(final String productId) {
                this.productId = productId;
                return this;
            }
            
            @Override
            public Unapprove set(final String parameterName, final Object value) {
                return (Unapprove)super.set(parameterName, value);
            }
        }
    }
    
    public class Serviceaccountkeys
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String keyId) throws IOException {
            final Delete result = new Delete(enterpriseId, keyId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String enterpriseId, final ServiceAccountKey content) throws IOException {
            final Insert result = new Insert(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId) throws IOException {
            final List result = new List(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys/{keyId}";
            @Key
            private String enterpriseId;
            @Key
            private String keyId;
            
            protected Delete(final String enterpriseId, final String keyId) {
                super(Serviceaccountkeys.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys/{keyId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.keyId = (String)Preconditions.checkNotNull((Object)keyId, (Object)"Required parameter keyId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getKeyId() {
                return this.keyId;
            }
            
            public Delete setKeyId(final String keyId) {
                this.keyId = keyId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Insert extends AndroidEnterpriseRequest<ServiceAccountKey>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys";
            @Key
            private String enterpriseId;
            
            protected Insert(final String enterpriseId, final ServiceAccountKey content) {
                super(Serviceaccountkeys.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys", content, ServiceAccountKey.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getType(), "ServiceAccountKey.getType()");
            }
            
            @Override
            public Insert set$Xgafv(final String $Xgafv) {
                return (Insert)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Insert setAccessToken(final String accessToken) {
                return (Insert)super.setAccessToken(accessToken);
            }
            
            @Override
            public Insert setAlt(final String alt) {
                return (Insert)super.setAlt(alt);
            }
            
            @Override
            public Insert setCallback(final String callback) {
                return (Insert)super.setCallback(callback);
            }
            
            @Override
            public Insert setFields(final String fields) {
                return (Insert)super.setFields(fields);
            }
            
            @Override
            public Insert setKey(final String key) {
                return (Insert)super.setKey(key);
            }
            
            @Override
            public Insert setOauthToken(final String oauthToken) {
                return (Insert)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Insert setPrettyPrint(final Boolean prettyPrint) {
                return (Insert)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Insert setQuotaUser(final String quotaUser) {
                return (Insert)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Insert setUploadType(final String uploadType) {
                return (Insert)super.setUploadType(uploadType);
            }
            
            @Override
            public Insert setUploadProtocol(final String uploadProtocol) {
                return (Insert)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Insert setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<ServiceAccountKeysListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys";
            @Key
            private String enterpriseId;
            
            protected List(final String enterpriseId) {
                super(Serviceaccountkeys.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/serviceAccountKeys", null, ServiceAccountKeysListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Storelayoutclusters
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String pageId, final String clusterId) throws IOException {
            final Delete result = new Delete(enterpriseId, pageId, clusterId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String pageId, final String clusterId) throws IOException {
            final Get result = new Get(enterpriseId, pageId, clusterId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String enterpriseId, final String pageId, final StoreCluster content) throws IOException {
            final Insert result = new Insert(enterpriseId, pageId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String pageId) throws IOException {
            final List result = new List(enterpriseId, pageId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String pageId, final String clusterId, final StoreCluster content) throws IOException {
            final Update result = new Update(enterpriseId, pageId, clusterId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            @Key
            private String clusterId;
            
            protected Delete(final String enterpriseId, final String pageId, final String clusterId) {
                super(Storelayoutclusters.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
                this.clusterId = (String)Preconditions.checkNotNull((Object)clusterId, (Object)"Required parameter clusterId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Delete setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            public String getClusterId() {
                return this.clusterId;
            }
            
            public Delete setClusterId(final String clusterId) {
                this.clusterId = clusterId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<StoreCluster>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            @Key
            private String clusterId;
            
            protected Get(final String enterpriseId, final String pageId, final String clusterId) {
                super(Storelayoutclusters.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}", null, StoreCluster.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
                this.clusterId = (String)Preconditions.checkNotNull((Object)clusterId, (Object)"Required parameter clusterId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Get setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            public String getClusterId() {
                return this.clusterId;
            }
            
            public Get setClusterId(final String clusterId) {
                this.clusterId = clusterId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends AndroidEnterpriseRequest<StoreCluster>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            
            protected Insert(final String enterpriseId, final String pageId, final StoreCluster content) {
                super(Storelayoutclusters.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters", content, StoreCluster.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
            }
            
            @Override
            public Insert set$Xgafv(final String $Xgafv) {
                return (Insert)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Insert setAccessToken(final String accessToken) {
                return (Insert)super.setAccessToken(accessToken);
            }
            
            @Override
            public Insert setAlt(final String alt) {
                return (Insert)super.setAlt(alt);
            }
            
            @Override
            public Insert setCallback(final String callback) {
                return (Insert)super.setCallback(callback);
            }
            
            @Override
            public Insert setFields(final String fields) {
                return (Insert)super.setFields(fields);
            }
            
            @Override
            public Insert setKey(final String key) {
                return (Insert)super.setKey(key);
            }
            
            @Override
            public Insert setOauthToken(final String oauthToken) {
                return (Insert)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Insert setPrettyPrint(final Boolean prettyPrint) {
                return (Insert)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Insert setQuotaUser(final String quotaUser) {
                return (Insert)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Insert setUploadType(final String uploadType) {
                return (Insert)super.setUploadType(uploadType);
            }
            
            @Override
            public Insert setUploadProtocol(final String uploadProtocol) {
                return (Insert)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Insert setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Insert setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<StoreLayoutClustersListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            
            protected List(final String enterpriseId, final String pageId) {
                super(Storelayoutclusters.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters", null, StoreLayoutClustersListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public List setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<StoreCluster>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            @Key
            private String clusterId;
            
            protected Update(final String enterpriseId, final String pageId, final String clusterId, final StoreCluster content) {
                super(Storelayoutclusters.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}/clusters/{clusterId}", content, StoreCluster.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
                this.clusterId = (String)Preconditions.checkNotNull((Object)clusterId, (Object)"Required parameter clusterId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Update setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            public String getClusterId() {
                return this.clusterId;
            }
            
            public Update setClusterId(final String clusterId) {
                this.clusterId = clusterId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Storelayoutpages
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String pageId) throws IOException {
            final Delete result = new Delete(enterpriseId, pageId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String pageId) throws IOException {
            final Get result = new Get(enterpriseId, pageId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String enterpriseId, final StorePage content) throws IOException {
            final Insert result = new Insert(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId) throws IOException {
            final List result = new List(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String pageId, final StorePage content) throws IOException {
            final Update result = new Update(enterpriseId, pageId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            
            protected Delete(final String enterpriseId, final String pageId) {
                super(Storelayoutpages.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Delete setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<StorePage>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            
            protected Get(final String enterpriseId, final String pageId) {
                super(Storelayoutpages.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}", null, StorePage.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Get setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends AndroidEnterpriseRequest<StorePage>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages";
            @Key
            private String enterpriseId;
            
            protected Insert(final String enterpriseId, final StorePage content) {
                super(Storelayoutpages.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages", content, StorePage.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public Insert set$Xgafv(final String $Xgafv) {
                return (Insert)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Insert setAccessToken(final String accessToken) {
                return (Insert)super.setAccessToken(accessToken);
            }
            
            @Override
            public Insert setAlt(final String alt) {
                return (Insert)super.setAlt(alt);
            }
            
            @Override
            public Insert setCallback(final String callback) {
                return (Insert)super.setCallback(callback);
            }
            
            @Override
            public Insert setFields(final String fields) {
                return (Insert)super.setFields(fields);
            }
            
            @Override
            public Insert setKey(final String key) {
                return (Insert)super.setKey(key);
            }
            
            @Override
            public Insert setOauthToken(final String oauthToken) {
                return (Insert)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Insert setPrettyPrint(final Boolean prettyPrint) {
                return (Insert)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Insert setQuotaUser(final String quotaUser) {
                return (Insert)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Insert setUploadType(final String uploadType) {
                return (Insert)super.setUploadType(uploadType);
            }
            
            @Override
            public Insert setUploadProtocol(final String uploadProtocol) {
                return (Insert)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Insert setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<StoreLayoutPagesListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages";
            @Key
            private String enterpriseId;
            
            protected List(final String enterpriseId) {
                super(Storelayoutpages.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages", null, StoreLayoutPagesListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<StorePage>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}";
            @Key
            private String enterpriseId;
            @Key
            private String pageId;
            
            protected Update(final String enterpriseId, final String pageId, final StorePage content) {
                super(Storelayoutpages.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/storeLayout/pages/{pageId}", content, StorePage.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.pageId = (String)Preconditions.checkNotNull((Object)pageId, (Object)"Required parameter pageId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getPageId() {
                return this.pageId;
            }
            
            public Update setPageId(final String pageId) {
                this.pageId = pageId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Users
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String userId) throws IOException {
            final Delete result = new Delete(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GenerateAuthenticationToken generateAuthenticationToken(final String enterpriseId, final String userId) throws IOException {
            final GenerateAuthenticationToken result = new GenerateAuthenticationToken(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String userId) throws IOException {
            final Get result = new Get(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public GetAvailableProductSet getAvailableProductSet(final String enterpriseId, final String userId) throws IOException {
            final GetAvailableProductSet result = new GetAvailableProductSet(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String enterpriseId, final User content) throws IOException {
            final Insert result = new Insert(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId, final String email) throws IOException {
            final List result = new List(enterpriseId, email);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public RevokeDeviceAccess revokeDeviceAccess(final String enterpriseId, final String userId) throws IOException {
            final RevokeDeviceAccess result = new RevokeDeviceAccess(enterpriseId, userId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SetAvailableProductSet setAvailableProductSet(final String enterpriseId, final String userId, final ProductSet content) throws IOException {
            final SetAvailableProductSet result = new SetAvailableProductSet(enterpriseId, userId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String userId, final User content) throws IOException {
            final Update result = new Update(enterpriseId, userId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected Delete(final String enterpriseId, final String userId) {
                super(Users.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Delete setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class GenerateAuthenticationToken extends AndroidEnterpriseRequest<AuthenticationToken>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/authenticationToken";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected GenerateAuthenticationToken(final String enterpriseId, final String userId) {
                super(Users.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/authenticationToken", null, AuthenticationToken.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            @Override
            public GenerateAuthenticationToken set$Xgafv(final String $Xgafv) {
                return (GenerateAuthenticationToken)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GenerateAuthenticationToken setAccessToken(final String accessToken) {
                return (GenerateAuthenticationToken)super.setAccessToken(accessToken);
            }
            
            @Override
            public GenerateAuthenticationToken setAlt(final String alt) {
                return (GenerateAuthenticationToken)super.setAlt(alt);
            }
            
            @Override
            public GenerateAuthenticationToken setCallback(final String callback) {
                return (GenerateAuthenticationToken)super.setCallback(callback);
            }
            
            @Override
            public GenerateAuthenticationToken setFields(final String fields) {
                return (GenerateAuthenticationToken)super.setFields(fields);
            }
            
            @Override
            public GenerateAuthenticationToken setKey(final String key) {
                return (GenerateAuthenticationToken)super.setKey(key);
            }
            
            @Override
            public GenerateAuthenticationToken setOauthToken(final String oauthToken) {
                return (GenerateAuthenticationToken)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GenerateAuthenticationToken setPrettyPrint(final Boolean prettyPrint) {
                return (GenerateAuthenticationToken)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GenerateAuthenticationToken setQuotaUser(final String quotaUser) {
                return (GenerateAuthenticationToken)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GenerateAuthenticationToken setUploadType(final String uploadType) {
                return (GenerateAuthenticationToken)super.setUploadType(uploadType);
            }
            
            @Override
            public GenerateAuthenticationToken setUploadProtocol(final String uploadProtocol) {
                return (GenerateAuthenticationToken)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GenerateAuthenticationToken setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public GenerateAuthenticationToken setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public GenerateAuthenticationToken set(final String parameterName, final Object value) {
                return (GenerateAuthenticationToken)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<User>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected Get(final String enterpriseId, final String userId) {
                super(Users.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}", null, User.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Get setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class GetAvailableProductSet extends AndroidEnterpriseRequest<ProductSet>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/availableProductSet";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected GetAvailableProductSet(final String enterpriseId, final String userId) {
                super(Users.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/availableProductSet", null, ProductSet.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public GetAvailableProductSet set$Xgafv(final String $Xgafv) {
                return (GetAvailableProductSet)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public GetAvailableProductSet setAccessToken(final String accessToken) {
                return (GetAvailableProductSet)super.setAccessToken(accessToken);
            }
            
            @Override
            public GetAvailableProductSet setAlt(final String alt) {
                return (GetAvailableProductSet)super.setAlt(alt);
            }
            
            @Override
            public GetAvailableProductSet setCallback(final String callback) {
                return (GetAvailableProductSet)super.setCallback(callback);
            }
            
            @Override
            public GetAvailableProductSet setFields(final String fields) {
                return (GetAvailableProductSet)super.setFields(fields);
            }
            
            @Override
            public GetAvailableProductSet setKey(final String key) {
                return (GetAvailableProductSet)super.setKey(key);
            }
            
            @Override
            public GetAvailableProductSet setOauthToken(final String oauthToken) {
                return (GetAvailableProductSet)super.setOauthToken(oauthToken);
            }
            
            @Override
            public GetAvailableProductSet setPrettyPrint(final Boolean prettyPrint) {
                return (GetAvailableProductSet)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public GetAvailableProductSet setQuotaUser(final String quotaUser) {
                return (GetAvailableProductSet)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public GetAvailableProductSet setUploadType(final String uploadType) {
                return (GetAvailableProductSet)super.setUploadType(uploadType);
            }
            
            @Override
            public GetAvailableProductSet setUploadProtocol(final String uploadProtocol) {
                return (GetAvailableProductSet)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public GetAvailableProductSet setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public GetAvailableProductSet setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public GetAvailableProductSet set(final String parameterName, final Object value) {
                return (GetAvailableProductSet)super.set(parameterName, value);
            }
        }
        
        public class Insert extends AndroidEnterpriseRequest<User>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users";
            @Key
            private String enterpriseId;
            
            protected Insert(final String enterpriseId, final User content) {
                super(Users.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/users", content, User.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getAccountIdentifier(), "User.getAccountIdentifier()");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getAccountType(), "User.getAccountType()");
            }
            
            @Override
            public Insert set$Xgafv(final String $Xgafv) {
                return (Insert)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Insert setAccessToken(final String accessToken) {
                return (Insert)super.setAccessToken(accessToken);
            }
            
            @Override
            public Insert setAlt(final String alt) {
                return (Insert)super.setAlt(alt);
            }
            
            @Override
            public Insert setCallback(final String callback) {
                return (Insert)super.setCallback(callback);
            }
            
            @Override
            public Insert setFields(final String fields) {
                return (Insert)super.setFields(fields);
            }
            
            @Override
            public Insert setKey(final String key) {
                return (Insert)super.setKey(key);
            }
            
            @Override
            public Insert setOauthToken(final String oauthToken) {
                return (Insert)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Insert setPrettyPrint(final Boolean prettyPrint) {
                return (Insert)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Insert setQuotaUser(final String quotaUser) {
                return (Insert)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Insert setUploadType(final String uploadType) {
                return (Insert)super.setUploadType(uploadType);
            }
            
            @Override
            public Insert setUploadProtocol(final String uploadProtocol) {
                return (Insert)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Insert setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<UsersListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users";
            @Key
            private String enterpriseId;
            @Key
            private String email;
            
            protected List(final String enterpriseId, final String email) {
                super(Users.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/users", null, UsersListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.email = (String)Preconditions.checkNotNull((Object)email, (Object)"Required parameter email must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getEmail() {
                return this.email;
            }
            
            public List setEmail(final String email) {
                this.email = email;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class RevokeDeviceAccess extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/deviceAccess";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected RevokeDeviceAccess(final String enterpriseId, final String userId) {
                super(Users.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/deviceAccess", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            @Override
            public RevokeDeviceAccess set$Xgafv(final String $Xgafv) {
                return (RevokeDeviceAccess)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public RevokeDeviceAccess setAccessToken(final String accessToken) {
                return (RevokeDeviceAccess)super.setAccessToken(accessToken);
            }
            
            @Override
            public RevokeDeviceAccess setAlt(final String alt) {
                return (RevokeDeviceAccess)super.setAlt(alt);
            }
            
            @Override
            public RevokeDeviceAccess setCallback(final String callback) {
                return (RevokeDeviceAccess)super.setCallback(callback);
            }
            
            @Override
            public RevokeDeviceAccess setFields(final String fields) {
                return (RevokeDeviceAccess)super.setFields(fields);
            }
            
            @Override
            public RevokeDeviceAccess setKey(final String key) {
                return (RevokeDeviceAccess)super.setKey(key);
            }
            
            @Override
            public RevokeDeviceAccess setOauthToken(final String oauthToken) {
                return (RevokeDeviceAccess)super.setOauthToken(oauthToken);
            }
            
            @Override
            public RevokeDeviceAccess setPrettyPrint(final Boolean prettyPrint) {
                return (RevokeDeviceAccess)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public RevokeDeviceAccess setQuotaUser(final String quotaUser) {
                return (RevokeDeviceAccess)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public RevokeDeviceAccess setUploadType(final String uploadType) {
                return (RevokeDeviceAccess)super.setUploadType(uploadType);
            }
            
            @Override
            public RevokeDeviceAccess setUploadProtocol(final String uploadProtocol) {
                return (RevokeDeviceAccess)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public RevokeDeviceAccess setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public RevokeDeviceAccess setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public RevokeDeviceAccess set(final String parameterName, final Object value) {
                return (RevokeDeviceAccess)super.set(parameterName, value);
            }
        }
        
        public class SetAvailableProductSet extends AndroidEnterpriseRequest<ProductSet>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/availableProductSet";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected SetAvailableProductSet(final String enterpriseId, final String userId, final ProductSet content) {
                super(Users.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}/availableProductSet", content, ProductSet.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            @Override
            public SetAvailableProductSet set$Xgafv(final String $Xgafv) {
                return (SetAvailableProductSet)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SetAvailableProductSet setAccessToken(final String accessToken) {
                return (SetAvailableProductSet)super.setAccessToken(accessToken);
            }
            
            @Override
            public SetAvailableProductSet setAlt(final String alt) {
                return (SetAvailableProductSet)super.setAlt(alt);
            }
            
            @Override
            public SetAvailableProductSet setCallback(final String callback) {
                return (SetAvailableProductSet)super.setCallback(callback);
            }
            
            @Override
            public SetAvailableProductSet setFields(final String fields) {
                return (SetAvailableProductSet)super.setFields(fields);
            }
            
            @Override
            public SetAvailableProductSet setKey(final String key) {
                return (SetAvailableProductSet)super.setKey(key);
            }
            
            @Override
            public SetAvailableProductSet setOauthToken(final String oauthToken) {
                return (SetAvailableProductSet)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SetAvailableProductSet setPrettyPrint(final Boolean prettyPrint) {
                return (SetAvailableProductSet)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SetAvailableProductSet setQuotaUser(final String quotaUser) {
                return (SetAvailableProductSet)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SetAvailableProductSet setUploadType(final String uploadType) {
                return (SetAvailableProductSet)super.setUploadType(uploadType);
            }
            
            @Override
            public SetAvailableProductSet setUploadProtocol(final String uploadProtocol) {
                return (SetAvailableProductSet)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public SetAvailableProductSet setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public SetAvailableProductSet setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public SetAvailableProductSet set(final String parameterName, final Object value) {
                return (SetAvailableProductSet)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<User>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}";
            @Key
            private String enterpriseId;
            @Key
            private String userId;
            
            protected Update(final String enterpriseId, final String userId, final User content) {
                super(Users.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/users/{userId}", content, User.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.userId = (String)Preconditions.checkNotNull((Object)userId, (Object)"Required parameter userId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getUserId() {
                return this.userId;
            }
            
            public Update setUserId(final String userId) {
                this.userId = userId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Webapps
    {
        final /* synthetic */ AndroidEnterprise this$0;
        
        public Delete delete(final String enterpriseId, final String webAppId) throws IOException {
            final Delete result = new Delete(enterpriseId, webAppId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String enterpriseId, final String webAppId) throws IOException {
            final Get result = new Get(enterpriseId, webAppId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String enterpriseId, final WebApp content) throws IOException {
            final Insert result = new Insert(enterpriseId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String enterpriseId) throws IOException {
            final List result = new List(enterpriseId);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String enterpriseId, final String webAppId, final WebApp content) throws IOException {
            final Update result = new Update(enterpriseId, webAppId, content);
            AndroidEnterprise.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends AndroidEnterpriseRequest<Void>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}";
            @Key
            private String enterpriseId;
            @Key
            private String webAppId;
            
            protected Delete(final String enterpriseId, final String webAppId) {
                super(Webapps.this.this$0, "DELETE", "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}", null, Void.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.webAppId = (String)Preconditions.checkNotNull((Object)webAppId, (Object)"Required parameter webAppId must be specified.");
            }
            
            @Override
            public Delete set$Xgafv(final String $Xgafv) {
                return (Delete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Delete setAccessToken(final String accessToken) {
                return (Delete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Delete setAlt(final String alt) {
                return (Delete)super.setAlt(alt);
            }
            
            @Override
            public Delete setCallback(final String callback) {
                return (Delete)super.setCallback(callback);
            }
            
            @Override
            public Delete setFields(final String fields) {
                return (Delete)super.setFields(fields);
            }
            
            @Override
            public Delete setKey(final String key) {
                return (Delete)super.setKey(key);
            }
            
            @Override
            public Delete setOauthToken(final String oauthToken) {
                return (Delete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Delete setPrettyPrint(final Boolean prettyPrint) {
                return (Delete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Delete setQuotaUser(final String quotaUser) {
                return (Delete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Delete setUploadType(final String uploadType) {
                return (Delete)super.setUploadType(uploadType);
            }
            
            @Override
            public Delete setUploadProtocol(final String uploadProtocol) {
                return (Delete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Delete setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getWebAppId() {
                return this.webAppId;
            }
            
            public Delete setWebAppId(final String webAppId) {
                this.webAppId = webAppId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends AndroidEnterpriseRequest<WebApp>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}";
            @Key
            private String enterpriseId;
            @Key
            private String webAppId;
            
            protected Get(final String enterpriseId, final String webAppId) {
                super(Webapps.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}", null, WebApp.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.webAppId = (String)Preconditions.checkNotNull((Object)webAppId, (Object)"Required parameter webAppId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String $Xgafv) {
                return (Get)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Get setAccessToken(final String accessToken) {
                return (Get)super.setAccessToken(accessToken);
            }
            
            @Override
            public Get setAlt(final String alt) {
                return (Get)super.setAlt(alt);
            }
            
            @Override
            public Get setCallback(final String callback) {
                return (Get)super.setCallback(callback);
            }
            
            @Override
            public Get setFields(final String fields) {
                return (Get)super.setFields(fields);
            }
            
            @Override
            public Get setKey(final String key) {
                return (Get)super.setKey(key);
            }
            
            @Override
            public Get setOauthToken(final String oauthToken) {
                return (Get)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Get setPrettyPrint(final Boolean prettyPrint) {
                return (Get)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Get setQuotaUser(final String quotaUser) {
                return (Get)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Get setUploadType(final String uploadType) {
                return (Get)super.setUploadType(uploadType);
            }
            
            @Override
            public Get setUploadProtocol(final String uploadProtocol) {
                return (Get)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Get setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getWebAppId() {
                return this.webAppId;
            }
            
            public Get setWebAppId(final String webAppId) {
                this.webAppId = webAppId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends AndroidEnterpriseRequest<WebApp>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/webApps";
            @Key
            private String enterpriseId;
            
            protected Insert(final String enterpriseId, final WebApp content) {
                super(Webapps.this.this$0, "POST", "androidenterprise/v1/enterprises/{enterpriseId}/webApps", content, WebApp.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            @Override
            public Insert set$Xgafv(final String $Xgafv) {
                return (Insert)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Insert setAccessToken(final String accessToken) {
                return (Insert)super.setAccessToken(accessToken);
            }
            
            @Override
            public Insert setAlt(final String alt) {
                return (Insert)super.setAlt(alt);
            }
            
            @Override
            public Insert setCallback(final String callback) {
                return (Insert)super.setCallback(callback);
            }
            
            @Override
            public Insert setFields(final String fields) {
                return (Insert)super.setFields(fields);
            }
            
            @Override
            public Insert setKey(final String key) {
                return (Insert)super.setKey(key);
            }
            
            @Override
            public Insert setOauthToken(final String oauthToken) {
                return (Insert)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Insert setPrettyPrint(final Boolean prettyPrint) {
                return (Insert)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Insert setQuotaUser(final String quotaUser) {
                return (Insert)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Insert setUploadType(final String uploadType) {
                return (Insert)super.setUploadType(uploadType);
            }
            
            @Override
            public Insert setUploadProtocol(final String uploadProtocol) {
                return (Insert)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Insert setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends AndroidEnterpriseRequest<WebAppsListResponse>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/webApps";
            @Key
            private String enterpriseId;
            
            protected List(final String enterpriseId) {
                super(Webapps.this.this$0, "GET", "androidenterprise/v1/enterprises/{enterpriseId}/webApps", null, WebAppsListResponse.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String $Xgafv) {
                return (List)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public List setAccessToken(final String accessToken) {
                return (List)super.setAccessToken(accessToken);
            }
            
            @Override
            public List setAlt(final String alt) {
                return (List)super.setAlt(alt);
            }
            
            @Override
            public List setCallback(final String callback) {
                return (List)super.setCallback(callback);
            }
            
            @Override
            public List setFields(final String fields) {
                return (List)super.setFields(fields);
            }
            
            @Override
            public List setKey(final String key) {
                return (List)super.setKey(key);
            }
            
            @Override
            public List setOauthToken(final String oauthToken) {
                return (List)super.setOauthToken(oauthToken);
            }
            
            @Override
            public List setPrettyPrint(final Boolean prettyPrint) {
                return (List)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public List setQuotaUser(final String quotaUser) {
                return (List)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public List setUploadType(final String uploadType) {
                return (List)super.setUploadType(uploadType);
            }
            
            @Override
            public List setUploadProtocol(final String uploadProtocol) {
                return (List)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public List setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Update extends AndroidEnterpriseRequest<WebApp>
        {
            private static final String REST_PATH = "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}";
            @Key
            private String enterpriseId;
            @Key
            private String webAppId;
            
            protected Update(final String enterpriseId, final String webAppId, final WebApp content) {
                super(Webapps.this.this$0, "PUT", "androidenterprise/v1/enterprises/{enterpriseId}/webApps/{webAppId}", content, WebApp.class);
                this.enterpriseId = (String)Preconditions.checkNotNull((Object)enterpriseId, (Object)"Required parameter enterpriseId must be specified.");
                this.webAppId = (String)Preconditions.checkNotNull((Object)webAppId, (Object)"Required parameter webAppId must be specified.");
            }
            
            @Override
            public Update set$Xgafv(final String $Xgafv) {
                return (Update)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Update setAccessToken(final String accessToken) {
                return (Update)super.setAccessToken(accessToken);
            }
            
            @Override
            public Update setAlt(final String alt) {
                return (Update)super.setAlt(alt);
            }
            
            @Override
            public Update setCallback(final String callback) {
                return (Update)super.setCallback(callback);
            }
            
            @Override
            public Update setFields(final String fields) {
                return (Update)super.setFields(fields);
            }
            
            @Override
            public Update setKey(final String key) {
                return (Update)super.setKey(key);
            }
            
            @Override
            public Update setOauthToken(final String oauthToken) {
                return (Update)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Update setPrettyPrint(final Boolean prettyPrint) {
                return (Update)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Update setQuotaUser(final String quotaUser) {
                return (Update)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Update setUploadType(final String uploadType) {
                return (Update)super.setUploadType(uploadType);
            }
            
            @Override
            public Update setUploadProtocol(final String uploadProtocol) {
                return (Update)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getEnterpriseId() {
                return this.enterpriseId;
            }
            
            public Update setEnterpriseId(final String enterpriseId) {
                this.enterpriseId = enterpriseId;
                return this;
            }
            
            public String getWebAppId() {
                return this.webAppId;
            }
            
            public Update setWebAppId(final String webAppId) {
                this.webAppId = webAppId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public static final class Builder extends AbstractGoogleJsonClient.Builder
    {
        private static String chooseEndpoint(final HttpTransport transport) {
            String useMtlsEndpoint = System.getenv("GOOGLE_API_USE_MTLS_ENDPOINT");
            useMtlsEndpoint = ((useMtlsEndpoint == null) ? "auto" : useMtlsEndpoint);
            if ("always".equals(useMtlsEndpoint) || ("auto".equals(useMtlsEndpoint) && transport != null && transport.isMtls())) {
                return "https://androidenterprise.mtls.googleapis.com/";
            }
            return "https://androidenterprise.googleapis.com/";
        }
        
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
            super(transport, jsonFactory, chooseEndpoint(transport), "", httpRequestInitializer, false);
            this.setBatchPath("batch");
        }
        
        public AndroidEnterprise build() {
            return new AndroidEnterprise(this);
        }
        
        public Builder setRootUrl(final String rootUrl) {
            return (Builder)super.setRootUrl(rootUrl);
        }
        
        public Builder setServicePath(final String servicePath) {
            return (Builder)super.setServicePath(servicePath);
        }
        
        public Builder setBatchPath(final String batchPath) {
            return (Builder)super.setBatchPath(batchPath);
        }
        
        public Builder setHttpRequestInitializer(final HttpRequestInitializer httpRequestInitializer) {
            return (Builder)super.setHttpRequestInitializer(httpRequestInitializer);
        }
        
        public Builder setApplicationName(final String applicationName) {
            return (Builder)super.setApplicationName(applicationName);
        }
        
        public Builder setSuppressPatternChecks(final boolean suppressPatternChecks) {
            return (Builder)super.setSuppressPatternChecks(suppressPatternChecks);
        }
        
        public Builder setSuppressRequiredParameterChecks(final boolean suppressRequiredParameterChecks) {
            return (Builder)super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
        }
        
        public Builder setSuppressAllChecks(final boolean suppressAllChecks) {
            return (Builder)super.setSuppressAllChecks(suppressAllChecks);
        }
        
        public Builder setAndroidEnterpriseRequestInitializer(final AndroidEnterpriseRequestInitializer androidenterpriseRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer((GoogleClientRequestInitializer)androidenterpriseRequestInitializer);
        }
        
        public Builder setGoogleClientRequestInitializer(final GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }
}
