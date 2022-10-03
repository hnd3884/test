package com.me.idps.core.util;

import java.util.Hashtable;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.JSONArray;
import com.me.idps.core.factory.TransactionExecutionImpl;
import java.util.Properties;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.factory.TransactionExecutionInterface;

public abstract class DirectorySequenceUtil implements TransactionExecutionInterface
{
    protected Boolean checkDomainFetchStatus(final Long dmDomainID, final String domainName, final Boolean alsoNotQueued) throws Exception {
        Integer syncStatus = null;
        Boolean domainSyncReady = false;
        final String syncStatusStr = String.valueOf(DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "FETCH_STATUS"));
        if (!SyMUtil.isStringEmpty(syncStatusStr)) {
            syncStatus = Integer.valueOf(syncStatusStr);
        }
        IDPSlogger.ASYNCH.log(Level.INFO, "{0} current sync status : {1}", new Object[] { domainName, DirectoryUtil.getInstance().getSyncStatusInString(syncStatus) });
        if (syncStatus != null) {
            if (syncStatus != 941) {
                if (alsoNotQueued && !syncStatus.equals(951)) {
                    domainSyncReady = true;
                }
                else if (!alsoNotQueued) {
                    domainSyncReady = true;
                }
            }
        }
        else {
            domainSyncReady = true;
        }
        if (domainSyncReady && alsoNotQueued && (syncStatus == null || (syncStatus != null && syncStatus != 951 && syncStatus != 941))) {
            final boolean stopADsync = IdpsUtil.isFeatureAvailable("STOP_AD_SYNC");
            if (stopADsync) {
                return false;
            }
            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "FETCH_STATUS", 951);
        }
        return domainSyncReady;
    }
    
    private Long createNewToken(final Long dmDomainID, final String dmDomainName, final Integer syncType, final String postSyncOPdetails) throws DataAccessException {
        final Row row = new Row("DirectorySyncDetails");
        row.set("SYNC_TYPE", (Object)syncType);
        row.set("DM_DOMAIN_ID", (Object)dmDomainID);
        if (!SyMUtil.isStringEmpty(postSyncOPdetails)) {
            row.set("POST_SYNC_DETAILS", (Object)postSyncOPdetails);
        }
        row.set("ADDED_AT", (Object)System.currentTimeMillis());
        row.set("STATUS_ID", (Object)951);
        final DataObject dObj = SyMUtil.getPersistenceLite().constructDataObject();
        dObj.addRow(row);
        SyMUtil.getPersistenceLite().add(dObj);
        final Long syncTokenID = (Long)row.get("SYNC_TOKEN_ID");
        IDPSlogger.ASYNCH.log(Level.INFO, "created new sync token:{0} for:{1},{2}", new Object[] { String.valueOf(syncTokenID), dmDomainName, String.valueOf(dmDomainID) });
        return syncTokenID;
    }
    
    public static Criteria getValidSyncTokenForDataPostingCri() {
        return new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)new Integer[] { 951, 941 }, 8);
    }
    
    private Boolean validateToken(final Long dmDomainID, final Long syncTokenID) throws DataAccessException {
        if (syncTokenID != null) {
            final DataObject dObj = SyMUtil.getPersistenceLite().get("DirectorySyncDetails", getValidSyncTokenForDataPostingCri().and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)syncTokenID, 0)));
            if (dObj != null && !dObj.isEmpty() && dObj.containsTable("DirectorySyncDetails")) {
                final Long dbSyncTokenID = (Long)dObj.getRow("DirectorySyncDetails").get("SYNC_TOKEN_ID");
                if (dbSyncTokenID != null && dbSyncTokenID.equals(syncTokenID)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Object executeTxTask(final Properties taskDetails) throws Exception {
        Object result = null;
        String taskType = null;
        if (taskDetails.containsKey("TASK_TYPE")) {
            taskType = ((Hashtable<K, String>)taskDetails).get("TASK_TYPE");
        }
        if (!SyMUtil.isStringEmpty(taskType)) {
            final String s = taskType;
            switch (s) {
                case "checkDomainSyncReady": {
                    final String domainName = ((Hashtable<K, String>)taskDetails).get("NAME");
                    final Long dmDomainID = ((Hashtable<K, Long>)taskDetails).get("DOMAIN_ID");
                    final Boolean alsoNotQueued = ((Hashtable<K, Boolean>)taskDetails).get("FETCH_STATUS");
                    result = this.checkDomainFetchStatus(dmDomainID, domainName, alsoNotQueued);
                    break;
                }
                case "createNewToken": {
                    final String dmDomainName = ((Hashtable<K, String>)taskDetails).get("NAME");
                    final Long dmDomainID = ((Hashtable<K, Long>)taskDetails).get("DM_DOMAIN_ID");
                    final String postSyncOPdetails = String.valueOf(((Hashtable<K, String>)taskDetails).getOrDefault("POST_SYNC_DETAILS", "null"));
                    final Integer syncType = Integer.valueOf(String.valueOf(((Hashtable<K, Object>)taskDetails).get("SYNC_TYPE")));
                    result = this.createNewToken(dmDomainID, dmDomainName, syncType, postSyncOPdetails);
                    break;
                }
                case "validateToken": {
                    final Long dmDomainID2 = ((Hashtable<K, Long>)taskDetails).get("DM_DOMAIN_ID");
                    final Long syncTokenID = ((Hashtable<K, Long>)taskDetails).get("SYNC_TOKEN_ID");
                    result = this.validateToken(dmDomainID2, syncTokenID);
                    break;
                }
            }
        }
        return result;
    }
    
    protected Object performDirectorySequenceTask(final String invokeClass, final Properties taskDetails) throws Exception {
        return TransactionExecutionImpl.getInstance().performTaskInTransactionMode(invokeClass, taskDetails);
    }
    
    public JSONArray getDirectorySyncSummary(Criteria criteria) throws Exception {
        final Criteria baseCri = new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 1);
        criteria = ((criteria == null) ? baseCri : criteria.and(baseCri));
        final Column dmDomainNameColumn = new Column("DMDomain", "NAME");
        dmDomainNameColumn.setColumnAlias("NAME");
        final Column dmDomainCustColumn = new Column("DMDomain", "CUSTOMER_ID");
        dmDomainCustColumn.setColumnAlias("CUSTOMER_ID");
        final Column dmDomainClientIDColumn = new Column("DMDomain", "CLIENT_ID");
        dmDomainClientIDColumn.setColumnAlias("CLIENT_ID");
        final Column dmDomainIDcolumn = new Column("DirectorySyncDetails", "DM_DOMAIN_ID");
        dmDomainIDcolumn.setColumnAlias("DM_DOMAIN_ID");
        final Column syncTypeColumn = Column.getColumn("DirectorySyncDetails", "SYNC_TYPE").minimum();
        syncTypeColumn.setColumnAlias("SYNC_TYPE");
        final Column syncStatusColumn = Column.getColumn("DirectorySyncDetails", "STATUS_ID").maximum();
        syncStatusColumn.setColumnAlias("STATUS_ID");
        final Column lastCountColumn = Column.getColumn("DirectorySyncDetails", "LAST_COUNT").summation();
        lastCountColumn.setColumnAlias("LAST_COUNT");
        final Column firstCountColumn = Column.getColumn("DirectorySyncDetails", "FIRST_COUNT").summation();
        firstCountColumn.setColumnAlias("FIRST_COUNT");
        final Column minSyncTokenAddedAtCol = Column.getColumn("DirectorySyncDetails", "ADDED_AT").minimum();
        minSyncTokenAddedAtCol.setColumnAlias("ADDED_AT");
        final Column postedCountColumn = Column.getColumn("DirectorySyncDetails", "POSTED_COUNT").summation();
        postedCountColumn.setColumnAlias("POSTED_COUNT");
        final Column receivedCountColumn = Column.getColumn("DirectorySyncDetails", "RECEIVED_COUNT").summation();
        receivedCountColumn.setColumnAlias("RECEIVED_COUNT");
        final Column preProcessedCountColumn = Column.getColumn("DirectorySyncDetails", "PRE_PROCESSED_COUNT").summation();
        preProcessedCountColumn.setColumnAlias("PRE_PROCESSED_COUNT");
        final Column currentBatchPostedAtMaxCol = Column.getColumn("DirectorySyncDetails", "CURRENT_BATCH_POSTED_AT").maximum();
        currentBatchPostedAtMaxCol.setColumnAlias("CURRENT_BATCH_POSTED_AT");
        final Column latestTempInsertionAtMaxCol = Column.getColumn("DirectorySyncDetails", "LATEST_BATCH_PROCESSED_AT").maximum();
        latestTempInsertionAtMaxCol.setColumnAlias("LATEST_BATCH_PROCESSED_AT");
        return IdpsUtil.executeSelectQuery(SyMUtil.formSelectQuery("DirectorySyncDetails", criteria, new ArrayList((Collection<? extends E>)Arrays.asList(dmDomainNameColumn, dmDomainCustColumn, dmDomainClientIDColumn, dmDomainIDcolumn, syncTypeColumn, syncStatusColumn, lastCountColumn, firstCountColumn, postedCountColumn, receivedCountColumn, preProcessedCountColumn, minSyncTokenAddedAtCol, currentBatchPostedAtMaxCol, latestTempInsertionAtMaxCol)), new ArrayList((Collection<? extends E>)Arrays.asList(dmDomainNameColumn, dmDomainCustColumn, dmDomainClientIDColumn, dmDomainIDcolumn)), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DirectorySyncDetails", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2))), (Criteria)null));
    }
    
    public JSONObject getDirectorySyncSummary(final Long dmDomainID) throws Exception {
        JSONObject domainSyncSummary = new JSONObject();
        final JSONArray jsArray = this.getDirectorySyncSummary(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0));
        if (jsArray != null && jsArray.size() > 0) {
            domainSyncSummary = (JSONObject)jsArray.get(0);
        }
        return domainSyncSummary;
    }
    
    protected DataObject getDirectorySyncDetailsDO(final Long dmDomainID, Criteria criteria) throws DataAccessException {
        final Criteria dmDomainCri = new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)new Integer[] { 951, 941 }, 8));
        if (criteria != null) {
            criteria = criteria.and(dmDomainCri);
        }
        else {
            criteria = dmDomainCri;
        }
        return SyMUtil.getPersistenceLite().get("DirectorySyncDetails", criteria);
    }
    
    protected Long getNewSyncToken(final Long dmDomainID, final String domainName, final Integer syncType, final String postSyncOPdetails, final boolean external) throws Exception {
        IDPSlogger.ASYNCH.log(Level.INFO, "new sync token being generated {0}", new Object[] { external ? "externally" : "internally" });
        final Properties taskDetails = new Properties();
        ((Hashtable<String, String>)taskDetails).put("NAME", domainName);
        ((Hashtable<String, Integer>)taskDetails).put("SYNC_TYPE", syncType);
        ((Hashtable<String, Long>)taskDetails).put("DM_DOMAIN_ID", dmDomainID);
        ((Hashtable<String, String>)taskDetails).put("TASK_TYPE", "createNewToken");
        if (postSyncOPdetails != null) {
            ((Hashtable<String, String>)taskDetails).put("POST_SYNC_DETAILS", postSyncOPdetails);
        }
        return (Long)this.performDirectorySequenceTask("com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl", taskDetails);
    }
}
