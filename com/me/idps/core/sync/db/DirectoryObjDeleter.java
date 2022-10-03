package com.me.idps.core.sync.db;

import com.adventnet.ds.query.DeleteQuery;
import java.util.Properties;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.IdpsUtil;
import java.util.ArrayList;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.util.IdpsJSONutil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.events.DirectoryEventsUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;

class DirectoryObjDeleter
{
    private static DirectoryObjDeleter directoryObjDeleter;
    
    static DirectoryObjDeleter getInstance() {
        if (DirectoryObjDeleter.directoryObjDeleter == null) {
            DirectoryObjDeleter.directoryObjDeleter = new DirectoryObjDeleter();
        }
        return DirectoryObjDeleter.directoryObjDeleter;
    }
    
    private void detectDeletedObjects(final Connection connection, Criteria criteria, final Long dmDomainID, final Long collationID, final Long modifiedTimeStamp) throws Exception {
        final Join dirResRelJoin = new Join("DirObjRegIntVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2);
        final Integer[] notActiveUsers = { 2, 4 };
        criteria = criteria.and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)notActiveUsers, 9, false)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0));
        final Column resIDcol = Column.getColumn("DirResRel", "RESOURCE_ID");
        final CaseExpression valCol = new CaseExpression("VAL_COL");
        valCol.addWhen(criteria, (Object)5);
        final Column eventTimeStampValCol = Column.getColumn("DirResRel", "ADDED_AT");
        final SelectQuery eventInsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegIntVal"));
        eventInsQuery.addJoin(dirResRelJoin);
        eventInsQuery.setCriteria(criteria.and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 1)));
        final Long dirEventID = DirectoryEventsUtil.getInstance().populateEvents(connection, dmDomainID, collationID, IdpEventConstants.STATUS_CHANGE_EVENT, eventInsQuery, resIDcol, eventTimeStampValCol, (Column)valCol);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirObjRegIntVal");
        updateQuery.addJoin(dirResRelJoin);
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn("MODIFIED_AT", (Object)modifiedTimeStamp);
        updateQuery.setUpdateColumn("VALUE", (Object)5);
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, "postSyncEngine", null, false);
        DirectoryEventsUtil.getInstance().markEventSucceeded(connection, dmDomainID, dirEventID);
    }
    
    void detectDeleted(final Connection connection, final JSONObject qNode, final Long collationID, final Criteria criteria) throws Exception {
        final Long dmDomainID = Long.valueOf(String.valueOf(qNode.get((Object)"DOMAIN_ID")));
        Criteria cri = new Criteria(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        cri = ((criteria != null) ? cri.and(criteria) : cri);
        final Long modifiedTimeStamp = System.currentTimeMillis();
        this.detectDeletedObjects(connection, cri, dmDomainID, collationID, modifiedTimeStamp);
    }
    
    private Criteria getCollationDelCri(final String tableName, final String collationColName, final boolean singleCollation, final Long[] collTokensAr) {
        return new Criteria(Column.getColumn(tableName, collationColName), singleCollation ? collTokensAr[0] : collTokensAr, singleCollation ? 0 : 8);
    }
    
    void clearToken(final JSONObject taskDetails) throws Exception {
        Criteria dirSyncDetailsCri = new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 0);
        if (taskDetails.containsKey((Object)"COLLATION_ID")) {
            final JSONArray collatedSyncTokens = (JSONArray)taskDetails.get((Object)"COLLATION_ID");
            if (collatedSyncTokens != null && !collatedSyncTokens.isEmpty()) {
                final Long[] syncTokens = IdpsJSONutil.convertJSONArrayToLongArray(collatedSyncTokens);
                dirSyncDetailsCri = dirSyncDetailsCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)syncTokens, 8));
            }
        }
        if (taskDetails.containsKey((Object)"DM_DOMAIN_ID")) {
            final Long dmDomainID = (Long)taskDetails.get((Object)"DM_DOMAIN_ID");
            dirSyncDetailsCri = dirSyncDetailsCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0));
        }
        if (taskDetails.containsKey((Object)"DOMAIN_ID")) {
            final Long dmDomainID = (Long)taskDetails.get((Object)"DOMAIN_ID");
            dirSyncDetailsCri = dirSyncDetailsCri.and(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0));
        }
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            final List<Long> syncTokenIDs = new ArrayList<Long>();
            final List<Long> collationTokenIDs = new ArrayList<Long>();
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
            selectQuery.setCriteria(dirSyncDetailsCri);
            selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"));
            final JSONArray toBeRevokedCollDetails = IdpsUtil.executeSelectQuery(connection, selectQuery);
            IDPSlogger.AUDIT.log(Level.INFO, "clearing sync tokens and their temp data : {0}", new Object[] { IdpsUtil.getPrettyJSON(toBeRevokedCollDetails) });
            if (toBeRevokedCollDetails != null && !toBeRevokedCollDetails.isEmpty()) {
                for (int i = 0; i < toBeRevokedCollDetails.size(); ++i) {
                    final JSONObject collIDdetails = (JSONObject)toBeRevokedCollDetails.get(i);
                    if (collIDdetails.containsKey((Object)"SYNC_TOKEN_ID") && collIDdetails.containsKey((Object)"DM_DOMAIN_ID")) {
                        final Long dmDomainID2 = Long.valueOf(String.valueOf(collIDdetails.get((Object)"DM_DOMAIN_ID")));
                        final Long syncTokenID = Long.valueOf(String.valueOf(collIDdetails.get((Object)"SYNC_TOKEN_ID")));
                        final String collIDstr = String.valueOf(collIDdetails.get((Object)"COLLATION_ID"));
                        Long collationID = null;
                        if (!IdpsUtil.isStringEmpty(collIDstr)) {
                            collationID = Long.valueOf(collIDstr);
                        }
                        if (syncTokenID != null && !syncTokenIDs.contains(syncTokenID)) {
                            syncTokenIDs.add(syncTokenID);
                        }
                        if (collationID != null && !collationTokenIDs.contains(collationID)) {
                            collationTokenIDs.add(collationID);
                        }
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_WRITE_NUM"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_WRITE_SIZE"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_WRITE_TIME_TAKEN"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_READ_NUM"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_READ_SIZE"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_READ_TIME_TAKEN"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_DELETE_NUM"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_DELETE_SIZE"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache(DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "FILE_DELETE_TIME_TAKEN"), 2);
                        ApiFactoryProvider.getCacheAccessAPI().removeCache("DirectorySyncDetails_SOURCE_" + dmDomainID2 + "_" + syncTokenID, 2);
                        final String[] blocks = { "DirectoryTempDataHandler", "coreSyncEngine", "MDMDirectoryDataPersistor", "DirectoryEventsUtil" };
                        for (int j = 0; j < blocks.length; ++j) {
                            final String block = blocks[j];
                            for (int opType = 1; opType < 4; ++opType) {
                                final String key = DirectoryUtil.getInstance().getKey(dmDomainID2, syncTokenID, "DirectorySyncDetails_" + block + "_" + opType);
                                ApiFactoryProvider.getCacheAccessAPI().removeCache(key, 2);
                            }
                        }
                    }
                }
            }
            final Long[] syncTokensAr = syncTokenIDs.toArray(new Long[syncTokenIDs.size()]);
            final Long[] collTokensAr = collationTokenIDs.toArray(new Long[collationTokenIDs.size()]);
            final boolean singleToken = syncTokensAr.length == 1;
            final boolean singleCollation = collTokensAr.length == 1;
            final Criteria syncTokenDelCri = new Criteria(Column.getColumn("DirObjTmp", "SYNC_TOKEN_ID"), singleToken ? syncTokensAr[0] : syncTokensAr, singleToken ? 0 : 8);
            if (syncTokensAr.length > 0) {
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmpDuplAttr");
                deleteQuery.addJoin(new Join("DirObjTmpDuplAttr", "DirObjTmp", new String[] { "DUPLICATED_MAX_TEMP_ID" }, new String[] { "TEMP_ID" }, 2));
                deleteQuery.setCriteria(syncTokenDelCri);
                DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            }
            if (syncTokensAr.length > 0) {
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmpDuplVal");
                deleteQuery.addJoin(new Join("DirObjTmpDuplVal", "DirObjTmp", new String[] { "DUPLICATED_MAX_TEMP_ID" }, new String[] { "TEMP_ID" }, 2));
                deleteQuery.setCriteria(syncTokenDelCri);
                DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            }
            final List<List<String>> tmpBasedTables = new ArrayList<List<String>>(Arrays.asList(new ArrayList((Collection<? extends E>)Arrays.asList("DirObjTmpRegIntVal", "COLLATION_ID", "TEMP_ID")), new ArrayList((Collection<? extends E>)Arrays.asList("DirObjTmpRegStrVal", "COLLATION_ID", "TEMP_ID")), new ArrayList((Collection<? extends E>)Arrays.asList("DirObjTmpArrStrVal", "COLLATION_ID", "TEMP_ID")), new ArrayList((Collection<? extends E>)Arrays.asList("DirObjTmpDupl", "COLLATION_ID", "DUPLICATED_MAX_TEMP_ID")), new ArrayList((Collection<? extends E>)Arrays.asList("DirObjTmp", "COLLATION_ID", null))));
            for (int k = 0; k < tmpBasedTables.size(); ++k) {
                final List<String> tmpTableDetails = tmpBasedTables.get(k);
                final String tableName = tmpTableDetails.get(0);
                final String collIDcolName = tmpTableDetails.get(1);
                final String tempIdColName = tmpTableDetails.get(2);
                if (collTokensAr.length > 0) {
                    final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
                    deleteQuery.setCriteria(this.getCollationDelCri(tableName, collIDcolName, singleCollation, collTokensAr));
                    DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
                }
                if (syncTokensAr.length > 0 && !IdpsUtil.isStringEmpty(tempIdColName)) {
                    final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
                    deleteQuery.addJoin(new Join(tableName, "DirObjTmp", new String[] { tempIdColName }, new String[] { "TEMP_ID" }, 2));
                    deleteQuery.setCriteria(syncTokenDelCri);
                    DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
                }
            }
            final Column postSyncOpCol = Column.getColumn("DirectorySyncDetails", "POST_SYNC_DETAILS");
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
            Criteria postSyncOpCri = new Criteria(postSyncOpCol, (Object)null, 1);
            postSyncOpCri = ((dirSyncDetailsCri != null) ? dirSyncDetailsCri.and(postSyncOpCri) : postSyncOpCri);
            selectQuery.setCriteria(postSyncOpCri);
            selectQuery.addSelectColumn(postSyncOpCol);
            final JSONArray postSyncOpDetails = IdpsUtil.executeSelectQuery(connection, selectQuery);
            if (postSyncOpDetails != null && !postSyncOpDetails.isEmpty()) {
                for (int l = 0; l < postSyncOpDetails.size(); ++l) {
                    final JSONObject postSyncOpDetail = (JSONObject)postSyncOpDetails.get(l);
                    final String postSyncOpDetailFilePath = (String)postSyncOpDetail.get((Object)"POST_SYNC_DETAILS");
                    DirectoryUtil.getInstance().deleteFile(postSyncOpDetailFilePath);
                }
            }
            DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirObjTmp");
            deleteQuery.setCriteria(syncTokenDelCri);
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            if (syncTokensAr.length > 0) {
                deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirTmpAvailableRes");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("DirTmpAvailableRes", "COLLATION_ID"), singleToken ? syncTokensAr[0] : syncTokensAr, singleToken ? 0 : 8));
                DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
                deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirectorySyncAsynchPostDetails");
                deleteQuery.setCriteria(new Criteria(Column.getColumn("DirectorySyncAsynchPostDetails", "SYNC_TOKEN_ID"), singleToken ? syncTokensAr[0] : syncTokensAr, singleToken ? 0 : 8));
                DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
            }
            deleteQuery = (DeleteQuery)new DeleteQueryImpl("DirectorySyncDetails");
            if (dirSyncDetailsCri != null) {
                deleteQuery.setCriteria(dirSyncDetailsCri);
            }
            DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        if (taskDetails.containsKey((Object)"ALL_DONE")) {
            final boolean allDoneVal = Boolean.valueOf(String.valueOf(taskDetails.get((Object)"ALL_DONE")));
            if (allDoneVal) {
                taskDetails.put((Object)"TASK_TYPE", (Object)"ALL_DONE");
                DirectoryUtil.getInstance().addTaskToQueue("adProc-task", null, taskDetails);
            }
        }
    }
    
    static {
        DirectoryObjDeleter.directoryObjDeleter = null;
    }
}
