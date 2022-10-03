package com.me.mdm.mdmmigration.meraki;

import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import java.util.ArrayList;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.mdmmigration.MigrationSummary;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.mdmmigration.MigrationConstants;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class MerakiAPIRequestHandler extends MigrationAPIRequestHandler
{
    public Logger logger;
    String networkId;
    String authenticationKey;
    JSONArray allDevices;
    
    public MerakiAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.networkId = null;
        this.authenticationKey = null;
        this.allDevices = null;
    }
    
    @Override
    protected void initializeConfiguration(final JSONObject request) throws Exception {
        if (request.length() > 0) {
            super.initializeConfiguration(request);
            final String serverURL = this.getAPIServerBaseURL();
            final JSONObject customHeaderInfo = this.getAuthDetailsForAuthType(6, null).getJSONObject("CustomHeadersAuthInfo");
            this.authenticationKey = (String)customHeaderInfo.get("authkey");
            this.networkId = (String)customHeaderInfo.get("networkid");
            this.fetchDeviceURL = serverURL + "/api/v0/networks/" + this.networkId + "/sm/devices";
            this.removeDeviceURL = serverURL + "/api/v0/networks/" + this.networkId + "/sm/devices/";
            this.fetchUsersURL = serverURL + "/api/v0/networks/" + this.networkId + "/sm/users";
        }
    }
    
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject response = new JSONObject();
        try {
            String managedStatus = "Managed";
            final String deviceId = msgJson.getString("deviceID");
            final Long config_id = Long.parseLong(msgJson.getString("config_id"));
            final String merakiServerDeviceId = new APIServiceDataHandler().getDeviceIdForUDID(deviceId, config_id);
            final JSONObject parameters = new JSONObject();
            parameters.put("ids", (Object)merakiServerDeviceId);
            parameters.put("fields", (Object)"isManaged");
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, parameters);
            final int statusCode = responseObject.getInt("StatusCode");
            final JSONArray devices = responseObject.getJSONObject("ResponseJson").getJSONArray("devices");
            if (devices != null && devices.length() > 0) {
                for (int i = 0; i < devices.length(); ++i) {
                    final JSONObject device = devices.getJSONObject(i);
                    if (!device.optBoolean("isManaged")) {
                        managedStatus = "Unmanaged";
                        response.put("NewEnrollmentURL", (Object)this.newEnrollmentURL);
                        response.put("udid", (Object)deviceId);
                    }
                }
                response.put("Status", (Object)managedStatus);
            }
            else if (statusCode == 401) {
                this.isAuthorizationFailed = true;
                new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                if (this.handleUnauthorisedResponse(msgJson, response)) {
                    return response;
                }
                this.handleManagementStatusCheckRequest(msgJson);
            }
            else {
                this.logger.log(Level.SEVERE, "Error while checking the device status", new Object[] { responseObject });
                response.put("Error", (Object)"100");
                response.put("ErrorMsg", (Object)"Unknown Error");
            }
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
            final Long config_id = Long.parseLong(msgJson.getString("config_id"));
            final String merakiServerDeviceId = new APIServiceDataHandler().getDeviceIdForUDID(deviceID, config_id);
            final JSONObject responseObject = this.executeAPIRequest("POST", this.removeDeviceURL + merakiServerDeviceId + "/unenroll", null);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final int statusCode = responseObject.getInt("StatusCode");
            if (responseBodyObject.has("success")) {
                if (responseBodyObject.optBoolean("success")) {
                    return this.handleManagementStatusCheckRequest(msgJson);
                }
                final String error = "Unknown Error";
                response.put("Error", (Object)"100");
                response.put("ErrorMsg", (Object)error);
            }
            else if (statusCode == 401) {
                new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                if (this.handleUnauthorisedResponse(msgJson, response)) {
                    return response;
                }
                this.handleUnmanageDeviceRequest(msgJson);
            }
            else {
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
        final JSONArray requirements = MigrationConstants.MerakiRequirements.productIRequirements;
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            if (requirements.getString(i).equalsIgnoreCase("API Key")) {
                object.put("api_key", (Object)"authkey");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Network ID")) {
                object.put("api_key", (Object)"networkid");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Root URL")) {
                object.put("api_key", (Object)"Server_URL");
            }
            object.put("display_name", (Object)requirements.getString(i));
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
        auth.put("authentication_type", (Object)"api_key");
        return auth;
    }
    
    public JSONObject prepareDeviceDBInsertObject(final JSONObject device) {
        final JSONObject deviceDetailsJSON = new JSONObject();
        deviceDetailsJSON.put("OS", device.opt("osName"));
        deviceDetailsJSON.put("UDID", device.opt("uuid"));
        deviceDetailsJSON.put("MODEL", device.opt("systemModel"));
        deviceDetailsJSON.put("DEVICE_NAME", device.opt("name"));
        deviceDetailsJSON.put("MANUFACTURER", device.opt("systemModel"));
        deviceDetailsJSON.put("MIGRATION_SERIAL_ID", device.opt("serialNumber"));
        deviceDetailsJSON.put("MIGRATION_SERVER_DEVICE_ID", device.opt("id"));
        return deviceDetailsJSON;
    }
    
    public JSONObject prepareUserDBInsertObject(final JSONObject user) {
        final JSONObject userObject = new JSONObject();
        if (user.has("fullName")) {
            userObject.put("USER_NAME", user.get("fullName"));
        }
        if (user.has("email")) {
            userObject.put("EMAIL_ID", user.get("email"));
        }
        if (user.has("id")) {
            userObject.put("MIGRATION_SERVER_USER_ID", user.get("id"));
        }
        return userObject;
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
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, null);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            if (responseBodyObject.get("devices") instanceof JSONArray) {
                while (responseBodyObject.has("batchToken")) {
                    final JSONArray devices = responseBodyObject.getJSONArray("devices");
                    for (int i = 0; i < devices.length(); ++i) {
                        deviceDetailsJSON = this.prepareDeviceDBInsertObject(devices.getJSONObject(i));
                        deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                        fetchedDevices.put((Object)deviceDetailsJSON);
                        deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                        new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                        apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                        apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                    }
                    final JSONObject parameters = new JSONObject();
                    parameters.put("batchToken", (Object)responseBodyObject.getString("batchToken"));
                    responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, parameters);
                    responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    apiServiceDataHandler.sleepForThrottle();
                }
                final JSONArray devices = responseBodyObject.getJSONArray("devices");
                for (int i = 0; i < devices.length(); ++i) {
                    deviceDetailsJSON = this.prepareDeviceDBInsertObject(devices.getJSONObject(i));
                    deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                    fetchedDevices.put((Object)deviceDetailsJSON);
                    deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                    try {
                        new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from Meraki API", e.toString());
                        continue;
                    }
                    apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                }
            }
            else {
                final JSONObject device = response.getJSONObject("devices");
                deviceDetailsJSON = this.prepareDeviceDBInsertObject(device);
                deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                try {
                    new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                }
                catch (final Exception e2) {
                    this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from Meraki API", e2.toString());
                }
                apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2182, null, user_name, "mdm.migration.device.completed", migrationSummary.get("migrated_devices"), customer_id);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception on fetching devices", e3);
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
                    this.logger.log(Level.SEVERE, "Exception while updating the API error to DB", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        response.put("devices", (Object)fetchedDevices);
        return response;
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
            this.fetchDevicesWithEmail();
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL, null);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            if (responseObject.get("ResponseJson") instanceof JSONArray) {
                final JSONArray users = responseObject.getJSONArray("ResponseJson");
                for (int i = 0; i < users.length(); ++i) {
                    try {
                        final JSONObject user = users.getJSONObject(i);
                        final JSONObject userDbObject = this.prepareUserDBInsertObject(user);
                        if (apiServiceDataHandler.isADSyncedUser(userDbObject.getString("EMAIL_ID"))) {
                            userDbObject.put("IS_AD_SYNCED_USER", true);
                            userDbObject.put("DOMAIN", (Object)apiServiceDataHandler.getADDomainName(userDbObject.getString("EMAIL_ID")));
                        }
                        userDbObject.put("CONFIG_ID", (Object)config_id);
                        final Row userRow = apiServiceDataHandler.getRowForUser(userDbObject, customer_id);
                        userDataObject.addRow(userRow);
                        apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Exception while migrating user", e);
                    }
                }
                for (int i = 0; i < users.length(); ++i) {
                    final JSONObject user = users.getJSONObject(i);
                    try {
                        final List<Row> userDevices = this.prepareDevicesPerUser(user, config_id, customer_id);
                        for (final Row row2 : userDevices) {
                            if (row2.get("DEVICE_ID") != null) {
                                associationDataObject.addRow(row2);
                            }
                        }
                    }
                    catch (final Exception e2) {
                        this.logger.log(Level.SEVERE, e2, () -> "Error while fetching associated device for user " + jsonObject.toString());
                    }
                }
            }
            else if (responseObject.get("ResponseJson") instanceof JSONObject) {
                final JSONObject user2 = responseObject.getJSONObject("ResponseJson");
                final JSONObject userDbObject2 = this.prepareUserDBInsertObject(user2);
                if (apiServiceDataHandler.isADSyncedUser(userDbObject2.getString("EMAIL_ID"))) {
                    userDbObject2.put("IS_AD_SYNCED_USER", true);
                    userDbObject2.put("DOMAIN", (Object)apiServiceDataHandler.getADDomainName(userDbObject2.getString("EMAIL_ID")));
                }
                userDbObject2.put("CONFIG_ID", (Object)config_id);
                final Row userRow2 = apiServiceDataHandler.getRowForUser(userDbObject2, customer_id);
                userDataObject.addRow(userRow2);
                try {
                    final List<Row> userDevices = this.prepareDevicesPerUser(user2, config_id, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                    for (final Row row2 : userDevices) {
                        if (row2.get("DEVICE_ID") != null) {
                            associationDataObject.addRow(row2);
                        }
                    }
                }
                catch (final Exception e2) {
                    this.logger.log(Level.SEVERE, e2, () -> "Error while fetching associated device for user " + jsonObject2.toString());
                }
            }
            apiServiceDataHandler.addOrUpdateMigrationUserDeviceAssociation(associationDataObject, config_id, customer_id);
            final int platformType = new EnrollmentTemplateHandler().getPlatformForTemplate(50);
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation();
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            final Iterator iterator = userDeviceAssociation.keys();
            while (iterator.hasNext()) {
                final String userId = iterator.next().toString();
                for (int j = 0; j < userDeviceAssociation.getJSONArray(userId).length(); ++j) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId).getJSONObject(j);
                    toAssign.put("CustomerId", (Object)customer_id);
                    listToAssign.add(toAssign);
                }
            }
            AdminEnrollmentHandler.assignUser(listToAssign, user_id, 50, "DEVICE_ID", platformType);
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2184, null, user_name, "mdm.migration.users.completed", migrationSummary.get("migrated_users"), customer_id);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error while fetching users", e3);
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
            final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("GROUPS_STATUS", (Object)2);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            final JSONObject groupDeviceMap = new JSONObject();
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final DataObject groupDeviceAssociationDO = (DataObject)new WritableDataObject();
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, null);
            if (String.valueOf(responseObject.getInt("StatusCode")).startsWith("4")) {
                this.isAuthorizationFailed = true;
                apiServiceDataHandler.setAuthorizationFailed(config_id);
                throw new APIHTTPException("MIG001", new Object[] { "Invalid API service configuration details" });
            }
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            if (responseBodyObject.get("devices") instanceof JSONArray) {
                final JSONArray devices = responseBodyObject.getJSONArray("devices");
                for (int i = 0; i < devices.length(); ++i) {
                    final JSONObject device = devices.getJSONObject(i);
                    final JSONArray tags = device.getJSONArray("tags");
                    final String uuid = device.getString("uuid");
                    for (int j = 0; j < tags.length(); ++j) {
                        if (groupDeviceMap.has(tags.getString(j))) {
                            final JSONArray UDIDs = groupDeviceMap.getJSONArray(tags.getString(j));
                            UDIDs.put((Object)uuid);
                            groupDeviceMap.put(tags.getString(j), (Object)UDIDs);
                        }
                        else {
                            final JSONArray UDIDs = new JSONArray();
                            UDIDs.put((Object)uuid);
                            final JSONObject groupDBObject = new JSONObject();
                            groupDBObject.put("GROUP_NAME", (Object)tags.getString(j));
                            groupDBObject.put("CONFIG_ID", (Object)config_id);
                            apiServiceDataHandler.getRowForGroup(groupDBObject, customer_id);
                            groupDeviceMap.put(tags.getString(j), (Object)UDIDs);
                            apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
                        }
                    }
                }
                Iterator iterator1 = groupDeviceMap.keys();
                while (iterator1.hasNext()) {
                    final String group = iterator1.next().toString();
                    try {
                        final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
                        cgDetails.groupType = 6;
                        cgDetails.platformType = 0;
                        cgDetails.groupCategory = 1;
                        cgDetails.customerId = customer_id;
                        cgDetails.domainName = "MDM";
                        cgDetails.groupPlatformType = 0;
                        cgDetails.groupName = group;
                        MDMGroupHandler.getInstance().addGroup(cgDetails);
                        apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Exception while creating migrated groups", e + group);
                    }
                }
                iterator1 = groupDeviceMap.keys();
                while (iterator1.hasNext()) {
                    final String group = iterator1.next().toString();
                    final JSONArray udids = groupDeviceMap.getJSONArray(group);
                    for (int k = 0; k < udids.length(); ++k) {
                        final Long group_id = new APIServiceDataHandler().getGroupIDForGroupName(group, customer_id);
                        final String deviceId = apiServiceDataHandler.getMDMDeviceIdForUDID(udids.getString(k), config_id, customer_id);
                        if (deviceId != null && group_id != null) {
                            row = new Row("MigrationDeviceToGroup");
                            row.set("CONFIG_ID", (Object)config_id);
                            row.set("DEVICE_ID", (Object)deviceId);
                            row.set("RESOURCE_GROUP_ID", (Object)group_id);
                            groupDeviceAssociationDO.addRow(row);
                        }
                    }
                }
                apiServiceDataHandler.addOrUpdateMigrationDeviceToGroupAssociation(groupDeviceAssociationDO, config_id, customer_id);
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2186, null, user_name, "mdm.migration.groups.completed", migrationSummary.get("migrated_groups"), customer_id);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while fetching all groups", e2);
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
                    this.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    private void fetchDevicesWithEmail() {
        final JSONObject parameters = new JSONObject();
        parameters.put("fields", (Object)"ownerEmail");
        final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL, parameters);
        final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
        if (responseBodyObject.get("devices") instanceof JSONArray) {
            this.allDevices = responseBodyObject.getJSONArray("devices");
        }
        else {
            (this.allDevices = new JSONArray()).put((Object)responseBodyObject.getJSONObject("devices"));
        }
    }
    
    private List<Row> prepareDevicesPerUser(final JSONObject user, final Long config_id, final Long customer_id) {
        final List<Row> devices = new ArrayList<Row>();
        for (int i = 0; i < this.allDevices.length(); ++i) {
            final JSONObject device = this.allDevices.getJSONObject(i);
            final String userId = new APIServiceDataHandler().getMigrationUserIDForUserName(user.getString("fullName"), config_id, customer_id);
            if (device.has("ownerEmail") && device.get("ownerEmail").toString().equals(user.getString("email"))) {
                final Long deviceID = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device.getString("id"), config_id, customer_id);
                final Row deviceRow = new Row("MigrationAssociation");
                deviceRow.set("CONFIG_ID", (Object)config_id);
                deviceRow.set("DEVICE_ID", (Object)deviceID);
                deviceRow.set("USER_ID", (Object)userId);
                devices.add(deviceRow);
            }
        }
        return devices;
    }
    
    @Override
    public void getAuthorization(final boolean newKey) throws Exception {
        this.headerObject.put("X-Cisco-Meraki-API-Key", (Object)this.authenticationKey);
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        return this.getOtherAuthDetails(6);
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
