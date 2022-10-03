package com.me.idps.core.sync.db;

import java.sql.Connection;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.Properties;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.util.DirectorySyncErrorHandler;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.json.simple.JSONArray;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.idps.core.sync.synch.DirSingletonQueue;

public class DirCoreDBhandler extends DirSingletonQueue
{
    public long getPartitionFeedId(final DCQueueData qData) {
        long schemaID = 0L;
        try {
            final String schemaName = IdpsFactoryProvider.getIdpsProdEnvAPI().getSchemaName();
            schemaID = Long.valueOf(schemaName.replaceAll("[^0-9]", ""));
        }
        catch (final Exception ex) {
            schemaID = 0L;
            IDPSlogger.ERR.log(Level.FINE, "could not get schemaID", ex);
        }
        return schemaID;
    }
    
    public boolean isParallelProcessingQueue() {
        return false;
    }
    
    @Override
    protected TABLE getTablePKGen() {
        return TABLE.DIRRESRELTBL;
    }
    
    @Override
    protected Logger getLogger() {
        return IDPSlogger.DBO;
    }
    
    private Long markSyncTokenCollated(final Criteria baseCri, final Long dmDomainID) throws Exception {
        Long collationID = null;
        final Criteria criteria = baseCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "LAST_COUNT"), (Object)0, 5)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "PRE_PROCESSED_COUNT"), (Object)0, 4)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "FIRST_COUNT"), (Object)Column.getColumn("DirectorySyncDetails", "LAST_COUNT"), 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "PRE_PROCESSED_COUNT"), (Object)Column.getColumn("DirectorySyncDetails", "RECEIVED_COUNT"), 0));
        final Column collIDcol = Column.getColumn("DirectorySyncDetails", "COLLATION_ID");
        final Column minAddedAtCol = IdpsUtil.getMinOfColumn("DirectorySyncDetails", "ADDED_AT", "DIRECTORYSYNCDETAILS.ADDED_AT", -5);
        final SelectQuery succededSyncTokenQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        succededSyncTokenQuery.setCriteria(criteria);
        succededSyncTokenQuery.addSelectColumn(collIDcol);
        succededSyncTokenQuery.addSelectColumn(minAddedAtCol);
        succededSyncTokenQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(collIDcol))));
        succededSyncTokenQuery.addSortColumn(new SortColumn(minAddedAtCol, true));
        final JSONArray jsArray = IdpsUtil.executeSelectQuery(succededSyncTokenQuery);
        final List<Long> collationIDs = new ArrayList<Long>();
        if (jsArray != null) {
            IDPSlogger.SYNC.log(Level.INFO, "obtained collated Sync tokens {0}|{1}", new Object[] { String.valueOf(dmDomainID), jsArray.toString() });
            for (int i = 0; i < jsArray.size(); ++i) {
                final JSONObject jsonObject = (JSONObject)jsArray.get(i);
                final Long curCollID = Long.valueOf(String.valueOf(jsonObject.get((Object)"COLLATION_ID")));
                if (i == 0) {
                    collationID = curCollID;
                }
                collationIDs.add(curCollID);
            }
        }
        final JSONObject collationRes = new JSONObject();
        final Criteria collCri = criteria.and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationIDs.toArray(new Long[collationIDs.size()]), 8));
        if (!collationIDs.isEmpty()) {
            final DataObject dobj = SyMUtil.getPersistenceLite().get("DirectorySyncDetails", collCri);
            if (dobj != null && !dobj.isEmpty() && dobj.containsTable("DirectorySyncDetails")) {
                final Iterator itr = dobj.getRows("DirectorySyncDetails");
                while (itr != null && itr.hasNext()) {
                    final Row dirSyncTokenRow = itr.next();
                    final String curCollID2 = String.valueOf(dirSyncTokenRow.get("COLLATION_ID"));
                    final String curSyncTokenID = String.valueOf(dirSyncTokenRow.get("SYNC_TOKEN_ID"));
                    final JSONArray syncTokensAr = (JSONArray)collationRes.getOrDefault((Object)curCollID2, (Object)new JSONArray());
                    syncTokensAr.add((Object)curSyncTokenID);
                    collationRes.put((Object)curCollID2, (Object)syncTokensAr);
                }
            }
        }
        IDPSlogger.SYNC.log(Level.INFO, "collation sync token mapping {0} {1}", new Object[] { String.valueOf(dmDomainID), IdpsUtil.getPrettyJSON(collationRes) });
        if (collationID != null) {
            final String pickedCollID = String.valueOf(collationID);
            if (!collationRes.containsKey((Object)pickedCollID)) {
                throw new Exception("INTERNAL_ERROR");
            }
            IDPSlogger.SYNC.log(Level.INFO, "picked coll ID {0} {1}", new Object[] { pickedCollID, IdpsUtil.getPrettyJSON(collationRes.get((Object)pickedCollID).toString()) });
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
            updateQuery.setCriteria(baseCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationID, 0)));
            updateQuery.setUpdateColumn("STATUS_ID", (Object)931);
            DirectoryQueryutil.getInstance().executeUpdateQuery(updateQuery, false);
        }
        return collationID;
    }
    
    public void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        switch (taskType) {
            case "processData": {
                final Integer[] processableStatuses = { 921, 931 };
                final Criteria collatedCri = new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 1);
                final Criteria criteria = collatedCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)processableStatuses, 8));
                final JSONArray dirSyncSummary = DirectorySequenceAsynchImpl.getInstance().getDirectorySyncSummary(criteria);
                for (int i = 0; dirSyncSummary != null && i < dirSyncSummary.size(); ++i) {
                    final JSONObject dirSummary = (JSONObject)dirSyncSummary.get(i);
                    if (dirSyncSummary != null) {
                        final String dirDmDomainName = String.valueOf(dirSummary.get((Object)"NAME"));
                        final Long dirCustID = Long.valueOf(String.valueOf(dirSummary.get((Object)"CUSTOMER_ID")));
                        final Long dirDomainID = Long.valueOf(String.valueOf(dirSummary.get((Object)"DM_DOMAIN_ID")));
                        final Long dataRecevingCompletedAt = (Long)dirSummary.get((Object)"CURRENT_BATCH_POSTED_AT");
                        final Long tempInsertionCompletedAt = (Long)dirSummary.get((Object)"LATEST_BATCH_PROCESSED_AT");
                        final Integer lastCount = Integer.valueOf(String.valueOf(dirSummary.get((Object)"LAST_COUNT")));
                        final Integer firstCount = Integer.valueOf(String.valueOf(dirSummary.get((Object)"FIRST_COUNT")));
                        final Long minSyncTokenAddedAt = Long.valueOf(String.valueOf(dirSummary.get((Object)"ADDED_AT")));
                        final Integer syncRequestType = Integer.valueOf(String.valueOf(dirSummary.get((Object)"SYNC_TYPE")));
                        final Integer collatedSyncStaus = Integer.valueOf(String.valueOf(dirSummary.get((Object)"STATUS_ID")));
                        final Integer receivedCount = Integer.valueOf(String.valueOf(dirSummary.get((Object)"RECEIVED_COUNT")));
                        final Integer preProcessedCount = Integer.valueOf(String.valueOf(dirSummary.get((Object)"PRE_PROCESSED_COUNT")));
                        if (receivedCount > preProcessedCount || lastCount > firstCount) {
                            DirectorySyncErrorHandler.getInstance().handleError(dirDomainID, dirCustID, dirDmDomainName, dmDomainClient, new Exception("something fishy when syncing data for " + dirDmDomainName), null);
                        }
                        if (collatedSyncStaus == 921) {
                            if (receivedCount == (int)preProcessedCount && lastCount == (int)firstCount && firstCount != 0 && minSyncTokenAddedAt > 0L) {
                                final Criteria collCri = collatedCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dirDomainID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)921, 0));
                                final Long collationID = this.markSyncTokenCollated(collCri, dirDomainID);
                                if (collationID != null) {
                                    final JSONObject taskDetails = new JSONObject();
                                    taskDetails.put((Object)"NAME", (Object)dirDmDomainName);
                                    taskDetails.put((Object)"DOMAIN_ID", (Object)dirDomainID);
                                    taskDetails.put((Object)"CUSTOMER_ID", (Object)dirCustID);
                                    taskDetails.put((Object)"COLLATION_ID", (Object)collationID);
                                    taskDetails.put((Object)"SYNC_TYPE", (Object)syncRequestType);
                                    taskDetails.put((Object)"ADDED_AT", (Object)minSyncTokenAddedAt);
                                    taskDetails.put((Object)"PRE_PROCESSED_COUNT", (Object)preProcessedCount);
                                    taskDetails.put((Object)"CURRENT_BATCH_POSTED_AT", (Object)dataRecevingCompletedAt);
                                    taskDetails.put((Object)"LATEST_BATCH_PROCESSED_AT", (Object)tempInsertionCompletedAt);
                                    taskDetails.put((Object)"TASK_TYPE", (Object)"coreSyncEngine");
                                    DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", null, taskDetails);
                                    IDPSlogger.SYNC.log(Level.INFO, "core sync ops initiated for {0}", new Object[] { dirDmDomainName });
                                }
                                else {
                                    DirectorySyncErrorHandler.getInstance().handleError(dirDomainID, dirCustID, dirDmDomainName, dmDomainClient, new Exception("INTERNAL_ERROR"), null);
                                }
                            }
                            else {
                                DirectorySyncErrorHandler.getInstance().handleError(dirDomainID, dirCustID, dirDmDomainName, dmDomainClient, new Exception("INTERNAL_ERROR"), null);
                            }
                        }
                        else if (collatedSyncStaus >= 941 || firstCount == 0 || receivedCount < preProcessedCount || lastCount < firstCount || minSyncTokenAddedAt == 0L) {
                            IDPSlogger.SYNC.log(Level.INFO, "still receiving and processing data for : {0}.. collatedSyncStatus is {1}", new Object[] { dirDmDomainName, collatedSyncStaus });
                        }
                        else if (collatedSyncStaus < 921) {
                            IDPSlogger.SYNC.log(Level.INFO, "succeeded state has already been arrived at : {0}.. collatedSyncStatus is {1}", new Object[] { dirDmDomainName, collatedSyncStaus });
                        }
                        else if (collatedSyncStaus == 931) {
                            IDPSlogger.SYNC.log(Level.INFO, "sync tokens of {0} | {1} | {2} already sent for core sync engine ops", new Object[] { dirDmDomainName, String.valueOf(dirCustID), String.valueOf(dirDomainID) });
                        }
                    }
                }
                break;
            }
            case "coreSyncEngine": {
                final String collIDstr = String.valueOf(qData.get((Object)"COLLATION_ID"));
                final Long collationID2 = Long.valueOf(collIDstr);
                final Properties dmDomainProps = this.getDomainProps(dmDomainID, dmDomainName, dmDomainClient, customerID);
                final Long aaaUserID = DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUserID(dmDomainID);
                final String userName = DMDomainSyncDetailsDataHandler.getInstance().getSyncIntiatedByUsername(dmDomainID);
                final Long minSyncTokenAddedAtCol = Long.valueOf(String.valueOf(qData.get((Object)"ADDED_AT")));
                DirectoryUtil.getInstance().updateGlobalCollStatus(dmDomainID, collIDstr, true);
                Connection connection = null;
                try {
                    connection = RelationalAPI.getInstance().getConnection();
                    IDPSlogger.DBO.log(Level.INFO, "got db connection... for {0}|{1}|{2}|{3}", new Object[] { dmDomainName, String.valueOf(customerID), String.valueOf(dmDomainID), collIDstr });
                    qData.put((Object)"coreSyncEngineStartedAt", (Object)System.currentTimeMillis());
                    final int numOfPkRequired = DirectoryDataPersistor.getInstance().performCoreSyncOps(connection, collationID2, qData, true);
                    if (numOfPkRequired > 0) {
                        final long[] pkAllocation = this.allocatePKs(numOfPkRequired);
                        qData.put((Object)"PK_START", (Object)String.valueOf(pkAllocation[0]));
                        qData.put((Object)"PK_END", (Object)String.valueOf(pkAllocation[1]));
                    }
                    DirectoryDataPersistor.getInstance().performCoreSyncOps(connection, collationID2, qData, false);
                    qData.put((Object)"coreSyncEngineCompletedAt", (Object)System.currentTimeMillis());
                    qData.put((Object)"productOpsStartedAt", (Object)System.currentTimeMillis());
                    final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                    dirProdImplRequest.userName = userName;
                    dirProdImplRequest.aaaUserID = aaaUserID;
                    dirProdImplRequest.dmDomainProps = dmDomainProps;
                    dirProdImplRequest.eventType = IdpEventConstants.PRODUCT_OPS;
                    dirProdImplRequest.args = new Object[] { connection, collationID2, minSyncTokenAddedAtCol };
                    DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                    qData.put((Object)"productOpsCompletedAt", (Object)System.currentTimeMillis());
                }
                catch (final Exception ex) {
                    throw ex;
                }
                finally {
                    try {
                        if (connection != null) {
                            connection.close();
                            IDPSlogger.DBO.log(Level.INFO, "closed db connection... for {0}|{1}|{2}|{3}", new Object[] { dmDomainName, String.valueOf(customerID), String.valueOf(dmDomainID), collIDstr });
                        }
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                    }
                }
                qData.put((Object)"TASK_TYPE", (Object)"postSyncEngine");
                DirectoryUtil.getInstance().addTaskToQueue("adAsync-task", null, qData);
                break;
            }
            case "CLEAR_TOKEN": {
                qData.put((Object)"NAME", (Object)dmDomainName);
                qData.put((Object)"CUSTOMER_ID", (Object)customerID);
                DirectoryObjDeleter.getInstance().clearToken(qData);
                break;
            }
            case "DISABLE_RES_TYPE_SYNC": {
                DirectoryDataPersistor.getInstance().handleObjTypeSyncModify(dmDomainName, customerID, dmDomainID, qData);
                break;
            }
            case "END_STATE": {
                this.reEvaluateOffset();
                break;
            }
        }
    }
}
