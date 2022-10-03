package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class ChromeEthernetPolicyFormBean extends MDMDefaultFormBean
{
    public Logger logger;
    
    public ChromeEthernetPolicyFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = "EthernetConfig";
                final String subConfig = "ETHERNET_ENTERPRISE";
                final Long configDataItemID = (Long)dynaJSON.opt("CONFIG_DATA_ITEM_ID");
                final Row configData = dataObject.getRow("ConfigData");
                final Object configDataID = configData.get("CONFIG_DATA_ID");
                JSONObject subConfigForm = null;
                String subTableName = null;
                if (subConfig != null) {
                    subConfigForm = dynaJSON.getJSONObject(subConfig);
                    subTableName = (String)subConfigForm.get("TABLE_NAME");
                }
                boolean isAdded = false;
                boolean isSubRowAdded = false;
                boolean isProxyRowAdded = false;
                Row payloadRow = null;
                Row payloadSubRow = null;
                Row proxyRow = null;
                if (configDataItemID == null || configDataItemID == 0L) {
                    Row dataItemRow = null;
                    dataItemRow = new Row("ConfigDataItem");
                    dataItemRow.set("CONFIG_DATA_ID", configDataID);
                    dataItemRow.set("EXECUTION_ORDER", (Object)k);
                    dataObject.addRow(dataItemRow);
                    dynaJSON.put("CONFIG_DATA_ITEM_ID", dataItemRow.get("CONFIG_DATA_ITEM_ID"));
                    super.insertConfigDataItemExtn(dynaJSON, dataObject);
                    payloadRow = new Row(tableName);
                    payloadRow.set("CONFIG_DATA_ITEM_ID", dataItemRow.get("CONFIG_DATA_ITEM_ID"));
                    if (subTableName != null) {
                        payloadSubRow = new Row(subTableName);
                        payloadSubRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                        isSubRowAdded = true;
                    }
                    proxyRow = new Row("PayloadProxyConfig");
                    proxyRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                    isProxyRowAdded = true;
                    isAdded = true;
                }
                else {
                    payloadRow = dataObject.getFirstRow(tableName);
                    if (dataObject.containsTable(subTableName)) {
                        if (subTableName != null) {
                            payloadSubRow = dataObject.getFirstRow(subTableName);
                            isSubRowAdded = false;
                        }
                    }
                    else if (subTableName != null) {
                        payloadSubRow = new Row(subTableName);
                        isSubRowAdded = true;
                    }
                    proxyRow = dataObject.getFirstRow("PayloadProxyConfig");
                    if (proxyRow == null) {
                        proxyRow = new Row("PayloadProxyConfig");
                        proxyRow.set("CONFIG_DATA_ITEM_ID", payloadRow.get("CONFIG_DATA_ITEM_ID"));
                        isProxyRowAdded = true;
                    }
                }
                String columnName = null;
                Object columnValue = null;
                final List columns = payloadRow.getColumns();
                final String notToBeInsertedColumn = "CONFIG_DATA_ITEM_ID";
                for (int i = 0; i < columns.size(); ++i) {
                    columnName = columns.get(i);
                    columnValue = dynaJSON.opt(columnName);
                    if (columnName != null && columnValue != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                        payloadRow.set(columnName, columnValue);
                    }
                }
                if (payloadSubRow != null && subConfigForm != null) {
                    final List subColumns = payloadSubRow.getColumns();
                    for (int j = 0; j < subColumns.size(); ++j) {
                        columnName = subColumns.get(j);
                        if (columnName != null && !columnName.equals(notToBeInsertedColumn)) {
                            columnValue = subConfigForm.opt(columnName);
                            if (columnValue != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                payloadSubRow.set(columnName, columnValue);
                            }
                        }
                    }
                }
                final JSONObject proxyConfig = dynaJSON.getJSONObject("PROXY_SETTINGS");
                if (proxyConfig != null) {
                    final List proxyColumns = proxyRow.getColumns();
                    for (int l = 0; l < proxyColumns.size(); ++l) {
                        columnName = proxyColumns.get(l);
                        if (columnName != null && !columnName.equals(notToBeInsertedColumn)) {
                            columnValue = proxyConfig.opt(columnName);
                            if (columnValue != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                proxyRow.set(columnName, columnValue);
                            }
                        }
                    }
                }
                if (isAdded) {
                    dataObject.addRow(payloadRow);
                }
                else {
                    dataObject.updateRow(payloadRow);
                }
                if (isSubRowAdded) {
                    dataObject.addRow(payloadSubRow);
                }
                else {
                    dataObject.updateRow(payloadSubRow);
                }
                if (isProxyRowAdded) {
                    dataObject.addRow(proxyRow);
                }
                else {
                    dataObject.updateRow(proxyRow);
                }
            }
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
}
