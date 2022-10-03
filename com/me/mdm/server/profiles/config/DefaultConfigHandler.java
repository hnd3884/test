package com.me.mdm.server.profiles.config;

import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.certificate.CertificateMapping;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.mdm.files.FileFacade;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DefaultConfigHandler implements ConfigHandler
{
    protected Logger logger;
    private static final long IMAGESIZE = 5242880L;
    public static final String NULLABLE_COLUMN_SUFFIX = "_NULLABLE";
    
    public DefaultConfigHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            final JSONObject result = new JSONObject(apiJSON.toString());
            final ProfileConfigurationUtil util = ProfileConfigurationUtil.getInstance();
            result.put("TABLE_NAME", (Object)util.getTableName(configName));
            result.put("BEAN_NAME", (Object)util.getBeanName(configName));
            result.put("CONFIG_ID", (Object)util.getConfigID(configName));
            result.put("CONFIG_NAME", (Object)util.getConfigurationName(configName));
            result.put("CONFIG_TYPE", (Object)util.getConfigType(configName));
            result.put("CONFIG_DATA_IDENTIFIER", (Object)util.getConfigDataIdentifier(configName));
            result.put("ALLOWED_COUNT", (Object)util.getAllowedCount(configName));
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            this.convertAPIFieldsToServerField(templateConfigProperties, result, apiJSON);
            this.logger.log(Level.INFO, "Completed converting API JSON to Server JSON...");
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
    
    private void convertAPIFieldsToServerField(final JSONArray templateConfigProperties, final JSONObject result, final JSONObject apiJSON) throws JSONException {
        for (int i = 0; i < templateConfigProperties.length(); ++i) {
            final JSONObject property = templateConfigProperties.getJSONObject(i);
            if (String.valueOf(property.get("type")).equals("org.json.JSONObject")) {
                final JSONObject temp = new JSONObject();
                if (property.has("bean_name")) {
                    temp.put("BEAN_NAME", (Object)String.valueOf(property.get("bean_name")));
                }
                if (property.has("table_name")) {
                    temp.put("TABLE_NAME", (Object)String.valueOf(property.get("table_name")));
                }
                if (apiJSON.has(String.valueOf(property.get("alias")))) {
                    final JSONObject tempApiJSON = apiJSON.getJSONObject(String.valueOf(property.get("alias")));
                    tempApiJSON.put("CUSTOMER_ID", apiJSON.optLong("CUSTOMER_ID"));
                    tempApiJSON.put("LAST_MODIFIED_BY", apiJSON.optLong("LAST_MODIFIED_BY"));
                    this.convertAPIFieldsToServerField(property.getJSONArray("properties"), temp, tempApiJSON);
                    result.put(String.valueOf(property.get("name")), (Object)temp);
                }
                else if (property.optBoolean("required", false)) {
                    throw new APIHTTPException("COM0005", new Object[] { String.valueOf(property.get("alias")) });
                }
            }
            else if (String.valueOf(property.get("type")).equals("org.json.JSONArray")) {
                if (property.has("alias") && apiJSON.has(String.valueOf(property.get("alias")))) {
                    final JSONArray input = apiJSON.getJSONArray(String.valueOf(property.get("alias")));
                    if (String.valueOf(property.get("item_type")).equals("org.json.JSONObject")) {
                        final Long customerId = apiJSON.optLong("CUSTOMER_ID");
                        final Long userId = apiJSON.optLong("LAST_MODIFIED_BY");
                        final JSONArray output = new JSONArray();
                        for (int j = 0; j < input.length(); ++j) {
                            final JSONObject obj = new JSONObject();
                            final JSONObject curInputObj = input.getJSONObject(j);
                            curInputObj.put("CUSTOMER_ID", (Object)customerId);
                            curInputObj.put("LAST_MODIFIED_BY", (Object)userId);
                            this.convertAPIFieldsToServerField(property.getJSONArray("properties"), obj, curInputObj);
                            output.put((Object)obj);
                        }
                        result.put(String.valueOf(property.get("name")), (Object)output);
                    }
                    else {
                        result.put(String.valueOf(property.get("name")), (Object)input);
                    }
                }
                else if (property.optBoolean("required", false)) {
                    throw new APIHTTPException("COM0005", new Object[] { String.valueOf(property.get("alias")) });
                }
            }
            else if ((property.has("alias") && apiJSON.has(String.valueOf(property.get("alias")))) || property.has("default") || property.has("value")) {
                this.convertAPIFieldToServerField(property, apiJSON, result);
            }
            else if (property.optBoolean("required", false)) {
                throw new APIHTTPException("COM0005", new Object[] { String.valueOf(property.get("alias")) });
            }
        }
    }
    
    private void convertAPIFieldToServerField(final JSONObject property, final JSONObject apiJSON, JSONObject result) throws JSONException {
        Object value = null;
        if (property.has("alias")) {
            if (apiJSON.has(String.valueOf(property.get("alias")))) {
                final String value2 = String.valueOf(property.get("type"));
                switch (value2) {
                    case "java.lang.Integer": {
                        value = apiJSON.getInt(String.valueOf(property.get("alias")));
                        break;
                    }
                    case "java.lang.Long": {
                        value = Long.valueOf(String.valueOf(apiJSON.get(String.valueOf(property.get("alias")))));
                        break;
                    }
                    case "java.lang.String": {
                        value = String.valueOf(apiJSON.get(String.valueOf(property.get("alias"))));
                        break;
                    }
                    case "java.lang.Boolean": {
                        value = apiJSON.getBoolean(String.valueOf(property.get("alias")));
                        break;
                    }
                    case "java.lang.Double": {
                        value = apiJSON.getDouble(String.valueOf(property.get("alias")));
                        break;
                    }
                    case "File": {
                        final String file_id = String.valueOf(apiJSON.get(String.valueOf(property.get("alias"))));
                        value = null;
                        if (file_id == null || file_id.length() == 0) {
                            throw new APIHTTPException("COM0005", new Object[] { String.valueOf(property.get("alias")) });
                        }
                        if (property.optBoolean("local_file_required", false)) {
                            value = new FileFacade().getLocalPathForFileID(Long.valueOf(file_id));
                            break;
                        }
                        value = FileUploadManager.getFilePath(Long.valueOf(file_id));
                        break;
                    }
                }
            }
            else if (property.has("default")) {
                final String value3 = String.valueOf(property.get("type"));
                switch (value3) {
                    case "java.lang.Integer": {
                        value = property.getInt("default");
                        break;
                    }
                    case "java.lang.Long": {
                        value = Long.valueOf(String.valueOf(property.get("default")));
                        break;
                    }
                    case "java.lang.String": {
                        value = String.valueOf(property.get("default"));
                        break;
                    }
                    case "java.lang.Boolean": {
                        value = property.getBoolean("default");
                        break;
                    }
                    case "java.lang.Double": {
                        value = property.getDouble("default");
                        break;
                    }
                    case "File": {
                        value = String.valueOf(property.get("default"));
                        break;
                    }
                }
            }
            else if (property.optBoolean("required", false)) {
                throw new APIHTTPException("COM0005", new Object[] { String.valueOf(property.get("alias")) });
            }
        }
        else if (property.has("value")) {
            final String value4 = String.valueOf(property.get("type"));
            switch (value4) {
                case "java.lang.Integer": {
                    value = property.getInt("value");
                    break;
                }
                case "java.lang.Long": {
                    value = property.getLong("value");
                    break;
                }
                case "java.lang.String": {
                    value = String.valueOf(property.get("value"));
                    break;
                }
                case "java.lang.Boolean": {
                    value = property.getBoolean("value");
                    break;
                }
                case "java.lang.Double": {
                    value = property.getDouble("value");
                    break;
                }
            }
        }
        value = this.transformApiValueToTableValue(property, result, value);
        if (value != null) {
            result.put(String.valueOf(property.get("name")), value);
            if (property.has("secret_field")) {
                result = PayloadSecretFieldsHandler.getInstance().handlePayloadSecretFields(property, apiJSON, result, value);
            }
        }
        else if (property.has("field_nullable") && property.optBoolean("field_nullable", false)) {
            result = this.setPayloadNullableColumn(result, String.valueOf(property.get("name")));
        }
    }
    
    protected Object transformApiValueToTableValue(final JSONObject property, final JSONObject result, Object apiValue) {
        final String tableName = result.optString("TABLE_NAME", (String)null);
        final String columnName = property.optString("name", (String)null);
        if (!MDMStringUtils.isEmpty(tableName) && !MDMStringUtils.isEmpty(columnName) && ProfileCertificateUtil.getInstance().certificatesMapptedTableList.contains(new CertificateMapping(tableName, columnName)) && (long)apiValue < 0L) {
            apiValue = null;
        }
        return apiValue;
    }
    
    protected Object transformTableValueToApiValue(final DataObject dataObject, final String columnName, Object columnValue, final String tableName, final String configName) throws APIHTTPException {
        if (!MDMStringUtils.isEmpty(tableName) && !MDMStringUtils.isEmpty(columnName) && ProfileCertificateUtil.getInstance().certificatesMapptedTableList.contains(new CertificateMapping(tableName, columnName)) && columnValue == null) {
            columnValue = -1L;
        }
        return columnValue;
    }
    
    public JSONObject setPayloadNullableColumn(final JSONObject resultJSON, final String columnName) {
        final String nullableColumnName = columnName + "_NULLABLE";
        resultJSON.put(nullableColumnName, true);
        return resultJSON;
    }
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            String tableName = null;
            tableName = ProfileConfigurationUtil.getInstance().getTableName(configName);
            final int configID = ProfileConfigurationUtil.getInstance().getConfigID(configName);
            if (dataObject.containsTable("ConfigData")) {
                final Row row = dataObject.getFirstRow("ConfigData");
                final int configIDT = Integer.valueOf(String.valueOf(row.get("CONFIG_ID")));
                if (configID != configIDT) {
                    return null;
                }
            }
            return this.DOToAPIJSON(dataObject, configName, tableName);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray result = new JSONArray();
            if (dataObject.containsTable(tableName)) {
                final Iterator<Row> rows = dataObject.getRows(tableName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final JSONObject config = new JSONObject();
                    this.addConfigForRow(row, dataObject, configName, config, tableName);
                    this.checkAndAddInnerJSON(config, dataObject, configName);
                    result.put((Object)config);
                }
            }
            return result;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected void addConfigForRow(final Row row, final DataObject dataObject, final String configName, JSONObject config, final String tableName) {
        String columnName = null;
        Object columnValue = null;
        JSONObject property = null;
        final List columns = row.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            columnName = columns.get(i);
            property = this.getDetailsForColName(configName, columnName);
            columnValue = row.get(columnName);
            columnValue = this.transformTableValueToApiValue(dataObject, columnName, columnValue, tableName, configName);
            if (property != null && property.has("alias") && columnValue != null) {
                if (property.has("type") && String.valueOf(property.get("type")).equals("File")) {
                    config.put(String.valueOf(property.get("alias")), this.constructFileUrl(columnValue));
                }
                else if (property.has("return_secret_field_value")) {
                    config = PayloadSecretFieldsHandler.getInstance().replaceSecretFieldIdInDoToApi(property, columnValue, config);
                }
                else {
                    config.put(String.valueOf(property.get("alias")), columnValue);
                }
            }
        }
    }
    
    protected JSONObject getDetailsForColName(final String configName, final String columnName) {
        try {
            final JSONArray properties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            for (int i = 0; i < properties.length(); ++i) {
                final JSONObject property = properties.getJSONObject(i);
                if (String.valueOf(property.get("name")).equals(columnName)) {
                    return property;
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in getAliasForName", (Throwable)e);
        }
        return null;
    }
    
    @Override
    public JSONObject DOToAPIJSON(final DataObject dataObject) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            final Iterator<String> configNameIterator = ProfileConfigurationUtil.getInstance().getPayloadConfiguration().keys();
            while (configNameIterator.hasNext()) {
                final String configName = configNameIterator.next();
                final JSONArray configJSON = this.DOToAPIJSON(dataObject, configName);
                if (configJSON == null) {
                    continue;
                }
                result.put(configName, (Object)configJSON);
            }
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in DOToAPIJSON");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        final Long collectionId = serverJSON.optLong("COLLECTION_ID");
        final Long configId = serverJSON.optLong("CONFIG_ID");
        final Integer allowedCount = serverJSON.optInt("ALLOWED_COUNT");
        final String payloadName = serverJSON.optString("payload_name", "Payload");
        if (!serverJSON.has("CONFIG_DATA_ITEM_ID") && this.isPayloadItemCountGreater(collectionId, configId, allowedCount)) {
            throw new APIHTTPException("PAY0006", new Object[] { allowedCount, payloadName });
        }
    }
    
    protected Object constructFileUrl(Object columnValue) {
        final HashMap hm = new HashMap();
        hm.put("path", columnValue);
        hm.put("IS_SERVER", true);
        hm.put("IS_AUTHTOKEN", false);
        hm.put("isApi", true);
        columnValue = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        return columnValue;
    }
    
    protected void checkAndAddInnerJSON(final JSONObject configJSON, final DataObject dataObject, final String configName) throws Exception {
        if (configJSON.has("payload_id")) {
            final long configDataItemId = configJSON.getLong("payload_id");
            String subConfigName = "";
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            for (int index = 0; index < templateConfigProperties.length(); ++index) {
                if (String.valueOf(templateConfigProperties.getJSONObject(index).get("type")).equals("org.json.JSONObject")) {
                    final JSONObject subConfig = templateConfigProperties.getJSONObject(index);
                    final String tableName = String.valueOf(subConfig.get("table_name"));
                    if (dataObject.containsTable(tableName)) {
                        subConfigName = String.valueOf(subConfig.get("alias"));
                        if (subConfig.has("properties")) {
                            final JSONArray jsonArray = subConfig.getJSONArray("properties");
                            final Row columnRow = new Row(tableName);
                            if (columnRow.getColumns().contains("CONFIG_DATA_ITEM_ID")) {
                                final Criteria criteria = new Criteria(Column.getColumn(tableName, "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0);
                                final Row row = dataObject.getRow(tableName, criteria);
                                JSONObject config = new JSONObject();
                                String columnName = null;
                                Object columnValue = null;
                                JSONObject property = null;
                                if (row != null) {
                                    final List columns = row.getColumns();
                                    for (int i = 0; i < columns.size(); ++i) {
                                        columnName = columns.get(i);
                                        property = this.getSubConfigProperties(jsonArray, columnName);
                                        columnValue = row.get(columnName);
                                        columnValue = this.transformTableValueToApiValue(dataObject, columnName, columnValue, tableName, configName);
                                        if (property != null && String.valueOf(property.get("alias")) != null && columnValue != null) {
                                            if (property.has("return_secret_field_value")) {
                                                config = PayloadSecretFieldsHandler.getInstance().replaceSecretFieldIdInDoToApi(property, columnValue, config);
                                            }
                                            else {
                                                config.put(String.valueOf(property.get("alias")), columnValue);
                                            }
                                        }
                                    }
                                    configJSON.put("sub_config", (Object)String.valueOf(subConfig.get("name")));
                                    configJSON.put(subConfigName, (Object)config);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected JSONObject getSubConfigProperties(final JSONArray properties, final String columnName) {
        try {
            for (int i = 0; i < properties.length(); ++i) {
                final JSONObject property = properties.getJSONObject(i);
                if (String.valueOf(property.get("name")).equals(columnName)) {
                    return property;
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in getAliasForName", (Throwable)e);
        }
        return null;
    }
    
    protected boolean isImageSizeGreater(final String source) {
        return this.isFileSizeGreater(source, 5242880L);
    }
    
    protected boolean isFileSizeGreater(final String source, final Long size) {
        final long uploadedImageSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(source);
        return size < uploadedImageSize;
    }
    
    protected boolean isPayloadItemCountGreater(final Long collectionId, final Long configId, final Integer payloadRestrictionCount) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigDataItem"));
        selectQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        final Column configDataColumn = new Column("ConfigData", "CONFIG_ID");
        final GroupByClause groupByClause = new GroupByClause((List)Arrays.asList(configDataColumn));
        selectQuery.setGroupByClause(groupByClause);
        selectQuery.addSelectColumn(configDataColumn.count());
        final Criteria collectionCriteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria configDataItemCriteria = new Criteria(configDataColumn, (Object)configId, 0);
        selectQuery.setCriteria(collectionCriteria.and(configDataItemCriteria));
        DataSet dataSet = null;
        Connection connection = null;
        int count = 0;
        try {
            final RelationalAPI relationAPI = RelationalAPI.getInstance();
            this.logger.log(Level.INFO, "executing query : {0}", relationAPI.getSelectSQL((Query)selectQuery));
            connection = relationAPI.getConnection();
            dataSet = relationAPI.executeQuery((Query)selectQuery, connection);
            final ArrayList columns = (ArrayList)selectQuery.getSelectColumns();
            if (dataSet.next()) {
                count = (int)dataSet.getValue(1);
            }
            dataSet.close();
        }
        catch (final SQLException ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
        catch (final QueryConstructionException ex2) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex2);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(connection, dataSet);
        }
        return payloadRestrictionCount <= count;
    }
    
    public int getCountWifiSSID(final JSONObject requestJSON) throws Exception {
        final Long collectionId = requestJSON.optLong("COLLECTION_ID");
        final Long configDataItemId = requestJSON.optLong("CONFIG_DATA_ITEM_ID");
        final String ssid = requestJSON.optString("SERVICE_SET_IDENTIFIER");
        this.logger.log(Level.INFO, "Same wifi exists with config_data_item_id : {0} and ssid : {1}", new Object[] { configDataItemId, ssid });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WifiPolicy"));
        selectQuery.addJoin(new Join("WifiPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("WifiPolicy", "SERVICE_SET_IDENTIFIER"), (Object)ssid, 0));
        if (configDataItemId > 0L) {
            criteria = criteria.and(new Criteria(Column.getColumn("WifiPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 1));
        }
        selectQuery.setCriteria(criteria);
        final int recordCount = DBUtil.getRecordCount(selectQuery, "WifiPolicy", "SERVICE_SET_IDENTIFIER");
        return recordCount;
    }
    
    protected void addAliasForRowInConfigJSON(final Row rowDetail, final JSONArray templateJSON, final JSONObject configJSON) {
        final List columns = rowDetail.getColumns();
        for (int i = 0; i < columns.size(); ++i) {
            final String columnName = columns.get(i);
            final JSONObject property = this.getSubConfigProperties(templateJSON, columnName);
            final Object columnValue = rowDetail.get(columnName);
            if (property != null && String.valueOf(property.get("alias")) != null && columnValue != null) {
                configJSON.put(property.getString("alias"), columnValue);
            }
        }
    }
    
    @Override
    public boolean deletePayloadFile(final DataObject dataObject, final Long configDataId) {
        return false;
    }
    
    @Override
    public void deletePayloads(final Long configDataId) throws Exception {
        MDMConfigUtil.deleteConfiguration(configDataId);
    }
    
    @Override
    public void deletePayloadItems(final Long collectionID, final JSONObject jsonObject, final Long customerId) throws Exception {
        ProfileConfigHandler.deleteConfigurationDataItem(collectionID, jsonObject, customerId);
    }
    
    @Override
    public boolean deleteSubPayloadsIfPresent(final List configDataIds) throws Exception {
        return false;
    }
}
