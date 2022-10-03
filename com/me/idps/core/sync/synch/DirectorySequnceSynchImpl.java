package com.me.idps.core.sync.synch;

import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import com.me.idps.core.util.IdpsJSONutil;
import com.me.idps.core.crud.DMDomainDataHandler;
import java.util.concurrent.TimeUnit;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.util.DirectorySyncErrorHandler;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.idps.core.util.DirectoryUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONObject;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.util.DirectorySequenceUtil;

class DirectorySequnceSynchImpl extends DirectorySequenceUtil
{
    private static DirectorySequnceSynchImpl directorySequnceSynchImpl;
    
    static DirectorySequnceSynchImpl getInstance() {
        if (DirectorySequnceSynchImpl.directorySequnceSynchImpl == null) {
            DirectorySequnceSynchImpl.directorySequnceSynchImpl = new DirectorySequnceSynchImpl();
        }
        return DirectorySequnceSynchImpl.directorySequnceSynchImpl;
    }
    
    private void setSyncTokenValue(final RelationalAPI relApi, final Connection connection, final Criteria criteria, final String columnName, final Object newVal) throws QueryConstructionException, SQLException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
        updateQuery.setCriteria(criteria);
        updateQuery.setUpdateColumn(columnName, newVal);
        relApi.execute(connection, relApi.getUpdateSQL(updateQuery));
    }
    
    void setValue(final Long dmDomainID, final Long syncTokenID, final JSONObject jsonObject) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)syncTokenID, 0);
        final DataObject dataObject = this.getDirectorySyncDetailsDO(dmDomainID, criteria);
        if (dataObject == null || dataObject.isEmpty() || !dataObject.containsTable("DirectorySyncDetails") || dataObject.size("DirectorySyncDetails") <= 0) {
            IDPSlogger.SYNC.log(Level.SEVERE, "oh boy...");
        }
        final Row row = dataObject.getRow("DirectorySyncDetails");
        final List<String> columnNames = row.getColumns();
        Connection connection = null;
        final RelationalAPI relApi = RelationalAPI.getInstance();
        try {
            connection = relApi.getConnection();
            for (int i = 0; i < columnNames.size(); ++i) {
                final String columnName = columnNames.get(i);
                if (jsonObject.containsKey((Object)columnName)) {
                    final String columnType = row.getColumnType(columnName);
                    final String value = String.valueOf(jsonObject.get((Object)columnName));
                    if (columnName.contains("_COUNT") && columnType.equalsIgnoreCase("INTEGER")) {
                        Integer existingValue = (Integer)DirectoryUtil.getInstance().extractValue(row, columnName, 0);
                        existingValue += Integer.parseInt(value);
                        this.setSyncTokenValue(relApi, connection, criteria, columnName, existingValue);
                    }
                    else if (columnType.equalsIgnoreCase("BIGINT")) {
                        final Long newVal = Long.parseLong(value);
                        final Long curValue = (Long)row.get(columnName);
                        if (curValue < newVal) {
                            this.setSyncTokenValue(relApi, connection, criteria, columnName, newVal);
                        }
                    }
                    else if (columnType.equalsIgnoreCase("INTEGER")) {
                        this.setSyncTokenValue(relApi, connection, criteria, columnName, Integer.parseInt(value));
                    }
                    else {
                        this.setSyncTokenValue(relApi, connection, criteria, columnName, value);
                    }
                    this.setSyncTokenValue(relApi, connection, criteria, "STATUS_ID", 941);
                }
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    private String generateTableLineForLog(String cell) {
        final int cellSize = 35;
        cell = cell.substring(0, Math.min(cell.length(), cellSize));
        for (int spaceLength = cellSize - cell.length(), i = 0; i < spaceLength; ++i) {
            cell += " ";
        }
        return "|" + cell + "|";
    }
    
    private void updateAsyncPostedTallies(final RelationalAPI relAPI, final Connection connection, final Criteria joinCri, final String columnName, final int tokeAttrID) throws QueryConstructionException, SQLException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
        updateQuery.addJoin(new Join("DirectorySyncDetails", "DirectorySyncAsynchPostDetails", joinCri, 2));
        updateQuery.setCriteria(joinCri.and(new Criteria(Column.getColumn("DirectorySyncAsynchPostDetails", "TOKEN_ATTRIBUTE_ID"), (Object)tokeAttrID, 0)));
        updateQuery.setUpdateColumn(columnName, (Object)Column.getColumn("DirectorySyncAsynchPostDetails", "VALUE"));
        relAPI.execute(connection, relAPI.getUpdateSQL(updateQuery));
    }
    
    void updateReceivedCount() throws Exception {
        Connection connection = null;
        final RelationalAPI relAPI = RelationalAPI.getInstance();
        try {
            connection = relAPI.getConnection();
            final Criteria baseJoinCri = new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)Column.getColumn("DirectorySyncAsynchPostDetails", "SYNC_TOKEN_ID"), 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)new Integer[] { 951, 941 }, 8));
            this.updateAsyncPostedTallies(relAPI, connection, baseJoinCri, "LAST_COUNT", 6);
            this.updateAsyncPostedTallies(relAPI, connection, baseJoinCri, "FIRST_COUNT", 5);
            this.updateAsyncPostedTallies(relAPI, connection, baseJoinCri, "POSTED_COUNT", 7);
            final Criteria baseCri = new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)941, 0));
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
            updateQuery.setCriteria(baseCri);
            final Column dirSyncTokenCol = new Column("DirObjTmp", "SYNC_TOKEN_ID");
            final Column collatedCountCol = (Column)Column.createFunction("COUNT", new Object[] { new Column("DirObjTmp", "TEMP_ID") });
            collatedCountCol.setType(4);
            collatedCountCol.setColumnAlias("DIROBJTEMP.TEMP_ID");
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjTmp"));
            subQuery.addJoin(new Join("DirObjTmp", "DirectorySyncDetails", new String[] { "SYNC_TOKEN_ID" }, new String[] { "SYNC_TOKEN_ID" }, 2));
            subQuery.addSelectColumn(dirSyncTokenCol);
            subQuery.addSelectColumn(collatedCountCol);
            subQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(dirSyncTokenCol))));
            subQuery.setCriteria(baseCri);
            final DerivedTable dtab = new DerivedTable("DirObjTmp", (Query)subQuery);
            updateQuery.addJoin(new Join(Table.getTable("DirectorySyncDetails"), (Table)dtab, new String[] { "SYNC_TOKEN_ID" }, new String[] { "SYNC_TOKEN_ID" }, 2));
            updateQuery.setUpdateColumn("RECEIVED_COUNT", (Object)new Column(dtab.getTableAlias(), collatedCountCol.getColumnAlias()));
            relAPI.execute(connection, relAPI.getUpdateSQL(updateQuery));
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    @Override
    public JSONArray getDirectorySyncSummary(final Criteria criteria) throws Exception {
        this.updateReceivedCount();
        return super.getDirectorySyncSummary(criteria);
    }
    
    private void evaluateForProcessingTempData() throws Exception {
        final JSONArray dirSyncDetails = getInstance().getDirectorySyncSummary((Criteria)null);
        for (int i = 0; i < dirSyncDetails.size(); ++i) {
            final JSONObject directorySyncDetails = (JSONObject)dirSyncDetails.get(i);
            final Integer syncStatusID = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"STATUS_ID")));
            if (syncStatusID == 941) {
                final String dmDomainName = (String)directorySyncDetails.get((Object)"NAME");
                final Long customerID = (Long)directorySyncDetails.get((Object)"CUSTOMER_ID");
                final Integer dmDomainClient = (Integer)directorySyncDetails.get((Object)"CLIENT_ID");
                final Long dmDomainID = (Long)directorySyncDetails.get((Object)"DM_DOMAIN_ID");
                final Integer syncType = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"SYNC_TYPE")));
                final Integer lastCount = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"LAST_COUNT")));
                final Integer firstCount = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"FIRST_COUNT")));
                final Long minSyncTokenAddedAt = Long.valueOf(String.valueOf(directorySyncDetails.get((Object)"ADDED_AT")));
                final Integer postedCount = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"POSTED_COUNT")));
                final Integer receivedCount = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"RECEIVED_COUNT")));
                final Integer preProcessedCount = Integer.valueOf(String.valueOf(directorySyncDetails.get((Object)"PRE_PROCESSED_COUNT")));
                if (firstCount != 0 && minSyncTokenAddedAt != 0L && lastCount == (int)firstCount && postedCount == (int)receivedCount && postedCount == (int)preProcessedCount && preProcessedCount == (int)receivedCount) {
                    if (syncType == 1 && postedCount == 0) {
                        DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, dmDomainName, dmDomainClient, new Exception("no fooling me my man :p"), null);
                    }
                    else {
                        final int collateWaitTime = IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClient).getCollateWaitTime();
                        if (collateWaitTime > 0) {
                            IDPSlogger.SYNC.log(Level.INFO, "collate is required.. waiting for {0} seconds", new Object[] { collateWaitTime });
                            final int pendingCollateTasksCount = DirectoryCollationHandler.getInstance().getPendingScheduledRequests(dmDomainID, dmDomainName, collateWaitTime);
                            if (pendingCollateTasksCount < 1) {
                                DirectoryCollationHandler.getInstance().scheduleCollateTask(customerID, dmDomainID, dmDomainName, dmDomainClient, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(collateWaitTime));
                            }
                        }
                        else {
                            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
                            final JSONObject domainTaskDetails = IdpsJSONutil.convertPropertiesToJSONObject(dmDomainProps);
                            DirectoryCollationHandler.getInstance().collate(domainTaskDetails, dmDomainID, null);
                        }
                    }
                }
            }
        }
    }
    
    void printDirectorySyncDetails() throws Exception {
        this.evaluateForProcessingTempData();
        final DataObject dataObject = SyMUtil.getPersistenceLite().get(IdpsUtil.formSelectQuery("DirectorySyncDetails", (Criteria)null, new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("DMDomain", "NAME"), Column.getColumn("DirectorySyncDetails", "*"), Column.getColumn("DMDomain", "DOMAIN_ID"))), (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new SortColumn(Column.getColumn("DirectorySyncDetails", "ADDED_AT"), true))), new ArrayList((Collection<? extends E>)Arrays.asList(new Join("DirectorySyncDetails", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2))), (Criteria)null));
        final List<StringBuilder> sbs = new ArrayList<StringBuilder>();
        for (int i = 0; i < 11; ++i) {
            final StringBuilder sb = new StringBuilder();
            switch (i) {
                case 0: {
                    sb.append(this.generateTableLineForLog("DOMAIN_ID"));
                    break;
                }
                case 1: {
                    sb.append(this.generateTableLineForLog("NAME"));
                    break;
                }
                case 2: {
                    sb.append(this.generateTableLineForLog("SYNC_TOKEN_ID"));
                    break;
                }
                case 3: {
                    sb.append(this.generateTableLineForLog("SYNC_TYPE"));
                    break;
                }
                case 4: {
                    sb.append(this.generateTableLineForLog("ADDED_AT"));
                    break;
                }
                case 5: {
                    sb.append(this.generateTableLineForLog("FIRST_COUNT"));
                    break;
                }
                case 6: {
                    sb.append(this.generateTableLineForLog("LAST_COUNT"));
                    break;
                }
                case 7: {
                    sb.append(this.generateTableLineForLog("POSTED_COUNT"));
                    break;
                }
                case 8: {
                    sb.append(this.generateTableLineForLog("PRE_PROCESSED_COUNT"));
                    break;
                }
                case 9: {
                    sb.append(this.generateTableLineForLog("RECEIVED_COUNT"));
                    break;
                }
                case 10: {
                    sb.append(this.generateTableLineForLog("STATUS_ID"));
                    break;
                }
            }
            sbs.add(sb);
        }
        if (dataObject != null && !dataObject.isEmpty() && dataObject.containsTable("DirectorySyncDetails") && dataObject.size("DirectorySyncDetails") > 0) {
            final Iterator iterator = dataObject.getRows("DirectorySyncDetails");
            while (iterator != null && iterator.hasNext()) {
                final Row row = iterator.next();
                final Long dmDomainID = (Long)row.get("DM_DOMAIN_ID");
                final Row dmDomainRow = dataObject.getRow("DMDomain", new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0));
                final String dmDomainName = (String)dmDomainRow.get("NAME");
                for (int j = 0; j < sbs.size(); ++j) {
                    switch (j) {
                        case 0: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(dmDomainID)));
                            break;
                        }
                        case 1: {
                            sbs.get(j).append(this.generateTableLineForLog(dmDomainName));
                            break;
                        }
                        case 2: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("SYNC_TOKEN_ID"))));
                            break;
                        }
                        case 3: {
                            sbs.get(j).append(this.generateTableLineForLog(DirectoryUtil.getInstance().getSyncTypeValueInString((Integer)row.get("SYNC_TYPE"))));
                            break;
                        }
                        case 4: {
                            sbs.get(j).append(this.generateTableLineForLog(DirectoryUtil.getInstance().longdateToString((Long)row.get("ADDED_AT"))));
                            break;
                        }
                        case 5: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("FIRST_COUNT"))));
                            break;
                        }
                        case 6: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("LAST_COUNT"))));
                            break;
                        }
                        case 7: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("POSTED_COUNT"))));
                            break;
                        }
                        case 8: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("PRE_PROCESSED_COUNT"))));
                            break;
                        }
                        case 9: {
                            sbs.get(j).append(this.generateTableLineForLog(String.valueOf(row.get("RECEIVED_COUNT"))));
                            break;
                        }
                        case 10: {
                            sbs.get(j).append(this.generateTableLineForLog(DirectoryUtil.getInstance().getSyncStatusInString(row.get("STATUS_ID"))));
                            break;
                        }
                    }
                    sbs.set(j, sbs.get(j));
                }
            }
            final StringBuilder syncLogView = new StringBuilder();
            for (final StringBuilder sb2 : sbs) {
                syncLogView.append(System.lineSeparator());
                syncLogView.append(sb2.toString());
            }
            IDPSlogger.SYNC.log(Level.INFO, syncLogView.toString());
        }
    }
    
    private Long[] getSyncTokens(final Criteria criteria) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"));
        final List<Long> syncTokenList = DBUtil.getColumnValuesAsList(SyMUtil.getPersistenceLite().get(selectQuery).getRows("DirectorySyncDetails"), "SYNC_TOKEN_ID");
        final Long[] syncTokens = syncTokenList.toArray(new Long[syncTokenList.size()]);
        return syncTokens;
    }
    
    Long[] getSyncTokens(final Long dmDomainID, final int status) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)status, 0));
        return this.getSyncTokens(criteria);
    }
    
    Long[] getSyncTokens(final int status) throws DataAccessException {
        return this.getSyncTokens(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)status, 0));
    }
    
    Long getNewSyncToken(final Long dmDomainID, final String domainName, final Integer syncType, final String postSyncOPdetails) throws Exception {
        return this.getNewSyncToken(dmDomainID, domainName, syncType, postSyncOPdetails, false);
    }
    
    static {
        DirectorySequnceSynchImpl.directorySequnceSynchImpl = null;
    }
}
