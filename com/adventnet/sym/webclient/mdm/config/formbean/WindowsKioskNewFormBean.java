package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Properties;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class WindowsKioskNewFormBean extends MDMDefaultFormBean
{
    public Logger logger;
    
    public WindowsKioskNewFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subTableName = (String)dynaJSON.get("WINDOWS_KIOSK_POLICY_APPS");
                final String subTableName2 = (String)dynaJSON.get("WINDOWS_APP_DETAILS");
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
                        dataObject.deleteRows("WindowsKioskPolicySystemApps", (Criteria)null);
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
                    final JSONArray appDataArr = dynaJSON.getJSONArray("ALLOWED_APPS");
                    final JSONArray nonSystemApps = new JSONArray();
                    final Properties properties = new Properties();
                    for (int i = 0; i < appDataArr.length(); ++i) {
                        final JSONObject appDataObj = appDataArr.getJSONObject(i);
                        final JSONObject nonSystemAppsData = new JSONObject();
                        final Boolean isSystemApp = Boolean.valueOf(appDataObj.optString("IsWindowsSystemApp", "false"));
                        Row subRow = null;
                        if (!isSystemApp) {
                            nonSystemApps.put((Object)appDataObj.optString("APP_ID", "-1"));
                            nonSystemAppsData.put(appDataObj.optString("APP_ID", "-1"), (Object)appDataObj);
                            subRow = new Row(subTableName);
                            final List subColumns = payloadSubRow.getColumns();
                            for (final Object subColumn : subColumns) {
                                final String columnName = (String)subColumn;
                                if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    subRow.set(columnName, configDataItemID);
                                }
                                else {
                                    subRow.set(columnName, (Object)Long.valueOf(appDataObj.optString("APP_ID", "-1")));
                                }
                            }
                            properties.setProperty("appJSON", nonSystemApps.toString());
                            ((Hashtable<String, JSONObject>)properties).put("allowedApps", nonSystemAppsData);
                            this.getWindowsAppDetailsDO(properties);
                            final JSONObject queueData = new JSONObject();
                            queueData.put("appJSON", (Object)nonSystemApps.toString());
                            queueData.put("allowedApps", (Object)nonSystemAppsData);
                            final CommonQueueData windowsAppData = new CommonQueueData();
                            windowsAppData.setCustomerId(1L);
                            windowsAppData.setTaskName("WindowsAppDBUpdateTask");
                            windowsAppData.setClassName("com.me.mdm.server.windows.apps.task.WindowsAppDBUpdateTask");
                            windowsAppData.setJsonQueueData(queueData);
                            CommonQueueUtil.getInstance().addToQueue(windowsAppData, CommonQueues.MDM_PROFILE_MGMT);
                        }
                        else {
                            subRow = new Row("WindowsKioskPolicySystemApps");
                            subRow.set("APP_ID", (Object)Long.valueOf(appDataObj.optString("APP_ID", "-1")));
                            subRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
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
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    private void getWindowsAppDetailsDO(final Properties properties) throws Exception {
        final JSONArray appGroupIDArr = new JSONArray((String)((Hashtable<K, String>)properties).get("appJSON"));
        final JSONObject appData = ((Hashtable<K, JSONObject>)properties).get("allowedApps");
        final List appGrpList = new ArrayList();
        final DataObject dataObject = (DataObject)new WritableDataObject();
        for (int i = 0; i < appGroupIDArr.length(); ++i) {
            final Long appGroupID = Long.valueOf(appGroupIDArr.optString(i, "-1"));
            appGrpList.add(appGroupID);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "*"));
        DataObject dataObject2 = null;
        dataObject2 = MDMUtil.getPersistence().get(selectQuery);
        for (int j = 0; j < appGroupIDArr.length(); ++j) {
            final Long appGroupID2 = Long.valueOf(appGroupIDArr.optString(j, "-1"));
            final Iterator iterator = dataObject2.getRows("MdAppToGroupRel", new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupID2, 0));
            final List appIDList = new ArrayList();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                appIDList.add(row.get("APP_ID"));
            }
            for (int k = 0; k < appIDList.size(); ++k) {
                Row row2 = dataObject2.getRow("WindowsAppDetails", new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), appIDList.get(k), 0));
                final String AUMID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).get("AUMID");
                final String storeID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).opt("storeID");
                final String productID = (String)appData.getJSONObject(appGroupIDArr.optString(j, "-1")).opt("productID");
                if (row2 == null) {
                    row2 = new Row("WindowsAppDetails");
                    row2.set("APP_ID", appIDList.get(k));
                    row2.set("AUMID", (Object)AUMID);
                    row2.set("PRODUCT_ID", (Object)storeID);
                    if (!MDMStringUtils.isEmpty(productID)) {
                        row2.set("PHONE_PRODUCT_ID", (Object)productID);
                    }
                    dataObject.addRow(row2);
                }
                else {
                    row2.set("AUMID", (Object)AUMID);
                    row2.set("PRODUCT_ID", (Object)storeID);
                    if (!MDMStringUtils.isEmpty(productID)) {
                        row2.set("PHONE_PRODUCT_ID", (Object)productID);
                    }
                    dataObject.updateRow(row2);
                }
            }
        }
        MDMUtil.getPersistence().update(dataObject);
    }
}
