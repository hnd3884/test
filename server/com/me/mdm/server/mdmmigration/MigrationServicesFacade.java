package server.com.me.mdm.server.mdmmigration;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.ArrayList;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;
import java.util.Collection;
import com.me.mdm.mdmmigration.MigrationSummary;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.Properties;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.mdm.mdmmigration.MigrationAPIUtilities;
import java.util.Map;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MigrationServicesFacade
{
    public static final Logger logger;
    
    protected JSONObject getMsgBody(final JSONObject msgJson) throws JSONException {
        JSONObject msgContent = new JSONObject();
        final String msgContentString = JSONUtil.optString(msgJson, "msg_body");
        if (msgContentString != null) {
            msgContent = new JSONObject(msgContentString);
        }
        return msgContent;
    }
    
    public JSONObject getMigrationStatus(final JSONObject request) {
        final JSONObject response = new JSONObject();
        final Long config_id = APIUtil.getResourceID(request, "synchronizeal_id");
        final Long customer_id = APIUtil.getCustomerID(request);
        response.put("Status", (Object)new APIServiceDataHandler().getMigrationStatus(config_id, customer_id));
        return response;
    }
    
    public void syncAllData(final Map request) {
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        final Row statusRow = new Row("MDMServerMigrationStatus");
        statusRow.set("CONFIG_ID", (Object)config_id);
        try {
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_STATUS", (Object)1);
            row.set("USERS_STATUS", (Object)1);
            row.set("GROUPS_STATUS", (Object)1);
            row.set("PROFILES_STATUS", (Object)1);
            row.set("APPS_STATUS", (Object)1);
            row.set("MIGRATED_DEVICES_COUNT", (Object)0);
            row.set("MIGRATED_USERS_COUNT", (Object)0);
            row.set("MIGRATED_GROUPS_COUNT", (Object)0);
            row.set("MIGRATED_PROFILES_COUNT", (Object)0);
            row.set("MIGRATED_APPS_COUNT", (Object)0);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
            if (service_id == 3) {
                this.setOrganizationalGroups(request);
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[] { "Error while updating migration status" });
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, Long>)properties).put("user_id", user_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_ALL");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.mdmmigration.MigrationFetchTask", taskInfoMap, properties);
    }
    
    public JSONObject getMigrationSummary(final JSONObject request) {
        final JSONObject response = new JSONObject();
        final Long config_id = APIUtil.getResourceID(request, "summar_id");
        final Long customer_id = APIUtil.getCustomerID(request);
        response.put("devices_summary", (Object)new MigrationSummary().devicesSummary(request));
        response.put("users_summary", (Object)new MigrationSummary().usersSummary(request));
        response.put("groups_summary", (Object)new MigrationSummary().groupsSummary(request));
        response.put("profiles_summary", (Collection)new MigrationSummary().profilesSummary(request));
        response.put("apps_summary", (Collection)new MigrationSummary().appsSummary(request));
        response.put("count_summary", (Object)new MigrationSummary().migrationCountSummary(config_id, customer_id));
        return response;
    }
    
    public JSONObject getProductConfigurationInformation(final JSONObject request) {
        final JSONObject response = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(request);
        final int service_id = Integer.parseInt(APIUtil.getResourceID(request, "serviceconfigdetail_id").toString());
        response.put("configuration_details_required", (Object)MigrationAPIRequestHandler.getInstance(service_id).configurationDetails());
        response.put("auth", (Object)MigrationAPIRequestHandler.getInstance(service_id).getAuthenticationType());
        response.put("migration_status", (Object)new APIServiceDataHandler().getMigrationStatusForServiceId(customerId, service_id));
        return response;
    }
    
    public JSONObject getAllOrganizations(final JSONObject request) throws Exception {
        final JSONObject response = new JSONObject();
        final Long config_id = APIUtil.getResourceID(request, "fetchorganization_id");
        final Long customer_id = APIUtil.getCustomerID(request);
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        response.put("organizations", (Object)MigrationAPIRequestHandler.getInstance(service_id).fetchOrganizationDetails(config_id, service_id, customer_id));
        return response;
    }
    
    public void setOrganizationalGroups(final Map request) throws Exception {
        final ArrayList orgIds = request.get("org_ids");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MigrationOrganizations");
        updateQuery.setUpdateColumn("TO_BE_MIGRATED", (Object)true);
        updateQuery.setCriteria(new Criteria(new Column("MigrationOrganizations", "OLD_SERVER_ORG_ID"), (Object)orgIds.toArray(), 8));
        DataAccess.update(updateQuery);
    }
    
    public JSONObject getAllDevices(final Map request) throws JSONException, SyMException {
        final JSONObject response = new JSONObject();
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        try {
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("DEVICES_STATUS", (Object)1);
            row.set("MIGRATED_DEVICES_COUNT", (Object)0);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[] { "Error while updating migration status for fetch all devices" });
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_DEVICES");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2181, null, user_name, "mdm.migration.device.started", null, customer_id);
        this.startMigration(properties);
        return response.put("Status", new APIServiceDataHandler().getMigrationStatus(config_id, customer_id).get("devices_migrated"));
    }
    
    public JSONObject getAllGroups(final Map request) throws JSONException, SyMException {
        final JSONObject response = new JSONObject();
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        try {
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("GROUPS_STATUS", (Object)1);
            row.set("MIGRATED_GROUPS_COUNT", (Object)0);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[] { "Error while updating migration status" });
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, Long>)properties).put("user_id", user_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_GROUPS");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2185, null, user_name, "mdm.migration.groups.started", null, customer_id);
        this.startMigration(properties);
        response.put("Status", new APIServiceDataHandler().getMigrationStatus(config_id, customer_id).get("groups_migrated"));
        return response;
    }
    
    public JSONObject getAllProfiles(final Map request) throws SyMException {
        final JSONObject response = new JSONObject();
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, Long>)properties).put("user_id", user_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_PROFILES");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2187, null, user_name, "mdm.migration.profiles.started", null, customer_id);
        this.startMigration(properties);
        return response.put("Status", new APIServiceDataHandler().getMigrationStatus(config_id, customer_id).get("profiles_migrated"));
    }
    
    public JSONObject getAllApps(final Map request) throws SyMException {
        final JSONObject response = new JSONObject();
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, Long>)properties).put("user_id", user_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_APPS");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2189, null, user_name, "mdm.migration.apps.started", null, customer_id);
        this.startMigration(properties);
        return response.put("Status", new APIServiceDataHandler().getMigrationStatus(config_id, customer_id).get("apps_migrated"));
    }
    
    public JSONObject getAllUsers(final Map request) throws JSONException, SyMException {
        final JSONObject response = new JSONObject();
        final Long config_id = Long.valueOf(request.get("config_id").toString());
        final Long customer_id = Long.valueOf(request.get("customer_id").toString());
        final Long user_id = Long.valueOf(request.get("user_id").toString());
        final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
        try {
            final Row statusRow = new Row("MDMServerMigrationStatus");
            statusRow.set("CONFIG_ID", (Object)config_id);
            final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
            final Row row = dataObject.getRow("MDMServerMigrationStatus");
            row.set("USERS_STATUS", (Object)1);
            row.set("MIGRATED_USERS_COUNT", (Object)0);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[] { "Error while updating migration status" });
        }
        final Properties properties = new Properties();
        ((Hashtable<String, Long>)properties).put("config_id", config_id);
        ((Hashtable<String, Integer>)properties).put("service_id", service_id);
        ((Hashtable<String, Long>)properties).put("customer_id", customer_id);
        ((Hashtable<String, Long>)properties).put("user_id", user_id);
        ((Hashtable<String, String>)properties).put("type", "FETCH_USERS");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "MigrationFetchTask");
        taskInfoMap.put("schedulerTime", MDMUtil.getCurrentTimeInMillis());
        taskInfoMap.put("poolName", "asynchThreadPool");
        final String user_name = DMUserHandler.getUserNameFromUserID(user_id);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2183, null, user_name, "mdm.migration.users.started", null, customer_id);
        this.startMigration(properties);
        response.put("Status", new APIServiceDataHandler().getMigrationStatus(config_id, customer_id).get("users_migrated"));
        return response;
    }
    
    public JSONObject getAPIServices() throws DataAccessException, JSONException {
        return new APIServiceDataHandler().getAPIServicesList();
    }
    
    public JSONObject getAPIServiceConfigurationsList(final JSONObject json) throws DataAccessException, JSONException {
        final Long customer_id = APIUtil.getCustomerID(json);
        return new APIServiceDataHandler().getAPIServiceConfigurationsList(customer_id);
    }
    
    public JSONObject getAPIServiceConfigDetails(final JSONObject json) throws JSONException {
        final JSONObject jsonMsg = new JSONObject();
        final int serviceId = APIUtil.getIntegerFilter(json, "service_id");
        final int type = APIUtil.getIntegerFilter(json, "type");
        jsonMsg.put("CONFIG_ID", (Object)APIUtil.getResourceID(json, "serviceconfig_id"));
        jsonMsg.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(json));
        if (serviceId != -1) {
            jsonMsg.put("SERVICE_ID", serviceId);
        }
        if (type != -1) {
            jsonMsg.put("TYPE", type);
        }
        return new APIServiceDataHandler().getAPIServiceConfigDetails(jsonMsg);
    }
    
    public JSONObject changeCustomHeaders(final JSONArray customHeaderAuthInfo) throws JSONException {
        final JSONObject customHeaderNew = new JSONObject();
        for (int i = 0; i < customHeaderAuthInfo.length(); ++i) {
            final JSONObject customHeaders = customHeaderAuthInfo.getJSONObject(i);
            customHeaderNew.put(customHeaders.getString("KEY"), (Object)customHeaders.getString("VALUE"));
        }
        return customHeaderNew;
    }
    
    public JSONObject changeKeyCase(final JSONObject jsonNew) throws JSONException {
        final JSONObject apiserviceConfig = jsonNew.getJSONObject("APIServiceConfiguration".toUpperCase());
        jsonNew.remove("APIServiceConfiguration".toUpperCase());
        jsonNew.put("APIServiceConfiguration", (Object)apiserviceConfig);
        final JSONArray apiAuthDetails = jsonNew.getJSONArray("APIServiceAuthDetails".toUpperCase());
        final JSONArray apiAuthDetailsNew = new JSONArray();
        for (int i = 0; i < apiAuthDetails.length(); ++i) {
            final JSONObject apiAuthDetail = apiAuthDetails.getJSONObject(i);
            final JSONObject apiAuthInfo = apiAuthDetail.getJSONObject("APIAuthInfo".toUpperCase());
            final JSONObject apiAuthDetailNew = new JSONObject();
            apiAuthDetailNew.put("APIAuthInfo", (Object)apiAuthInfo);
            if (apiAuthDetail.has("BasicDigestAuthInfo".toUpperCase())) {
                final JSONObject basicDigestAuthInfo = apiAuthDetail.getJSONObject("BasicDigestAuthInfo".toUpperCase());
                apiAuthDetailNew.put("BasicDigestAuthInfo", (Object)basicDigestAuthInfo);
            }
            if (apiAuthDetail.has("CustomHeadersAuthInfo".toUpperCase())) {
                final JSONArray customHeaderAuthInfo = apiAuthDetail.getJSONArray("CustomHeadersAuthInfo".toUpperCase());
                final JSONObject customHeaderNew = this.changeCustomHeaders(customHeaderAuthInfo);
                apiAuthDetailNew.put("CustomHeadersAuthInfo", (Object)customHeaderNew);
            }
            apiAuthDetailsNew.put(i, (Object)apiAuthDetailNew);
        }
        jsonNew.remove("APIServiceAuthDetails".toUpperCase());
        jsonNew.put("APIServiceAuthDetails", (Object)apiAuthDetailsNew);
        return jsonNew;
    }
    
    public JSONObject changeKeyCaseForEdit(final JSONObject jsonNew) throws JSONException {
        final JSONObject apiserviceConfig = jsonNew.getJSONObject("APIServiceConfiguration".toUpperCase());
        jsonNew.remove("APIServiceConfiguration".toUpperCase());
        jsonNew.put("APIServiceConfiguration", (Object)apiserviceConfig);
        final JSONArray apiAuthDetails = jsonNew.getJSONArray("APIServiceAuthDetails".toUpperCase());
        final JSONArray apiAuthDetailsNew = new JSONArray();
        for (int i = 0; i < apiAuthDetails.length(); ++i) {
            final JSONObject apiAuthDetail = apiAuthDetails.getJSONObject(i);
            final String authID = apiAuthDetail.get("AUTH_ID").toString();
            final JSONObject apiAuthInfo = apiAuthDetail.getJSONObject("APIAuthInfo".toUpperCase());
            final JSONObject apiAuthDetailNew = new JSONObject();
            if (apiAuthDetail.has("BasicDigestAuthInfo".toUpperCase())) {
                final JSONObject basicDigestAuthInfo = apiAuthDetail.getJSONObject("BasicDigestAuthInfo".toUpperCase());
                apiAuthDetailNew.put("BasicDigestAuthInfo", (Object)basicDigestAuthInfo);
            }
            if (apiAuthDetail.has("CustomHeadersAuthInfo".toUpperCase())) {
                final JSONArray customHeaderAuthInfo = apiAuthDetail.getJSONArray("CustomHeadersAuthInfo".toUpperCase());
                final JSONObject customHeaderNew = this.changeCustomHeaders(customHeaderAuthInfo);
                apiAuthDetailNew.put("CustomHeadersAuthInfo", (Object)customHeaderNew);
            }
            apiAuthDetailNew.put("APIAuthInfo", (Object)apiAuthInfo);
            apiAuthDetailNew.put("AUTH_ID", (Object)authID);
            apiAuthDetailsNew.put(i, (Object)apiAuthDetailNew);
        }
        jsonNew.remove("APIServiceAuthDetails".toUpperCase());
        jsonNew.put("APIServiceAuthDetails", (Object)apiAuthDetailsNew);
        return jsonNew;
    }
    
    public JSONObject addServiceConfig(final JSONObject json) throws Exception {
        final Long customerID = APIUtil.getCustomerID(json);
        final JSONObject msgBody = this.getMsgBody(json);
        JSONObject jsonNew = new APIServiceDataHandler().convertKeysToUpperCase(msgBody);
        jsonNew = this.changeKeyCase(jsonNew);
        return new APIServiceDataHandler().addOrUpdateServiceConfig(jsonNew, customerID, null);
    }
    
    public String getEnrollmentURL(final Long customerID) {
        return null;
    }
    
    public JSONObject editServiceConfig(final JSONObject json) throws Exception {
        final JSONObject msgBody = this.getMsgBody(json);
        final Long customerID = APIUtil.getCustomerID(json);
        JSONObject jsonNew = new APIServiceDataHandler().convertKeysToUpperCase(msgBody);
        jsonNew = this.changeKeyCaseForEdit(jsonNew);
        final Long configID = APIUtil.getResourceID(json, "serviceconfig_id");
        return new APIServiceDataHandler().addOrUpdateServiceConfig(jsonNew, customerID, configID);
    }
    
    public JSONObject deleteServiceConfig(final JSONObject json) throws JSONException {
        final JSONObject jsonMsg = new JSONObject();
        jsonMsg.put("CONFIG_ID", (Object)APIUtil.getResourceID(json, "serviceconfig_id"));
        return new APIServiceDataHandler().deleteServiceConfig(jsonMsg);
    }
    
    public JSONObject getWebClipURL(final JSONObject json) throws Exception {
        final Long configID = APIUtil.getResourceID(json, "serviceconfig_id");
        final Long customer_id = APIUtil.getCustomerID(json);
        return new APIServiceDataHandler().getWebClipURL(configID, customer_id);
    }
    
    public void processMigrationRequest(final JSONObject requestJSON, final String type, final Long config_id) {
        try {
            final Long userId = APIUtil.getUserID(requestJSON);
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            final JSONObject bodyJSON = requestJSON.optJSONObject("msg_body");
            HashMap queueData;
            if (bodyJSON != null) {
                queueData = JSONUtil.convertJSONtoMap(bodyJSON);
            }
            else {
                queueData = new HashMap();
            }
            queueData.put("customer_id", String.valueOf(customerId));
            queueData.put("user_id", String.valueOf(userId));
            queueData.put("config_id", String.valueOf(config_id));
            queueData.put("type", type);
            this.addToQueue(queueData);
        }
        catch (final Exception e) {
            MigrationServicesFacade.logger.log(Level.SEVERE, "processMigrationRequest()    error, ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addToQueue(final HashMap dataToQueue) {
        try {
            final DCQueue queue = DCQueueHandler.getQueue("me-server-migration-processor");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = dataToQueue;
            queue.addToQueue(queueData);
            MigrationServicesFacade.logger.log(Level.INFO, "Added the migration request to queue migration type:{0} request id:{1}", new Object[] { dataToQueue.toString() });
        }
        catch (final Exception e) {
            MigrationServicesFacade.logger.log(Level.SEVERE, "Exception in adding to queue", e);
        }
    }
    
    public void startMigration(final Properties properties) {
        final Long config_id = Long.parseLong(((Hashtable<K, Object>)properties).get("config_id").toString());
        final int service_id = Integer.parseInt(((Hashtable<K, Object>)properties).get("service_id").toString());
        final Long customer_id = Long.parseLong(((Hashtable<K, Object>)properties).get("customer_id").toString());
        final Long user_id = Long.parseLong(((Hashtable<K, Object>)properties).get("user_id").toString());
        final String type = ((Hashtable<K, Object>)properties).get("type").toString();
        if (type.equalsIgnoreCase("FETCH_ALL")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllDevices(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "DEVICES_STATUS");
            }
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllUsers(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllGroups(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllApps(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllProfiles(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "GROUPS_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_DEVICES")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllDevices(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "DEVICES_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_USERS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllUsers(config_id, service_id, customer_id, user_id);
        }
        else if (type.equalsIgnoreCase("FETCH_GROUPS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllGroups(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "GROUPS_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_APPS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllApps(config_id, service_id, customer_id, user_id);
        }
        else if (type.equalsIgnoreCase("FETCH_PROFILES")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllProfiles(config_id, service_id, customer_id, user_id);
        }
    }
    
    public JSONObject getMigrationProceedDetails(final JSONObject request) throws Exception {
        final JSONObject response = new JSONObject();
        final Long customer_id = APIUtil.getCustomerID(request);
        boolean showMigrationDetails = Boolean.FALSE;
        final String showMigrationDetailsParam = CustomerParamsHandler.getInstance().getParameterValue("showMigrationDetails", (long)customer_id);
        if (!MDMUtil.isStringEmpty(showMigrationDetailsParam)) {
            showMigrationDetails = Boolean.TRUE;
        }
        response.put("showMigrationDetails", showMigrationDetails);
        return response;
    }
    
    public void updateMigrationProceedDetails(final JSONObject request) throws Exception {
        final Long customer_id = APIUtil.getCustomerID(request);
        final JSONObject body = request.getJSONObject("msg_body");
        final boolean showMigrationDetails = body.getBoolean("showmigrationdetails");
        CustomerParamsHandler.getInstance().addOrUpdateParameter("showMigrationDetails", String.valueOf(showMigrationDetails), (long)customer_id);
    }
    
    static {
        logger = Logger.getLogger("MDMMigrationLogger");
    }
}
