package com.google.api.services.directory;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.services.directory.model.VerificationCodes;
import com.google.api.services.directory.model.UserPhoto;
import com.google.api.services.directory.model.Users;
import com.google.api.services.directory.model.UserUndelete;
import com.google.api.services.directory.model.UserMakeAdmin;
import com.google.api.services.directory.model.User;
import com.google.api.services.directory.model.Tokens;
import com.google.api.services.directory.model.Token;
import com.google.api.services.directory.model.Schemas;
import com.google.api.services.directory.model.Schema;
import com.google.api.services.directory.model.Roles;
import com.google.api.services.directory.model.Role;
import com.google.api.services.directory.model.RoleAssignments;
import com.google.api.services.directory.model.RoleAssignment;
import com.google.api.services.directory.model.Features;
import com.google.api.services.directory.model.FeatureRename;
import com.google.api.services.directory.model.Feature;
import com.google.api.services.directory.model.CalendarResources;
import com.google.api.services.directory.model.CalendarResource;
import com.google.api.services.directory.model.Buildings;
import com.google.api.services.directory.model.Building;
import com.google.api.services.directory.model.Privileges;
import com.google.api.services.directory.model.OrgUnits;
import com.google.api.services.directory.model.OrgUnit;
import com.google.api.services.directory.model.MobileDevices;
import com.google.api.services.directory.model.MobileDevice;
import com.google.api.services.directory.model.MobileDeviceAction;
import com.google.api.services.directory.model.Members;
import com.google.api.services.directory.model.MembersHasMember;
import com.google.api.services.directory.model.Member;
import com.google.api.services.directory.model.Aliases;
import com.google.api.services.directory.model.Alias;
import com.google.api.services.directory.model.Groups;
import com.google.api.services.directory.model.Group;
import com.google.api.services.directory.model.Domains2;
import com.google.api.services.directory.model.Domains;
import com.google.api.services.directory.model.DomainAliases;
import com.google.api.services.directory.model.DomainAlias;
import com.google.api.services.directory.model.ListPrinterModelsResponse;
import com.google.api.services.directory.model.ListPrintersResponse;
import com.google.api.services.directory.model.Empty;
import com.google.api.services.directory.model.BatchDeletePrintersResponse;
import java.util.regex.Pattern;
import com.google.api.services.directory.model.BatchCreatePrintersResponse;
import com.google.api.services.directory.model.Printer;
import com.google.api.services.directory.model.BatchDeletePrintersRequest;
import com.google.api.services.directory.model.BatchCreatePrintersRequest;
import com.google.api.services.directory.model.Customer;
import com.google.api.services.directory.model.DirectoryChromeosdevicesCommand;
import com.google.api.services.directory.model.DirectoryChromeosdevicesIssueCommandResponse;
import com.google.api.services.directory.model.DirectoryChromeosdevicesIssueCommandRequest;
import com.google.api.services.directory.model.ChromeOsDevices;
import com.google.api.services.directory.model.ChromeOsDevice;
import com.google.api.services.directory.model.ChromeOsMoveDevicesToOu;
import com.google.api.services.directory.model.ChromeOsDeviceAction;
import com.google.api.services.directory.model.Channel;
import com.google.api.services.directory.model.Asps;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.directory.model.Asp;
import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;
import com.google.api.client.googleapis.GoogleUtils;
import java.io.IOException;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

public class Directory extends AbstractGoogleJsonClient
{
    public static final String DEFAULT_ROOT_URL = "https://admin.googleapis.com/";
    public static final String DEFAULT_MTLS_ROOT_URL = "https://admin.mtls.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "";
    public static final String DEFAULT_BATCH_PATH = "batch";
    public static final String DEFAULT_BASE_URL = "https://admin.googleapis.com/";
    
    public Directory(final HttpTransport transport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, jsonFactory, httpRequestInitializer));
    }
    
    Directory(final Builder builder) {
        super((AbstractGoogleJsonClient.Builder)builder);
    }
    
    protected void initialize(final AbstractGoogleClientRequest<?> httpClientRequest) throws IOException {
        super.initialize((AbstractGoogleClientRequest)httpClientRequest);
    }
    
    public Asps asps() {
        return new Asps();
    }
    
    public Channels channels() {
        return new Channels();
    }
    
    public Chromeosdevices chromeosdevices() {
        return new Chromeosdevices();
    }
    
    public Customer customer() {
        return new Customer();
    }
    
    public Customers customers() {
        return new Customers();
    }
    
    public DomainAliases domainAliases() {
        return new DomainAliases();
    }
    
    public Domains domains() {
        return new Domains();
    }
    
    public Groups groups() {
        return new Groups();
    }
    
    public Members members() {
        return new Members();
    }
    
    public Mobiledevices mobiledevices() {
        return new Mobiledevices();
    }
    
    public Orgunits orgunits() {
        return new Orgunits();
    }
    
    public Privileges privileges() {
        return new Privileges();
    }
    
    public Resources resources() {
        return new Resources();
    }
    
    public RoleAssignments roleAssignments() {
        return new RoleAssignments();
    }
    
    public Roles roles() {
        return new Roles();
    }
    
    public Schemas schemas() {
        return new Schemas();
    }
    
    public Tokens tokens() {
        return new Tokens();
    }
    
    public TwoStepVerification twoStepVerification() {
        return new TwoStepVerification();
    }
    
    public Users users() {
        return new Users();
    }
    
    public VerificationCodes verificationCodes() {
        return new VerificationCodes();
    }
    
    static {
        Preconditions.checkState(GoogleUtils.MAJOR_VERSION == 1 && (GoogleUtils.MINOR_VERSION >= 32 || (GoogleUtils.MINOR_VERSION == 31 && GoogleUtils.BUGFIX_VERSION >= 1)), "You are currently running with version %s of google-api-client. You need at least version 1.31.1 of google-api-client to run version 1.31.5 of the Admin SDK API library.", new Object[] { GoogleUtils.VERSION });
    }
    
    public class Asps
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String userKey, final Integer codeId) throws IOException {
            final Delete result = new Delete(userKey, codeId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String userKey, final Integer codeId) throws IOException {
            final Get result = new Get(userKey, codeId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String userKey) throws IOException {
            final List result = new List(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/asps/{codeId}";
            @Key
            private String userKey;
            @Key
            private Integer codeId;
            
            protected Delete(final String userKey, final Integer codeId) {
                super(Asps.this.this$0, "DELETE", "admin/directory/v1/users/{userKey}/asps/{codeId}", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                this.codeId = (Integer)Preconditions.checkNotNull((Object)codeId, (Object)"Required parameter codeId must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Delete setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            public Integer getCodeId() {
                return this.codeId;
            }
            
            public Delete setCodeId(final Integer codeId) {
                this.codeId = codeId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Asp>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/asps/{codeId}";
            @Key
            private String userKey;
            @Key
            private Integer codeId;
            
            protected Get(final String userKey, final Integer codeId) {
                super(Asps.this.this$0, "GET", "admin/directory/v1/users/{userKey}/asps/{codeId}", null, Asp.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                this.codeId = (Integer)Preconditions.checkNotNull((Object)codeId, (Object)"Required parameter codeId must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Get setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            public Integer getCodeId() {
                return this.codeId;
            }
            
            public Get setCodeId(final Integer codeId) {
                this.codeId = codeId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Asps>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/asps";
            @Key
            private String userKey;
            
            protected List(final String userKey) {
                super(Asps.this.this$0, "GET", "admin/directory/v1/users/{userKey}/asps", null, com.google.api.services.directory.model.Asps.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public List setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Channels
    {
        final /* synthetic */ Directory this$0;
        
        public Stop stop(final Channel content) throws IOException {
            final Stop result = new Stop(content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Stop extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory_v1/channels/stop";
            
            protected Stop(final Channel content) {
                super(Channels.this.this$0, "POST", "admin/directory_v1/channels/stop", content, Void.class);
            }
            
            @Override
            public Stop set$Xgafv(final String $Xgafv) {
                return (Stop)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Stop setAccessToken(final String accessToken) {
                return (Stop)super.setAccessToken(accessToken);
            }
            
            @Override
            public Stop setAlt(final String alt) {
                return (Stop)super.setAlt(alt);
            }
            
            @Override
            public Stop setCallback(final String callback) {
                return (Stop)super.setCallback(callback);
            }
            
            @Override
            public Stop setFields(final String fields) {
                return (Stop)super.setFields(fields);
            }
            
            @Override
            public Stop setKey(final String key) {
                return (Stop)super.setKey(key);
            }
            
            @Override
            public Stop setOauthToken(final String oauthToken) {
                return (Stop)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Stop setPrettyPrint(final Boolean prettyPrint) {
                return (Stop)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Stop setQuotaUser(final String quotaUser) {
                return (Stop)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Stop setUploadType(final String uploadType) {
                return (Stop)super.setUploadType(uploadType);
            }
            
            @Override
            public Stop setUploadProtocol(final String uploadProtocol) {
                return (Stop)super.setUploadProtocol(uploadProtocol);
            }
            
            @Override
            public Stop set(final String parameterName, final Object value) {
                return (Stop)super.set(parameterName, value);
            }
        }
    }
    
    public class Chromeosdevices
    {
        final /* synthetic */ Directory this$0;
        
        public Action action(final String customerId, final String resourceId, final ChromeOsDeviceAction content) throws IOException {
            final Action result = new Action(customerId, resourceId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customerId, final String deviceId) throws IOException {
            final Get result = new Get(customerId, deviceId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customerId) throws IOException {
            final List result = new List(customerId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public MoveDevicesToOu moveDevicesToOu(final String customerId, final String orgUnitPath, final ChromeOsMoveDevicesToOu content) throws IOException {
            final MoveDevicesToOu result = new MoveDevicesToOu(customerId, orgUnitPath, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String customerId, final String deviceId, final ChromeOsDevice content) throws IOException {
            final Patch result = new Patch(customerId, deviceId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String customerId, final String deviceId, final ChromeOsDevice content) throws IOException {
            final Update result = new Update(customerId, deviceId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Action extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{resourceId}/action";
            @Key
            private String customerId;
            @Key
            private String resourceId;
            
            protected Action(final String customerId, final String resourceId, final ChromeOsDeviceAction content) {
                super(Chromeosdevices.this.this$0, "POST", "admin/directory/v1/customer/{customerId}/devices/chromeos/{resourceId}/action", content, Void.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.resourceId = (String)Preconditions.checkNotNull((Object)resourceId, (Object)"Required parameter resourceId must be specified.");
            }
            
            @Override
            public Action set$Xgafv(final String $Xgafv) {
                return (Action)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Action setAccessToken(final String accessToken) {
                return (Action)super.setAccessToken(accessToken);
            }
            
            @Override
            public Action setAlt(final String alt) {
                return (Action)super.setAlt(alt);
            }
            
            @Override
            public Action setCallback(final String callback) {
                return (Action)super.setCallback(callback);
            }
            
            @Override
            public Action setFields(final String fields) {
                return (Action)super.setFields(fields);
            }
            
            @Override
            public Action setKey(final String key) {
                return (Action)super.setKey(key);
            }
            
            @Override
            public Action setOauthToken(final String oauthToken) {
                return (Action)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Action setPrettyPrint(final Boolean prettyPrint) {
                return (Action)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Action setQuotaUser(final String quotaUser) {
                return (Action)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Action setUploadType(final String uploadType) {
                return (Action)super.setUploadType(uploadType);
            }
            
            @Override
            public Action setUploadProtocol(final String uploadProtocol) {
                return (Action)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Action setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getResourceId() {
                return this.resourceId;
            }
            
            public Action setResourceId(final String resourceId) {
                this.resourceId = resourceId;
                return this;
            }
            
            @Override
            public Action set(final String parameterName, final Object value) {
                return (Action)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<ChromeOsDevice>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}";
            @Key
            private String customerId;
            @Key
            private String deviceId;
            @Key
            private String projection;
            
            protected Get(final String customerId, final String deviceId) {
                super(Chromeosdevices.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}", null, ChromeOsDevice.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Get setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Get setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Get setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<ChromeOsDevices>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos";
            @Key
            private String customerId;
            @Key
            private Integer maxResults;
            @Key
            private String orderBy;
            @Key
            private String orgUnitPath;
            @Key
            private String pageToken;
            @Key
            private String projection;
            @Key
            private String query;
            @Key
            private String sortOrder;
            
            protected List(final String customerId) {
                super(Chromeosdevices.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/devices/chromeos", null, ChromeOsDevices.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public List setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getOrderBy() {
                return this.orderBy;
            }
            
            public List setOrderBy(final String orderBy) {
                this.orderBy = orderBy;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public List setOrgUnitPath(final String orgUnitPath) {
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public List setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public List setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getSortOrder() {
                return this.sortOrder;
            }
            
            public List setSortOrder(final String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class MoveDevicesToOu extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/moveDevicesToOu";
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            
            protected MoveDevicesToOu(final String customerId, final String orgUnitPath, final ChromeOsMoveDevicesToOu content) {
                super(Chromeosdevices.this.this$0, "POST", "admin/directory/v1/customer/{customerId}/devices/chromeos/moveDevicesToOu", content, Void.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.orgUnitPath = (String)Preconditions.checkNotNull((Object)orgUnitPath, (Object)"Required parameter orgUnitPath must be specified.");
            }
            
            @Override
            public MoveDevicesToOu set$Xgafv(final String $Xgafv) {
                return (MoveDevicesToOu)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public MoveDevicesToOu setAccessToken(final String accessToken) {
                return (MoveDevicesToOu)super.setAccessToken(accessToken);
            }
            
            @Override
            public MoveDevicesToOu setAlt(final String alt) {
                return (MoveDevicesToOu)super.setAlt(alt);
            }
            
            @Override
            public MoveDevicesToOu setCallback(final String callback) {
                return (MoveDevicesToOu)super.setCallback(callback);
            }
            
            @Override
            public MoveDevicesToOu setFields(final String fields) {
                return (MoveDevicesToOu)super.setFields(fields);
            }
            
            @Override
            public MoveDevicesToOu setKey(final String key) {
                return (MoveDevicesToOu)super.setKey(key);
            }
            
            @Override
            public MoveDevicesToOu setOauthToken(final String oauthToken) {
                return (MoveDevicesToOu)super.setOauthToken(oauthToken);
            }
            
            @Override
            public MoveDevicesToOu setPrettyPrint(final Boolean prettyPrint) {
                return (MoveDevicesToOu)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public MoveDevicesToOu setQuotaUser(final String quotaUser) {
                return (MoveDevicesToOu)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public MoveDevicesToOu setUploadType(final String uploadType) {
                return (MoveDevicesToOu)super.setUploadType(uploadType);
            }
            
            @Override
            public MoveDevicesToOu setUploadProtocol(final String uploadProtocol) {
                return (MoveDevicesToOu)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public MoveDevicesToOu setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public MoveDevicesToOu setOrgUnitPath(final String orgUnitPath) {
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            @Override
            public MoveDevicesToOu set(final String parameterName, final Object value) {
                return (MoveDevicesToOu)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<ChromeOsDevice>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}";
            @Key
            private String customerId;
            @Key
            private String deviceId;
            @Key
            private String projection;
            
            protected Patch(final String customerId, final String deviceId, final ChromeOsDevice content) {
                super(Chromeosdevices.this.this$0, "PATCH", "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}", content, ChromeOsDevice.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Patch setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Patch setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Patch setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<ChromeOsDevice>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}";
            @Key
            private String customerId;
            @Key
            private String deviceId;
            @Key
            private String projection;
            
            protected Update(final String customerId, final String deviceId, final ChromeOsDevice content) {
                super(Chromeosdevices.this.this$0, "PUT", "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}", content, ChromeOsDevice.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Update setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getDeviceId() {
                return this.deviceId;
            }
            
            public Update setDeviceId(final String deviceId) {
                this.deviceId = deviceId;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Update setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Customer
    {
        final /* synthetic */ Directory this$0;
        
        public Devices devices() {
            return new Devices();
        }
        
        public class Devices
        {
            final /* synthetic */ Customer this$1;
            
            public Chromeos chromeos() {
                return new Chromeos();
            }
            
            public class Chromeos
            {
                final /* synthetic */ Devices this$2;
                
                public IssueCommand issueCommand(final String customerId, final String deviceId, final DirectoryChromeosdevicesIssueCommandRequest content) throws IOException {
                    final IssueCommand result = new IssueCommand(customerId, deviceId, content);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public Commands commands() {
                    return new Commands();
                }
                
                public class IssueCommand extends DirectoryRequest<DirectoryChromeosdevicesIssueCommandResponse>
                {
                    private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}:issueCommand";
                    @Key
                    private String customerId;
                    @Key
                    private String deviceId;
                    
                    protected IssueCommand(final String customerId, final String deviceId, final DirectoryChromeosdevicesIssueCommandRequest content) {
                        super(Chromeos.this.this$2.this$1.this$0, "POST", "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}:issueCommand", content, DirectoryChromeosdevicesIssueCommandResponse.class);
                        this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                        this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                    }
                    
                    @Override
                    public IssueCommand set$Xgafv(final String $Xgafv) {
                        return (IssueCommand)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public IssueCommand setAccessToken(final String accessToken) {
                        return (IssueCommand)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public IssueCommand setAlt(final String alt) {
                        return (IssueCommand)super.setAlt(alt);
                    }
                    
                    @Override
                    public IssueCommand setCallback(final String callback) {
                        return (IssueCommand)super.setCallback(callback);
                    }
                    
                    @Override
                    public IssueCommand setFields(final String fields) {
                        return (IssueCommand)super.setFields(fields);
                    }
                    
                    @Override
                    public IssueCommand setKey(final String key) {
                        return (IssueCommand)super.setKey(key);
                    }
                    
                    @Override
                    public IssueCommand setOauthToken(final String oauthToken) {
                        return (IssueCommand)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public IssueCommand setPrettyPrint(final Boolean prettyPrint) {
                        return (IssueCommand)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public IssueCommand setQuotaUser(final String quotaUser) {
                        return (IssueCommand)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public IssueCommand setUploadType(final String uploadType) {
                        return (IssueCommand)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public IssueCommand setUploadProtocol(final String uploadProtocol) {
                        return (IssueCommand)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getCustomerId() {
                        return this.customerId;
                    }
                    
                    public IssueCommand setCustomerId(final String customerId) {
                        this.customerId = customerId;
                        return this;
                    }
                    
                    public String getDeviceId() {
                        return this.deviceId;
                    }
                    
                    public IssueCommand setDeviceId(final String deviceId) {
                        this.deviceId = deviceId;
                        return this;
                    }
                    
                    @Override
                    public IssueCommand set(final String parameterName, final Object value) {
                        return (IssueCommand)super.set(parameterName, value);
                    }
                }
                
                public class Commands
                {
                    final /* synthetic */ Chromeos this$3;
                    
                    public Get get(final String customerId, final String deviceId, final Long commandId) throws IOException {
                        final Get result = new Get(customerId, deviceId, commandId);
                        Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                        return result;
                    }
                    
                    public class Get extends DirectoryRequest<DirectoryChromeosdevicesCommand>
                    {
                        private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}/commands/{commandId}";
                        @Key
                        private String customerId;
                        @Key
                        private String deviceId;
                        @Key
                        private Long commandId;
                        
                        protected Get(final String customerId, final String deviceId, final Long commandId) {
                            super(Commands.this.this$3.this$2.this$1.this$0, "GET", "admin/directory/v1/customer/{customerId}/devices/chromeos/{deviceId}/commands/{commandId}", null, DirectoryChromeosdevicesCommand.class);
                            this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                            this.deviceId = (String)Preconditions.checkNotNull((Object)deviceId, (Object)"Required parameter deviceId must be specified.");
                            this.commandId = (Long)Preconditions.checkNotNull((Object)commandId, (Object)"Required parameter commandId must be specified.");
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
                        
                        public String getCustomerId() {
                            return this.customerId;
                        }
                        
                        public Get setCustomerId(final String customerId) {
                            this.customerId = customerId;
                            return this;
                        }
                        
                        public String getDeviceId() {
                            return this.deviceId;
                        }
                        
                        public Get setDeviceId(final String deviceId) {
                            this.deviceId = deviceId;
                            return this;
                        }
                        
                        public Long getCommandId() {
                            return this.commandId;
                        }
                        
                        public Get setCommandId(final Long commandId) {
                            this.commandId = commandId;
                            return this;
                        }
                        
                        @Override
                        public Get set(final String parameterName, final Object value) {
                            return (Get)super.set(parameterName, value);
                        }
                    }
                }
            }
        }
    }
    
    public class Customers
    {
        final /* synthetic */ Directory this$0;
        
        public Get get(final String customerKey) throws IOException {
            final Get result = new Get(customerKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String customerKey, final com.google.api.services.directory.model.Customer content) throws IOException {
            final Patch result = new Patch(customerKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String customerKey, final com.google.api.services.directory.model.Customer content) throws IOException {
            final Update result = new Update(customerKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Chrome chrome() {
            return new Chrome();
        }
        
        public class Get extends DirectoryRequest<com.google.api.services.directory.model.Customer>
        {
            private static final String REST_PATH = "admin/directory/v1/customers/{customerKey}";
            @Key
            private String customerKey;
            
            protected Get(final String customerKey) {
                super(Customers.this.this$0, "GET", "admin/directory/v1/customers/{customerKey}", null, com.google.api.services.directory.model.Customer.class);
                this.customerKey = (String)Preconditions.checkNotNull((Object)customerKey, (Object)"Required parameter customerKey must be specified.");
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
            
            public String getCustomerKey() {
                return this.customerKey;
            }
            
            public Get setCustomerKey(final String customerKey) {
                this.customerKey = customerKey;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<com.google.api.services.directory.model.Customer>
        {
            private static final String REST_PATH = "admin/directory/v1/customers/{customerKey}";
            @Key
            private String customerKey;
            
            protected Patch(final String customerKey, final com.google.api.services.directory.model.Customer content) {
                super(Customers.this.this$0, "PATCH", "admin/directory/v1/customers/{customerKey}", content, com.google.api.services.directory.model.Customer.class);
                this.customerKey = (String)Preconditions.checkNotNull((Object)customerKey, (Object)"Required parameter customerKey must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerKey() {
                return this.customerKey;
            }
            
            public Patch setCustomerKey(final String customerKey) {
                this.customerKey = customerKey;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<com.google.api.services.directory.model.Customer>
        {
            private static final String REST_PATH = "admin/directory/v1/customers/{customerKey}";
            @Key
            private String customerKey;
            
            protected Update(final String customerKey, final com.google.api.services.directory.model.Customer content) {
                super(Customers.this.this$0, "PUT", "admin/directory/v1/customers/{customerKey}", content, com.google.api.services.directory.model.Customer.class);
                this.customerKey = (String)Preconditions.checkNotNull((Object)customerKey, (Object)"Required parameter customerKey must be specified.");
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
            
            public String getCustomerKey() {
                return this.customerKey;
            }
            
            public Update setCustomerKey(final String customerKey) {
                this.customerKey = customerKey;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
        
        public class Chrome
        {
            final /* synthetic */ Customers this$1;
            
            public Printers printers() {
                return new Printers();
            }
            
            public class Printers
            {
                final /* synthetic */ Chrome this$2;
                
                public BatchCreatePrinters batchCreatePrinters(final String parent, final BatchCreatePrintersRequest content) throws IOException {
                    final BatchCreatePrinters result = new BatchCreatePrinters(parent, content);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public BatchDeletePrinters batchDeletePrinters(final String parent, final BatchDeletePrintersRequest content) throws IOException {
                    final BatchDeletePrinters result = new BatchDeletePrinters(parent, content);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public Create create(final String parent, final Printer content) throws IOException {
                    final Create result = new Create(parent, content);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public Delete delete(final String name) throws IOException {
                    final Delete result = new Delete(name);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public Get get(final String name) throws IOException {
                    final Get result = new Get(name);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public List list(final String parent) throws IOException {
                    final List result = new List(parent);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public ListPrinterModels listPrinterModels(final String parent) throws IOException {
                    final ListPrinterModels result = new ListPrinterModels(parent);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public Patch patch(final String name, final Printer content) throws IOException {
                    final Patch result = new Patch(name, content);
                    Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                    return result;
                }
                
                public class BatchCreatePrinters extends DirectoryRequest<BatchCreatePrintersResponse>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+parent}/chrome/printers:batchCreatePrinters";
                    private final Pattern PARENT_PATTERN;
                    @Key
                    private String parent;
                    
                    protected BatchCreatePrinters(final String parent, final BatchCreatePrintersRequest content) {
                        super(Printers.this.this$2.this$1.this$0, "POST", "admin/directory/v1/{+parent}/chrome/printers:batchCreatePrinters", content, BatchCreatePrintersResponse.class);
                        this.PARENT_PATTERN = Pattern.compile("^customers/[^/]+$");
                        this.parent = (String)Preconditions.checkNotNull((Object)parent, (Object)"Required parameter parent must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                    }
                    
                    @Override
                    public BatchCreatePrinters set$Xgafv(final String $Xgafv) {
                        return (BatchCreatePrinters)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public BatchCreatePrinters setAccessToken(final String accessToken) {
                        return (BatchCreatePrinters)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public BatchCreatePrinters setAlt(final String alt) {
                        return (BatchCreatePrinters)super.setAlt(alt);
                    }
                    
                    @Override
                    public BatchCreatePrinters setCallback(final String callback) {
                        return (BatchCreatePrinters)super.setCallback(callback);
                    }
                    
                    @Override
                    public BatchCreatePrinters setFields(final String fields) {
                        return (BatchCreatePrinters)super.setFields(fields);
                    }
                    
                    @Override
                    public BatchCreatePrinters setKey(final String key) {
                        return (BatchCreatePrinters)super.setKey(key);
                    }
                    
                    @Override
                    public BatchCreatePrinters setOauthToken(final String oauthToken) {
                        return (BatchCreatePrinters)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public BatchCreatePrinters setPrettyPrint(final Boolean prettyPrint) {
                        return (BatchCreatePrinters)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public BatchCreatePrinters setQuotaUser(final String quotaUser) {
                        return (BatchCreatePrinters)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public BatchCreatePrinters setUploadType(final String uploadType) {
                        return (BatchCreatePrinters)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public BatchCreatePrinters setUploadProtocol(final String uploadProtocol) {
                        return (BatchCreatePrinters)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getParent() {
                        return this.parent;
                    }
                    
                    public BatchCreatePrinters setParent(final String parent) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                        this.parent = parent;
                        return this;
                    }
                    
                    @Override
                    public BatchCreatePrinters set(final String parameterName, final Object value) {
                        return (BatchCreatePrinters)super.set(parameterName, value);
                    }
                }
                
                public class BatchDeletePrinters extends DirectoryRequest<BatchDeletePrintersResponse>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+parent}/chrome/printers:batchDeletePrinters";
                    private final Pattern PARENT_PATTERN;
                    @Key
                    private String parent;
                    
                    protected BatchDeletePrinters(final String parent, final BatchDeletePrintersRequest content) {
                        super(Printers.this.this$2.this$1.this$0, "POST", "admin/directory/v1/{+parent}/chrome/printers:batchDeletePrinters", content, BatchDeletePrintersResponse.class);
                        this.PARENT_PATTERN = Pattern.compile("^customers/[^/]+$");
                        this.parent = (String)Preconditions.checkNotNull((Object)parent, (Object)"Required parameter parent must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                    }
                    
                    @Override
                    public BatchDeletePrinters set$Xgafv(final String $Xgafv) {
                        return (BatchDeletePrinters)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public BatchDeletePrinters setAccessToken(final String accessToken) {
                        return (BatchDeletePrinters)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public BatchDeletePrinters setAlt(final String alt) {
                        return (BatchDeletePrinters)super.setAlt(alt);
                    }
                    
                    @Override
                    public BatchDeletePrinters setCallback(final String callback) {
                        return (BatchDeletePrinters)super.setCallback(callback);
                    }
                    
                    @Override
                    public BatchDeletePrinters setFields(final String fields) {
                        return (BatchDeletePrinters)super.setFields(fields);
                    }
                    
                    @Override
                    public BatchDeletePrinters setKey(final String key) {
                        return (BatchDeletePrinters)super.setKey(key);
                    }
                    
                    @Override
                    public BatchDeletePrinters setOauthToken(final String oauthToken) {
                        return (BatchDeletePrinters)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public BatchDeletePrinters setPrettyPrint(final Boolean prettyPrint) {
                        return (BatchDeletePrinters)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public BatchDeletePrinters setQuotaUser(final String quotaUser) {
                        return (BatchDeletePrinters)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public BatchDeletePrinters setUploadType(final String uploadType) {
                        return (BatchDeletePrinters)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public BatchDeletePrinters setUploadProtocol(final String uploadProtocol) {
                        return (BatchDeletePrinters)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getParent() {
                        return this.parent;
                    }
                    
                    public BatchDeletePrinters setParent(final String parent) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                        this.parent = parent;
                        return this;
                    }
                    
                    @Override
                    public BatchDeletePrinters set(final String parameterName, final Object value) {
                        return (BatchDeletePrinters)super.set(parameterName, value);
                    }
                }
                
                public class Create extends DirectoryRequest<Printer>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+parent}/chrome/printers";
                    private final Pattern PARENT_PATTERN;
                    @Key
                    private String parent;
                    
                    protected Create(final String parent, final Printer content) {
                        super(Printers.this.this$2.this$1.this$0, "POST", "admin/directory/v1/{+parent}/chrome/printers", content, Printer.class);
                        this.PARENT_PATTERN = Pattern.compile("^customers/[^/]+$");
                        this.parent = (String)Preconditions.checkNotNull((Object)parent, (Object)"Required parameter parent must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                    }
                    
                    @Override
                    public Create set$Xgafv(final String $Xgafv) {
                        return (Create)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public Create setAccessToken(final String accessToken) {
                        return (Create)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public Create setAlt(final String alt) {
                        return (Create)super.setAlt(alt);
                    }
                    
                    @Override
                    public Create setCallback(final String callback) {
                        return (Create)super.setCallback(callback);
                    }
                    
                    @Override
                    public Create setFields(final String fields) {
                        return (Create)super.setFields(fields);
                    }
                    
                    @Override
                    public Create setKey(final String key) {
                        return (Create)super.setKey(key);
                    }
                    
                    @Override
                    public Create setOauthToken(final String oauthToken) {
                        return (Create)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public Create setPrettyPrint(final Boolean prettyPrint) {
                        return (Create)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public Create setQuotaUser(final String quotaUser) {
                        return (Create)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public Create setUploadType(final String uploadType) {
                        return (Create)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public Create setUploadProtocol(final String uploadProtocol) {
                        return (Create)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getParent() {
                        return this.parent;
                    }
                    
                    public Create setParent(final String parent) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                        this.parent = parent;
                        return this;
                    }
                    
                    @Override
                    public Create set(final String parameterName, final Object value) {
                        return (Create)super.set(parameterName, value);
                    }
                }
                
                public class Delete extends DirectoryRequest<Empty>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+name}";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    
                    protected Delete(final String name) {
                        super(Printers.this.this$2.this$1.this$0, "DELETE", "admin/directory/v1/{+name}", null, Empty.class);
                        this.NAME_PATTERN = Pattern.compile("^customers/[^/]+/chrome/printers/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)name, (Object)"Required parameter name must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
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
                    
                    public String getName() {
                        return this.name;
                    }
                    
                    public Delete setName(final String name) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    @Override
                    public Delete set(final String parameterName, final Object value) {
                        return (Delete)super.set(parameterName, value);
                    }
                }
                
                public class Get extends DirectoryRequest<Printer>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+name}";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    
                    protected Get(final String name) {
                        super(Printers.this.this$2.this$1.this$0, "GET", "admin/directory/v1/{+name}", null, Printer.class);
                        this.NAME_PATTERN = Pattern.compile("^customers/[^/]+/chrome/printers/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)name, (Object)"Required parameter name must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
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
                    
                    public String getName() {
                        return this.name;
                    }
                    
                    public Get setName(final String name) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    @Override
                    public Get set(final String parameterName, final Object value) {
                        return (Get)super.set(parameterName, value);
                    }
                }
                
                public class List extends DirectoryRequest<ListPrintersResponse>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+parent}/chrome/printers";
                    private final Pattern PARENT_PATTERN;
                    @Key
                    private String parent;
                    @Key
                    private String filter;
                    @Key
                    private String orgUnitId;
                    @Key
                    private Integer pageSize;
                    @Key
                    private String pageToken;
                    
                    protected List(final String parent) {
                        super(Printers.this.this$2.this$1.this$0, "GET", "admin/directory/v1/{+parent}/chrome/printers", null, ListPrintersResponse.class);
                        this.PARENT_PATTERN = Pattern.compile("^customers/[^/]+$");
                        this.parent = (String)Preconditions.checkNotNull((Object)parent, (Object)"Required parameter parent must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
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
                    
                    public String getParent() {
                        return this.parent;
                    }
                    
                    public List setParent(final String parent) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                        this.parent = parent;
                        return this;
                    }
                    
                    public String getFilter() {
                        return this.filter;
                    }
                    
                    public List setFilter(final String filter) {
                        this.filter = filter;
                        return this;
                    }
                    
                    public String getOrgUnitId() {
                        return this.orgUnitId;
                    }
                    
                    public List setOrgUnitId(final String orgUnitId) {
                        this.orgUnitId = orgUnitId;
                        return this;
                    }
                    
                    public Integer getPageSize() {
                        return this.pageSize;
                    }
                    
                    public List setPageSize(final Integer pageSize) {
                        this.pageSize = pageSize;
                        return this;
                    }
                    
                    public String getPageToken() {
                        return this.pageToken;
                    }
                    
                    public List setPageToken(final String pageToken) {
                        this.pageToken = pageToken;
                        return this;
                    }
                    
                    @Override
                    public List set(final String parameterName, final Object value) {
                        return (List)super.set(parameterName, value);
                    }
                }
                
                public class ListPrinterModels extends DirectoryRequest<ListPrinterModelsResponse>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+parent}/chrome/printers:listPrinterModels";
                    private final Pattern PARENT_PATTERN;
                    @Key
                    private String parent;
                    @Key
                    private String filter;
                    @Key
                    private Integer pageSize;
                    @Key
                    private String pageToken;
                    
                    protected ListPrinterModels(final String parent) {
                        super(Printers.this.this$2.this$1.this$0, "GET", "admin/directory/v1/{+parent}/chrome/printers:listPrinterModels", null, ListPrinterModelsResponse.class);
                        this.PARENT_PATTERN = Pattern.compile("^customers/[^/]+$");
                        this.parent = (String)Preconditions.checkNotNull((Object)parent, (Object)"Required parameter parent must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                    }
                    
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }
                    
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }
                    
                    @Override
                    public ListPrinterModels set$Xgafv(final String $Xgafv) {
                        return (ListPrinterModels)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public ListPrinterModels setAccessToken(final String accessToken) {
                        return (ListPrinterModels)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public ListPrinterModels setAlt(final String alt) {
                        return (ListPrinterModels)super.setAlt(alt);
                    }
                    
                    @Override
                    public ListPrinterModels setCallback(final String callback) {
                        return (ListPrinterModels)super.setCallback(callback);
                    }
                    
                    @Override
                    public ListPrinterModels setFields(final String fields) {
                        return (ListPrinterModels)super.setFields(fields);
                    }
                    
                    @Override
                    public ListPrinterModels setKey(final String key) {
                        return (ListPrinterModels)super.setKey(key);
                    }
                    
                    @Override
                    public ListPrinterModels setOauthToken(final String oauthToken) {
                        return (ListPrinterModels)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public ListPrinterModels setPrettyPrint(final Boolean prettyPrint) {
                        return (ListPrinterModels)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public ListPrinterModels setQuotaUser(final String quotaUser) {
                        return (ListPrinterModels)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public ListPrinterModels setUploadType(final String uploadType) {
                        return (ListPrinterModels)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public ListPrinterModels setUploadProtocol(final String uploadProtocol) {
                        return (ListPrinterModels)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getParent() {
                        return this.parent;
                    }
                    
                    public ListPrinterModels setParent(final String parent) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^customers/[^/]+$");
                        }
                        this.parent = parent;
                        return this;
                    }
                    
                    public String getFilter() {
                        return this.filter;
                    }
                    
                    public ListPrinterModels setFilter(final String filter) {
                        this.filter = filter;
                        return this;
                    }
                    
                    public Integer getPageSize() {
                        return this.pageSize;
                    }
                    
                    public ListPrinterModels setPageSize(final Integer pageSize) {
                        this.pageSize = pageSize;
                        return this;
                    }
                    
                    public String getPageToken() {
                        return this.pageToken;
                    }
                    
                    public ListPrinterModels setPageToken(final String pageToken) {
                        this.pageToken = pageToken;
                        return this;
                    }
                    
                    @Override
                    public ListPrinterModels set(final String parameterName, final Object value) {
                        return (ListPrinterModels)super.set(parameterName, value);
                    }
                }
                
                public class Patch extends DirectoryRequest<Printer>
                {
                    private static final String REST_PATH = "admin/directory/v1/{+name}";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    @Key
                    private String clearMask;
                    @Key
                    private String updateMask;
                    
                    protected Patch(final String name, final Printer content) {
                        super(Printers.this.this$2.this$1.this$0, "PATCH", "admin/directory/v1/{+name}", content, Printer.class);
                        this.NAME_PATTERN = Pattern.compile("^customers/[^/]+/chrome/printers/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)name, (Object)"Required parameter name must be specified.");
                        if (!Printers.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
                    }
                    
                    @Override
                    public Patch set$Xgafv(final String $Xgafv) {
                        return (Patch)super.set$Xgafv($Xgafv);
                    }
                    
                    @Override
                    public Patch setAccessToken(final String accessToken) {
                        return (Patch)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public Patch setAlt(final String alt) {
                        return (Patch)super.setAlt(alt);
                    }
                    
                    @Override
                    public Patch setCallback(final String callback) {
                        return (Patch)super.setCallback(callback);
                    }
                    
                    @Override
                    public Patch setFields(final String fields) {
                        return (Patch)super.setFields(fields);
                    }
                    
                    @Override
                    public Patch setKey(final String key) {
                        return (Patch)super.setKey(key);
                    }
                    
                    @Override
                    public Patch setOauthToken(final String oauthToken) {
                        return (Patch)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public Patch setPrettyPrint(final Boolean prettyPrint) {
                        return (Patch)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public Patch setQuotaUser(final String quotaUser) {
                        return (Patch)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public Patch setUploadType(final String uploadType) {
                        return (Patch)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public Patch setUploadProtocol(final String uploadProtocol) {
                        return (Patch)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getName() {
                        return this.name;
                    }
                    
                    public Patch setName(final String name) {
                        if (!Directory.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^customers/[^/]+/chrome/printers/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    public String getClearMask() {
                        return this.clearMask;
                    }
                    
                    public Patch setClearMask(final String clearMask) {
                        this.clearMask = clearMask;
                        return this;
                    }
                    
                    public String getUpdateMask() {
                        return this.updateMask;
                    }
                    
                    public Patch setUpdateMask(final String updateMask) {
                        this.updateMask = updateMask;
                        return this;
                    }
                    
                    @Override
                    public Patch set(final String parameterName, final Object value) {
                        return (Patch)super.set(parameterName, value);
                    }
                }
            }
        }
    }
    
    public class DomainAliases
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customer, final String domainAliasName) throws IOException {
            final Delete result = new Delete(customer, domainAliasName);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customer, final String domainAliasName) throws IOException {
            final Get result = new Get(customer, domainAliasName);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customer, final DomainAlias content) throws IOException {
            final Insert result = new Insert(customer, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customer) throws IOException {
            final List result = new List(customer);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domainaliases/{domainAliasName}";
            @Key
            private String customer;
            @Key
            private String domainAliasName;
            
            protected Delete(final String customer, final String domainAliasName) {
                super(DomainAliases.this.this$0, "DELETE", "admin/directory/v1/customer/{customer}/domainaliases/{domainAliasName}", null, Void.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.domainAliasName = (String)Preconditions.checkNotNull((Object)domainAliasName, (Object)"Required parameter domainAliasName must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Delete setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomainAliasName() {
                return this.domainAliasName;
            }
            
            public Delete setDomainAliasName(final String domainAliasName) {
                this.domainAliasName = domainAliasName;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<DomainAlias>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domainaliases/{domainAliasName}";
            @Key
            private String customer;
            @Key
            private String domainAliasName;
            
            protected Get(final String customer, final String domainAliasName) {
                super(DomainAliases.this.this$0, "GET", "admin/directory/v1/customer/{customer}/domainaliases/{domainAliasName}", null, DomainAlias.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.domainAliasName = (String)Preconditions.checkNotNull((Object)domainAliasName, (Object)"Required parameter domainAliasName must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Get setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomainAliasName() {
                return this.domainAliasName;
            }
            
            public Get setDomainAliasName(final String domainAliasName) {
                this.domainAliasName = domainAliasName;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<DomainAlias>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domainaliases";
            @Key
            private String customer;
            
            protected Insert(final String customer, final DomainAlias content) {
                super(DomainAliases.this.this$0, "POST", "admin/directory/v1/customer/{customer}/domainaliases", content, DomainAlias.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Insert setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.DomainAliases>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domainaliases";
            @Key
            private String customer;
            @Key
            private String parentDomainName;
            
            protected List(final String customer) {
                super(DomainAliases.this.this$0, "GET", "admin/directory/v1/customer/{customer}/domainaliases", null, com.google.api.services.directory.model.DomainAliases.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getParentDomainName() {
                return this.parentDomainName;
            }
            
            public List setParentDomainName(final String parentDomainName) {
                this.parentDomainName = parentDomainName;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Domains
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customer, final String domainName) throws IOException {
            final Delete result = new Delete(customer, domainName);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customer, final String domainName) throws IOException {
            final Get result = new Get(customer, domainName);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customer, final com.google.api.services.directory.model.Domains content) throws IOException {
            final Insert result = new Insert(customer, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customer) throws IOException {
            final List result = new List(customer);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domains/{domainName}";
            @Key
            private String customer;
            @Key
            private String domainName;
            
            protected Delete(final String customer, final String domainName) {
                super(Domains.this.this$0, "DELETE", "admin/directory/v1/customer/{customer}/domains/{domainName}", null, Void.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.domainName = (String)Preconditions.checkNotNull((Object)domainName, (Object)"Required parameter domainName must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Delete setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomainName() {
                return this.domainName;
            }
            
            public Delete setDomainName(final String domainName) {
                this.domainName = domainName;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<com.google.api.services.directory.model.Domains>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domains/{domainName}";
            @Key
            private String customer;
            @Key
            private String domainName;
            
            protected Get(final String customer, final String domainName) {
                super(Domains.this.this$0, "GET", "admin/directory/v1/customer/{customer}/domains/{domainName}", null, com.google.api.services.directory.model.Domains.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.domainName = (String)Preconditions.checkNotNull((Object)domainName, (Object)"Required parameter domainName must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Get setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomainName() {
                return this.domainName;
            }
            
            public Get setDomainName(final String domainName) {
                this.domainName = domainName;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<com.google.api.services.directory.model.Domains>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domains";
            @Key
            private String customer;
            
            protected Insert(final String customer, final com.google.api.services.directory.model.Domains content) {
                super(Domains.this.this$0, "POST", "admin/directory/v1/customer/{customer}/domains", content, com.google.api.services.directory.model.Domains.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getDomainName(), "Domains.getDomainName()");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Insert setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<Domains2>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/domains";
            @Key
            private String customer;
            
            protected List(final String customer) {
                super(Domains.this.this$0, "GET", "admin/directory/v1/customer/{customer}/domains", null, Domains2.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Groups
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String groupKey) throws IOException {
            final Delete result = new Delete(groupKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String groupKey) throws IOException {
            final Get result = new Get(groupKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final Group content) throws IOException {
            final Insert result = new Insert(content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list() throws IOException {
            final List result = new List();
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String groupKey, final Group content) throws IOException {
            final Patch result = new Patch(groupKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String groupKey, final Group content) throws IOException {
            final Update result = new Update(groupKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Aliases aliases() {
            return new Aliases();
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}";
            @Key
            private String groupKey;
            
            protected Delete(final String groupKey) {
                super(Groups.this.this$0, "DELETE", "admin/directory/v1/groups/{groupKey}", null, Void.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Delete setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Group>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}";
            @Key
            private String groupKey;
            
            protected Get(final String groupKey) {
                super(Groups.this.this$0, "GET", "admin/directory/v1/groups/{groupKey}", null, Group.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Get setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<Group>
        {
            private static final String REST_PATH = "admin/directory/v1/groups";
            
            protected Insert(final Group content) {
                super(Groups.this.this$0, "POST", "admin/directory/v1/groups", content, Group.class);
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getEmail(), "Group.getEmail()");
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
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Groups>
        {
            private static final String REST_PATH = "admin/directory/v1/groups";
            @Key
            private String customer;
            @Key
            private String domain;
            @Key
            private Integer maxResults;
            @Key
            private String orderBy;
            @Key
            private String pageToken;
            @Key
            private String query;
            @Key
            private String sortOrder;
            @Key
            private String userKey;
            
            protected List() {
                super(Groups.this.this$0, "GET", "admin/directory/v1/groups", null, com.google.api.services.directory.model.Groups.class);
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomain() {
                return this.domain;
            }
            
            public List setDomain(final String domain) {
                this.domain = domain;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getOrderBy() {
                return this.orderBy;
            }
            
            public List setOrderBy(final String orderBy) {
                this.orderBy = orderBy;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public List setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getSortOrder() {
                return this.sortOrder;
            }
            
            public List setSortOrder(final String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public List setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<Group>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}";
            @Key
            private String groupKey;
            
            protected Patch(final String groupKey, final Group content) {
                super(Groups.this.this$0, "PATCH", "admin/directory/v1/groups/{groupKey}", content, Group.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Patch setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<Group>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}";
            @Key
            private String groupKey;
            
            protected Update(final String groupKey, final Group content) {
                super(Groups.this.this$0, "PUT", "admin/directory/v1/groups/{groupKey}", content, Group.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Update setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
        
        public class Aliases
        {
            final /* synthetic */ Groups this$1;
            
            public Delete delete(final String groupKey, final String alias) throws IOException {
                final Delete result = new Delete(groupKey, alias);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Insert insert(final String groupKey, final Alias content) throws IOException {
                final Insert result = new Insert(groupKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public List list(final String groupKey) throws IOException {
                final List result = new List(groupKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/aliases/{alias}";
                @Key
                private String groupKey;
                @Key
                private String alias;
                
                protected Delete(final String groupKey, final String alias) {
                    super(Aliases.this.this$1.this$0, "DELETE", "admin/directory/v1/groups/{groupKey}/aliases/{alias}", null, Void.class);
                    this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                    this.alias = (String)Preconditions.checkNotNull((Object)alias, (Object)"Required parameter alias must be specified.");
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
                
                public String getGroupKey() {
                    return this.groupKey;
                }
                
                public Delete setGroupKey(final String groupKey) {
                    this.groupKey = groupKey;
                    return this;
                }
                
                public String getAlias() {
                    return this.alias;
                }
                
                public Delete setAlias(final String alias) {
                    this.alias = alias;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Insert extends DirectoryRequest<Alias>
            {
                private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/aliases";
                @Key
                private String groupKey;
                
                protected Insert(final String groupKey, final Alias content) {
                    super(Aliases.this.this$1.this$0, "POST", "admin/directory/v1/groups/{groupKey}/aliases", content, Alias.class);
                    this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
                
                public String getGroupKey() {
                    return this.groupKey;
                }
                
                public Insert setGroupKey(final String groupKey) {
                    this.groupKey = groupKey;
                    return this;
                }
                
                @Override
                public Insert set(final String parameterName, final Object value) {
                    return (Insert)super.set(parameterName, value);
                }
            }
            
            public class List extends DirectoryRequest<com.google.api.services.directory.model.Aliases>
            {
                private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/aliases";
                @Key
                private String groupKey;
                
                protected List(final String groupKey) {
                    super(Aliases.this.this$1.this$0, "GET", "admin/directory/v1/groups/{groupKey}/aliases", null, com.google.api.services.directory.model.Aliases.class);
                    this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
                
                public String getGroupKey() {
                    return this.groupKey;
                }
                
                public List setGroupKey(final String groupKey) {
                    this.groupKey = groupKey;
                    return this;
                }
                
                @Override
                public List set(final String parameterName, final Object value) {
                    return (List)super.set(parameterName, value);
                }
            }
        }
    }
    
    public class Members
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String groupKey, final String memberKey) throws IOException {
            final Delete result = new Delete(groupKey, memberKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String groupKey, final String memberKey) throws IOException {
            final Get result = new Get(groupKey, memberKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public HasMember hasMember(final String groupKey, final String memberKey) throws IOException {
            final HasMember result = new HasMember(groupKey, memberKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String groupKey, final Member content) throws IOException {
            final Insert result = new Insert(groupKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String groupKey) throws IOException {
            final List result = new List(groupKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String groupKey, final String memberKey, final Member content) throws IOException {
            final Patch result = new Patch(groupKey, memberKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String groupKey, final String memberKey, final Member content) throws IOException {
            final Update result = new Update(groupKey, memberKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members/{memberKey}";
            @Key
            private String groupKey;
            @Key
            private String memberKey;
            
            protected Delete(final String groupKey, final String memberKey) {
                super(Members.this.this$0, "DELETE", "admin/directory/v1/groups/{groupKey}/members/{memberKey}", null, Void.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                this.memberKey = (String)Preconditions.checkNotNull((Object)memberKey, (Object)"Required parameter memberKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Delete setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public String getMemberKey() {
                return this.memberKey;
            }
            
            public Delete setMemberKey(final String memberKey) {
                this.memberKey = memberKey;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Member>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members/{memberKey}";
            @Key
            private String groupKey;
            @Key
            private String memberKey;
            
            protected Get(final String groupKey, final String memberKey) {
                super(Members.this.this$0, "GET", "admin/directory/v1/groups/{groupKey}/members/{memberKey}", null, Member.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                this.memberKey = (String)Preconditions.checkNotNull((Object)memberKey, (Object)"Required parameter memberKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Get setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public String getMemberKey() {
                return this.memberKey;
            }
            
            public Get setMemberKey(final String memberKey) {
                this.memberKey = memberKey;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class HasMember extends DirectoryRequest<MembersHasMember>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/hasMember/{memberKey}";
            @Key
            private String groupKey;
            @Key
            private String memberKey;
            
            protected HasMember(final String groupKey, final String memberKey) {
                super(Members.this.this$0, "GET", "admin/directory/v1/groups/{groupKey}/hasMember/{memberKey}", null, MembersHasMember.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                this.memberKey = (String)Preconditions.checkNotNull((Object)memberKey, (Object)"Required parameter memberKey must be specified.");
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public HasMember set$Xgafv(final String $Xgafv) {
                return (HasMember)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public HasMember setAccessToken(final String accessToken) {
                return (HasMember)super.setAccessToken(accessToken);
            }
            
            @Override
            public HasMember setAlt(final String alt) {
                return (HasMember)super.setAlt(alt);
            }
            
            @Override
            public HasMember setCallback(final String callback) {
                return (HasMember)super.setCallback(callback);
            }
            
            @Override
            public HasMember setFields(final String fields) {
                return (HasMember)super.setFields(fields);
            }
            
            @Override
            public HasMember setKey(final String key) {
                return (HasMember)super.setKey(key);
            }
            
            @Override
            public HasMember setOauthToken(final String oauthToken) {
                return (HasMember)super.setOauthToken(oauthToken);
            }
            
            @Override
            public HasMember setPrettyPrint(final Boolean prettyPrint) {
                return (HasMember)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public HasMember setQuotaUser(final String quotaUser) {
                return (HasMember)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public HasMember setUploadType(final String uploadType) {
                return (HasMember)super.setUploadType(uploadType);
            }
            
            @Override
            public HasMember setUploadProtocol(final String uploadProtocol) {
                return (HasMember)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public HasMember setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public String getMemberKey() {
                return this.memberKey;
            }
            
            public HasMember setMemberKey(final String memberKey) {
                this.memberKey = memberKey;
                return this;
            }
            
            @Override
            public HasMember set(final String parameterName, final Object value) {
                return (HasMember)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<Member>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members";
            @Key
            private String groupKey;
            
            protected Insert(final String groupKey, final Member content) {
                super(Members.this.this$0, "POST", "admin/directory/v1/groups/{groupKey}/members", content, Member.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Insert setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Members>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members";
            @Key
            private String groupKey;
            @Key
            private Boolean includeDerivedMembership;
            @Key
            private Integer maxResults;
            @Key
            private String pageToken;
            @Key
            private String roles;
            
            protected List(final String groupKey) {
                super(Members.this.this$0, "GET", "admin/directory/v1/groups/{groupKey}/members", null, com.google.api.services.directory.model.Members.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public List setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public Boolean getIncludeDerivedMembership() {
                return this.includeDerivedMembership;
            }
            
            public List setIncludeDerivedMembership(final Boolean includeDerivedMembership) {
                this.includeDerivedMembership = includeDerivedMembership;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getRoles() {
                return this.roles;
            }
            
            public List setRoles(final String roles) {
                this.roles = roles;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<Member>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members/{memberKey}";
            @Key
            private String groupKey;
            @Key
            private String memberKey;
            
            protected Patch(final String groupKey, final String memberKey, final Member content) {
                super(Members.this.this$0, "PATCH", "admin/directory/v1/groups/{groupKey}/members/{memberKey}", content, Member.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                this.memberKey = (String)Preconditions.checkNotNull((Object)memberKey, (Object)"Required parameter memberKey must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Patch setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public String getMemberKey() {
                return this.memberKey;
            }
            
            public Patch setMemberKey(final String memberKey) {
                this.memberKey = memberKey;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<Member>
        {
            private static final String REST_PATH = "admin/directory/v1/groups/{groupKey}/members/{memberKey}";
            @Key
            private String groupKey;
            @Key
            private String memberKey;
            
            protected Update(final String groupKey, final String memberKey, final Member content) {
                super(Members.this.this$0, "PUT", "admin/directory/v1/groups/{groupKey}/members/{memberKey}", content, Member.class);
                this.groupKey = (String)Preconditions.checkNotNull((Object)groupKey, (Object)"Required parameter groupKey must be specified.");
                this.memberKey = (String)Preconditions.checkNotNull((Object)memberKey, (Object)"Required parameter memberKey must be specified.");
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
            
            public String getGroupKey() {
                return this.groupKey;
            }
            
            public Update setGroupKey(final String groupKey) {
                this.groupKey = groupKey;
                return this;
            }
            
            public String getMemberKey() {
                return this.memberKey;
            }
            
            public Update setMemberKey(final String memberKey) {
                this.memberKey = memberKey;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Mobiledevices
    {
        final /* synthetic */ Directory this$0;
        
        public Action action(final String customerId, final String resourceId, final MobileDeviceAction content) throws IOException {
            final Action result = new Action(customerId, resourceId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Delete delete(final String customerId, final String resourceId) throws IOException {
            final Delete result = new Delete(customerId, resourceId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customerId, final String resourceId) throws IOException {
            final Get result = new Get(customerId, resourceId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customerId) throws IOException {
            final List result = new List(customerId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Action extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}/action";
            @Key
            private String customerId;
            @Key
            private String resourceId;
            
            protected Action(final String customerId, final String resourceId, final MobileDeviceAction content) {
                super(Mobiledevices.this.this$0, "POST", "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}/action", content, Void.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.resourceId = (String)Preconditions.checkNotNull((Object)resourceId, (Object)"Required parameter resourceId must be specified.");
            }
            
            @Override
            public Action set$Xgafv(final String $Xgafv) {
                return (Action)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Action setAccessToken(final String accessToken) {
                return (Action)super.setAccessToken(accessToken);
            }
            
            @Override
            public Action setAlt(final String alt) {
                return (Action)super.setAlt(alt);
            }
            
            @Override
            public Action setCallback(final String callback) {
                return (Action)super.setCallback(callback);
            }
            
            @Override
            public Action setFields(final String fields) {
                return (Action)super.setFields(fields);
            }
            
            @Override
            public Action setKey(final String key) {
                return (Action)super.setKey(key);
            }
            
            @Override
            public Action setOauthToken(final String oauthToken) {
                return (Action)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Action setPrettyPrint(final Boolean prettyPrint) {
                return (Action)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Action setQuotaUser(final String quotaUser) {
                return (Action)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Action setUploadType(final String uploadType) {
                return (Action)super.setUploadType(uploadType);
            }
            
            @Override
            public Action setUploadProtocol(final String uploadProtocol) {
                return (Action)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Action setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getResourceId() {
                return this.resourceId;
            }
            
            public Action setResourceId(final String resourceId) {
                this.resourceId = resourceId;
                return this;
            }
            
            @Override
            public Action set(final String parameterName, final Object value) {
                return (Action)super.set(parameterName, value);
            }
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}";
            @Key
            private String customerId;
            @Key
            private String resourceId;
            
            protected Delete(final String customerId, final String resourceId) {
                super(Mobiledevices.this.this$0, "DELETE", "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}", null, Void.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.resourceId = (String)Preconditions.checkNotNull((Object)resourceId, (Object)"Required parameter resourceId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Delete setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getResourceId() {
                return this.resourceId;
            }
            
            public Delete setResourceId(final String resourceId) {
                this.resourceId = resourceId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<MobileDevice>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}";
            @Key
            private String customerId;
            @Key
            private String resourceId;
            @Key
            private String projection;
            
            protected Get(final String customerId, final String resourceId) {
                super(Mobiledevices.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/devices/mobile/{resourceId}", null, MobileDevice.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.resourceId = (String)Preconditions.checkNotNull((Object)resourceId, (Object)"Required parameter resourceId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Get setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getResourceId() {
                return this.resourceId;
            }
            
            public Get setResourceId(final String resourceId) {
                this.resourceId = resourceId;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Get setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<MobileDevices>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/devices/mobile";
            @Key
            private String customerId;
            @Key
            private Integer maxResults;
            @Key
            private String orderBy;
            @Key
            private String pageToken;
            @Key
            private String projection;
            @Key
            private String query;
            @Key
            private String sortOrder;
            
            protected List(final String customerId) {
                super(Mobiledevices.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/devices/mobile", null, MobileDevices.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public List setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getOrderBy() {
                return this.orderBy;
            }
            
            public List setOrderBy(final String orderBy) {
                this.orderBy = orderBy;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public List setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public List setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getSortOrder() {
                return this.sortOrder;
            }
            
            public List setSortOrder(final String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Orgunits
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customerId, final String orgUnitPath) throws IOException {
            final Delete result = new Delete(customerId, orgUnitPath);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customerId, final String orgUnitPath) throws IOException {
            final Get result = new Get(customerId, orgUnitPath);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customerId, final OrgUnit content) throws IOException {
            final Insert result = new Insert(customerId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customerId) throws IOException {
            final List result = new List(customerId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String customerId, final String orgUnitPath, final OrgUnit content) throws IOException {
            final Patch result = new Patch(customerId, orgUnitPath, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String customerId, final String orgUnitPath, final OrgUnit content) throws IOException {
            final Update result = new Update(customerId, orgUnitPath, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}";
            private final Pattern ORG_UNIT_PATH_PATTERN;
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            
            protected Delete(final String customerId, final String orgUnitPath) {
                super(Orgunits.this.this$0, "DELETE", "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}", null, Void.class);
                this.ORG_UNIT_PATH_PATTERN = Pattern.compile("^.*$");
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.orgUnitPath = (String)Preconditions.checkNotNull((Object)orgUnitPath, (Object)"Required parameter orgUnitPath must be specified.");
                if (!Orgunits.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Delete setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public Delete setOrgUnitPath(final String orgUnitPath) {
                if (!Directory.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<OrgUnit>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}";
            private final Pattern ORG_UNIT_PATH_PATTERN;
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            
            protected Get(final String customerId, final String orgUnitPath) {
                super(Orgunits.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}", null, OrgUnit.class);
                this.ORG_UNIT_PATH_PATTERN = Pattern.compile("^.*$");
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.orgUnitPath = (String)Preconditions.checkNotNull((Object)orgUnitPath, (Object)"Required parameter orgUnitPath must be specified.");
                if (!Orgunits.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Get setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public Get setOrgUnitPath(final String orgUnitPath) {
                if (!Directory.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<OrgUnit>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits";
            @Key
            private String customerId;
            
            protected Insert(final String customerId, final OrgUnit content) {
                super(Orgunits.this.this$0, "POST", "admin/directory/v1/customer/{customerId}/orgunits", content, OrgUnit.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getName(), "OrgUnit.getName()");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Insert setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<OrgUnits>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits";
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            @Key
            private String type;
            
            protected List(final String customerId) {
                super(Orgunits.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/orgunits", null, OrgUnits.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public List setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public List setOrgUnitPath(final String orgUnitPath) {
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            public String getType() {
                return this.type;
            }
            
            public List setType(final String type) {
                this.type = type;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<OrgUnit>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}";
            private final Pattern ORG_UNIT_PATH_PATTERN;
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            
            protected Patch(final String customerId, final String orgUnitPath, final OrgUnit content) {
                super(Orgunits.this.this$0, "PATCH", "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}", content, OrgUnit.class);
                this.ORG_UNIT_PATH_PATTERN = Pattern.compile("^.*$");
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.orgUnitPath = (String)Preconditions.checkNotNull((Object)orgUnitPath, (Object)"Required parameter orgUnitPath must be specified.");
                if (!Orgunits.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Patch setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public Patch setOrgUnitPath(final String orgUnitPath) {
                if (!Directory.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<OrgUnit>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}";
            private final Pattern ORG_UNIT_PATH_PATTERN;
            @Key
            private String customerId;
            @Key
            private String orgUnitPath;
            
            protected Update(final String customerId, final String orgUnitPath, final OrgUnit content) {
                super(Orgunits.this.this$0, "PUT", "admin/directory/v1/customer/{customerId}/orgunits/{+orgUnitPath}", content, OrgUnit.class);
                this.ORG_UNIT_PATH_PATTERN = Pattern.compile("^.*$");
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.orgUnitPath = (String)Preconditions.checkNotNull((Object)orgUnitPath, (Object)"Required parameter orgUnitPath must be specified.");
                if (!Orgunits.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Update setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getOrgUnitPath() {
                return this.orgUnitPath;
            }
            
            public Update setOrgUnitPath(final String orgUnitPath) {
                if (!Directory.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.ORG_UNIT_PATH_PATTERN.matcher(orgUnitPath).matches(), (Object)"Parameter orgUnitPath must conform to the pattern ^.*$");
                }
                this.orgUnitPath = orgUnitPath;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Privileges
    {
        final /* synthetic */ Directory this$0;
        
        public List list(final String customer) throws IOException {
            final List result = new List(customer);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Privileges>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles/ALL/privileges";
            @Key
            private String customer;
            
            protected List(final String customer) {
                super(Privileges.this.this$0, "GET", "admin/directory/v1/customer/{customer}/roles/ALL/privileges", null, com.google.api.services.directory.model.Privileges.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Resources
    {
        final /* synthetic */ Directory this$0;
        
        public Buildings buildings() {
            return new Buildings();
        }
        
        public Calendars calendars() {
            return new Calendars();
        }
        
        public Features features() {
            return new Features();
        }
        
        public class Buildings
        {
            final /* synthetic */ Resources this$1;
            
            public Delete delete(final String customer, final String buildingId) throws IOException {
                final Delete result = new Delete(customer, buildingId);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Get get(final String customer, final String buildingId) throws IOException {
                final Get result = new Get(customer, buildingId);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Insert insert(final String customer, final Building content) throws IOException {
                final Insert result = new Insert(customer, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public List list(final String customer) throws IOException {
                final List result = new List(customer);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Patch patch(final String customer, final String buildingId, final Building content) throws IOException {
                final Patch result = new Patch(customer, buildingId, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Update update(final String customer, final String buildingId, final Building content) throws IOException {
                final Update result = new Update(customer, buildingId, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}";
                @Key
                private String customer;
                @Key
                private String buildingId;
                
                protected Delete(final String customer, final String buildingId) {
                    super(Buildings.this.this$1.this$0, "DELETE", "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}", null, Void.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.buildingId = (String)Preconditions.checkNotNull((Object)buildingId, (Object)"Required parameter buildingId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Delete setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getBuildingId() {
                    return this.buildingId;
                }
                
                public Delete setBuildingId(final String buildingId) {
                    this.buildingId = buildingId;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Get extends DirectoryRequest<Building>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}";
                @Key
                private String customer;
                @Key
                private String buildingId;
                
                protected Get(final String customer, final String buildingId) {
                    super(Buildings.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}", null, Building.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.buildingId = (String)Preconditions.checkNotNull((Object)buildingId, (Object)"Required parameter buildingId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Get setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getBuildingId() {
                    return this.buildingId;
                }
                
                public Get setBuildingId(final String buildingId) {
                    this.buildingId = buildingId;
                    return this;
                }
                
                @Override
                public Get set(final String parameterName, final Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }
            
            public class Insert extends DirectoryRequest<Building>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings";
                @Key
                private String customer;
                @Key
                private String coordinatesSource;
                
                protected Insert(final String customer, final Building content) {
                    super(Buildings.this.this$1.this$0, "POST", "admin/directory/v1/customer/{customer}/resources/buildings", content, Building.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Insert setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getCoordinatesSource() {
                    return this.coordinatesSource;
                }
                
                public Insert setCoordinatesSource(final String coordinatesSource) {
                    this.coordinatesSource = coordinatesSource;
                    return this;
                }
                
                @Override
                public Insert set(final String parameterName, final Object value) {
                    return (Insert)super.set(parameterName, value);
                }
            }
            
            public class List extends DirectoryRequest<com.google.api.services.directory.model.Buildings>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings";
                @Key
                private String customer;
                @Key
                private Integer maxResults;
                @Key
                private String pageToken;
                
                protected List(final String customer) {
                    super(Buildings.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/buildings", null, com.google.api.services.directory.model.Buildings.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public List setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public Integer getMaxResults() {
                    return this.maxResults;
                }
                
                public List setMaxResults(final Integer maxResults) {
                    this.maxResults = maxResults;
                    return this;
                }
                
                public String getPageToken() {
                    return this.pageToken;
                }
                
                public List setPageToken(final String pageToken) {
                    this.pageToken = pageToken;
                    return this;
                }
                
                @Override
                public List set(final String parameterName, final Object value) {
                    return (List)super.set(parameterName, value);
                }
            }
            
            public class Patch extends DirectoryRequest<Building>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}";
                @Key
                private String customer;
                @Key
                private String buildingId;
                @Key
                private String coordinatesSource;
                
                protected Patch(final String customer, final String buildingId, final Building content) {
                    super(Buildings.this.this$1.this$0, "PATCH", "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}", content, Building.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.buildingId = (String)Preconditions.checkNotNull((Object)buildingId, (Object)"Required parameter buildingId must be specified.");
                }
                
                @Override
                public Patch set$Xgafv(final String $Xgafv) {
                    return (Patch)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Patch setAccessToken(final String accessToken) {
                    return (Patch)super.setAccessToken(accessToken);
                }
                
                @Override
                public Patch setAlt(final String alt) {
                    return (Patch)super.setAlt(alt);
                }
                
                @Override
                public Patch setCallback(final String callback) {
                    return (Patch)super.setCallback(callback);
                }
                
                @Override
                public Patch setFields(final String fields) {
                    return (Patch)super.setFields(fields);
                }
                
                @Override
                public Patch setKey(final String key) {
                    return (Patch)super.setKey(key);
                }
                
                @Override
                public Patch setOauthToken(final String oauthToken) {
                    return (Patch)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Patch setPrettyPrint(final Boolean prettyPrint) {
                    return (Patch)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Patch setQuotaUser(final String quotaUser) {
                    return (Patch)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Patch setUploadType(final String uploadType) {
                    return (Patch)super.setUploadType(uploadType);
                }
                
                @Override
                public Patch setUploadProtocol(final String uploadProtocol) {
                    return (Patch)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Patch setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getBuildingId() {
                    return this.buildingId;
                }
                
                public Patch setBuildingId(final String buildingId) {
                    this.buildingId = buildingId;
                    return this;
                }
                
                public String getCoordinatesSource() {
                    return this.coordinatesSource;
                }
                
                public Patch setCoordinatesSource(final String coordinatesSource) {
                    this.coordinatesSource = coordinatesSource;
                    return this;
                }
                
                @Override
                public Patch set(final String parameterName, final Object value) {
                    return (Patch)super.set(parameterName, value);
                }
            }
            
            public class Update extends DirectoryRequest<Building>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}";
                @Key
                private String customer;
                @Key
                private String buildingId;
                @Key
                private String coordinatesSource;
                
                protected Update(final String customer, final String buildingId, final Building content) {
                    super(Buildings.this.this$1.this$0, "PUT", "admin/directory/v1/customer/{customer}/resources/buildings/{buildingId}", content, Building.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.buildingId = (String)Preconditions.checkNotNull((Object)buildingId, (Object)"Required parameter buildingId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Update setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getBuildingId() {
                    return this.buildingId;
                }
                
                public Update setBuildingId(final String buildingId) {
                    this.buildingId = buildingId;
                    return this;
                }
                
                public String getCoordinatesSource() {
                    return this.coordinatesSource;
                }
                
                public Update setCoordinatesSource(final String coordinatesSource) {
                    this.coordinatesSource = coordinatesSource;
                    return this;
                }
                
                @Override
                public Update set(final String parameterName, final Object value) {
                    return (Update)super.set(parameterName, value);
                }
            }
        }
        
        public class Calendars
        {
            final /* synthetic */ Resources this$1;
            
            public Delete delete(final String customer, final String calendarResourceId) throws IOException {
                final Delete result = new Delete(customer, calendarResourceId);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Get get(final String customer, final String calendarResourceId) throws IOException {
                final Get result = new Get(customer, calendarResourceId);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Insert insert(final String customer, final CalendarResource content) throws IOException {
                final Insert result = new Insert(customer, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public List list(final String customer) throws IOException {
                final List result = new List(customer);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Patch patch(final String customer, final String calendarResourceId, final CalendarResource content) throws IOException {
                final Patch result = new Patch(customer, calendarResourceId, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Update update(final String customer, final String calendarResourceId, final CalendarResource content) throws IOException {
                final Update result = new Update(customer, calendarResourceId, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}";
                @Key
                private String customer;
                @Key
                private String calendarResourceId;
                
                protected Delete(final String customer, final String calendarResourceId) {
                    super(Calendars.this.this$1.this$0, "DELETE", "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}", null, Void.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.calendarResourceId = (String)Preconditions.checkNotNull((Object)calendarResourceId, (Object)"Required parameter calendarResourceId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Delete setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getCalendarResourceId() {
                    return this.calendarResourceId;
                }
                
                public Delete setCalendarResourceId(final String calendarResourceId) {
                    this.calendarResourceId = calendarResourceId;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Get extends DirectoryRequest<CalendarResource>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}";
                @Key
                private String customer;
                @Key
                private String calendarResourceId;
                
                protected Get(final String customer, final String calendarResourceId) {
                    super(Calendars.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}", null, CalendarResource.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.calendarResourceId = (String)Preconditions.checkNotNull((Object)calendarResourceId, (Object)"Required parameter calendarResourceId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Get setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getCalendarResourceId() {
                    return this.calendarResourceId;
                }
                
                public Get setCalendarResourceId(final String calendarResourceId) {
                    this.calendarResourceId = calendarResourceId;
                    return this;
                }
                
                @Override
                public Get set(final String parameterName, final Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }
            
            public class Insert extends DirectoryRequest<CalendarResource>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars";
                @Key
                private String customer;
                
                protected Insert(final String customer, final CalendarResource content) {
                    super(Calendars.this.this$1.this$0, "POST", "admin/directory/v1/customer/{customer}/resources/calendars", content, CalendarResource.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.checkRequiredParameter((Object)content, "content");
                    this.checkRequiredParameter((Object)content.getResourceId(), "CalendarResource.getResourceId()");
                    this.checkRequiredParameter((Object)content, "content");
                    this.checkRequiredParameter((Object)content.getResourceName(), "CalendarResource.getResourceName()");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Insert setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                @Override
                public Insert set(final String parameterName, final Object value) {
                    return (Insert)super.set(parameterName, value);
                }
            }
            
            public class List extends DirectoryRequest<CalendarResources>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars";
                @Key
                private String customer;
                @Key
                private Integer maxResults;
                @Key
                private String orderBy;
                @Key
                private String pageToken;
                @Key
                private String query;
                
                protected List(final String customer) {
                    super(Calendars.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/calendars", null, CalendarResources.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public List setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public Integer getMaxResults() {
                    return this.maxResults;
                }
                
                public List setMaxResults(final Integer maxResults) {
                    this.maxResults = maxResults;
                    return this;
                }
                
                public String getOrderBy() {
                    return this.orderBy;
                }
                
                public List setOrderBy(final String orderBy) {
                    this.orderBy = orderBy;
                    return this;
                }
                
                public String getPageToken() {
                    return this.pageToken;
                }
                
                public List setPageToken(final String pageToken) {
                    this.pageToken = pageToken;
                    return this;
                }
                
                public String getQuery() {
                    return this.query;
                }
                
                public List setQuery(final String query) {
                    this.query = query;
                    return this;
                }
                
                @Override
                public List set(final String parameterName, final Object value) {
                    return (List)super.set(parameterName, value);
                }
            }
            
            public class Patch extends DirectoryRequest<CalendarResource>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}";
                @Key
                private String customer;
                @Key
                private String calendarResourceId;
                
                protected Patch(final String customer, final String calendarResourceId, final CalendarResource content) {
                    super(Calendars.this.this$1.this$0, "PATCH", "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}", content, CalendarResource.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.calendarResourceId = (String)Preconditions.checkNotNull((Object)calendarResourceId, (Object)"Required parameter calendarResourceId must be specified.");
                }
                
                @Override
                public Patch set$Xgafv(final String $Xgafv) {
                    return (Patch)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Patch setAccessToken(final String accessToken) {
                    return (Patch)super.setAccessToken(accessToken);
                }
                
                @Override
                public Patch setAlt(final String alt) {
                    return (Patch)super.setAlt(alt);
                }
                
                @Override
                public Patch setCallback(final String callback) {
                    return (Patch)super.setCallback(callback);
                }
                
                @Override
                public Patch setFields(final String fields) {
                    return (Patch)super.setFields(fields);
                }
                
                @Override
                public Patch setKey(final String key) {
                    return (Patch)super.setKey(key);
                }
                
                @Override
                public Patch setOauthToken(final String oauthToken) {
                    return (Patch)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Patch setPrettyPrint(final Boolean prettyPrint) {
                    return (Patch)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Patch setQuotaUser(final String quotaUser) {
                    return (Patch)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Patch setUploadType(final String uploadType) {
                    return (Patch)super.setUploadType(uploadType);
                }
                
                @Override
                public Patch setUploadProtocol(final String uploadProtocol) {
                    return (Patch)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Patch setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getCalendarResourceId() {
                    return this.calendarResourceId;
                }
                
                public Patch setCalendarResourceId(final String calendarResourceId) {
                    this.calendarResourceId = calendarResourceId;
                    return this;
                }
                
                @Override
                public Patch set(final String parameterName, final Object value) {
                    return (Patch)super.set(parameterName, value);
                }
            }
            
            public class Update extends DirectoryRequest<CalendarResource>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}";
                @Key
                private String customer;
                @Key
                private String calendarResourceId;
                
                protected Update(final String customer, final String calendarResourceId, final CalendarResource content) {
                    super(Calendars.this.this$1.this$0, "PUT", "admin/directory/v1/customer/{customer}/resources/calendars/{calendarResourceId}", content, CalendarResource.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.calendarResourceId = (String)Preconditions.checkNotNull((Object)calendarResourceId, (Object)"Required parameter calendarResourceId must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Update setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getCalendarResourceId() {
                    return this.calendarResourceId;
                }
                
                public Update setCalendarResourceId(final String calendarResourceId) {
                    this.calendarResourceId = calendarResourceId;
                    return this;
                }
                
                @Override
                public Update set(final String parameterName, final Object value) {
                    return (Update)super.set(parameterName, value);
                }
            }
        }
        
        public class Features
        {
            final /* synthetic */ Resources this$1;
            
            public Delete delete(final String customer, final String featureKey) throws IOException {
                final Delete result = new Delete(customer, featureKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Get get(final String customer, final String featureKey) throws IOException {
                final Get result = new Get(customer, featureKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Insert insert(final String customer, final Feature content) throws IOException {
                final Insert result = new Insert(customer, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public List list(final String customer) throws IOException {
                final List result = new List(customer);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Patch patch(final String customer, final String featureKey, final Feature content) throws IOException {
                final Patch result = new Patch(customer, featureKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Rename rename(final String customer, final String oldName, final FeatureRename content) throws IOException {
                final Rename result = new Rename(customer, oldName, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Update update(final String customer, final String featureKey, final Feature content) throws IOException {
                final Update result = new Update(customer, featureKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features/{featureKey}";
                @Key
                private String customer;
                @Key
                private String featureKey;
                
                protected Delete(final String customer, final String featureKey) {
                    super(Features.this.this$1.this$0, "DELETE", "admin/directory/v1/customer/{customer}/resources/features/{featureKey}", null, Void.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.featureKey = (String)Preconditions.checkNotNull((Object)featureKey, (Object)"Required parameter featureKey must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Delete setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getFeatureKey() {
                    return this.featureKey;
                }
                
                public Delete setFeatureKey(final String featureKey) {
                    this.featureKey = featureKey;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Get extends DirectoryRequest<Feature>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features/{featureKey}";
                @Key
                private String customer;
                @Key
                private String featureKey;
                
                protected Get(final String customer, final String featureKey) {
                    super(Features.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/features/{featureKey}", null, Feature.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.featureKey = (String)Preconditions.checkNotNull((Object)featureKey, (Object)"Required parameter featureKey must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Get setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getFeatureKey() {
                    return this.featureKey;
                }
                
                public Get setFeatureKey(final String featureKey) {
                    this.featureKey = featureKey;
                    return this;
                }
                
                @Override
                public Get set(final String parameterName, final Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }
            
            public class Insert extends DirectoryRequest<Feature>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features";
                @Key
                private String customer;
                
                protected Insert(final String customer, final Feature content) {
                    super(Features.this.this$1.this$0, "POST", "admin/directory/v1/customer/{customer}/resources/features", content, Feature.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.checkRequiredParameter((Object)content, "content");
                    this.checkRequiredParameter((Object)content.getName(), "Feature.getName()");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Insert setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                @Override
                public Insert set(final String parameterName, final Object value) {
                    return (Insert)super.set(parameterName, value);
                }
            }
            
            public class List extends DirectoryRequest<com.google.api.services.directory.model.Features>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features";
                @Key
                private String customer;
                @Key
                private Integer maxResults;
                @Key
                private String pageToken;
                
                protected List(final String customer) {
                    super(Features.this.this$1.this$0, "GET", "admin/directory/v1/customer/{customer}/resources/features", null, com.google.api.services.directory.model.Features.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public List setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public Integer getMaxResults() {
                    return this.maxResults;
                }
                
                public List setMaxResults(final Integer maxResults) {
                    this.maxResults = maxResults;
                    return this;
                }
                
                public String getPageToken() {
                    return this.pageToken;
                }
                
                public List setPageToken(final String pageToken) {
                    this.pageToken = pageToken;
                    return this;
                }
                
                @Override
                public List set(final String parameterName, final Object value) {
                    return (List)super.set(parameterName, value);
                }
            }
            
            public class Patch extends DirectoryRequest<Feature>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features/{featureKey}";
                @Key
                private String customer;
                @Key
                private String featureKey;
                
                protected Patch(final String customer, final String featureKey, final Feature content) {
                    super(Features.this.this$1.this$0, "PATCH", "admin/directory/v1/customer/{customer}/resources/features/{featureKey}", content, Feature.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.featureKey = (String)Preconditions.checkNotNull((Object)featureKey, (Object)"Required parameter featureKey must be specified.");
                }
                
                @Override
                public Patch set$Xgafv(final String $Xgafv) {
                    return (Patch)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Patch setAccessToken(final String accessToken) {
                    return (Patch)super.setAccessToken(accessToken);
                }
                
                @Override
                public Patch setAlt(final String alt) {
                    return (Patch)super.setAlt(alt);
                }
                
                @Override
                public Patch setCallback(final String callback) {
                    return (Patch)super.setCallback(callback);
                }
                
                @Override
                public Patch setFields(final String fields) {
                    return (Patch)super.setFields(fields);
                }
                
                @Override
                public Patch setKey(final String key) {
                    return (Patch)super.setKey(key);
                }
                
                @Override
                public Patch setOauthToken(final String oauthToken) {
                    return (Patch)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Patch setPrettyPrint(final Boolean prettyPrint) {
                    return (Patch)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Patch setQuotaUser(final String quotaUser) {
                    return (Patch)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Patch setUploadType(final String uploadType) {
                    return (Patch)super.setUploadType(uploadType);
                }
                
                @Override
                public Patch setUploadProtocol(final String uploadProtocol) {
                    return (Patch)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Patch setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getFeatureKey() {
                    return this.featureKey;
                }
                
                public Patch setFeatureKey(final String featureKey) {
                    this.featureKey = featureKey;
                    return this;
                }
                
                @Override
                public Patch set(final String parameterName, final Object value) {
                    return (Patch)super.set(parameterName, value);
                }
            }
            
            public class Rename extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features/{oldName}/rename";
                @Key
                private String customer;
                @Key
                private String oldName;
                
                protected Rename(final String customer, final String oldName, final FeatureRename content) {
                    super(Features.this.this$1.this$0, "POST", "admin/directory/v1/customer/{customer}/resources/features/{oldName}/rename", content, Void.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.oldName = (String)Preconditions.checkNotNull((Object)oldName, (Object)"Required parameter oldName must be specified.");
                }
                
                @Override
                public Rename set$Xgafv(final String $Xgafv) {
                    return (Rename)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Rename setAccessToken(final String accessToken) {
                    return (Rename)super.setAccessToken(accessToken);
                }
                
                @Override
                public Rename setAlt(final String alt) {
                    return (Rename)super.setAlt(alt);
                }
                
                @Override
                public Rename setCallback(final String callback) {
                    return (Rename)super.setCallback(callback);
                }
                
                @Override
                public Rename setFields(final String fields) {
                    return (Rename)super.setFields(fields);
                }
                
                @Override
                public Rename setKey(final String key) {
                    return (Rename)super.setKey(key);
                }
                
                @Override
                public Rename setOauthToken(final String oauthToken) {
                    return (Rename)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Rename setPrettyPrint(final Boolean prettyPrint) {
                    return (Rename)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Rename setQuotaUser(final String quotaUser) {
                    return (Rename)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Rename setUploadType(final String uploadType) {
                    return (Rename)super.setUploadType(uploadType);
                }
                
                @Override
                public Rename setUploadProtocol(final String uploadProtocol) {
                    return (Rename)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Rename setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getOldName() {
                    return this.oldName;
                }
                
                public Rename setOldName(final String oldName) {
                    this.oldName = oldName;
                    return this;
                }
                
                @Override
                public Rename set(final String parameterName, final Object value) {
                    return (Rename)super.set(parameterName, value);
                }
            }
            
            public class Update extends DirectoryRequest<Feature>
            {
                private static final String REST_PATH = "admin/directory/v1/customer/{customer}/resources/features/{featureKey}";
                @Key
                private String customer;
                @Key
                private String featureKey;
                
                protected Update(final String customer, final String featureKey, final Feature content) {
                    super(Features.this.this$1.this$0, "PUT", "admin/directory/v1/customer/{customer}/resources/features/{featureKey}", content, Feature.class);
                    this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                    this.featureKey = (String)Preconditions.checkNotNull((Object)featureKey, (Object)"Required parameter featureKey must be specified.");
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
                
                public String getCustomer() {
                    return this.customer;
                }
                
                public Update setCustomer(final String customer) {
                    this.customer = customer;
                    return this;
                }
                
                public String getFeatureKey() {
                    return this.featureKey;
                }
                
                public Update setFeatureKey(final String featureKey) {
                    this.featureKey = featureKey;
                    return this;
                }
                
                @Override
                public Update set(final String parameterName, final Object value) {
                    return (Update)super.set(parameterName, value);
                }
            }
        }
    }
    
    public class RoleAssignments
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customer, final String roleAssignmentId) throws IOException {
            final Delete result = new Delete(customer, roleAssignmentId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customer, final String roleAssignmentId) throws IOException {
            final Get result = new Get(customer, roleAssignmentId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customer, final RoleAssignment content) throws IOException {
            final Insert result = new Insert(customer, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customer) throws IOException {
            final List result = new List(customer);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roleassignments/{roleAssignmentId}";
            @Key
            private String customer;
            @Key
            private String roleAssignmentId;
            
            protected Delete(final String customer, final String roleAssignmentId) {
                super(RoleAssignments.this.this$0, "DELETE", "admin/directory/v1/customer/{customer}/roleassignments/{roleAssignmentId}", null, Void.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleAssignmentId = (String)Preconditions.checkNotNull((Object)roleAssignmentId, (Object)"Required parameter roleAssignmentId must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Delete setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleAssignmentId() {
                return this.roleAssignmentId;
            }
            
            public Delete setRoleAssignmentId(final String roleAssignmentId) {
                this.roleAssignmentId = roleAssignmentId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<RoleAssignment>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roleassignments/{roleAssignmentId}";
            @Key
            private String customer;
            @Key
            private String roleAssignmentId;
            
            protected Get(final String customer, final String roleAssignmentId) {
                super(RoleAssignments.this.this$0, "GET", "admin/directory/v1/customer/{customer}/roleassignments/{roleAssignmentId}", null, RoleAssignment.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleAssignmentId = (String)Preconditions.checkNotNull((Object)roleAssignmentId, (Object)"Required parameter roleAssignmentId must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Get setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleAssignmentId() {
                return this.roleAssignmentId;
            }
            
            public Get setRoleAssignmentId(final String roleAssignmentId) {
                this.roleAssignmentId = roleAssignmentId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<RoleAssignment>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roleassignments";
            @Key
            private String customer;
            
            protected Insert(final String customer, final RoleAssignment content) {
                super(RoleAssignments.this.this$0, "POST", "admin/directory/v1/customer/{customer}/roleassignments", content, RoleAssignment.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Insert setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.RoleAssignments>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roleassignments";
            @Key
            private String customer;
            @Key
            private Integer maxResults;
            @Key
            private String pageToken;
            @Key
            private String roleId;
            @Key
            private String userKey;
            
            protected List(final String customer) {
                super(RoleAssignments.this.this$0, "GET", "admin/directory/v1/customer/{customer}/roleassignments", null, com.google.api.services.directory.model.RoleAssignments.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getRoleId() {
                return this.roleId;
            }
            
            public List setRoleId(final String roleId) {
                this.roleId = roleId;
                return this;
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public List setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class Roles
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customer, final String roleId) throws IOException {
            final Delete result = new Delete(customer, roleId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customer, final String roleId) throws IOException {
            final Get result = new Get(customer, roleId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customer, final Role content) throws IOException {
            final Insert result = new Insert(customer, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customer) throws IOException {
            final List result = new List(customer);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String customer, final String roleId, final Role content) throws IOException {
            final Patch result = new Patch(customer, roleId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String customer, final String roleId, final Role content) throws IOException {
            final Update result = new Update(customer, roleId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles/{roleId}";
            @Key
            private String customer;
            @Key
            private String roleId;
            
            protected Delete(final String customer, final String roleId) {
                super(Roles.this.this$0, "DELETE", "admin/directory/v1/customer/{customer}/roles/{roleId}", null, Void.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleId = (String)Preconditions.checkNotNull((Object)roleId, (Object)"Required parameter roleId must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Delete setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleId() {
                return this.roleId;
            }
            
            public Delete setRoleId(final String roleId) {
                this.roleId = roleId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Role>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles/{roleId}";
            @Key
            private String customer;
            @Key
            private String roleId;
            
            protected Get(final String customer, final String roleId) {
                super(Roles.this.this$0, "GET", "admin/directory/v1/customer/{customer}/roles/{roleId}", null, Role.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleId = (String)Preconditions.checkNotNull((Object)roleId, (Object)"Required parameter roleId must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Get setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleId() {
                return this.roleId;
            }
            
            public Get setRoleId(final String roleId) {
                this.roleId = roleId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<Role>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles";
            @Key
            private String customer;
            
            protected Insert(final String customer, final Role content) {
                super(Roles.this.this$0, "POST", "admin/directory/v1/customer/{customer}/roles", content, Role.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getRoleName(), "Role.getRoleName()");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Insert setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Roles>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles";
            @Key
            private String customer;
            @Key
            private Integer maxResults;
            @Key
            private String pageToken;
            
            protected List(final String customer) {
                super(Roles.this.this$0, "GET", "admin/directory/v1/customer/{customer}/roles", null, com.google.api.services.directory.model.Roles.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<Role>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles/{roleId}";
            @Key
            private String customer;
            @Key
            private String roleId;
            
            protected Patch(final String customer, final String roleId, final Role content) {
                super(Roles.this.this$0, "PATCH", "admin/directory/v1/customer/{customer}/roles/{roleId}", content, Role.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleId = (String)Preconditions.checkNotNull((Object)roleId, (Object)"Required parameter roleId must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Patch setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleId() {
                return this.roleId;
            }
            
            public Patch setRoleId(final String roleId) {
                this.roleId = roleId;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<Role>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customer}/roles/{roleId}";
            @Key
            private String customer;
            @Key
            private String roleId;
            
            protected Update(final String customer, final String roleId, final Role content) {
                super(Roles.this.this$0, "PUT", "admin/directory/v1/customer/{customer}/roles/{roleId}", content, Role.class);
                this.customer = (String)Preconditions.checkNotNull((Object)customer, (Object)"Required parameter customer must be specified.");
                this.roleId = (String)Preconditions.checkNotNull((Object)roleId, (Object)"Required parameter roleId must be specified.");
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
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Update setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getRoleId() {
                return this.roleId;
            }
            
            public Update setRoleId(final String roleId) {
                this.roleId = roleId;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Schemas
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String customerId, final String schemaKey) throws IOException {
            final Delete result = new Delete(customerId, schemaKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String customerId, final String schemaKey) throws IOException {
            final Get result = new Get(customerId, schemaKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final String customerId, final Schema content) throws IOException {
            final Insert result = new Insert(customerId, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String customerId) throws IOException {
            final List result = new List(customerId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String customerId, final String schemaKey, final Schema content) throws IOException {
            final Patch result = new Patch(customerId, schemaKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String customerId, final String schemaKey, final Schema content) throws IOException {
            final Update result = new Update(customerId, schemaKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}";
            @Key
            private String customerId;
            @Key
            private String schemaKey;
            
            protected Delete(final String customerId, final String schemaKey) {
                super(Schemas.this.this$0, "DELETE", "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}", null, Void.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.schemaKey = (String)Preconditions.checkNotNull((Object)schemaKey, (Object)"Required parameter schemaKey must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Delete setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getSchemaKey() {
                return this.schemaKey;
            }
            
            public Delete setSchemaKey(final String schemaKey) {
                this.schemaKey = schemaKey;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Schema>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}";
            @Key
            private String customerId;
            @Key
            private String schemaKey;
            
            protected Get(final String customerId, final String schemaKey) {
                super(Schemas.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}", null, Schema.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.schemaKey = (String)Preconditions.checkNotNull((Object)schemaKey, (Object)"Required parameter schemaKey must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Get setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getSchemaKey() {
                return this.schemaKey;
            }
            
            public Get setSchemaKey(final String schemaKey) {
                this.schemaKey = schemaKey;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<Schema>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas";
            @Key
            private String customerId;
            
            protected Insert(final String customerId, final Schema content) {
                super(Schemas.this.this$0, "POST", "admin/directory/v1/customer/{customerId}/schemas", content, Schema.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getDisplayName(), "Schema.getDisplayName()");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getSchemaName(), "Schema.getSchemaName()");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Insert setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Schemas>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas";
            @Key
            private String customerId;
            
            protected List(final String customerId) {
                super(Schemas.this.this$0, "GET", "admin/directory/v1/customer/{customerId}/schemas", null, com.google.api.services.directory.model.Schemas.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public List setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<Schema>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}";
            @Key
            private String customerId;
            @Key
            private String schemaKey;
            
            protected Patch(final String customerId, final String schemaKey, final Schema content) {
                super(Schemas.this.this$0, "PATCH", "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}", content, Schema.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.schemaKey = (String)Preconditions.checkNotNull((Object)schemaKey, (Object)"Required parameter schemaKey must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Patch setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getSchemaKey() {
                return this.schemaKey;
            }
            
            public Patch setSchemaKey(final String schemaKey) {
                this.schemaKey = schemaKey;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<Schema>
        {
            private static final String REST_PATH = "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}";
            @Key
            private String customerId;
            @Key
            private String schemaKey;
            
            protected Update(final String customerId, final String schemaKey, final Schema content) {
                super(Schemas.this.this$0, "PUT", "admin/directory/v1/customer/{customerId}/schemas/{schemaKey}", content, Schema.class);
                this.customerId = (String)Preconditions.checkNotNull((Object)customerId, (Object)"Required parameter customerId must be specified.");
                this.schemaKey = (String)Preconditions.checkNotNull((Object)schemaKey, (Object)"Required parameter schemaKey must be specified.");
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
            
            public String getCustomerId() {
                return this.customerId;
            }
            
            public Update setCustomerId(final String customerId) {
                this.customerId = customerId;
                return this;
            }
            
            public String getSchemaKey() {
                return this.schemaKey;
            }
            
            public Update setSchemaKey(final String schemaKey) {
                this.schemaKey = schemaKey;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
    }
    
    public class Tokens
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String userKey, final String clientId) throws IOException {
            final Delete result = new Delete(userKey, clientId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String userKey, final String clientId) throws IOException {
            final Get result = new Get(userKey, clientId);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String userKey) throws IOException {
            final List result = new List(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/tokens/{clientId}";
            @Key
            private String userKey;
            @Key
            private String clientId;
            
            protected Delete(final String userKey, final String clientId) {
                super(Tokens.this.this$0, "DELETE", "admin/directory/v1/users/{userKey}/tokens/{clientId}", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                this.clientId = (String)Preconditions.checkNotNull((Object)clientId, (Object)"Required parameter clientId must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Delete setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            public String getClientId() {
                return this.clientId;
            }
            
            public Delete setClientId(final String clientId) {
                this.clientId = clientId;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<Token>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/tokens/{clientId}";
            @Key
            private String userKey;
            @Key
            private String clientId;
            
            protected Get(final String userKey, final String clientId) {
                super(Tokens.this.this$0, "GET", "admin/directory/v1/users/{userKey}/tokens/{clientId}", null, Token.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                this.clientId = (String)Preconditions.checkNotNull((Object)clientId, (Object)"Required parameter clientId must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Get setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            public String getClientId() {
                return this.clientId;
            }
            
            public Get setClientId(final String clientId) {
                this.clientId = clientId;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Tokens>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/tokens";
            @Key
            private String userKey;
            
            protected List(final String userKey) {
                super(Tokens.this.this$0, "GET", "admin/directory/v1/users/{userKey}/tokens", null, com.google.api.services.directory.model.Tokens.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public List setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public class TwoStepVerification
    {
        final /* synthetic */ Directory this$0;
        
        public TurnOff turnOff(final String userKey) throws IOException {
            final TurnOff result = new TurnOff(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class TurnOff extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/twoStepVerification/turnOff";
            @Key
            private String userKey;
            
            protected TurnOff(final String userKey) {
                super(TwoStepVerification.this.this$0, "POST", "admin/directory/v1/users/{userKey}/twoStepVerification/turnOff", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public TurnOff set$Xgafv(final String $Xgafv) {
                return (TurnOff)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public TurnOff setAccessToken(final String accessToken) {
                return (TurnOff)super.setAccessToken(accessToken);
            }
            
            @Override
            public TurnOff setAlt(final String alt) {
                return (TurnOff)super.setAlt(alt);
            }
            
            @Override
            public TurnOff setCallback(final String callback) {
                return (TurnOff)super.setCallback(callback);
            }
            
            @Override
            public TurnOff setFields(final String fields) {
                return (TurnOff)super.setFields(fields);
            }
            
            @Override
            public TurnOff setKey(final String key) {
                return (TurnOff)super.setKey(key);
            }
            
            @Override
            public TurnOff setOauthToken(final String oauthToken) {
                return (TurnOff)super.setOauthToken(oauthToken);
            }
            
            @Override
            public TurnOff setPrettyPrint(final Boolean prettyPrint) {
                return (TurnOff)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public TurnOff setQuotaUser(final String quotaUser) {
                return (TurnOff)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public TurnOff setUploadType(final String uploadType) {
                return (TurnOff)super.setUploadType(uploadType);
            }
            
            @Override
            public TurnOff setUploadProtocol(final String uploadProtocol) {
                return (TurnOff)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public TurnOff setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public TurnOff set(final String parameterName, final Object value) {
                return (TurnOff)super.set(parameterName, value);
            }
        }
    }
    
    public class Users
    {
        final /* synthetic */ Directory this$0;
        
        public Delete delete(final String userKey) throws IOException {
            final Delete result = new Delete(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Get get(final String userKey) throws IOException {
            final Get result = new Get(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Insert insert(final User content) throws IOException {
            final Insert result = new Insert(content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list() throws IOException {
            final List result = new List();
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public MakeAdmin makeAdmin(final String userKey, final UserMakeAdmin content) throws IOException {
            final MakeAdmin result = new MakeAdmin(userKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Patch patch(final String userKey, final User content) throws IOException {
            final Patch result = new Patch(userKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public SignOut signOut(final String userKey) throws IOException {
            final SignOut result = new SignOut(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Undelete undelete(final String userKey, final UserUndelete content) throws IOException {
            final Undelete result = new Undelete(userKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Update update(final String userKey, final User content) throws IOException {
            final Update result = new Update(userKey, content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Watch watch(final Channel content) throws IOException {
            final Watch result = new Watch(content);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Aliases aliases() {
            return new Aliases();
        }
        
        public Photos photos() {
            return new Photos();
        }
        
        public class Delete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}";
            @Key
            private String userKey;
            
            protected Delete(final String userKey) {
                super(Users.this.this$0, "DELETE", "admin/directory/v1/users/{userKey}", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Delete setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Delete set(final String parameterName, final Object value) {
                return (Delete)super.set(parameterName, value);
            }
        }
        
        public class Get extends DirectoryRequest<User>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}";
            @Key
            private String userKey;
            @Key
            private String customFieldMask;
            @Key
            private String projection;
            @Key
            private String viewType;
            
            protected Get(final String userKey) {
                super(Users.this.this$0, "GET", "admin/directory/v1/users/{userKey}", null, User.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Get setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            public String getCustomFieldMask() {
                return this.customFieldMask;
            }
            
            public Get setCustomFieldMask(final String customFieldMask) {
                this.customFieldMask = customFieldMask;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Get setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            public String getViewType() {
                return this.viewType;
            }
            
            public Get setViewType(final String viewType) {
                this.viewType = viewType;
                return this;
            }
            
            @Override
            public Get set(final String parameterName, final Object value) {
                return (Get)super.set(parameterName, value);
            }
        }
        
        public class Insert extends DirectoryRequest<User>
        {
            private static final String REST_PATH = "admin/directory/v1/users";
            
            protected Insert(final User content) {
                super(Users.this.this$0, "POST", "admin/directory/v1/users", content, User.class);
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getPassword(), "User.getPassword()");
                this.checkRequiredParameter((Object)content, "content");
                this.checkRequiredParameter((Object)content.getPrimaryEmail(), "User.getPrimaryEmail()");
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
            
            @Override
            public Insert set(final String parameterName, final Object value) {
                return (Insert)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.Users>
        {
            private static final String REST_PATH = "admin/directory/v1/users";
            @Key
            private String customFieldMask;
            @Key
            private String customer;
            @Key
            private String domain;
            @Key
            private String event;
            @Key
            private Integer maxResults;
            @Key
            private String orderBy;
            @Key
            private String pageToken;
            @Key
            private String projection;
            @Key
            private String query;
            @Key
            private String showDeleted;
            @Key
            private String sortOrder;
            @Key
            private String viewType;
            
            protected List() {
                super(Users.this.this$0, "GET", "admin/directory/v1/users", null, com.google.api.services.directory.model.Users.class);
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
            
            public String getCustomFieldMask() {
                return this.customFieldMask;
            }
            
            public List setCustomFieldMask(final String customFieldMask) {
                this.customFieldMask = customFieldMask;
                return this;
            }
            
            public String getCustomer() {
                return this.customer;
            }
            
            public List setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomain() {
                return this.domain;
            }
            
            public List setDomain(final String domain) {
                this.domain = domain;
                return this;
            }
            
            public String getEvent() {
                return this.event;
            }
            
            public List setEvent(final String event) {
                this.event = event;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public List setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getOrderBy() {
                return this.orderBy;
            }
            
            public List setOrderBy(final String orderBy) {
                this.orderBy = orderBy;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public List setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public List setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public List setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getShowDeleted() {
                return this.showDeleted;
            }
            
            public List setShowDeleted(final String showDeleted) {
                this.showDeleted = showDeleted;
                return this;
            }
            
            public String getSortOrder() {
                return this.sortOrder;
            }
            
            public List setSortOrder(final String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }
            
            public String getViewType() {
                return this.viewType;
            }
            
            public List setViewType(final String viewType) {
                this.viewType = viewType;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
        
        public class MakeAdmin extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/makeAdmin";
            @Key
            private String userKey;
            
            protected MakeAdmin(final String userKey, final UserMakeAdmin content) {
                super(Users.this.this$0, "POST", "admin/directory/v1/users/{userKey}/makeAdmin", content, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public MakeAdmin set$Xgafv(final String $Xgafv) {
                return (MakeAdmin)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public MakeAdmin setAccessToken(final String accessToken) {
                return (MakeAdmin)super.setAccessToken(accessToken);
            }
            
            @Override
            public MakeAdmin setAlt(final String alt) {
                return (MakeAdmin)super.setAlt(alt);
            }
            
            @Override
            public MakeAdmin setCallback(final String callback) {
                return (MakeAdmin)super.setCallback(callback);
            }
            
            @Override
            public MakeAdmin setFields(final String fields) {
                return (MakeAdmin)super.setFields(fields);
            }
            
            @Override
            public MakeAdmin setKey(final String key) {
                return (MakeAdmin)super.setKey(key);
            }
            
            @Override
            public MakeAdmin setOauthToken(final String oauthToken) {
                return (MakeAdmin)super.setOauthToken(oauthToken);
            }
            
            @Override
            public MakeAdmin setPrettyPrint(final Boolean prettyPrint) {
                return (MakeAdmin)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public MakeAdmin setQuotaUser(final String quotaUser) {
                return (MakeAdmin)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public MakeAdmin setUploadType(final String uploadType) {
                return (MakeAdmin)super.setUploadType(uploadType);
            }
            
            @Override
            public MakeAdmin setUploadProtocol(final String uploadProtocol) {
                return (MakeAdmin)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public MakeAdmin setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public MakeAdmin set(final String parameterName, final Object value) {
                return (MakeAdmin)super.set(parameterName, value);
            }
        }
        
        public class Patch extends DirectoryRequest<User>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}";
            @Key
            private String userKey;
            
            protected Patch(final String userKey, final User content) {
                super(Users.this.this$0, "PATCH", "admin/directory/v1/users/{userKey}", content, User.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public Patch set$Xgafv(final String $Xgafv) {
                return (Patch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Patch setAccessToken(final String accessToken) {
                return (Patch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Patch setAlt(final String alt) {
                return (Patch)super.setAlt(alt);
            }
            
            @Override
            public Patch setCallback(final String callback) {
                return (Patch)super.setCallback(callback);
            }
            
            @Override
            public Patch setFields(final String fields) {
                return (Patch)super.setFields(fields);
            }
            
            @Override
            public Patch setKey(final String key) {
                return (Patch)super.setKey(key);
            }
            
            @Override
            public Patch setOauthToken(final String oauthToken) {
                return (Patch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Patch setPrettyPrint(final Boolean prettyPrint) {
                return (Patch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Patch setQuotaUser(final String quotaUser) {
                return (Patch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Patch setUploadType(final String uploadType) {
                return (Patch)super.setUploadType(uploadType);
            }
            
            @Override
            public Patch setUploadProtocol(final String uploadProtocol) {
                return (Patch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Patch setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Patch set(final String parameterName, final Object value) {
                return (Patch)super.set(parameterName, value);
            }
        }
        
        public class SignOut extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/signOut";
            @Key
            private String userKey;
            
            protected SignOut(final String userKey) {
                super(Users.this.this$0, "POST", "admin/directory/v1/users/{userKey}/signOut", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public SignOut set$Xgafv(final String $Xgafv) {
                return (SignOut)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public SignOut setAccessToken(final String accessToken) {
                return (SignOut)super.setAccessToken(accessToken);
            }
            
            @Override
            public SignOut setAlt(final String alt) {
                return (SignOut)super.setAlt(alt);
            }
            
            @Override
            public SignOut setCallback(final String callback) {
                return (SignOut)super.setCallback(callback);
            }
            
            @Override
            public SignOut setFields(final String fields) {
                return (SignOut)super.setFields(fields);
            }
            
            @Override
            public SignOut setKey(final String key) {
                return (SignOut)super.setKey(key);
            }
            
            @Override
            public SignOut setOauthToken(final String oauthToken) {
                return (SignOut)super.setOauthToken(oauthToken);
            }
            
            @Override
            public SignOut setPrettyPrint(final Boolean prettyPrint) {
                return (SignOut)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public SignOut setQuotaUser(final String quotaUser) {
                return (SignOut)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public SignOut setUploadType(final String uploadType) {
                return (SignOut)super.setUploadType(uploadType);
            }
            
            @Override
            public SignOut setUploadProtocol(final String uploadProtocol) {
                return (SignOut)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public SignOut setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public SignOut set(final String parameterName, final Object value) {
                return (SignOut)super.set(parameterName, value);
            }
        }
        
        public class Undelete extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/undelete";
            @Key
            private String userKey;
            
            protected Undelete(final String userKey, final UserUndelete content) {
                super(Users.this.this$0, "POST", "admin/directory/v1/users/{userKey}/undelete", content, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public Undelete set$Xgafv(final String $Xgafv) {
                return (Undelete)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Undelete setAccessToken(final String accessToken) {
                return (Undelete)super.setAccessToken(accessToken);
            }
            
            @Override
            public Undelete setAlt(final String alt) {
                return (Undelete)super.setAlt(alt);
            }
            
            @Override
            public Undelete setCallback(final String callback) {
                return (Undelete)super.setCallback(callback);
            }
            
            @Override
            public Undelete setFields(final String fields) {
                return (Undelete)super.setFields(fields);
            }
            
            @Override
            public Undelete setKey(final String key) {
                return (Undelete)super.setKey(key);
            }
            
            @Override
            public Undelete setOauthToken(final String oauthToken) {
                return (Undelete)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Undelete setPrettyPrint(final Boolean prettyPrint) {
                return (Undelete)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Undelete setQuotaUser(final String quotaUser) {
                return (Undelete)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Undelete setUploadType(final String uploadType) {
                return (Undelete)super.setUploadType(uploadType);
            }
            
            @Override
            public Undelete setUploadProtocol(final String uploadProtocol) {
                return (Undelete)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Undelete setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Undelete set(final String parameterName, final Object value) {
                return (Undelete)super.set(parameterName, value);
            }
        }
        
        public class Update extends DirectoryRequest<User>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}";
            @Key
            private String userKey;
            
            protected Update(final String userKey, final User content) {
                super(Users.this.this$0, "PUT", "admin/directory/v1/users/{userKey}", content, User.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Update setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Update set(final String parameterName, final Object value) {
                return (Update)super.set(parameterName, value);
            }
        }
        
        public class Watch extends DirectoryRequest<Channel>
        {
            private static final String REST_PATH = "admin/directory/v1/users/watch";
            @Key
            private String customFieldMask;
            @Key
            private String customer;
            @Key
            private String domain;
            @Key
            private String event;
            @Key
            private Integer maxResults;
            @Key
            private String orderBy;
            @Key
            private String pageToken;
            @Key
            private String projection;
            @Key
            private String query;
            @Key
            private String showDeleted;
            @Key
            private String sortOrder;
            @Key
            private String viewType;
            
            protected Watch(final Channel content) {
                super(Users.this.this$0, "POST", "admin/directory/v1/users/watch", content, Channel.class);
            }
            
            @Override
            public Watch set$Xgafv(final String $Xgafv) {
                return (Watch)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Watch setAccessToken(final String accessToken) {
                return (Watch)super.setAccessToken(accessToken);
            }
            
            @Override
            public Watch setAlt(final String alt) {
                return (Watch)super.setAlt(alt);
            }
            
            @Override
            public Watch setCallback(final String callback) {
                return (Watch)super.setCallback(callback);
            }
            
            @Override
            public Watch setFields(final String fields) {
                return (Watch)super.setFields(fields);
            }
            
            @Override
            public Watch setKey(final String key) {
                return (Watch)super.setKey(key);
            }
            
            @Override
            public Watch setOauthToken(final String oauthToken) {
                return (Watch)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Watch setPrettyPrint(final Boolean prettyPrint) {
                return (Watch)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Watch setQuotaUser(final String quotaUser) {
                return (Watch)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Watch setUploadType(final String uploadType) {
                return (Watch)super.setUploadType(uploadType);
            }
            
            @Override
            public Watch setUploadProtocol(final String uploadProtocol) {
                return (Watch)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getCustomFieldMask() {
                return this.customFieldMask;
            }
            
            public Watch setCustomFieldMask(final String customFieldMask) {
                this.customFieldMask = customFieldMask;
                return this;
            }
            
            public String getCustomer() {
                return this.customer;
            }
            
            public Watch setCustomer(final String customer) {
                this.customer = customer;
                return this;
            }
            
            public String getDomain() {
                return this.domain;
            }
            
            public Watch setDomain(final String domain) {
                this.domain = domain;
                return this;
            }
            
            public String getEvent() {
                return this.event;
            }
            
            public Watch setEvent(final String event) {
                this.event = event;
                return this;
            }
            
            public Integer getMaxResults() {
                return this.maxResults;
            }
            
            public Watch setMaxResults(final Integer maxResults) {
                this.maxResults = maxResults;
                return this;
            }
            
            public String getOrderBy() {
                return this.orderBy;
            }
            
            public Watch setOrderBy(final String orderBy) {
                this.orderBy = orderBy;
                return this;
            }
            
            public String getPageToken() {
                return this.pageToken;
            }
            
            public Watch setPageToken(final String pageToken) {
                this.pageToken = pageToken;
                return this;
            }
            
            public String getProjection() {
                return this.projection;
            }
            
            public Watch setProjection(final String projection) {
                this.projection = projection;
                return this;
            }
            
            public String getQuery() {
                return this.query;
            }
            
            public Watch setQuery(final String query) {
                this.query = query;
                return this;
            }
            
            public String getShowDeleted() {
                return this.showDeleted;
            }
            
            public Watch setShowDeleted(final String showDeleted) {
                this.showDeleted = showDeleted;
                return this;
            }
            
            public String getSortOrder() {
                return this.sortOrder;
            }
            
            public Watch setSortOrder(final String sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }
            
            public String getViewType() {
                return this.viewType;
            }
            
            public Watch setViewType(final String viewType) {
                this.viewType = viewType;
                return this;
            }
            
            @Override
            public Watch set(final String parameterName, final Object value) {
                return (Watch)super.set(parameterName, value);
            }
        }
        
        public class Aliases
        {
            final /* synthetic */ Users this$1;
            
            public Delete delete(final String userKey, final String alias) throws IOException {
                final Delete result = new Delete(userKey, alias);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Insert insert(final String userKey, final Alias content) throws IOException {
                final Insert result = new Insert(userKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public List list(final String userKey) throws IOException {
                final List result = new List(userKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Watch watch(final String userKey, final Channel content) throws IOException {
                final Watch result = new Watch(userKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/aliases/{alias}";
                @Key
                private String userKey;
                @Key
                private String alias;
                
                protected Delete(final String userKey, final String alias) {
                    super(Aliases.this.this$1.this$0, "DELETE", "admin/directory/v1/users/{userKey}/aliases/{alias}", null, Void.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                    this.alias = (String)Preconditions.checkNotNull((Object)alias, (Object)"Required parameter alias must be specified.");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Delete setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                public String getAlias() {
                    return this.alias;
                }
                
                public Delete setAlias(final String alias) {
                    this.alias = alias;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Insert extends DirectoryRequest<Alias>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/aliases";
                @Key
                private String userKey;
                
                protected Insert(final String userKey, final Alias content) {
                    super(Aliases.this.this$1.this$0, "POST", "admin/directory/v1/users/{userKey}/aliases", content, Alias.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Insert setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                @Override
                public Insert set(final String parameterName, final Object value) {
                    return (Insert)super.set(parameterName, value);
                }
            }
            
            public class List extends DirectoryRequest<com.google.api.services.directory.model.Aliases>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/aliases";
                @Key
                private String userKey;
                @Key
                private String event;
                
                protected List(final String userKey) {
                    super(Aliases.this.this$1.this$0, "GET", "admin/directory/v1/users/{userKey}/aliases", null, com.google.api.services.directory.model.Aliases.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public List setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                public String getEvent() {
                    return this.event;
                }
                
                public List setEvent(final String event) {
                    this.event = event;
                    return this;
                }
                
                @Override
                public List set(final String parameterName, final Object value) {
                    return (List)super.set(parameterName, value);
                }
            }
            
            public class Watch extends DirectoryRequest<Channel>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/aliases/watch";
                @Key
                private String userKey;
                @Key
                private String event;
                
                protected Watch(final String userKey, final Channel content) {
                    super(Aliases.this.this$1.this$0, "POST", "admin/directory/v1/users/{userKey}/aliases/watch", content, Channel.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                }
                
                @Override
                public Watch set$Xgafv(final String $Xgafv) {
                    return (Watch)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Watch setAccessToken(final String accessToken) {
                    return (Watch)super.setAccessToken(accessToken);
                }
                
                @Override
                public Watch setAlt(final String alt) {
                    return (Watch)super.setAlt(alt);
                }
                
                @Override
                public Watch setCallback(final String callback) {
                    return (Watch)super.setCallback(callback);
                }
                
                @Override
                public Watch setFields(final String fields) {
                    return (Watch)super.setFields(fields);
                }
                
                @Override
                public Watch setKey(final String key) {
                    return (Watch)super.setKey(key);
                }
                
                @Override
                public Watch setOauthToken(final String oauthToken) {
                    return (Watch)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Watch setPrettyPrint(final Boolean prettyPrint) {
                    return (Watch)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Watch setQuotaUser(final String quotaUser) {
                    return (Watch)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Watch setUploadType(final String uploadType) {
                    return (Watch)super.setUploadType(uploadType);
                }
                
                @Override
                public Watch setUploadProtocol(final String uploadProtocol) {
                    return (Watch)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Watch setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                public String getEvent() {
                    return this.event;
                }
                
                public Watch setEvent(final String event) {
                    this.event = event;
                    return this;
                }
                
                @Override
                public Watch set(final String parameterName, final Object value) {
                    return (Watch)super.set(parameterName, value);
                }
            }
        }
        
        public class Photos
        {
            final /* synthetic */ Users this$1;
            
            public Delete delete(final String userKey) throws IOException {
                final Delete result = new Delete(userKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Get get(final String userKey) throws IOException {
                final Get result = new Get(userKey);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Patch patch(final String userKey, final UserPhoto content) throws IOException {
                final Patch result = new Patch(userKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public Update update(final String userKey, final UserPhoto content) throws IOException {
                final Update result = new Update(userKey, content);
                Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
                return result;
            }
            
            public class Delete extends DirectoryRequest<Void>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/photos/thumbnail";
                @Key
                private String userKey;
                
                protected Delete(final String userKey) {
                    super(Photos.this.this$1.this$0, "DELETE", "admin/directory/v1/users/{userKey}/photos/thumbnail", null, Void.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Delete setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                @Override
                public Delete set(final String parameterName, final Object value) {
                    return (Delete)super.set(parameterName, value);
                }
            }
            
            public class Get extends DirectoryRequest<UserPhoto>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/photos/thumbnail";
                @Key
                private String userKey;
                
                protected Get(final String userKey) {
                    super(Photos.this.this$1.this$0, "GET", "admin/directory/v1/users/{userKey}/photos/thumbnail", null, UserPhoto.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Get setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                @Override
                public Get set(final String parameterName, final Object value) {
                    return (Get)super.set(parameterName, value);
                }
            }
            
            public class Patch extends DirectoryRequest<UserPhoto>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/photos/thumbnail";
                @Key
                private String userKey;
                
                protected Patch(final String userKey, final UserPhoto content) {
                    super(Photos.this.this$1.this$0, "PATCH", "admin/directory/v1/users/{userKey}/photos/thumbnail", content, UserPhoto.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                }
                
                @Override
                public Patch set$Xgafv(final String $Xgafv) {
                    return (Patch)super.set$Xgafv($Xgafv);
                }
                
                @Override
                public Patch setAccessToken(final String accessToken) {
                    return (Patch)super.setAccessToken(accessToken);
                }
                
                @Override
                public Patch setAlt(final String alt) {
                    return (Patch)super.setAlt(alt);
                }
                
                @Override
                public Patch setCallback(final String callback) {
                    return (Patch)super.setCallback(callback);
                }
                
                @Override
                public Patch setFields(final String fields) {
                    return (Patch)super.setFields(fields);
                }
                
                @Override
                public Patch setKey(final String key) {
                    return (Patch)super.setKey(key);
                }
                
                @Override
                public Patch setOauthToken(final String oauthToken) {
                    return (Patch)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Patch setPrettyPrint(final Boolean prettyPrint) {
                    return (Patch)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Patch setQuotaUser(final String quotaUser) {
                    return (Patch)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Patch setUploadType(final String uploadType) {
                    return (Patch)super.setUploadType(uploadType);
                }
                
                @Override
                public Patch setUploadProtocol(final String uploadProtocol) {
                    return (Patch)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Patch setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                @Override
                public Patch set(final String parameterName, final Object value) {
                    return (Patch)super.set(parameterName, value);
                }
            }
            
            public class Update extends DirectoryRequest<UserPhoto>
            {
                private static final String REST_PATH = "admin/directory/v1/users/{userKey}/photos/thumbnail";
                @Key
                private String userKey;
                
                protected Update(final String userKey, final UserPhoto content) {
                    super(Photos.this.this$1.this$0, "PUT", "admin/directory/v1/users/{userKey}/photos/thumbnail", content, UserPhoto.class);
                    this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
                    this.checkRequiredParameter((Object)content, "content");
                    this.checkRequiredParameter((Object)content.getPhotoData(), "UserPhoto.getPhotoData()");
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
                
                public String getUserKey() {
                    return this.userKey;
                }
                
                public Update setUserKey(final String userKey) {
                    this.userKey = userKey;
                    return this;
                }
                
                @Override
                public Update set(final String parameterName, final Object value) {
                    return (Update)super.set(parameterName, value);
                }
            }
        }
    }
    
    public class VerificationCodes
    {
        final /* synthetic */ Directory this$0;
        
        public Generate generate(final String userKey) throws IOException {
            final Generate result = new Generate(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public Invalidate invalidate(final String userKey) throws IOException {
            final Invalidate result = new Invalidate(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public List list(final String userKey) throws IOException {
            final List result = new List(userKey);
            Directory.this.initialize((AbstractGoogleClientRequest<?>)result);
            return result;
        }
        
        public class Generate extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/verificationCodes/generate";
            @Key
            private String userKey;
            
            protected Generate(final String userKey) {
                super(VerificationCodes.this.this$0, "POST", "admin/directory/v1/users/{userKey}/verificationCodes/generate", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public Generate set$Xgafv(final String $Xgafv) {
                return (Generate)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Generate setAccessToken(final String accessToken) {
                return (Generate)super.setAccessToken(accessToken);
            }
            
            @Override
            public Generate setAlt(final String alt) {
                return (Generate)super.setAlt(alt);
            }
            
            @Override
            public Generate setCallback(final String callback) {
                return (Generate)super.setCallback(callback);
            }
            
            @Override
            public Generate setFields(final String fields) {
                return (Generate)super.setFields(fields);
            }
            
            @Override
            public Generate setKey(final String key) {
                return (Generate)super.setKey(key);
            }
            
            @Override
            public Generate setOauthToken(final String oauthToken) {
                return (Generate)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Generate setPrettyPrint(final Boolean prettyPrint) {
                return (Generate)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Generate setQuotaUser(final String quotaUser) {
                return (Generate)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Generate setUploadType(final String uploadType) {
                return (Generate)super.setUploadType(uploadType);
            }
            
            @Override
            public Generate setUploadProtocol(final String uploadProtocol) {
                return (Generate)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Generate setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Generate set(final String parameterName, final Object value) {
                return (Generate)super.set(parameterName, value);
            }
        }
        
        public class Invalidate extends DirectoryRequest<Void>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/verificationCodes/invalidate";
            @Key
            private String userKey;
            
            protected Invalidate(final String userKey) {
                super(VerificationCodes.this.this$0, "POST", "admin/directory/v1/users/{userKey}/verificationCodes/invalidate", null, Void.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
            }
            
            @Override
            public Invalidate set$Xgafv(final String $Xgafv) {
                return (Invalidate)super.set$Xgafv($Xgafv);
            }
            
            @Override
            public Invalidate setAccessToken(final String accessToken) {
                return (Invalidate)super.setAccessToken(accessToken);
            }
            
            @Override
            public Invalidate setAlt(final String alt) {
                return (Invalidate)super.setAlt(alt);
            }
            
            @Override
            public Invalidate setCallback(final String callback) {
                return (Invalidate)super.setCallback(callback);
            }
            
            @Override
            public Invalidate setFields(final String fields) {
                return (Invalidate)super.setFields(fields);
            }
            
            @Override
            public Invalidate setKey(final String key) {
                return (Invalidate)super.setKey(key);
            }
            
            @Override
            public Invalidate setOauthToken(final String oauthToken) {
                return (Invalidate)super.setOauthToken(oauthToken);
            }
            
            @Override
            public Invalidate setPrettyPrint(final Boolean prettyPrint) {
                return (Invalidate)super.setPrettyPrint(prettyPrint);
            }
            
            @Override
            public Invalidate setQuotaUser(final String quotaUser) {
                return (Invalidate)super.setQuotaUser(quotaUser);
            }
            
            @Override
            public Invalidate setUploadType(final String uploadType) {
                return (Invalidate)super.setUploadType(uploadType);
            }
            
            @Override
            public Invalidate setUploadProtocol(final String uploadProtocol) {
                return (Invalidate)super.setUploadProtocol(uploadProtocol);
            }
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public Invalidate setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public Invalidate set(final String parameterName, final Object value) {
                return (Invalidate)super.set(parameterName, value);
            }
        }
        
        public class List extends DirectoryRequest<com.google.api.services.directory.model.VerificationCodes>
        {
            private static final String REST_PATH = "admin/directory/v1/users/{userKey}/verificationCodes";
            @Key
            private String userKey;
            
            protected List(final String userKey) {
                super(VerificationCodes.this.this$0, "GET", "admin/directory/v1/users/{userKey}/verificationCodes", null, com.google.api.services.directory.model.VerificationCodes.class);
                this.userKey = (String)Preconditions.checkNotNull((Object)userKey, (Object)"Required parameter userKey must be specified.");
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
            
            public String getUserKey() {
                return this.userKey;
            }
            
            public List setUserKey(final String userKey) {
                this.userKey = userKey;
                return this;
            }
            
            @Override
            public List set(final String parameterName, final Object value) {
                return (List)super.set(parameterName, value);
            }
        }
    }
    
    public static final class Builder extends AbstractGoogleJsonClient.Builder
    {
        private static String chooseEndpoint(final HttpTransport transport) {
            String useMtlsEndpoint = System.getenv("GOOGLE_API_USE_MTLS_ENDPOINT");
            useMtlsEndpoint = ((useMtlsEndpoint == null) ? "auto" : useMtlsEndpoint);
            if ("always".equals(useMtlsEndpoint) || ("auto".equals(useMtlsEndpoint) && transport != null && transport.isMtls())) {
                return "https://admin.mtls.googleapis.com/";
            }
            return "https://admin.googleapis.com/";
        }
        
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
            super(transport, jsonFactory, chooseEndpoint(transport), "", httpRequestInitializer, false);
            this.setBatchPath("batch");
        }
        
        public Directory build() {
            return new Directory(this);
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
        
        public Builder setDirectoryRequestInitializer(final DirectoryRequestInitializer directoryRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer((GoogleClientRequestInitializer)directoryRequestInitializer);
        }
        
        public Builder setGoogleClientRequestInitializer(final GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }
}
