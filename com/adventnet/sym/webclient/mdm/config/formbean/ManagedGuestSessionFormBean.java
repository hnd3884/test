package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.Iterator;
import com.me.mdm.server.profiles.config.ProfileConfigurationUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class ManagedGuestSessionFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                Object configDataItemID = dynaJSON.optLong("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    final Row configData = dataObject.getRow("ConfigData");
                    final Object configDataID = configData.get("CONFIG_DATA_ID");
                    this.isAdded = false;
                    Row payloadRow;
                    if (configDataItemID == null || configDataItemID.equals(0L)) {
                        this.insertConfigDataItem(dynaJSON, dataObject, k);
                        final Row dataItemRow = dataObject.getRow("ConfigDataItem");
                        configDataItemID = dataItemRow.get("CONFIG_DATA_ITEM_ID");
                        payloadRow = new Row(tableName);
                        payloadRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                        this.isAdded = true;
                    }
                    else {
                        payloadRow = dataObject.getRow(tableName);
                        this.isAdded = false;
                    }
                    final List columns = payloadRow.getColumns();
                    String columnName = null;
                    Object columnValue = null;
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        columnValue = dynaJSON.opt(columnName);
                        if (columnName != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                            payloadRow.set(columnName, columnValue);
                        }
                    }
                    if (dynaJSON.has("CHROME_MGS_RESTRICTIONS")) {
                        final JSONObject restrictionsPayload = dynaJSON.optJSONObject("CHROME_MGS_RESTRICTIONS");
                        this.addOrUpdateSubPayload(restrictionsPayload, k, dataObject, configDataItemID);
                    }
                    if (dynaJSON.has("CHROME_MGS_BROWSER_RESTRICTIONS")) {
                        final JSONObject browserPayload = dynaJSON.optJSONObject("CHROME_MGS_BROWSER_RESTRICTIONS");
                        this.addOrUpdateSubPayload(browserPayload, k, dataObject, configDataItemID);
                    }
                    if (dynaJSON.has("CHROME_MGS_WEB_CONTENT_FILTER")) {
                        final JSONObject webContentPayload = dynaJSON.optJSONObject("CHROME_MGS_WEB_CONTENT_FILTER");
                        this.addOrUpdateSubPayload(webContentPayload, k, dataObject, configDataItemID);
                        final Object webContentConfigDataItemId = webContentPayload.opt("CONFIG_DATA_ITEM_ID");
                        if (!this.isAdded) {
                            this.deleteExistingURLDetails(dataObject, "ManagedGuestSessionWebContentToUrl", "ManagedGuestSessionWebContentUrlDetails");
                        }
                        final JSONArray urlDetails = webContentPayload.optJSONArray("URL_DETAILS");
                        this.addOrUpdateURLDetails(urlDetails, dataObject, webContentConfigDataItemId);
                    }
                    if (dynaJSON.has("CHROME_MGS_MANAGED_BOOKMARKS")) {
                        final JSONObject managedBookmarksPayload = dynaJSON.optJSONObject("CHROME_MGS_MANAGED_BOOKMARKS");
                        this.addOrUpdateSubPayload(managedBookmarksPayload, k, dataObject, configDataItemID);
                        final Object managedBookmarksConfigDataItemId = managedBookmarksPayload.opt("CONFIG_DATA_ITEM_ID");
                        if (!this.isAdded) {
                            this.deleteExistingURLDetails(dataObject, "ManagedGuestSessionBookmarksToUrl", "ManagedGuestSessionBookmarksUrlDetails");
                        }
                        final JSONArray urlDetails = managedBookmarksPayload.optJSONArray("URL_DETAILS");
                        this.addOrUpdateURLDetails(urlDetails, dataObject, managedBookmarksConfigDataItemId);
                    }
                    if (this.isAdded) {
                        dataObject.addRow(payloadRow);
                    }
                    else {
                        dataObject.updateRow(payloadRow);
                    }
                }
            }
        }
        catch (final PayloadException e) {
            throw e;
        }
        catch (final Exception exp) {
            ManagedGuestSessionFormBean.logger.log(Level.SEVERE, "ManagedGuestSessionFormBean: Error while dynaFormToDO() ", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    private DataObject addConfigData(final JSONObject configDataJSON, final DataObject configDataDO) throws DataAccessException {
        final String configName = configDataJSON.optString("CONFIG_NAME");
        final Integer configID = configDataJSON.optInt("CONFIG_ID");
        final Integer configType = new Integer(3);
        final Row configDataRow = new Row("ConfigData");
        configDataRow.set("CONFIG_ID", (Object)configID);
        configDataRow.set("LABEL", (Object)configName);
        configDataRow.set("CREATION_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
        configDataRow.set("MODIFIED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
        configDataRow.set("DESCRIPTION", (Object)configName);
        configDataRow.set("CONFIG_TYPE", (Object)configType);
        configDataDO.addRow(configDataRow);
        final Object configDataId = configDataRow.get("CONFIG_DATA_ID");
        configDataJSON.put("CONFIG_DATA_ID", configDataId);
        return configDataDO;
    }
    
    private DataObject addConfigDataItem(final JSONObject jsonObject, final DataObject configItemDataDO, final int executionOrder) throws DataAccessException {
        final Object configDataID = jsonObject.opt("CONFIG_DATA_ID");
        final Row configDataItemRow = new Row("ConfigDataItem");
        configDataItemRow.set("CONFIG_DATA_ID", configDataID);
        configDataItemRow.set("EXECUTION_ORDER", (Object)executionOrder);
        configItemDataDO.addRow(configDataItemRow);
        final Object configDataItemId = configDataItemRow.get("CONFIG_DATA_ITEM_ID");
        jsonObject.put("CONFIG_DATA_ITEM_ID", configDataItemId);
        return configItemDataDO;
    }
    
    private void addOrUpdateSubPayload(final JSONObject subPayloadJSON, final int executionOrder, DataObject dataObject, final Object mgsConfigDataItemId) throws DataAccessException {
        final String subPayloadTableName = subPayloadJSON.optString("TABLE_NAME");
        Boolean isSubRowAdded = false;
        Row subRow;
        if (this.isAdded || !dataObject.containsTable(subPayloadTableName)) {
            dataObject = this.addConfigData(subPayloadJSON, dataObject);
            final Object configDataId = subPayloadJSON.opt("CONFIG_DATA_ID");
            dataObject = this.addConfigDataItem(subPayloadJSON, dataObject, executionOrder);
            final Object configDataItemId = subPayloadJSON.opt("CONFIG_DATA_ITEM_ID");
            subRow = new Row(subPayloadTableName);
            subRow.set("CONFIG_DATA_ITEM_ID", configDataItemId);
            final Row relationTable = new Row("ManagedGuestSessionToInnerPolicies");
            relationTable.set("MGS_CONFIG_DATA_ITEM_ID", mgsConfigDataItemId);
            relationTable.set("CONFIG_DATA_ID", configDataId);
            dataObject.addRow(relationTable);
            isSubRowAdded = true;
        }
        else {
            subRow = dataObject.getFirstRow(subPayloadTableName);
            final Object configDataItemId = subRow.get("CONFIG_DATA_ITEM_ID");
            subPayloadJSON.put("CONFIG_DATA_ITEM_ID", configDataItemId);
            isSubRowAdded = false;
        }
        final List subColumns = subRow.getColumns();
        String columnName = null;
        Object columnValue = null;
        for (int i = 0; i < subColumns.size(); ++i) {
            columnName = subColumns.get(i);
            columnValue = subPayloadJSON.opt(columnName);
            if (columnName != null && !columnName.equals("CONFIG_DATA_ITEM_ID") && columnValue != null) {
                subRow.set(columnName, columnValue);
            }
        }
        if (isSubRowAdded) {
            dataObject.addRow(subRow);
        }
        else {
            dataObject.updateRow(subRow);
        }
    }
    
    private void addOrUpdateURLDetails(final JSONArray jsonArray, final DataObject dataObject, final Object configdataItemId) throws JSONException, DataAccessException {
        ManagedGuestSessionFormBean.logger.log(Level.INFO, "URL Details {0}", jsonArray);
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject json = (JSONObject)jsonArray.get(i);
            final String url = json.optString("URL");
            if (url != "") {
                final Row urlRow = new Row("URLDetails");
                urlRow.set("URL", (Object)url);
                urlRow.set("BOOKMARK_TITILE", (Object)json.optString("BOOKMARK_TITILE", "--"));
                urlRow.set("BOOKMARK_PATH", (Object)json.optString("BOOKMARK_PATH", "--"));
                final Row urlToConfigRel = new Row("CfgDataItemToUrl");
                urlToConfigRel.set("URL_DETAILS_ID", urlRow.get("URL_DETAILS_ID"));
                urlToConfigRel.set("CONFIG_DATA_ITEM_ID", configdataItemId);
                dataObject.addRow(urlRow);
                dataObject.addRow(urlToConfigRel);
            }
        }
    }
    
    private void deleteExistingURLDetails(final DataObject dataObject, final String cfgDataToItemUrl, final String urlDetails) {
        try {
            dataObject.deleteRows(urlDetails, (Criteria)null);
            dataObject.deleteRows(cfgDataToItemUrl, (Criteria)null);
        }
        catch (final Exception e) {
            ManagedGuestSessionFormBean.logger.log(Level.WARNING, "Exception occured in deleteExistingURLDetails ", e);
        }
    }
    
    @Override
    public void cloneConfigDO(final Integer configID, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        super.cloneConfigDO(configID, configDOFromDB, cloneConfigDO);
        final Row clonedConfigDataRow = cloneConfigDO.getRow("ConfigData", new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)new Integer(configID), 0));
        final Object configDataId = clonedConfigDataRow.get("CONFIG_DATA_ID");
        final Row clonedConfigDataItemRow = cloneConfigDO.getRow("ConfigDataItem", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), configDataId, 0));
        final Object configDataItemId = clonedConfigDataItemRow.get("CONFIG_DATA_ITEM_ID");
        final int executionOrder = (int)clonedConfigDataItemRow.get("EXECUTION_ORDER");
        ManagedGuestSessionFormBean.logger.log(Level.INFO, " configDOFromDB {0}", configDOFromDB);
        final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties("chromemanagedguestsession");
        for (int index = 0; index < templateConfigProperties.length(); ++index) {
            if (String.valueOf(templateConfigProperties.getJSONObject(index).get("type")).equals("org.json.JSONObject")) {
                final JSONObject subConfig = templateConfigProperties.getJSONObject(index);
                final String subConfigTableName = String.valueOf(subConfig.get("table_name"));
                if (configDOFromDB.containsTable(subConfigTableName)) {
                    final Row oldRow = configDOFromDB.getRow(subConfigTableName);
                    if (oldRow != null) {
                        final JSONObject subPayloadJSON = new JSONObject();
                        final String subConfigName = String.valueOf(subConfig.get("config_name"));
                        final int subConfigId = subConfig.getInt("config_id");
                        subPayloadJSON.put("CONFIG_NAME", (Object)subConfigName);
                        subPayloadJSON.put("CONFIG_ID", subConfigId);
                        subPayloadJSON.put("EXECUTION_ORDER", executionOrder);
                        subPayloadJSON.put("subConfigTableName", (Object)subConfigTableName);
                        subPayloadJSON.put("MGS_CONFIG_DATA_ITEM_ID", configDataItemId);
                        this.cloneSubPayloadConfigDO(subPayloadJSON, configDOFromDB, cloneConfigDO);
                    }
                }
            }
        }
    }
    
    private void cloneSubPayloadConfigDO(final JSONObject subPayloadJSON, final DataObject configDOFromDB, DataObject cloneConfigDO) throws DataAccessException {
        final int executionOrder = subPayloadJSON.optInt("EXECUTION_ORDER");
        final Object mgsConfigDataItemId = subPayloadJSON.opt("MGS_CONFIG_DATA_ITEM_ID");
        final String subConfigTableName = subPayloadJSON.optString("subConfigTableName");
        final Integer configID = subPayloadJSON.optInt("CONFIG_ID");
        cloneConfigDO = this.addConfigData(subPayloadJSON, cloneConfigDO);
        final Object subConfigDataId = subPayloadJSON.opt("CONFIG_DATA_ID");
        subPayloadJSON.put("CONFIG_DATA_ID", subConfigDataId);
        cloneConfigDO = this.addConfigDataItem(subPayloadJSON, cloneConfigDO, executionOrder);
        final Object subConfigDataItemId = subPayloadJSON.opt("CONFIG_DATA_ITEM_ID");
        final Row oldRow = configDOFromDB.getRow(subConfigTableName);
        final Row clonedRow = new Row(subConfigTableName);
        clonedRow.set("CONFIG_DATA_ITEM_ID", subConfigDataItemId);
        this.cloneRow(oldRow, clonedRow, "CONFIG_DATA_ITEM_ID");
        final Row relationTable = new Row("ManagedGuestSessionToInnerPolicies");
        relationTable.set("MGS_CONFIG_DATA_ITEM_ID", mgsConfigDataItemId);
        relationTable.set("CONFIG_DATA_ID", subConfigDataId);
        cloneConfigDO.addRow(clonedRow);
        cloneConfigDO.addRow(relationTable);
        if (configID == 707) {
            subPayloadJSON.put("urlDetailsTable", (Object)"ManagedGuestSessionWebContentUrlDetails");
            this.cloneURLDetails(subPayloadJSON, configDOFromDB, cloneConfigDO);
        }
        else if (configID == 709) {
            subPayloadJSON.put("urlDetailsTable", (Object)"ManagedGuestSessionBookmarksUrlDetails");
            this.cloneURLDetails(subPayloadJSON, configDOFromDB, cloneConfigDO);
        }
    }
    
    private void cloneURLDetails(final JSONObject subPayloadJSON, final DataObject configDOFromDB, final DataObject cloneConfigDO) throws DataAccessException {
        final String urlDetailsTable = subPayloadJSON.optString("urlDetailsTable");
        final Object subConfigDataItemId = subPayloadJSON.opt("CONFIG_DATA_ITEM_ID");
        final Iterator it = configDOFromDB.getRows(urlDetailsTable);
        while (it.hasNext()) {
            final Row urlDetailsRow = it.next();
            final Row clonedRow = new Row("URLDetails");
            final Object urlDetailsId = this.cloneRow(urlDetailsRow, clonedRow, "URL_DETAILS_ID");
            final Row relationRow = new Row("CfgDataItemToUrl");
            relationRow.set("URL_DETAILS_ID", urlDetailsId);
            relationRow.set("CONFIG_DATA_ITEM_ID", subConfigDataItemId);
            cloneConfigDO.addRow(clonedRow);
            cloneConfigDO.addRow(relationRow);
        }
    }
}
