package com.me.mdm.mdmmigration.airwatch;

import java.util.LinkedList;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import java.util.Iterator;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import java.util.ArrayList;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.mdmmigration.MigrationSummary;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.me.mdm.mdmmigration.MigrationConstants;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import sun.misc.BASE64Encoder;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class AirwatchAPIRequestHandler extends MigrationAPIRequestHandler
{
    String aw_tenant_code;
    String username;
    String password;
    String apiKey;
    String fetchOrganizationalGroupsURL;
    String fetchUserGroupsURL;
    String fetchUserGroupsAssociationURL;
    Logger logger;
    
    public AirwatchAPIRequestHandler() {
        this.logger = Logger.getLogger("MigrationEventLogger");
    }
    
    @Override
    protected void initializeConfiguration(final JSONObject request) throws Exception {
        if (request.length() > 0) {
            super.initializeConfiguration(request);
            final String serverURL = this.getAPIServerBaseURL();
            final JSONObject basicDigestInfo = this.getAuthDetailsForAuthType(3, null).getJSONObject("BasicDigestAuthInfo");
            this.username = basicDigestInfo.getString("USERNAME".toLowerCase());
            this.password = basicDigestInfo.getString("PASSWORD".toLowerCase());
            final JSONObject customHeaderInfo = this.getAuthDetailsForAuthType(3, null).getJSONObject("CustomHeadersAuthInfo");
            this.aw_tenant_code = (String)customHeaderInfo.get("aw-tenant-code");
            this.apiKey = new BASE64Encoder().encode((this.username + ":" + this.password).getBytes());
            this.fetchDeviceURL = serverURL + "api/mdm/devices/search";
            this.fetchUsersURL = serverURL + "api/mdm/devices";
            this.removeDeviceURL = serverURL + "api/mdm/devices";
            this.fetchGroupsURL = serverURL + "api/mdm/devices/";
            this.fetchOrganizationalGroupsURL = serverURL + "api/system/groups/search";
            this.fetchUserGroupsURL = serverURL + "api/system/usergroups/custom/search";
            this.fetchUserGroupsAssociationURL = serverURL + "api/system/usergroups/";
        }
    }
    
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject response = new JSONObject();
        try {
            String managedStatus = "Managed";
            final String deviceId = msgJson.getString("deviceID");
            final JSONObject parameters = new JSONObject();
            parameters.put("searchby", (Object)"Udid");
            parameters.put("id", (Object)deviceId);
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL, parameters);
            final int statusCode = responseObject.getInt("StatusCode");
            final JSONObject device = responseObject.getJSONObject("ResponseJson");
            this.logger.log(Level.INFO, "Response Success Airwatch:{0}", statusCode);
            if (statusCode == 401) {
                if (this.handleUnauthorisedResponse(msgJson, response)) {
                    return response;
                }
            }
            else {
                if (statusCode == 404) {
                    managedStatus = "Unmanaged";
                    response.put("Status", (Object)managedStatus);
                    response.put("NewEnrollmentURL", (Object)this.newEnrollmentURL);
                    this.logger.log(Level.INFO, "Airwatch Device unmanaged, response sent {0}", managedStatus);
                    return response;
                }
                if (device != null && device.getString("EnrollmentStatus").equalsIgnoreCase("Enrolled")) {
                    response.put("Status", (Object)managedStatus);
                    this.logger.log(Level.INFO, "Airwatch Device is managed, response sent: {0}", managedStatus);
                    return response;
                }
            }
            this.logger.log(Level.SEVERE, "Error while checking the device status", statusCode);
            response.put("Error", (Object)"100");
            response.put("ErrorMsg", (Object)"Unknown Error");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while checking device management status for device", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    protected JSONObject handleUnmanageDeviceRequest(final JSONObject msgJson) {
        final JSONObject response = new JSONObject();
        try {
            final String deviceID = msgJson.getString("deviceID");
            final JSONObject parameters = new JSONObject();
            parameters.put("searchby", (Object)"Udid");
            parameters.put("id", (Object)deviceID);
            final JSONObject responseObject = this.executeAPIRequest("DELETE", this.removeDeviceURL, parameters);
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final int statusCode = responseObject.getInt("StatusCode");
            if (statusCode == 401) {
                if (this.handleUnauthorisedResponse(msgJson, response)) {
                    return response;
                }
                this.handleUnmanageDeviceRequest(msgJson);
            }
            else {
                if (statusCode == 200 || statusCode == 204) {
                    this.logger.log(Level.INFO, "Airwatch Device is unmanaged, calling handleManagementStatusCheckRequest(msgJson): {0}", msgJson);
                    return this.handleManagementStatusCheckRequest(msgJson);
                }
                response.put("Error", (Object)"100");
                response.put("ErrorMsg", (Object)"Unknown Error");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while handleUnmanageDeviceRequest ", e);
            try {
                response.put("Error", (Object)"100");
                response.put("ErrorMsg", (Object)"Unknown Error");
                response.put("ErrorMsg", (Object)e.getMessage());
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Error while handleUnmanageDeviceRequest ", ex);
            }
        }
        return response;
    }
    
    @Override
    public JSONArray configurationDetails() {
        final JSONArray response = new JSONArray();
        final JSONArray requirements = MigrationConstants.AirwatchRequirements.requirementList;
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            object.put("display_name", (Object)requirements.get(i).toString());
            if (requirements.getString(i).equalsIgnoreCase("Root URL")) {
                object.put("api_key", (Object)"Server_URL");
            }
            else if (requirements.getString(i).equalsIgnoreCase("AirWatch Tenant Code")) {
                object.put("api_key", (Object)"aw-tenant-code");
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
        final JSONArray result = new JSONArray();
        final JSONObject configJSON = new JSONObject();
        configJSON.put("CONFIG_ID", (Object)config_id);
        this.initializeConfiguration(configJSON);
        final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchOrganizationalGroupsURL, null);
        if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
            this.isAuthorizationFailed = true;
            new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
            throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
        }
        final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
        final JSONArray organizationalGroups = responseBodyObject.getJSONArray("LocationGroups");
        final DataObject dataObject = (DataObject)new WritableDataObject();
        for (int i = 0; i < organizationalGroups.length(); ++i) {
            final JSONObject json = new JSONObject();
            final String orgName = organizationalGroups.getJSONObject(i).getString("Name");
            final String orgAirwatchId = String.valueOf(organizationalGroups.getJSONObject(i).getJSONObject("Id").getInt("Value"));
            final Row row = new Row("MigrationOrganizations");
            row.set("CONFIG_ID", (Object)config_id);
            row.set("ORG_NAME", (Object)orgName);
            row.set("OLD_SERVER_ORG_ID", (Object)orgAirwatchId);
            dataObject.addRow(row);
            json.put("name", (Object)orgName);
            json.put("id", (Object)orgAirwatchId);
            result.put((Object)json);
        }
        try {
            DataAccess.add(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while adding organizational details to DB", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    @Override
    public JSONObject getAuthenticationType() {
        final JSONObject auth = new JSONObject();
        auth.put("authentication_type", (Object)"api_key");
        return auth;
    }
    
    public JSONObject prepareDeviceDBInsertObject(final JSONObject device) {
        final JSONObject deviceDetailsJSON = new JSONObject();
        deviceDetailsJSON.put("OS", device.opt("OperatingSystem"));
        deviceDetailsJSON.put("UDID", device.opt("Udid"));
        deviceDetailsJSON.put("MODEL", device.opt("Model"));
        deviceDetailsJSON.put("DEVICE_NAME", device.opt("DeviceFriendlyName"));
        deviceDetailsJSON.put("MIGRATION_SERIAL_ID", device.opt("SerialNumber"));
        deviceDetailsJSON.put("MIGRATION_SERVER_DEVICE_ID", device.getJSONObject("Id").getInt("Value"));
        deviceDetailsJSON.put("IMEI", device.opt("Imei"));
        deviceDetailsJSON.put("EAS_ID", device.opt("EasId"));
        return deviceDetailsJSON;
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
            final List orgIds = new APIServiceDataHandler().getOrgIdsToBeMigrated(customer_id, config_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final JSONObject parameters = new JSONObject();
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, parameters);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            if (responseBodyObject.get("Devices") instanceof JSONArray) {
                JSONArray devices = responseBodyObject.getJSONArray("Devices");
                int totalDevicesCount = responseBodyObject.getInt("Total");
                int pageNumber = 0;
                while (totalDevicesCount > 0) {
                    ++pageNumber;
                    for (int i = 0; i < devices.length(); ++i) {
                        if (orgIds.contains((long)devices.getJSONObject(i).getJSONObject("LocationGroupId").getJSONObject("Id").getInt("Value"))) {
                            deviceDetailsJSON = this.prepareDeviceDBInsertObject(devices.getJSONObject(i));
                            deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                            fetchedDevices.put((Object)deviceDetailsJSON);
                            deviceDetailsJSON.put("EASID", deviceDetailsJSON.get("EAS_ID"));
                            deviceDetailsJSON.put("SerialNumber", deviceDetailsJSON.get("MIGRATION_SERIAL_ID"));
                            deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                            new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                            apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                            apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                        }
                    }
                    totalDevicesCount -= devices.length();
                    parameters.put("page", pageNumber);
                    responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, parameters);
                    if (responseObject.getInt("StatusCode") == 204) {
                        break;
                    }
                    responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    devices = responseBodyObject.getJSONArray("Devices");
                }
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2182, null, user_name, "mdm.migration.device.completed", migrationSummary.get("migrated_devices"), customer_id);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception on fetching devices", e);
            if (!this.isAuthorizationFailed) {
                try {
                    final Row statusRow2 = new Row("MDMServerMigrationStatus");
                    statusRow2.set("CONFIG_ID", (Object)config_id);
                    final DataObject dataObject2 = DataAccess.get("MDMServerMigrationStatus", statusRow2);
                    final Row row2 = dataObject2.getRow("MDMServerMigrationStatus");
                    row2.set("DEVICES_STATUS", (Object)4);
                    row2.set("FETCH_DEVICES_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                    dataObject2.updateRow(row2);
                    DataAccess.update(dataObject2);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception on updating authorization failed info to DB", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        response.put("devices", (Object)fetchedDevices);
        return response;
    }
    
    public JSONObject prepareUserDBInsertObject(final JSONObject user) {
        final JSONObject userObject = new JSONObject();
        if (user.has("UserName")) {
            userObject.put("USER_NAME", user.get("UserName"));
        }
        if (user.has("Email")) {
            userObject.put("EMAIL_ID", user.get("Email"));
        }
        if (user.has("Id")) {
            userObject.put("MIGRATION_SERVER_USER_ID", (Object)String.valueOf(user.getJSONObject("Id").getInt("Value")));
        }
        if (user.has("Status")) {
            userObject.put("STATUS", user.get("Status"));
        }
        if (user.has("ContactNumber")) {
            userObject.put("PHONE_NUMBER", user.get("ContactNumber"));
        }
        return userObject;
    }
    
    @Override
    public JSONObject fetchAllUsers(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        final DataObject userDataObject = (DataObject)new WritableDataObject();
        final DataObject associationDataObject = (DataObject)new WritableDataObject();
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        try {
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("USERS_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final JSONObject parameters = new JSONObject();
            final JSONArray deviceIds = apiServiceDataHandler.getAllMigratedServerDeviceIDs(config_id, customer_id);
            for (int index = 0; index < deviceIds.length(); ++index) {
                final String deviceID = deviceIds.getString(index);
                final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL + "/" + deviceID + "/user", parameters);
                final int statusCode = responseObject.getInt("StatusCode");
                if (statusCode == 401) {
                    this.isAuthorizationFailed = true;
                    new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                    throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
                }
                if (statusCode == 200) {
                    final JSONObject user = responseObject.getJSONObject("ResponseJson").getJSONObject("DeviceUser");
                    if (user.getBoolean("Status")) {
                        final JSONObject userDbObject = this.prepareUserDBInsertObject(user);
                        if (apiServiceDataHandler.isADSyncedUser(userDbObject.getString("EMAIL_ID"))) {
                            userDbObject.put("IS_AD_SYNCED_USER", true);
                            userDbObject.put("DOMAIN", (Object)apiServiceDataHandler.getADDomainName(userDbObject.getString("EMAIL_ID")));
                        }
                        userDbObject.put("CONFIG_ID", (Object)config_id);
                        final Row userRow = apiServiceDataHandler.getRowForUser(userDbObject, customer_id);
                        userDataObject.addRow(userRow);
                        apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                        try {
                            final Row associationRow = new Row("MigrationAssociation");
                            associationRow.set("CONFIG_ID", (Object)config_id);
                            associationRow.set("DEVICE_ID", (Object)apiServiceDataHandler.getDeviceIDForServerDeviceID(deviceID, config_id, customer_id));
                            final String userId = apiServiceDataHandler.getMigrationUserIDForUserName(userDbObject.getString("USER_NAME"), config_id, customer_id);
                            associationRow.set("USER_ID", (Object)userId);
                            associationDataObject.addRow(associationRow);
                        }
                        catch (final Exception e) {
                            this.logger.log(Level.SEVERE, "Exception while user device mapping:", e);
                        }
                    }
                }
            }
            apiServiceDataHandler.addOrUpdateMigrationUserDeviceAssociation(associationDataObject, config_id, customer_id);
            final int platformType = new EnrollmentTemplateHandler().getPlatformForTemplate(50);
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation();
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            final Iterator iterator = userDeviceAssociation.keys();
            while (iterator.hasNext()) {
                final String userId2 = iterator.next().toString();
                for (int i = 0; i < userDeviceAssociation.getJSONArray(userId2).length(); ++i) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId2).getJSONObject(i);
                    toAssign.put("CustomerId", (Object)customer_id);
                    listToAssign.add(toAssign);
                }
            }
            AdminEnrollmentHandler.assignUser(listToAssign, user_id, 50, "DEVICE_ID", platformType);
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2184, null, user_name, "mdm.migration.users.completed", migrationSummary.get("migrated_users"), customer_id);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error while fetching users", e2);
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
                    this.logger.log(Level.SEVERE, "Error while updating Migration Failure Status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public JSONObject fetchAllGroups(final Long config_id, final int service_id, final Long customer_id, final Long user_id) {
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        try {
            final List<Integer> orgIds = APIServiceDataHandler.class.newInstance().getOrgIdsToBeMigrated(customer_id, config_id);
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
            final JSONArray deviceIds = apiServiceDataHandler.getAllMigratedServerDeviceIDs(config_id, customer_id);
            final JSONObject deviceGroupAssociation = new JSONObject();
            JSONObject groupsDetails = new JSONObject();
            for (int index = 0; index < deviceIds.length(); ++index) {
                final String deviceID = deviceIds.getString(index);
                final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL + "/" + deviceID + "/smartgroups", null);
                final int statusCode = responseObject.getInt("StatusCode");
                if (statusCode == 401) {
                    this.isAuthorizationFailed = true;
                    new APIServiceDataHandler().setAuthorizationFailed(config_id);
                    throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
                }
                if (statusCode == 200) {
                    final JSONArray smartGroups = responseObject.getJSONObject("ResponseJson").getJSONArray("SmartGroup");
                    if (smartGroups != null) {
                        for (int i = 0; i < smartGroups.length(); ++i) {
                            final JSONObject smartGroup = smartGroups.getJSONObject(i);
                            final String group_id = smartGroup.getJSONObject("SmartGroupId").get("Value").toString();
                            final String groupName = smartGroup.getString("SmartGroupName");
                            if (!groupsDetails.has(group_id)) {
                                groupsDetails.put(group_id, (Object)groupName);
                            }
                            if (deviceGroupAssociation.has(deviceID)) {
                                final JSONArray groupNames = deviceGroupAssociation.getJSONArray(deviceID);
                                groupNames.put((Object)groupName);
                                deviceGroupAssociation.put(deviceID, (Object)groupNames);
                            }
                            else {
                                final JSONArray groupNames = new JSONArray();
                                groupNames.put((Object)groupName);
                                deviceGroupAssociation.put(deviceID, (Object)groupNames);
                            }
                        }
                    }
                }
            }
            Iterator keys = groupsDetails.keys();
            while (keys.hasNext()) {
                final String key = keys.next().toString();
                try {
                    final JSONObject group = new JSONObject();
                    group.put("GROUP_NAME", (Object)groupsDetails.getString(key));
                    group.put("MIGRATION_SERVER_GROUP_ID", (Object)key);
                    group.put("CONFIG_ID", (Object)config_id);
                    apiServiceDataHandler.getRowForGroup(group, customer_id);
                    final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
                    cgDetails.groupType = 6;
                    cgDetails.platformType = 0;
                    cgDetails.groupCategory = 1;
                    cgDetails.customerId = customer_id;
                    cgDetails.domainName = "MDM";
                    cgDetails.groupPlatformType = 0;
                    cgDetails.groupName = group.getString("GROUP_NAME");
                    MDMGroupHandler.getInstance().addGroup(cgDetails);
                    apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group.getString("GROUP_NAME"));
                    apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Exception while creating migrated group {0} {1}", new Object[] { e, key });
                }
            }
            final DataObject deviceGroupAssociationDO = (DataObject)new WritableDataObject();
            final Iterator iterator1 = deviceGroupAssociation.keys();
            while (iterator1.hasNext()) {
                final String deviceId = iterator1.next().toString();
                final String mdmDeviceId = APIServiceDataHandler.class.newInstance().getDeviceIDForServerDeviceID(deviceId, config_id, customer_id).toString();
                for (int i = 0; i < deviceGroupAssociation.getJSONArray(deviceId).length(); ++i) {
                    try {
                        final String group_name = deviceGroupAssociation.getJSONArray(deviceId).getString(i);
                        final Long group_id2 = new APIServiceDataHandler().getGroupIDForGroupName(group_name, customer_id);
                        if (group_id2 != null && deviceId != null) {
                            row = new Row("MigrationDeviceToGroup");
                            row.set("CONFIG_ID", (Object)config_id);
                            row.set("DEVICE_ID", (Object)mdmDeviceId);
                            row.set("RESOURCE_GROUP_ID", (Object)group_id2);
                            deviceGroupAssociationDO.addRow(row);
                        }
                    }
                    catch (final Exception e2) {
                        this.logger.log(Level.SEVERE, "Exception while adding device row:", e2);
                    }
                }
            }
            final DataObject userGroupAssociationDO = (DataObject)new WritableDataObject();
            final JSONObject userGroupAssociation = new JSONObject();
            final List<String> orgNames = new LinkedList<String>();
            for (int j = 0; j < orgIds.size(); ++j) {
                orgNames.add(APIServiceDataHandler.class.newInstance().getOrgNameForOrgId(String.valueOf(orgIds.get(j)), customer_id, config_id));
            }
            final JSONObject responseObject2 = this.executeAPIRequest("GET", this.fetchUserGroupsURL, null);
            final int statusCode2 = responseObject2.getInt("StatusCode");
            if (statusCode2 == 401) {
                this.isAuthorizationFailed = true;
                new APIServiceDataHandler().setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            if (statusCode2 == 200) {
                groupsDetails = new JSONObject();
                final JSONArray userGroups = responseObject2.getJSONObject("ResponseJson").getJSONArray("UserGroup");
                if (userGroups != null) {
                    for (int k = 0; k < userGroups.length(); ++k) {
                        final JSONObject userGroup = userGroups.getJSONObject(k);
                        if (orgNames.contains(userGroup.getString("OrganizationGroup"))) {
                            final String group_id3 = String.valueOf(userGroup.getInt("UserGroupId"));
                            final String groupName2 = userGroup.getString("UserGroupName");
                            if (!groupsDetails.has(group_id3)) {
                                groupsDetails.put(group_id3, (Object)groupName2);
                            }
                            final JSONObject resp = this.executeAPIRequest("GET", this.fetchUserGroupsAssociationURL + group_id3 + "/users", null);
                            if (resp.getInt("StatusCode") == 200) {
                                final JSONArray users = resp.getJSONObject("ResponseJson").getJSONArray("EnrollmentUser");
                                for (int x = 0; x < users.length(); ++x) {
                                    final JSONObject user = users.getJSONObject(x);
                                    final String userName = user.getString("UserName");
                                    final String mdmUserId = APIServiceDataHandler.class.newInstance().getUserIdForUserName(userName, customer_id, config_id);
                                    if (mdmUserId != null) {
                                        if (userGroupAssociation.has(mdmUserId)) {
                                            final JSONArray groupNames2 = userGroupAssociation.getJSONArray(mdmUserId);
                                            groupNames2.put((Object)groupName2);
                                            userGroupAssociation.put(mdmUserId, (Object)groupNames2);
                                        }
                                        else {
                                            final JSONArray groupNames2 = new JSONArray();
                                            groupNames2.put((Object)groupName2);
                                            userGroupAssociation.put(mdmUserId, (Object)groupNames2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            keys = groupsDetails.keys();
            while (keys.hasNext()) {
                final String key2 = keys.next().toString();
                final JSONObject group2 = new JSONObject();
                group2.put("GROUP_NAME", (Object)groupsDetails.getString(key2));
                group2.put("MIGRATION_SERVER_GROUP_ID", (Object)key2);
                group2.put("CONFIG_ID", (Object)config_id);
                apiServiceDataHandler.getRowForGroup(group2, customer_id);
                final MDMCustomGroupDetails cgDetails2 = new MDMCustomGroupDetails();
                cgDetails2.groupType = 7;
                cgDetails2.platformType = 0;
                cgDetails2.groupCategory = 1;
                cgDetails2.customerId = customer_id;
                cgDetails2.domainName = "MDM";
                cgDetails2.groupPlatformType = 0;
                cgDetails2.groupName = group2.getString("GROUP_NAME");
                MDMGroupHandler.getInstance().addGroup(cgDetails2);
                apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group2.getString("GROUP_NAME"));
                apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
            }
            Iterator iterator2 = userGroupAssociation.keys();
            while (iterator2.hasNext()) {
                final String mdmUserId2 = iterator2.next().toString();
                for (int l = 0; l < userGroupAssociation.getJSONArray(mdmUserId2).length(); ++l) {
                    try {
                        final String groupName3 = userGroupAssociation.getJSONArray(mdmUserId2).getString(l);
                        final Long mdmGroupId = APIServiceDataHandler.class.newInstance().getResourceGroupIdForGroupName(groupName3, customer_id);
                        row = new Row("MigrationUserToGroup");
                        row.set("CONFIG_ID", (Object)config_id);
                        row.set("USER_ID", (Object)mdmUserId2);
                        row.set("RESOURCE_GROUP_ID", (Object)mdmGroupId);
                        userGroupAssociationDO.addRow(row);
                    }
                    catch (final Exception e3) {
                        this.logger.log(Level.SEVERE, "Exception while user group association iteration", e3);
                    }
                }
            }
            apiServiceDataHandler.addOrUpdateMigrationDeviceToGroupAssociation(deviceGroupAssociationDO, config_id, customer_id);
            apiServiceDataHandler.addOrUpdateMigrationUserGroupAssociation(userGroupAssociationDO, config_id, customer_id);
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation();
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            iterator2 = userDeviceAssociation.keys();
            while (iterator2.hasNext()) {
                final String userId = iterator2.next().toString();
                for (int m = 0; m < userDeviceAssociation.getJSONArray(userId).length(); ++m) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId).getJSONObject(m);
                    toAssign.put("CustomerId", (Object)customer_id);
                    listToAssign.add(toAssign);
                }
            }
            for (int i2 = 0; i2 < listToAssign.size(); ++i2) {
                final Long resourceId = new APIServiceDataHandler().getUserResourceIdForUserName(listToAssign.get(i2).getString("UserName"), customer_id);
                final Long[] resourceIds = { resourceId };
                final List<Long> groupIds = new APIServiceDataHandler().getGroupIdsForUsername(listToAssign.get(i2).getString("UserName"), customer_id);
                MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupIds, resourceIds, customer_id, null);
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2186, null, user_name, "mdm.migration.groups.completed", migrationSummary.get("migrated_groups"), customer_id);
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "Exception while fetching all groups", e4);
            if (!this.isAuthorizationFailed) {
                try {
                    final Row statusRow2 = new Row("MDMServerMigrationStatus");
                    statusRow2.set("CONFIG_ID", (Object)config_id);
                    final DataObject dataObject2 = DataAccess.get("MDMServerMigrationStatus", statusRow2);
                    final Row row2 = dataObject2.getRow("MDMServerMigrationStatus");
                    row2.set("GROUPS_STATUS", (Object)4);
                    row2.set("FETCH_GROUPS_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                    dataObject2.updateRow(row2);
                    DataAccess.update(dataObject2);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public void getAuthorization(final boolean newKey) throws Exception {
        this.headerObject.put("aw-tenant-code", (Object)this.aw_tenant_code);
        this.headerObject.put("Authorization", (Object)("Basic " + this.apiKey));
        this.headerObject.put("Accept", (Object)"application/json");
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        return this.getOtherAuthDetails(3);
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
}
