package com.me.mdm.server.config.formbean;

import java.util.List;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class WorkSpaceSecurityFormBean extends MDMDefaultFormBean
{
    public Logger logger;
    
    public WorkSpaceSecurityFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subTableName = (String)dynaJSON.get("MD_PROFILE_TO_APPS");
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
                        final Iterator configDataItemExtnRow = dataObject.getRows("MdConfigDataItemExtn");
                        if (!configDataItemExtnRow.hasNext()) {
                            this.insertConfigDataItemExtn(dynaJSON, dataObject);
                        }
                        payloadRow = dataObject.getRow(tableName);
                        dataObject.deleteRows(subTableName, (Criteria)null);
                    }
                    final List columns = payloadRow.getColumns();
                    for (final Object column : columns) {
                        final String columnName = (String)column;
                        final Object columnValue = dynaJSON.opt(columnName);
                        if (columnName != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                            payloadRow.set(columnName, columnValue);
                        }
                    }
                    final Row payloadSubRow = new Row(subTableName);
                    final JSONArray appData = new JSONArray(dynaJSON.optString("ALLOWED_APPS"));
                    for (int i = 0; i < appData.length(); ++i) {
                        final Row subRow = new Row(subTableName);
                        final List subColumns = payloadSubRow.getColumns();
                        for (final Object subColumn : subColumns) {
                            final String columnName = (String)subColumn;
                            if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                subRow.set(columnName, configDataItemID);
                            }
                            else {
                                subRow.set(columnName, (Object)Long.valueOf(appData.getJSONObject(i).optString("APP_GROUP_ID", "-1")));
                            }
                        }
                        dataObject.addRow(subRow);
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
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while creating " + this.getClass().getName() + " payload", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
}
