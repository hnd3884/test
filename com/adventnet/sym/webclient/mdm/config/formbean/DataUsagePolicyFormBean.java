package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class DataUsagePolicyFormBean extends MDMDefaultFormBean
{
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subTableName = "DATATRACKINGSSID";
                final JSONObject subtableJSON = dynaJSON.getJSONObject("DATATRACKINGSSID");
                Object configDataItemID = dynaJSON.opt("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    final Row configData = dataObject.getRow("ConfigData");
                    final Object configDataID = configData.get("CONFIG_DATA_ID");
                    this.isAdded = false;
                    Row payloadRow;
                    if (configDataItemID == null || configDataItemID.equals(0L)) {
                        final Row dataItemRow = new Row("ConfigDataItem");
                        dataItemRow.set("CONFIG_DATA_ID", configDataID);
                        dataItemRow.set("EXECUTION_ORDER", (Object)k);
                        dataObject.addRow(dataItemRow);
                        configDataItemID = dataItemRow.get("CONFIG_DATA_ITEM_ID");
                        payloadRow = new Row(tableName);
                        payloadRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                        this.isAdded = true;
                    }
                    else {
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
                    final Row payloadSubRow = new Row("DataTrackingSSID");
                    Row subRow = null;
                    subRow = new Row("DataTrackingSSID");
                    final List subColumns = payloadSubRow.getColumns();
                    for (final Object subColumn : subColumns) {
                        final String columnName = (String)subColumn;
                        if (columnName != null && columnName.equals("SSID_TRACKING_ID")) {
                            payloadRow.set("SSID_TRACKING_ID", subRow.get("SSID_TRACKING_ID"));
                        }
                        else {
                            subRow.set(columnName, subtableJSON.get(columnName));
                        }
                    }
                    dataObject.addRow(subRow);
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
            throw new SyMException(1002, exp.getCause());
        }
    }
}
