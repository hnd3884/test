package com.me.mdm.mdmmigration.mobileiron;

import org.json.JSONArray;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import org.json.JSONObject;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class MobileironAPIRequestHandler extends MigrationAPIRequestHandler
{
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject returnJSON = new JSONObject();
        try {
            final String managedStatus = "Managed";
            final String deviceID = msgJson.getString("deviceID");
            final String serverURl = this.getAPIServerBaseURL();
            final String deviceAPIURL = serverURl + "api/v1/device/" + deviceID;
            final DMHttpRequest request = new DMHttpRequest();
            request.url = deviceAPIURL;
            request.method = "GET";
            request.headers = this.getBasicAuthorizationHeader(5);
            MobileironAPIRequestHandler.logger.log(Level.INFO, "Going to request device status API to Mobileiron for device:{0}", new Object[] { deviceID });
            final JSONObject responseObject = this.executeHTTPRequest(request);
            final int responseStatus = responseObject.getInt("StatusCode");
            final JSONObject responseJSON = responseObject.getJSONObject("ResponseJson");
            if (responseStatus == 200 && responseJSON.has("result")) {
                final JSONObject resultJSON = responseJSON.optJSONObject("result");
                if (resultJSON != null && responseJSON.has("udid")) {
                    final String UDID = resultJSON.getString("udid");
                    returnJSON.put("udid", (Object)UDID);
                }
            }
            returnJSON.put("NewEnrollmentURL", (Object)this.newEnrollmentURL);
            returnJSON.put("Status", (Object)managedStatus);
        }
        catch (final Exception e) {
            MobileironAPIRequestHandler.logger.log(Level.SEVERE, "Exception in mobileiron device status", e);
        }
        return null;
    }
    
    @Override
    protected JSONObject handleUnmanageDeviceRequest(final JSONObject msgJson) {
        final JSONObject returnJSON = new JSONObject();
        try {
            final String deviceID = msgJson.getString("deviceID");
            final String serverURL = this.getAPIServerBaseURL();
            final String retireURL = serverURL + "api/v1/device/retire";
            final JSONArray deviceIds = new JSONArray();
            deviceIds.put((Object)deviceID);
            final JSONObject parameter = new JSONObject();
            parameter.put("ids", (Object)deviceIds);
            final DMHttpRequest request = new DMHttpRequest();
            request.url = retireURL;
            request.headers = this.getBasicAuthorizationHeader(5);
            request.parameters = parameter;
            request.method = "PUT";
            MobileironAPIRequestHandler.logger.log(Level.INFO, "Going to request device removal API to Mobileiron for device:{0}", new Object[] { deviceID });
            final JSONObject responseObject = this.executeHTTPRequest(request);
            final int responseStatus = responseObject.getInt("StatusCode");
            final JSONObject responseJson = responseObject.getJSONObject("ResponseJson");
            if (responseStatus == 200 && responseJson.has("result") && responseJson.getInt("result") == 1) {
                return this.handleManagementStatusCheckRequest(msgJson);
            }
            MobileironAPIRequestHandler.logger.log(Level.INFO, "Mobileiron responded with an unknown reason:{0}", new Object[] { responseObject });
            returnJSON.put("Error", (Object)"100");
            returnJSON.put("ErrorMsg", (Object)"Unknown Error");
        }
        catch (final Exception e) {
            MobileironAPIRequestHandler.logger.log(Level.SEVERE, "Exception in mobileiron device removal", e);
        }
        return returnJSON;
    }
    
    @Override
    public JSONArray configurationDetails() {
        return null;
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
    public void getAuthorization(final boolean newKey) throws Exception {
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
    protected JSONObject getNewServerAuthDetails() throws Exception {
        return this.getOtherAuthDetails(5);
    }
}
