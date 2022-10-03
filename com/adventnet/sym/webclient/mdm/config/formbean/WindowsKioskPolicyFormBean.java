package com.adventnet.sym.webclient.mdm.config.formbean;

import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
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

public class WindowsKioskPolicyFormBean extends MDMDefaultFormBean
{
    public Logger logger;
    
    public WindowsKioskPolicyFormBean() {
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
                        dynaJSON.put("CONFIG_DATA_ITEM_ID", configDataItemID);
                        super.insertConfigDataItemExtn(dynaJSON, dataObject);
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
                    final JSONObject appData = new JSONObject(dynaJSON.optString("ALLOWED_APPS"));
                    final JSONArray appGroupIDArr = appData.names();
                    final JSONArray nonSystemApps = new JSONArray();
                    final Properties properties = new Properties();
                    for (int i = 0; i < appGroupIDArr.length(); ++i) {
                        final Boolean isSystemApp = Boolean.valueOf(appData.getJSONObject(appGroupIDArr.optString(i, "-1")).optString("IsWindowsSystemApp", "false"));
                        Row subRow = null;
                        if (!isSystemApp) {
                            nonSystemApps.put((Object)appGroupIDArr.optString(i, "-1"));
                            subRow = new Row(subTableName);
                            final List subColumns = payloadSubRow.getColumns();
                            for (final Object subColumn : subColumns) {
                                final String columnName = (String)subColumn;
                                if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    subRow.set(columnName, configDataItemID);
                                }
                                else {
                                    subRow.set(columnName, (Object)Long.valueOf(appGroupIDArr.optString(i, "-1")));
                                }
                            }
                            properties.setProperty("appJSON", nonSystemApps.toString());
                            properties.setProperty("allowedApps", appData.toString());
                            this.getWindowsAppDetailsDO(properties);
                            final JSONObject queueData = new JSONObject();
                            queueData.put("appJSON", (Object)nonSystemApps.toString());
                            queueData.put("allowedApps", (Object)appData.toString());
                            final CommonQueueData windowsAppData = new CommonQueueData();
                            windowsAppData.setCustomerId(1L);
                            windowsAppData.setTaskName("WindowsAppDBUpdateTask");
                            windowsAppData.setClassName("com.me.mdm.server.windows.apps.task.WindowsAppDBUpdateTask");
                            windowsAppData.setJsonQueueData(queueData);
                            CommonQueueUtil.getInstance().addToQueue(windowsAppData, CommonQueues.MDM_PROFILE_MGMT);
                        }
                        else {
                            subRow = new Row("WindowsKioskPolicySystemApps");
                            subRow.set("APP_ID", (Object)Long.valueOf(appGroupIDArr.optString(i, "-1")));
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
    
    public JSONObject getAppInformation(final List<Long> appGroupID, final List<Long> systemAppGroupIDList) {
        final JSONObject containerApps = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appToClnJoin = new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join pkgJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            selectQuery.addJoin(appToGroupJoin);
            selectQuery.addJoin(appJoin);
            selectQuery.addJoin(appToClnJoin);
            selectQuery.addJoin(pkgJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "AUMID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "PHONE_PRODUCT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "PRODUCT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
            final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID.toArray(), 8);
            selectQuery.setCriteria(criteria);
            try {
                DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                final List<String> addedIdentifier = new ArrayList<String>();
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                    while (iterator.hasNext()) {
                        final Row mdAppGroupDetailsRow = iterator.next();
                        final JSONObject appData = new JSONObject();
                        final Object appGroupObj = mdAppGroupDetailsRow.get("APP_GROUP_ID");
                        final Object identifierObj = mdAppGroupDetailsRow.get("IDENTIFIER");
                        final Object groupDisplyNameObj = mdAppGroupDetailsRow.get("GROUP_DISPLAY_NAME");
                        final Long appId = (Long)dataObject.getRow("MdAppToGroupRel", new Criteria(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), appGroupObj, 0)).get("APP_ID");
                        final Row windowsAppDetailsRow = dataObject.getRow("WindowsAppDetails", new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appId, 0));
                        final Object aumid = windowsAppDetailsRow.get("AUMID");
                        final Object phoneProductID = windowsAppDetailsRow.get("PHONE_PRODUCT_ID");
                        final Row mdPackageToAppGroupRow = dataObject.getRow("MdPackageToAppGroup", new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), appGroupObj, 0));
                        Object packageID = null;
                        Object isPurchasedFromProtal = null;
                        if (mdPackageToAppGroupRow != null) {
                            packageID = mdPackageToAppGroupRow.get("PACKAGE_ID");
                            isPurchasedFromProtal = mdPackageToAppGroupRow.get("IS_PURCHASED_FROM_PORTAL");
                        }
                        final Object storeID = windowsAppDetailsRow.get("PRODUCT_ID");
                        final String identifier = (String)identifierObj;
                        if (!addedIdentifier.contains(identifier)) {
                            final String appName = (String)groupDisplyNameObj;
                            appData.put("APP_GROUP_ID", (Object)appGroupObj);
                            appData.put("IDENTIFIER", (Object)identifier);
                            appData.put("GROUP_DISPLAY_NAME", (Object)appName);
                            appData.put("AUMID", aumid);
                            if (packageID != null) {
                                appData.put("managedApp", true);
                                appData.put("enterpriseApp", !(boolean)isPurchasedFromProtal);
                            }
                            appData.put("productID", phoneProductID);
                            if (storeID != null) {
                                appData.put("storeID", storeID);
                            }
                            containerApps.put(appGroupObj + "", (Object)appData);
                            addedIdentifier.add(identifier);
                        }
                    }
                }
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("WindowsSystemApps"));
                selectQuery2.addSelectColumn(Column.getColumn("WindowsSystemApps", "*"));
                selectQuery2.setCriteria(new Criteria(Column.getColumn("WindowsSystemApps", "APP_ID"), (Object)systemAppGroupIDList.toArray(), 8));
                dataObject = MDMUtil.getPersistence().get(selectQuery2);
                final Iterator iterator2 = dataObject.getRows("WindowsSystemApps");
                while (iterator2.hasNext()) {
                    final Row row = iterator2.next();
                    final JSONObject appData2 = new JSONObject();
                    final Long appGroupObj2 = (Long)row.get("APP_ID");
                    final String identifierObj2 = (String)row.get("PACKAGE_FAMILY_NAME");
                    final String groupDisplyNameObj2 = (String)row.get("APP_NAME");
                    final String aumid2 = (String)row.get("AUMID");
                    final String phoneProductID2 = (String)row.get("PHONE_PRODUCT_ID");
                    appData2.put("APP_GROUP_ID", (Object)appGroupObj2);
                    appData2.put("IDENTIFIER", (Object)identifierObj2);
                    appData2.put("GROUP_DISPLAY_NAME", (Object)groupDisplyNameObj2);
                    appData2.put("AUMID", (Object)aumid2);
                    appData2.put("productID", (Object)phoneProductID2);
                    appData2.put("IsWindowsSystemApp", true);
                    containerApps.put(appGroupObj2 + "", (Object)appData2);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex2);
        }
        return containerApps;
    }
    
    private void getWindowsAppDetailsDO(final Properties properties) throws Exception {
        final JSONArray appGroupIDArr = new JSONArray((String)((Hashtable<K, String>)properties).get("appJSON"));
        final JSONObject appData = new JSONObject((String)((Hashtable<K, String>)properties).get("allowedApps"));
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
                if (row2 == null) {
                    row2 = new Row("WindowsAppDetails");
                    row2.set("APP_ID", appIDList.get(k));
                    row2.set("AUMID", (Object)AUMID);
                    row2.set("PRODUCT_ID", (Object)storeID);
                    dataObject.addRow(row2);
                }
                else {
                    row2.set("AUMID", (Object)AUMID);
                    row2.set("PRODUCT_ID", (Object)storeID);
                    dataObject.updateRow(row2);
                }
            }
        }
        MDMUtil.getPersistence().update(dataObject);
    }
}
