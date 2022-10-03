package com.me.devicemanagement.framework.server.config;

import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccess;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class CollectionUtil
{
    protected static Logger logger;
    public static final Integer TOTAL_TARGETS_COUNT;
    public static final Integer YET_TO_APPLY_TARGETS_COUNT;
    public static final Integer SUCCEEDED_TARGETS_COUNT;
    public static final Integer FAILED_TARGETS_COUNT;
    public static final Integer INPROGRESS_TARGETS_COUNT;
    public static final Integer INPROGRESS_FAILED_TARGETS_COUNT;
    public static final Integer RETRY_INPROGRESS_TARGETS_COUNT;
    public static final Integer NOT_APPLICABLE_TARGETS_COUNT;
    public static final Integer NOTIFICATION_SENT_COUNT;
    
    public static Integer getCollectionType(final Long collectionId) throws SyMException {
        Integer collnType = null;
        try {
            final Row row = new Row("Collection");
            row.set("COLLECTION_ID", (Object)collectionId);
            final DataObject resultDO = SyMUtil.getPersistence().get("Collection", row);
            if (!resultDO.isEmpty()) {
                collnType = (Integer)resultDO.getFirstValue("Collection", "COLLECTION_TYPE");
            }
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while getting collection type for the given collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
        return collnType;
    }
    
    public static Integer getCollectionPlatForm(final Long collectionId) throws SyMException {
        Integer platFormID = null;
        try {
            final Row row = new Row("Collection");
            row.set("COLLECTION_ID", (Object)collectionId);
            final DataObject resultDO = SyMUtil.getPersistence().get("Collection", row);
            if (!resultDO.isEmpty()) {
                platFormID = (Integer)resultDO.getFirstValue("Collection", "PLATFORM_ID");
            }
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while getting platform type for the given collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
        return platFormID;
    }
    
    public static List getTargetResourceIds(final Long collectionId) throws SyMException {
        return getTargetResourceIds(collectionId, null);
    }
    
    public static List getTargetResourceIds(final Long collectionId, final Criteria statusCriteria) throws SyMException {
        final List resourceIds = new ArrayList();
        try {
            final String tblName = "CollnToResources";
            final Column col = Column.getColumn(tblName, "COLLECTION_ID");
            Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            if (statusCriteria != null) {
                criteria = criteria.and(statusCriteria);
            }
            final DataObject resultDO = SyMUtil.getPersistence().get(tblName, criteria);
            if (!resultDO.isEmpty()) {
                final Iterator rows = resultDO.getRows(tblName);
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    resourceIds.add(row.get("RESOURCE_ID"));
                }
            }
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Error while retrieving resource ids for  collection id: " + collectionId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Error while retrieving resource ids for  collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
        return resourceIds;
    }
    
    public static Hashtable getTargetResourcesCountWithStatusFromSummaryTable(final Long collectionId) throws SyMException {
        Hashtable statusCntHash = new Hashtable();
        try {
            final Column col = Column.getColumn("CollectionStatusSummary", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collSummaryDO = SyMUtil.getPersistence().get("CollectionStatusSummary", criteria);
            if (collSummaryDO.isEmpty()) {
                statusCntHash = computeAndUpdateCollectionStatusSummary(collectionId);
            }
            else {
                final Row collSummaryRow = collSummaryDO.getRow("CollectionStatusSummary");
                statusCntHash.put(CollectionUtil.TOTAL_TARGETS_COUNT, collSummaryRow.get("TOTAL_TARGET_COUNT"));
                statusCntHash.put(CollectionUtil.YET_TO_APPLY_TARGETS_COUNT, collSummaryRow.get("YET_TO_APPLY_COUNT"));
                statusCntHash.put(CollectionUtil.SUCCEEDED_TARGETS_COUNT, collSummaryRow.get("SUCCESS_COUNT"));
                statusCntHash.put(CollectionUtil.FAILED_TARGETS_COUNT, collSummaryRow.get("FAILED_COUNT"));
                statusCntHash.put(CollectionUtil.INPROGRESS_TARGETS_COUNT, collSummaryRow.get("INPROGRESS_COUNT"));
                statusCntHash.put(CollectionUtil.INPROGRESS_FAILED_TARGETS_COUNT, collSummaryRow.get("INPROGRESS_FAILED_COUNT"));
                statusCntHash.put(CollectionUtil.RETRY_INPROGRESS_TARGETS_COUNT, collSummaryRow.get("RETRY_INPROGRESS_COUNT"));
                statusCntHash.put(CollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, collSummaryRow.get("NOT_APPLICABLE_COUNT"));
            }
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Error while retrieving target resource counts from summary table for  collection id: " + collectionId, ex);
        }
        return statusCntHash;
    }
    
    public static Hashtable getTargetResourceCountWithStatus(final Long collectionId) throws SyMException {
        final Hashtable statusCntHash = new Hashtable();
        try {
            final int targetCnt = getTargetResourcesCountForColln(collectionId, null);
            final int yetToApplyCnt = getTargetResourcesCountForColln(collectionId, new Integer(12)) + getTargetResourcesCountForColln(collectionId, new Integer(3));
            final int successCnt = getTargetResourcesCountForColln(collectionId, new Integer(6));
            final int inprogressCnt = getTargetResourcesCountForColln(collectionId, new Integer(3));
            final int inprogressFailedCnt = getTargetResourcesCountForColln(collectionId, new Integer(10));
            final int failedCnt = getTargetResourcesCountForColln(collectionId, new Integer(7));
            final int retryInProgCnt = getTargetResourcesCountForColln(collectionId, new Integer(16));
            final int notApplicableCnt = getTargetResourcesCountForColln(collectionId, new Integer(8));
            statusCntHash.put(CollectionUtil.TOTAL_TARGETS_COUNT, new Integer(targetCnt));
            statusCntHash.put(CollectionUtil.YET_TO_APPLY_TARGETS_COUNT, new Integer(yetToApplyCnt));
            statusCntHash.put(CollectionUtil.SUCCEEDED_TARGETS_COUNT, new Integer(successCnt));
            statusCntHash.put(CollectionUtil.INPROGRESS_TARGETS_COUNT, new Integer(inprogressCnt));
            statusCntHash.put(CollectionUtil.INPROGRESS_FAILED_TARGETS_COUNT, new Integer(inprogressFailedCnt));
            statusCntHash.put(CollectionUtil.FAILED_TARGETS_COUNT, new Integer(failedCnt));
            statusCntHash.put(CollectionUtil.RETRY_INPROGRESS_TARGETS_COUNT, new Integer(retryInProgCnt));
            statusCntHash.put(CollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, new Integer(notApplicableCnt));
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while retrieving target resources counts with status for collection id: " + collectionId, ex);
        }
        return statusCntHash;
    }
    
    public static int getTargetResourcesCountForColln(final Long collectionId, final Integer status) throws SyMException {
        int recordCount = 0;
        final String baseTblName = "CollnToResources";
        final Table baseTable = Table.getTable(baseTblName);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        final Column collIdCol = Column.getColumn(baseTblName, "COLLECTION_ID");
        Criteria criteria = new Criteria(collIdCol, (Object)collectionId, 0);
        if (status != null) {
            final Column statusCol = Column.getColumn(baseTblName, "STATUS");
            final Criteria statusCri = new Criteria(statusCol, (Object)status, 0);
            criteria = criteria.and(statusCri);
        }
        query.setCriteria(criteria);
        Column selCol = new Column(baseTblName, "RESOURCE_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        query.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount = (int)value;
                }
            }
            ds.close();
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while retrieving target resources count for collection Id : " + collectionId, ex);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return recordCount;
    }
    
    public static int getCollectionStatus(final Long collectionId) throws SyMException {
        try {
            final Column col = Column.getColumn("CollectionStatus", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collDO = SyMUtil.getPersistence().get("CollectionStatus", criteria);
            if (!collDO.isEmpty()) {
                final Row collRow = collDO.getFirstRow("CollectionStatus");
                final Integer existingStatus = (Integer)collRow.get("STATUS");
                return existingStatus;
            }
            throw new SyMException(1001, "No data found for collection id: " + collectionId, null);
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while retrieving status for collection id: " + collectionId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while retrieving status for collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
    }
    
    public static void updateCollectionStatus(final Long collectionId, final int status) throws SyMException {
        updateCollectionStatus(collectionId, status, null, null);
    }
    
    public static void updateCollectionStatus(final Long collectionId, final int status, final Boolean isStatusComputable) throws SyMException {
        updateCollectionStatus(collectionId, status, isStatusComputable, null);
    }
    
    public static void updateCollectionStatus(final Long collectionId, final int status, final Boolean isStatusComputable, final Boolean dontUpdateIfSuspended) throws SyMException {
        updateCollectionStatus(collectionId, status, isStatusComputable, dontUpdateIfSuspended, null);
    }
    
    public static void updateCollectionStatus(final Long collectionId, final int status, final Boolean isStatusComputable, final Boolean dontUpdateIfSuspended, final Hashtable statusCounts) throws SyMException {
        try {
            final Column col = Column.getColumn("CollectionStatus", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collDO = SyMUtil.getPersistence().get("CollectionStatus", criteria);
            if (!collDO.isEmpty()) {
                final Row collRow = collDO.getFirstRow("CollectionStatus");
                final Integer existingStatus = (Integer)collRow.get("STATUS");
                if (existingStatus == 5 && dontUpdateIfSuspended != null && dontUpdateIfSuspended) {
                    addToSuspendedCollnList(collectionId, status);
                    CollectionUtil.logger.log(Level.INFO, "Retaining the old status for collectionid: " + collectionId + ". Argument for dontUpdateIfSuspended passed is: " + dontUpdateIfSuspended + " Old status: " + existingStatus + ". New status updated in SUSPENDEDCOLLN table. New status : " + status);
                }
                else if ((existingStatus == 1 || existingStatus == 9) && dontUpdateIfSuspended != null && dontUpdateIfSuspended) {
                    CollectionUtil.logger.log(Level.INFO, "Retaining the old status for Draft Collection - collectionid: " + collectionId + ", Old status: " + existingStatus);
                }
                else {
                    collRow.set("STATUS", (Object)new Integer(status));
                }
                if (isStatusComputable != null) {
                    collRow.set("IS_STATUS_COMPUTABLE", (Object)isStatusComputable);
                }
                collRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                collDO.updateRow(collRow);
                SyMUtil.getPersistence().update(collDO);
            }
            else {
                CollectionUtil.logger.log(Level.WARNING, "Status row does not exist in the DB for update for collection id " + collectionId);
                final Row collRow = new Row("CollectionStatus");
                collRow.set("COLLECTION_ID", (Object)collectionId);
                collRow.set("STATUS", (Object)new Integer(status));
                collRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                if (isStatusComputable != null) {
                    collRow.set("IS_STATUS_COMPUTABLE", (Object)isStatusComputable);
                }
                collDO.addRow(collRow);
                SyMUtil.getPersistence().add(collDO);
            }
            if (statusCounts != null) {
                updateCollectionStatusSummary(collectionId, statusCounts);
            }
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while updating the given status: " + status + " for collection id: " + collectionId, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while updating the given status: " + status + " for collection id: " + collectionId, ex2);
            throw new SyMException(1001, ex2);
        }
    }
    
    public static Hashtable computeAndUpdateCollectionStatusSummary(final Long collectionId) throws Exception {
        final Hashtable statusCntHash = getTargetResourceCountWithStatus(collectionId);
        updateCollectionStatusSummary(collectionId, statusCntHash);
        CollectionUtil.logger.log(Level.INFO, "Successfully Computed and Updated CollectionStatusSummary for collection id : " + collectionId);
        return statusCntHash;
    }
    
    public static void updateCollectionStatusSummary(final Long collectionId, final Hashtable statusCounts) throws SyMException {
        try {
            if (statusCounts == null || statusCounts.size() <= 0) {
                CollectionUtil.logger.log(Level.WARNING, "Unable to update CollectionStatusSummary for collection id " + collectionId + " with given status data: " + statusCounts);
                return;
            }
            final Row collSummaryRow = new Row("CollectionStatusSummary");
            collSummaryRow.set("COLLECTION_ID", (Object)collectionId);
            collSummaryRow.set("TOTAL_TARGET_COUNT", getValueFromHash(statusCounts, CollectionUtil.TOTAL_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("YET_TO_APPLY_COUNT", getValueFromHash(statusCounts, CollectionUtil.YET_TO_APPLY_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("SUCCESS_COUNT", getValueFromHash(statusCounts, CollectionUtil.SUCCEEDED_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("FAILED_COUNT", getValueFromHash(statusCounts, CollectionUtil.FAILED_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("INPROGRESS_COUNT", getValueFromHash(statusCounts, CollectionUtil.INPROGRESS_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("INPROGRESS_FAILED_COUNT", getValueFromHash(statusCounts, CollectionUtil.INPROGRESS_FAILED_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("RETRY_INPROGRESS_COUNT", getValueFromHash(statusCounts, CollectionUtil.RETRY_INPROGRESS_TARGETS_COUNT, new Integer(0)));
            collSummaryRow.set("NOT_APPLICABLE_COUNT", getValueFromHash(statusCounts, CollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, new Integer(0)));
            final String statusCountDesc = getStatusCountDescStr(collSummaryRow);
            collSummaryRow.set("STATUS_COUNT_DESC", (Object)statusCountDesc);
            final Column col = Column.getColumn("CollectionStatusSummary", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collSummaryDO = SyMUtil.getPersistence().get("CollectionStatusSummary", criteria);
            if (!collSummaryDO.isEmpty()) {
                collSummaryDO.updateRow(collSummaryRow);
                SyMUtil.getPersistence().update(collSummaryDO);
            }
            else {
                collSummaryDO.addRow(collSummaryRow);
                SyMUtil.getPersistence().add(collSummaryDO);
            }
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while updating the collection status summary for collection id: " + collectionId + " with given status data: " + statusCounts, ex);
        }
    }
    
    private static String getStatusCountDescStr(final Row collectionStatusSummaryRow) throws Exception {
        String countStr = null;
        if (collectionStatusSummaryRow == null) {
            return "--";
        }
        final Integer targetCnt = (Integer)collectionStatusSummaryRow.get("TOTAL_TARGET_COUNT");
        String i18n = countStr = I18N.getMsg("desktopcentral.server.config.tooltip.Total_Target_Count", new Object[] { targetCnt });
        final Integer yetToApplyCnt = (Integer)collectionStatusSummaryRow.get("YET_TO_APPLY_COUNT");
        if (yetToApplyCnt != null && yetToApplyCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Yet_To_Apply_Count", new Object[] { yetToApplyCnt });
            countStr += i18n;
        }
        final Integer successCnt = (Integer)collectionStatusSummaryRow.get("SUCCESS_COUNT");
        if (successCnt != null && successCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Success_Count", new Object[] { successCnt });
            countStr += i18n;
        }
        final Integer retryInPrgCnt = (Integer)collectionStatusSummaryRow.get("RETRY_INPROGRESS_COUNT");
        if (retryInPrgCnt != null && retryInPrgCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Retry_In_Progress_Count", new Object[] { retryInPrgCnt });
            countStr += i18n;
        }
        final Integer failedCnt = (Integer)collectionStatusSummaryRow.get("FAILED_COUNT");
        if (failedCnt != null && failedCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Failed_Count", new Object[] { failedCnt });
            countStr += i18n;
        }
        final Integer notApplicableCnt = (Integer)collectionStatusSummaryRow.get("NOT_APPLICABLE_COUNT");
        if (notApplicableCnt != null && notApplicableCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Not_Applicable_Count", new Object[] { notApplicableCnt });
            countStr += i18n;
        }
        return countStr;
    }
    
    private static String getStatusCountDescStr(final Hashtable statusCounts) throws Exception {
        String countStr = null;
        if (statusCounts == null || statusCounts.size() <= 0) {
            return countStr;
        }
        final Integer targetCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.TOTAL_TARGETS_COUNT, new Integer(0));
        String i18n = countStr = I18N.getMsg("desktopcentral.server.config.tooltip.Total_Target_Count", new Object[] { targetCnt });
        final Integer yetToApplyCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.YET_TO_APPLY_TARGETS_COUNT, new Integer(0));
        if (yetToApplyCnt != null && yetToApplyCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Yet_To_Apply_Count", new Object[] { yetToApplyCnt });
            countStr += i18n;
        }
        final Integer successCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.SUCCEEDED_TARGETS_COUNT, new Integer(0));
        if (successCnt != null && successCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Success_Count", new Object[] { successCnt });
            countStr += i18n;
        }
        final Integer retryInPrgCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.RETRY_INPROGRESS_TARGETS_COUNT, new Integer(0));
        if (retryInPrgCnt != null && retryInPrgCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Retry_In_Progress_Count", new Object[] { retryInPrgCnt });
            countStr += i18n;
        }
        final Integer failedCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.FAILED_TARGETS_COUNT, new Integer(0));
        if (failedCnt != null && failedCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Failed_Count", new Object[] { failedCnt });
            countStr += i18n;
        }
        final Integer notApplicableCnt = (Integer)getValueFromHash(statusCounts, CollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, new Integer(0));
        if (notApplicableCnt != null && notApplicableCnt > 0) {
            i18n = I18N.getMsg("desktopcentral.server.config.tooltip.Not_Applicable_Count", new Object[] { notApplicableCnt });
            countStr += i18n;
        }
        return countStr;
    }
    
    private static Object getValueFromHash(final Hashtable inputHash, final Object key, final Object defaultValue) {
        if (inputHash == null) {
            return defaultValue;
        }
        Object valueObj = inputHash.get(key);
        if (valueObj == null) {
            valueObj = defaultValue;
        }
        return valueObj;
    }
    
    public static boolean isCollectionStatusComputable(final Long collectionId) throws SyMException {
        boolean isComputable = true;
        try {
            final Column col = Column.getColumn("CollectionStatus", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collDO = SyMUtil.getPersistence().get("CollectionStatus", criteria);
            if (!collDO.isEmpty()) {
                final Boolean isComp = (Boolean)collDO.getFirstValue("CollectionStatus", "IS_STATUS_COMPUTABLE");
                isComputable = isComp;
            }
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while retrieving isComputable for collection id: " + collectionId, ex);
        }
        return isComputable;
    }
    
    public static boolean isSingleConfiguration(final Long collectionId) throws SyMException {
        boolean isSingleConfig = true;
        try {
            final Column col = Column.getColumn("Collection", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            final DataObject collDO = SyMUtil.getPersistence().get("Collection", criteria);
            if (!collDO.isEmpty()) {
                final Boolean isComp = (Boolean)collDO.getFirstValue("Collection", "IS_SINGLE_CONFIG");
                isSingleConfig = isComp;
            }
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while retrieving isSingleConfiguration for collection id: " + collectionId, ex);
        }
        return isSingleConfig;
    }
    
    public static Long getModifiedTime(final Long collectionId) {
        Long modTime = new Long(0L);
        try {
            final Row collRow = new Row("Collection");
            collRow.set("COLLECTION_ID", (Object)collectionId);
            final DataObject collDO = SyMUtil.getPersistence().get("Collection", collRow);
            final Row row = collDO.getFirstRow("Collection");
            modTime = (Long)row.get("MODIFIED_TIME");
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while retrieving collection for collectionid: " + collectionId, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while retrieving collection for collectionid: " + collectionId, ex2);
        }
        return modTime;
    }
    
    public static void addToSuspendedCollnList(final Long collectionId, final int oldStatus) {
        try {
            final Row susRow = new Row("SuspendedColln");
            susRow.set("COLLECTION_ID", (Object)collectionId);
            final DataObject susDO = SyMUtil.getPersistence().get("SuspendedColln", susRow);
            if (susDO.isEmpty()) {
                final DataObject susData = SyMUtil.getPersistence().constructDataObject();
                susRow.set("MODIFIED_TIME", (Object)getModifiedTime(collectionId));
                susRow.set("OLD_STATUS", (Object)new Integer(oldStatus));
                susData.addRow(susRow);
                CollectionUtil.logger.log(Level.INFO, "Going to add suspended collection row: " + susRow);
                SyMUtil.getPersistence().add(susData);
            }
            else {
                final Row row = susDO.getFirstRow("SuspendedColln");
                final int status = (int)row.get("OLD_STATUS");
                if (status != oldStatus) {
                    row.set("MODIFIED_TIME", (Object)getModifiedTime(collectionId));
                    row.set("OLD_STATUS", (Object)new Integer(oldStatus));
                    susDO.updateRow(row);
                    CollectionUtil.logger.log(Level.INFO, "Going to update suspended collection row: " + row);
                    SyMUtil.getPersistence().update(susDO);
                }
                else {
                    CollectionUtil.logger.log(Level.INFO, "SUSPENDEDCOLLN table is up to date.");
                }
            }
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while adding collection id into the suspended collection: " + collectionId, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while adding collection id into the suspended collection: " + collectionId, ex2);
        }
    }
    
    public static int deleteFromSuspendedCollnList(final Long collectionId) {
        CollectionUtil.logger.log(Level.FINER, "Going to delete collection id from the suspended collection list: " + collectionId);
        int status = 2;
        try {
            final Row susRow = new Row("SuspendedColln");
            susRow.set("COLLECTION_ID", (Object)collectionId);
            final DataObject susDO = SyMUtil.getPersistence().get("SuspendedColln", susRow);
            if (!susDO.isEmpty()) {
                final Row row = susDO.getFirstRow("SuspendedColln");
                final Long modTime = (Long)row.get("MODIFIED_TIME");
                status = (int)row.get("OLD_STATUS");
                CollectionUtil.logger.log(Level.INFO, "SuspendedColln: MODIFIED_TIME: " + modTime + "Old Status: " + status);
                DataAccess.delete(susRow);
            }
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while removing collection id from the suspended collection list: " + collectionId, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.WARNING, "Caught exception while removing collection id from the suspended collection list: " + collectionId, ex2);
        }
        return status;
    }
    
    public static Hashtable getConfigDataIdWithStatus(final Long collectionId) throws Exception {
        final Hashtable configDataIdVsStatus = new Hashtable();
        try {
            final String baseTableName = "ConfigDataStatus";
            final Table baseTable = Table.getTable(baseTableName);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join(baseTableName, "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            final Column col = Column.getColumn("CfgDataToCollection", "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator cfgStatusRows = resultDO.getRows("ConfigDataStatus");
            while (cfgStatusRows.hasNext()) {
                final Row cfgStatusRow = cfgStatusRows.next();
                configDataIdVsStatus.put(cfgStatusRow.get("CONFIG_DATA_ID"), cfgStatusRow.get("CONFIG_STATUS"));
            }
        }
        catch (final Exception ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Caught exception while retrieving configuration with status from DB for collection id: " + collectionId, ex);
            throw ex;
        }
        return configDataIdVsStatus;
    }
    
    public static DataObject getCollection(final Criteria criteria) throws SyMException {
        DataObject collDO = null;
        SelectQuery query = null;
        final String baseTableName = "Collection";
        try {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable(baseTableName));
            query.addJoin(new Join(baseTableName, "CollectionVersion", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn(baseTableName, "COLLECTION_ID");
            query.setCriteria(criteria);
            collDO = SyMUtil.getPersistence().get(query);
        }
        catch (final DataAccessException ex) {
            CollectionUtil.logger.log(Level.SEVERE, "Error while retrieving collection for  given criteria: " + criteria, (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            CollectionUtil.logger.log(Level.SEVERE, "Error while retrieving collection for  given criteria: " + criteria, ex2);
            throw new SyMException(1001, ex2);
        }
        return collDO;
    }
    
    static {
        CollectionUtil.logger = Logger.getLogger(CollectionUtil.class.getName());
        TOTAL_TARGETS_COUNT = new Integer(301);
        YET_TO_APPLY_TARGETS_COUNT = new Integer(302);
        SUCCEEDED_TARGETS_COUNT = new Integer(303);
        FAILED_TARGETS_COUNT = new Integer(304);
        INPROGRESS_TARGETS_COUNT = new Integer(305);
        INPROGRESS_FAILED_TARGETS_COUNT = new Integer(306);
        RETRY_INPROGRESS_TARGETS_COUNT = new Integer(307);
        NOT_APPLICABLE_TARGETS_COUNT = new Integer(308);
        NOTIFICATION_SENT_COUNT = new Integer(309);
    }
}
