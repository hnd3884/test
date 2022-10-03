package com.me.mdm.server.apps.businessstore;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import com.me.mdm.server.apps.businessstore.windows.WindowsStoreHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import java.util.logging.Logger;

public class MDBusinessStoreUtil
{
    public static Logger logger;
    
    public static StoreInterface getInstance(final int platformType, final Long customerID) {
        StoreInterface storeInterface = null;
        if (platformType == 1) {
            storeInterface = new IOSStoreHandler(null, customerID);
        }
        else if (platformType == 3) {
            storeInterface = new WindowsStoreHandler(null, customerID);
        }
        else if (platformType == 2) {
            storeInterface = new AndroidStoreHandler(null, customerID);
        }
        return storeInterface;
    }
    
    public static SelectQuery getBusinessStoreQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
        selectQuery.addJoin(new Join("ManagedBusinessStore", "MDMResource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        return selectQuery;
    }
    
    public static Long getBusinessStoreID(final Long customerID, final int serviceType) {
        Long businessStoreID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "MDMResource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria serviceCrit = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)serviceType, 0);
            selectQuery.setCriteria(customerCrit.and(serviceCrit));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row businessRow = dataObject.getFirstRow("ManagedBusinessStore");
                businessStoreID = (Long)businessRow.get("BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreID", e);
        }
        return businessStoreID;
    }
    
    public static String getBusinessStoreParamValue(final String paramName, final Long businessStoreID) {
        String paramValue = null;
        try {
            final SelectQuery bsParamQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreParams"));
            bsParamQuery.addSelectColumn(Column.getColumn("MdBusinessStoreParams", "*"));
            final Criteria businessStoreCrit = new Criteria(Column.getColumn("MdBusinessStoreParams", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria paramCriteria = new Criteria(Column.getColumn("MdBusinessStoreParams", "PARAM_NAME"), (Object)paramName, 0);
            bsParamQuery.setCriteria(businessStoreCrit.and(paramCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(bsParamQuery);
            if (!dataObject.isEmpty()) {
                final Row paramRow = dataObject.getFirstRow("MdBusinessStoreParams");
                paramValue = (String)paramRow.get("PARAM_VALUE");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreParamValue", e);
        }
        return paramValue;
    }
    
    public static void addOrUpdateBusinessStoreParam(final String paramName, final String paramValue, final Long businessStoreID) {
        try {
            final SelectQuery bsParamQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreParams"));
            bsParamQuery.addSelectColumn(Column.getColumn("MdBusinessStoreParams", "*"));
            final Criteria businessStoreCrit = new Criteria(Column.getColumn("MdBusinessStoreParams", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria paramCriteria = new Criteria(Column.getColumn("MdBusinessStoreParams", "PARAM_NAME"), (Object)paramName, 0);
            bsParamQuery.setCriteria(businessStoreCrit.and(paramCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(bsParamQuery);
            if (!dataObject.isEmpty()) {
                final Row paramRow = dataObject.getFirstRow("MdBusinessStoreParams");
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                dataObject.updateRow(paramRow);
            }
            else {
                final Row paramRow = new Row("MdBusinessStoreParams");
                paramRow.set("PARAM_NAME", (Object)paramName);
                paramRow.set("PARAM_VALUE", (Object)paramValue);
                paramRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
                dataObject.addRow(paramRow);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateBusinessStoreParam", e);
        }
    }
    
    public static JSONObject getBusinessStoreDetails(final Long customerID, final int serviceType) throws DataAccessException, JSONException {
        final Table table = new Table("ManagedBusinessStore");
        final Join resJoin = new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        selectQuery.addJoin(resJoin);
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_IDENTIFICATION"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        final Criteria serviceCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)serviceType, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(serviceCriteria.and(customerCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONObject response = new JSONObject();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ManagedBusinessStore");
            response.put("BUSINESSSTORE_ID", row.get("BUSINESSSTORE_ID"));
            response.put("BUSINESSSTORE_IDENTIFICATION", row.get("BUSINESSSTORE_IDENTIFICATION"));
        }
        else {
            response.put("Error", (Object)"No rows retrieved");
        }
        return response;
    }
    
    public static JSONObject getBusinessStoreSyncDetails(final Long businessStoreID) {
        final JSONObject syncDetails = new JSONObject();
        try {
            final SelectQuery syncDetailsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            syncDetailsQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreSyncStatus", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1));
            syncDetailsQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreSyncDetails", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1));
            syncDetailsQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "*"));
            syncDetailsQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "*"));
            syncDetailsQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject syncDO = MDMUtil.getPersistence().get(syncDetailsQuery);
            if (!syncDO.isEmpty()) {
                final Row syncDetailsRow = syncDO.getRow("MdBusinessStoreSyncDetails");
                if (syncDetailsRow != null) {
                    final int completedCount = (int)syncDetailsRow.get("COMPLETED_APP_COUNT");
                    final int failedCount = (int)syncDetailsRow.get("FAILED_APP_COUNT");
                    final int totalCount = (int)syncDetailsRow.get("TOTAL_APP_COUNT");
                    final Long storeNextSync = (Long)syncDetailsRow.get("STORE_NEXT_SYNC");
                    final Long lastSuccessfulSync = (Long)syncDetailsRow.get("LAST_SUCCESSFULL_SYNC_TIME");
                    syncDetails.put("COMPLETED_APP_COUNT", completedCount);
                    syncDetails.put("FAILED_APP_COUNT", failedCount);
                    syncDetails.put("TOTAL_APP_COUNT", totalCount);
                    if (storeNextSync != null) {
                        syncDetails.put("STORE_NEXT_SYNC", (Object)storeNextSync);
                    }
                    if (lastSuccessfulSync != null) {
                        syncDetails.put("LAST_SUCCESSFULL_SYNC_TIME", (Object)lastSuccessfulSync);
                    }
                }
                final Row syncStatusRow = syncDO.getRow("MdBusinessStoreSyncStatus");
                if (syncStatusRow != null) {
                    final Integer errorCode = (Integer)syncStatusRow.get("ERROR_CODE");
                    final String remarks = (String)syncStatusRow.get("REMARKS");
                    final String remarksParams = (String)syncStatusRow.get("REMARKS_PARAMS");
                    final Integer storeSyncStatus = (Integer)syncStatusRow.get("STORE_SYNC_STATUS");
                    final Long currentSyncLastProgress = (Long)syncStatusRow.get("CURRENT_SYNC_LAST_PROGRESS");
                    if (errorCode != null && errorCode != -1) {
                        syncDetails.put("ERROR_CODE", (Object)errorCode);
                    }
                    if (remarks != null) {
                        syncDetails.put("REMARKS", (Object)remarks);
                    }
                    if (remarksParams != null) {
                        syncDetails.put("REMARKS_PARAMS", (Object)remarksParams);
                    }
                    if (storeSyncStatus != null) {
                        syncDetails.put("STORE_SYNC_STATUS", (Object)storeSyncStatus);
                    }
                    if (currentSyncLastProgress != null) {
                        syncDetails.put("CURRENT_SYNC_LAST_PROGRESS", (Object)currentSyncLastProgress);
                    }
                }
            }
            else {
                MDBusinessStoreUtil.logger.log(Level.INFO, "No sync details available for businessStore {0}", new Object[] { businessStoreID });
                syncDetails.put("STORE_SYNC_STATUS", 0);
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreSyncDetails", e);
        }
        return syncDetails;
    }
    
    public static List<Long> getBusinessStoreIDs(final Long customerID, final int serviceType) {
        List<Long> businessStoreIDList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "MDMResource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCrit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria serviceCrit = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)serviceType, 0);
            selectQuery.setCriteria(customerCrit.and(serviceCrit));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("ManagedBusinessStore");
                businessStoreIDList = DBUtil.getColumnValuesAsList(iter, "BUSINESSSTORE_ID");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreIDs", e);
        }
        return businessStoreIDList;
    }
    
    public static Long getLastSuccessfulSyncTime(final Long businessStoreID) {
        Long lastSyncTime = null;
        try {
            final Row syncStatusRow = DBUtil.getRowFromDB("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID", (Object)businessStoreID);
            if (syncStatusRow != null) {
                lastSyncTime = (Long)syncStatusRow.get("LAST_SUCCESSFULL_SYNC_TIME");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getLastSyncSuccessfulTime()", e);
        }
        return lastSyncTime;
    }
    
    public static Long getBusinessStoreAddedByUserID(final Long businessStoreID) {
        Long userID = null;
        try {
            final Row businessRow = DBUtil.getRowFromDB("ManagedBusinessStore", "BUSINESSSTORE_ID", (Object)businessStoreID);
            userID = (Long)businessRow.get("BUSINESSSTORE_ADDED_BY");
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreAddedByUserID");
        }
        return businessStoreID;
    }
    
    public static Long getCurrentSyncLastProgress(final Long businessStoreID) {
        Long lastSyncTime = null;
        try {
            final Row syncStatusRow = DBUtil.getRowFromDB("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID", (Object)businessStoreID);
            lastSyncTime = (Long)syncStatusRow.get("CURRENT_SYNC_LAST_PROGRESS");
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getLastSyncSuccessfulTime()", e);
        }
        return lastSyncTime;
    }
    
    public static void updateStoreSyncStatus(final Long businessStoreID, final int status) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncStatus");
            updateQuery.setUpdateColumn("STORE_SYNC_STATUS", (Object)status);
            updateQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in clearSyncStatus", e);
        }
    }
    
    public static Integer getStoreSyncStatus(final Long businessStoreID) {
        Integer status = null;
        try {
            final Row bsSyncStatusRow = DBUtil.getRowFromDB("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID", (Object)businessStoreID);
            if (bsSyncStatusRow != null) {
                status = (Integer)bsSyncStatusRow.get("STORE_SYNC_STATUS");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getStoreSyncStatus", e);
        }
        return status;
    }
    
    public static void updateCurrentSyncLastProgress(final Long businessStoreID) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncStatus");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("CURRENT_SYNC_LAST_PROGRESS", (Object)MDMUtil.getCurrentTimeInMillis());
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateCurrentSyncLastProgress", ex);
        }
    }
    
    public static void updateLastSuccessfulSyncTime(final Long businessStoreID) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncDetails");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("LAST_SUCCESSFULL_SYNC_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateCurrentSyncLastProgress", ex);
        }
    }
    
    public static void updateStoreNextSyncTime(final Long businessStoreID, final Long storeNextSyncTime) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncDetails");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("STORE_NEXT_SYNC", (Object)storeNextSyncTime);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in updateStoreNextSyncTime", ex);
        }
    }
    
    public static void updateTotalAppsCount(final Long businessStoreID, final Integer count) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncDetails");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("TOTAL_APP_COUNT", (Object)count);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in updateTotalAppsCount", ex);
        }
    }
    
    public static void updateCompletedAppsCount(final Long businessStoreID, final Integer count) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncDetails");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("COMPLETED_APP_COUNT", (Object)count);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in updateCompletedAppsCount", ex);
        }
    }
    
    public static void updateFailedAppsCount(final Long businessStoreID, final Integer count) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdBusinessStoreSyncDetails");
            uQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            uQuery.setUpdateColumn("FAILED_APP_COUNT", (Object)count);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in updateCompletedAppsCount", ex);
        }
    }
    
    public static void setInitialSyncDetails(final Long businessStoreID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreSyncDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "*"));
            selectQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            Row syncRow = null;
            if (DO.isEmpty()) {
                syncRow = new Row("MdBusinessStoreSyncDetails");
                syncRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            }
            else {
                syncRow = DO.getFirstRow("MdBusinessStoreSyncDetails");
            }
            syncRow.set("TOTAL_APP_COUNT", (Object)new Integer(0));
            syncRow.set("FAILED_APP_COUNT", (Object)new Integer(0));
            syncRow.set("COMPLETED_APP_COUNT", (Object)new Integer(0));
            if (DO.isEmpty()) {
                DO.addRow(syncRow);
            }
            DO.updateRow(syncRow);
            MDMUtil.getPersistence().update(DO);
            MDBusinessStoreUtil.logger.log(Level.INFO, "Initial Sync details set successfully.");
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in setInitialSyncDetails", ex);
        }
    }
    
    public static void resetAppSyncStatus(final Long businessStoreID) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreSyncDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "*"));
            selectQuery.setCriteria(new Criteria(new Column("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            Row syncRow = null;
            if (DO.isEmpty()) {
                syncRow = new Row("MdBusinessStoreSyncDetails");
                syncRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            }
            else {
                syncRow = DO.getFirstRow("MdBusinessStoreSyncDetails");
            }
            syncRow.set("FAILED_APP_COUNT", (Object)new Integer(0));
            syncRow.set("COMPLETED_APP_COUNT", (Object)new Integer(0));
            if (DO.isEmpty()) {
                DO.addRow(syncRow);
            }
            DO.updateRow(syncRow);
            MDMUtil.getPersistence().update(DO);
            MDBusinessStoreUtil.logger.log(Level.INFO, "Initial Sync details set successfully.");
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in setInitialSyncDetails", ex);
        }
    }
    
    public static void incrementBusinessStoreAppsFailedCount(final Long businessStoreID, final Integer count) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreSyncDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "FAILED_APP_COUNT"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdBusinessStoreSyncDetails");
                Integer failedCount = (Integer)row.get("FAILED_APP_COUNT");
                failedCount += count;
                row.set("FAILED_APP_COUNT", (Object)failedCount);
                dataObject.updateRow(row);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in incrementBusinessStoreAppsFailedCount", e);
        }
    }
    
    public static void incrementBusinessStoreAppsCompletedCount(final Long businessStoreID, final Integer count) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreSyncDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncDetails", "COMPLETED_APP_COUNT"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdBusinessStoreSyncDetails");
                Integer completedCount = (Integer)row.get("COMPLETED_APP_COUNT");
                completedCount += count;
                row.set("COMPLETED_APP_COUNT", (Object)completedCount);
                dataObject.updateRow(row);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in incrementBusinessStoreAppsCompletedCount", e);
        }
    }
    
    public static void addOrUpdateBusinessStoreSyncStatus(final Long businessStoreID, final Integer errorCode, final String remarks, final String remarksParams, final Integer status) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdBusinessStoreSyncStatus"));
            selectQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            Row errorRow = null;
            if (DO.isEmpty()) {
                errorRow = new Row("MdBusinessStoreSyncStatus");
                errorRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            }
            else {
                errorRow = DO.getFirstRow("MdBusinessStoreSyncStatus");
            }
            if (errorCode != null) {
                errorRow.set("ERROR_CODE", (Object)errorCode);
            }
            errorRow.set("REMARKS", (Object)remarks);
            errorRow.set("REMARKS_PARAMS", (Object)remarksParams);
            if (status != null) {
                errorRow.set("STORE_SYNC_STATUS", (Object)status);
            }
            errorRow.set("CURRENT_SYNC_LAST_PROGRESS", (Object)MDMUtil.getCurrentTimeInMillis());
            if (DO.isEmpty()) {
                DO.addRow(errorRow);
            }
            else {
                DO.updateRow(errorRow);
            }
            MDMUtil.getPersistence().update(DO);
            MDBusinessStoreUtil.logger.log(Level.INFO, "MDBusinessStoreSyncStatus for business store ID {0} set successfully", businessStoreID);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateBusinessStoreErrorCode", e);
        }
    }
    
    public static String getBusinessStoreAlertMailAddress(final Long businessStoreID) {
        String email = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDBusinessStoreAlertDetails"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MDBusinessStoreAlertDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            selectQuery.addSelectColumn(Column.getColumn("MDBusinessStoreAlertDetails", "*"));
            final DataObject mailDO = MDMUtil.getPersistence().get(selectQuery);
            if (!mailDO.isEmpty()) {
                final Row mailRow = mailDO.getFirstRow("MDBusinessStoreAlertDetails");
                email = (String)mailRow.get("EMAIL_ADDRESS");
            }
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in getBusinessStoreErrorDetails");
        }
        return email;
    }
    
    public static void addOrUpdateStoreAlertMail(final Long businessStoreID, final String mailAddr) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDBusinessStoreAlertDetails"));
            selectQuery.addSelectColumn(new Column("MDBusinessStoreAlertDetails", "*"));
            selectQuery.setCriteria(new Criteria(new Column("MDBusinessStoreAlertDetails", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            Row alertRow = null;
            if (DO.isEmpty()) {
                alertRow = new Row("MDBusinessStoreAlertDetails");
            }
            else {
                alertRow = DO.getFirstRow("MDBusinessStoreAlertDetails");
            }
            alertRow.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            alertRow.set("EMAIL_ADDRESS", (Object)mailAddr);
            if (DO.isEmpty()) {
                DO.addRow(alertRow);
            }
            else {
                DO.updateRow(alertRow);
            }
            MDMUtil.getPersistence().update(DO);
        }
        catch (final Exception ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateStoreAlertMail", ex);
        }
    }
    
    public static int getServiceType(final int platformType) {
        int serviceType = 0;
        if (platformType == 1) {
            serviceType = StoreInterface.BS_SERVICE_VPP;
        }
        else if (platformType == 3) {
            serviceType = StoreInterface.BS_SERVICE_WBS;
        }
        else if (platformType == 2) {
            serviceType = StoreInterface.BS_SERVICE_AFW;
        }
        return serviceType;
    }
    
    public static int getPlatformType(final int serviceType) {
        int platformType = 0;
        if (serviceType == StoreInterface.BS_SERVICE_VPP) {
            platformType = 1;
        }
        else if (serviceType == StoreInterface.BS_SERVICE_WBS) {
            platformType = 3;
        }
        else if (serviceType == StoreInterface.BS_SERVICE_AFW) {
            platformType = 2;
        }
        return platformType;
    }
    
    public static Long getBusinessStoreIDFromAPIBody(final JSONObject message) {
        Long businessStoreID = -1L;
        if (message != null && message.has("msg_body")) {
            businessStoreID = message.getJSONObject("msg_body").optLong("businessstore_id", -1L);
        }
        return businessStoreID;
    }
    
    public static String getBusinessStoreName(final Long businessStoreID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedBusinessStore"));
        selectQuery.addJoin(new Join("ManagedBusinessStore", "MDMResource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MDMResource", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
        selectQuery.addSelectColumn(new Column("ManagedBusinessStore", "BUSINESSSTORE_ID"));
        selectQuery.addSelectColumn(new Column("ManagedBusinessStore", "BS_SERVICE_TYPE"));
        selectQuery.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("Resource", "CUSTOMER_ID"));
        DataObject dataObject = null;
        try {
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row bsrow = dataObject.getFirstRow("ManagedBusinessStore");
                final Row resourceRow = dataObject.getFirstRow("Resource");
                return new StoreFacade().getStoreName(getPlatformType((int)bsrow.get("BS_SERVICE_TYPE")), (Long)resourceRow.get("CUSTOMER_ID"), businessStoreID);
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void deleteBusinessStoreParam(final String paramName, final Long businessStoreID) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdBusinessStoreParams");
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreParams", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria paramNameCriteria = new Criteria(Column.getColumn("MdBusinessStoreParams", "PARAM_NAME"), (Object)paramName, 0);
            deleteQuery.setCriteria(businessStoreCriteria.and(paramNameCriteria));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in deleteBusinessStoreParam", e);
        }
    }
    
    public static Join getLatestDeploymentConfigJoinForRecentProfileForResource() {
        final SelectQuery latestDeplymentConfigQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
        final Column latestAssignedTime = Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME").maximum();
        latestAssignedTime.setColumnAlias("ADDED_TIME");
        latestDeplymentConfigQuery.addSelectColumn(latestAssignedTime);
        final Column profileIDCol = Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID");
        final Column resIDCol = Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID");
        final Column bsiDCol = Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID");
        latestDeplymentConfigQuery.addSelectColumn(profileIDCol);
        latestDeplymentConfigQuery.addSelectColumn(resIDCol);
        latestDeplymentConfigQuery.setGroupByClause(new GroupByClause((List)Arrays.asList(new GroupByColumn(resIDCol, (boolean)Boolean.FALSE), new GroupByColumn(profileIDCol, (boolean)Boolean.FALSE))));
        final DerivedTable derivedTable = new DerivedTable("tempa", (Query)latestDeplymentConfigQuery);
        final SelectQuery latestDeplymentConfigQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
        latestDeplymentConfigQuery2.addSelectColumn(profileIDCol);
        latestDeplymentConfigQuery2.addSelectColumn(resIDCol);
        latestDeplymentConfigQuery2.addSelectColumn(bsiDCol);
        latestDeplymentConfigQuery2.addJoin(new Join(Table.getTable("MDMResourceToDeploymentConfigs"), (Table)derivedTable, new String[] { "RESOURCE_ID", "PROFILE_ID", "ADDED_TIME" }, new String[] { "RESOURCE_ID", "PROFILE_ID", "ADDED_TIME" }, 2));
        final DerivedTable derivedTable2 = new DerivedTable("MDMResourceToDeploymentConfigs", (Query)latestDeplymentConfigQuery2);
        final Criteria jc1 = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0);
        final Criteria jc2 = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
        final Join resToDeploymentConfig = new Join(Table.getTable("RecentProfileForResource"), (Table)derivedTable2, jc1.and(jc2), 1);
        return resToDeploymentConfig;
    }
    
    public static void validateIfStoreFound(final Long businessStoreID, final Long customerID, final int serviceType) throws Exception {
        try {
            final SelectQuery businessQuery = getBusinessStoreQuery();
            businessQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria serviceCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BS_SERVICE_TYPE"), (Object)serviceType, 0);
            businessQuery.setCriteria(businessStoreCriteria.and(customerCriteria).and(serviceCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(businessQuery);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { businessStoreID });
            }
        }
        catch (final DataAccessException ex) {
            MDBusinessStoreUtil.logger.log(Level.SEVERE, "Exception in validateIfStoreFound", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        MDBusinessStoreUtil.logger = Logger.getLogger("MDMBStoreLogger");
    }
}
