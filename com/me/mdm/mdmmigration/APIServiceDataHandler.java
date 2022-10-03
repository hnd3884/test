package com.me.mdm.mdmmigration;

import java.util.Hashtable;
import java.util.LinkedList;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.server.profiles.ProfileFacade;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.Properties;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.NotSupportedException;
import com.me.mdm.api.APIUtil;
import java.util.ArrayList;
import java.net.URL;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.concurrent.TimeUnit;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccess;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class APIServiceDataHandler
{
    private static Logger logger;
    private DataObject existingMigratedDevices;
    private DataObject existingMigratedUsers;
    private DataObject existingMigratedGroups;
    
    public APIServiceDataHandler() {
        this.existingMigratedDevices = null;
        this.existingMigratedUsers = null;
        this.existingMigratedGroups = null;
    }
    
    public JSONObject addAuthDetails(final JSONObject json) throws DataAccessException, JSONException {
        final DataObject dO = DataAccess.constructDataObject();
        this.addAuthDetails(json, dO);
        DataAccess.update(dO);
        final JSONArray authDetailArray = this.getAuthDetailsFromDO(dO);
        if (authDetailArray.length() > 0) {
            return authDetailArray.getJSONObject(0);
        }
        return new JSONObject();
    }
    
    public JSONObject convertKeysToUpperCase(final JSONObject json) throws JSONException {
        final JSONObject result = new JSONObject();
        final Iterator<String> keyIterator = json.keys();
        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();
            Object tempObj = null;
            if (json.get(key) instanceof JSONObject) {
                tempObj = this.convertKeysToUpperCase(json.getJSONObject(key));
            }
            else if (json.get(key) instanceof JSONArray) {
                tempObj = this.convertKeysToUpperCase(json.getJSONArray(key));
            }
            else {
                result.put(key.toUpperCase(), json.get(key));
            }
            if (tempObj != null) {
                result.put(key.toUpperCase(), tempObj);
            }
        }
        return result;
    }
    
    public JSONArray convertKeysToUpperCase(final JSONArray json) throws JSONException {
        final JSONArray result = new JSONArray();
        for (int i = 0; i < json.length(); ++i) {
            if (json.get(i) instanceof JSONObject) {
                result.put((Object)this.convertKeysToUpperCase(json.getJSONObject(i)));
            }
            else if (json.get(i) instanceof JSONArray) {
                result.put((Object)this.convertKeysToUpperCase(json.getJSONArray(i)));
            }
            else {
                result.put(json.get(i));
            }
        }
        return result;
    }
    
    private Criteria getCustomerIdMatchCriteria(final Long customer_id) {
        return new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0);
    }
    
    private Criteria getConfigIdMatchCriteria(final Long config_id, final String tableName) {
        return new Criteria(new Column(tableName, "CONFIG_ID"), (Object)config_id, 0);
    }
    
    private Criteria getCustomerIDAndConfigIdCriteria(final Long customer_id, final Long config_id, final String tableName) {
        return this.getConfigIdMatchCriteria(config_id, tableName).and(this.getCustomerIdMatchCriteria(customer_id));
    }
    
    private Join getCustomerAPIServiceConfigJoin(final String tableName) {
        return new Join(tableName, "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1);
    }
    
    public void sleepForThrottle() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10L);
    }
    
    public JSONArray getAllMigratedServerDeviceIDs(final Long config_id, final Long customerId) {
        final JSONArray response = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDevices"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customerId, config_id, "MigrationDevices"));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationDevices");
            while (iterator.hasNext()) {
                final Row device = iterator.next();
                response.put((Object)device.get("MIGRATION_SERVER_DEVICE_ID").toString());
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching migrated devices", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONArray getAllMigratedGroupIds(final Long config_id, final int groupType, final Long customer_id) {
        final JSONArray response = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationGroups"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationGroups"));
            if (groupType == 1) {
                selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationGroups").and(new Criteria(new Column("MigrationGroups", "GROUP_TYPE"), (Object)1, 0)));
            }
            else if (groupType == 2) {
                selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationGroups").and(new Criteria(new Column("MigrationGroups", "GROUP_TYPE"), (Object)2, 0)));
            }
            else {
                selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationGroups"));
            }
            selectQuery.addSelectColumn(new Column("MigrationGroups", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationGroups");
            while (iterator.hasNext()) {
                final Row groupsRow = iterator.next();
                response.put(groupsRow.get("MIGRATION_SERVER_GROUP_ID"));
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while getting group ids", e);
        }
        return response;
    }
    
    public String getMigrationStatusDescription(final int status) {
        switch (status) {
            case 1: {
                return "YetToStart";
            }
            case 2: {
                return "InProgress";
            }
            case 3: {
                return "SuccessfullyCompleted";
            }
            case 4: {
                return "Failed";
            }
            default: {
                return "";
            }
        }
    }
    
    public JSONObject getMigrationStatusAsResponse(final int devices_status, final int users_status, final int groups_status, final int profiles_status, final int apps_status) {
        final JSONObject response = new JSONObject();
        JSONObject statusResponse = new JSONObject();
        statusResponse.put("FETCH_DEVICES", devices_status);
        statusResponse.put("remarks", (Object)this.getMigrationStatusDescription(devices_status));
        response.put("devices_migrated", (Object)statusResponse);
        statusResponse = new JSONObject();
        statusResponse.put("FETCH_USERS", users_status);
        statusResponse.put("remarks", (Object)this.getMigrationStatusDescription(users_status));
        response.put("users_migrated", (Object)statusResponse);
        statusResponse = new JSONObject();
        statusResponse.put("FETCH_GROUPS", groups_status);
        statusResponse.put("remarks", (Object)this.getMigrationStatusDescription(groups_status));
        response.put("groups_migrated", (Object)statusResponse);
        statusResponse = new JSONObject();
        statusResponse.put("FETCH_PROFILES", profiles_status);
        statusResponse.put("remarks", (Object)this.getMigrationStatusDescription(profiles_status));
        response.put("profiles_migrated", (Object)statusResponse);
        statusResponse = new JSONObject();
        statusResponse.put("FETCH_APPS", apps_status);
        statusResponse.put("remarks", (Object)this.getMigrationStatusDescription(apps_status));
        response.put("apps_migrated", (Object)statusResponse);
        return response;
    }
    
    public JSONObject getMigrationStatus(final Long config_id, final Long customer_id) {
        JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMServerMigrationStatus"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MDMServerMigrationStatus"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MDMServerMigrationStatus"));
            selectQuery.addSelectColumn(new Column("MDMServerMigrationStatus", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Row status = dataObject.getRow("MDMServerMigrationStatus");
            final int devices_status = Integer.parseInt(status.get("DEVICES_STATUS").toString());
            final int users_status = Integer.parseInt(status.get("USERS_STATUS").toString());
            final int groups_status = Integer.parseInt(status.get("GROUPS_STATUS").toString());
            final int profiles_status = Integer.parseInt(status.get("PROFILES_STATUS").toString());
            final int apps_status = Integer.parseInt(status.get("APPS_STATUS").toString());
            response = this.getMigrationStatusAsResponse(devices_status, users_status, groups_status, profiles_status, apps_status);
            if (devices_status == 4) {
                final String errorMsg = (String)status.get("FETCH_DEVICES_ERRORS");
                if (errorMsg != null && errorMsg.equals("Invalid API service configuration details")) {
                    response.put("migration_error_msg", (Object)errorMsg);
                    response.put("migration_error_code", 36001);
                }
                else {
                    response.put("migration_device_error", (Object)errorMsg);
                }
            }
            if (users_status == 4) {
                final String errorMsg = (String)status.get("FETCH_USERS_ERRORS");
                if (errorMsg != null && errorMsg.equals("Invalid API service configuration details")) {
                    response.put("migration_error_msg", (Object)errorMsg);
                    response.put("migration_error_code", 36001);
                }
                else {
                    response.put("migration_user_error", (Object)errorMsg);
                }
            }
            if (groups_status == 4) {
                final String errorMsg = (String)status.get("FETCH_GROUPS_ERRORS");
                if (errorMsg != null && errorMsg.equals("Invalid API service configuration details")) {
                    response.put("migration_error_msg", (Object)errorMsg);
                    response.put("migration_error_code", 36001);
                }
                else {
                    response.put("migration_group_error", (Object)errorMsg);
                }
            }
            if (profiles_status == 4) {
                final String errorMsg = (String)status.get("FETCH_PROFILES_ERRORS");
                if (errorMsg != null && errorMsg.equals("Invalid API service configuration details")) {
                    response.put("migration_error_msg", (Object)errorMsg);
                    response.put("migration_error_code", 36001);
                }
                else {
                    response.put("migration_profile_error", (Object)errorMsg);
                }
            }
            if (apps_status == 4) {
                final String errorMsg = (String)status.get("FETCH_APPS_ERRORS");
                if (errorMsg != null && errorMsg.equals("Invalid API service configuration details")) {
                    response.put("migration_error_msg", (Object)errorMsg);
                    response.put("migration_error_code", 36001);
                }
                else {
                    response.put("migration_apps_error", (Object)errorMsg);
                }
            }
            response.put("count_summary", (Object)new MigrationSummary().migrationCountSummary(config_id, customer_id));
            return response;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching migration status", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray addAuthDetails(final JSONArray jsonArray) {
        final JSONArray returnArray = new JSONArray();
        try {
            final DataObject dO = DataAccess.constructDataObject();
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject authDetailJSON = jsonArray.getJSONObject(i);
                this.addAuthDetails(authDetailJSON, dO);
            }
            DataAccess.update(dO);
            return this.getAuthDetailsFromDO(dO);
        }
        catch (final JSONException | DataAccessException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception in adding auth details", e);
            return returnArray;
        }
    }
    
    private Object addAuthDetails(final JSONObject authDetailJSON, final DataObject dO) throws DataAccessException, JSONException {
        Object authId = null;
        final JSONObject authInfoJson = new JSONObject(authDetailJSON.get("APIAuthInfo").toString());
        final Row row = new Row("APIAuthInfo");
        final Integer type = Integer.parseInt(authInfoJson.get("TYPE").toString());
        final Integer authServiceType = Integer.parseInt(authInfoJson.get("AUTH_SERVICE_TYPE").toString());
        row.set("TYPE", (Object)type);
        row.set("AUTH_SERVICE_TYPE", (Object)authServiceType);
        final long currentMillisec = MDMUtil.getCurrentTimeInMillis();
        row.set("CREATED_AT", (Object)currentMillisec);
        row.set("MODIFIED_AT", (Object)currentMillisec);
        dO.addRow(row);
        authId = row.get("AUTH_ID");
        JSONObject specificAuthJson = new JSONObject();
        final Row basicRow = new Row("BasicDigestAuthInfo");
        if (authDetailJSON.has("BasicDigestAuthInfo")) {
            specificAuthJson = new JSONObject(authDetailJSON.get("BasicDigestAuthInfo").toString());
            if (specificAuthJson != null) {
                final Object authorization = specificAuthJson.opt("AUTHORIZATION_HEADER");
                if (authorization == null) {
                    basicRow.set("USERNAME", (Object)specificAuthJson.get("USERNAME").toString());
                    basicRow.set("PASSWORD", (Object)specificAuthJson.get("PASSWORD").toString());
                }
                else {
                    basicRow.set("AUTHORIZATION_HEADER", (Object)authorization.toString());
                }
            }
        }
        basicRow.set("AUTH_ID", authId);
        dO.addRow(basicRow);
        final JSONObject customHeaders = (JSONObject)JSONUtil.optObject(authDetailJSON, "CustomHeadersAuthInfo", true);
        if (customHeaders != null) {
            final Iterator<String> headers = customHeaders.keys();
            while (headers.hasNext()) {
                final String header = headers.next();
                final Row customHeader = new Row("CustomHeadersAuthInfo");
                customHeader.set("CUSTOM_KEY", (Object)header);
                customHeader.set("CUSTOM_VALUE_ENCRYPTED", (Object)customHeaders.get(header).toString());
                customHeader.set("AUTH_ID", authId);
                dO.addRow(customHeader);
            }
        }
        return authId;
    }
    
    public JSONArray getAuthDetailsFromDO(final DataObject dataObject) {
        final JSONArray jsonArray = new JSONArray();
        try {
            final Iterator iterator = dataObject.getRows("APIAuthInfo");
            int indexCount = 0;
            while (iterator.hasNext()) {
                final JSONObject authDetailsJSON = new JSONObject();
                final Row apiAuthInfoRow = iterator.next();
                final Long apiAuthID = (Long)apiAuthInfoRow.get("AUTH_ID");
                final Criteria authIdCriteria = new Criteria(new Column("APIAuthInfo", "AUTH_ID"), (Object)apiAuthID, 0);
                final JSONObject apiAuthJSON = apiAuthInfoRow.getAsJSON();
                apiAuthJSON.remove("AUTH_ID");
                authDetailsJSON.put("APIAuthInfo", (Object)apiAuthJSON);
                authDetailsJSON.put("AUTH_ID", (Object)apiAuthID);
                final Row basicDigestRow = dataObject.getRow("BasicDigestAuthInfo", authIdCriteria);
                if (basicDigestRow != null) {
                    final JSONObject digestJSON = basicDigestRow.getAsJSON();
                    digestJSON.remove("AUTH_ID");
                    authDetailsJSON.put("BasicDigestAuthInfo", (Object)digestJSON);
                }
                final Iterator iterator2 = dataObject.getRows("CustomHeadersAuthInfo", authIdCriteria);
                final JSONObject customHeaderJSON = new JSONObject();
                while (iterator2.hasNext()) {
                    final Row customHeaderRow = iterator2.next();
                    if (customHeaderRow != null) {
                        final String customKey = (String)customHeaderRow.get("CUSTOM_KEY");
                        final String customValue = (String)customHeaderRow.get("CUSTOM_VALUE_ENCRYPTED");
                        customHeaderJSON.put(customKey, (Object)customValue);
                    }
                }
                authDetailsJSON.put("CustomHeadersAuthInfo", (Object)customHeaderJSON);
                jsonArray.put(indexCount++, (Object)authDetailsJSON);
            }
        }
        catch (final DataAccessException | JSONException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception in getting auth details from DO", e);
        }
        return jsonArray;
    }
    
    private SelectQuery getAuthDetailQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("APIAuthInfo"));
        final Join basicJoin = new Join("APIAuthInfo", "BasicDigestAuthInfo", new String[] { "AUTH_ID" }, new String[] { "AUTH_ID" }, 1);
        final Join headersJoin = new Join("APIAuthInfo", "CustomHeadersAuthInfo", new String[] { "AUTH_ID" }, new String[] { "AUTH_ID" }, 1);
        selectQuery.addJoin(basicJoin);
        selectQuery.addJoin(headersJoin);
        return selectQuery;
    }
    
    private SelectQuery getApiConfigQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
        selectQuery.addJoin(new Join("APIServiceConfiguration", "APIServiceAuthDetails", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 2));
        selectQuery.addJoin(new Join("APIServiceAuthDetails", "APIAuthInfo", new String[] { "AUTH_ID" }, new String[] { "AUTH_ID" }, 2));
        selectQuery.addJoin(new Join("APIAuthInfo", "BasicDigestAuthInfo", new String[] { "AUTH_ID" }, new String[] { "AUTH_ID" }, 1));
        selectQuery.addJoin(new Join("APIAuthInfo", "CustomHeadersAuthInfo", new String[] { "AUTH_ID" }, new String[] { "AUTH_ID" }, 1));
        return selectQuery;
    }
    
    public DataObject getAuthDetails(final List<Long> authIds) throws DataAccessException {
        final SelectQuery selectQuery = this.getAuthDetailQuery();
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria authIdsCriteria = new Criteria(new Column("APIAuthInfo", "AUTH_ID"), (Object)authIds.toArray(), 8);
        selectQuery.setCriteria(authIdsCriteria);
        return MDMUtil.getPersistence().get(selectQuery);
    }
    
    public JSONObject editAuthDetails(final JSONObject json) throws DataAccessException, JSONException {
        final JSONArray authArray = new JSONArray();
        authArray.put((Object)json);
        final JSONArray returnArray = this.editAuthDetails(authArray);
        if (returnArray.length() > 0) {
            return returnArray.getJSONObject(0);
        }
        return new JSONObject();
    }
    
    public String generateWebClipURL(final String eurl, final Long configID, final int serviceId) throws Exception {
        final String newURL = MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/enrollment/migration";
        final URL url = new URL(eurl);
        final String s = url.getQuery();
        final String[] str = s.split("&");
        int flag = 0;
        int p = 0;
        for (int i = 0; i < str.length; ++i) {
            if (str[i].contains("zapikey")) {
                flag = 1;
                p = i;
                break;
            }
        }
        String dynamicVariable = "%deviceID%";
        if (serviceId == 1 || serviceId == 7 || serviceId == 8 || serviceId == 2) {
            dynamicVariable = "%resourceid%";
        }
        else if (serviceId == 6) {
            dynamicVariable = "%udid%";
        }
        if (flag == 1) {
            final StringBuilder webclip_url = new StringBuilder();
            webclip_url.append(newURL);
            webclip_url.append("?");
            webclip_url.append(str[p]);
            webclip_url.append("&deviceID=" + dynamicVariable);
            webclip_url.append("&configID=");
            webclip_url.append(configID);
            return webclip_url.toString();
        }
        APIServiceDataHandler.logger.log(Level.SEVERE, " Exception occured in generateWebClipURL");
        throw new APIHTTPException("COM0009", new Object[0]);
    }
    
    private void addRowFromJSON(final Row row, final JSONObject jsonObject, final String primaryKey) throws JSONException {
        final List columns = row.getColumns();
        for (final Object column : columns) {
            final String columnName = column.toString();
            if (!columnName.equalsIgnoreCase(primaryKey) && jsonObject.has(columnName)) {
                row.set(columnName, jsonObject.get(columnName));
            }
        }
    }
    
    public JSONArray editAuthDetails(final JSONArray authArray) throws JSONException, DataAccessException {
        final List<Long> authIds = new ArrayList<Long>();
        for (int i = 0; i < authArray.length(); ++i) {
            authIds.add(authArray.getJSONObject(i).getLong("AUTH_ID"));
        }
        final DataObject dataObject = this.getAuthDetails(authIds);
        return this.editAuthDetails(authArray, dataObject);
    }
    
    public JSONArray editAuthDetails(final JSONArray authArray, final DataObject dataObject) {
        JSONArray returnArray = new JSONArray();
        try {
            for (int i = 0; i < authArray.length(); ++i) {
                final JSONObject authJSON = authArray.getJSONObject(i);
                if (authJSON.has("APIAuthInfo")) {
                    final Long authId = authJSON.getLong("AUTH_ID");
                    final Criteria authIdCriteria = new Criteria(new Column("APIAuthInfo", "AUTH_ID"), (Object)authId, 0);
                    final Row authDetailsRow = dataObject.getRow("APIAuthInfo", authIdCriteria);
                    final JSONObject authInfoJSON = authJSON.getJSONObject("APIAuthInfo");
                    authInfoJSON.put("MODIFIED_AT", MDMUtil.getCurrentTimeInMillis());
                    this.addRowFromJSON(authDetailsRow, authInfoJSON, "AUTH_ID");
                    dataObject.updateRow(authDetailsRow);
                    Row digestAuthInfoRow = dataObject.getRow("BasicDigestAuthInfo", authDetailsRow);
                    if (digestAuthInfoRow == null) {
                        digestAuthInfoRow = new Row("BasicDigestAuthInfo");
                    }
                    if (authJSON.has("BasicDigestAuthInfo")) {
                        final JSONObject digestAuthJSON = authJSON.getJSONObject("BasicDigestAuthInfo");
                        this.addRowFromJSON(digestAuthInfoRow, digestAuthJSON, "AUTH_ID");
                        dataObject.updateRow(digestAuthInfoRow);
                    }
                    else {
                        dataObject.deleteRow(digestAuthInfoRow);
                    }
                    final Criteria customAuthCritieria = new Criteria(new Column("CustomHeadersAuthInfo", "AUTH_ID"), (Object)authId, 0);
                    if (authJSON.has("CustomHeadersAuthInfo")) {
                        final JSONObject customHeaderObject = authJSON.getJSONObject("CustomHeadersAuthInfo");
                        final Iterator<String> headers = customHeaderObject.keys();
                        final List<String> addedHeader = new ArrayList<String>();
                        while (headers.hasNext()) {
                            final String header = headers.next();
                            addedHeader.add(header);
                            Row customHeaderRow = dataObject.getRow("CustomHeadersAuthInfo", new Criteria(new Column("CustomHeadersAuthInfo", "CUSTOM_KEY"), (Object)header, 0, false).and(customAuthCritieria));
                            if (customHeaderRow == null) {
                                customHeaderRow = new Row("CustomHeadersAuthInfo");
                                customHeaderRow.set("CUSTOM_KEY", (Object)header);
                                customHeaderRow.set("CUSTOM_VALUE_ENCRYPTED", (Object)customHeaderObject.get(header).toString());
                                customHeaderRow.set("AUTH_ID", (Object)authId);
                                dataObject.addRow(customHeaderRow);
                            }
                            else {
                                customHeaderRow.set("CUSTOM_KEY", (Object)header);
                                customHeaderRow.set("CUSTOM_VALUE_ENCRYPTED", (Object)customHeaderObject.get(header).toString());
                                dataObject.updateRow(customHeaderRow);
                            }
                        }
                        dataObject.deleteRows("CustomHeadersAuthInfo", new Criteria(new Column("CustomHeadersAuthInfo", "CUSTOM_KEY"), (Object)addedHeader.toArray(), 9).and(customAuthCritieria));
                    }
                    else {
                        dataObject.deleteRows("CustomHeadersAuthInfo", customAuthCritieria);
                    }
                }
            }
            MDMUtil.getPersistence().update(dataObject);
            returnArray = this.getAuthDetailsFromDO(dataObject);
        }
        catch (final JSONException | DataAccessException e) {
            APIServiceDataHandler.logger.log(Level.INFO, "Exception occurred in editAuthDetails", e);
        }
        return returnArray;
    }
    
    private JSONArray getAuthIdsFromAuthDetails(final JSONArray authDetails) throws JSONException {
        final JSONArray authIds = new JSONArray();
        for (int i = 0; i < authDetails.length(); ++i) {
            final JSONObject authJSON = authDetails.getJSONObject(i);
            authIds.put(authJSON.getLong("AUTH_ID"));
        }
        return authIds;
    }
    
    public JSONObject addServiceConfig(final JSONObject json) throws Exception {
        final JSONObject returnJson = new JSONObject();
        final JSONObject configJson = new JSONObject(json.get("APIServiceConfiguration").toString());
        JSONArray authArray = json.getJSONArray("APIServiceAuthDetails");
        final Integer serviceID = Integer.parseInt(configJson.get("SERVICE_ID").toString());
        final Long customerID = APIUtil.getCustomerID(json);
        final String configName = configJson.optString("NAME");
        final JSONObject queryExistingJSON = new JSONObject();
        queryExistingJSON.put("NAME", (Object)configName);
        queryExistingJSON.put("CUSTOMER_ID", (Object)customerID);
        final JSONObject existingDetails = this.getAPIServiceConfigDetails(queryExistingJSON);
        if (existingDetails.length() > 0) {
            throw new Exception("Cannot create new config. One exists already for the same name");
        }
        try {
            DataAccess.getTransactionManager().begin();
            final DataObject dO = DataAccess.constructDataObject();
            final Row configRow = new Row("APIServiceConfiguration");
            configRow.set("SERVICE_ID", (Object)serviceID);
            configRow.set("NAME", (Object)configJson.get("NAME").toString());
            configRow.set("DESCRIPTION", (Object)configJson.get("DESCRIPTION").toString());
            final long currentMillisec = MDMUtil.getCurrentTimeInMillis();
            configRow.set("CREATED_AT", (Object)currentMillisec);
            configRow.set("MODIFIED_AT", (Object)currentMillisec);
            final Object url = configJson.opt("SERVER_URL");
            if (url != null) {
                configRow.set("SERVER_URL", (Object)url.toString());
            }
            final Object eUrl = configJson.opt("NEW_ENROLLMENT_URL");
            if (eUrl != null) {
                configRow.set("NEW_ENROLLMENT_URL", (Object)eUrl.toString());
            }
            dO.addRow(configRow);
            final Object configId = configRow.get("CONFIG_ID");
            final Row configToCustomerIDAssociationRow = new Row("CustomerAPIServiceConfigAssociation");
            configToCustomerIDAssociationRow.set("CONFIG_ID", configId);
            configToCustomerIDAssociationRow.set("CUSTOMER_ID", (Object)customerID);
            dO.addRow(configToCustomerIDAssociationRow);
            for (int i = 0; i < authArray.length(); ++i) {
                final Object authItem = authArray.get(i);
                Object authId = null;
                if (authItem instanceof JSONObject) {
                    authId = this.addAuthDetails(new JSONObject(String.valueOf(authItem)), dO);
                }
                else {
                    authId = authItem;
                }
                final Row serviceAuthDetail = new Row("APIServiceAuthDetails");
                serviceAuthDetail.set("AUTH_ID", authId);
                serviceAuthDetail.set("CONFIG_ID", configId);
                dO.addRow(serviceAuthDetail);
            }
            final Row migrationStatus = new Row("MDMServerMigrationStatus");
            migrationStatus.set("CONFIG_ID", configId);
            dO.addRow(migrationStatus);
            DataAccess.update(dO);
            DataAccess.getTransactionManager().commit();
            configJson.put("CONFIG_ID", Long.parseLong(dO.getFirstRow("APIServiceConfiguration").get("CONFIG_ID").toString()));
            returnJson.put("APIServiceConfiguration", (Object)configJson);
            authArray = this.getAuthDetailsFromDO(dO);
            returnJson.put("APIServiceAuthDetails", (Object)authArray);
        }
        catch (final NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while addServiceConfig(). Some transactions related exceptions. Going to rollback. ", e);
            try {
                if (6 != DataAccess.getTransactionManager().getStatus()) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e2) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while rollback addServiceConfig() ", e2);
                throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e2.getMessage());
            }
            throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e.getMessage());
        }
        catch (final JSONException | DataAccessException e3) {
            try {
                if (6 != DataAccess.getTransactionManager().getStatus()) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e2) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while rollback addServiceConfig() ", e2);
                throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e2.getMessage());
            }
            APIServiceDataHandler.logger.log(Level.SEVERE, " Exception occured in addServiceConfig", e3);
            throw e3;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, " Exception occured in addServiceConfig", e);
            try {
                if (6 != DataAccess.getTransactionManager().getStatus()) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e2) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while rollback addServiceConfig() ", e2);
                throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e2.getMessage());
            }
            throw e;
        }
        return returnJson;
    }
    
    public JSONObject addOrUpdateServiceConfig(final JSONObject json, final Long customerId, final Long configId) throws Exception {
        final JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = this.getApiConfigQuery();
            if (configId != null) {
                selectQuery.setCriteria(new Criteria(new Column("APIServiceConfiguration", "CONFIG_ID"), (Object)configId, 0));
                selectQuery.addSelectColumn(new Column((String)null, "*"));
                final DataObject dataObject = DataAccess.get(selectQuery);
                final JSONObject configJson = (JSONObject)JSONUtil.optObject(json, "APIServiceConfiguration", true);
                if (configJson != null) {
                    final Row configRow = dataObject.getFirstRow("APIServiceConfiguration");
                    final Integer serviceID = (Integer)JSONUtil.optObject(configJson, "SERVICE_ID", true);
                    if (serviceID != null) {
                        configRow.set("SERVICE_ID", (Object)serviceID);
                    }
                    final String name = (String)JSONUtil.optObject(configJson, "NAME", true);
                    if (name != null) {
                        configRow.set("NAME", (Object)name);
                    }
                    final String description = (String)JSONUtil.optObject(configJson, "DESCRIPTION", true);
                    if (description != null) {
                        configRow.set("DESCRIPTION", (Object)description);
                    }
                    final Long modified = MDMUtil.getCurrentTimeInMillis();
                    if (modified != null) {
                        configRow.set("MODIFIED_AT", (Object)modified);
                    }
                    final Object url = JSONUtil.optObject(configJson, "SERVER_URL", true);
                    if (url != null) {
                        configRow.set("SERVER_URL", (Object)url.toString());
                    }
                    final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                    if (natProps.size() == 0) {
                        throw new Exception("NAT Settings not configured.");
                    }
                    final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
                    final int serverPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
                    final String templateToken = new APIServiceDataHandler().getIOSMigrationEnrollmentTemplateToken(customerId);
                    final JSONObject loginInfo = new JSONObject();
                    loginInfo.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID()));
                    loginInfo.put("TEMPLATE_TYPE", 50);
                    final String userApiKey = MDMUserAPIKeyGenerator.getInstance().generateAPIKey(loginInfo).getKeyValue();
                    final String eUrl = "https://" + serverIP + ":" + serverPort + "/mdm/client/v1/ios/ac/" + templateToken + "?templateToken=" + templateToken + "&zapikey=" + userApiKey;
                    if (eUrl != null) {
                        configRow.set("NEW_ENROLLMENT_URL", (Object)eUrl);
                    }
                    dataObject.updateRow(configRow);
                    if (json.has("APIServiceAuthDetails")) {
                        final JSONArray authArray = json.getJSONArray("APIServiceAuthDetails");
                        this.editAuthDetails(authArray, dataObject);
                    }
                    DataAccess.update(dataObject);
                    DataAccess.getTransactionManager().commit();
                }
            }
            else {
                final JSONObject configJson2 = new JSONObject(json.get("APIServiceConfiguration").toString());
                final JSONArray authArray2 = json.getJSONArray("APIServiceAuthDetails");
                final Integer serviceID2 = Integer.parseInt(configJson2.get("SERVICE_ID").toString());
                final String configName = configJson2.optString("NAME");
                final JSONObject queryExistingJSON = new JSONObject();
                queryExistingJSON.put("NAME", (Object)configName);
                if (this.ifConfigExists(customerId, serviceID2)) {
                    throw new Exception("Cannot create new config. One exists already for the same service");
                }
                DataAccess.getTransactionManager().begin();
                final DataObject dO = DataAccess.constructDataObject();
                final Row configRow2 = new Row("APIServiceConfiguration");
                configRow2.set("SERVICE_ID", (Object)serviceID2);
                configRow2.set("NAME", (Object)configJson2.get("NAME").toString());
                configRow2.set("DESCRIPTION", (Object)configJson2.get("DESCRIPTION").toString());
                final long currentMillisec = MDMUtil.getCurrentTimeInMillis();
                configRow2.set("CREATED_AT", (Object)currentMillisec);
                configRow2.set("MODIFIED_AT", (Object)currentMillisec);
                final Object url2 = configJson2.opt("SERVER_URL");
                if (url2 != null) {
                    configRow2.set("SERVER_URL", (Object)url2.toString());
                }
                final Properties natProps2 = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                if (natProps2.size() == 0) {
                    throw new Exception("NAT Settings not configured.");
                }
                final String serverIP2 = ((Hashtable<K, String>)natProps2).get("NAT_ADDRESS");
                final int serverPort2 = ((Hashtable<K, Integer>)natProps2).get("NAT_HTTPS_PORT");
                final String templateToken2 = new APIServiceDataHandler().getIOSMigrationEnrollmentTemplateToken(customerId);
                final JSONObject loginInfo2 = new JSONObject();
                loginInfo2.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID()));
                loginInfo2.put("TEMPLATE_TYPE", 50);
                final String userApiKey2 = MDMUserAPIKeyGenerator.getInstance().generateAPIKey(loginInfo2).getKeyValue();
                final String eUrl2 = "https://" + serverIP2 + ":" + serverPort2 + "/mdm/client/v1/ios/ac/" + templateToken2 + "?templateToken=" + templateToken2 + "&zapikey=" + userApiKey2;
                if (eUrl2 != null) {
                    configRow2.set("NEW_ENROLLMENT_URL", (Object)eUrl2.toString());
                }
                dO.addRow(configRow2);
                final Row configToCustomerIDAssociationRow = new Row("CustomerAPIServiceConfigAssociation");
                final Object config_id = configRow2.get("CONFIG_ID");
                configToCustomerIDAssociationRow.set("CONFIG_ID", config_id);
                configToCustomerIDAssociationRow.set("CUSTOMER_ID", (Object)customerId);
                dO.addRow(configToCustomerIDAssociationRow);
                for (int i = 0; i < authArray2.length(); ++i) {
                    final Object authItem = authArray2.get(i);
                    Object authId = null;
                    if (authItem instanceof JSONObject) {
                        authId = this.addAuthDetails(new JSONObject(String.valueOf(authItem)), dO);
                    }
                    else {
                        authId = authItem;
                    }
                    final Row serviceAuthDetail = new Row("APIServiceAuthDetails");
                    serviceAuthDetail.set("AUTH_ID", authId);
                    serviceAuthDetail.set("CONFIG_ID", config_id);
                    dO.addRow(serviceAuthDetail);
                }
                final Row migrationStatus = new Row("MDMServerMigrationStatus");
                migrationStatus.set("CONFIG_ID", config_id);
                dO.addRow(migrationStatus);
                DataAccess.update(dO);
                DataAccess.getTransactionManager().commit();
                response.put("CONFIG_ID", Long.parseLong(dO.getFirstRow("APIServiceConfiguration").get("CONFIG_ID").toString()));
            }
        }
        catch (final NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while addServiceConfig(). Some transactions related exceptions. Going to rollback. ", e);
        }
        catch (final JSONException | DataAccessException e2) {
            try {
                if (6 != DataAccess.getTransactionManager().getStatus()) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e3) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while rollback addServiceConfig() ", e3);
                throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e3.getMessage());
            }
            APIServiceDataHandler.logger.log(Level.SEVERE, " Exception occured in addServiceConfig", e2);
            throw e2;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while addition or editing api service configs");
            throw new APIHTTPException("COM0004", new Object[] { e.getMessage() });
        }
        return response;
    }
    
    public JSONObject editServiceConfig(final JSONObject json, final Long configID) throws Exception {
        final JSONObject returnJSON = new JSONObject();
        try {
            DataAccess.getTransactionManager().begin();
            final SelectQuery selectQuery = this.getApiConfigQuery();
            final Criteria configIDCrit = new Criteria(Column.getColumn("APIServiceConfiguration", "CONFIG_ID"), (Object)configID, 0);
            selectQuery.setCriteria(configIDCrit);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = DataAccess.get(selectQuery);
            if (configID != null) {
                this.validateConfiguration(configID);
            }
            final JSONObject configJson = (JSONObject)JSONUtil.optObject(json, "APIServiceConfiguration", true);
            if (configJson != null) {
                Row configRow = dO.getFirstRow("APIServiceConfiguration");
                final Integer serviceID = (Integer)JSONUtil.optObject(configJson, "SERVICE_ID", true);
                if (serviceID != null) {
                    configRow.set("SERVICE_ID", (Object)serviceID);
                }
                final String name = (String)JSONUtil.optObject(configJson, "NAME", true);
                if (name != null) {
                    configRow.set("NAME", (Object)name);
                }
                final String description = (String)JSONUtil.optObject(configJson, "DESCRIPTION", true);
                if (description != null) {
                    configRow.set("DESCRIPTION", (Object)description);
                }
                final Long modified = MDMUtil.getCurrentTimeInMillis();
                if (modified != null) {
                    configRow.set("MODIFIED_AT", (Object)modified);
                }
                final Object url = JSONUtil.optObject(configJson, "SERVER_URL", true);
                if (url != null) {
                    configRow.set("SERVER_URL", (Object)url.toString());
                }
                final Object eUrl = JSONUtil.optObject(configJson, "NEW_ENROLLMENT_URL", true);
                if (eUrl != null) {
                    configRow.set("NEW_ENROLLMENT_URL", (Object)eUrl.toString());
                }
                dO.updateRow(configRow);
                JSONArray addedAuthDetails = new JSONArray();
                if (json.has("APIServiceAuthDetails")) {
                    final JSONArray authArray = json.getJSONArray("APIServiceAuthDetails");
                    addedAuthDetails = this.editAuthDetails(authArray, dO);
                }
                DataAccess.update(dO);
                DataAccess.getTransactionManager().commit();
                configRow = dO.getRow("APIServiceConfiguration");
                returnJSON.put("APIServiceConfiguration", (Object)configRow.getAsJSON());
                returnJSON.put("APIServiceAuthDetails", (Object)addedAuthDetails);
            }
        }
        catch (final NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while editServiceConfig(). Some transactions related exceptions. Going to rollback. ", e);
            throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e.getMessage());
        }
        catch (final JSONException | DataAccessException e2) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception in editing service config", e2);
            throw e2;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "APIHTTPException occured in editServiceConfiguration", ex);
                throw (APIHTTPException)ex;
            }
            APIServiceDataHandler.logger.log(Level.SEVERE, "APIHTTPException  occured in editServiceConfiguration", ex);
            throw new APIHTTPException("SCN0002", new Object[0]);
        }
        finally {
            try {
                if (6 != DataAccess.getTransactionManager().getStatus()) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e3) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Migration APIServiceDataHandler Exception while rollback editServiceConfig() ", e3);
                throw new Exception("JAVAX Tranaction Exceptions occurred. Refer logs for the trace: " + e3.getMessage());
            }
        }
        return returnJSON;
    }
    
    public JSONObject getUserDeviceAssociation() throws DataAccessException, SQLException, QueryConstructionException {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationAssociation"));
        selectQuery.addJoin(new Join("MigrationAssociation", "MigrationUsers", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        selectQuery.addJoin(new Join("MigrationAssociation", "MigrationDevices", new String[] { "DEVICE_ID" }, new String[] { "DEVICE_ID" }, 1));
        selectQuery.addSelectColumns((List)new ArrayList() {
            {
                this.add(new Column("MigrationUsers", "USER_ID"));
                this.add(new Column("MigrationUsers", "USER_NAME"));
                this.add(new Column("MigrationUsers", "EMAIL_ID"));
                this.add(new Column("MigrationDevices", "DEVICE_ID"));
                this.add(new Column("MigrationDevices", "IMEI"));
                this.add(new Column("MigrationDevices", "UDID"));
                this.add(new Column("MigrationDevices", "DEVICE_NAME"));
                this.add(new Column("MigrationDevices", "MIGRATION_SERIAL_ID"));
                this.add(new Column("MigrationAssociation", "USER_ID"));
                this.add(new Column("MigrationAssociation", "DEVICE_ID"));
            }
        });
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator usersIterator = dataObject.getRows("MigrationUsers");
        while (usersIterator.hasNext()) {
            final Row userRow = usersIterator.next();
            final String userId = userRow.get("USER_ID").toString();
            final Criteria userIdCriteria = new Criteria(new Column("MigrationAssociation", "USER_ID"), (Object)userId, 0);
            final Iterator associationIterator = dataObject.getRows("MigrationAssociation", userIdCriteria);
            final JSONArray devices = new JSONArray();
            while (associationIterator.hasNext()) {
                final Row associationRowForDevices = associationIterator.next();
                final String deviceId = associationRowForDevices.get("DEVICE_ID").toString();
                final Criteria deviceIdCriteria = new Criteria(new Column("MigrationDevices", "DEVICE_ID"), (Object)deviceId, 0);
                final Iterator devicesIterator = dataObject.getRows("MigrationDevices", deviceIdCriteria);
                while (devicesIterator.hasNext()) {
                    final Row row = devicesIterator.next();
                    final JSONObject device = new JSONObject();
                    if (row.get("IMEI") != null) {
                        device.put("IMEI", (Object)row.get("IMEI").toString());
                    }
                    if (row.get("UDID") != null) {
                        device.put("UDID", (Object)row.get("UDID").toString());
                    }
                    if (row.get("DEVICE_NAME") != null) {
                        device.put("DeviceName", (Object)row.get("DEVICE_NAME").toString());
                    }
                    device.put("DEVICE_ID", (Object)row.get("DEVICE_ID").toString());
                    if (row.get("MIGRATION_SERIAL_ID") != null) {
                        device.put("SerialNumber", (Object)row.get("MIGRATION_SERIAL_ID").toString());
                    }
                    if (userRow.get("USER_NAME") != null) {
                        device.put("UserName", (Object)userRow.get("USER_NAME").toString());
                    }
                    if (userRow.get("EMAIL_ID") != null) {
                        device.put("EmailAddr", (Object)userRow.get("EMAIL_ID").toString());
                    }
                    if (userRow.get("PHONE_NUMBER") != null) {
                        device.put("PHONE_NUMBER", userRow.get("PHONE_NUMBER"));
                    }
                    devices.put((Object)device);
                }
                response.put(userId, (Object)devices);
            }
        }
        return response;
    }
    
    public JSONObject getUserDeviceAssociation(final String udid, final String serialNo, final Long customer_id) throws DataAccessException {
        final JSONObject response = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationAssociation"));
        selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationAssociation"));
        selectQuery.addJoin(new Join("MigrationAssociation", "MigrationUsers", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        selectQuery.addJoin(new Join("MigrationAssociation", "MigrationDevices", new String[] { "DEVICE_ID" }, new String[] { "DEVICE_ID" }, 1));
        selectQuery.setCriteria(this.getCustomerIdMatchCriteria(customer_id).and(new Criteria(new Column("MigrationDevices", "UDID"), (Object)udid, 0).or(new Criteria(new Column("MigrationDevices", "MIGRATION_SERIAL_ID"), (Object)serialNo, 0))));
        selectQuery.addSelectColumns((List)new ArrayList() {
            {
                this.add(new Column("MigrationUsers", "USER_ID"));
                this.add(new Column("MigrationUsers", "USER_NAME"));
                this.add(new Column("MigrationUsers", "EMAIL_ID"));
                this.add(new Column("MigrationUsers", "DOMAIN"));
                this.add(new Column("MigrationUsers", "IS_AD_SYNCED_USER"));
                this.add(new Column("MigrationDevices", "DEVICE_ID"));
                this.add(new Column("MigrationDevices", "IMEI"));
                this.add(new Column("MigrationDevices", "UDID"));
                this.add(new Column("MigrationDevices", "DEVICE_NAME"));
                this.add(new Column("MigrationDevices", "MIGRATION_SERIAL_ID"));
                this.add(new Column("MigrationAssociation", "USER_ID"));
                this.add(new Column("MigrationAssociation", "DEVICE_ID"));
            }
        });
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator usersIterator = dataObject.getRows("MigrationUsers");
        while (usersIterator.hasNext()) {
            final Row userRow = usersIterator.next();
            final String userId = userRow.get("USER_ID").toString();
            final Criteria userIdCriteria = new Criteria(new Column("MigrationAssociation", "USER_ID"), (Object)userId, 0);
            final Iterator associationIterator = dataObject.getRows("MigrationAssociation", userIdCriteria);
            final JSONArray devices = new JSONArray();
            while (associationIterator.hasNext()) {
                final Row associationRowForDevices = associationIterator.next();
                final String deviceId = associationRowForDevices.get("DEVICE_ID").toString();
                final Criteria deviceIdCriteria = new Criteria(new Column("MigrationDevices", "DEVICE_ID"), (Object)deviceId, 0);
                final Iterator devicesIterator = dataObject.getRows("MigrationDevices", deviceIdCriteria);
                while (devicesIterator.hasNext()) {
                    final Row row = devicesIterator.next();
                    final JSONObject device = new JSONObject();
                    if (row.get("IMEI") != null) {
                        device.put("IMEI", (Object)row.get("IMEI").toString());
                    }
                    if (row.get("UDID") != null) {
                        device.put("UDID", (Object)row.get("UDID").toString());
                    }
                    if (row.get("DEVICE_NAME") != null) {
                        device.put("DeviceName", (Object)row.get("DEVICE_NAME").toString());
                    }
                    device.put("DEVICE_ID", (Object)row.get("DEVICE_ID").toString());
                    if (row.get("MIGRATION_SERIAL_ID") != null) {
                        device.put("SerialNumber", (Object)row.get("MIGRATION_SERIAL_ID").toString());
                    }
                    if (userRow.get("USER_NAME") != null) {
                        device.put("UserName", (Object)userRow.get("USER_NAME").toString());
                    }
                    if (userRow.get("EMAIL_ID") != null) {
                        device.put("EmailAddr", (Object)userRow.get("EMAIL_ID").toString());
                    }
                    if (userRow.get("PHONE_NUMBER") != null) {
                        device.put("PHONE_NUMBER", userRow.get("PHONE_NUMBER"));
                    }
                    if (userRow.get("IS_AD_SYNCED_USER") != null) {
                        device.put("Domain", userRow.get("DOMAIN"));
                    }
                    devices.put((Object)device);
                }
                response.put(userId, (Object)devices);
            }
        }
        return response;
    }
    
    public JSONObject getAllDeviceEnrollmentRequestIDsForCustomerID(final Long customerID) {
        final JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
            selectQuery.addSelectColumn(new Column("DeviceForEnrollment", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            selectQuery.addSelectColumn(new Column("DeviceForEnrollment", "UDID"));
            selectQuery.addSelectColumn(new Column("DeviceForEnrollment", "UDID"));
            selectQuery.addSelectColumn(new Column("DeviceForEnrollment", "MIGRATION_SERVER_DEVICE_ID"));
            selectQuery.addSelectColumn(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"));
            selectQuery.setCriteria(new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
            selectQuery.addJoin(new Join("DeviceForEnrollment", "MigrationDevices", new String[] { "UDID" }, new String[] { "UDID" }, 1));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("DeviceEnrollmentToRequest");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Iterator iterator2 = dataObject.getRows("MigrationDevices");
                while (iterator2.hasNext()) {
                    final Row migrationDeviceRow = iterator.next();
                    if (row.get("UDID").toString().equalsIgnoreCase(migrationDeviceRow.get("UDID").toString())) {
                        response.put(row.get("ENROLLMENT_REQUEST_ID").toString(), (Object)migrationDeviceRow.get("MIGRATION_SERVER_DEVICE_ID").toString());
                    }
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching enrollment request IDs.", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public String getMigrationUserIDForUserName(final String username, final Long config_id, final Long customerId) {
        String userid = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUsers"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationUsers"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customerId, config_id, "MigrationUsers").and(new Criteria(new Column("MigrationUsers", "USER_NAME"), (Object)username, 0)));
            selectQuery.addSelectColumn(new Column("MigrationUsers", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationUsers");
            while (iterator.hasNext()) {
                final Row userRow = iterator.next();
                userid = userRow.get("USER_ID").toString();
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching migration userid", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return userid;
    }
    
    public String getResourceUserIDForUserName(final String username, final Long customerID) {
        String userid = "";
        try {
            final Row row = new Row("Resource");
            row.set("CUSTOMER_ID", (Object)customerID);
            row.set("NAME", (Object)username);
            row.set("RESOURCE_TYPE", (Object)2);
            final DataObject dataObject = DataAccess.get("Resource", row);
            final Iterator iterator = dataObject.getRows("Resource");
            while (iterator.hasNext()) {
                final Row userRow = iterator.next();
                userid = userRow.get("RESOURCE_ID").toString();
            }
            return userid;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching userid", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String getIOSMigrationEnrollmentTemplateToken(final Long customer_id) {
        String token = "";
        try {
            final Row enrollmentRow = new Row("EnrollmentTemplate");
            enrollmentRow.set("TEMPLATE_TYPE", (Object)50);
            enrollmentRow.set("CUSTOMER_ID", (Object)customer_id);
            final DataObject dataObject = DataAccess.get("EnrollmentTemplate", enrollmentRow);
            final Row row = dataObject.getFirstRow("EnrollmentTemplate");
            token = row.get("TEMPLATE_TOKEN").toString();
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching enrollment template token", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return token;
    }
    
    public Long getGroupIDForGroupName(final String group_name, final Long customer_id) {
        Long groupId = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
            final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_TYPE"), (Object)101, 0);
            final Criteria criteria2 = new Criteria(new Column("Resource", "NAME"), (Object)group_name, 0);
            final Criteria criteria3 = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customer_id, 0);
            selectQuery.setCriteria(criteria.and(criteria2).and(criteria3));
            selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            groupId = Long.parseLong(dataObject.getFirstRow("Resource").get("RESOURCE_ID").toString());
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching group id for the given group name", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return groupId;
    }
    
    public String getGroupNameForGroupId(final String groupId, final Long config_id, final Long customer_id) {
        String group_name = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationGroups"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationGroups"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationGroups").and(new Criteria(new Column("MigrationGroups", "MIGRATION_SERVER_GROUP_ID"), (Object)groupId, 0)));
            selectQuery.addSelectColumn(new Column("MigrationGroups", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Row groupRow = dataObject.getRow("MigrationGroups");
            final Object group = groupRow.get("GROUP_NAME");
            if (group != null) {
                group_name = group.toString();
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching group id for the given group name", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return group_name;
    }
    
    public List<Long> getGroupIDsForDeviceUDID(final String udid, final String serialNo, final Long customer_id) {
        final List<Long> response = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDeviceToGroup"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDeviceToGroup"));
            selectQuery.setCriteria(this.getCustomerIdMatchCriteria(customer_id));
            final Criteria criteria1 = new Criteria(new Column("MigrationDevices", "UDID"), (Object)udid, 0);
            final Criteria criteria2 = new Criteria(new Column("MigrationDevices", "MIGRATION_SERIAL_ID"), (Object)serialNo, 0);
            selectQuery.setCriteria(criteria1.or(criteria2));
            selectQuery.addSelectColumn(new Column("MigrationDeviceToGroup", "*"));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "UDID"));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "DEVICE_ID"));
            selectQuery.addJoin(new Join("MigrationDeviceToGroup", "MigrationDevices", new String[] { "DEVICE_ID" }, new String[] { "DEVICE_ID" }, 2));
            final DataObject result = DataAccess.get(selectQuery);
            if (!result.isEmpty()) {
                final Iterator iterator = result.getRows("MigrationDeviceToGroup");
                while (iterator.hasNext()) {
                    final Row deviceGroupAssociationRow = iterator.next();
                    response.add(Long.parseLong(deviceGroupAssociationRow.get("RESOURCE_GROUP_ID").toString()));
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching groupIDs from Device Group Association Table", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public String getUDIDForDeviceID(final String deviceID) {
        String udid = null;
        try {
            final Row row = new Row("MigrationDevices");
            row.set("MIGRATION_SERVER_DEVICE_ID", (Object)deviceID);
            final DataObject dataObject = DataAccess.get("MigrationDevices", row);
            udid = dataObject.getFirstRow("MigrationDevices").get("UDID").toString();
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching UDID for the given DeviceID", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return udid;
    }
    
    public String getDeviceIdForUDID(final String deviceID, final Long config_id) {
        String udid = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDevices"));
            selectQuery.setCriteria(this.getConfigIdMatchCriteria(config_id, "MigrationDevices").and(new Criteria(new Column("MigrationDevices", "UDID"), (Object)deviceID, 0)));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "MIGRATION_SERVER_DEVICE_ID"));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "CONFIG_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationDevices");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                udid = row.get("MIGRATION_SERVER_DEVICE_ID").toString();
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching UDID for the given DeviceID", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return udid;
    }
    
    public String getMDMDeviceIdForUDID(final String deviceID, final Long config_id, final Long customer_id) {
        String udid = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDevices"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationDevices").and(new Criteria(new Column("MigrationDevices", "UDID"), (Object)deviceID, 0)));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationDevices");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                udid = row.get("DEVICE_ID").toString();
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching UDID for the given DeviceID", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return udid;
    }
    
    public Long getDeviceIDForServerDeviceID(final String serverDeviceId, final Long config_id, final Long customer_id) {
        Long deviceId = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDevices"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationDevices").and(new Criteria(new Column("MigrationDevices", "MIGRATION_SERVER_DEVICE_ID"), (Object)serverDeviceId, 0)));
            selectQuery.addSelectColumn(new Column("MigrationDevices", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationDevices");
            while (iterator.hasNext()) {
                final Row deviceRow = iterator.next();
                deviceId = Long.parseLong(deviceRow.get("DEVICE_ID").toString());
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching device id", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return deviceId;
    }
    
    public void addOrUpdateMigrationDevices(final Long config_id) {
        int status = 3;
        try {
            DataAccess.update(this.existingMigratedDevices);
        }
        catch (final Exception e) {
            status = 4;
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating migrating devices", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                final Row row = dataObject.getRow("MDMServerMigrationStatus");
                final int count = Integer.parseInt(row.get("MIGRATED_DEVICES_COUNT").toString());
                if (status == 4) {
                    row.set("FETCH_DEVICES_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                }
                else if (count + this.existingMigratedDevices.size("MigrationDevices") == Integer.parseInt(row.get("DEVICES_COUNT").toString())) {
                    row.set("DEVICES_STATUS", (Object)status);
                }
                row.set("MIGRATED_DEVICES_COUNT", (Object)(count + this.existingMigratedDevices.size("MigrationDevices")));
                dataObject.updateRow(row);
                DataAccess.update(dataObject);
            }
            catch (final Exception ex) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    public void addOrUpdateMigrationUsers(final Long config_id) {
        try {
            DataAccess.update(this.existingMigratedUsers);
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating migrating users", e);
            try {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                final Row row = dataObject.getRow("MDMServerMigrationStatus");
                final int count = Integer.parseInt(row.get("MIGRATED_USERS_COUNT").toString());
                row.set("MIGRATED_USERS_COUNT", (Object)(count + this.existingMigratedUsers.size("MigrationUsers")));
                dataObject.updateRow(row);
                DataAccess.update(dataObject);
            }
            catch (final Exception e) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Users Status", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        finally {
            try {
                final Row statusRow2 = new Row("MDMServerMigrationStatus");
                statusRow2.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject2 = DataAccess.get("MDMServerMigrationStatus", statusRow2);
                final Row row2 = dataObject2.getRow("MDMServerMigrationStatus");
                final int count2 = Integer.parseInt(row2.get("MIGRATED_USERS_COUNT").toString());
                row2.set("MIGRATED_USERS_COUNT", (Object)(count2 + this.existingMigratedUsers.size("MigrationUsers")));
                dataObject2.updateRow(row2);
                DataAccess.update(dataObject2);
            }
            catch (final Exception e2) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Users Status", e2);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    public void addOrUpdateMigrationGroups(final Long config_id) {
        try {
            DataAccess.update(this.existingMigratedGroups);
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating migrating groups", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject = DataAccess.get("MDMServerMigrationStatus", statusRow);
                final Row row = dataObject.getRow("MDMServerMigrationStatus");
                final int count = Integer.parseInt(row.get("MIGRATED_GROUPS_COUNT").toString());
                row.set("MIGRATED_GROUPS_COUNT", (Object)(count + this.existingMigratedGroups.size("MigrationGroups")));
                dataObject.updateRow(row);
                DataAccess.update(dataObject);
            }
            catch (final Exception e2) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Users Status", e2);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    public void addOrUpdateMigratedTotalApps(final Long config_id, final int totalApps) {
        try {
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("MDMServerMigrationStatus", new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0));
            if (!migrationStatusDO.isEmpty()) {
                final Row statusRow = migrationStatusDO.getFirstRow("MDMServerMigrationStatus");
                statusRow.set("APPS_COUNT", (Object)totalApps);
                migrationStatusDO.updateRow(statusRow);
                MDMUtil.getPersistence().update(migrationStatusDO);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating total src apps", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrUpdateMigratedApps(final Long config_id, final int count) {
        try {
            final Criteria criteria = new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0);
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("MDMServerMigrationStatus", criteria);
            if (!migrationStatusDO.isEmpty()) {
                final Row statusRow = migrationStatusDO.getFirstRow("MDMServerMigrationStatus");
                int currentCount = (int)statusRow.get("MIGRATED_APPS_COUNT");
                if (count == -1) {
                    statusRow.set("MIGRATED_APPS_COUNT", (Object)(++currentCount));
                }
                else {
                    statusRow.set("MIGRATED_APPS_COUNT", (Object)(currentCount + count));
                }
                migrationStatusDO.updateRow(statusRow);
                MDMUtil.getPersistence().update(migrationStatusDO);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating migrating apps", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrUpdateMigratedTotalProfiles(final Long config_id, final int totalApps) {
        try {
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("MDMServerMigrationStatus", new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0));
            if (!migrationStatusDO.isEmpty()) {
                final Row statusRow = migrationStatusDO.getFirstRow("MDMServerMigrationStatus");
                statusRow.set("PROFILES_COUNT", (Object)totalApps);
                migrationStatusDO.updateRow(statusRow);
                MDMUtil.getPersistence().update(migrationStatusDO);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating total src apps", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrUpdateMigratedProfiles(final Long config_id) {
        try {
            final Criteria criteria = new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0);
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("MDMServerMigrationStatus", criteria);
            if (!migrationStatusDO.isEmpty()) {
                final Row statusRow = migrationStatusDO.getFirstRow("MDMServerMigrationStatus");
                int currentCount = (int)statusRow.get("MIGRATED_PROFILES_COUNT");
                statusRow.set("MIGRATED_PROFILES_COUNT", (Object)(++currentCount));
                migrationStatusDO.updateRow(statusRow);
                MDMUtil.getPersistence().update(migrationStatusDO);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating migrating profiles", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrUpdateMigrationDeviceToGroupAssociation(final DataObject associationDO, final Long config_id, final Long customer_id) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDeviceToGroup"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDeviceToGroup"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationDeviceToGroup"));
            selectQuery.addSelectColumn(new Column("MigrationDeviceToGroup", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject.isEmpty()) {
                DataAccess.add(associationDO);
            }
            else {
                final Iterator iterator = associationDO.getRows("MigrationDeviceToGroup");
                while (iterator.hasNext()) {
                    String deviceID = null;
                    String resource_id = null;
                    try {
                        final Row associationRow = iterator.next();
                        deviceID = associationRow.get("DEVICE_ID").toString();
                        resource_id = associationRow.get("RESOURCE_GROUP_ID").toString();
                        final Criteria criteria1 = new Criteria(new Column("MigrationDeviceToGroup", "DEVICE_ID"), (Object)deviceID, 0);
                        final Criteria criteria2 = new Criteria(new Column("MigrationDeviceToGroup", "RESOURCE_GROUP_ID"), (Object)resource_id, 0);
                        final Iterator migrationAssociationIterator = dataObject.getRows("MigrationDeviceToGroup", criteria1.and(criteria2));
                        if (!migrationAssociationIterator.hasNext()) {
                            dataObject.addRow(associationRow);
                        }
                        else {
                            while (migrationAssociationIterator.hasNext()) {
                                final Row row = migrationAssociationIterator.next();
                                dataObject.updateRow(row);
                            }
                        }
                    }
                    catch (final NullPointerException e) {
                        APIServiceDataHandler.logger.log(Level.INFO, "NullPointer exception for device to group association", e + deviceID + resource_id);
                    }
                    DataAccess.update(dataObject);
                }
            }
        }
        catch (final Exception e2) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching migration group device association", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addOrUpdateMigrationUserGroupAssociation(final DataObject associationDO, final Long config_id, final Long customer_id) {
        int status = 3;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUserToGroup"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationUserToGroup"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationUserToGroup"));
            selectQuery.addSelectColumn(new Column("MigrationUserToGroup", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject.isEmpty()) {
                DataAccess.add(associationDO);
            }
            else {
                final Iterator iterator = associationDO.getRows("MigrationUserToGroup");
                while (iterator.hasNext()) {
                    final Row associationRow = iterator.next();
                    final String userId = associationRow.get("USER_ID").toString();
                    final String resource_id = associationRow.get("RESOURCE_GROUP_ID").toString();
                    final Criteria criteria1 = new Criteria(new Column("MigrationUserToGroup", "USER_ID"), (Object)userId, 0);
                    final Criteria criteria2 = new Criteria(new Column("MigrationUserToGroup", "RESOURCE_GROUP_ID"), (Object)resource_id, 0);
                    final Iterator migrationAssociationIterator = dataObject.getRows("MigrationUserToGroup", criteria1.and(criteria2));
                    if (!migrationAssociationIterator.hasNext()) {
                        dataObject.addRow(associationRow);
                    }
                    else {
                        while (migrationAssociationIterator.hasNext()) {
                            final Row row = migrationAssociationIterator.next();
                            dataObject.updateRow(row);
                        }
                    }
                    DataAccess.update(dataObject);
                }
            }
        }
        catch (final Exception e) {
            status = 4;
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching migration user group association", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject2 = DataAccess.get("MDMServerMigrationStatus", statusRow);
                final Row row2 = dataObject2.getRow("MDMServerMigrationStatus");
                row2.set("GROUPS_STATUS", (Object)status);
                if (status == 4) {
                    row2.set("FETCH_GROUPS_ERRORS", (Object)"Internal Server Error. Contact support with logs.");
                }
                dataObject2.updateRow(row2);
                DataAccess.update(dataObject2);
            }
            catch (final Exception ex) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    public void addOrUpdateMigrationUserDeviceAssociation(final DataObject associationDO, final Long config_id, final Long customer_id) {
        int status = 3;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationAssociation"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationAssociation"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationAssociation"));
            selectQuery.addSelectColumn(new Column("MigrationAssociation", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                DataAccess.add(associationDO);
            }
            else {
                final Iterator iterator = associationDO.getRows("MigrationAssociation");
                while (iterator.hasNext()) {
                    final Row associationRow = iterator.next();
                    final String deviceID = associationRow.get("DEVICE_ID").toString();
                    final Iterator migrationAssociationIterator = dataObject.getRows("MigrationAssociation", new Criteria(new Column("MigrationAssociation", "DEVICE_ID"), (Object)deviceID, 0));
                    while (migrationAssociationIterator.hasNext()) {
                        final Row row = migrationAssociationIterator.next();
                        row.set("USER_ID", associationRow.get("USER_ID"));
                        dataObject.updateRow(row);
                    }
                }
                DataAccess.update(dataObject);
            }
        }
        catch (final Exception e) {
            status = 4;
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching migration user device association", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                final DataObject dataObject2 = DataAccess.get("MDMServerMigrationStatus", statusRow);
                final Row row2 = dataObject2.getRow("MDMServerMigrationStatus");
                row2.set("USERS_STATUS", (Object)status);
                if (status == 4) {
                    row2.set("USERS_STATUS", (Object)"Internal Server Error. Contact support with logs.");
                }
                dataObject2.updateRow(row2);
                DataAccess.update(dataObject2);
            }
            catch (final Exception ex) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
    
    public Row getRowForDevice(final JSONObject device, final Long customer_id) throws DataAccessException {
        final Row newDevice = new Row("MigrationDevices");
        newDevice.set("CONFIG_ID", device.get("CONFIG_ID"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationDevices"));
        selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationDevices"));
        selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, device.getLong("CONFIG_ID"), "MigrationDevices").and(new Criteria(new Column("MigrationDevices", "UDID"), device.get("UDID"), 0)));
        selectQuery.addSelectColumn(new Column("MigrationDevices", "*"));
        final DataObject migratedDeviceData = MDMUtil.getPersistence().get(selectQuery);
        if (device.has("UDID")) {
            newDevice.set("UDID", device.get("UDID"));
        }
        if (device.has("IMEI")) {
            String imei = device.getString("IMEI");
            if (imei != null) {
                imei = imei.replace(" ", "");
            }
            newDevice.set("IMEI", (Object)imei);
        }
        if (device.has("DEVICE_NAME")) {
            newDevice.set("DEVICE_NAME", device.get("DEVICE_NAME"));
        }
        if (device.has("DEVICE_TYPE")) {
            newDevice.set("DEVICE_TYPE", device.get("DEVICE_TYPE"));
        }
        if (device.has("MODEL")) {
            newDevice.set("MODEL", device.get("MODEL"));
        }
        if (device.has("OS")) {
            newDevice.set("OS", device.get("OS"));
        }
        if (device.has("MANUFACTURER")) {
            newDevice.set("MANUFACTURER", device.get("MANUFACTURER"));
        }
        if (device.has("MIGRATION_SERVER_DEVICE_ID")) {
            newDevice.set("MIGRATION_SERVER_DEVICE_ID", (Object)device.get("MIGRATION_SERVER_DEVICE_ID").toString());
        }
        if (device.has("MIGRATION_SERIAL_ID")) {
            newDevice.set("MIGRATION_SERIAL_ID", device.get("MIGRATION_SERIAL_ID"));
        }
        if (device.has("EAS_ID")) {
            newDevice.set("EAS_ID", device.get("EAS_ID"));
        }
        if (migratedDeviceData.isEmpty()) {
            try {
                (this.existingMigratedDevices = (DataObject)new WritableDataObject()).addRow(newDevice);
                return newDevice;
            }
            catch (final Exception e) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while adding new device row into migration device", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        this.existingMigratedDevices = migratedDeviceData;
        try {
            final Row row = this.existingMigratedDevices.getRow("MigrationDevices", new Criteria(new Column("MigrationDevices", "MIGRATION_SERVER_DEVICE_ID"), (Object)device.get("MIGRATION_SERVER_DEVICE_ID").toString(), 0));
            if (row != null) {
                if (device.has("UDID")) {
                    row.set("UDID", newDevice.get("UDID"));
                }
                if (device.has("IMEI")) {
                    String imei2 = device.getString("IMEI");
                    if (imei2 != null) {
                        imei2 = imei2.replace(" ", "");
                    }
                    row.set("IMEI", (Object)imei2);
                }
                if (device.has("DEVICE_NAME")) {
                    row.set("DEVICE_NAME", device.get("DEVICE_NAME"));
                }
                if (device.has("DEVICE_TYPE")) {
                    row.set("DEVICE_TYPE", device.get("DEVICE_TYPE"));
                }
                if (device.has("MODEL")) {
                    row.set("MODEL", device.get("MODEL"));
                }
                if (device.has("OS")) {
                    row.set("OS", device.get("OS"));
                }
                if (device.has("MANUFACTURER")) {
                    row.set("MANUFACTURER", device.get("MANUFACTURER"));
                }
                if (device.has("MIGRATION_SERVER_DEVICE_ID")) {
                    row.set("MIGRATION_SERVER_DEVICE_ID", device.get("MIGRATION_SERVER_DEVICE_ID"));
                }
                if (device.has("MIGRATION_SERIAL_ID")) {
                    row.set("MIGRATION_SERIAL_ID", device.get("MIGRATION_SERIAL_ID"));
                }
                if (device.has("EAS_ID")) {
                    newDevice.set("EAS_ID", device.get("EAS_ID"));
                }
                this.existingMigratedDevices.updateRow(row);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while adding new device row into migration device", e);
        }
        return newDevice;
    }
    
    public Row getRowForGroup(final JSONObject group, final Long customer_id) {
        try {
            final Row groupRow = new Row("MigrationGroups");
            groupRow.set("CONFIG_ID", group.get("CONFIG_ID"));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationGroups"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationGroups"));
            Criteria criteria = null;
            if (group.has("MIGRATION_SERVER_GROUP_ID")) {
                criteria = new Criteria(new Column("MigrationGroups", "MIGRATION_SERVER_GROUP_ID"), group.get("MIGRATION_SERVER_GROUP_ID"), 0);
            }
            else if (group.has("GROUP_NAME")) {
                criteria = new Criteria(new Column("MigrationGroups", "GROUP_NAME"), (Object)group.getString("GROUP_NAME"), 0);
            }
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, group.getLong("CONFIG_ID"), "MigrationGroups").and(criteria));
            selectQuery.addSelectColumn(new Column("MigrationGroups", "*"));
            final DataObject migratedGroupsData = MDMUtil.getPersistence().get(selectQuery);
            if (group.has("GROUP_NAME")) {
                groupRow.set("GROUP_NAME", group.get("GROUP_NAME"));
            }
            if (group.has("MIGRATION_SERVER_GROUP_ID")) {
                groupRow.set("MIGRATION_SERVER_GROUP_ID", group.get("MIGRATION_SERVER_GROUP_ID"));
            }
            if (group.has("CONFIG_ID")) {
                groupRow.set("CONFIG_ID", group.get("CONFIG_ID"));
            }
            if (group.has("GROUP_TYPE")) {
                groupRow.set("GROUP_TYPE", group.get("GROUP_TYPE"));
            }
            if (migratedGroupsData.isEmpty()) {
                (this.existingMigratedGroups = (DataObject)new WritableDataObject()).addRow(groupRow);
                return groupRow;
            }
            this.existingMigratedGroups = migratedGroupsData;
            final Criteria criteria2 = new Criteria(new Column("MigrationGroups", "GROUP_NAME"), group.get("GROUP_NAME"), 0);
            final Criteria criteria3 = new Criteria(new Column("MigrationGroups", "CONFIG_ID"), group.get("CONFIG_ID"), 0);
            final Row specificGroupRow = migratedGroupsData.getRow("MigrationGroups", criteria2.and(criteria3));
            if (group.has("GROUP_NAME")) {
                specificGroupRow.set("GROUP_NAME", group.get("GROUP_NAME"));
            }
            if (group.has("MIGRATION_SERVER_GROUP_ID")) {
                specificGroupRow.set("MIGRATION_SERVER_GROUP_ID", group.get("MIGRATION_SERVER_GROUP_ID"));
            }
            if (group.has("CONFIG_ID")) {
                specificGroupRow.set("CONFIG_ID", group.get("CONFIG_ID"));
            }
            if (group.has("GROUP_TYPE")) {
                specificGroupRow.set("GROUP_TYPE", group.get("GROUP_TYPE"));
            }
            this.existingMigratedGroups.updateRow(specificGroupRow);
            return specificGroupRow;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching selected group to update", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Row getRowForUser(final JSONObject user, final Long customer_id) throws DataAccessException {
        final Row userRow = new Row("MigrationUsers");
        userRow.set("CONFIG_ID", user.get("CONFIG_ID"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUsers"));
        selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationUsers"));
        selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, user.getLong("CONFIG_ID"), "MigrationUsers").and(new Criteria(new Column("MigrationUsers", "MIGRATION_SERVER_USER_ID"), (Object)user.get("MIGRATION_SERVER_USER_ID").toString(), 0)));
        selectQuery.addSelectColumn(new Column("MigrationUsers", "*"));
        final DataObject migratedUserData = MDMUtil.getPersistence().get(selectQuery);
        if (user.has("MIGRATION_SERVER_USER_ID")) {
            userRow.set("MIGRATION_SERVER_USER_ID", user.get("MIGRATION_SERVER_USER_ID"));
        }
        if (user.has("USER_NAME")) {
            userRow.set("USER_NAME", user.get("USER_NAME"));
        }
        if (user.has("STATUS")) {
            userRow.set("STATUS", user.get("STATUS"));
        }
        if (user.has("EMAIL_ID")) {
            userRow.set("EMAIL_ID", user.get("EMAIL_ID"));
        }
        if (user.has("PHONE_NUMBER")) {
            userRow.set("PHONE_NUMBER", user.get("PHONE_NUMBER"));
        }
        if (user.has("DOMAIN")) {
            userRow.set("DOMAIN", user.get("DOMAIN"));
        }
        if (user.has("IS_AD_SYNCED_USER")) {
            userRow.set("IS_AD_SYNCED_USER", (Object)user.getBoolean("IS_AD_SYNCED_USER"));
        }
        if (migratedUserData.isEmpty()) {
            try {
                (this.existingMigratedUsers = (DataObject)new WritableDataObject()).addRow(userRow);
                return userRow;
            }
            catch (final Exception e) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "Error on accessing migrated users", e);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        this.existingMigratedUsers = migratedUserData;
        try {
            final Row row = this.existingMigratedUsers.getRow("MigrationUsers", new Criteria(new Column("MigrationUsers", "MIGRATION_SERVER_USER_ID"), userRow.get("MIGRATION_SERVER_USER_ID"), 0));
            if (user.has("MIGRATION_SERVER_USER_ID")) {
                row.set("MIGRATION_SERVER_USER_ID", user.get("MIGRATION_SERVER_USER_ID"));
            }
            if (user.has("USER_NAME")) {
                row.set("USER_NAME", user.get("USER_NAME"));
            }
            if (user.has("STATUS")) {
                row.set("STATUS", user.get("STATUS"));
            }
            if (user.has("EMAIL_ID")) {
                row.set("EMAIL_ID", user.get("EMAIL_ID"));
            }
            if (user.has("PHONE_NUMBER")) {
                row.set("PHONE_NUMBER", user.get("PHONE_NUMBER"));
            }
            if (user.has("DOMAIN")) {
                row.set("DOMAIN", user.get("DOMAIN"));
            }
            this.existingMigratedUsers.updateRow(row);
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error on accessing migrated users", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return userRow;
    }
    
    public JSONObject getWebClipURL(final Long configID, final Long customer_id) throws APIHTTPException, Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            if (configID != null) {
                this.validateConfiguration(configID);
            }
            final SelectQuery selectQuery = this.getApiConfigQuery();
            final Criteria configIDCrit = new Criteria(Column.getColumn("APIServiceConfiguration", "CONFIG_ID"), (Object)configID, 0);
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("APIServiceConfiguration"));
            selectQuery.setCriteria(configIDCrit.and(this.getCustomerIdMatchCriteria(customer_id)));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "*"));
            final DataObject dO = DataAccess.get(selectQuery);
            final Row urlRow = dO.getFirstRow("APIServiceConfiguration");
            JSONObject result = new JSONObject();
            result = urlRow.getAsJSON();
            final String eurl = result.get("new_enrollment_url").toString();
            final int serviceId = result.getInt("SERVICE_ID".toLowerCase());
            final String webClipURL = this.generateWebClipURL(eurl, configID, serviceId);
            responseJSON.put("webclip_url", (Object)webClipURL);
        }
        catch (final APIHTTPException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, " APIHTTPException occured in getWebClipURL", e);
            throw e;
        }
        catch (final Exception e2) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception occured in getWebClipURL", e2);
            throw e2;
        }
        return responseJSON;
    }
    
    public JSONObject getAPIServicesList() throws DataAccessException, JSONException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("APIServices"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = DataAccess.get(sq);
        final JSONArray services = new JSONArray();
        final String product = ProductUrlLoader.getInstance().getValue("productcode");
        if (!dO.isEmpty()) {
            final Iterator<Row> rows = dO.getRows("APIServices");
            while (rows.hasNext()) {
                final JSONObject row = rows.next().getAsJSON();
                if (row.getInt("SERVICE_ID".toLowerCase()) != 5) {
                    if (product.equals("MDMP")) {
                        if (row.getInt("SERVICE_ID".toLowerCase()) == 2) {
                            continue;
                        }
                        services.put((Object)row);
                    }
                    else if (product.equals("DCEE")) {
                        if (row.getInt("SERVICE_ID".toLowerCase()) == 7) {
                            continue;
                        }
                        services.put((Object)row);
                    }
                    else {
                        services.put((Object)row);
                    }
                }
            }
        }
        final JSONObject returnJson = new JSONObject();
        returnJson.put("APIServices", (Object)services);
        return returnJson;
    }
    
    public JSONObject getMigrationStatusForServiceId(final Long customerId, final int serviceId) {
        JSONObject response = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
            selectQuery.addJoin(new Join("APIServiceConfiguration", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            selectQuery.addJoin(new Join("APIServiceConfiguration", "MDMServerMigrationStatus", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(new Column("APIServiceConfiguration", "SERVICE_ID"), (Object)serviceId, 0)));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("MDMServerMigrationStatus", "*"));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                final int deviceStatus = (int)dmDataSetWrapper.getValue("DEVICES_STATUS");
                final int userStatus = (int)dmDataSetWrapper.getValue("USERS_STATUS");
                final int groupStatus = (int)dmDataSetWrapper.getValue("GROUPS_STATUS");
                final int profileStatus = (int)dmDataSetWrapper.getValue("PROFILES_STATUS");
                final int appStatus = (int)dmDataSetWrapper.getValue("APPS_STATUS");
                response = this.getMigrationStatusAsResponse(deviceStatus, userStatus, groupStatus, profileStatus, appStatus);
                if (deviceStatus == 4) {
                    response.put("migration_device_error", dmDataSetWrapper.getValue("FETCH_DEVICES_ERRORS"));
                }
                if (userStatus == 4) {
                    response.put("migration_user_error", dmDataSetWrapper.getValue("FETCH_USERS_ERRORS"));
                }
                if (groupStatus == 4) {
                    response.put("migration_group_error", dmDataSetWrapper.getValue("FETCH_GROUPS_ERRORS"));
                }
                if (profileStatus == 4) {
                    response.put("migration_profile_error", dmDataSetWrapper.getValue("FETCH_PROFILES_ERRORS"));
                }
                if (appStatus == 4) {
                    response.put("migration_apps_error", dmDataSetWrapper.getValue("FETCH_APPS_ERRORS"));
                }
            }
            return response;
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while getting migration status for service id", e);
            return response;
        }
    }
    
    public JSONObject getAPIServiceConfigurationsList(final Long customer_id) throws DataAccessException, JSONException, APIHTTPException {
        final JSONObject response = new JSONObject();
        final JSONArray configurations = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
            selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0));
            selectQuery.addJoin(new Join("APIServiceConfiguration", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("APIServiceConfiguration");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final JSONObject config = new JSONObject();
                config.put("CONFIG_ID", (Object)row.get("CONFIG_ID").toString());
                config.put("NAME", (Object)row.get("NAME").toString());
                config.put("SERVICE_ID", (Object)row.get("SERVICE_ID").toString());
                configurations.put((Object)config);
            }
            response.put("apiserviceconfiguration", (Object)configurations);
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching api service config details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public void validateConfiguration(final long configID) throws DataAccessException, APIHTTPException {
        final Criteria criteria = new Criteria(Column.getColumn("APIServiceConfiguration", "CONFIG_ID"), (Object)configID, 0);
        final SelectQuery selectQuery1 = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
        final Column column = new Column("APIServiceConfiguration", "CONFIG_ID");
        selectQuery1.addSelectColumn(column);
        selectQuery1.setCriteria(criteria);
        final DataObject dataObject = DataAccess.get(selectQuery1);
        final Iterator<Row> rows = dataObject.getRows("APIServiceConfiguration");
        final ArrayList<Long> serviceConfig = new ArrayList<Long>();
        while (rows.hasNext()) {
            serviceConfig.add(Long.valueOf(String.valueOf(rows.next().get("CONFIG_ID"))));
        }
        if (serviceConfig.size() == 0) {
            throw new APIHTTPException("COM0008", new Object[] { configID });
        }
    }
    
    public JSONObject getAPIServiceConfigDetails(final JSONObject json) throws APIHTTPException {
        try {
            final JSONObject returnJson = new JSONObject();
            Long configID = null;
            Integer serviceID = null;
            Integer authType = null;
            String configName = null;
            configID = JSONUtil.optLong(json, "CONFIG_ID");
            serviceID = JSONUtil.optInteger(json, "SERVICE_ID");
            authType = JSONUtil.optInteger(json, "TYPE");
            configName = JSONUtil.optString(json, "NAME", null);
            final Long customerId = JSONUtil.optLong(json, "CUSTOMER_ID");
            if (configID != null) {
                this.validateConfiguration(configID);
            }
            final SelectQuery selectQuery = this.getApiConfigQuery();
            selectQuery.addJoin(new Join("APIServiceConfiguration", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            if (customerId != null) {
                selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customerId, 0));
            }
            if (configID != null) {
                final Criteria configCriteria = new Criteria(Column.getColumn("APIServiceConfiguration", "CONFIG_ID"), (Object)configID, 0);
                if (authType != null) {
                    final Criteria authCrit1 = new Criteria(Column.getColumn("APIAuthInfo", "TYPE"), (Object)authType, 0);
                    selectQuery.setCriteria(configCriteria.and(authCrit1));
                }
                else {
                    selectQuery.setCriteria(configCriteria);
                }
            }
            else if (!MDMStringUtils.isEmpty(configName)) {
                final Criteria apiConfigCriteria = new Criteria(new Column("APIServiceConfiguration", "NAME"), (Object)configName, 2, true);
                selectQuery.setCriteria(apiConfigCriteria);
            }
            else {
                final Criteria serviceCrit = new Criteria(Column.getColumn("APIServiceConfiguration", "SERVICE_ID"), (Object)serviceID, 0);
                final Criteria authCrit2 = new Criteria(Column.getColumn("APIAuthInfo", "TYPE"), (Object)authType, 0);
                selectQuery.setCriteria(serviceCrit.and(authCrit2));
            }
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = DataAccess.get(selectQuery);
            if (!dO.isEmpty()) {
                final Iterator iterator = dO.getRows("APIServiceConfiguration");
                while (iterator.hasNext()) {
                    final Row apiServiceConfig = iterator.next();
                    returnJson.put("APIServiceConfiguration", (Object)apiServiceConfig.getAsJSON());
                    final DataObject authDO = dO.getDataObject(dO.getTableNames(), apiServiceConfig);
                    final SortColumn sortColumn = new SortColumn("APIAuthInfo", "AUTH_ID", true);
                    authDO.sortRows("APIAuthInfo", new SortColumn[] { sortColumn });
                    final JSONArray authArray = this.getAuthDetailsFromDO(authDO);
                    returnJson.put("APIServiceAuthDetails", (Object)authArray);
                }
            }
            return returnJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "APIHTTPException occured in getServiceConfiguration", ex);
                throw (APIHTTPException)ex;
            }
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception occured in getServiceConfiguration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean ifConfigExists(final Long customer_id, final Integer serviceID) {
        boolean response = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerAPIServiceConfigAssociation"));
            selectQuery.addSelectColumn(new Column("CustomerAPIServiceConfigAssociation", "CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("APIServiceConfiguration", "SERVICE_ID"));
            selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0));
            selectQuery.addJoin(new Join("CustomerAPIServiceConfigAssociation", "APIServiceConfiguration", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("APIServiceConfiguration");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                response = (Integer.parseInt(row.get("SERVICE_ID").toString()) == serviceID);
                if (response) {
                    return response;
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while checking existing configs", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    public JSONObject deleteServiceConfig(final JSONObject json) throws APIHTTPException {
        try {
            final Long configID = Long.parseLong(json.get("CONFIG_ID").toString());
            this.validateConfiguration(configID);
            final SelectQuery selectQuery = this.getApiConfigQuery();
            selectQuery.addJoin(new Join("APIServiceConfiguration", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            final Criteria configIDCrit = new Criteria(Column.getColumn("APIServiceConfiguration", "CONFIG_ID"), (Object)configID, 0);
            selectQuery.setCriteria(configIDCrit);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = DataAccess.get(selectQuery);
            final Iterator<Row> authIdIterator = dO.getRows("APIAuthInfo");
            final List<Long> authIdArray = new ArrayList<Long>();
            while (authIdIterator.hasNext()) {
                final Row authRow = authIdIterator.next();
                authIdArray.add((Long)authRow.get("AUTH_ID"));
            }
            final Criteria authCriteria = new Criteria(new Column("APIAuthInfo", "AUTH_ID"), (Object)authIdArray.toArray(), 8);
            dO.deleteRows("APIAuthInfo", authCriteria);
            dO.deleteRows("CustomerAPIServiceConfigAssociation", new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CONFIG_ID"), (Object)configID, 0));
            dO.deleteRows("APIServiceConfiguration", configIDCrit);
            DataAccess.update(dO);
            return new JSONObject(json.toString());
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                APIServiceDataHandler.logger.log(Level.SEVERE, "APIHTTPException occured in deleteServiceConfiguration", ex);
                throw (APIHTTPException)ex;
            }
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception occured in deleteServiceConfiguration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void removeMultipleAuthRows(final DataObject dO) throws DataAccessException {
        final Iterator<Row> rows = dO.getRows("APIAuthInfo");
        Long time = -1L;
        Long authID = -1L;
        while (rows.hasNext()) {
            final Row row = rows.next();
            final Long newTime = Long.parseLong(row.get("MODIFIED_AT").toString());
            if (newTime > time) {
                time = new Long(newTime);
                authID = Long.parseLong(row.get("AUTH_ID").toString());
            }
        }
        Criteria authCrit = new Criteria(Column.getColumn("APIAuthInfo", "AUTH_ID"), (Object)authID, 1);
        dO.deleteRows("APIAuthInfo", authCrit);
        authCrit = new Criteria(Column.getColumn("CustomHeadersAuthInfo", "AUTH_ID"), (Object)authID, 1);
        dO.deleteRows("CustomHeadersAuthInfo", authCrit);
    }
    
    public void setAuthorizationFailed(final Long configId) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MDMServerMigrationStatus");
        updateQuery.setCriteria(new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)configId, 0));
        updateQuery.setUpdateColumn("DEVICES_STATUS", (Object)4);
        updateQuery.setUpdateColumn("USERS_STATUS", (Object)4);
        updateQuery.setUpdateColumn("GROUPS_STATUS", (Object)4);
        updateQuery.setUpdateColumn("PROFILES_STATUS", (Object)4);
        updateQuery.setUpdateColumn("APPS_STATUS", (Object)4);
        updateQuery.setUpdateColumn("FETCH_DEVICES_ERRORS", (Object)"Invalid API service configuration details");
        updateQuery.setUpdateColumn("FETCH_USERS_ERRORS", (Object)"Invalid API service configuration details");
        updateQuery.setUpdateColumn("FETCH_GROUPS_ERRORS", (Object)"Invalid API service configuration details");
        updateQuery.setUpdateColumn("FETCH_PROFILES_ERRORS", (Object)"Invalid API service configuration details");
        updateQuery.setUpdateColumn("FETCH_APPS_ERRORS", (Object)"Invalid API service configuration details");
        SyMUtil.getPersistence().update(updateQuery);
    }
    
    public SelectQuery getADSyncedDetailsQuery(final String email) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMDomain"));
        selectQuery.addSelectColumn(new Column("DMDomain", "NAME"));
        selectQuery.addSelectColumn(new Column("DMDomain", "CUSTOMER_ID"));
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        subQuery.addJoin(new Join("Resource", "DirObjRegStrVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        subQuery.setCriteria(new Criteria(new Column("DirObjRegStrVal", "VALUE"), (Object)email, 0));
        subQuery.addSelectColumn(new Column("Resource", "DOMAIN_NETBIOS_NAME"));
        selectQuery.addSelectColumn((Column)new DerivedColumn("subQuery", subQuery));
        return selectQuery;
    }
    
    public boolean isADConfigured(final String domain) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMDomain"));
            selectQuery.addSelectColumn(new Column("DMDomain", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("DMDomain");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (row.get("NAME").toString().equalsIgnoreCase(domain)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching domain details", e);
        }
        return false;
    }
    
    public String getADDomainName(final String email) throws Exception {
        String ADDomain = "";
        final SelectQuery selectQuery = this.getADSyncedDetailsQuery(email);
        final DMDataSetWrapper dataset = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dataset.next()) {
            ADDomain = dataset.getValue("NAME").toString();
        }
        return ADDomain;
    }
    
    public boolean isADSyncedUser(final String email) throws DataAccessException {
        final SelectQuery selectQuery = this.getADSyncedDetailsQuery(email);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        return !dataObject.isEmpty();
    }
    
    public JSONArray getProfileIDsForDeviceId(final String udid) {
        final JSONArray profileIDs = new JSONArray();
        try {
            final DataObject migratedProfileDO = MDMUtil.getPersistence().get("MigrationDeviceToProfile", new Criteria(new Column("MigrationDeviceToProfile", "UDID"), (Object)udid, 0));
            if (!migratedProfileDO.isEmpty()) {
                final Iterator profileIterator = migratedProfileDO.getRows("MigrationDeviceToProfile");
                while (profileIterator.hasNext()) {
                    final Row profileRow = profileIterator.next();
                    profileIDs.put((Object)String.valueOf(profileRow.get("PROFILE_ID")));
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, e, () -> "Exception occurred while getting profileId for association of - " + s);
        }
        return profileIDs;
    }
    
    public JSONArray getAppDetailsForDeviceId(final String udid) {
        final JSONArray appDetails = new JSONArray();
        try {
            final DataObject migratedAppDO = MDMUtil.getPersistence().get("MigrationDeviceToApp", new Criteria(new Column("MigrationDeviceToApp", "UDID"), (Object)udid, 0));
            if (!migratedAppDO.isEmpty()) {
                final Iterator appIterator = migratedAppDO.getRows("MigrationDeviceToApp");
                while (appIterator.hasNext()) {
                    final Row appRow = appIterator.next();
                    final JSONObject appInfo = new JSONObject();
                    appInfo.put("app_id", appRow.get("APP_ID"));
                    appInfo.put("release_label_id", appRow.get("RELEASE_LABEL_ID"));
                    appDetails.put((Object)appInfo);
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception occurred while getting app details for  device association of {0} ....{1}", new Object[] { udid, e });
        }
        return appDetails;
    }
    
    public String getUserIDForDeviceUDID(final String udid) {
        String user_id = "";
        try {
            final DataObject migratedProfileDO = MDMUtil.getPersistence().get("MigrationDeviceToProfile", new Criteria(new Column("MigrationDeviceToProfile", "UDID"), (Object)udid, 0, false));
            if (!migratedProfileDO.isEmpty()) {
                final Row profileRow = migratedProfileDO.getRow("MigrationDeviceToProfile");
                user_id = String.valueOf(profileRow.get("USER_ID"));
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, e, () -> "Exception occurred while getting user id for association of - " + s);
        }
        return user_id;
    }
    
    public void updateMigrationStatus(final Long config_id, final int status, final String errorMsg, final int type) {
        try {
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("MDMServerMigrationStatus", new Criteria(new Column("MDMServerMigrationStatus", "CONFIG_ID"), (Object)config_id, 0));
            if (migrationStatusDO.isEmpty()) {
                final Row statusRow = new Row("MDMServerMigrationStatus");
                statusRow.set("CONFIG_ID", (Object)config_id);
                if (type == 2) {
                    statusRow.set("APPS_STATUS", (Object)status);
                    statusRow.set("FETCH_APPS_ERRORS", (Object)errorMsg);
                }
                else if (type == 1) {
                    statusRow.set("PROFILES_STATUS", (Object)status);
                    statusRow.set("FETCH_PROFILES_ERRORS", (Object)errorMsg);
                }
                migrationStatusDO.addRow(statusRow);
            }
            else {
                final Row statusRow = migrationStatusDO.getFirstRow("MDMServerMigrationStatus");
                if (type == 2) {
                    statusRow.set("APPS_STATUS", (Object)status);
                    statusRow.set("FETCH_APPS_ERRORS", (Object)errorMsg);
                }
                else if (type == 1) {
                    statusRow.set("PROFILES_STATUS", (Object)status);
                    statusRow.set("FETCH_PROFILES_ERRORS", (Object)errorMsg);
                }
                migrationStatusDO.updateRow(statusRow);
            }
            MDMUtil.getPersistence().update(migrationStatusDO);
        }
        catch (final Exception ex) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while updating Migration Status", ex);
        }
    }
    
    public String getProfileIDFromProfileName(final String profileName) {
        String profileID = "";
        try {
            final DataObject profileDO = MDMUtil.getPersistence().get("Profile", new Criteria(new Column("Profile", "PROFILE_NAME"), (Object)profileName, 0, false));
            if (!profileDO.isEmpty()) {
                final Row profileRow = profileDO.getRow("Profile");
                profileID = String.valueOf(profileRow.get("PROFILE_ID"));
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, e, () -> "Exception occurred while getting profile id from profile name- " + s);
        }
        return profileID;
    }
    
    public void deleteMigratedProfiles(final JSONArray profiles, final Long user_id, final Long customer_id) {
        APIServiceDataHandler.logger.log(Level.INFO, "Going to remove already populated Profiles ...");
        try {
            final ProfileFacade profileFacade = new ProfileFacade();
            final Long login_id = DMUserHandler.getLoginIdForUserId(Long.valueOf((long)user_id));
            final JSONArray profileIdsTodelete = new JSONArray();
            final List<Long> profileList = new ArrayList<Long>();
            JSONObject profile = new JSONObject();
            final JSONObject profileToDelete = new JSONObject();
            for (int i = 0; i < profiles.length(); ++i) {
                profile = profiles.getJSONObject(i);
                final String profile_name = profile.getString("profile_name");
                if (new ProfileHandler().checkProfileNameExist(customer_id, profile_name, profile.optInt("profile_type", 1), null)) {
                    profileIdsTodelete.put((Object)this.getProfileIDFromProfileName(profile_name));
                    profileList.add(Long.valueOf(this.getProfileIDFromProfileName(profile_name)));
                }
            }
            if (profileIdsTodelete != null && profileIdsTodelete.length() > 0) {
                profileToDelete.put("msg_header", (Object)new JSONObject().put("filters", (Object)new JSONObject().put("user_id", (Object)user_id).put("customer_id", (Object)customer_id).put("login_id", (Object)login_id)));
                profileToDelete.put("msg_body", (Object)new JSONObject().put("profile_ids", (Object)profileIdsTodelete));
                profileToDelete.put("move_to_trash", (Object)Boolean.TRUE);
                profileFacade.deleteOrTrashProfile(profileToDelete);
                new ProfileUtil().markAsDeleted(profileList, customer_id, user_id);
                profileToDelete.remove("move_to_trash");
                profileToDelete.put("permanent_delete", (Object)Boolean.TRUE);
                profileFacade.deleteOrTrashProfile(profileToDelete);
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception occurred while removed migrated profiles- ", e);
        }
    }
    
    public Long getUserIDForServerUserID(final String user_id, final Long customer_id, final Long config_id) {
        Long userId = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUsers"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationUsers"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationUsers").and(new Criteria(new Column("MigrationUsers", "MIGRATION_SERVER_USER_ID"), (Object)user_id, 0)));
            selectQuery.addSelectColumn(new Column("MigrationUsers", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MigrationUsers");
            while (iterator.hasNext()) {
                final Row deviceRow = iterator.next();
                userId = Long.parseLong(deviceRow.get("USER_ID").toString());
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while fetching device id {0}", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return userId;
    }
    
    public Long getUserResourceIdForUserName(final String userName, final Long customer_id) throws DataAccessException {
        Row row = new Row("Resource");
        row.set("NAME", (Object)userName);
        row.set("CUSTOMER_ID", (Object)customer_id);
        final DataObject dataObject = MDMUtil.getPersistence().get("Resource", row);
        row = dataObject.getFirstRow("Resource");
        return Long.parseLong(row.get("RESOURCE_ID").toString());
    }
    
    public String getUserIdForUserName(final String userName, final Long customer_id, final Long config_id) throws DataAccessException {
        String userId = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUsers"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationUsers"));
            final Criteria criteria = new Criteria(new Column("MigrationUsers", "USER_NAME"), (Object)userName, 0);
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationUsers").and(criteria));
            selectQuery.addSelectColumn(new Column("MigrationUsers", "*"));
            final DataObject userDO = DataAccess.get(selectQuery);
            if (!userDO.isEmpty()) {
                userId = userDO.getFirstRow("MigrationUsers").get("USER_ID").toString();
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while getting user id for user name", e);
        }
        return userId;
    }
    
    public List<Long> getGroupIdsForUsername(final String userName, final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationUsers"));
        selectQuery.addJoin(new Join("MigrationUsers", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
        selectQuery.addJoin(new Join("MigrationUsers", "MigrationUserToGroup", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
        selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.addSelectColumn(new Column("MigrationUserToGroup", "RESOURCE_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MigrationUsers", "USER_NAME"));
        final DMDataSetWrapper dataset = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final List<Long> groupIds = new ArrayList<Long>();
        while (dataset.next()) {
            if (dataset.getValue("USER_NAME").toString().equalsIgnoreCase(userName)) {
                final Object groupId = dataset.getValue("RESOURCE_GROUP_ID");
                if (groupId == null) {
                    continue;
                }
                groupIds.add(Long.parseLong(groupId.toString()));
            }
        }
        return groupIds;
    }
    
    public String getDomainIDFromName(final Long customer_id, final String domainName) throws APIHTTPException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
            query.addJoin(new Join("DMDomain", "DMManagedDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
            query.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
            Criteria criteria = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customer_id, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DMManagedDomain", "AD_DOMAIN_NAME"), (Object)domainName, 2));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Row row = dataObject.getRow("DMDomain");
            return String.valueOf(row.get("DOMAIN_ID"));
        }
        catch (final APIHTTPException e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, " Exception at program directoryFacade APIServiceDataHandler:getDomainIDFromName {0}", e);
            throw e;
        }
        catch (final Exception e2) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception at program directoryFacade APIServiceDataHandler:getDomainIDFromName {0}", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String getMigrationGroupIdForServerGroupID(final String groupId, final Long customer_id) throws Exception {
        String response = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationGroups"));
        selectQuery.addJoin(new Join("MigrationGroups", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0));
        selectQuery.addSelectColumn(new Column("MigrationGroups", "GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MigrationGroups", "MIGRATION_SERVER_GROUP_ID"));
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dataSetWrapper.next()) {
            if (dataSetWrapper.getValue("MIGRATION_SERVER_GROUP_ID").toString().equalsIgnoreCase(groupId)) {
                response = (String)dataSetWrapper.getValue("GROUP_ID");
            }
        }
        return response;
    }
    
    public Long getResourceGroupIdForGroupId(final String groupId, final Long customer_id) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "CustomerAPIServiceConfigAssociation", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        selectQuery.addJoin(new Join("Resource", "MigrationGroups", new String[] { "NAME" }, new String[] { "GROUP_NAME" }, 1));
        final Criteria criteria = new Criteria(new Column("MigrationGroups", "MIGRATION_SERVER_GROUP_ID"), (Object)groupId, 0);
        selectQuery.setCriteria(criteria.and(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0)));
        selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        return (Long)MDMUtil.getPersistence().get(selectQuery).getFirstRow("Resource").get("RESOURCE_ID");
    }
    
    public Long getResourceGroupIdForGroupName(final String groupName, final Long customer_id) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "CustomerAPIServiceConfigAssociation", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        selectQuery.addJoin(new Join("Resource", "MigrationGroups", new String[] { "NAME" }, new String[] { "GROUP_NAME" }, 1));
        final Criteria criteria = new Criteria(new Column("MigrationGroups", "GROUP_NAME"), (Object)groupName, 0);
        selectQuery.setCriteria(criteria.and(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0)));
        selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        return (Long)MDMUtil.getPersistence().get(selectQuery).getFirstRow("Resource").get("RESOURCE_ID");
    }
    
    public List getOrgIdsToBeMigrated(final Long customer_id, final Long config_id) {
        final List orgIds = new LinkedList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MigrationOrganizations"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationOrganizations"));
            final Criteria criteria1 = new Criteria(Column.getColumn("MigrationOrganizations", "TO_BE_MIGRATED"), (Object)true, 0);
            final Criteria criteria2 = this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationOrganizations");
            selectQuery.setCriteria(criteria1.and(criteria2));
            selectQuery.addSelectColumn(new Column("MigrationOrganizations", "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MigrationOrganizations");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    orgIds.add(row.get("OLD_SERVER_ORG_ID"));
                }
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while fetching org detials to be migrated");
        }
        return orgIds;
    }
    
    public void setMigrationSuccessStatus(final long config_id, final long customer_id, final String column) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMServerMigrationStatus"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MDMServerMigrationStatus"));
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MDMServerMigrationStatus"));
            selectQuery.addSelectColumn(new Column("MDMServerMigrationStatus", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Row row = dataObject.getFirstRow("MDMServerMigrationStatus");
            row.set(column, (Object)3);
            dataObject.updateRow(row);
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Exception while setting migration status");
        }
    }
    
    public void updateGroupLastModifiedBy(final Long customer_id, final Long user_id, final String groupName) throws DataAccessException {
        final Row row = new Row("Resource");
        row.set("CUSTOMER_ID", (Object)customer_id);
        row.set("NAME", (Object)groupName);
        row.set("RESOURCE_TYPE", (Object)101);
        final DataObject dataObject = MDMUtil.getPersistence().get("Resource", row);
        final Long resourceId = Long.parseLong(dataObject.getFirstRow("Resource").get("RESOURCE_ID").toString());
        final UpdateQueryImpl updateQuery = new UpdateQueryImpl("CustomGroupExtn");
        updateQuery.setCriteria(new Criteria(new Column("CustomGroupExtn", "RESOURCE_ID"), (Object)resourceId, 0));
        updateQuery.setUpdateColumn("LAST_MODIFIED_BY", (Object)user_id);
        updateQuery.setUpdateColumn("CREATED_BY", (Object)user_id);
        MDMUtil.getPersistence().update((UpdateQuery)updateQuery);
    }
    
    public JSONObject getMigrationTypeWiseCount(final Long customer_id) {
        final JSONObject trackingDetails = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMServerMigrationStatus"));
            selectQuery.addJoin(new Join("MDMServerMigrationStatus", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
            selectQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0));
            selectQuery.addSelectColumn(new Column("MDMServerMigrationStatus", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MDMServerMigrationStatus");
                trackingDetails.put("migrated_devices", row.get("MIGRATED_DEVICES_COUNT"));
                trackingDetails.put("migrated_users", row.get("MIGRATED_USERS_COUNT"));
                trackingDetails.put("migrated_groups", row.get("MIGRATED_GROUPS_COUNT"));
                trackingDetails.put("migrated_profiles", row.get("MIGRATED_PROFILES_COUNT"));
                trackingDetails.put("migrated_apps", row.get("MIGRATED_APPS_COUNT"));
                final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(new Table("APIServiceConfiguration"));
                selQuery.addJoin(new Join("APIServiceConfiguration", "CustomerAPIServiceConfigAssociation", new String[] { "CONFIG_ID" }, new String[] { "CONFIG_ID" }, 1));
                selQuery.setCriteria(new Criteria(new Column("CustomerAPIServiceConfigAssociation", "CUSTOMER_ID"), (Object)customer_id, 0));
                selQuery.addSelectColumn(new Column("APIServiceConfiguration", "SERVICE_ID"));
                final DataObject dataObj = MDMUtil.getPersistence().get(selectQuery);
                if (dataObj != null && !dataObj.isEmpty()) {
                    final Row serRow = dataObject.getFirstRow("APIServiceConfiguration");
                    final int service_id = Integer.valueOf((String)serRow.get("SERVICE_ID"));
                    trackingDetails.put("source", (Object)new APIServiceDataHandler().getServiceType(service_id));
                }
                final String productcode = ProductUrlLoader.getInstance().getValue("productcode");
                String destination = "";
                if (productcode.equals("MDMP")) {
                    destination = "MDM_ONPREMISE";
                }
                else if (productcode.equals("MDMODEE")) {
                    destination = "MDM_CLOUD";
                }
                else if (productcode.equals("DCEE")) {
                    destination = "DC_ONPREMISE";
                }
                else {
                    destination = "DC_CLOUD";
                }
                trackingDetails.put("destination", (Object)destination);
            }
            else {
                trackingDetails.put("migrated_devices", 0);
                trackingDetails.put("migrated_users", 0);
                trackingDetails.put("migrated_groups", 0);
                trackingDetails.put("migrated_profiles", 0);
                trackingDetails.put("migrated_apps", 0);
                trackingDetails.put("source", (Object)"");
                trackingDetails.put("destination", (Object)"");
            }
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "Error while getting migration count from DB");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return trackingDetails;
    }
    
    public String getServiceType(final int service_id) {
        switch (service_id) {
            case 4: {
                return "IBM_MAAS";
            }
            case 6: {
                return "MERAKI";
            }
            case 3: {
                return "AIRWATCH";
            }
            case 5: {
                return "MOBILE_IRON";
            }
            case 1: {
                return "MDM_CLOUD";
            }
            case 8: {
                return "DC_CLOUD";
            }
            case 2: {
                return "MDM_ONPREMISE";
            }
            case 7: {
                return "DC_ONPREMISE";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getOrgNameForOrgId(final String orgId, final Long customer_id, final Long config_id) {
        String orgName = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MigrationOrganizations"));
            selectQuery.addJoin(this.getCustomerAPIServiceConfigJoin("MigrationOrganizations"));
            final Criteria criteria = new Criteria(new Column("MigrationOrganizations", "OLD_SERVER_ORG_ID"), (Object)orgId, 0);
            selectQuery.setCriteria(this.getCustomerIDAndConfigIdCriteria(customer_id, config_id, "MigrationOrganizations").and(criteria));
            selectQuery.addSelectColumn(Column.getColumn("MigrationOrganizations", "*"));
            orgName = DataAccess.get(selectQuery).getFirstRow("MigrationOrganizations").get("ORG_NAME").toString();
        }
        catch (final Exception e) {
            APIServiceDataHandler.logger.log(Level.SEVERE, "fetching Organization name for org id - failed");
        }
        return orgName;
    }
    
    static {
        APIServiceDataHandler.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
