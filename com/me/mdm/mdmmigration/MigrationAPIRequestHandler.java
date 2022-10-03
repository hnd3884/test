package com.me.mdm.mdmmigration;

import java.util.Base64;
import org.json.JSONException;
import java.net.URL;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.logging.Level;
import com.me.mdm.mdmmigration.desktopcentral.DesktopCentralAPIRequestHandler;
import com.me.mdm.mdmmigration.jamf.JamfAPIRequestHandler;
import com.me.mdm.mdmmigration.meonpremise.MEOnPremiseAPIRequestHandler;
import com.me.mdm.mdmmigration.mobileiron.MobileironAPIRequestHandler;
import com.me.mdm.mdmmigration.airwatch.AirwatchAPIRequestHandler;
import com.me.mdm.mdmmigration.meraki.MerakiAPIRequestHandler;
import com.me.mdm.mdmmigration.ibmmaas.MaasAPIRequestHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.mecloud.MECloudAPIRequestHandler;
import org.json.JSONObject;

public abstract class MigrationAPIRequestHandler extends APIRequestHandler
{
    protected JSONObject apiDetails;
    protected String authorizationURL;
    protected String checkStatusURl;
    protected String removeDeviceURL;
    protected String newEnrollmentURL;
    protected String fetchDeviceURL;
    protected String fetchUsersURL;
    protected String fetchGroupsURL;
    protected String fetchProfilesURL;
    protected String fetchAppsURL;
    protected JSONObject headerObject;
    protected boolean isAuthorizationFailed;
    public static MECloudAPIRequestHandler meCloudAPIRequestHandler;
    protected static Logger logger;
    
    public MigrationAPIRequestHandler() {
        this.apiDetails = null;
        this.authorizationURL = null;
        this.checkStatusURl = null;
        this.removeDeviceURL = null;
        this.newEnrollmentURL = null;
        this.fetchDeviceURL = null;
        this.fetchUsersURL = null;
        this.fetchGroupsURL = null;
        this.fetchProfilesURL = null;
        this.fetchAppsURL = null;
        this.headerObject = new JSONObject();
        this.isAuthorizationFailed = false;
    }
    
    protected abstract JSONObject handleManagementStatusCheckRequest(final JSONObject p0);
    
    protected abstract JSONObject handleUnmanageDeviceRequest(final JSONObject p0);
    
    public abstract JSONArray configurationDetails();
    
    public abstract JSONArray fetchOrganizationDetails(final Long p0, final int p1, final Long p2) throws Exception;
    
    public abstract JSONObject getAuthenticationType();
    
    public abstract JSONObject fetchAllDevices(final Long p0, final int p1, final Long p2, final Long p3);
    
    public abstract JSONObject fetchAllUsers(final Long p0, final int p1, final Long p2, final Long p3);
    
    public abstract JSONObject fetchAllGroups(final Long p0, final int p1, final Long p2, final Long p3);
    
    public abstract JSONObject fetchAllProfiles(final Long p0, final int p1, final Long p2, final Long p3);
    
    public abstract JSONObject fetchAllApps(final Long p0, final int p1, final Long p2, final Long p3);
    
    public abstract JSONObject fetchMigrationPrerequisite(final Long p0);
    
    public abstract void getAuthorization(final boolean p0) throws Exception;
    
    protected void initializeConfiguration(final JSONObject requestJson) throws Exception {
        if (requestJson.length() > 0) {
            final Long configID = JSONUtil.optLongForUVH(requestJson, "CONFIG_ID", Long.valueOf(-1L));
            final Long customer_id = CustomerInfoUtil.getInstance().getCustomerId();
            this.apiDetails = this.getServiceConfigDetails(configID, customer_id);
            final JSONObject serviceConfiguration = this.apiDetails.getJSONObject("APIServiceConfiguration");
            this.newEnrollmentURL = serviceConfiguration.getString("NEW_ENROLLMENT_URL".toLowerCase());
        }
    }
    
    public static MigrationAPIRequestHandler getInstance(final int service_id) {
        switch (service_id) {
            case 4: {
                return new MaasAPIRequestHandler();
            }
            case 6: {
                return new MerakiAPIRequestHandler();
            }
            case 3: {
                return new AirwatchAPIRequestHandler();
            }
            case 5: {
                return new MobileironAPIRequestHandler();
            }
            case 1:
            case 8: {
                return MigrationAPIRequestHandler.meCloudAPIRequestHandler = new MECloudAPIRequestHandler();
            }
            case 2: {
                return MigrationAPIRequestHandler.meCloudAPIRequestHandler = new MEOnPremiseAPIRequestHandler();
            }
            case 15: {
                return new JamfAPIRequestHandler();
            }
            case 7: {
                return MigrationAPIRequestHandler.meCloudAPIRequestHandler = new DesktopCentralAPIRequestHandler();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public JSONObject processRequest(final JSONObject msgJson) {
        JSONObject responseJson = new JSONObject();
        try {
            final String msgType = msgJson.get("msgType").toString();
            final JSONObject msgContentJSON = this.getMsgContent(msgJson);
            if (msgType.equals("QueryManagementStatus")) {
                this.initializeConfiguration(msgContentJSON);
                responseJson = this.handleManagementStatusCheckRequest(msgContentJSON);
            }
            else if (msgType.equals("UnmanageDevice")) {
                this.initializeConfiguration(msgContentJSON);
                responseJson = this.handleUnmanageDeviceRequest(msgContentJSON);
            }
            else if (msgType.equals("EnrolledStatus")) {
                this.initializeConfiguration(msgContentJSON);
                responseJson = this.handleNewServerEnrolledStatus(msgContentJSON);
            }
        }
        catch (final Exception e) {
            MigrationAPIRequestHandler.logger.log(Level.SEVERE, "Exception while APIRequestHandler processRequest() ", e);
        }
        return responseJson;
    }
    
    public JSONObject executeAPIRequest(final String method, final String url, final JSONObject parameters) {
        return this.executeAPIRequest(method, url, parameters, null);
    }
    
    public JSONObject executeAPIRequest(final String method, String url, final JSONObject parameters, final JSONObject body) {
        try {
            if (url.contains("csez")) {
                url = (url.contains("?") ? (url + "&service=mdm") : (url + "?service=mdm"));
            }
            final DMHttpRequest dmHttpRequest = new DMHttpRequest();
            this.getAuthorization(true);
            dmHttpRequest.headers = this.headerObject;
            dmHttpRequest.method = method;
            if (body != null) {
                dmHttpRequest.data = body.toString().getBytes("utf-8");
            }
            dmHttpRequest.url = url;
            if (parameters != null) {
                dmHttpRequest.parameters = parameters;
            }
            final JSONObject response = this.executeHTTPRequest(dmHttpRequest);
            return response;
        }
        catch (final Exception e) {
            MigrationAPIRequestHandler.logger.log(Level.SEVERE, "Error while preparing request object", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected String getAPIServerBaseURL() throws Exception {
        final JSONObject serviceConfiguration = this.apiDetails.getJSONObject("APIServiceConfiguration");
        String apiURL = serviceConfiguration.getString("SERVER_URL".toLowerCase());
        if (!apiURL.endsWith("/")) {
            apiURL = apiURL.concat("/");
        }
        return apiURL;
    }
    
    protected abstract JSONObject getNewServerAuthDetails() throws Exception;
    
    protected JSONObject handleNewServerEnrolledStatus(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            final String deviceUDID = msgJson.getString("deviceID");
            final Long configId = JSONUtil.optLongForUVH(msgJson, "CONFIG_ID", Long.valueOf(-1L));
            if (configId != -1L) {
                final JSONObject serviceConfigurationObject = this.apiDetails.getJSONObject("APIServiceConfiguration");
                final String serverURL = this.getMDMServiceURL(serviceConfigurationObject);
                MigrationAPIRequestHandler.logger.fine("Sucessfully got server URL" + serverURL);
                final JSONObject apiAuthDetails = this.getNewServerAuthDetails();
                MigrationAPIRequestHandler.logger.log(Level.FINE, "Successfully got NewServerAuthDetails{0}", apiAuthDetails);
                final String authorizationHeader = this.getMDMAuthorizationHeader(apiAuthDetails, false);
                MigrationAPIRequestHandler.logger.log(Level.FINE, "Successfully got MDMAuthorizationHeader{0}", authorizationHeader);
                final DMHttpRequest request = new DMHttpRequest();
                final JSONObject headerObject = new JSONObject();
                headerObject.put("Authorization", (Object)authorizationHeader);
                request.url = "https://" + serverURL + "/api/v1/mdm/devices/udid/" + deviceUDID;
                request.method = "GET";
                request.headers = headerObject;
                final JSONObject responseObject = this.executeHTTPRequest(request);
                MigrationAPIRequestHandler.logger.log(Level.FINE, "Fetched device status");
                final JSONObject responseJson = responseObject.getJSONObject("ResponseJson");
                if (responseJson.has("managed_status")) {
                    final Integer deviceStatus = responseJson.getInt("managed_status");
                    MigrationAPIRequestHandler.logger.log(Level.SEVERE, "Device Status:{0} UDID:{1}", new Object[] { deviceStatus, deviceUDID });
                    if (!deviceStatus.equals(-1) && (deviceStatus.equals(5) || deviceStatus.equals(2))) {
                        returnJson.put("Status", (Object)"Managed");
                        returnJson.put("state", 5);
                    }
                    else {
                        returnJson.put("Status", (Object)"Unmanaged");
                        returnJson.put("state", (Object)deviceStatus);
                    }
                }
                else {
                    returnJson.put("Error", (Object)"100");
                    returnJson.put("ErrorMsg", (Object)"Unknown Error");
                }
            }
        }
        catch (final Exception e) {
            MigrationAPIRequestHandler.logger.log(Level.SEVERE, "Exception in handling handle new server enrollment");
        }
        return returnJson;
    }
    
    protected String getMDMServiceURL(final JSONObject serviceConfiguration) throws Exception {
        final String enrollmentURL = serviceConfiguration.getString("NEW_ENROLLMENT_URL".toLowerCase());
        final URL url = new URL(enrollmentURL);
        MigrationAPIRequestHandler.logger.fine("getMDMServiceURL" + url.getAuthority());
        return url.getAuthority();
    }
    
    protected JSONObject getServiceConfigDetails(final Long configId, final Long customer_id) throws Exception {
        final JSONObject queryExistingJSON = new JSONObject();
        queryExistingJSON.put("CONFIG_ID", (Object)configId);
        queryExistingJSON.put("CUSTOMER_ID", (Object)customer_id);
        final JSONObject apiDetails = new APIServiceDataHandler().getAPIServiceConfigDetails(queryExistingJSON);
        return apiDetails;
    }
    
    protected JSONObject getAuthDetailsForAuthType(final int type, final Integer occurence) throws JSONException {
        final JSONArray apiJSONArray = this.apiDetails.getJSONArray("APIServiceAuthDetails");
        JSONObject apiJSONObject = null;
        for (int i = 0; i < apiJSONArray.length(); ++i) {
            final JSONObject authObject = apiJSONArray.getJSONObject(i);
            final JSONObject apiAuthInfo = authObject.getJSONObject("APIAuthInfo");
            final int authType = apiAuthInfo.getInt("AUTH_SERVICE_TYPE".toLowerCase());
            if (authType == type && (occurence == null || i == occurence)) {
                apiJSONObject = authObject;
                break;
            }
        }
        return apiJSONObject;
    }
    
    protected JSONObject getOtherAuthDetails(final int type) throws JSONException {
        final JSONArray apiJSONArray = this.apiDetails.getJSONArray("APIServiceAuthDetails");
        JSONObject apiJSONObject = null;
        for (int i = 0; i < apiJSONArray.length(); ++i) {
            final JSONObject authObject = apiJSONArray.getJSONObject(i);
            final JSONObject apiAuthInfo = authObject.getJSONObject("APIAuthInfo");
            final int authType = apiAuthInfo.getInt("AUTH_SERVICE_TYPE".toLowerCase());
            if (authType != type) {
                apiJSONObject = authObject;
            }
        }
        return apiJSONObject;
    }
    
    protected boolean checkThresholdForApiKey(final JSONObject customHeaderObject) throws JSONException {
        boolean getAuthKey = true;
        if (customHeaderObject.has("authKeyTime")) {
            final Long authKeyTime = Long.valueOf((String)customHeaderObject.get("authKeyTime"));
            final Long currentMilliSec = System.currentTimeMillis();
            final Long keyThreshold = 3480000L;
            if (currentMilliSec - authKeyTime < keyThreshold) {
                getAuthKey = false;
            }
        }
        return getAuthKey;
    }
    
    protected boolean handleUnauthorisedResponse(final JSONObject msgJson, final JSONObject returnJson) throws Exception {
        msgJson.put("getNewKey", true);
        if (msgJson.has("retryCount")) {
            final int retryCount = msgJson.getInt("retryCount");
            if (retryCount < 2) {
                returnJson.put("Error", (Object)"100");
                returnJson.put("ErrorMsg", (Object)"Unknown Error");
                return true;
            }
        }
        else {
            msgJson.put("retryCount", 1);
        }
        return false;
    }
    
    protected String getMDMAuthorizationHeader(final JSONObject authApiDetails, final boolean getNewKey) throws Exception {
        String authorizationHeader = null;
        final JSONObject apiAuthInfo = authApiDetails.getJSONObject("APIAuthInfo");
        final int authServiceType = apiAuthInfo.getInt("AUTH_SERVICE_TYPE".toLowerCase());
        switch (authServiceType) {
            case 1:
            case 8: {
                final JSONObject customHeaderObject = authApiDetails.getJSONObject("CustomHeadersAuthInfo");
                final boolean getAuthKey = this.checkThresholdForApiKey(customHeaderObject);
                if (!getAuthKey && !getNewKey) {
                    final JSONObject basicDigestObject = authApiDetails.getJSONObject("BasicDigestAuthInfo");
                    authorizationHeader = basicDigestObject.getString("AUTHORIZATION_HEADER".toLowerCase());
                    break;
                }
                final String refreshToken = customHeaderObject.getString("refresh_token");
                final String redirectURI = customHeaderObject.getString("redirect_uri");
                final String clientId = customHeaderObject.getString("client_id");
                final String clientSecret = customHeaderObject.getString("client_secret");
                String accountsUrl = this.getAPIServerBaseURL();
                if (accountsUrl.contains("csez")) {
                    accountsUrl = "https://accounts.csez.zohocorpin.com";
                }
                else if (accountsUrl.contains(".com")) {
                    accountsUrl = "https://accounts.zoho.com";
                }
                else if (accountsUrl.contains(".in")) {
                    accountsUrl = "https://accounts.zoho.in";
                }
                else if (accountsUrl.contains(".cn")) {
                    accountsUrl = "https://accounts.zoho.cn";
                }
                else if (accountsUrl.contains(".eu")) {
                    accountsUrl = "https://accounts.zoho.eu";
                }
                else if (accountsUrl.contains(".au")) {
                    accountsUrl = "https://accounts.zoho.au";
                }
                final String refreshTokenURL = accountsUrl + "/oauth/v2/token";
                final JSONObject params = new JSONObject();
                params.put("refresh_token", (Object)refreshToken);
                params.put("redirect_uri", (Object)redirectURI);
                params.put("client_id", (Object)clientId);
                params.put("client_secret", (Object)clientSecret);
                params.put("grant_type", (Object)"refresh_token");
                final DMHttpRequest request = new DMHttpRequest();
                request.url = refreshTokenURL;
                request.parameters = params;
                request.method = "POST";
                final JSONObject responseObject = this.executeHTTPRequest(request);
                final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                if (responseBodyObject.has("access_token")) {
                    final String accessToken = responseBodyObject.getString("access_token");
                    final String tokenType = responseBodyObject.getString("token_type");
                    authorizationHeader = tokenType + " " + accessToken;
                    break;
                }
                MigrationAPIRequestHandler.logger.log(Level.INFO, "Error in getting authtoken");
                new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                throw new Exception("AuthToken Problem");
            }
            case 2:
            case 7: {
                final JSONObject basicDigestObject2 = authApiDetails.getJSONObject("BasicDigestAuthInfo");
                authorizationHeader = basicDigestObject2.getString("AUTHORIZATION_HEADER".toLowerCase());
                break;
            }
        }
        return authorizationHeader;
    }
    
    public JSONObject getBasicAuthorizationHeader(final int authType) throws Exception {
        final JSONObject authObject = new JSONObject();
        final JSONObject basicDigestInfo = this.getAuthDetailsForAuthType(authType, null).getJSONObject("BasicDigestAuthInfo");
        final String userName = basicDigestInfo.getString("USERNAME".toLowerCase());
        final String password = basicDigestInfo.getString("PASSWORD".toLowerCase());
        final String encodedString = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes("UTF-8"));
        authObject.put("Authorization", (Object)("Basic " + encodedString));
        return authObject;
    }
    
    public void validateResponse(final JSONObject response) {
        final int httpStatus = response.getInt("StatusCode");
        if (httpStatus == 200) {
            return;
        }
        switch (httpStatus) {
            case 400:
            case 401: {
                throw new APIHTTPException("MIG001", new Object[0]);
            }
            case 403:
            case 404: {
                throw new APIHTTPException("MIG002", new Object[0]);
            }
            case 405: {
                throw new APIHTTPException("MIG015", new Object[0]);
            }
            case 412: {
                throw new APIHTTPException("MIG004", new Object[0]);
            }
            case 500: {
                throw new APIHTTPException("MIG003", new Object[0]);
            }
            case 503: {
                throw new APIHTTPException("MIG005", new Object[0]);
            }
            case 502:
            case 504: {
                throw new APIHTTPException("MIG006", new Object[0]);
            }
            default: {
                throw new APIHTTPException("MIG016", new Object[0]);
            }
        }
    }
    
    static {
        MigrationAPIRequestHandler.meCloudAPIRequestHandler = null;
        MigrationAPIRequestHandler.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
