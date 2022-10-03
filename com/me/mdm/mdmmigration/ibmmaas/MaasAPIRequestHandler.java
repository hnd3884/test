package com.me.mdm.mdmmigration.ibmmaas;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.customgroup.MDMCustomGroupDetails;
import java.util.Iterator;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.persistence.WritableDataObject;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.mdmmigration.MigrationSummary;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import org.json.JSONException;
import com.me.mdm.mdmmigration.MigrationConstants;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;

public class MaasAPIRequestHandler extends MigrationAPIRequestHandler
{
    public Logger logger;
    String billingId;
    String fetchGroupsForDeviceIdURL;
    String fetchGroupsForUserURL;
    
    public MaasAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMMigrationLogger");
        this.billingId = null;
        this.fetchGroupsForDeviceIdURL = null;
        this.fetchGroupsForUserURL = null;
    }
    
    @Override
    protected void initializeConfiguration(final JSONObject requestJson) throws Exception {
        if (requestJson.length() > 0) {
            this.logger.log(Level.SEVERE, "Initializing Maas configuration");
            super.initializeConfiguration(requestJson);
            final JSONObject customHeaderInfo = this.getAuthDetailsForAuthType(4, null).getJSONObject("CustomHeadersAuthInfo");
            this.billingId = (String)customHeaderInfo.get("billingid");
            final String apiServerURL = this.getAPIServerBaseURL();
            this.authorizationURL = apiServerURL + "/auth-apis/auth/1.0/authenticate/";
            this.checkStatusURl = apiServerURL + "/device-apis/devices/1.0/core/";
            this.removeDeviceURL = apiServerURL + "/device-apis/devices/1.0/removeDevice/";
            this.fetchDeviceURL = apiServerURL + "/device-apis/devices/1.0/search/";
            this.fetchUsersURL = apiServerURL + "/device-apis/user/1.0/search/";
            this.fetchGroupsURL = apiServerURL + "/group-apis/group/1.0/groups/customer/";
            this.fetchGroupsForDeviceIdURL = apiServerURL + "/device-apis/devices/1.0/searchByDeviceGroup/";
            this.fetchGroupsForUserURL = apiServerURL + "user-apis/user/1.0/searchByGroup/customer/";
        }
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        return this.getOtherAuthDetails(4);
    }
    
    @Override
    protected JSONObject handleManagementStatusCheckRequest(final JSONObject msgJson) {
        final JSONObject returnJson = new JSONObject();
        try {
            String managedStatus = "Managed";
            final String deviceID = msgJson.getString("deviceID");
            final boolean getNewKey = msgJson.optBoolean("getNewKey");
            this.getAuthorization(getNewKey);
            final JSONObject parameters = new JSONObject();
            parameters.put("deviceId", (Object)deviceID);
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.method = "GET";
            request.url = this.checkStatusURl + this.billingId;
            request.parameters = parameters;
            this.logger.log(Level.INFO, "Going to request device status API to MAAS 360 for device:{0}", new Object[] { deviceID });
            final JSONObject responseObject = this.executeHTTPRequest(request);
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            if (responseBodyObject.has("device")) {
                final JSONObject deviceObject = responseBodyObject.getJSONObject("device");
                if (deviceObject.has("maas360ManagedStatus")) {
                    final String status = deviceObject.getString("maas360ManagedStatus");
                    if (status.equalsIgnoreCase("Inactive")) {
                        final String udid = deviceObject.getString("udid");
                        managedStatus = "Unmanaged";
                        returnJson.put("NewEnrollmentURL", (Object)this.newEnrollmentURL);
                        returnJson.put("udid", (Object)udid);
                    }
                    returnJson.put("Status", (Object)managedStatus);
                }
                else {
                    this.logger.log(Level.INFO, "MAAS360 responded with an unknown reason:{0}", new Object[] { responseObject });
                    returnJson.put("Error", (Object)"100");
                    returnJson.put("ErrorMsg", (Object)"Unknown Error");
                }
            }
            else {
                final int statusCode = responseObject.getInt("StatusCode");
                if (statusCode == 401) {
                    if (this.handleUnauthorisedResponse(msgJson, returnJson)) {
                        return returnJson;
                    }
                    this.handleManagementStatusCheckRequest(msgJson);
                }
                else {
                    this.processFailureResponse(responseObject, returnJson);
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
            final boolean getNewKey = msgJson.optBoolean("getNewKey");
            this.getAuthorization(getNewKey);
            final JSONObject parameters = new JSONObject();
            parameters.put("deviceId", (Object)deviceID);
            final DMHttpRequest request = new DMHttpRequest();
            request.headers = this.headerObject;
            request.method = "POST";
            request.url = this.removeDeviceURL + this.billingId;
            request.parameters = parameters;
            this.logger.log(Level.INFO, "Going to request device removal API to MAAS 360 for device:{0}", new Object[] { deviceID });
            final JSONObject responseJSON = this.executeHTTPRequest(request);
            final JSONObject responsebodyObject = responseJSON.getJSONObject("ResponseJson");
            if (responsebodyObject.has("actionResponse")) {
                final JSONObject actionResponse = responsebodyObject.getJSONObject("actionResponse");
                if (actionResponse.has("actionStatus")) {
                    final int actionStatus = actionResponse.getInt("actionStatus");
                    if (actionStatus == 0) {
                        return this.handleManagementStatusCheckRequest(msgJson);
                    }
                    String error = "Unknown Error";
                    if (actionResponse.has("description")) {
                        error = actionResponse.getString("description");
                    }
                    returnJson.put("Error", (Object)"100");
                    returnJson.put("ErrorMsg", (Object)error);
                }
                else {
                    returnJson.put("Error", (Object)"100");
                    returnJson.put("ErrorMsg", (Object)"Unknown Error");
                }
            }
            else {
                final int statusCode = responseJSON.getInt("StatusCode");
                if (statusCode == 401) {
                    if (this.handleUnauthorisedResponse(msgJson, returnJson)) {
                        return returnJson;
                    }
                    this.handleUnmanageDeviceRequest(msgJson);
                }
                else {
                    this.processFailureResponse(responseJSON, returnJson);
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
        final JSONArray response = new JSONArray();
        final JSONArray requirements = MigrationConstants.MaaS360Requirements.requirementArrayList;
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            if (requirements.getString(i).equalsIgnoreCase("Billing Id")) {
                object.put("api_key", (Object)"billingid");
            }
            else if (requirements.getString(i).equalsIgnoreCase("App Id")) {
                object.put("api_key", (Object)"appid");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Version")) {
                object.put("api_key", (Object)"appversion");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Platform Id")) {
                object.put("api_key", (Object)"platformid");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Access Key")) {
                object.put("api_key", (Object)"appaccesskey");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Root URL")) {
                object.put("api_key", (Object)"Server_URL");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Username")) {
                object.put("api_key", (Object)"userName");
            }
            else if (requirements.getString(i).equalsIgnoreCase("Password")) {
                object.put("api_key", (Object)"password");
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
    
    private void processFailureResponse(final JSONObject responseJSON, final JSONObject returnJson) throws JSONException {
        final JSONObject responseHeader = responseJSON.getJSONObject("ResponseHeader");
        returnJson.put("Error", (Object)"100");
        returnJson.put("ErrorMsg", (Object)"Unknown Error");
        if (this.isApiLimitExceeded(responseHeader)) {
            returnJson.put("ErrorMsg", (Object)"API Limit reached try after sometime");
        }
    }
    
    public JSONObject prepareDeviceDBInsertObject(final JSONObject device) {
        final JSONObject deviceDetailsJSON = new JSONObject();
        deviceDetailsJSON.put("UDID", device.opt("udid"));
        deviceDetailsJSON.put("MIGRATION_SERVER_DEVICE_ID", device.opt("maas360DeviceID"));
        deviceDetailsJSON.put("IMEI", device.opt("imeiEsn"));
        deviceDetailsJSON.put("DEVICE_NAME", device.opt("deviceName"));
        deviceDetailsJSON.put("DEVICE_TYPE", device.opt("deviceType"));
        deviceDetailsJSON.put("MODEL", device.opt("model"));
        deviceDetailsJSON.put("OS", device.opt("osName"));
        deviceDetailsJSON.put("MANUFACTURER", device.opt("manufacturer"));
        return deviceDetailsJSON;
    }
    
    public JSONObject prepareUserDBInsertObject(final JSONObject user) {
        final JSONObject userDetailsJSON = new JSONObject();
        if (user.has("userName")) {
            userDetailsJSON.put("USER_NAME", user.get("userName"));
        }
        if (user.has("IS_AD_SYNCED_USER")) {
            userDetailsJSON.put("IS_AD_SYNCED_USER", true);
        }
        else {
            userDetailsJSON.put("IS_AD_SYNCED_USER", false);
        }
        if (user.has("userIdentifier")) {
            userDetailsJSON.put("MIGRATION_SERVER_USER_ID", user.get("userIdentifier"));
        }
        if (user.has("status")) {
            userDetailsJSON.put("STATUS", user.get("status"));
        }
        if (user.has("emailAddress")) {
            userDetailsJSON.put("EMAIL_ID", user.get("emailAddress"));
        }
        if (user.has("phoneNumber")) {
            userDetailsJSON.put("PHONE_NUMBER", (Object)user.get("phoneNumber").toString());
        }
        if (user.has("domain")) {
            userDetailsJSON.put("DOMAIN", user.get("domain"));
        }
        return userDetailsJSON;
    }
    
    public JSONObject prepareGroupDBInsertObject(final JSONObject group) {
        final JSONObject groupDetailsJSON = new JSONObject();
        if (group.has("groupName")) {
            groupDetailsJSON.put("GROUP_NAME", group.get("groupName"));
        }
        if (group.has("groupID")) {
            groupDetailsJSON.put("MIGRATION_SERVER_GROUP_ID", group.get("groupID"));
        }
        if (group.has("groupType")) {
            if (group.getInt("groupType") == 3) {
                groupDetailsJSON.put("GROUP_TYPE", 2);
            }
            else if (group.getInt("groupType") >= 0 && group.getInt("groupType") <= 2) {
                groupDetailsJSON.put("GROUP_TYPE", 1);
            }
        }
        return groupDetailsJSON;
    }
    
    @Override
    public JSONObject fetchAllDevices(final Long config_id, final int service_id, final Long customer_id, final Long user_id) throws JSONException {
        final JSONObject response = new JSONObject();
        final JSONArray fetchedDevices = new JSONArray();
        final JSONObject configJSON = new JSONObject();
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
            int pageNumber = 2;
            configJSON.put("CONFIG_ID", (Object)config_id);
            this.initializeConfiguration(configJSON);
            final JSONObject parameters = new JSONObject();
            parameters.put("pageSize", 500);
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL + this.billingId, parameters);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson").getJSONObject("devices");
            int count = responseBodyObject.optInt("count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_COUNT", (Object)count);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            if (responseBodyObject.get("device") instanceof JSONArray) {
                final JSONArray devices = responseBodyObject.getJSONArray("device");
                for (int i = 0; i < devices.length(); ++i) {
                    final JSONObject deviceDetailsJSON = this.prepareDeviceDBInsertObject(devices.getJSONObject(i));
                    deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                    deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                    try {
                        new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                    }
                    catch (final SyMException e) {
                        this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from MaaS360 API", e.toString());
                        continue;
                    }
                    apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                }
            }
            else if (responseBodyObject.get("device") instanceof JSONObject) {
                final JSONObject device = responseBodyObject.getJSONObject("device");
                final JSONObject deviceDetailsJSON = this.prepareDeviceDBInsertObject(device);
                deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                try {
                    new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                    apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                }
                catch (final Exception e2) {
                    this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from MaaS360 API", e2.toString());
                }
            }
            if (count > 500) {
                count -= 500;
                while (count > 0) {
                    parameters.put("pageNumber", pageNumber);
                    ++pageNumber;
                    count -= 500;
                    responseObject = this.executeAPIRequest("GET", this.fetchDeviceURL + this.billingId, parameters);
                    responseBodyObject = responseObject.getJSONObject("ResponseJson").getJSONObject("devices");
                    if (responseBodyObject.get("device") instanceof JSONArray) {
                        final JSONArray devices = responseBodyObject.getJSONArray("device");
                        for (int i = 0; i < devices.length(); ++i) {
                            final JSONObject deviceDetailsJSON = this.prepareDeviceDBInsertObject(devices.getJSONObject(i));
                            deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                            deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                            try {
                                new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                            }
                            catch (final SyMException e) {
                                this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from MaaS360 API", e.toString());
                                continue;
                            }
                            apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                            apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                        }
                    }
                    else if (responseBodyObject.get("device") instanceof JSONObject) {
                        final JSONObject device = response.getJSONObject("device");
                        final JSONObject deviceDetailsJSON = this.prepareDeviceDBInsertObject(device);
                        deviceDetailsJSON.put("CONFIG_ID", (Object)config_id);
                        apiServiceDataHandler.getRowForDevice(deviceDetailsJSON, customer_id);
                        deviceDetailsJSON.put("CustomerId", (Object)customer_id);
                        try {
                            new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceDetailsJSON, 50);
                            apiServiceDataHandler.addOrUpdateMigrationDevices(config_id);
                        }
                        catch (final SyMException e3) {
                            this.logger.log(Level.SEVERE, "Enrollment request failed due to insufficient data from MaaS360 API", e3.toString());
                        }
                    }
                    apiServiceDataHandler.sleepForThrottle();
                }
            }
            final JSONObject migrationSummary = new MigrationSummary().migrationCountSummary(config_id, customer_id);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2182, null, user_name, "mdm.migration.device.completed", migrationSummary.get("migrated_devices"), customer_id);
        }
        catch (final Exception e4) {
            this.logger.log(Level.SEVERE, "Exception on fetching devices", e4);
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
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        response.put("devices", (Object)fetchedDevices);
        return response;
    }
    
    public List fetchDevicesPerUser(final JSONObject user, final Long config_id, final Long customer_id) {
        final JSONObject parameters = new JSONObject();
        final List<Row> associationDataObject = new ArrayList<Row>();
        parameters.put("partialUsername", (Object)user.get("userName").toString());
        parameters.put("match", 1);
        final String userID = new APIServiceDataHandler().getMigrationUserIDForUserName(user.getString("userName"), config_id, customer_id);
        final JSONObject responseDeviceObject = this.executeAPIRequest("GET", this.fetchDeviceURL + this.billingId, parameters);
        final JSONObject responseDevice = responseDeviceObject.getJSONObject("ResponseJson").getJSONObject("devices");
        if (responseDevice.get("device") instanceof JSONArray) {
            final JSONArray devices = responseDevice.getJSONArray("device");
            for (int j = 0; j < devices.length(); ++j) {
                final JSONObject device = devices.getJSONObject(j);
                final Long deviceID = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device.getString("maas360DeviceID"), config_id, customer_id);
                final Row row = new Row("MigrationAssociation");
                row.set("CONFIG_ID", (Object)config_id);
                row.set("DEVICE_ID", (Object)deviceID);
                row.set("USER_ID", (Object)userID);
                associationDataObject.add(row);
            }
        }
        else if (responseDevice.get("device") instanceof JSONObject) {
            final JSONObject device2 = responseDevice.getJSONObject("device");
            final Long deviceID2 = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device2.getString("maas360DeviceID"), config_id, customer_id);
            final Row row2 = new Row("MigrationAssociation");
            row2.set("CONFIG_ID", (Object)config_id);
            row2.set("DEVICE_ID", (Object)deviceID2);
            row2.set("USER_ID", (Object)userID);
            associationDataObject.add(row2);
        }
        return associationDataObject;
    }
    
    @Override
    public void getAuthorization(final boolean getNewKey) throws Exception {
        XMLStreamWriter xmlStreamWriter = null;
        StringWriter writer = null;
        String authKeyHeader = null;
        try {
            final JSONObject basicDigestInfo = this.getAuthDetailsForAuthType(4, null).getJSONObject("BasicDigestAuthInfo");
            final JSONObject customHeaderInfo = this.getAuthDetailsForAuthType(4, null).getJSONObject("CustomHeadersAuthInfo");
            final JSONObject authInfo = this.getAuthDetailsForAuthType(4, null).getJSONObject("APIAuthInfo");
            final Long authID = (Long)authInfo.get("AUTH_ID".toLowerCase());
            boolean getAuthKey = true;
            if (customHeaderInfo.has("authKeyTime")) {
                final Long authKeyTime = Long.valueOf((String)customHeaderInfo.get("authKeyTime"));
                final Long currentMilliSec = System.currentTimeMillis();
                final Long keyThreshold = 3480000L;
                if (currentMilliSec - authKeyTime < keyThreshold) {
                    getAuthKey = false;
                }
            }
            if (getAuthKey || getNewKey) {
                final String userName = (String)basicDigestInfo.get("USERNAME".toLowerCase());
                final String passWord = (String)basicDigestInfo.get("PASSWORD".toLowerCase());
                final String appId = (String)customHeaderInfo.get("appid");
                final String appVersion = (String)customHeaderInfo.get("appversion");
                final String appAccessKey = (String)customHeaderInfo.get("appaccesskey");
                final String platformId = (String)customHeaderInfo.get("platformid");
                final String billing = (String)customHeaderInfo.get("billingid");
                writer = new StringWriter();
                final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
                xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement("authRequest");
                xmlStreamWriter.writeStartElement("maaS360AdminAuth");
                xmlStreamWriter.writeStartElement("platformID");
                xmlStreamWriter.writeCharacters(platformId);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("billingID");
                xmlStreamWriter.writeCharacters(billing);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("password");
                xmlStreamWriter.writeCharacters(passWord);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("userName");
                xmlStreamWriter.writeCharacters(userName);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("appID");
                xmlStreamWriter.writeCharacters(appId);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("appVersion");
                xmlStreamWriter.writeCharacters(appVersion);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeStartElement("appAccessKey");
                xmlStreamWriter.writeCharacters(appAccessKey);
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
                final String xmlString = writer.getBuffer().toString();
                final DMHttpRequest request = new DMHttpRequest();
                request.url = this.authorizationURL + billing;
                request.method = "POST";
                request.data = xmlString.getBytes();
                final JSONObject headerObject = new JSONObject();
                headerObject.put("Content-Type", (Object)"application/xml");
                headerObject.put("Accept", (Object)"application/json");
                request.headers = headerObject;
                final JSONObject httpResponse = this.executeHTTPRequest(request);
                final JSONObject responseBody = httpResponse.getJSONObject("ResponseJson");
                final JSONObject authResponse = responseBody.getJSONObject("authResponse");
                if (!authResponse.has("authToken")) {
                    this.logger.log(Level.INFO, "Error in getting authtoken");
                    this.isAuthorizationFailed = true;
                    new APIServiceDataHandler().setAuthorizationFailed(this.apiDetails.getJSONObject("APIServiceConfiguration").getLong("CONFIG_ID".toLowerCase()));
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
                authKeyHeader = authResponse.getString("authToken");
                final JSONObject authDetails = new JSONObject();
                authDetails.put("AUTH_ID", (Object)authID);
                authDetails.put("APIAuthInfo", (Object)authInfo);
                authDetails.put("BasicDigestAuthInfo", (Object)basicDigestInfo);
                authDetails.put("CustomHeadersAuthInfo", (Object)customHeaderInfo);
                basicDigestInfo.put("AUTHORIZATION_HEADER", (Object)authKeyHeader);
                customHeaderInfo.put("authKeyTime", (Object)String.valueOf(System.currentTimeMillis()));
                new APIServiceDataHandler().editAuthDetails(authDetails);
            }
            else {
                authKeyHeader = basicDigestInfo.getString("AUTHORIZATION_HEADER".toLowerCase());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetching authorization token from MaaS360", ex);
            throw ex;
        }
        finally {
            if (xmlStreamWriter != null) {
                xmlStreamWriter.flush();
                xmlStreamWriter.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        this.headerObject.put("Accept", (Object)"application/json");
        this.headerObject.put("Authorization", (Object)("MaaS token=\"" + authKeyHeader + "\""));
    }
    
    @Override
    public JSONObject fetchAllUsers(final Long config_id, final int service_id, final Long customer_id, final Long user_id) throws JSONException {
        final JSONObject response = new JSONObject();
        final JSONObject configJSON = new JSONObject();
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        int pageNumber = 2;
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
            final JSONObject parameters = new JSONObject();
            parameters.put("pageSize", 500);
            JSONObject responseObject = this.executeAPIRequest("GET", this.fetchUsersURL + this.billingId, parameters);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson").getJSONObject("users");
            int count = responseBodyObject.optInt("count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("USERS_COUNT", (Object)count);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            final DataObject userDataObject = (DataObject)new WritableDataObject();
            final DataObject associationDataObject = (DataObject)new WritableDataObject();
            if (responseBodyObject.get("user") instanceof JSONArray) {
                final JSONArray users = responseBodyObject.getJSONArray("user");
                for (int i = 0; i < users.length(); ++i) {
                    final JSONObject user = users.getJSONObject(i);
                    if (user.has("domain") && apiServiceDataHandler.isADConfigured(user.getString("domain"))) {
                        user.put("IS_AD_SYNCED_USER", true);
                        user.put("DOMAIN", (Object)user.getString("domain"));
                    }
                    final JSONObject userRowObject = this.prepareUserDBInsertObject(user);
                    userRowObject.put("CONFIG_ID", (Object)config_id);
                    final Row userRow = apiServiceDataHandler.getRowForUser(userRowObject, customer_id);
                    userDataObject.addRow(userRow);
                    apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                }
                for (int i = 0; i < users.length(); ++i) {
                    final JSONObject user = users.getJSONObject(i);
                    try {
                        final List<Row> deviceAssociation = this.fetchDevicesPerUser(user, config_id, customer_id);
                        for (final Row row2 : deviceAssociation) {
                            if (row2.get("DEVICE_ID") != null) {
                                associationDataObject.addRow(row2);
                            }
                        }
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, e, () -> "Error while fetching devices associated details for the user " + jsonObject.toString());
                    }
                }
            }
            else if (responseBodyObject.get("user") instanceof JSONObject) {
                final JSONObject user2 = responseBodyObject.getJSONObject("user");
                if (user2.has("domain") && apiServiceDataHandler.isADConfigured(user2.getString("domain"))) {
                    user2.put("IS_AD_SYNCED_USER", true);
                    user2.put("DOMAIN", (Object)user2.getString("domain"));
                }
                final JSONObject userRowObject2 = this.prepareUserDBInsertObject(user2);
                userRowObject2.put("CONFIG_ID", (Object)config_id);
                final Row userRow2 = apiServiceDataHandler.getRowForUser(userRowObject2, customer_id);
                userDataObject.addRow(userRow2);
                apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                try {
                    final List<Row> deviceAssociation = this.fetchDevicesPerUser(user2, config_id, customer_id);
                    for (final Row row2 : deviceAssociation) {
                        if (row2.get("DEVICE_ID") != null) {
                            associationDataObject.addRow(row2);
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, e, () -> "Error while fetching devices associated details for the user " + jsonObject2.toString());
                }
            }
            if (count > 500) {
                count -= 500;
                while (count > 0) {
                    parameters.put("pageNumber", pageNumber);
                    ++pageNumber;
                    count -= 500;
                    responseObject = this.executeAPIRequest("GET", this.fetchUsersURL + this.billingId, parameters);
                    responseBodyObject = responseObject.getJSONObject("ResponseJson").getJSONObject("users");
                    if (responseBodyObject.get("user") instanceof JSONArray) {
                        final JSONArray users = responseBodyObject.getJSONArray("user");
                        for (int i = 0; i < users.length(); ++i) {
                            final JSONObject user = users.getJSONObject(i);
                            if (user.has("domain") && apiServiceDataHandler.isADConfigured(user.getString("domain"))) {
                                user.put("IS_AD_SYNCED_USER", true);
                                user.put("DOMAIN", (Object)user.getString("domain"));
                            }
                            final JSONObject userRowObject = this.prepareUserDBInsertObject(user);
                            userRowObject.put("CONFIG_ID", (Object)config_id);
                            final Row userRow = apiServiceDataHandler.getRowForUser(userRowObject, customer_id);
                            userDataObject.addRow(userRow);
                        }
                        apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                        for (int i = 0; i < users.length(); ++i) {
                            final JSONObject user = users.getJSONObject(i);
                            try {
                                final List<Row> deviceAssociation = this.fetchDevicesPerUser(user, config_id, customer_id);
                                for (final Row row2 : deviceAssociation) {
                                    if (row2.get("DEVICE_ID") != null) {
                                        associationDataObject.addRow(row2);
                                    }
                                }
                            }
                            catch (final Exception e) {
                                this.logger.log(Level.SEVERE, e, () -> "Error while fetching devices associated details for the user " + jsonObject3.toString());
                            }
                        }
                    }
                    else if (responseBodyObject.get("user") instanceof JSONObject) {
                        final JSONObject user2 = responseBodyObject.getJSONObject("user");
                        if (user2.has("domain") && apiServiceDataHandler.isADConfigured(user2.getString("domain"))) {
                            user2.put("IS_AD_SYNCED_USER", true);
                            user2.put("DOMAIN", (Object)user2.getString("domain"));
                        }
                        final JSONObject userRowObject2 = this.prepareUserDBInsertObject(user2);
                        userRowObject2.put("CONFIG_ID", (Object)config_id);
                        final Row userRow2 = apiServiceDataHandler.getRowForUser(userRowObject2, customer_id);
                        userDataObject.addRow(userRow2);
                        apiServiceDataHandler.addOrUpdateMigrationUsers(config_id);
                        try {
                            final List<Row> deviceAssociation = this.fetchDevicesPerUser(user2, config_id, customer_id);
                            for (final Row row2 : deviceAssociation) {
                                if (row2.get("DEVICE_ID") != null) {
                                    associationDataObject.addRow(row2);
                                }
                            }
                        }
                        catch (final Exception e) {
                            this.logger.log(Level.SEVERE, e, () -> "Error while fetching devices associated details for the user " + jsonObject4.toString());
                        }
                    }
                    apiServiceDataHandler.sleepForThrottle();
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
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Error while fetching all users", e2);
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
    
    public DataObject fetchGroupsForDeviceId(final Long customer_id, final Long config_id) {
        final DataObject response = (DataObject)new WritableDataObject();
        try {
            final JSONObject parameters = new JSONObject();
            JSONArray groupIds = new APIServiceDataHandler().getAllMigratedGroupIds(config_id, 1, customer_id);
            for (int i = 0; i < groupIds.length(); ++i) {
                try {
                    final String groupId = groupIds.getString(i);
                    parameters.put("deviceGroupId", (Object)groupId);
                    final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchGroupsForDeviceIdURL + this.billingId, parameters);
                    JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    if (responseBodyObject.has("devices") && responseBodyObject.get("devices") instanceof JSONObject) {
                        responseBodyObject = responseBodyObject.getJSONObject("devices");
                        if (responseBodyObject.has("device") && responseBodyObject.get("device") instanceof JSONArray) {
                            final JSONArray devices = responseBodyObject.getJSONArray("device");
                            for (int j = 0; j < devices.length(); ++j) {
                                final JSONObject device = devices.getJSONObject(j);
                                final Long migrationServerDeviceId = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device.getString("maas360DeviceID"), config_id, customer_id);
                                final Long group_id = new APIServiceDataHandler().getResourceGroupIdForGroupId(groupId, customer_id);
                                if (migrationServerDeviceId != null && group_id != null) {
                                    final Row row = new Row("MigrationDeviceToGroup");
                                    row.set("CONFIG_ID", (Object)config_id);
                                    row.set("DEVICE_ID", (Object)migrationServerDeviceId);
                                    row.set("RESOURCE_GROUP_ID", (Object)group_id);
                                    response.addRow(row);
                                }
                            }
                        }
                        else if (responseBodyObject.has("device") && responseBodyObject.get("device") instanceof JSONObject) {
                            final JSONObject device2 = responseBodyObject.getJSONObject("device");
                            final Long migrationServerDeviceId2 = new APIServiceDataHandler().getDeviceIDForServerDeviceID(device2.getString("maas360DeviceID"), config_id, customer_id);
                            final Long group_id2 = new APIServiceDataHandler().getResourceGroupIdForGroupId(groupId, customer_id);
                            if (migrationServerDeviceId2 != null && group_id2 != null) {
                                final Row row2 = new Row("MigrationDeviceToGroup");
                                row2.set("CONFIG_ID", (Object)config_id);
                                row2.set("DEVICE_ID", (Object)migrationServerDeviceId2);
                                row2.set("RESOURCE_GROUP_ID", (Object)group_id2);
                                response.addRow(row2);
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Error while fetching device groups association details for group {0}", groupIds.getString(i));
                }
            }
            groupIds = new APIServiceDataHandler().getAllMigratedGroupIds(config_id, 2, customer_id);
            for (int i = 0; i < groupIds.length(); ++i) {
                try {
                    final String groupId = groupIds.getString(i);
                    final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchGroupsForUserURL + this.billingId + "/groupIdentifier/" + groupId, null);
                    JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    if (responseBodyObject.has("users") && responseBodyObject.get("users") instanceof JSONObject) {
                        responseBodyObject = responseBodyObject.getJSONObject("users");
                        if (responseBodyObject.has("user") && responseBodyObject.get("user") instanceof JSONArray) {
                            final JSONArray users = responseBodyObject.getJSONArray("user");
                            for (int j = 0; j < users.length(); ++j) {
                                final JSONObject user = users.getJSONObject(j);
                                final String maasServerUserId = user.getString("userIdentifier");
                                final Long userID = new APIServiceDataHandler().getUserIDForServerUserID(maasServerUserId, customer_id, config_id);
                                final Long group_id3 = new APIServiceDataHandler().getResourceGroupIdForGroupId(groupId, customer_id);
                                if (maasServerUserId != null && userID != null && group_id3 != null) {
                                    final Row row3 = new Row("MigrationUserToGroup");
                                    row3.set("CONFIG_ID", (Object)config_id);
                                    row3.set("USER_ID", (Object)userID);
                                    row3.set("RESOURCE_GROUP_ID", (Object)group_id3);
                                    response.addRow(row3);
                                }
                            }
                        }
                        else if (responseBodyObject.has("user") && responseBodyObject.get("user") instanceof JSONObject) {
                            final JSONObject user2 = responseBodyObject.getJSONObject("user");
                            final String maasServerUserId2 = user2.getString("userIdentifier");
                            final Long userID2 = new APIServiceDataHandler().getUserIDForServerUserID(maasServerUserId2, customer_id, config_id);
                            final Long group_id4 = new APIServiceDataHandler().getResourceGroupIdForGroupId(groupId, customer_id);
                            if (maasServerUserId2 != null && userID2 != null && group_id4 != null) {
                                final Row row4 = new Row("MigrationUserToGroup");
                                row4.set("CONFIG_ID", (Object)config_id);
                                row4.set("USER_ID", (Object)userID2);
                                row4.set("RESOURCE_GROUP_ID", (Object)group_id4);
                                response.addRow(row4);
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Error while fetching user groups association details for group {0}", groupIds.getString(i));
                }
            }
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while preparing device group association data object", e2);
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
            final JSONObject responseObject = this.executeAPIRequest("GET", this.fetchGroupsURL + this.billingId, null);
            final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson").getJSONObject("groups");
            final int count = responseBodyObject.optInt("count");
            row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("GROUPS_COUNT", (Object)count);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            if (responseBodyObject.get("group") instanceof JSONArray) {
                final JSONArray groups = responseBodyObject.getJSONArray("group");
                for (int i = 0; i < groups.length(); ++i) {
                    try {
                        final JSONObject group = groups.getJSONObject(i);
                        final JSONObject groupDBObject = this.prepareGroupDBInsertObject(group);
                        groupDBObject.put("CONFIG_ID", (Object)config_id);
                        apiServiceDataHandler.getRowForGroup(groupDBObject, customer_id);
                        apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Error while adding group details to MigrationGroups table", e);
                    }
                }
                for (int i = 0; i < groups.length(); ++i) {
                    try {
                        final JSONObject group = groups.getJSONObject(i);
                        final MDMCustomGroupDetails cgDetails = new MDMCustomGroupDetails();
                        if (group.getInt("groupType") == 3) {
                            cgDetails.groupType = 7;
                        }
                        else if (group.getInt("groupType") <= 2 && group.getInt("groupType") >= 0) {
                            cgDetails.groupType = 6;
                        }
                        cgDetails.platformType = 0;
                        cgDetails.groupCategory = 1;
                        cgDetails.customerId = customer_id;
                        cgDetails.domainName = "MDM";
                        cgDetails.groupPlatformType = 0;
                        cgDetails.groupName = group.getString("groupName");
                        MDMGroupHandler.getInstance().addGroup(cgDetails);
                        apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group.getString("groupName"));
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Error while adding group details to resource table", e);
                    }
                }
            }
            else {
                try {
                    final JSONObject group2 = responseBodyObject.getJSONObject("group");
                    final JSONObject groupDBObject2 = this.prepareGroupDBInsertObject(group2);
                    groupDBObject2.put("CONFIG_ID", (Object)config_id);
                    apiServiceDataHandler.getRowForGroup(groupDBObject2, customer_id);
                    apiServiceDataHandler.addOrUpdateMigrationGroups(config_id);
                    final MDMCustomGroupDetails cgDetails2 = new MDMCustomGroupDetails();
                    if (group2.getInt("groupType") == 3) {
                        cgDetails2.groupType = 7;
                    }
                    else if (group2.getInt("groupType") <= 2 && group2.getInt("groupType") >= 0) {
                        cgDetails2.groupType = 6;
                    }
                    cgDetails2.platformType = 0;
                    cgDetails2.groupCategory = 1;
                    cgDetails2.customerId = CustomerInfoUtil.getInstance().getCustomerId();
                    cgDetails2.domainName = "MDM";
                    cgDetails2.groupPlatformType = 0;
                    cgDetails2.groupName = group2.getString("groupName");
                    MDMGroupHandler.getInstance().addGroup(cgDetails2);
                    apiServiceDataHandler.updateGroupLastModifiedBy(customer_id, user_id, group2.getString("groupName"));
                }
                catch (final Exception e2) {
                    this.logger.log(Level.SEVERE, "Error while adding groups details ", e2);
                }
            }
            final DataObject associationDataObject = this.fetchGroupsForDeviceId(customer_id, config_id);
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
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception while fetching all groups", e3);
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
    
    private boolean isApiLimitExceeded(final JSONObject headerObject) {
        final boolean limitExceeded = false;
        try {
            if (headerObject.has("X-Rate-Limit-Remaining")) {
                final int count = (int)headerObject.get("X-Rate-Limit-Remaining");
                final int resetWindow = (int)headerObject.get("X-Rate-Limit-ResetWindow");
                if (count == 0) {
                    this.logger.log(Level.INFO, "Next Api Available sec:{0}", new Object[] { resetWindow });
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in API Limit", e);
        }
        return limitExceeded;
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
