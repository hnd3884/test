package com.me.mdm.mdmmigration.mecloud;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.MalformedURLException;
import com.me.mdm.files.FileFacade;
import org.apache.tika.Tika;
import java.util.Collection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.Map;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Properties;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.AppFacade;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.mdmmigration.MigrationSummary;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.me.mdm.mdmmigration.MigrationConstants;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class MECloudAPIRequestHandler extends MigrationAPIRequestHandler
{
    private static Logger logger;
    String fetchUserDeviceAssociation;
    String apiServerURL;
    String profilesUrl;
    String appsUrl;
    String groupsUrl;
    String deviceUrl;
    String tempMigrationDir;
    String authToken;
    long authTokenTime;
    private final long expiration = 3000000L;
    
    public MECloudAPIRequestHandler() {
        this.fetchUserDeviceAssociation = null;
        this.apiServerURL = null;
        this.tempMigrationDir = null;
        this.authToken = null;
        this.authTokenTime = 0L;
    }
    
    @Override
    protected void initializeConfiguration(final JSONObject requestJson) throws Exception {
        if (requestJson.length() > 0) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Initializing ME MDM configuration");
            super.initializeConfiguration(requestJson);
            this.apiServerURL = this.getAPIServerBaseURL();
            this.authorizationURL = this.apiServerURL + "/auth-apis/auth/1.0/authenticate/";
            final String string = this.apiServerURL + "api/v1/mdm/devices";
            this.fetchDeviceURL = string;
            this.deviceUrl = string;
            this.fetchUsersURL = this.apiServerURL + "api/v1/mdm/users";
            final String string2 = this.apiServerURL + "api/v1/mdm/groups";
            this.fetchGroupsURL = string2;
            this.groupsUrl = string2;
            final String string3 = this.apiServerURL + "api/v1/mdm/profiles";
            this.fetchProfilesURL = string3;
            this.profilesUrl = string3;
            final String string4 = this.apiServerURL + "api/v1/mdm/apps";
            this.fetchAppsURL = string4;
            this.appsUrl = string4;
            this.fetchUserDeviceAssociation = this.apiServerURL + "api/v1/mdm/users/devices";
            this.tempMigrationDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "api_temp_downloads" + File.separator + "temp_migration";
        }
    }
    
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            final String corpWipeApiURL = this.getAPIServerBaseURL() + "api/v1/mdm/devices/" + msgJson.get("deviceID").toString() + "/" + "enterprise/erase";
            final String deviceApiURL = this.getAPIServerBaseURL() + "api/v1/mdm/devices/" + msgJson.get("deviceID").toString();
            final JSONObject jsonHeaders = new JSONObject();
            final boolean getNewKey = msgJson.optBoolean("getNewKey");
            this.getAuthorization(getNewKey);
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.url = deviceApiURL;
            request.method = "GET";
            final JSONObject httpResponse = this.executeHTTPRequest(request);
            final JSONObject responseMsgJson = new JSONObject(httpResponse.get("ResponseJson").toString());
            final Integer responseStatusCode = Integer.parseInt(httpResponse.get("StatusCode").toString());
            String mgmtStatus = "Managed";
            if (responseStatusCode == 404) {
                try {
                    if (responseMsgJson.get("error_code").toString().equalsIgnoreCase("COM0008")) {
                        mgmtStatus = "Unmanaged";
                    }
                }
                catch (final JSONException ex) {}
            }
            else {
                try {
                    if (!responseMsgJson.get("managed_status").toString().equalsIgnoreCase("2")) {
                        mgmtStatus = "Unmanaged";
                    }
                    final String udid = responseMsgJson.getString("udid");
                    returnJson.put("udid", (Object)udid);
                }
                catch (final JSONException ex2) {}
            }
            returnJson.put("Status", (Object)mgmtStatus);
            returnJson.put("NewEnrollmentURL", (Object)this.getNewEnrollmentURL());
        }
        catch (final Exception e) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "MECloudAPIRequestHandler Error while handleManagementStatusCheck ", e);
            try {
                returnJson.put("Error", (Object)"100");
                returnJson.put("ErrorMsg", (Object)"Unknown Error");
                returnJson.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex3) {}
        }
        return returnJson;
    }
    
    @Override
    protected JSONObject handleUnmanageDeviceRequest(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            final String apiURL = this.getAPIServerBaseURL() + "api/v1/mdm/devices/" + msgJson.get("deviceID").toString() + "/" + "enterprise/erase";
            final JSONObject jsonHeaders = new JSONObject();
            final boolean getNewKey = msgJson.optBoolean("getNewKey");
            this.getAuthorization(getNewKey);
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.url = apiURL;
            request.method = "POST";
            request.data = new JSONObject().toString().getBytes();
            final JSONObject httpResponse = this.executeHTTPRequest(request);
            final JSONObject responseMsgJson = new JSONObject(httpResponse.get("ResponseJson").toString());
            final Integer responseStatusCode = Integer.parseInt(httpResponse.get("StatusCode").toString());
            boolean success = false;
            if (String.valueOf(responseStatusCode).startsWith("20")) {
                success = true;
            }
            else if (responseStatusCode == 404) {
                try {
                    if (responseMsgJson.get("error_code").toString().equalsIgnoreCase("COM0008")) {
                        MECloudAPIRequestHandler.logger.log(Level.INFO, "MECloudAPIRequestHandler handleUnmanageDeviceRequest() : device already unmanaged");
                        success = true;
                    }
                }
                catch (final JSONException ex) {}
            }
            returnJson.put("NewEnrollmentURL", (Object)this.getNewEnrollmentURL());
            if (success) {
                return this.handleManagementStatusCheckRequest(msgJson);
            }
            returnJson.put("Error", (Object)"100");
            returnJson.put("ErrorMsg", (Object)"Server has not confirmed if unmanagement is scheduled successfully. Try again?");
        }
        catch (final Exception e) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "MECloudAPIRequestHandler Error while handleUnmanageDeviceRequest ", e);
            try {
                returnJson.put("Error", (Object)"100");
                returnJson.put("ErrorMsg", (Object)"Unknown Error");
                returnJson.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex2) {}
        }
        return returnJson;
    }
    
    @Override
    public JSONArray configurationDetails() {
        final JSONArray response = new JSONArray();
        final JSONArray requirements = MigrationConstants.MECloudRequirements.requirements;
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            object.put("display_name", (Object)requirements.get(i).toString());
            if (requirements.getString(i).equalsIgnoreCase("Refresh Token")) {
                object.put("api_key", (Object)"refresh_token");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Client ID")) {
                object.put("api_key", (Object)"client_id");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Client Secret")) {
                object.put("api_key", (Object)"client_secret");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Grant Type")) {
                object.put("api_key", (Object)"grant_type");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Redirect URI")) {
                object.put("api_key", (Object)"redirect_uri");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Account URL")) {
                object.put("api_key", (Object)"Server_URL");
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
    
    public JSONObject prepareDeviceDBInsertObject(final JSONObject device) {
        final JSONObject deviceDetailsJSON = new JSONObject();
        final int platform = device.getJSONObject("os").getInt("platform_type");
        if (platform == 2) {
            deviceDetailsJSON.put("OS", (Object)"Android");
        }
        else if (platform == 1) {
            deviceDetailsJSON.put("OS", (Object)"iOS");
        }
        else if (platform == 3) {
            deviceDetailsJSON.put("OS", (Object)"Windows");
        }
        deviceDetailsJSON.put("MANUFACTURER", device.opt("manufacturer"));
        deviceDetailsJSON.put("UDID", device.opt("udid"));
        deviceDetailsJSON.put("MODEL", device.opt("model_name"));
        deviceDetailsJSON.put("DEVICE_NAME", device.opt("device_name"));
        deviceDetailsJSON.put("MIGRATION_SERIAL_ID", device.opt("serial_number"));
        deviceDetailsJSON.put("MIGRATION_SERVER_DEVICE_ID", device.opt("device_id"));
        deviceDetailsJSON.put("IMEI", device.opt("imei"));
        deviceDetailsJSON.put("EAS_ID", device.opt("eas_device_identifier"));
        return deviceDetailsJSON;
    }
    
    @Override
    public JSONObject getAuthenticationType() {
        final JSONObject auth = new JSONObject();
        auth.put("authentication_type", (Object)"basic");
        return auth;
    }
    
    @Override
    public JSONObject fetchAllDevices(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final JSONObject response = new JSONObject();
        final JSONArray fetchedDevices = new JSONArray();
        final JSONObject configJSON = new JSONObject();
        JSONObject deviceDetailsJSON = new JSONObject();
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        try {
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, null);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final int count = responseBodyObject.getJSONObject("metadata").getInt("total_record_count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_COUNT", (Object)count);
            if (count == 0) {
                row.set("DEVICES_STATUS", (Object)3);
            }
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            final JSONArray devices = responseBodyObject.getJSONArray("devices");
            if (responseBodyObject.has("paging")) {
                while (responseBodyObject.getJSONObject("paging").has("next")) {
                    responseObject = this.executeAPIRequest("GET", responseBodyObject.getJSONObject("paging").getString("next"), null);
                    responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray nextSetOfDevices = responseBodyObject.getJSONArray("devices");
                    for (int i = 0; i < nextSetOfDevices.length(); ++i) {
                        devices.put((Object)nextSetOfDevices.getJSONObject(i));
                    }
                    apiServiceDataHandler.sleepForThrottle();
                }
            }
            for (int j = 0; j < devices.length(); ++j) {
                try {
                    responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL + "/" + devices.getJSONObject(j).getString("device_id"), null);
                    final JSONObject device = responseObject.getJSONObject("ResponseJson");
                    deviceDetailsJSON = this.prepareDeviceDBInsertObject(device);
                    deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                    fetchedDevices.put((Object)deviceDetailsJSON);
                    deviceDetailsJSON.put("EASID", deviceDetailsJSON.opt("EAS_ID"));
                    deviceDetailsJSON.put("SerialNumber", deviceDetailsJSON.opt("MIGRATION_SERIAL_ID"));
                    deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                    new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                    apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                    apiServiceDataHandler.sleepForThrottle();
                }
                catch (final Exception e) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching device details " + devices.getJSONObject(j), e);
                }
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2182, null, user_name, "mdm.migration.device.completed", migrationSummary.get("migrated_devices"), customer_id);
        }
        catch (final Exception e2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception on fetching devices", e2);
            if (!this.isAuthorizationFailed) {
                try {
                    final Row statusRow = new Row("MDMServerMigrationStatus");
                    statusRow.set("CONFIG_ID", (Object)config_id);
                    final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                    final Row row = dataObject.getRow("MDMServerMigrationStatus");
                    row.set("DEVICES_STATUS", (Object)4);
                    row.set("FETCH_DEVICES_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                    dataObject.updateRow(row);
                    DataAccess.update(dataObject);
                }
                catch (final Exception ex) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while updating migration status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching all devices", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        response.put("devices", (Object)fetchedDevices);
        return response;
    }
    
    public JSONObject prepareUserDBInsertObject(final JSONObject user) {
        final JSONObject userDetailsJSON = new JSONObject();
        if (user.has("user_name")) {
            userDetailsJSON.put("USER_NAME", user.get("user_name"));
        }
        if (user.has("user_id")) {
            userDetailsJSON.put("MIGRATION_SERVER_USER_ID", user.get("user_id"));
        }
        if (user.has("user_email")) {
            userDetailsJSON.put("EMAIL_ID", user.get("user_email"));
        }
        if (user.has("phone_number")) {
            userDetailsJSON.put("PHONE_NUMBER", user.get("phone_number"));
        }
        if (user.has("domain_name")) {
            userDetailsJSON.put("DOMAIN", user.get("domain_name"));
        }
        return userDetailsJSON;
    }
    
    public List fetchDevicesPerUser(final JSONObject user, final Long config_id, final Long customer_id) throws Exception {
        final JSONObject parameters = new JSONObject();
        final List<Row> associationDataObject = new ArrayList<Row>();
        parameters.put("email_id", (Object)user.get("user_email").toString());
        final String userID = new APIServiceDataHandler().getMigrationUserIDForUserName(user.getString("user_name"), config_id, customer_id);
        final JSONObject responseDeviceObject = this.executeAPIRequest("GET", this.fetchUserDeviceAssociation, parameters);
        final JSONObject responseDevice = responseDeviceObject.getJSONObject("ResponseJson");
        final JSONArray devices = responseDevice.getJSONArray("device_ids");
        for (int j = 0; j < devices.length(); ++j) {
            final String device = devices.getString(j);
            final Long deviceID = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device, config_id, customer_id);
            if (deviceID != null) {
                final Row row = new Row("MigrationAssociation");
                row.set("CONFIG_ID", (Object)config_id);
                row.set("DEVICE_ID", (Object)deviceID);
                row.set("USER_ID", (Object)userID);
                associationDataObject.add(row);
            }
        }
        return associationDataObject;
    }
    
    @Override
    public JSONObject fetchAllUsers(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        try {
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("USERS_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final DataObject userDataObject = (DataObject)new WritableDataObject();
            final DataObject associationDataObject = (DataObject)new WritableDataObject();
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL, null);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final int count = responseBodyObject.getJSONObject("metadata").getInt("total_record_count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("USERS_COUNT", (Object)count);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            final JSONArray users = responseBodyObject.getJSONArray("users");
            if (responseBodyObject.has("paging")) {
                while (responseBodyObject.getJSONObject("paging").has("next")) {
                    responseObject = this.executeAPIRequest("GET", responseBodyObject.getJSONObject("paging").getString("next"), null);
                    responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray nextSetOfUsers = responseBodyObject.getJSONArray("users");
                    for (int i = 0; i < nextSetOfUsers.length(); ++i) {
                        users.put((Object)nextSetOfUsers.getJSONObject(i));
                    }
                    apiServiceDataHandler.sleepForThrottle();
                }
            }
            for (int j = 0; j < users.length(); ++j) {
                final JSONObject user = users.getJSONObject(j);
                final JSONObject userRowObject = this.prepareUserDBInsertObject(user);
                if (apiServiceDataHandler.isADSyncedUser(userRowObject.getString("EMAIL_ID"))) {
                    userRowObject.put("IS_AD_SYNCED_USER", true);
                    userRowObject.put("DOMAIN", (Object)apiServiceDataHandler.getADDomainName(userRowObject.getString("EMAIL_ID")));
                }
                userRowObject.put("CONFIG_ID", (Object)config_id);
                final Row userRow = apiServiceDataHandler.getRowForUser(userRowObject, customer_id);
                userDataObject.addRow(userRow);
                apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
            }
            for (int j = 0; j < users.length(); ++j) {
                final JSONObject user = users.getJSONObject(j);
                try {
                    final List<Row> deviceAssociation = this.fetchDevicesPerUser(user, config_id, customer_id);
                    for (final Row row2 : deviceAssociation) {
                        associationDataObject.addRow(row2);
                    }
                }
                catch (final Exception e) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, e, () -> "Error fetching devices for user for association " + jsonObject.toString());
                }
            }
            apiServiceDataHandler.addOrUpdateMigrationUserDeviceAssociation(associationDataObject, config_id, customer_id);
            final int platformType = new EnrollmentTemplateHandler().getPlatformForTemplate(50);
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation();
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            final Iterator iterator = userDeviceAssociation.keys();
            while (iterator.hasNext()) {
                final String userId = iterator.next().toString();
                for (int k = 0; k < userDeviceAssociation.getJSONArray(userId).length(); ++k) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId).getJSONObject(k);
                    toAssign.put("CustomerId", (Object)customer_id);
                    listToAssign.add(toAssign);
                }
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2184, null, user_name, "mdm.migration.users.completed", migrationSummary.get("migrated_users"), customer_id);
            AdminEnrollmentHandler.assignUser(listToAssign, user_id, 50, "DEVICE_ID", platformType);
        }
        catch (final Exception e2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching all users", e2);
            if (!this.isAuthorizationFailed) {
                try {
                    final Row statusRow = new Row("MDMServerMigrationStatus");
                    statusRow.set("CONFIG_ID", (Object)config_id);
                    final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                    final Row row = dataObject.getRow("MDMServerMigrationStatus");
                    row.set("USERS_STATUS", (Object)4);
                    row.set("FETCH_USERS_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                    dataObject.updateRow(row);
                    DataAccess.update(dataObject);
                }
                catch (final Exception ex) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while updating Migration Failure Status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching all users", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONObject prepareGroupDBInsertObject(final JSONObject group) {
        final JSONObject groupDetailsJSON = new JSONObject();
        if (group.has("name")) {
            groupDetailsJSON.put("GROUP_NAME", group.get("name"));
        }
        if (group.has("group_id")) {
            groupDetailsJSON.put("MIGRATION_SERVER_GROUP_ID", group.get("group_id"));
        }
        return groupDetailsJSON;
    }
    
    public DataObject fetchGroupsForDeviceId(final Long config_id, final Long customer_id) {
        final DataObject response = (DataObject)new WritableDataObject();
        try {
            final JSONObject parameters = new JSONObject();
            final JSONArray groupIds = new APIServiceDataHandler().getAllMigratedGroupIds(config_id, 3, customer_id);
            for (int i = 0; i < groupIds.length(); ++i) {
                try {
                    final String groupId = groupIds.getString(i);
                    parameters.put("include", (Object)"memberdetails");
                    final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchGroupsURL + "/" + groupId, parameters);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    if (responseBodyObject.has("associated")) {
                        final JSONArray members = responseBodyObject.getJSONObject("associated").getJSONArray("members");
                        final String group_name = new APIServiceDataHandler().getGroupNameForGroupId(groupId, config_id, customer_id);
                        if (group_name.equals("")) {
                            continue;
                        }
                        final Long group_id = new APIServiceDataHandler().getGroupIDForGroupName(group_name, customer_id);
                        for (int j = 0; j < members.length(); ++j) {
                            final JSONObject member = members.getJSONObject(j);
                            if (member.has("device_id")) {
                                final Long migrationServerDeviceId = new APIServiceDataHandler().getDeviceIDForServerDeviceID(member.getString("device_id"), config_id, customer_id);
                                if (migrationServerDeviceId != null && group_id != null) {
                                    final Row row = new Row("MigrationDeviceToGroup");
                                    row.set("CONFIG_ID", (Object)config_id);
                                    row.set("DEVICE_ID", (Object)migrationServerDeviceId);
                                    row.set("RESOURCE_GROUP_ID", (Object)group_id);
                                    response.addRow(row);
                                }
                            }
                            else if (member.has("user_id")) {
                                final Long migrationServerUserId = new APIServiceDataHandler().getUserIDForServerUserID(member.getString("user_id"), customer_id, config_id);
                                if (migrationServerUserId != null && group_id != null) {
                                    final Row row = new Row("MigrationUserToGroup");
                                    row.set("CONFIG_ID", (Object)config_id);
                                    row.set("USER_ID", (Object)migrationServerUserId);
                                    row.set("RESOURCE_GROUP_ID", (Object)group_id);
                                    try {
                                        response.addRow(row);
                                    }
                                    catch (final DataAccessException ex) {}
                                }
                            }
                        }
                    }
                    new APIServiceDataHandler().sleepForThrottle();
                }
                catch (final Exception e) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching associated users and devies for group " + groupIds.getString(i), e);
                }
            }
        }
        catch (final Exception e2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception while preparing device group association data object", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public JSONObject fetchAllGroups(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        try {
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("GROUPS_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchGroupsURL, null);
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final int count = responseBodyObject.getJSONObject("metadata").getInt("total_record_count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("GROUPS_COUNT", (Object)count);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            final long lastUpdatedTime = System.currentTimeMillis();
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            final JSONArray groups = responseBodyObject.getJSONArray("groups");
            for (int i = 0; i < groups.length(); ++i) {
                try {
                    final JSONObject group = groups.getJSONObject(i);
                    final JSONObject groupDBObject = this.prepareGroupDBInsertObject(group);
                    groupDBObject.put("CONFIG_ID", (Object)config_id);
                    apiServiceDataHandler.getRowForGroup(groupDBObject, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
                }
                catch (final Exception e) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while adding groups to migrationgroups table for " + groups.getJSONObject(i).toString(), e);
                }
            }
            for (int i = 0; i < groups.length(); ++i) {
                try {
                    final JSONObject group = groups.getJSONObject(i);
                    if (!group.has("domain")) {
                        final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
                        cgDetails.groupType = group.getInt("group_type");
                        cgDetails.platformType = 0;
                        cgDetails.groupCategory = 1;
                        cgDetails.customerId = customer_id;
                        cgDetails.domainName = "MDM";
                        cgDetails.groupPlatformType = 0;
                        cgDetails.groupName = group.getString("name");
                        cgDetails.lastUpdatedTime = lastUpdatedTime;
                        cgDetails.createdTime = lastUpdatedTime;
                        MDMGroupHandler.getInstance().addGroup(cgDetails);
                        apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group.getString("name"));
                    }
                }
                catch (final Exception e) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while adding group to resource table " + groups.getJSONObject(i).toString(), e);
                }
            }
            final DataObject associationDataObject = this.fetchGroupsForDeviceId(config_id, customer_id);
            Iterator iterator = associationDataObject.getRows("MigrationDeviceToGroup");
            final DataObject deviceToGroupDO = (DataObject)new WritableDataObject();
            while (iterator.hasNext()) {
                deviceToGroupDO.addRow((Row)iterator.next());
            }
            iterator = associationDataObject.getRows("MigrationUserToGroup");
            final DataObject userToGroupDO = (DataObject)new WritableDataObject();
            while (iterator.hasNext()) {
                userToGroupDO.addRow((Row)iterator.next());
            }
            apiServiceDataHandler.addOrUpdateMigrationDeviceToGroupAssociation(deviceToGroupDO, config_id, customer_id);
            apiServiceDataHandler.addOrUpdateMigrationUserGroupAssociation(userToGroupDO, config_id, customer_id);
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation();
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            iterator = userDeviceAssociation.keys();
            while (iterator.hasNext()) {
                final String userId = iterator.next().toString();
                for (int j = 0; j < userDeviceAssociation.getJSONArray(userId).length(); ++j) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId).getJSONObject(j);
                    toAssign.put("CustomerId", (Object)customer_id);
                    listToAssign.add(toAssign);
                }
            }
            for (int k = 0; k < listToAssign.size(); ++k) {
                final Long resourceId = new APIServiceDataHandler().getUserResourceIdForUserName(listToAssign.get(k).getString("UserName"), customer_id);
                final Long[] resourceIds = { resourceId };
                final List<Long> groupIds = new APIServiceDataHandler().getGroupIdsForUsername(listToAssign.get(k).getString("UserName"), customer_id);
                MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupIds, resourceIds, customer_id, null);
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2186, null, user_name, "mdm.migration.groups.completed", migrationSummary.get("migrated_groups"), customer_id);
        }
        catch (final Exception e2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception while fetching all groups", e2);
            if (!this.isAuthorizationFailed) {
                try {
                    final Row statusRow = new Row("MDMServerMigrationStatus");
                    statusRow.set("CONFIG_ID", (Object)config_id);
                    final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                    final Row row = dataObject.getRow("MDMServerMigrationStatus");
                    row.set("GROUPS_STATUS", (Object)4);
                    row.set("FETCH_GROUPS_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                    dataObject.updateRow(row);
                    DataAccess.update(dataObject);
                }
                catch (final Exception ex) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching all groups", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public JSONObject fetchMigrationPrerequisite(final Long config_id) {
        final String sourceMethod = "MECloudAPIRequestHandler::migrationPrerequisite";
        MECloudAPIRequestHandler.logger.log(Level.INFO, "{0} --> Going to fetch Prerequisite for migration..", sourceMethod);
        final JSONObject preReq = new JSONObject();
        boolean isAfwWithGsuit = Boolean.FALSE;
        try {
            final JSONObject configJSON = new JSONObject();
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            JSONObject responseObject = this.executeAPIRequest("GET", this.apiServerURL + "api/v1/mdm/active_directory", null);
            this.validateResponse(responseObject);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            preReq.put("active_directory_list", responseBodyObject.get("domain_list"));
            responseObject = this.executeAPIRequest("GET", this.apiServerURL + "api/v1/mdm/apps/account/pfw", null);
            this.validateResponse(responseObject);
            responseBodyObject = responseObject.getJSONObject("ResponseJson");
            if (responseBodyObject.has("enterprise_type")) {
                isAfwWithGsuit = responseBodyObject.getString("enterprise_type").equals("1");
            }
            responseObject = this.executeAPIRequest("GET", this.apiServerURL + "api/v1/mdm/apps/account/vpp", null);
            this.validateResponse(responseObject);
            responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final boolean isABMConfigured = responseBodyObject.has("account_user_name");
            preReq.put("is_gsuit_configured", isAfwWithGsuit);
            preReq.put("is_abm_configured", isABMConfigured);
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Prerequisite details for App migration..{0}", preReq.toString());
        }
        catch (final Exception ex) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while fetching migration Prerequisite", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return preReq;
    }
    
    @Override
    public JSONObject fetchAllApps(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final String sourceMethod = "MECloudAPIRequestHandler::fetchAllApps";
        MECloudAPIRequestHandler.logger.log(Level.INFO, "{0} --> Going to start Apps migration..", sourceMethod);
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        int status = 2;
        String statusMsg = "Migration completed successfully";
        final Map requestHeaderMap = new HashMap();
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        final AppMigrationHandler appMigrationHandler = new AppMigrationHandler(MECloudAPIRequestHandler.meCloudAPIRequestHandler);
        final AppFacade appFacade = new AppFacade();
        final JSONArray migratedApps = new JSONArray();
        final Map<String, String> businessApps = new HashMap<String, String>();
        final Map<String, Long> releaseLableInfo = new HashMap<String, Long>();
        final Map<String, String> appIdsMap = new HashMap<String, String>();
        try {
            apiServiceDataHandler.updateMigrationStatus(config_id, status, null, 2);
            status = 3;
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            requestHeaderMap.put("service_id", service_id);
            requestHeaderMap.put("user_id", user_id);
            requestHeaderMap.put("customer_id", customer_id);
            if (!ApiFactoryProvider.getFileAccessAPI().isDirectory(this.tempMigrationDir)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(this.tempMigrationDir);
            }
            final JSONArray allAppsArray = appMigrationHandler.fetchAppsFromSrc(Boolean.FALSE);
            int storeAppsCount = 0;
            for (int x = 0; x < allAppsArray.length(); ++x) {
                if (allAppsArray.getJSONObject(x).getInt("app_type") != 2) {
                    ++storeAppsCount;
                }
            }
            apiServiceDataHandler.addOrUpdateMigratedTotalApps(config_id, allAppsArray.length());
            apiServiceDataHandler.addOrUpdateMigratedApps(config_id, storeAppsCount);
            JSONObject responseObject = this.executeAPIRequest("GET", this.apiServerURL + "api/v1/mdm/apps/account/pfw", null);
            this.validateResponse(responseObject);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            boolean is_src_afw_configured = Boolean.FALSE;
            if (responseBodyObject.has("enterprise_type")) {
                is_src_afw_configured = responseBodyObject.getString("enterprise_type").equals("2");
            }
            final Boolean is_dest_afw_configured = GoogleForWorkSettings.isAFWSettingsConfigured(customer_id);
            if (is_src_afw_configured && !is_dest_afw_configured) {
                MECloudAPIRequestHandler.logger.log(Level.INFO, "Afw account configured without Gsuit");
                appMigrationHandler.afwAccountMigration(requestHeaderMap);
            }
            final Map<String, String> destBusinessApps = appMigrationHandler.fetchDestinationApps(requestHeaderMap, Boolean.TRUE);
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Going to start Enterprise and Store Apps Migration..");
            final List<String> alreadyMigratedApps = appMigrationHandler.getMigratedApps(config_id);
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Already Migrated Apps count...{0}", alreadyMigratedApps.size());
            final JSONArray businessAppsArray = appMigrationHandler.fetchAppsFromSrc(Boolean.TRUE);
            for (int i = 0; i < businessAppsArray.length(); ++i) {
                businessApps.put(businessAppsArray.getJSONObject(i).getString("bundle_identifier"), businessAppsArray.getJSONObject(i).getString("app_id"));
            }
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Total source apps count : {0}  and business apps count : {1}", new Object[] { allAppsArray.length(), businessApps.size() });
            final Map<String, String> destAllApps = appMigrationHandler.fetchDestinationApps(requestHeaderMap, Boolean.FALSE);
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Total destination apps count : {0}  and business apps count : {1}", new Object[] { destAllApps.size(), destBusinessApps.size() });
            for (int j = 0; j < allAppsArray.length(); ++j) {
                final JSONObject currentApp = allAppsArray.getJSONObject(j);
                final String app_id = currentApp.getString("app_id");
                final String bundle_identifier = currentApp.getString("bundle_identifier");
                if (!alreadyMigratedApps.contains(app_id) && !destAllApps.containsKey(bundle_identifier)) {
                    String new_app_id = "";
                    final JSONObject migratedApp = new JSONObject();
                    try {
                        responseObject = this.executeAPIRequest("GET", this.fetchAppsURL + "/" + app_id + "?include=migrationdetails", null);
                        this.validateResponse(responseObject);
                        final JSONObject appDetails = responseObject.getJSONObject("ResponseJson");
                        migratedApp.put("server_app_id", (Object)app_id);
                        migratedApp.put("bundle_identifier", (Object)appDetails.getString("bundle_identifier"));
                        migratedApp.put("profile_name", (Object)appDetails.getString("profile_name"));
                        migratedApp.put("platform_type", (Object)appDetails.getString("platform_type"));
                        final String stable_version_code = appDetails.getString("app_version_code");
                        if (businessApps.containsKey(currentApp.getString("bundle_identifier"))) {
                            MECloudAPIRequestHandler.logger.log(Level.INFO, "Migrating app ....{0}", bundle_identifier);
                            final JSONObject appDataToPost = appMigrationHandler.constructAppJSON(appDetails, null, Boolean.FALSE, requestHeaderMap, is_src_afw_configured);
                            JSONObject appPostJSON = appMigrationHandler.createRequestJsonForApp(appDataToPost, requestHeaderMap);
                            final JSONObject newAppDetails = appFacade.addApp(appPostJSON);
                            new_app_id = newAppDetails.getString("app_id");
                            requestHeaderMap.put("app_id", new_app_id);
                            final JSONArray appVersions = appDetails.getJSONArray("version_details");
                            for (int ver = 0; ver < appVersions.length(); ++ver) {
                                final JSONObject versionData = appVersions.getJSONObject(ver);
                                final String release_label_name = versionData.getJSONObject("release_label").getString("release_label_name");
                                if (!releaseLableInfo.containsKey(release_label_name)) {
                                    releaseLableInfo.put(release_label_name, appMigrationHandler.getReleaseLabelId(release_label_name, customer_id));
                                }
                                final Long label_id = Long.valueOf(releaseLableInfo.get(release_label_name).toString());
                                requestHeaderMap.put("label_id", label_id);
                                if (!stable_version_code.equals(versionData.getJSONObject("release_label").getString("version_code"))) {
                                    MECloudAPIRequestHandler.logger.log(Level.INFO, "{0} App Migrating Version {1}...", new Object[] { bundle_identifier, release_label_name });
                                    final JSONObject betaAppDetailsToPost = appMigrationHandler.constructAppJSON(appDetails, versionData, Boolean.TRUE, requestHeaderMap, is_src_afw_configured);
                                    betaAppDetailsToPost.put("force_update_as_beta", (Object)Boolean.FALSE);
                                    appPostJSON = appMigrationHandler.createRequestJsonForApp(betaAppDetailsToPost, requestHeaderMap);
                                    appFacade.updateApp(appPostJSON);
                                }
                                if (appDetails.has("app_config_template_id")) {
                                    appMigrationHandler.migrateAppConfigurations(versionData, requestHeaderMap);
                                }
                                appMigrationHandler.migrateAppPermissions(versionData, requestHeaderMap);
                            }
                        }
                        else {
                            MECloudAPIRequestHandler.logger.log(Level.INFO, "Migrating business app details ....{0}", bundle_identifier);
                            new_app_id = destBusinessApps.get(currentApp.getString("bundle_identifier"));
                            requestHeaderMap.put("app_id", new_app_id);
                            final JSONArray appVersions2 = appDetails.getJSONArray("version_details");
                            for (int ver2 = 0; ver2 < appVersions2.length(); ++ver2) {
                                final JSONObject versionData2 = appVersions2.getJSONObject(ver2);
                                final String release_label_name2 = versionData2.getJSONObject("release_label").getString("release_label_name");
                                if (!releaseLableInfo.containsKey(release_label_name2)) {
                                    releaseLableInfo.put(release_label_name2, appMigrationHandler.getReleaseLabelId(release_label_name2, customer_id));
                                }
                                final Long label_id2 = Long.valueOf(releaseLableInfo.get(release_label_name2).toString());
                                requestHeaderMap.put("label_id", label_id2);
                                if (appDetails.has("app_config_template_id")) {
                                    appMigrationHandler.migrateAppConfigurations(versionData2, requestHeaderMap);
                                }
                                appMigrationHandler.migrateAppPermissions(versionData2, requestHeaderMap);
                            }
                        }
                        final JSONObject requestJSON = appMigrationHandler.createRequestJsonForApp(null, requestHeaderMap);
                        final JSONObject new_app_details = appFacade.getApp(requestJSON);
                        migratedApp.put("app_id", (Object)new_app_details.getString("app_id"));
                        migratedApp.put("is_migrated", (Object)Boolean.TRUE);
                        final StringBuilder release_labels = new StringBuilder();
                        final JSONArray new_release_labels = new_app_details.getJSONArray("release_labels");
                        for (int k = 0; k < new_release_labels.length(); ++k) {
                            release_labels.append(new_release_labels.getJSONObject(k).getString("release_label_id")).append(",");
                        }
                        migratedApp.put("release_labels", (Object)release_labels.deleteCharAt(release_labels.length() - 1).toString());
                        migratedApp.put("remarks", (Object)"Migration completed successfully");
                        appIdsMap.put(app_id, new_app_id);
                    }
                    catch (final Exception ex) {
                        status = 4;
                        statusMsg = "Internal Server Error. Contact support with logs.";
                        MECloudAPIRequestHandler.logger.log(Level.SEVERE, " Exception occurred while migrating app ..{0}....{1}", new Object[] { bundle_identifier, ex });
                        migratedApp.put("is_migrated", (Object)Boolean.FALSE);
                        migratedApp.put("remarks", (Object)"Internal Server Error. Contact support with logs.");
                    }
                    migratedApps.put((Object)migratedApp);
                    if (migratedApp.getBoolean("is_migrated")) {
                        apiServiceDataHandler.addOrUpdateMigratedApps(config_id, -1);
                    }
                    apiServiceDataHandler.sleepForThrottle();
                }
            }
            responseObject = this.executeAPIRequest("GET", this.apiServerURL + "api/v1/mdm/apps/settings", null);
            this.validateResponse(responseObject);
            responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", customer_id);
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", responseBodyObject.getBoolean("is_silent_install"));
            ((Hashtable<String, Boolean>)properties).put("isNotify", Boolean.FALSE);
            AppsUtil.getInstance().addOrUpdateAppSettings(properties);
            if (appIdsMap.size() > 0) {
                appMigrationHandler.associateAppsToGroup(appIdsMap, config_id, requestHeaderMap, releaseLableInfo);
                appMigrationHandler.associateAppsToDevice(appIdsMap, config_id, releaseLableInfo);
            }
            int updatedApps = 0;
            if (migratedApps.length() > 0) {
                updatedApps = appMigrationHandler.addOrUpdateAppMigrationSummary(migratedApps, config_id);
            }
            apiServiceDataHandler.updateMigrationStatus(config_id, status, statusMsg, 2);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.tempMigrationDir);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2190, null, user_name, "mdm.migration.apps.completed", updatedApps, customer_id);
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "{0} Apps Migrated Successfully..", updatedApps);
            apiServiceDataHandler.updateMigrationStatus(config_id, 3, "Migration completed successfully", 2);
        }
        catch (final Exception ex2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception occurred while migrating apps {1}.....{2}", new Object[] { sourceMethod, ex2 });
            apiServiceDataHandler.updateMigrationStatus(config_id, 3, "Migration completed successfully", 2);
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(this.tempMigrationDir)) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.tempMigrationDir);
                }
            }
            catch (final Exception e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public JSONObject fetchAllProfiles(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final String sourceMethod = "MECloudAPIRequestHandler::fetchAllProfiles";
        MECloudAPIRequestHandler.logger.log(Level.INFO, "Profile Migration starts here..");
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        final JSONArray migrationSummary = new JSONArray();
        int status = 2;
        String statusMsg = "Migration completed successfully";
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        final ProfileMigrationHandler profileMigrationHandler = new ProfileMigrationHandler(MECloudAPIRequestHandler.meCloudAPIRequestHandler);
        final ProfileFacade profileFacade = new ProfileFacade();
        final Map requestHeaderMap = new HashMap();
        try {
            apiServiceDataHandler.updateMigrationStatus(config_id, status, null, 1);
            status = 3;
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchProfilesURL, null);
            this.validateResponse(responseObject);
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final JSONArray profiles = responseBodyObject.getJSONArray("profiles");
            apiServiceDataHandler.addOrUpdateMigratedTotalProfiles(config_id, profiles.length());
            JSONObject payloadResponseJSON = new JSONObject();
            final Map<String, String> profilesMigrationMap = new HashMap<String, String>();
            if (!ApiFactoryProvider.getFileAccessAPI().isDirectory(this.tempMigrationDir)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(this.tempMigrationDir);
            }
            final List<String> migratedProfileList = profileMigrationHandler.getMigratedProfiles(config_id);
            MECloudAPIRequestHandler.logger.log(Level.INFO, "Starting Migration for {0} profiles..", profiles.length());
            for (int i = 0; i < profiles.length(); ++i) {
                final JSONObject profile = profiles.getJSONObject(i);
                final String oldProfileID = profiles.getJSONObject(i).getString("profile_id");
                if (migratedProfileList.contains(oldProfileID)) {
                    MECloudAPIRequestHandler.logger.log(Level.INFO, "skipping profile :{0}", profile.getString("profile_name"));
                }
                else {
                    requestHeaderMap.put("user_id", user_id);
                    requestHeaderMap.put("customer_id", customer_id);
                    requestHeaderMap.put("service_id", service_id);
                    JSONObject requestJson = profileMigrationHandler.createRequestJsonForProfile(profiles.getJSONObject(i), requestHeaderMap);
                    final JSONObject newProfile = profileFacade.createProfile(requestJson);
                    final String new_profile_id = newProfile.getString("profile_id");
                    final String new_profile_name = newProfile.getString("profile_name");
                    final String new_collection_id = newProfile.getString("collection_id");
                    final String platform_type = newProfile.getString("platform_type");
                    requestHeaderMap.put("profile_id", new_profile_id);
                    requestHeaderMap.put("collection_id", new_collection_id);
                    profilesMigrationMap.put(oldProfileID, new_profile_id);
                    final JSONObject migratedProfile = new JSONObject();
                    migratedProfile.put("profile_id", (Object)new_profile_id);
                    migratedProfile.put("server_profile_id", (Object)oldProfileID);
                    migratedProfile.put("profile_name", (Object)new_profile_name);
                    migratedProfile.put("is_migrated", (Object)Boolean.TRUE);
                    migratedProfile.put("remarks", (Object)"Migration completed successfully");
                    responseObject = this.executeAPIRequest("GET", this.fetchProfilesURL + "/" + oldProfileID + "?include=payloaditems", null);
                    this.validateResponse(responseObject);
                    final JSONObject profileResponseJSON = responseObject.getJSONObject("ResponseJson");
                    final JSONArray payloads = profileResponseJSON.getJSONArray("payloads");
                    MECloudAPIRequestHandler.logger.log(Level.INFO, "going to migrate profile {0}", new_profile_name);
                    for (int payloadIndex = 0; payloadIndex < payloads.length(); ++payloadIndex) {
                        String payloadName = "";
                        try {
                            final JSONObject detailed_payload = payloads.getJSONObject(payloadIndex);
                            payloadName = detailed_payload.getString("payload_name");
                            requestHeaderMap.put("payload_name", payloadName);
                            final JSONArray payloadArray = detailed_payload.getJSONArray("payloaditems");
                            for (int j = 0; j < payloadArray.length(); ++j) {
                                payloadResponseJSON = payloadArray.getJSONObject(j);
                                payloadResponseJSON.remove("payload_id");
                                payloadResponseJSON.remove("sub_config");
                                profileMigrationHandler.migrateMediaContent(payloadResponseJSON, requestHeaderMap);
                                profileMigrationHandler.migrateCertificateContent(payloadResponseJSON, requestHeaderMap);
                                profileMigrationHandler.migrateMacSpecificContent(payloadResponseJSON, requestHeaderMap);
                                profileMigrationHandler.migrateKioskProfile(payloadResponseJSON, requestHeaderMap, platform_type, payloadName, config_id);
                                profileMigrationHandler.migrateSCEPTemplate(payloadResponseJSON, requestHeaderMap);
                                profileMigrationHandler.migrateCustomConfiguration(payloadResponseJSON, requestHeaderMap);
                                requestJson = profileMigrationHandler.createRequestJsonForProfile(payloadResponseJSON, requestHeaderMap);
                                String webclip_policy_id = "";
                                if (payloadName.equals("webclipspolicy") || payloadName.equals("androidwebclipspolicy")) {
                                    final JSONObject webclipPolicyJSON = new JSONObject(requestJson.toString());
                                    final JSONObject oldResIdentifier = webclipPolicyJSON.getJSONObject("msg_header").getJSONObject("resource_identifier");
                                    webclipPolicyJSON.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("profile_id", (Object)"webclips"));
                                    final JSONObject webclipsPolicyRes = WebClipsFacade.class.newInstance().addWebClipsPolicy(webclipPolicyJSON);
                                    webclipPolicyJSON.getJSONObject("msg_header").put("resource_identifier", (Object)oldResIdentifier);
                                    webclip_policy_id = String.valueOf(webclipsPolicyRes.getLong("webclip_policy_id"));
                                    final JSONObject webclipPolicyId = new JSONObject();
                                    webclipPolicyId.put("webclip_policy_id", (Object)webclip_policy_id);
                                    requestJson.remove("msg_body");
                                    requestJson.put("msg_body", (Object)webclipPolicyId);
                                }
                                profileFacade.addPayload(requestJson);
                            }
                        }
                        catch (final APIHTTPException ex) {
                            status = 4;
                            statusMsg = "Internal Server Error. Contact support with logs.";
                            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "{0} --> Exception occurred while migrating payload ..{1}->{2}", new Object[] { payloadName, payloadResponseJSON.toString(), ex });
                            final String errorMsg = ex.toJSONObject().getString("error_description");
                            profileMigrationHandler.revokeProfileMigration(profilesMigrationMap, migratedProfile, status, errorMsg, newProfile, oldProfileID, config_id, requestHeaderMap);
                            profile.put("profile_status", (Object)"Yet To Deploy");
                            break;
                        }
                    }
                    if (profile.getString("profile_status").equalsIgnoreCase("published")) {
                        try {
                            MECloudAPIRequestHandler.logger.log(Level.INFO, "going to publish profile.. {0}", requestJson.toString());
                            requestJson = profileMigrationHandler.createRequestJsonForProfile(payloadResponseJSON, requestHeaderMap);
                            profileFacade.publishProfile(requestJson);
                        }
                        catch (final Exception ex2) {
                            status = 4;
                            statusMsg = "Internal Server Error. Contact support with logs.";
                            migratedProfile.put("is_migrated", (Object)Boolean.FALSE);
                            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception occurred while publishing profile {0}: {1}....{2}", new Object[] { new_profile_name, payloadResponseJSON.toString(), ex2 });
                            profileMigrationHandler.revokeProfileMigration(profilesMigrationMap, migratedProfile, status, "Internal Server Error. Contact support with logs.", newProfile, oldProfileID, config_id, requestHeaderMap);
                            requestHeaderMap.keySet().clear();
                        }
                    }
                    migrationSummary.put((Object)migratedProfile);
                    if (migratedProfile.getBoolean("is_migrated")) {
                        apiServiceDataHandler.addOrUpdateMigratedProfiles(config_id);
                        MECloudAPIRequestHandler.logger.log(Level.INFO, "{0} profile migrated", new_profile_name);
                    }
                    apiServiceDataHandler.sleepForThrottle();
                }
            }
            MECloudAPIRequestHandler.logger.log(Level.INFO, "All Profiles Migration successfully completed..");
            int updatedCount = 0;
            if (!profilesMigrationMap.isEmpty()) {
                requestHeaderMap.put("user_id", user_id);
                requestHeaderMap.put("customer_id", customer_id);
                profileMigrationHandler.associateProfilesToGroup(profilesMigrationMap, config_id, requestHeaderMap);
                profileMigrationHandler.associateProfilesToDevice(profilesMigrationMap, config_id, requestHeaderMap);
                MECloudAPIRequestHandler.logger.log(Level.INFO, "Profiles Association  successfully completed ...");
            }
            updatedCount = profileMigrationHandler.updateProfileMigrationSummary(migrationSummary, config_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2188, null, user_name, "mdm.migration.profiles.completed", updatedCount, customer_id);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.tempMigrationDir);
            apiServiceDataHandler.updateMigrationStatus(config_id, 3, "Migration completed successfully", 1);
        }
        catch (final Exception ex3) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "{0} --> Exception occurred in ..{1}", new Object[] { sourceMethod, ex3 });
            apiServiceDataHandler.updateMigrationStatus(config_id, 3, "Migration completed successfully", 1);
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isDirectory(this.tempMigrationDir)) {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.tempMigrationDir);
                }
            }
            catch (final Exception e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        JSONObject authDetails = this.getAuthDetailsForAuthType(2, null);
        if (authDetails != null && authDetails.length() == 0) {
            authDetails = this.getAuthDetailsForAuthType(1, 1);
        }
        return authDetails;
    }
    
    protected String getNewEnrollmentURL() throws Exception {
        final Object newEnrollmentURL = new JSONObject(this.apiDetails.get("APIServiceConfiguration").toString()).opt("NEW_ENROLLMENT_URL".toLowerCase());
        return newEnrollmentURL.toString();
    }
    
    @Override
    public void getAuthorization(final boolean getNewKey) throws Exception {
        final JSONObject authInfoObject = this.getAuthDetailsForAuthType(1, 0);
        if (this.authToken == null || this.authTokenTime - System.currentTimeMillis() >= 3000000L) {
            this.authToken = this.getMDMAuthorizationHeader(authInfoObject, getNewKey);
            this.authTokenTime = System.currentTimeMillis();
        }
        JSONObject basicDigestObject = authInfoObject.optJSONObject("BasicDigestAuthInfo");
        if (basicDigestObject == null || basicDigestObject.length() == 0) {
            basicDigestObject = new JSONObject();
            authInfoObject.put("BasicDigestAuthInfo", (Object)basicDigestObject);
        }
        basicDigestObject.put("AUTHORIZATION_HEADER", (Object)this.authToken);
        new APIServiceDataHandler().editAuthDetails(authInfoObject);
        this.headerObject.put("Authorization", (Object)this.authToken);
    }
    
    public String fileUpload(String fileInfo, final Map requestHeaderMap, final int fileType) throws Exception {
        MECloudAPIRequestHandler.logger.log(Level.INFO, " Going to start Media Migration..");
        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON = new ProfileMigrationHandler(MECloudAPIRequestHandler.meCloudAPIRequestHandler).createRequestJsonForProfile(requestJSON, requestHeaderMap);
            String downloadUrl = this.apiServerURL;
            final int service_id = Integer.valueOf(requestHeaderMap.get("service_id").toString());
            if (fileType == 3) {
                final String cert_id = Paths.get(fileInfo, new String[0]).getFileName().toString().split("\\.")[0];
                downloadUrl = this.fetchProfilesURL + "/certificates/" + cert_id + "/download";
                if (service_id == 1 || service_id == 8) {
                    downloadUrl += "?service=mdm";
                }
            }
            else if (fileType == 1 || fileType == 2) {
                if (service_id == 1 || service_id == 8) {
                    final String fileId = fileInfo.substring(fileInfo.lastIndexOf("/") + 1).split("\\?")[0];
                    downloadUrl = downloadUrl + "api/v1/mdm/dfsfiles/" + fileId + "?service=mdm";
                }
                else {
                    downloadUrl += fileInfo.substring(1);
                }
            }
            else if (fileType == 4) {
                fileInfo = Paths.get(fileInfo, new String[0]).getFileName().toString();
                if (service_id == 1 || service_id == 8) {
                    downloadUrl = this.fetchProfilesURL + "/customprofiles/" + fileInfo + "/download?service=mdm";
                }
                else {
                    downloadUrl = this.fetchProfilesURL + "/customprofiles/" + fileInfo + "/download";
                }
            }
            this.fileDownload(downloadUrl, requestJSON);
        }
        catch (final Exception ex) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Error while uploading media {0}", ex);
            throw new APIHTTPException("MIG008", new Object[0]);
        }
        return requestJSON.getString("file_id");
    }
    
    private void fileDownload(final String url, JSONObject responseJSON) throws IOException {
        String file_location = "";
        InputStream inputStream = null;
        String fileName = "";
        MECloudAPIRequestHandler.logger.log(Level.INFO, " Going to start Media download {0}", fileName);
        try {
            final URL linkURL = new URL(url);
            final HttpURLConnection urlConnection = (HttpURLConnection)linkURL.openConnection();
            urlConnection.setRequestProperty("Authorization", this.headerObject.getString("Authorization"));
            urlConnection.setRequestProperty("content-type", "application/json");
            urlConnection.setRequestMethod("GET");
            final int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();
                final Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
                if (responseHeaders.containsKey("Content-Disposition")) {
                    fileName = responseHeaders.get("Content-Disposition").get(0).split("filename=")[1].replace("\"", "").replace(" ", "");
                }
                else {
                    final String[] urlSplit = url.split("/");
                    fileName = urlSplit[urlSplit.length - 1];
                }
                if (responseHeaders.containsKey("Content-Type")) {
                    responseJSON.put("content_type", (Collection)responseHeaders.get("Content-Type"));
                }
                if (responseHeaders.containsKey("Content-Length")) {
                    responseJSON.put("content_length", (Collection)responseHeaders.get("Content-Length"));
                }
                file_location = this.tempMigrationDir + File.separator + fileName;
                if (!ApiFactoryProvider.getFileAccessAPI().getCanonicalPath(file_location).startsWith(ApiFactoryProvider.getFileAccessAPI().getCanonicalPath(this.tempMigrationDir))) {
                    throw new Exception("ZIP_SLIP_ERROR");
                }
                ApiFactoryProvider.getFileAccessAPI().writeFile(file_location, inputStream);
                if (!responseHeaders.containsKey("Content-Length") || !responseHeaders.containsKey("Content-Type")) {
                    inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(file_location);
                    Long content_length = 0L;
                    int bytesRead = -1;
                    final byte[] buffer = new byte[4096];
                    String content_type = "";
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        content_type = new Tika().detect(buffer);
                        content_length += (Long)bytesRead;
                    }
                    responseJSON.put("content_type", (Object)content_type);
                    responseJSON.put("content_length", (Object)content_length);
                }
                fileName = Paths.get(fileName, new String[0]).getFileName().toString();
                responseJSON.put("file_name", (Object)fileName);
                inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(file_location);
                responseJSON = new FileFacade().addFile(responseJSON, inputStream);
            }
            else {
                if (responseCode == 412) {
                    MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Unable to download media due to pre-condition failure");
                    throw new APIHTTPException("COM0015", new Object[0]);
                }
                MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Unable to download media for migration");
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        catch (final MalformedURLException e) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Incorrect URL: {0}", e);
        }
        catch (final ProtocolException e2) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Protocol exception: {0}", e2);
        }
        catch (final IOException e3) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "IOException while getting ResponseCode/InputStream {0}", e3);
        }
        catch (final Exception e4) {
            MECloudAPIRequestHandler.logger.log(Level.SEVERE, "Exception occured while downloading media content {0}", e4);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            try {
                if (ApiFactoryProvider.getFileAccessAPI().isFileExists(file_location)) {
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(file_location);
                }
            }
            catch (final Exception e5) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    static {
        MECloudAPIRequestHandler.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
