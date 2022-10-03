package com.me.mdm.server.profiles.config;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;

public class ManagedGuestSessionConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        try {
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            for (int index = 0; index < templateConfigProperties.length(); ++index) {
                if (String.valueOf(templateConfigProperties.getJSONObject(index).get("type")).equals("org.json.JSONObject")) {
                    final JSONObject subConfig = templateConfigProperties.getJSONObject(index);
                    final String subConfigName = String.valueOf(subConfig.get("name"));
                    final int configId = subConfig.getInt("config_id");
                    final String subConfiguration = String.valueOf(subConfig.get("config_name"));
                    final JSONObject resultSubConfig = result.optJSONObject(subConfigName);
                    if (resultSubConfig != null) {
                        resultSubConfig.put("CONFIG_NAME", (Object)subConfiguration);
                        resultSubConfig.put("CONFIG_ID", configId);
                        result.put(subConfigName, (Object)resultSubConfig);
                    }
                }
            }
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON in ManagedGuestSessionConfigHandler", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
    
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray result = super.DOToAPIJSON(dataObject, configName);
            final JSONObject managedGuestSession = result.getJSONObject(0);
            final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
            if (!dataObject.isEmpty()) {
                for (int index = 0; index < templateConfigProperties.length(); ++index) {
                    if (String.valueOf(templateConfigProperties.getJSONObject(index).get("type")).equals("org.json.JSONObject")) {
                        final JSONObject subConfig = templateConfigProperties.getJSONObject(index);
                        final String subConfigName = String.valueOf(subConfig.get("alias"));
                        final String tableName = String.valueOf(subConfig.get("table_name"));
                        if (dataObject.containsTable(tableName) && subConfig.has("properties")) {
                            managedGuestSession.put(subConfigName, (Object)this.convertSubConfigDOToApiJSON(tableName, subConfig, dataObject));
                        }
                    }
                }
            }
            return result;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON in ManagedGuestSessionConfigHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject convertSubConfigDOToApiJSON(final String tableName, final JSONObject subConfig, final DataObject dataObject) throws DataAccessException {
        final JSONObject subConfigApiJson = new JSONObject();
        final Row subConfigRow = dataObject.getRow(tableName);
        final String subConfigName = String.valueOf(subConfig.get("alias"));
        final JSONArray jsonArray = subConfig.getJSONArray("properties");
        JSONObject property = null;
        if (subConfigRow != null) {
            final List subColumns = subConfigRow.getColumns();
            String columnName = null;
            Object columnValue = null;
            for (int i = 0; i < subColumns.size(); ++i) {
                columnName = subColumns.get(i);
                columnValue = subConfigRow.get(columnName);
                property = this.getSubConfigProperties(jsonArray, columnName);
                if (property != null && columnValue != null && String.valueOf(property.get("alias")) != null) {
                    subConfigApiJson.put(String.valueOf(property.get("alias")), columnValue);
                }
            }
            if (subConfigName.equals("chromemgswebcontentfilter")) {
                subConfigApiJson.put("url_details", (Object)this.convertURLDetailsToApiJSON(dataObject, "ManagedGuestSessionWebContentUrlDetails"));
            }
            else if (subConfigName.equals("chromemgsmanagedbookmarks")) {
                subConfigApiJson.put("url_details", (Object)this.convertURLDetailsToApiJSON(dataObject, "ManagedGuestSessionBookmarksUrlDetails"));
            }
        }
        return subConfigApiJson;
    }
    
    private JSONArray convertURLDetailsToApiJSON(final DataObject dataObject, final String urlDetails) throws DataAccessException {
        final JSONArray urlDetailsArray = new JSONArray();
        if (dataObject.containsTable(urlDetails)) {
            final Iterator it = dataObject.getRows(urlDetails);
            while (it.hasNext()) {
                final JSONObject urlDetailsJSON = new JSONObject();
                final Row urlDetailsRow = it.next();
                urlDetailsJSON.put("url", urlDetailsRow.get("URL"));
                urlDetailsJSON.put("bookmark_title", urlDetailsRow.get("BOOKMARK_TITILE"));
                urlDetailsJSON.put("bookmark_path", urlDetailsRow.get("BOOKMARK_PATH"));
                urlDetailsArray.put((Object)urlDetailsJSON);
            }
        }
        return urlDetailsArray;
    }
    
    private List getConfigDataIdsOfOtherPayloads(final List configDataIds) throws Exception {
        List configDataIdsOfOtherPayloads = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConfigData"));
        selectQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ManagedGuestSession", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedGuestSession", "ManagedGuestSessionToInnerPolicies", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "MGS_CONFIG_DATA_ITEM_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ConfigData", "CONFIG_DATA_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedGuestSession", "CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedGuestSessionToInnerPolicies", "MGS_CONFIG_DATA_ITEM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedGuestSessionToInnerPolicies", "CONFIG_DATA_ID"));
        final Criteria configDataIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)configDataIds.toArray(), 8);
        selectQuery.setCriteria(configDataIdCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("ManagedGuestSessionToInnerPolicies");
            configDataIdsOfOtherPayloads = DBUtil.getColumnValuesAsList((Iterator)rows, "CONFIG_DATA_ID");
        }
        return configDataIdsOfOtherPayloads;
    }
    
    private void deleteSubPayloads(final List configDataIdsOfInnerPayloads) throws Exception {
        if (!configDataIdsOfInnerPayloads.isEmpty()) {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ConfigData");
            final Criteria configDataIdsCri = new Criteria(Column.getColumn("ConfigData", "CONFIG_DATA_ID"), (Object)configDataIdsOfInnerPayloads.toArray(), 8);
            deleteQuery.setCriteria(configDataIdsCri);
            MDMUtil.getPersistence().delete(deleteQuery);
        }
    }
    
    @Override
    public void deletePayloads(final Long configDataId) throws Exception {
        final List configDataIds = new ArrayList();
        configDataIds.add(configDataId);
        final List configDataIdsOfOtherPayloads = this.getConfigDataIdsOfOtherPayloads(configDataIds);
        this.deleteSubPayloads(configDataIdsOfOtherPayloads);
        super.deletePayloads(configDataId);
    }
    
    @Override
    public void deletePayloadItems(final Long collectionID, final JSONObject jsonObject, final Long customerId) throws Exception {
        final Long configDataId = jsonObject.optLong("CONFIG_DATA_ID");
        final List configDataIds = new ArrayList();
        configDataIds.add(configDataId);
        final List configDataIdsOfOtherPayloads = this.getConfigDataIdsOfOtherPayloads(configDataIds);
        this.deleteSubPayloads(configDataIdsOfOtherPayloads);
        super.deletePayloadItems(collectionID, jsonObject, customerId);
    }
    
    @Override
    public boolean deleteSubPayloadsIfPresent(final List configDataIds) throws Exception {
        try {
            this.logger.log(Level.INFO, "Going to deleteSubPayloadsIfPresent for configDataIds {0}", configDataIds);
            final List configDataIdsOfOtherPayloads = this.getConfigDataIdsOfOtherPayloads(configDataIds);
            this.deleteSubPayloads(configDataIdsOfOtherPayloads);
            return true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleteSubPayloadsIfPresent", ex);
            return false;
        }
    }
}
