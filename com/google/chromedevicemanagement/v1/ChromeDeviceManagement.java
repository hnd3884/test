package com.google.chromedevicemanagement.v1;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.chromedevicemanagement.v1.model.BatchUpdateUserPolicyResponse;
import com.google.chromedevicemanagement.v1.model.BatchDeleteUserPolicyResponse;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.google.chromedevicemanagement.v1.model.BatchUpdateUserPolicyRequest;
import com.google.chromedevicemanagement.v1.model.BatchDeleteUserPolicyRequest;
import com.google.chromedevicemanagement.v1.model.CreateEnterpriseFileResponse;
import com.google.chromedevicemanagement.v1.model.StartCreateEnterpriseFileResponse;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.chromedevicemanagement.v1.model.CreateEnterpriseFileRequest;
import com.google.chromedevicemanagement.v1.model.StartCreateEnterpriseFileRequest;
import com.google.chromedevicemanagement.v1.model.ListOperationsResponse;
import com.google.chromedevicemanagement.v1.model.ListDevicesResponse;
import com.google.chromedevicemanagement.v1.model.Operation;
import com.google.chromedevicemanagement.v1.model.Device;
import com.google.chromedevicemanagement.v1.model.Empty;
import com.google.chromedevicemanagement.v1.model.BatchUpdateDevicePolicyResponse;
import java.util.List;
import com.google.chromedevicemanagement.v1.model.BatchGetDeviceResponse;
import com.google.chromedevicemanagement.v1.model.BatchDeleteDevicePolicyResponse;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import com.google.chromedevicemanagement.v1.model.IssueDeviceCommandRequest;
import com.google.chromedevicemanagement.v1.model.BatchUpdateDevicePolicyRequest;
import com.google.chromedevicemanagement.v1.model.BatchDeleteDevicePolicyRequest;
import com.google.chromedevicemanagement.v1.model.ListEnterprisesResponse;
import com.google.api.client.util.GenericData;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Key;
import java.util.regex.Pattern;
import com.google.chromedevicemanagement.v1.model.Enterprise;
import com.google.api.client.util.Preconditions;
import com.google.api.client.googleapis.GoogleUtils;
import java.io.IOException;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

public class ChromeDeviceManagement extends AbstractGoogleJsonClient
{
    public static final String DEFAULT_ROOT_URL = "https://chromedevicemanagement.googleapis.com/";
    public static final String DEFAULT_SERVICE_PATH = "";
    public static final String DEFAULT_BASE_URL = "https://chromedevicemanagement.googleapis.com/";
    
    public ChromeDeviceManagement(final HttpTransport httpTransport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(httpTransport, jsonFactory, httpRequestInitializer));
    }
    
    ChromeDeviceManagement(final Builder builder) {
        super((AbstractGoogleJsonClient.Builder)builder);
    }
    
    protected void initialize(final AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
        super.initialize((AbstractGoogleClientRequest)abstractGoogleClientRequest);
    }
    
    public Enterprises enterprises() {
        return new Enterprises();
    }
    
    static {
        Preconditions.checkState(GoogleUtils.MAJOR_VERSION == 1 && GoogleUtils.MINOR_VERSION >= 15, "You are currently running with version %s of google-api-client. You need at least version 1.15 of google-api-client to run version 1.19.1 of the Chrome Device Management API library.", new Object[] { GoogleUtils.VERSION });
    }
    
    public class Enterprises
    {
        final /* synthetic */ ChromeDeviceManagement this$0;
        
        public Get get(final String s) throws IOException {
            final Get get = new Get(s);
            ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)get);
            return get;
        }
        
        public List list() throws IOException {
            final List list = new List();
            ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)list);
            return list;
        }
        
        public Patch patch(final String s, final Enterprise enterprise) throws IOException {
            final Patch patch = new Patch(s, enterprise);
            ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)patch);
            return patch;
        }
        
        public Devices devices() {
            return new Devices();
        }
        
        public Files files() {
            return new Files();
        }
        
        public Users users() {
            return new Users();
        }
        
        public class Get extends ChromeDeviceManagementRequest<Enterprise>
        {
            private static final String REST_PATH = "v1/{+name}";
            private final Pattern NAME_PATTERN;
            @Key
            private String name;
            @Key
            private String view;
            
            protected Get(final String s) {
                super(Enterprises.this.this$0, "GET", "v1/{+name}", null, Enterprise.class);
                this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                if (!Enterprises.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                }
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public Get set$Xgafv(final String s) {
                return (Get)super.set$Xgafv(s);
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
                if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                }
                this.name = name;
                return this;
            }
            
            public String getView() {
                return this.view;
            }
            
            public Get setView(final String view) {
                this.view = view;
                return this;
            }
            
            @Override
            public Get set(final String s, final Object o) {
                return (Get)super.set(s, o);
            }
        }
        
        public class List extends ChromeDeviceManagementRequest<ListEnterprisesResponse>
        {
            private static final String REST_PATH = "v1/enterprises";
            @Key
            private String enterpriseName;
            
            protected List() {
                super(Enterprises.this.this$0, "GET", "v1/enterprises", null, ListEnterprisesResponse.class);
            }
            
            public HttpResponse executeUsingHead() throws IOException {
                return super.executeUsingHead();
            }
            
            public HttpRequest buildHttpRequestUsingHead() throws IOException {
                return super.buildHttpRequestUsingHead();
            }
            
            @Override
            public List set$Xgafv(final String s) {
                return (List)super.set$Xgafv(s);
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
            
            public String getEnterpriseName() {
                return this.enterpriseName;
            }
            
            public List setEnterpriseName(final String enterpriseName) {
                this.enterpriseName = enterpriseName;
                return this;
            }
            
            @Override
            public List set(final String s, final Object o) {
                return (List)super.set(s, o);
            }
        }
        
        public class Patch extends ChromeDeviceManagementRequest<Enterprise>
        {
            private static final String REST_PATH = "v1/{+name}";
            private final Pattern NAME_PATTERN;
            @Key
            private String name;
            @Key
            private String updateMask;
            
            protected Patch(final String s, final Enterprise enterprise) {
                super(Enterprises.this.this$0, "PATCH", "v1/{+name}", enterprise, Enterprise.class);
                this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                if (!Enterprises.this.this$0.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                }
            }
            
            @Override
            public Patch set$Xgafv(final String s) {
                return (Patch)super.set$Xgafv(s);
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
                if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                    Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                }
                this.name = name;
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
            public Patch set(final String s, final Object o) {
                return (Patch)super.set(s, o);
            }
        }
        
        public class Devices
        {
            final /* synthetic */ Enterprises this$1;
            
            public BatchDeleteDevicePolicy batchDeleteDevicePolicy(final String s, final BatchDeleteDevicePolicyRequest batchDeleteDevicePolicyRequest) throws IOException {
                final BatchDeleteDevicePolicy batchDeleteDevicePolicy = new BatchDeleteDevicePolicy(s, batchDeleteDevicePolicyRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)batchDeleteDevicePolicy);
                return batchDeleteDevicePolicy;
            }
            
            public BatchGet batchGet(final String s) throws IOException {
                final BatchGet batchGet = new BatchGet(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)batchGet);
                return batchGet;
            }
            
            public BatchUpdateDevicePolicy batchUpdateDevicePolicy(final String s, final BatchUpdateDevicePolicyRequest batchUpdateDevicePolicyRequest) throws IOException {
                final BatchUpdateDevicePolicy batchUpdateDevicePolicy = new BatchUpdateDevicePolicy(s, batchUpdateDevicePolicyRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)batchUpdateDevicePolicy);
                return batchUpdateDevicePolicy;
            }
            
            public DeleteDevicePolicy deleteDevicePolicy(final String s) throws IOException {
                final DeleteDevicePolicy deleteDevicePolicy = new DeleteDevicePolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)deleteDevicePolicy);
                return deleteDevicePolicy;
            }
            
            public Get get(final String s) throws IOException {
                final Get get = new Get(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)get);
                return get;
            }
            
            public GetCompiledDevicePolicy getCompiledDevicePolicy(final String s) throws IOException {
                final GetCompiledDevicePolicy getCompiledDevicePolicy = new GetCompiledDevicePolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)getCompiledDevicePolicy);
                return getCompiledDevicePolicy;
            }
            
            public GetDevicePolicy getDevicePolicy(final String s) throws IOException {
                final GetDevicePolicy getDevicePolicy = new GetDevicePolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)getDevicePolicy);
                return getDevicePolicy;
            }
            
            public IssueCommand issueCommand(final String s, final IssueDeviceCommandRequest issueDeviceCommandRequest) throws IOException {
                final IssueCommand issueCommand = new IssueCommand(s, issueDeviceCommandRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)issueCommand);
                return issueCommand;
            }
            
            public List list(final String s) throws IOException {
                final List list = new List(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)list);
                return list;
            }
            
            public UpdateDevicePolicy updateDevicePolicy(final String s, final DevicePolicy devicePolicy) throws IOException {
                final UpdateDevicePolicy updateDevicePolicy = new UpdateDevicePolicy(s, devicePolicy);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)updateDevicePolicy);
                return updateDevicePolicy;
            }
            
            public Operations operations() {
                return new Operations();
            }
            
            public class BatchDeleteDevicePolicy extends ChromeDeviceManagementRequest<BatchDeleteDevicePolicyResponse>
            {
                private static final String REST_PATH = "v1/{+name}/devices:batchDeleteDevicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected BatchDeleteDevicePolicy(final String s, final BatchDeleteDevicePolicyRequest batchDeleteDevicePolicyRequest) {
                    super(Devices.this.this$1.this$0, "POST", "v1/{+name}/devices:batchDeleteDevicePolicy", batchDeleteDevicePolicyRequest, BatchDeleteDevicePolicyResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                @Override
                public BatchDeleteDevicePolicy set$Xgafv(final String s) {
                    return (BatchDeleteDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public BatchDeleteDevicePolicy setAccessToken(final String accessToken) {
                    return (BatchDeleteDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public BatchDeleteDevicePolicy setAlt(final String alt) {
                    return (BatchDeleteDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public BatchDeleteDevicePolicy setCallback(final String callback) {
                    return (BatchDeleteDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public BatchDeleteDevicePolicy setFields(final String fields) {
                    return (BatchDeleteDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public BatchDeleteDevicePolicy setKey(final String key) {
                    return (BatchDeleteDevicePolicy)super.setKey(key);
                }
                
                @Override
                public BatchDeleteDevicePolicy setOauthToken(final String oauthToken) {
                    return (BatchDeleteDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public BatchDeleteDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (BatchDeleteDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public BatchDeleteDevicePolicy setQuotaUser(final String quotaUser) {
                    return (BatchDeleteDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public BatchDeleteDevicePolicy setUploadType(final String uploadType) {
                    return (BatchDeleteDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public BatchDeleteDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (BatchDeleteDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public BatchDeleteDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public BatchDeleteDevicePolicy set(final String s, final Object o) {
                    return (BatchDeleteDevicePolicy)super.set(s, o);
                }
            }
            
            public class BatchGet extends ChromeDeviceManagementRequest<BatchGetDeviceResponse>
            {
                private static final String REST_PATH = "v1/{+name}/devices:batchGet";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                @Key
                private java.util.List<String> deviceIds;
                
                protected BatchGet(final String s) {
                    super(Devices.this.this$1.this$0, "GET", "v1/{+name}/devices:batchGet", null, BatchGetDeviceResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public BatchGet set$Xgafv(final String s) {
                    return (BatchGet)super.set$Xgafv(s);
                }
                
                @Override
                public BatchGet setAccessToken(final String accessToken) {
                    return (BatchGet)super.setAccessToken(accessToken);
                }
                
                @Override
                public BatchGet setAlt(final String alt) {
                    return (BatchGet)super.setAlt(alt);
                }
                
                @Override
                public BatchGet setCallback(final String callback) {
                    return (BatchGet)super.setCallback(callback);
                }
                
                @Override
                public BatchGet setFields(final String fields) {
                    return (BatchGet)super.setFields(fields);
                }
                
                @Override
                public BatchGet setKey(final String key) {
                    return (BatchGet)super.setKey(key);
                }
                
                @Override
                public BatchGet setOauthToken(final String oauthToken) {
                    return (BatchGet)super.setOauthToken(oauthToken);
                }
                
                @Override
                public BatchGet setPrettyPrint(final Boolean prettyPrint) {
                    return (BatchGet)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public BatchGet setQuotaUser(final String quotaUser) {
                    return (BatchGet)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public BatchGet setUploadType(final String uploadType) {
                    return (BatchGet)super.setUploadType(uploadType);
                }
                
                @Override
                public BatchGet setUploadProtocol(final String uploadProtocol) {
                    return (BatchGet)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public BatchGet setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                public java.util.List<String> getDeviceIds() {
                    return this.deviceIds;
                }
                
                public BatchGet setDeviceIds(final java.util.List<String> deviceIds) {
                    this.deviceIds = deviceIds;
                    return this;
                }
                
                @Override
                public BatchGet set(final String s, final Object o) {
                    return (BatchGet)super.set(s, o);
                }
            }
            
            public class BatchUpdateDevicePolicy extends ChromeDeviceManagementRequest<BatchUpdateDevicePolicyResponse>
            {
                private static final String REST_PATH = "v1/{+name}/devices:batchUpdateDevicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected BatchUpdateDevicePolicy(final String s, final BatchUpdateDevicePolicyRequest batchUpdateDevicePolicyRequest) {
                    super(Devices.this.this$1.this$0, "POST", "v1/{+name}/devices:batchUpdateDevicePolicy", batchUpdateDevicePolicyRequest, BatchUpdateDevicePolicyResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                @Override
                public BatchUpdateDevicePolicy set$Xgafv(final String s) {
                    return (BatchUpdateDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public BatchUpdateDevicePolicy setAccessToken(final String accessToken) {
                    return (BatchUpdateDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public BatchUpdateDevicePolicy setAlt(final String alt) {
                    return (BatchUpdateDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public BatchUpdateDevicePolicy setCallback(final String callback) {
                    return (BatchUpdateDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public BatchUpdateDevicePolicy setFields(final String fields) {
                    return (BatchUpdateDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public BatchUpdateDevicePolicy setKey(final String key) {
                    return (BatchUpdateDevicePolicy)super.setKey(key);
                }
                
                @Override
                public BatchUpdateDevicePolicy setOauthToken(final String oauthToken) {
                    return (BatchUpdateDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public BatchUpdateDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (BatchUpdateDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public BatchUpdateDevicePolicy setQuotaUser(final String quotaUser) {
                    return (BatchUpdateDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public BatchUpdateDevicePolicy setUploadType(final String uploadType) {
                    return (BatchUpdateDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public BatchUpdateDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (BatchUpdateDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public BatchUpdateDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public BatchUpdateDevicePolicy set(final String s, final Object o) {
                    return (BatchUpdateDevicePolicy)super.set(s, o);
                }
            }
            
            public class DeleteDevicePolicy extends ChromeDeviceManagementRequest<Empty>
            {
                private static final String REST_PATH = "v1/{+name}/devicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected DeleteDevicePolicy(final String s) {
                    super(Devices.this.this$1.this$0, "DELETE", "v1/{+name}/devicePolicy", null, Empty.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                @Override
                public DeleteDevicePolicy set$Xgafv(final String s) {
                    return (DeleteDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public DeleteDevicePolicy setAccessToken(final String accessToken) {
                    return (DeleteDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public DeleteDevicePolicy setAlt(final String alt) {
                    return (DeleteDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public DeleteDevicePolicy setCallback(final String callback) {
                    return (DeleteDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public DeleteDevicePolicy setFields(final String fields) {
                    return (DeleteDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public DeleteDevicePolicy setKey(final String key) {
                    return (DeleteDevicePolicy)super.setKey(key);
                }
                
                @Override
                public DeleteDevicePolicy setOauthToken(final String oauthToken) {
                    return (DeleteDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public DeleteDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (DeleteDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public DeleteDevicePolicy setQuotaUser(final String quotaUser) {
                    return (DeleteDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public DeleteDevicePolicy setUploadType(final String uploadType) {
                    return (DeleteDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public DeleteDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (DeleteDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public DeleteDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public DeleteDevicePolicy set(final String s, final Object o) {
                    return (DeleteDevicePolicy)super.set(s, o);
                }
            }
            
            public class Get extends ChromeDeviceManagementRequest<Device>
            {
                private static final String REST_PATH = "v1/{+name}";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected Get(final String s) {
                    super(Devices.this.this$1.this$0, "GET", "v1/{+name}", null, Device.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public Get set$Xgafv(final String s) {
                    return (Get)super.set$Xgafv(s);
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
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public Get set(final String s, final Object o) {
                    return (Get)super.set(s, o);
                }
            }
            
            public class GetCompiledDevicePolicy extends ChromeDeviceManagementRequest<DevicePolicy>
            {
                private static final String REST_PATH = "v1/{+name}/compiledDevicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected GetCompiledDevicePolicy(final String s) {
                    super(Devices.this.this$1.this$0, "GET", "v1/{+name}/compiledDevicePolicy", null, DevicePolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public GetCompiledDevicePolicy set$Xgafv(final String s) {
                    return (GetCompiledDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public GetCompiledDevicePolicy setAccessToken(final String accessToken) {
                    return (GetCompiledDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public GetCompiledDevicePolicy setAlt(final String alt) {
                    return (GetCompiledDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public GetCompiledDevicePolicy setCallback(final String callback) {
                    return (GetCompiledDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public GetCompiledDevicePolicy setFields(final String fields) {
                    return (GetCompiledDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public GetCompiledDevicePolicy setKey(final String key) {
                    return (GetCompiledDevicePolicy)super.setKey(key);
                }
                
                @Override
                public GetCompiledDevicePolicy setOauthToken(final String oauthToken) {
                    return (GetCompiledDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public GetCompiledDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (GetCompiledDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public GetCompiledDevicePolicy setQuotaUser(final String quotaUser) {
                    return (GetCompiledDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public GetCompiledDevicePolicy setUploadType(final String uploadType) {
                    return (GetCompiledDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public GetCompiledDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (GetCompiledDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public GetCompiledDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public GetCompiledDevicePolicy set(final String s, final Object o) {
                    return (GetCompiledDevicePolicy)super.set(s, o);
                }
            }
            
            public class GetDevicePolicy extends ChromeDeviceManagementRequest<DevicePolicy>
            {
                private static final String REST_PATH = "v1/{+name}/devicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected GetDevicePolicy(final String s) {
                    super(Devices.this.this$1.this$0, "GET", "v1/{+name}/devicePolicy", null, DevicePolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public GetDevicePolicy set$Xgafv(final String s) {
                    return (GetDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public GetDevicePolicy setAccessToken(final String accessToken) {
                    return (GetDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public GetDevicePolicy setAlt(final String alt) {
                    return (GetDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public GetDevicePolicy setCallback(final String callback) {
                    return (GetDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public GetDevicePolicy setFields(final String fields) {
                    return (GetDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public GetDevicePolicy setKey(final String key) {
                    return (GetDevicePolicy)super.setKey(key);
                }
                
                @Override
                public GetDevicePolicy setOauthToken(final String oauthToken) {
                    return (GetDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public GetDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (GetDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public GetDevicePolicy setQuotaUser(final String quotaUser) {
                    return (GetDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public GetDevicePolicy setUploadType(final String uploadType) {
                    return (GetDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public GetDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (GetDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public GetDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public GetDevicePolicy set(final String s, final Object o) {
                    return (GetDevicePolicy)super.set(s, o);
                }
            }
            
            public class IssueCommand extends ChromeDeviceManagementRequest<Operation>
            {
                private static final String REST_PATH = "v1/{+name}:issueCommand";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected IssueCommand(final String s, final IssueDeviceCommandRequest issueDeviceCommandRequest) {
                    super(Devices.this.this$1.this$0, "POST", "v1/{+name}:issueCommand", issueDeviceCommandRequest, Operation.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                @Override
                public IssueCommand set$Xgafv(final String s) {
                    return (IssueCommand)super.set$Xgafv(s);
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
                
                public String getName() {
                    return this.name;
                }
                
                public IssueCommand setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public IssueCommand set(final String s, final Object o) {
                    return (IssueCommand)super.set(s, o);
                }
            }
            
            public class List extends ChromeDeviceManagementRequest<ListDevicesResponse>
            {
                private static final String REST_PATH = "v1/{+parent}/devices";
                private final Pattern PARENT_PATTERN;
                @Key
                private String parent;
                @Key
                private String sortOrder;
                @Key
                private String view;
                @Key
                private String deviceQuery;
                @Key
                private Integer pageSize;
                @Key
                private String pageToken;
                @Key
                private String sortByColumn;
                
                protected List(final String s) {
                    super(Devices.this.this$1.this$0, "GET", "v1/{+parent}/devices", null, ListDevicesResponse.class);
                    this.PARENT_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.parent = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter parent must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.PARENT_PATTERN.matcher(s).matches(), (Object)"Parameter parent must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public List set$Xgafv(final String s) {
                    return (List)super.set$Xgafv(s);
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
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.parent = parent;
                    return this;
                }
                
                public String getSortOrder() {
                    return this.sortOrder;
                }
                
                public List setSortOrder(final String sortOrder) {
                    this.sortOrder = sortOrder;
                    return this;
                }
                
                public String getView() {
                    return this.view;
                }
                
                public List setView(final String view) {
                    this.view = view;
                    return this;
                }
                
                public String getDeviceQuery() {
                    return this.deviceQuery;
                }
                
                public List setDeviceQuery(final String deviceQuery) {
                    this.deviceQuery = deviceQuery;
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
                
                public String getSortByColumn() {
                    return this.sortByColumn;
                }
                
                public List setSortByColumn(final String sortByColumn) {
                    this.sortByColumn = sortByColumn;
                    return this;
                }
                
                @Override
                public List set(final String s, final Object o) {
                    return (List)super.set(s, o);
                }
            }
            
            public class UpdateDevicePolicy extends ChromeDeviceManagementRequest<DevicePolicy>
            {
                private static final String REST_PATH = "v1/{+name}/devicePolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                @Key
                private String updateMask;
                
                protected UpdateDevicePolicy(final String s, final DevicePolicy devicePolicy) {
                    super(Devices.this.this$1.this$0, "PATCH", "v1/{+name}/devicePolicy", devicePolicy, DevicePolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Devices.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                }
                
                @Override
                public UpdateDevicePolicy set$Xgafv(final String s) {
                    return (UpdateDevicePolicy)super.set$Xgafv(s);
                }
                
                @Override
                public UpdateDevicePolicy setAccessToken(final String accessToken) {
                    return (UpdateDevicePolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public UpdateDevicePolicy setAlt(final String alt) {
                    return (UpdateDevicePolicy)super.setAlt(alt);
                }
                
                @Override
                public UpdateDevicePolicy setCallback(final String callback) {
                    return (UpdateDevicePolicy)super.setCallback(callback);
                }
                
                @Override
                public UpdateDevicePolicy setFields(final String fields) {
                    return (UpdateDevicePolicy)super.setFields(fields);
                }
                
                @Override
                public UpdateDevicePolicy setKey(final String key) {
                    return (UpdateDevicePolicy)super.setKey(key);
                }
                
                @Override
                public UpdateDevicePolicy setOauthToken(final String oauthToken) {
                    return (UpdateDevicePolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public UpdateDevicePolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (UpdateDevicePolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public UpdateDevicePolicy setQuotaUser(final String quotaUser) {
                    return (UpdateDevicePolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public UpdateDevicePolicy setUploadType(final String uploadType) {
                    return (UpdateDevicePolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public UpdateDevicePolicy setUploadProtocol(final String uploadProtocol) {
                    return (UpdateDevicePolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public UpdateDevicePolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                public String getUpdateMask() {
                    return this.updateMask;
                }
                
                public UpdateDevicePolicy setUpdateMask(final String updateMask) {
                    this.updateMask = updateMask;
                    return this;
                }
                
                @Override
                public UpdateDevicePolicy set(final String s, final Object o) {
                    return (UpdateDevicePolicy)super.set(s, o);
                }
            }
            
            public class Operations
            {
                final /* synthetic */ Devices this$2;
                
                public Cancel cancel(final String s) throws IOException {
                    final Cancel cancel = new Cancel(s);
                    ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)cancel);
                    return cancel;
                }
                
                public Delete delete(final String s) throws IOException {
                    final Delete delete = new Delete(s);
                    ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)delete);
                    return delete;
                }
                
                public Get get(final String s) throws IOException {
                    final Get get = new Get(s);
                    ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)get);
                    return get;
                }
                
                public List list(final String s) throws IOException {
                    final List list = new List(s);
                    ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)list);
                    return list;
                }
                
                public class Cancel extends ChromeDeviceManagementRequest<Empty>
                {
                    private static final String REST_PATH = "v1/{+name}:cancel";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    
                    protected Cancel(final String s) {
                        super(Operations.this.this$2.this$1.this$0, "POST", "v1/{+name}:cancel", null, Empty.class);
                        this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                        if (!Operations.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                    }
                    
                    @Override
                    public Cancel set$Xgafv(final String s) {
                        return (Cancel)super.set$Xgafv(s);
                    }
                    
                    @Override
                    public Cancel setAccessToken(final String accessToken) {
                        return (Cancel)super.setAccessToken(accessToken);
                    }
                    
                    @Override
                    public Cancel setAlt(final String alt) {
                        return (Cancel)super.setAlt(alt);
                    }
                    
                    @Override
                    public Cancel setCallback(final String callback) {
                        return (Cancel)super.setCallback(callback);
                    }
                    
                    @Override
                    public Cancel setFields(final String fields) {
                        return (Cancel)super.setFields(fields);
                    }
                    
                    @Override
                    public Cancel setKey(final String key) {
                        return (Cancel)super.setKey(key);
                    }
                    
                    @Override
                    public Cancel setOauthToken(final String oauthToken) {
                        return (Cancel)super.setOauthToken(oauthToken);
                    }
                    
                    @Override
                    public Cancel setPrettyPrint(final Boolean prettyPrint) {
                        return (Cancel)super.setPrettyPrint(prettyPrint);
                    }
                    
                    @Override
                    public Cancel setQuotaUser(final String quotaUser) {
                        return (Cancel)super.setQuotaUser(quotaUser);
                    }
                    
                    @Override
                    public Cancel setUploadType(final String uploadType) {
                        return (Cancel)super.setUploadType(uploadType);
                    }
                    
                    @Override
                    public Cancel setUploadProtocol(final String uploadProtocol) {
                        return (Cancel)super.setUploadProtocol(uploadProtocol);
                    }
                    
                    public String getName() {
                        return this.name;
                    }
                    
                    public Cancel setName(final String name) {
                        if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    @Override
                    public Cancel set(final String s, final Object o) {
                        return (Cancel)super.set(s, o);
                    }
                }
                
                public class Delete extends ChromeDeviceManagementRequest<Empty>
                {
                    private static final String REST_PATH = "v1/{+name}";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    
                    protected Delete(final String s) {
                        super(Operations.this.this$2.this$1.this$0, "DELETE", "v1/{+name}", null, Empty.class);
                        this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                        if (!Operations.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                    }
                    
                    @Override
                    public Delete set$Xgafv(final String s) {
                        return (Delete)super.set$Xgafv(s);
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
                        if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    @Override
                    public Delete set(final String s, final Object o) {
                        return (Delete)super.set(s, o);
                    }
                }
                
                public class Get extends ChromeDeviceManagementRequest<Operation>
                {
                    private static final String REST_PATH = "v1/{+name}";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    
                    protected Get(final String s) {
                        super(Operations.this.this$2.this$1.this$0, "GET", "v1/{+name}", null, Operation.class);
                        this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                        if (!Operations.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                    }
                    
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }
                    
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }
                    
                    @Override
                    public Get set$Xgafv(final String s) {
                        return (Get)super.set$Xgafv(s);
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
                        if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+/operations/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    @Override
                    public Get set(final String s, final Object o) {
                        return (Get)super.set(s, o);
                    }
                }
                
                public class List extends ChromeDeviceManagementRequest<ListOperationsResponse>
                {
                    private static final String REST_PATH = "v1/{+name}/operations";
                    private final Pattern NAME_PATTERN;
                    @Key
                    private String name;
                    @Key
                    private String pageToken;
                    @Key
                    private Integer pageSize;
                    @Key
                    private String filter;
                    
                    protected List(final String s) {
                        super(Operations.this.this$2.this$1.this$0, "GET", "v1/{+name}/operations", null, ListOperationsResponse.class);
                        this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/devices/[^/]+$");
                        this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                        if (!Operations.this.this$2.this$1.this$0.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                        }
                    }
                    
                    public HttpResponse executeUsingHead() throws IOException {
                        return super.executeUsingHead();
                    }
                    
                    public HttpRequest buildHttpRequestUsingHead() throws IOException {
                        return super.buildHttpRequestUsingHead();
                    }
                    
                    @Override
                    public List set$Xgafv(final String s) {
                        return (List)super.set$Xgafv(s);
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
                    
                    public String getName() {
                        return this.name;
                    }
                    
                    public List setName(final String name) {
                        if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                            Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/devices/[^/]+$");
                        }
                        this.name = name;
                        return this;
                    }
                    
                    public String getPageToken() {
                        return this.pageToken;
                    }
                    
                    public List setPageToken(final String pageToken) {
                        this.pageToken = pageToken;
                        return this;
                    }
                    
                    public Integer getPageSize() {
                        return this.pageSize;
                    }
                    
                    public List setPageSize(final Integer pageSize) {
                        this.pageSize = pageSize;
                        return this;
                    }
                    
                    public String getFilter() {
                        return this.filter;
                    }
                    
                    public List setFilter(final String filter) {
                        this.filter = filter;
                        return this;
                    }
                    
                    @Override
                    public List set(final String s, final Object o) {
                        return (List)super.set(s, o);
                    }
                }
            }
        }
        
        public class Files
        {
            final /* synthetic */ Enterprises this$1;
            
            public Init init(final String s, final StartCreateEnterpriseFileRequest startCreateEnterpriseFileRequest) throws IOException {
                final Init init = new Init(s, startCreateEnterpriseFileRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)init);
                return init;
            }
            
            public Upload upload(final String s, final String s2, final CreateEnterpriseFileRequest createEnterpriseFileRequest) throws IOException {
                final Upload upload = new Upload(s, s2, createEnterpriseFileRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)upload);
                return upload;
            }
            
            public Upload upload(final String s, final String s2, final CreateEnterpriseFileRequest createEnterpriseFileRequest, final AbstractInputStreamContent abstractInputStreamContent) throws IOException {
                final Upload upload = new Upload(s, s2, createEnterpriseFileRequest, abstractInputStreamContent);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)upload);
                return upload;
            }
            
            public class Init extends ChromeDeviceManagementRequest<StartCreateEnterpriseFileResponse>
            {
                private static final String REST_PATH = "v1/{+name}/files:init";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected Init(final String s, final StartCreateEnterpriseFileRequest startCreateEnterpriseFileRequest) {
                    super(Files.this.this$1.this$0, "POST", "v1/{+name}/files:init", startCreateEnterpriseFileRequest, StartCreateEnterpriseFileResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Files.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                @Override
                public Init set$Xgafv(final String s) {
                    return (Init)super.set$Xgafv(s);
                }
                
                @Override
                public Init setAccessToken(final String accessToken) {
                    return (Init)super.setAccessToken(accessToken);
                }
                
                @Override
                public Init setAlt(final String alt) {
                    return (Init)super.setAlt(alt);
                }
                
                @Override
                public Init setCallback(final String callback) {
                    return (Init)super.setCallback(callback);
                }
                
                @Override
                public Init setFields(final String fields) {
                    return (Init)super.setFields(fields);
                }
                
                @Override
                public Init setKey(final String key) {
                    return (Init)super.setKey(key);
                }
                
                @Override
                public Init setOauthToken(final String oauthToken) {
                    return (Init)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Init setPrettyPrint(final Boolean prettyPrint) {
                    return (Init)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Init setQuotaUser(final String quotaUser) {
                    return (Init)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Init setUploadType(final String uploadType) {
                    return (Init)super.setUploadType(uploadType);
                }
                
                @Override
                public Init setUploadProtocol(final String uploadProtocol) {
                    return (Init)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public Init setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public Init set(final String s, final Object o) {
                    return (Init)super.set(s, o);
                }
            }
            
            public class Upload extends ChromeDeviceManagementRequest<CreateEnterpriseFileResponse>
            {
                private static final String REST_PATH = "v1/{+parent}/files/{+transientFileId}";
                private final Pattern PARENT_PATTERN;
                private final Pattern TRANSIENT_FILE_ID_PATTERN;
                @Key
                private String parent;
                @Key
                private String transientFileId;
                
                protected Upload(final String s, final String s2, final CreateEnterpriseFileRequest createEnterpriseFileRequest) {
                    super(Files.this.this$1.this$0, "POST", "v1/{+parent}/files/{+transientFileId}", createEnterpriseFileRequest, CreateEnterpriseFileResponse.class);
                    this.PARENT_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.TRANSIENT_FILE_ID_PATTERN = Pattern.compile("^[^/]+$");
                    this.parent = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter parent must be specified.");
                    if (!Files.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.PARENT_PATTERN.matcher(s).matches(), (Object)"Parameter parent must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.transientFileId = (String)Preconditions.checkNotNull((Object)s2, (Object)"Required parameter transientFileId must be specified.");
                    if (!Files.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.TRANSIENT_FILE_ID_PATTERN.matcher(s2).matches(), (Object)"Parameter transientFileId must conform to the pattern ^[^/]+$");
                    }
                }
                
                protected Upload(final String s, final String s2, final CreateEnterpriseFileRequest createEnterpriseFileRequest, final AbstractInputStreamContent abstractInputStreamContent) {
                    super(Files.this.this$1.this$0, "POST", "/upload/" + Files.this.this$1.this$0.getServicePath() + "v1/{+parent}/files/{+transientFileId}", createEnterpriseFileRequest, CreateEnterpriseFileResponse.class);
                    this.PARENT_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.TRANSIENT_FILE_ID_PATTERN = Pattern.compile("^[^/]+$");
                    this.parent = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter parent must be specified.");
                    this.transientFileId = (String)Preconditions.checkNotNull((Object)s2, (Object)"Required parameter transientFileId must be specified.");
                    this.initializeMediaUpload(abstractInputStreamContent);
                }
                
                @Override
                public Upload set$Xgafv(final String s) {
                    return (Upload)super.set$Xgafv(s);
                }
                
                @Override
                public Upload setAccessToken(final String accessToken) {
                    return (Upload)super.setAccessToken(accessToken);
                }
                
                @Override
                public Upload setAlt(final String alt) {
                    return (Upload)super.setAlt(alt);
                }
                
                @Override
                public Upload setCallback(final String callback) {
                    return (Upload)super.setCallback(callback);
                }
                
                @Override
                public Upload setFields(final String fields) {
                    return (Upload)super.setFields(fields);
                }
                
                @Override
                public Upload setKey(final String key) {
                    return (Upload)super.setKey(key);
                }
                
                @Override
                public Upload setOauthToken(final String oauthToken) {
                    return (Upload)super.setOauthToken(oauthToken);
                }
                
                @Override
                public Upload setPrettyPrint(final Boolean prettyPrint) {
                    return (Upload)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public Upload setQuotaUser(final String quotaUser) {
                    return (Upload)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public Upload setUploadType(final String uploadType) {
                    return (Upload)super.setUploadType(uploadType);
                }
                
                @Override
                public Upload setUploadProtocol(final String uploadProtocol) {
                    return (Upload)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getParent() {
                    return this.parent;
                }
                
                public Upload setParent(final String parent) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.PARENT_PATTERN.matcher(parent).matches(), (Object)"Parameter parent must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.parent = parent;
                    return this;
                }
                
                public String getTransientFileId() {
                    return this.transientFileId;
                }
                
                public Upload setTransientFileId(final String transientFileId) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.TRANSIENT_FILE_ID_PATTERN.matcher(transientFileId).matches(), (Object)"Parameter transientFileId must conform to the pattern ^[^/]+$");
                    }
                    this.transientFileId = transientFileId;
                    return this;
                }
                
                @Override
                public Upload set(final String s, final Object o) {
                    return (Upload)super.set(s, o);
                }
            }
        }
        
        public class Users
        {
            final /* synthetic */ Enterprises this$1;
            
            public BatchDeleteUserPolicy batchDeleteUserPolicy(final String s, final BatchDeleteUserPolicyRequest batchDeleteUserPolicyRequest) throws IOException {
                final BatchDeleteUserPolicy batchDeleteUserPolicy = new BatchDeleteUserPolicy(s, batchDeleteUserPolicyRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)batchDeleteUserPolicy);
                return batchDeleteUserPolicy;
            }
            
            public BatchUpdateUserPolicy batchUpdateUserPolicy(final String s, final BatchUpdateUserPolicyRequest batchUpdateUserPolicyRequest) throws IOException {
                final BatchUpdateUserPolicy batchUpdateUserPolicy = new BatchUpdateUserPolicy(s, batchUpdateUserPolicyRequest);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)batchUpdateUserPolicy);
                return batchUpdateUserPolicy;
            }
            
            public DeleteUserPolicy deleteUserPolicy(final String s) throws IOException {
                final DeleteUserPolicy deleteUserPolicy = new DeleteUserPolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)deleteUserPolicy);
                return deleteUserPolicy;
            }
            
            public GetCompiledUserPolicy getCompiledUserPolicy(final String s) throws IOException {
                final GetCompiledUserPolicy getCompiledUserPolicy = new GetCompiledUserPolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)getCompiledUserPolicy);
                return getCompiledUserPolicy;
            }
            
            public GetUserPolicy getUserPolicy(final String s) throws IOException {
                final GetUserPolicy getUserPolicy = new GetUserPolicy(s);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)getUserPolicy);
                return getUserPolicy;
            }
            
            public UpdateUserPolicy updateUserPolicy(final String s, final UserPolicy userPolicy) throws IOException {
                final UpdateUserPolicy updateUserPolicy = new UpdateUserPolicy(s, userPolicy);
                ChromeDeviceManagement.this.initialize((AbstractGoogleClientRequest<?>)updateUserPolicy);
                return updateUserPolicy;
            }
            
            public class BatchDeleteUserPolicy extends ChromeDeviceManagementRequest<BatchDeleteUserPolicyResponse>
            {
                private static final String REST_PATH = "v1/{+name}/users:batchDeleteUserPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected BatchDeleteUserPolicy(final String s, final BatchDeleteUserPolicyRequest batchDeleteUserPolicyRequest) {
                    super(Users.this.this$1.this$0, "POST", "v1/{+name}/users:batchDeleteUserPolicy", batchDeleteUserPolicyRequest, BatchDeleteUserPolicyResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                @Override
                public BatchDeleteUserPolicy set$Xgafv(final String s) {
                    return (BatchDeleteUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public BatchDeleteUserPolicy setAccessToken(final String accessToken) {
                    return (BatchDeleteUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public BatchDeleteUserPolicy setAlt(final String alt) {
                    return (BatchDeleteUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public BatchDeleteUserPolicy setCallback(final String callback) {
                    return (BatchDeleteUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public BatchDeleteUserPolicy setFields(final String fields) {
                    return (BatchDeleteUserPolicy)super.setFields(fields);
                }
                
                @Override
                public BatchDeleteUserPolicy setKey(final String key) {
                    return (BatchDeleteUserPolicy)super.setKey(key);
                }
                
                @Override
                public BatchDeleteUserPolicy setOauthToken(final String oauthToken) {
                    return (BatchDeleteUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public BatchDeleteUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (BatchDeleteUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public BatchDeleteUserPolicy setQuotaUser(final String quotaUser) {
                    return (BatchDeleteUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public BatchDeleteUserPolicy setUploadType(final String uploadType) {
                    return (BatchDeleteUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public BatchDeleteUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (BatchDeleteUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public BatchDeleteUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public BatchDeleteUserPolicy set(final String s, final Object o) {
                    return (BatchDeleteUserPolicy)super.set(s, o);
                }
            }
            
            public class BatchUpdateUserPolicy extends ChromeDeviceManagementRequest<BatchUpdateUserPolicyResponse>
            {
                private static final String REST_PATH = "v1/{+name}/users:batchUpdateUserPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected BatchUpdateUserPolicy(final String s, final BatchUpdateUserPolicyRequest batchUpdateUserPolicyRequest) {
                    super(Users.this.this$1.this$0, "POST", "v1/{+name}/users:batchUpdateUserPolicy", batchUpdateUserPolicyRequest, BatchUpdateUserPolicyResponse.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                }
                
                @Override
                public BatchUpdateUserPolicy set$Xgafv(final String s) {
                    return (BatchUpdateUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public BatchUpdateUserPolicy setAccessToken(final String accessToken) {
                    return (BatchUpdateUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public BatchUpdateUserPolicy setAlt(final String alt) {
                    return (BatchUpdateUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public BatchUpdateUserPolicy setCallback(final String callback) {
                    return (BatchUpdateUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public BatchUpdateUserPolicy setFields(final String fields) {
                    return (BatchUpdateUserPolicy)super.setFields(fields);
                }
                
                @Override
                public BatchUpdateUserPolicy setKey(final String key) {
                    return (BatchUpdateUserPolicy)super.setKey(key);
                }
                
                @Override
                public BatchUpdateUserPolicy setOauthToken(final String oauthToken) {
                    return (BatchUpdateUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public BatchUpdateUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (BatchUpdateUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public BatchUpdateUserPolicy setQuotaUser(final String quotaUser) {
                    return (BatchUpdateUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public BatchUpdateUserPolicy setUploadType(final String uploadType) {
                    return (BatchUpdateUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public BatchUpdateUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (BatchUpdateUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public BatchUpdateUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public BatchUpdateUserPolicy set(final String s, final Object o) {
                    return (BatchUpdateUserPolicy)super.set(s, o);
                }
            }
            
            public class DeleteUserPolicy extends ChromeDeviceManagementRequest<Empty>
            {
                private static final String REST_PATH = "v1/{+name}/userPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected DeleteUserPolicy(final String s) {
                    super(Users.this.this$1.this$0, "DELETE", "v1/{+name}/userPolicy", null, Empty.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/users/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                }
                
                @Override
                public DeleteUserPolicy set$Xgafv(final String s) {
                    return (DeleteUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public DeleteUserPolicy setAccessToken(final String accessToken) {
                    return (DeleteUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public DeleteUserPolicy setAlt(final String alt) {
                    return (DeleteUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public DeleteUserPolicy setCallback(final String callback) {
                    return (DeleteUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public DeleteUserPolicy setFields(final String fields) {
                    return (DeleteUserPolicy)super.setFields(fields);
                }
                
                @Override
                public DeleteUserPolicy setKey(final String key) {
                    return (DeleteUserPolicy)super.setKey(key);
                }
                
                @Override
                public DeleteUserPolicy setOauthToken(final String oauthToken) {
                    return (DeleteUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public DeleteUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (DeleteUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public DeleteUserPolicy setQuotaUser(final String quotaUser) {
                    return (DeleteUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public DeleteUserPolicy setUploadType(final String uploadType) {
                    return (DeleteUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public DeleteUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (DeleteUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public DeleteUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public DeleteUserPolicy set(final String s, final Object o) {
                    return (DeleteUserPolicy)super.set(s, o);
                }
            }
            
            public class GetCompiledUserPolicy extends ChromeDeviceManagementRequest<UserPolicy>
            {
                private static final String REST_PATH = "v1/{+name}/compiledUserPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected GetCompiledUserPolicy(final String s) {
                    super(Users.this.this$1.this$0, "GET", "v1/{+name}/compiledUserPolicy", null, UserPolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/users/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public GetCompiledUserPolicy set$Xgafv(final String s) {
                    return (GetCompiledUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public GetCompiledUserPolicy setAccessToken(final String accessToken) {
                    return (GetCompiledUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public GetCompiledUserPolicy setAlt(final String alt) {
                    return (GetCompiledUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public GetCompiledUserPolicy setCallback(final String callback) {
                    return (GetCompiledUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public GetCompiledUserPolicy setFields(final String fields) {
                    return (GetCompiledUserPolicy)super.setFields(fields);
                }
                
                @Override
                public GetCompiledUserPolicy setKey(final String key) {
                    return (GetCompiledUserPolicy)super.setKey(key);
                }
                
                @Override
                public GetCompiledUserPolicy setOauthToken(final String oauthToken) {
                    return (GetCompiledUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public GetCompiledUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (GetCompiledUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public GetCompiledUserPolicy setQuotaUser(final String quotaUser) {
                    return (GetCompiledUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public GetCompiledUserPolicy setUploadType(final String uploadType) {
                    return (GetCompiledUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public GetCompiledUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (GetCompiledUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public GetCompiledUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public GetCompiledUserPolicy set(final String s, final Object o) {
                    return (GetCompiledUserPolicy)super.set(s, o);
                }
            }
            
            public class GetUserPolicy extends ChromeDeviceManagementRequest<UserPolicy>
            {
                private static final String REST_PATH = "v1/{+name}/userPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                
                protected GetUserPolicy(final String s) {
                    super(Users.this.this$1.this$0, "GET", "v1/{+name}/userPolicy", null, UserPolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/users/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                }
                
                public HttpResponse executeUsingHead() throws IOException {
                    return super.executeUsingHead();
                }
                
                public HttpRequest buildHttpRequestUsingHead() throws IOException {
                    return super.buildHttpRequestUsingHead();
                }
                
                @Override
                public GetUserPolicy set$Xgafv(final String s) {
                    return (GetUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public GetUserPolicy setAccessToken(final String accessToken) {
                    return (GetUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public GetUserPolicy setAlt(final String alt) {
                    return (GetUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public GetUserPolicy setCallback(final String callback) {
                    return (GetUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public GetUserPolicy setFields(final String fields) {
                    return (GetUserPolicy)super.setFields(fields);
                }
                
                @Override
                public GetUserPolicy setKey(final String key) {
                    return (GetUserPolicy)super.setKey(key);
                }
                
                @Override
                public GetUserPolicy setOauthToken(final String oauthToken) {
                    return (GetUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public GetUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (GetUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public GetUserPolicy setQuotaUser(final String quotaUser) {
                    return (GetUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public GetUserPolicy setUploadType(final String uploadType) {
                    return (GetUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public GetUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (GetUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public GetUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                @Override
                public GetUserPolicy set(final String s, final Object o) {
                    return (GetUserPolicy)super.set(s, o);
                }
            }
            
            public class UpdateUserPolicy extends ChromeDeviceManagementRequest<UserPolicy>
            {
                private static final String REST_PATH = "v1/{+name}/userPolicy";
                private final Pattern NAME_PATTERN;
                @Key
                private String name;
                @Key
                private String updateMask;
                
                protected UpdateUserPolicy(final String s, final UserPolicy userPolicy) {
                    super(Users.this.this$1.this$0, "PATCH", "v1/{+name}/userPolicy", userPolicy, UserPolicy.class);
                    this.NAME_PATTERN = Pattern.compile("^enterprises/[^/]+/users/[^/]+$");
                    this.name = (String)Preconditions.checkNotNull((Object)s, (Object)"Required parameter name must be specified.");
                    if (!Users.this.this$1.this$0.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(s).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                }
                
                @Override
                public UpdateUserPolicy set$Xgafv(final String s) {
                    return (UpdateUserPolicy)super.set$Xgafv(s);
                }
                
                @Override
                public UpdateUserPolicy setAccessToken(final String accessToken) {
                    return (UpdateUserPolicy)super.setAccessToken(accessToken);
                }
                
                @Override
                public UpdateUserPolicy setAlt(final String alt) {
                    return (UpdateUserPolicy)super.setAlt(alt);
                }
                
                @Override
                public UpdateUserPolicy setCallback(final String callback) {
                    return (UpdateUserPolicy)super.setCallback(callback);
                }
                
                @Override
                public UpdateUserPolicy setFields(final String fields) {
                    return (UpdateUserPolicy)super.setFields(fields);
                }
                
                @Override
                public UpdateUserPolicy setKey(final String key) {
                    return (UpdateUserPolicy)super.setKey(key);
                }
                
                @Override
                public UpdateUserPolicy setOauthToken(final String oauthToken) {
                    return (UpdateUserPolicy)super.setOauthToken(oauthToken);
                }
                
                @Override
                public UpdateUserPolicy setPrettyPrint(final Boolean prettyPrint) {
                    return (UpdateUserPolicy)super.setPrettyPrint(prettyPrint);
                }
                
                @Override
                public UpdateUserPolicy setQuotaUser(final String quotaUser) {
                    return (UpdateUserPolicy)super.setQuotaUser(quotaUser);
                }
                
                @Override
                public UpdateUserPolicy setUploadType(final String uploadType) {
                    return (UpdateUserPolicy)super.setUploadType(uploadType);
                }
                
                @Override
                public UpdateUserPolicy setUploadProtocol(final String uploadProtocol) {
                    return (UpdateUserPolicy)super.setUploadProtocol(uploadProtocol);
                }
                
                public String getName() {
                    return this.name;
                }
                
                public UpdateUserPolicy setName(final String name) {
                    if (!ChromeDeviceManagement.this.getSuppressPatternChecks()) {
                        Preconditions.checkArgument(this.NAME_PATTERN.matcher(name).matches(), (Object)"Parameter name must conform to the pattern ^enterprises/[^/]+/users/[^/]+$");
                    }
                    this.name = name;
                    return this;
                }
                
                public String getUpdateMask() {
                    return this.updateMask;
                }
                
                public UpdateUserPolicy setUpdateMask(final String updateMask) {
                    this.updateMask = updateMask;
                    return this;
                }
                
                @Override
                public UpdateUserPolicy set(final String s, final Object o) {
                    return (UpdateUserPolicy)super.set(s, o);
                }
            }
        }
    }
    
    public static final class Builder extends AbstractGoogleJsonClient.Builder
    {
        public Builder(final HttpTransport httpTransport, final JsonFactory jsonFactory, final HttpRequestInitializer httpRequestInitializer) {
            super(httpTransport, jsonFactory, "https://chromedevicemanagement.googleapis.com/", "", httpRequestInitializer, false);
        }
        
        public ChromeDeviceManagement build() {
            return new ChromeDeviceManagement(this);
        }
        
        public Builder setRootUrl(final String rootUrl) {
            return (Builder)super.setRootUrl(rootUrl);
        }
        
        public Builder setServicePath(final String servicePath) {
            return (Builder)super.setServicePath(servicePath);
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
        
        public Builder setChromeDeviceManagementRequestInitializer(final ChromeDeviceManagementRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer((GoogleClientRequestInitializer)googleClientRequestInitializer);
        }
        
        public Builder setGoogleClientRequestInitializer(final GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }
}
