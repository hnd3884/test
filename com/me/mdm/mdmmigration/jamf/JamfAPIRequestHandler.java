package com.me.mdm.mdmmigration.jamf;

import com.me.mdm.api.error.APIHTTPException;
import java.util.Base64;
import com.me.mdm.mdmmigration.MigrationConstants;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class JamfAPIRequestHandler extends MigrationAPIRequestHandler
{
    public Logger logger;
    
    public JamfAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    protected void initializeConfiguration(final JSONObject requestJson) throws Exception {
        if (requestJson.length() > 0) {
            this.logger.log(Level.SEVERE, "Initializing Jamf configuration");
            super.initializeConfiguration(requestJson);
            final String apiServerURL = this.getAPIServerBaseURL();
            this.authorizationURL = apiServerURL + " /uapi/auth/tokens";
            this.removeDeviceURL = apiServerURL + "/v1/device-enrollments/";
            this.fetchDeviceURL = apiServerURL + "/v1/mobile-devices/";
            this.fetchUsersURL = apiServerURL + "/device-apis/user/1.0/search/";
            this.fetchGroupsURL = apiServerURL + "/group-apis/group/1.0/groups/customer/";
        }
    }
    
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            final String managedStatus = "Managed";
            final String deviceID = msgJson.getString("deviceID");
            this.getAuthorization(true);
            DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.method = "GET";
            request.url = this.fetchDeviceURL;
            this.logger.log(Level.INFO, "Going to request device status API to Jamf for device:{0}", new Object[] { deviceID });
            JSONObject responseObject = this.executeHTTPRequest(request);
            final JSONArray responseBody = responseObject.getJSONArray("ResponseJson");
            this.logger.log(Level.INFO, "Response on request device status API", responseObject);
            final int statusCode = responseObject.getInt("StatusCode");
            if (statusCode == 401) {
                if (this.handleUnauthorisedResponse(msgJson, returnJson)) {
                    return returnJson;
                }
                this.handleManagementStatusCheckRequest(msgJson);
            }
            int i = 0;
            while (i < responseBody.length()) {
                final String udid = responseBody.getJSONObject(i).getString("udid");
                if (udid.equalsIgnoreCase(deviceID)) {
                    this.logger.log(Level.INFO, "Response on request device status API, device details", responseBody.getJSONObject(i));
                    final String id = responseBody.getJSONObject(i).getString("id");
                    request = new DMHttpRequest();
                    request.headers = this.headerObject;
                    request.method = "GET";
                    request.url = this.fetchDeviceURL + id + "/detail";
                    responseObject = this.executeHTTPRequest(request);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    this.logger.log(Level.INFO, "Device details for id{0} {1}", new Object[] { id, responseBodyObject });
                    if (!responseBodyObject.getBoolean("isManaged")) {
                        returnJson.put("NewEnrollmentURL", (Object)this.newEnrollmentURL);
                        returnJson.put("udid", (Object)udid);
                        returnJson.put("Status", (Object)"Unmanaged");
                        break;
                    }
                    returnJson.put("Status", (Object)managedStatus);
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while handleManagementStatusCheck ", e);
            try {
                returnJson.put("Error", (Object)"100");
                returnJson.put("ErrorMsg", (Object)"Unknown Error");
                returnJson.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Error while handleManagementStatusCheck ", e);
            }
        }
        return returnJson;
    }
    
    @Override
    protected JSONObject handleUnmanageDeviceRequest(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            final String deviceID = msgJson.getString("deviceID");
            this.getAuthorization(true);
            final JSONObject parameters = new JSONObject();
            parameters.put("deviceId", (Object)deviceID);
            DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.method = "POST";
            request.url = this.fetchDeviceURL;
            this.logger.log(Level.INFO, "Going to request device status API to Jamf for device:{0}", new Object[] { deviceID });
            JSONObject responseObject = this.executeHTTPRequest(request);
            this.logger.log(Level.INFO, "Device details response {0}", responseObject);
            final JSONArray responseBody = responseObject.getJSONArray("ResponseJson");
            int statusCode = responseObject.getInt("StatusCode");
            if (statusCode == 401) {
                if (this.handleUnauthorisedResponse(msgJson, returnJson)) {
                    return returnJson;
                }
                this.handleManagementStatusCheckRequest(msgJson);
            }
            for (int i = 0; i < responseBody.length(); ++i) {
                final String udid = responseBody.getJSONObject(i).getString("udid");
                if (udid.equalsIgnoreCase(deviceID)) {
                    final String id = responseBody.getJSONObject(i).getString("id");
                    request = new DMHttpRequest();
                    request.url = this.removeDeviceURL + id;
                    request.method = "DELETE";
                    request.headers = this.headerObject;
                    this.logger.log(Level.INFO, "Going to request for device delete enrollment API to Jamf for device:{0}", new Object[] { deviceID });
                    responseObject = this.executeHTTPRequest(request);
                    this.logger.log(Level.INFO, "Device unenroll API sent");
                    statusCode = responseObject.getInt("StatusCode");
                    if (statusCode == 204) {
                        return this.handleManagementStatusCheckRequest(msgJson);
                    }
                    final String error = "Unknown Error";
                    returnJson.put("Error", (Object)"100");
                    returnJson.put("ErrorMsg", (Object)error);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removing device", e);
        }
        return returnJson;
    }
    
    @Override
    public JSONArray configurationDetails() {
        final JSONArray requirements = MigrationConstants.JamfRequirements.requirementList;
        final JSONArray response = new JSONArray();
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            object.put("display_name", (Object)requirements.get(i).toString());
            if (requirements.getString(i).equalsIgnoreCase("Root URL")) {
                object.put("api_key", (Object)"Server_URL");
            }
            else {
                object.put("api_key", (Object)requirements.get(i).toString());
            }
            object.put("type", (Object)"String");
            response.put((Object)object);
        }
        return response;
    }
    
    @Override
    public JSONArray fetchOrganizationDetails(final Long config_id, final int service_id, final Long customer_id) throws Exception {
        return null;
    }
    
    @Override
    public JSONObject getAuthenticationType() {
        final JSONObject auth = new JSONObject();
        auth.put("authentication_type", (Object)"basic");
        return auth;
    }
    
    @Override
    public JSONObject fetchAllDevices(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        return null;
    }
    
    @Override
    public JSONObject fetchAllUsers(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        return null;
    }
    
    @Override
    public JSONObject fetchAllGroups(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        return null;
    }
    
    @Override
    public JSONObject fetchAllProfiles(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        return null;
    }
    
    @Override
    public JSONObject fetchAllApps(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        return null;
    }
    
    @Override
    public JSONObject fetchMigrationPrerequisite(final Long config_id) {
        return null;
    }
    
    @Override
    public void getAuthorization(final boolean newKey) throws Exception {
        final JSONObject basicDigestInfo = this.getAuthDetailsForAuthType(15, null).getJSONObject("BasicDigestAuthInfo");
        final String username = basicDigestInfo.getString("USERNAME");
        final String password = basicDigestInfo.getString("PASSWORD");
        final String credentials = username + ":" + password;
        final JSONObject authorization = new JSONObject();
        authorization.put("Authorization", (Object)Base64.getEncoder().encodeToString(credentials.getBytes("utf-8")));
        try {
            final DMHttpRequest request = new DMHttpRequest();
            request.url = this.authorizationURL;
            request.headers = authorization;
            request.method = "POST";
            final JSONObject response = this.executeHTTPRequest(request);
            final JSONObject responseBody = response.getJSONObject("ResponseJson");
            this.headerObject.put("Authorization", (Object)responseBody.getString("token"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while getting auth token");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        return this.getOtherAuthDetails(15);
    }
}
