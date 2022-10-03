package com.me.idps.core.sync.synch;

import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.IdpsJSONutil;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.IdpsUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.apache.commons.lang3.mutable.MutableInt;
import java.text.MessageFormat;
import org.apache.commons.io.FileUtils;
import com.me.idps.core.util.DirectoryUtil;
import org.json.simple.JSONObject;

class DirSuccessHandler
{
    private static DirSuccessHandler dirSuccessHandler;
    
    static DirSuccessHandler getInstance() {
        if (DirSuccessHandler.dirSuccessHandler == null) {
            DirSuccessHandler.dirSuccessHandler = new DirSuccessHandler();
        }
        return DirSuccessHandler.dirSuccessHandler;
    }
    
    private JSONObject fillJSONObject(final JSONObject jsonObject, final String key, final int val) {
        if (val > 0) {
            jsonObject.put((Object)key, (Object)val);
        }
        return jsonObject;
    }
    
    private JSONObject getBlockOpsMetrics(final Long dmDomainID, final Long collationID, final String block) {
        int updatedRows = DirectoryUtil.getInstance().getCurrentDBOpsMetric(dmDomainID, collationID, block, 2, true);
        int deletedRows = DirectoryUtil.getInstance().getCurrentDBOpsMetric(dmDomainID, collationID, block, 3, true);
        int insertedRows = DirectoryUtil.getInstance().getCurrentDBOpsMetric(dmDomainID, collationID, block, 1, true);
        if (block.equalsIgnoreCase("coreSyncEngine")) {
            final JSONObject resOps = this.getBlockOpsMetrics(dmDomainID, collationID, "postSyncEngine");
            updatedRows += (int)resOps.getOrDefault((Object)"u", (Object)0);
            deletedRows += (int)resOps.getOrDefault((Object)"d", (Object)0);
            insertedRows += (int)resOps.getOrDefault((Object)"i", (Object)0);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject = this.fillJSONObject(jsonObject, "u", updatedRows);
        jsonObject = this.fillJSONObject(jsonObject, "d", deletedRows);
        jsonObject = this.fillJSONObject(jsonObject, "i", insertedRows);
        return jsonObject;
    }
    
    private String getFileMetricLine(final int fileNum, final String fileOptype, final int fileSize, final int totalTimeTaken) {
        final String template = "{0} files {1}, size {2}, total time {3}";
        final String totalFileSize = FileUtils.byteCountToDisplaySize((long)Long.valueOf(String.valueOf(fileSize)));
        return MessageFormat.format(template, String.valueOf(fileNum), fileOptype, totalFileSize, DirectoryUtil.getInstance().formatDurationMS(totalTimeTaken));
    }
    
    private String getFileOpsMetrics(final Long dmDomainID, final Long collationID, final MutableInt totalFileIOtimeTaken) {
        final int writeNum = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_WRITE_NUM", true);
        final int totalWriteSize = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_WRITE_SIZE", true);
        final int totalWriteTime = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_WRITE_TIME_TAKEN", true);
        final int readNum = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_READ_NUM", true);
        final int totalReadSize = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_READ_SIZE", true);
        final int totalReadTime = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_READ_TIME_TAKEN", true);
        final int delNum = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_DELETE_NUM", true);
        final int totalDelSize = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_DELETE_SIZE", true);
        final int totalDelTime = DirectoryUtil.getInstance().extractValFromCache(dmDomainID, collationID, "FILE_DELETE_TIME_TAKEN", true);
        totalFileIOtimeTaken.setValue(totalWriteTime + totalReadTime + totalDelTime);
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getFileMetricLine(writeNum, "written", totalWriteSize, totalWriteTime));
        sb.append(System.lineSeparator());
        sb.append(this.getFileMetricLine(readNum, "read", totalReadSize, totalReadTime));
        sb.append(System.lineSeparator());
        sb.append(this.getFileMetricLine(delNum, "deleted", totalDelSize, totalDelTime));
        return sb.toString();
    }
    
    public JSONObject logSyncCompletion(final Long customerID, final Long dmDomainID, final Long collationID, final String dmDomainName, final int syncType, final int totalPostedCount, final Long syncRequestedAt, final Long minSyncTokenAddedAt, final Long dataReceviedCompletedAt, final Long tempInsertionCompletedAt, final Long coreSyncEngineOpsStartedAt, final Long coreSyncEngineOpsCompletedAt, final Long productOpsStartedAt, final Long productOpsCompletedAt) {
        final MutableInt totalFileIOtimeTaken = new MutableInt();
        final JSONObject tempOps = this.getBlockOpsMetrics(dmDomainID, collationID, "DirectoryTempDataHandler");
        final JSONObject coreOps = this.getBlockOpsMetrics(dmDomainID, collationID, "coreSyncEngine");
        final JSONObject prodOps = this.getBlockOpsMetrics(dmDomainID, collationID, "MDMDirectoryDataPersistor");
        final JSONObject eventOps = this.getBlockOpsMetrics(dmDomainID, collationID, "DirectoryEventsUtil");
        IDPSlogger.AUDIT.log(Level.INFO, System.lineSeparator() + "{0} : {1} report:" + System.lineSeparator() + "{2} objects received ," + System.lineSeparator() + "sync requested at:{3}," + System.lineSeparator() + "sync request acknowledged at:{4} duration;{5}," + System.lineSeparator() + "{6}" + System.lineSeparator() + "data receiving completed at:{7} duration:{8}" + System.lineSeparator() + "File IO metrics " + System.lineSeparator() + "{9}" + System.lineSeparator() + "data temp insertion completed at:{10} additional insertion duration:{11}" + System.lineSeparator() + "core sync engine ops started at:{12} duration:{13}" + System.lineSeparator() + "core ops details {14}" + System.lineSeparator() + "core sync engine ops completed at:{15} duration:{16}" + System.lineSeparator() + "product ops started at:{17} duration:{18}" + System.lineSeparator() + "product ops details : {19}" + System.lineSeparator() + "product ops completed at:{20} duration:{21}" + System.lineSeparator() + "event trigger details : {22}" + System.lineSeparator() + "total duration:{23}", new Object[] { dmDomainName, DirectoryUtil.getInstance().getSyncTypeValueInString(syncType), String.valueOf(totalPostedCount), DirectoryUtil.getInstance().longdateToString(syncRequestedAt), DirectoryUtil.getInstance().longdateToString(minSyncTokenAddedAt), DirectoryUtil.getInstance().formatDurationMS(minSyncTokenAddedAt - syncRequestedAt), IdpsUtil.getPrettyJSON(tempOps), DirectoryUtil.getInstance().longdateToString(dataReceviedCompletedAt), DirectoryUtil.getInstance().formatDurationMS(dataReceviedCompletedAt - minSyncTokenAddedAt), this.getFileOpsMetrics(dmDomainID, collationID, totalFileIOtimeTaken), DirectoryUtil.getInstance().longdateToString(tempInsertionCompletedAt), DirectoryUtil.getInstance().formatDurationMS(Math.max(0L, tempInsertionCompletedAt - dataReceviedCompletedAt - Long.valueOf(String.valueOf(totalFileIOtimeTaken.getValue())))), DirectoryUtil.getInstance().longdateToString(coreSyncEngineOpsStartedAt), DirectoryUtil.getInstance().formatDurationMS(coreSyncEngineOpsStartedAt - tempInsertionCompletedAt), IdpsUtil.getPrettyJSON(coreOps), DirectoryUtil.getInstance().longdateToString(coreSyncEngineOpsCompletedAt), DirectoryUtil.getInstance().formatDurationMS(coreSyncEngineOpsCompletedAt - coreSyncEngineOpsStartedAt), DirectoryUtil.getInstance().longdateToString(productOpsStartedAt), DirectoryUtil.getInstance().formatDurationMS(productOpsStartedAt - coreSyncEngineOpsCompletedAt), IdpsUtil.getPrettyJSON(prodOps), DirectoryUtil.getInstance().longdateToString(productOpsCompletedAt), DirectoryUtil.getInstance().formatDurationMS(productOpsCompletedAt - productOpsStartedAt), IdpsUtil.getPrettyJSON(eventOps), DirectoryUtil.getInstance().formatDurationMS(productOpsCompletedAt - syncRequestedAt) });
        final int duration = Integer.valueOf(String.valueOf(productOpsCompletedAt - productOpsStartedAt + coreSyncEngineOpsCompletedAt - coreSyncEngineOpsStartedAt));
        DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, "SYNC_ENGINE_OPS_DURATION", duration);
        tempOps.put((Object)"T", (Object)DirectoryUtil.getInstance().formatDurationMS(tempInsertionCompletedAt - minSyncTokenAddedAt));
        coreOps.put((Object)"T", (Object)DirectoryUtil.getInstance().formatDurationMS(coreSyncEngineOpsCompletedAt - coreSyncEngineOpsStartedAt));
        prodOps.put((Object)"T", (Object)DirectoryUtil.getInstance().formatDurationMS(productOpsCompletedAt - productOpsStartedAt));
        final JSONObject opsDetails = new JSONObject();
        opsDetails.put((Object)"tempOps", (Object)tempOps);
        opsDetails.put((Object)"coreOps", (Object)coreOps);
        opsDetails.put((Object)"prodOps", (Object)prodOps);
        opsDetails.put((Object)"eventOps", (Object)eventOps);
        opsDetails.put((Object)"SYNC_TYPE", (Object)DirectoryUtil.getInstance().getSyncTypeValueInString(syncType));
        return opsDetails;
    }
    
    private void markSyncSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final Long syncRequestedAt) throws Exception {
        final Criteria cri = new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 5));
        final int liveSynctokenCountForDomain = DBUtil.getRecordCount("DirectorySyncDetails", "SYNC_TOKEN_ID", cri);
        if (liveSynctokenCountForDomain == 0) {
            final HashMap<String, Object> successMap = new HashMap<String, Object>();
            successMap.put("SYNC_STATUS", "");
            successMap.put("LAST_SUCCESSFUL_SYNC", syncRequestedAt);
            successMap.put("FETCH_STATUS", 921);
            successMap.put("REMARKS", "");
            DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, successMap);
            IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClient).handleSuccess(dmDomainName, customerID, dmDomainID);
        }
    }
    
    JSONObject handleSuccess(final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        final Long collationID = Long.valueOf(String.valueOf(qData.get((Object)"COLLATION_ID")));
        final Long minSyncTokenAddedAt = Long.valueOf(String.valueOf(qData.get((Object)"ADDED_AT")));
        final Integer preProcessedCount = Integer.valueOf(String.valueOf(qData.get((Object)"PRE_PROCESSED_COUNT")));
        final Integer syncRequestType = Integer.valueOf(String.valueOf(qData.get((Object)"SYNC_TYPE")));
        final Long coreSyncEngineOpsStartedAt = Long.valueOf(String.valueOf(qData.get((Object)"coreSyncEngineStartedAt")));
        final Long coreSyncEngineOpsCompletedAt = Long.valueOf(String.valueOf(qData.get((Object)"coreSyncEngineCompletedAt")));
        final Long productOpsStartedAt = Long.valueOf(String.valueOf(qData.get((Object)"productOpsStartedAt")));
        final Long productOpsCompletedAt = Long.valueOf(String.valueOf(qData.get((Object)"productOpsCompletedAt")));
        final Long dataReceivingCompletedAt = Long.valueOf(String.valueOf(qData.get((Object)"CURRENT_BATCH_POSTED_AT")));
        final Long tempInsertionCompletedAt = Long.valueOf(String.valueOf(qData.get((Object)"LATEST_BATCH_PROCESSED_AT")));
        final Long syncRequestedAt = (Long)DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_SYNC_INITIATED");
        final JSONObject opsDetails = this.logSyncCompletion(customerID, dmDomainID, collationID, dmDomainName, syncRequestType, preProcessedCount, syncRequestedAt, minSyncTokenAddedAt, dataReceivingCompletedAt, tempInsertionCompletedAt, coreSyncEngineOpsStartedAt, coreSyncEngineOpsCompletedAt, productOpsStartedAt, productOpsCompletedAt);
        final Column statusIDcol = Column.getColumn("DirectorySyncDetails", "STATUS_ID");
        final Column domainIDcol = Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID");
        final Column syncTokenCountcol = IdpsUtil.getCountOfColumn("DirectorySyncDetails", "SYNC_TOKEN_ID", "count");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 5)));
        selectQuery.addSelectColumns((List)new ArrayList(Arrays.asList(statusIDcol, domainIDcol, syncTokenCountcol)));
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(statusIDcol, domainIDcol)), new Criteria(syncTokenCountcol, (Object)0, 5)));
        selectQuery.addSortColumn(new SortColumn(statusIDcol, false));
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(selectQuery);
        boolean markSuccess = false;
        if (jsonArray != null && jsonArray.size() > 0) {
            IDPSlogger.AUDIT.log(Level.INFO, "sync token distribution for {0} : {1}", new Object[] { dmDomainName, IdpsUtil.getPrettyJSON(jsonArray) });
        }
        else {
            markSuccess = true;
        }
        if (markSuccess) {
            this.markSyncSuccess(dmDomainName, customerID, dmDomainID, dmDomainClient, syncRequestedAt);
            final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
            dirProdImplRequest.eventType = IdpEventConstants.CUSTOM_OPS;
            dirProdImplRequest.args = new Object[] { "DO_CG_CLEAN_UP", customerID, dmDomainName, dmDomainID };
            DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
        }
        qData.put((Object)"DirectoryMetrics", (Object)opsDetails);
        qData.put((Object)"SOURCE", (Object)"DirSuccessHandler");
        qData.put((Object)"ALL_DONE", (Object)Boolean.TRUE);
        qData.put((Object)"COLLATION_ID", (Object)IdpsJSONutil.convertListToJSONArray(DirectorySequnceSynchImpl.getInstance().getSyncTokens(dmDomainID, 911)));
        return qData;
    }
    
    static {
        DirSuccessHandler.dirSuccessHandler = null;
    }
}
