package com.me.idps.core.sync.synch;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Properties;
import com.me.idps.core.util.IdpsUtil;
import com.me.idps.core.util.IdpsJSONutil;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.concurrent.TimeUnit;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.idps.core.util.DirectoryUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;

class DirectoryCollationHandler
{
    private static DirectoryCollationHandler directoryCollationHandler;
    
    static DirectoryCollationHandler getInstance() {
        if (DirectoryCollationHandler.directoryCollationHandler == null) {
            DirectoryCollationHandler.directoryCollationHandler = new DirectoryCollationHandler();
        }
        return DirectoryCollationHandler.directoryCollationHandler;
    }
    
    private Long addNewCollateRequest(final Long dmDomainID, final String dmDomainName, final Long scheduleTime) throws DataAccessException {
        final Row row = new Row("DirectoryCollateRequest");
        row.set("SCHEDULED_AT", (Object)scheduleTime);
        row.set("ADDED_AT", (Object)System.currentTimeMillis());
        row.set("DM_DOMAIN_ID", (Object)dmDomainID);
        final DataObject dobj = (DataObject)new WritableDataObject();
        dobj.addRow(row);
        SyMUtil.getPersistenceLite().add(dobj);
        final Long collateRequestID = (Long)dobj.getRow("DirectoryCollateRequest").get("COLLATE_REQUEST_ID");
        IDPSlogger.SYNC.log(Level.INFO, "new collate Request added:{0}, for domain:{1}, scheduled at:{2}, added at:{3}", new Object[] { String.valueOf(collateRequestID), dmDomainName, DirectoryUtil.getInstance().longdateToString(scheduleTime), DirectoryUtil.getInstance().longdateToString((Long)row.get("ADDED_AT")) });
        return collateRequestID;
    }
    
    int getPendingScheduledRequests(final Long dmDomainID, final String dmDomainName, final int collateWaitTime) throws Exception {
        final Long timeCriVal = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(45 + collateWaitTime);
        final Criteria criteria = new Criteria(Column.getColumn("DirectoryCollateRequest", "ADDED_AT"), (Object)timeCriVal, 5).and(new Criteria(Column.getColumn("DirectoryCollateRequest", "DM_DOMAIN_ID"), (Object)dmDomainID, 0));
        final int pendingAsyncRequestCount = DBUtil.getRecordCount("DirectoryCollateRequest", "COLLATE_REQUEST_ID", criteria);
        IDPSlogger.SYNC.log(Level.INFO, "number of pending Async request count for " + dmDomainName + " are : " + String.valueOf(pendingAsyncRequestCount));
        return pendingAsyncRequestCount;
    }
    
    void scheduleCollateTask(final Long customerID, final Long dmDomainID, final String dmDomainName, final Integer dmDomainClientID, final Long scheduleTime) throws Exception {
        final String taskName = "collate";
        final Long collateRequestID = this.addNewCollateRequest(dmDomainID, dmDomainName, scheduleTime);
        final JSONObject taskDetails = DirectoryUtil.getInstance().getNewTaskDetails(dmDomainName, dmDomainClientID, dmDomainID, customerID);
        taskDetails.put((Object)"TASK_TYPE", (Object)taskName);
        taskDetails.put((Object)"COLLATE_REQUEST_ID", (Object)collateRequestID);
        DirectoryUtil.getInstance().executeAsynchronousWithDelay(taskName, scheduleTime, taskDetails);
    }
    
    void collate(final JSONObject taskDetails, final Long dmDomainID, final Long collateRequestID) throws Exception {
        this.deleteCollateRequest(collateRequestID);
        long maxSyncTokenID = 0L;
        final Criteria criteria = new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "FIRST_COUNT"), (Object)0, 5)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)941, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "PRE_PROCESSED_COUNT"), (Object)0, 4)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "FIRST_COUNT"), (Object)Column.getColumn("DirectorySyncDetails", "LAST_COUNT"), 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "PRE_PROCESSED_COUNT"), (Object)Column.getColumn("DirectorySyncDetails", "RECEIVED_COUNT"), 0));
        final SelectQuery toBeCollatedSyncTokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        toBeCollatedSyncTokenQuery.setCriteria(criteria);
        toBeCollatedSyncTokenQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"));
        final DataObject dobj = SyMUtil.getPersistenceLite().get(toBeCollatedSyncTokenQuery);
        final Iterator itr = dobj.getRows("DirectorySyncDetails");
        final List<Long> syncTokenList = DBUtil.getColumnValuesAsList(itr, "SYNC_TOKEN_ID");
        final Long[] array;
        final Long[] succeededSyncTokens = array = syncTokenList.toArray(new Long[syncTokenList.size()]);
        for (final Long curSyncTokenID : array) {
            maxSyncTokenID = Math.max(curSyncTokenID, maxSyncTokenID);
        }
        final Criteria cri = new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)succeededSyncTokens, 8);
        final UpdateQuery collateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
        collateQuery.setCriteria(cri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 0)));
        collateQuery.setUpdateColumn("COLLATION_ID", (Object)maxSyncTokenID);
        collateQuery.setUpdateColumn("STATUS_ID", (Object)921);
        DirectoryQueryutil.getInstance().executeUpdateQuery(collateQuery, false);
        int totalFileDel = 0;
        int totalDelSize = 0;
        int totalDelTimeTaken = 0;
        int totalFileRead = 0;
        int totalReadSize = 0;
        int totalReadTimeTaken = 0;
        int totalWritSize = 0;
        int totalFileWritten = 0;
        int totalWriteTimeTaken = 0;
        for (final Long syncTokenID : succeededSyncTokens) {
            final int insertedRows = DirectoryUtil.getInstance().getCurrentDBOpsMetric(dmDomainID, syncTokenID, "DirectoryTempDataHandler", 1, true);
            DirectoryQueryutil.getInstance().incrementDbOpsMetric(dmDomainID, maxSyncTokenID, "DirectoryTempDataHandler", 1, insertedRows);
            totalFileDel += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_NUM", true);
            totalDelSize += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_SIZE", true);
            totalDelTimeTaken += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_TIME_TAKEN", true);
            totalWritSize += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_SIZE", true);
            totalFileWritten += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_NUM", true);
            totalWriteTimeTaken += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_TIME_TAKEN", true);
            totalFileRead += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_NUM", true);
            totalReadSize += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_SIZE", true);
            totalReadTimeTaken += DirectoryUtil.getInstance().extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_TIME_TAKEN", true);
        }
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_DELETE_NUM", totalFileDel);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_DELETE_SIZE", totalDelSize);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_DELETE_TIME_TAKEN", totalDelTimeTaken);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_WRITE_SIZE", totalWritSize);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_WRITE_NUM", totalFileWritten);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_WRITE_TIME_TAKEN", totalWriteTimeTaken);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_READ_NUM", totalFileRead);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_READ_SIZE", totalReadSize);
        DirectoryUtil.getInstance().setFileIOstats(dmDomainID, maxSyncTokenID, "FILE_READ_TIME_TAKEN", totalReadTimeTaken);
        final JSONObject collationDetails = new JSONObject();
        collationDetails.put((Object)"DOMAIN_ID", (Object)dmDomainID);
        collationDetails.put((Object)"COLLATION_ID", (Object)maxSyncTokenID);
        collationDetails.put((Object)"SYNC_TOKEN_ID", (Object)IdpsJSONutil.convertListToJSONArray(succeededSyncTokens));
        IDPSlogger.SYNC.log(Level.INFO, "collation details {0}", new Object[] { IdpsUtil.getPrettyJSON(collationDetails) });
        if (maxSyncTokenID > 0L) {
            taskDetails.put((Object)"TASK_TYPE", (Object)"processData");
            DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", null, taskDetails);
        }
    }
    
    private void deleteCollateRequest(final Long collateRequestID) throws Exception {
        if (collateRequestID != null) {
            final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("DirectoryCollateRequest");
            delQuery.setCriteria(new Criteria(Column.getColumn("DirectoryCollateRequest", "COLLATE_REQUEST_ID"), (Object)collateRequestID, 0));
            DirectoryQueryutil.getInstance().executeDeleteQuery(delQuery, false);
        }
    }
    
    static {
        DirectoryCollationHandler.directoryCollationHandler = null;
    }
}
