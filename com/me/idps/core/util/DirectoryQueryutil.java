package com.me.idps.core.util;

import java.util.Hashtable;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.Join;
import com.me.idps.core.factory.TransactionExecutionImpl;
import java.util.Properties;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.sql.SQLTimeoutException;
import java.util.concurrent.TimeUnit;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;
import java.sql.Statement;
import java.sql.ResultSet;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.List;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.sql.Connection;
import java.util.ArrayList;
import com.me.idps.core.factory.TransactionExecutionInterface;

public class DirectoryQueryutil implements TransactionExecutionInterface
{
    public static final int MAX_QUERY_RETRY = 5;
    private static final int[] TIME_OUT;
    public static final boolean IN_TRANSACTION = false;
    private static final String QUERY = "QUERY";
    private static final String CONNECTION = "CONNECTION";
    private static final int QUERY_TIME_OUT = 1;
    private static final int MS_SQL_DEADLOCK = 2;
    private static final ArrayList<String> AUTO_VA_DISABLED_TABLES;
    private static DirectoryQueryutil directoryQueryutil;
    
    public void prePlan(final Connection connection) throws Exception {
        if (IdpsFactoryProvider.getIdpsProdEnvAPI().isPrePlanEnabled()) {
            final QueryExecMeta queryExecMeta = new QueryExecMeta("", "", (List)DirectoryQueryutil.AUTO_VA_DISABLED_TABLES);
            this.vacuumAndAnalyse(connection, queryExecMeta, false);
            IDPSlogger.DBO.log(Level.INFO, "prePlan {0}", new Object[] { queryExecMeta.getDurationLog() });
        }
    }
    
    private static ArrayList<String> getAutoVADisabledTables() {
        final ArrayList<String> autoVAdisabledTables = new ArrayList<String>(Arrays.asList("DirResRel".toUpperCase(), "DirObjTmp".toUpperCase(), "DirObjTmpDupl".toUpperCase(), "DirObjRegIntVal".toUpperCase(), "DirObjRegStrVal".toUpperCase(), "DirObjArrLngVal".toUpperCase(), "DirObjTmpDuplVal".toUpperCase(), "DirObjTmpDuplAttr".toUpperCase(), "DirObjTmpRegIntVal".toUpperCase(), "DirObjTmpRegStrVal".toUpperCase(), "DirObjTmpArrStrVal".toUpperCase(), "DirTmpAvailableRes".toUpperCase(), "DirectoryEventToken".toUpperCase(), "DirectoryEventDetails".toUpperCase()));
        try {
            final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
            dirProdImplRequest.eventType = IdpEventConstants.GET_AUTO_VA_DISABLED_TABLES;
            final ArrayList<String> prodSpecificAutoVAdisabledTables = (ArrayList<String>)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
            autoVAdisabledTables.addAll(prodSpecificAutoVAdisabledTables);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
        return autoVAdisabledTables;
    }
    
    public static DirectoryQueryutil getInstance() {
        if (DirectoryQueryutil.directoryQueryutil == null) {
            DirectoryQueryutil.directoryQueryutil = new DirectoryQueryutil();
        }
        return DirectoryQueryutil.directoryQueryutil;
    }
    
    public TableDefinition getTableDefinition(final String tableName) throws MetaDataException {
        return MetaDataUtil.getTableDefinitionByName(tableName);
    }
    
    public Criteria getResCri(final String dmDomainName, final Long customerID) {
        return new Criteria(Column.getColumn("Resource", "NAME"), (Object)null, 1).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)null, 1, false)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)dmDomainName, 0, false));
    }
    
    public SelectQuery getSelectQuery(final SelectQuery selectQuery, final HashMap<String, Column> columnValuesMap) {
        final Iterator itr = columnValuesMap.entrySet().iterator();
        while (itr != null && itr.hasNext()) {
            final Map.Entry<String, Column> entry = itr.next();
            final Column selectCol = entry.getValue();
            if (selectCol != null) {
                selectQuery.addSelectColumn(selectCol);
            }
        }
        return selectQuery;
    }
    
    private String getInsertQueryBasedOnSelectQueryStr(final String insertIntoTableName, final String selectQueryStr, final HashMap<String, Column> columnValuesMap) {
        String insertColumnsStr = "";
        final Iterator itr = columnValuesMap.entrySet().iterator();
        while (itr != null && itr.hasNext()) {
            final Map.Entry<String, Column> entry = itr.next();
            final String insertColumn = entry.getKey();
            insertColumnsStr += (SyMUtil.isStringEmpty(insertColumnsStr) ? insertColumn : ("," + insertColumn));
        }
        final String query = "insert into " + insertIntoTableName + " (" + insertColumnsStr + ") " + selectQueryStr;
        return query;
    }
    
    public SelectQuery getDirObjAttrQuery(final Criteria criteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegStrVal"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return selectQuery;
    }
    
    private String getTxStatusStr(final int txStatus) {
        switch (txStatus) {
            case 0: {
                return "STATUS_ACTIVE";
            }
            case 1: {
                return "STATUS_MARKED_ROLLBACK";
            }
            case 2: {
                return "STATUS_PREPARED";
            }
            case 3: {
                return "STATUS_COMMITTED";
            }
            case 4: {
                return "STATUS_ROLLEDBACK";
            }
            case 5: {
                return "STATUS_UNKNOWN";
            }
            case 6: {
                return "STATUS_NO_TRANSACTION";
            }
            case 7: {
                return "STATUS_PREPARING";
            }
            case 8: {
                return "STATUS_COMMITTING";
            }
            case 9: {
                return "STATUS_ROLLING_BACK";
            }
            default: {
                return "STATUS_UNKNOWN";
            }
        }
    }
    
    protected boolean executePGSpecificQueries(final Connection connection, final String query) throws SystemException {
        boolean executedPGSpecificQueries = false;
        if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            final TransactionManager tm = SyMUtil.getUserTransaction();
            if (tm != null) {
                final int tmStatus = tm.getStatus();
                IDPSlogger.DBO.log(Level.FINE, "transaction status:{0}", new Object[] { tmStatus });
                if (tmStatus == 6) {
                    try {
                        RelationalAPI.getInstance().execute(connection, query);
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                    }
                    executedPGSpecificQueries = true;
                }
                else {
                    IDPSlogger.DBO.log(Level.WARNING, "skipping PG specific queries as transaction status = {0}", new Object[] { this.getTxStatusStr(tmStatus) });
                }
            }
        }
        return executedPGSpecificQueries;
    }
    
    private boolean isAutoVAdisabledTable(final String tableName) {
        final String upperTableName = tableName.toUpperCase();
        return DirectoryQueryutil.AUTO_VA_DISABLED_TABLES.contains(upperTableName);
    }
    
    private boolean vacuum(final Connection connection, final String tableName, final boolean doVAforAll) throws Exception {
        final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        return loginId != null || (!this.isAutoVAdisabledTable(tableName) && !doVAforAll) || this.executePGSpecificQueries(connection, MessageFormat.format("vacuum analyze {0}", tableName.toLowerCase()));
    }
    
    private String formVAcheckQuery(final QueryExecMeta queryExecMeta, final String schemaName) {
        final List<String> tableNames = queryExecMeta.getTableNames();
        String query = "select relname,n_dead_tup from pg_stat_user_tables where relname in ({0}){1}and n_dead_tup > 0";
        final StringBuilder tables = new StringBuilder();
        for (int i = 0; tableNames != null && i < tableNames.size(); ++i) {
            final String tableName = tableNames.get(i);
            final String tableNameFormatted = tableName.toLowerCase();
            if (!SyMUtil.isStringEmpty(tableNameFormatted)) {
                if (i > 0) {
                    tables.append(",");
                }
                tables.append("'");
                tables.append(tableNameFormatted);
                tables.append("'");
            }
        }
        final String tablesStr = tables.toString();
        if (!IdpsUtil.isStringEmpty(tablesStr)) {
            String schemaFilter = " ";
            if (!IdpsUtil.isStringEmpty(schemaName) && !schemaName.equalsIgnoreCase("---")) {
                schemaFilter = " and schemaname = '" + schemaName + "' ";
            }
            query = MessageFormat.format(query, tables.toString(), schemaFilter);
            return query;
        }
        return null;
    }
    
    private void vacuumAndAnalyse(final Connection connection, final QueryExecMeta queryExecMeta, final boolean doVAforAll) throws Exception {
        final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        if (loginId == null && !DirectoryUtil.getInstance().isManualVAdisabled(false) && DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
            final JSONArray deadTupTables = new JSONArray();
            final long planStart = System.currentTimeMillis();
            final String schemaName = IdpsFactoryProvider.getIdpsProdEnvAPI().getSchemaName();
            final String query = this.formVAcheckQuery(queryExecMeta, schemaName);
            if (!IdpsUtil.isStringEmpty(query)) {
                ResultSet rs = null;
                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                    rs = stmt.executeQuery(query);
                    while (rs != null & rs.next()) {
                        final String tableName = rs.getString("relname");
                        final int deadTupCount = rs.getInt("n_dead_tup");
                        final JSONObject deadTupTblDetails = new JSONObject();
                        deadTupTblDetails.put((Object)"relname", (Object)tableName);
                        deadTupTblDetails.put((Object)"n_dead_tup", (Object)deadTupCount);
                        deadTupTables.add((Object)deadTupTblDetails);
                    }
                }
                catch (final Exception ex) {
                    throw ex;
                }
                finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                    }
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                    }
                }
            }
            final long planEnd = System.currentTimeMillis();
            queryExecMeta.addDurationLog(planEnd - planStart, 1);
            if (deadTupTables != null && deadTupTables.size() > 0) {
                final long vaStart = System.currentTimeMillis();
                for (int i = 0; i < deadTupTables.size(); ++i) {
                    final JSONObject deadTupTblDetails2 = (JSONObject)deadTupTables.get(i);
                    final String tableName2 = (String)deadTupTblDetails2.get((Object)"relname");
                    final int deadTupCount2 = (int)deadTupTblDetails2.get((Object)"n_dead_tup");
                    IDPSlogger.DBO.log(Level.INFO, "{0} {1} has {2} deadTupCount", new Object[] { schemaName, tableName2, deadTupCount2 });
                    this.vacuum(connection, tableName2, doVAforAll);
                }
                final long vaEnd = System.currentTimeMillis();
                queryExecMeta.addDurationLog(vaEnd - vaStart, 2);
            }
        }
    }
    
    private int doQueryExecTimeOutRetryHandling(final Long customerID, final Connection connection, final QueryExecMeta queryExecMeta, int retry, int mssqlRetry, final Exception e, final int errorType) throws Exception {
        IDPSlogger.ERR.log(Level.SEVERE, null, e);
        if (errorType == 1) {
            if (retry > 1) {
                this.vacuumAndAnalyse(connection, queryExecMeta, true);
                --retry;
                IDPSlogger.DBO.log(Level.WARNING, "retrying {0}, attempt {1}", new Object[] { queryExecMeta.getExecQuery(), 5 - retry });
                return this.execQuery(customerID, connection, queryExecMeta, retry, mssqlRetry);
            }
            IDPSlogger.DBO.log(Level.SEVERE, "{0} failed to execute despite time-out retry attempts {1}", new Object[] { queryExecMeta.getExecQuery(), 5 });
            DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, "QUERY_TIMED_OUT_ERROR_COUNT", 1);
            throw e;
        }
        else {
            if (errorType != 2) {
                throw e;
            }
            if (mssqlRetry > 1) {
                --mssqlRetry;
                IDPSlogger.DBO.log(Level.WARNING, "ms sql retrying {0}, attempt {1}", new Object[] { queryExecMeta.getExecQuery(), 5 - mssqlRetry });
                return this.execQuery(customerID, connection, queryExecMeta, retry, mssqlRetry);
            }
            IDPSlogger.DBO.log(Level.SEVERE, "{0} failed to execute despite sql-deadlock retry attempts {1}", new Object[] { queryExecMeta.getExecQuery(), 5 });
            DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, "MS_SQL_DEADLOCK_ERROR_COUNT", 1);
            throw e;
        }
    }
    
    private int execQuery(final Long customerID, final Connection connection, final QueryExecMeta queryExecMeta, final int retry, final int msSqlretry) throws Exception {
        int affectedRows = 0;
        final String execQuery = queryExecMeta.getExecQuery();
        try {
            if (retry <= 0 || retry > 5) {
                throw new Exception("retry violates constraints");
            }
            final int timeOutSeconds = DirectoryQueryutil.TIME_OUT[5 - retry];
            Statement stmt = null;
            boolean doPostVAanalysis = false;
            queryExecMeta.start();
            final Long queryExecStart = System.currentTimeMillis();
            try {
                stmt = connection.createStatement();
                final TransactionManager tm = SyMUtil.getUserTransaction();
                if (tm != null) {
                    final int tmStatus = tm.getStatus();
                    IDPSlogger.DBO.log(Level.FINE, "transaction status:{0}", new Object[] { tmStatus });
                    if (tmStatus == 6) {
                        stmt.setQueryTimeout(timeOutSeconds);
                    }
                }
                RelationalAPI.getInstance().getDBAdapter().execute(stmt, execQuery);
                try {
                    affectedRows = stmt.getUpdateCount();
                    queryExecMeta.setAffectedRows(affectedRows);
                }
                catch (final Exception ex1) {
                    IDPSlogger.ERR.log(Level.FINEST, "Exception occured while getting affected rows", ex1);
                }
                doPostVAanalysis = true;
            }
            finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (final Exception ex2) {
                    IDPSlogger.ERR.log(Level.FINEST, "Exception occured while closing a statement", ex2);
                }
                final Long queryExecEnd = System.currentTimeMillis();
                queryExecMeta.addDurationLog(queryExecEnd - queryExecStart, 3);
                if (doPostVAanalysis) {
                    if (IdpsFactoryProvider.getIdpsProdEnvAPI().isPrePlanEnabled()) {
                        if (affectedRows > 0) {
                            final long vaStart = System.currentTimeMillis();
                            this.vacuum(connection, queryExecMeta.affectedTableName, false);
                            final long vaEnd = System.currentTimeMillis();
                            queryExecMeta.addDurationLog(vaEnd - vaStart, 2);
                        }
                    }
                    else {
                        final long queryExecInSeconds = TimeUnit.MILLISECONDS.toSeconds(queryExecEnd - queryExecStart);
                        if (queryExecInSeconds > 1L && affectedRows > 0) {
                            final long vaStart2 = System.currentTimeMillis();
                            this.vacuum(connection, queryExecMeta.affectedTableName, false);
                            final long vaEnd2 = System.currentTimeMillis();
                            queryExecMeta.addDurationLog(vaEnd2 - vaStart2, 2);
                        }
                    }
                }
                queryExecMeta.finish();
            }
        }
        catch (final SQLTimeoutException ste) {
            final boolean errLoggingLevelWarning = retry <= 2;
            IDPSlogger.ERR.log(errLoggingLevelWarning ? Level.WARNING : Level.FINE, "timed out executing {0}, customer id {1} {2}", new Object[] { execQuery, String.valueOf(customerID), ste });
            return this.doQueryExecTimeOutRetryHandling(customerID, connection, queryExecMeta, retry, msSqlretry, ste, 1);
        }
        catch (final Exception ex3) {
            final boolean errLoggingLevelWarning = retry <= 2;
            IDPSlogger.ERR.log(errLoggingLevelWarning ? Level.WARNING : Level.FINE, "exception occurred executing {0} customer ID:{1}", new Object[] { execQuery, String.valueOf(customerID) });
            final String exMsg = ex3.getMessage();
            if (SyMUtil.isStringEmpty(exMsg) || (!exMsg.contains("The query has timed out") && !exMsg.contains("canceling statement due to user request") && !exMsg.contains("was deadlocked on lock resources with another process and has been chosen as the deadlock victim. Rerun the transaction"))) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in executing " + execQuery, ex3);
                throw ex3;
            }
            if (exMsg.contains("The query has timed out") || exMsg.contains("canceling statement due to user request")) {
                return this.doQueryExecTimeOutRetryHandling(customerID, connection, queryExecMeta, retry, msSqlretry, ex3, 1);
            }
            if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql") && exMsg.contains("was deadlocked on lock resources with another process and has been chosen as the deadlock victim. Rerun the transaction")) {
                return this.doQueryExecTimeOutRetryHandling(customerID, connection, queryExecMeta, retry, msSqlretry, ex3, 2);
            }
            throw ex3;
        }
        return affectedRows;
    }
    
    private int execQuery(final Connection connection, final QueryExecMeta queryExecMeta) throws Exception {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final int affectedRows = this.execQuery(customerID, connection, queryExecMeta, 5, 10);
        return affectedRows;
    }
    
    private List<Integer> executeQueries(final Connection connection, final List<QueryExecMeta> queriesToExecute, final boolean inTransaction) throws Exception {
        List<Integer> affectedRows = new ArrayList<Integer>();
        final List<QueryExecMeta> rectifiedQueriesToExecute = new ArrayList<QueryExecMeta>();
        for (int i = 0; i < queriesToExecute.size(); ++i) {
            final QueryExecMeta queryExecMeta = queriesToExecute.get(i);
            final String query = queryExecMeta.getExecQuery();
            final String rectifiedQuery = DirectoryMickeyIssueHandler.getInstance().rectifyQuery(query);
            queryExecMeta.setExecQuery(rectifiedQuery);
            if (inTransaction) {
                rectifiedQueriesToExecute.add(queryExecMeta);
            }
            else {
                affectedRows.add(this.execQuery(connection, queryExecMeta));
            }
        }
        if (inTransaction) {
            final Properties props = new Properties();
            ((Hashtable<String, Connection>)props).put("CONNECTION", connection);
            ((Hashtable<String, List<QueryExecMeta>>)props).put("QUERY", rectifiedQueriesToExecute);
            IDPSlogger.DBO.log(Level.INFO, "transaction going to be started");
            affectedRows = (List)TransactionExecutionImpl.getInstance().performTaskInTransactionMode("com.me.idps.core.util.DirectoryQueryutil", props);
            IDPSlogger.DBO.log(Level.INFO, "transaction done");
        }
        return affectedRows;
    }
    
    private int executeQuery(final Connection connection, final QueryExecMeta queryExecMeta, final boolean inTransaction) throws Exception {
        final List<QueryExecMeta> queries = new ArrayList<QueryExecMeta>(Arrays.asList(queryExecMeta));
        final List<Integer> affectedRows = this.executeQueries(connection, queries, inTransaction);
        return affectedRows.get(0);
    }
    
    private List<String> validateAndAddTable(final String tableName, final List<String> joinTables) {
        if (!SyMUtil.isStringEmpty(tableName) && !joinTables.contains(tableName)) {
            boolean dbTable = false;
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td != null) {
                    dbTable = true;
                }
            }
            catch (final Exception ex) {}
            if (dbTable) {
                joinTables.add(tableName);
            }
        }
        return joinTables;
    }
    
    private List<String> getAllQueryTableNames(List<String> joinTables, final List<Join> joins) {
        if (joins != null && !joins.isEmpty()) {
            for (final Join join : joins) {
                final String refTableName = join.getReferencedTableName();
                joinTables = this.validateAndAddTable(refTableName, joinTables);
                final String baseTableName = join.getBaseTableName();
                joinTables = this.validateAndAddTable(baseTableName, joinTables);
                final Table baseTable = join.getBaseTable();
                if (baseTable != null && baseTable instanceof DerivedTable) {
                    final Query subQuery = ((DerivedTable)baseTable).getSubQuery();
                    if (subQuery != null && subQuery instanceof SelectQuery) {
                        joinTables = this.getAllQueryTableNames(joinTables, ((SelectQuery)subQuery).getJoins());
                    }
                }
                final Table refTable = join.getReferencedTable();
                if (refTable != null && refTable instanceof DerivedTable) {
                    final Query subQuery2 = ((DerivedTable)refTable).getSubQuery();
                    if (subQuery2 == null || !(subQuery2 instanceof SelectQuery)) {
                        continue;
                    }
                    joinTables = this.getAllQueryTableNames(joinTables, ((SelectQuery)subQuery2).getJoins());
                }
            }
        }
        return joinTables;
    }
    
    private List<String> getAllQueryTableNames(final List<Join> joins) {
        return this.getAllQueryTableNames(new ArrayList<String>(), joins);
    }
    
    private String getInsertQuery(final SelectQuery baseInsertQuery, final String insertIntoTable, final HashMap<String, Column> colMap, final HashMap<String, String> replaceMap) throws QueryConstructionException {
        String insQueryStr = RelationalAPI.getInstance().getSelectSQL((Query)baseInsertQuery);
        insQueryStr = this.getInsertQueryBasedOnSelectQueryStr(insertIntoTable, insQueryStr, colMap);
        if (replaceMap != null) {
            final Iterator itr = replaceMap.entrySet().iterator();
            while (itr != null && itr.hasNext()) {
                final Map.Entry<String, String> entry = itr.next();
                insQueryStr = insQueryStr.replace(entry.getKey(), entry.getValue());
            }
        }
        return insQueryStr;
    }
    
    private int executeInsertQuery(final Connection connection, SelectQuery baseSelectQueryToFormInsertQuery, final String insertIntoTable, final HashMap<String, Column> colMap, final HashMap<String, String> replaceMap, final boolean addSelectColsFromMap, final String block, final boolean inTransaction) throws Exception {
        if (addSelectColsFromMap) {
            baseSelectQueryToFormInsertQuery = this.getSelectQuery(baseSelectQueryToFormInsertQuery, colMap);
        }
        final List<String> queryTables = this.getAllQueryTableNames(baseSelectQueryToFormInsertQuery.getJoins());
        if (!queryTables.contains(insertIntoTable)) {
            queryTables.add(insertIntoTable);
        }
        final String insQueryStr = this.getInsertQuery(baseSelectQueryToFormInsertQuery, insertIntoTable, colMap, replaceMap);
        final QueryExecMeta queryExecMeta = new QueryExecMeta(insertIntoTable, insQueryStr, (List)queryTables);
        final int affectedRows = this.executeQuery(connection, queryExecMeta, inTransaction);
        IDPSlogger.DBO.log(Level.INFO, "   inserted {0} into {1} in {2} | {3}", new Object[] { String.valueOf(affectedRows), insertIntoTable, queryExecMeta.getDurationLog(), block });
        return affectedRows;
    }
    
    private int executeInsertQuery(final Connection connection, final SelectQuery baseSelectQueryToFormInsertQuery, final String insertIntoTable, final HashMap<String, Column> colMap, final HashMap<String, String> replaceMap, final String block, final boolean inTransaction) throws Exception {
        final int affectedRows = this.executeInsertQuery(connection, baseSelectQueryToFormInsertQuery, insertIntoTable, colMap, replaceMap, true, block, inTransaction);
        return affectedRows;
    }
    
    public int executeInsertQuery(final Connection connection, final SelectQuery baseSelectQueryToFormInsertQuery, final String insertIntoTable, final HashMap<String, Column> colMap, final HashMap<String, String> replaceMap, final boolean inTransaction) throws Exception {
        return this.executeInsertQuery(connection, baseSelectQueryToFormInsertQuery, insertIntoTable, colMap, replaceMap, "ND", inTransaction);
    }
    
    public int executeUpdateQuery(final Connection connection, final UpdateQuery updateQuery, final HashMap<String, String> replaceMap, final String block, final boolean inTransaction) throws Exception {
        final String tableName = updateQuery.getTableName();
        String updateSqlStr = RelationalAPI.getInstance().getUpdateSQL(updateQuery);
        if (replaceMap != null) {
            final Iterator itr = replaceMap.entrySet().iterator();
            while (itr != null && itr.hasNext()) {
                final Map.Entry<String, String> entry = itr.next();
                updateSqlStr = updateSqlStr.replace(entry.getKey(), entry.getValue());
            }
        }
        final QueryExecMeta queryExecMeta = new QueryExecMeta(tableName, updateSqlStr, (List)this.getAllQueryTableNames(updateQuery.getJoins()));
        final int affectedRows = this.executeQuery(connection, queryExecMeta, inTransaction);
        if (!tableName.equalsIgnoreCase("DirectorySyncDetails") && !tableName.equalsIgnoreCase("DirPKGenerator")) {
            IDPSlogger.DBO.log(Level.INFO, "   updated {0} rows in {1} in {2} | {3}", new Object[] { String.valueOf(affectedRows), tableName, queryExecMeta.getDurationLog(), block });
        }
        return affectedRows;
    }
    
    private int executeUpdateQuery(final Connection connection, final UpdateQuery updateQuery, final HashMap<String, String> replaceMap, final boolean inTransaction) throws Exception {
        return this.executeUpdateQuery(connection, updateQuery, replaceMap, "ND", inTransaction);
    }
    
    public int executeUpdateQuery(final Connection connection, final UpdateQuery updateQuery, final boolean inTransaction) throws Exception {
        return this.executeUpdateQuery(connection, updateQuery, null, inTransaction);
    }
    
    public int executeUpdateQuery(final UpdateQuery updateQuery, final boolean inTransaction) throws Exception {
        int affectedRows = -1;
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            affectedRows = this.executeUpdateQuery(connection, updateQuery, inTransaction);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            throw ex;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex2);
                throw ex2;
            }
        }
        return affectedRows;
    }
    
    private int executeDeleteQuery(final Connection connection, final DeleteQuery deleteQuery, final String block, final boolean inTransaction) throws Exception {
        final String tableName = deleteQuery.getTableName();
        final String deleteSqlstr = RelationalAPI.getInstance().getDeleteSQL(deleteQuery);
        final List<String> tableNames = this.getAllQueryTableNames(deleteQuery.getJoins());
        final QueryExecMeta queryExecMeta = new QueryExecMeta(tableName, deleteSqlstr, (List)tableNames);
        final int affectedRows = this.executeQuery(connection, queryExecMeta, inTransaction);
        IDPSlogger.DBO.log(Level.INFO, "   deleted {0} rows from {1} in {2} | {3}", new Object[] { String.valueOf(affectedRows), tableName, queryExecMeta.getDurationLog(), block });
        return affectedRows;
    }
    
    public int executeDeleteQuery(final Connection connection, final DeleteQuery deleteQuery, final boolean inTransaction) throws Exception {
        return this.executeDeleteQuery(connection, deleteQuery, "ND", inTransaction);
    }
    
    public int executeDeleteQuery(final DeleteQuery deleteQuery, final boolean inTransaction) throws Exception {
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            return this.executeDeleteQuery(connection, deleteQuery, inTransaction);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            throw ex;
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex2);
                throw ex2;
            }
        }
    }
    
    public int executeUpdateQuery(final Connection connection, final Long dmDomainID, final Long collationID, final UpdateQuery updateQuery, final HashMap<String, String> replaceMap, final String block, final String logMsg, final boolean inTransaction) throws Exception {
        int updatedRows = 0;
        if (!block.equalsIgnoreCase("coreSyncEngine") || DirectoryUtil.getInstance().canExecQuery(dmDomainID, collationID)) {
            if (!SyMUtil.isStringEmpty(logMsg)) {
                IDPSlogger.DBO.log(Level.INFO, logMsg);
            }
            updatedRows = this.executeUpdateQuery(connection, updateQuery, replaceMap, block, inTransaction);
            this.incrementDbOpsMetric(dmDomainID, collationID, block, 2, updatedRows);
        }
        return updatedRows;
    }
    
    public int executeUpdateQuery(final Connection connection, final Long dmDomainID, final Long collationID, final UpdateQuery updateQuery, final String block, final String logMsg, final boolean inTransaction) throws Exception {
        return this.executeUpdateQuery(connection, dmDomainID, collationID, updateQuery, null, block, logMsg, inTransaction);
    }
    
    public int executeInsertQuery(final Connection connection, final Long dmDomainID, final Long collationID, final SelectQuery baseSelectQueryToFormInsertQuery, final String insertIntoTable, final HashMap<String, Column> colMap, final HashMap<String, String> replaceMap, final String block, final String logMsg, final boolean inTransaction) throws Exception {
        int insertedRows = 0;
        if (!block.equalsIgnoreCase("coreSyncEngine") || DirectoryUtil.getInstance().canExecQuery(dmDomainID, collationID)) {
            if (!SyMUtil.isStringEmpty(logMsg)) {
                IDPSlogger.DBO.log(Level.INFO, logMsg);
            }
            insertedRows = this.executeInsertQuery(connection, baseSelectQueryToFormInsertQuery, insertIntoTable, colMap, replaceMap, inTransaction);
            this.incrementDbOpsMetric(dmDomainID, collationID, block, 1, insertedRows);
        }
        return insertedRows;
    }
    
    public void executeDeleteQuery(final Connection connection, final Long dmDomainID, final Long collationID, final DeleteQuery deleteQuery, final String block, final String logMsg, final boolean inTransaction) throws Exception {
        if (!block.equalsIgnoreCase("coreSyncEngine") || DirectoryUtil.getInstance().canExecQuery(dmDomainID, collationID)) {
            if (!SyMUtil.isStringEmpty(logMsg)) {
                IDPSlogger.DBO.log(Level.INFO, logMsg);
            }
            final int deletedRows = this.executeDeleteQuery(connection, deleteQuery, inTransaction);
            this.incrementDbOpsMetric(dmDomainID, collationID, block, 3, deletedRows);
        }
    }
    
    @Override
    public Object executeTxTask(final Properties props) throws Exception {
        final List<Integer> res = new ArrayList<Integer>();
        final List<QueryExecMeta> queries = ((Hashtable<K, List<QueryExecMeta>>)props).get("QUERY");
        final Connection connection = ((Hashtable<K, Connection>)props).get("CONNECTION");
        for (int i = 0; i < queries.size(); ++i) {
            final QueryExecMeta queryExecMeta = queries.get(i);
            final int affectedRows = this.execQuery(connection, queryExecMeta);
            res.add(affectedRows);
        }
        return res;
    }
    
    public void validateUserNameUniqueness(final Connection connection, final Criteria baseResJoinCri) throws Exception {
        final String countAlias = "DUPLICATED_RESNAME_COUNT";
        final Column resNameCol = Column.getColumn("DirObjRegStrVal", "VALUE", "DUPLICATED_RESNAME");
        final Column domainIDCol = Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID", "DUPLICATED_DOMAINID");
        final Column objIDcountCol = IdpsUtil.getCountOfColumn("DirObjRegStrVal", "OBJ_ID", countAlias);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegStrVal"));
        selectQuery.addJoin(new Join("DirObjRegStrVal", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        selectQuery.setCriteria(baseResJoinCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "DIR_RESOURCE_TYPE"), (Object)2, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)new Integer[] { 4, 5 }, 9)));
        selectQuery.addSelectColumn(resNameCol);
        selectQuery.addSelectColumn(objIDcountCol);
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(resNameCol, domainIDCol)), new Criteria(objIDcountCol, (Object)1, 5)));
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(connection, selectQuery);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            int count = 0;
            for (int i = 0; i < jsonArray.size(); ++i) {
                final JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                if (jsonObject.containsKey((Object)countAlias)) {
                    int curCount = 0;
                    try {
                        curCount = Integer.valueOf(String.valueOf(jsonObject.get((Object)countAlias)));
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.WARNING, null, ex);
                    }
                    count += curCount;
                }
            }
            if (count > 0) {
                final String hint = IdpsUtil.getPrettyJSON(jsonArray);
                IDPSlogger.ERR.log(Level.SEVERE, "resource name needs to be unique {0}", new Object[] { hint });
                throw new Exception("INPUT_USER_NAME_DUPL_ERROR " + hint);
            }
        }
    }
    
    public void incrementDbOpsMetric(final Long dmDomainID, final Long collationID, final String block, final int opType, final int affectedRowsCount) {
        final int curValue = DirectoryUtil.getInstance().getCurrentDBOpsMetric(dmDomainID, collationID, block, opType, false);
        final String key = DirectoryUtil.getInstance().getDbOpMetricKey(dmDomainID, collationID, block, opType);
        final int newVal = curValue + affectedRowsCount;
        ApiFactoryProvider.getCacheAccessAPI().putCache(key, (Object)newVal, 2, 10800);
    }
    
    public static String getCorrespondingMainTable(final String tempTableName) {
        switch (tempTableName) {
            case "DirObjTmpRegIntVal": {
                return "DirObjRegIntVal";
            }
            case "DirObjTmpRegStrVal": {
                return "DirObjRegStrVal";
            }
            case "DirObjTmpArrStrVal": {
                return "DirObjArrLngVal";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getAddedAtCol(final String mainTableName) {
        switch (mainTableName) {
            case "DirObjRegIntVal": {
                return "ADDED_AT";
            }
            case "DirObjRegStrVal": {
                return "ADDED_AT";
            }
            case "DirObjArrLngVal": {
                return "ADDED_AT";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getModifiedAtCol(final String mainTableName) {
        switch (mainTableName) {
            case "DirObjRegIntVal": {
                return "MODIFIED_AT";
            }
            case "DirObjRegStrVal": {
                return "MODIFIED_AT";
            }
            case "DirObjArrLngVal": {
                return "MODIFIED_AT";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getValCol(final String mainTableName) {
        switch (mainTableName) {
            case "DirObjRegIntVal": {
                return "VALUE";
            }
            case "DirObjRegStrVal": {
                return "VALUE";
            }
            case "DirObjArrLngVal": {
                return "VALUE";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getObjIDcol(final String mainTableName) {
        switch (mainTableName) {
            case "DirObjRegIntVal": {
                return "OBJ_ID";
            }
            case "DirObjRegStrVal": {
                return "OBJ_ID";
            }
            case "DirObjArrLngVal": {
                return "OBJ_ID";
            }
            case "DirObjTmpRegStrVal": {
                return "OBJ_ID";
            }
            case "DirObjTmpRegIntVal": {
                return "OBJ_ID";
            }
            case "DirObjTmpArrStrVal": {
                return "OBJ_ID";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getTempIDcol(final String tableName) {
        String tempIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                tempIDcol = "TEMP_ID";
                break;
            }
            case "DirObjTmpRegIntVal": {
                tempIDcol = "TEMP_ID";
                break;
            }
            case "DirObjTmpArrStrVal": {
                tempIDcol = "TEMP_ID";
                break;
            }
        }
        return tempIDcol;
    }
    
    public static String getTempValCol(final String tableName) {
        String tempIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                tempIDcol = "TEMP_VALUE";
                break;
            }
            case "DirObjTmpRegIntVal": {
                tempIDcol = "TEMP_VALUE";
                break;
            }
            case "DirObjTmpArrStrVal": {
                tempIDcol = "TEMP_VALUE";
                break;
            }
        }
        return tempIDcol;
    }
    
    public static String getAttrAddedAtCol(final String tableName) {
        String tempIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                tempIDcol = "ADDED_AT";
                break;
            }
            case "DirObjTmpRegIntVal": {
                tempIDcol = "ADDED_AT";
                break;
            }
            case "DirObjTmpArrStrVal": {
                tempIDcol = "ATTR_ADDED_AT";
                break;
            }
        }
        return tempIDcol;
    }
    
    public static String getMaxAddedAtCol(final String tableName) {
        String tempIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                tempIDcol = "MAX_ADDED_AT";
                break;
            }
            case "DirObjTmpRegIntVal": {
                tempIDcol = "MAX_ADDED_AT";
                break;
            }
            case "DirObjTmpArrStrVal": {
                tempIDcol = "MAX_ADDED_AT";
                break;
            }
        }
        return tempIDcol;
    }
    
    public static String getAttrIDcol(final String tableName) {
        switch (tableName) {
            case "DirObjRegIntVal": {
                return "ATTR_ID";
            }
            case "DirObjRegStrVal": {
                return "ATTR_ID";
            }
            case "DirObjArrLngVal": {
                return "ATTR_ID";
            }
            case "DirObjTmpRegStrVal": {
                return "ATTR_ID";
            }
            case "DirObjTmpRegIntVal": {
                return "ATTR_ID";
            }
            case "DirObjTmpArrStrVal": {
                return "ATTR_ID";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getDuplMaxTempIDcol(final String tableName) {
        String tempIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                tempIDcol = "DUPLICATED_MAX_TEMP_ID";
                break;
            }
            case "DirObjTmpRegIntVal": {
                tempIDcol = "DUPLICATED_MAX_TEMP_ID";
                break;
            }
            case "DirObjTmpArrStrVal": {
                tempIDcol = "DUPLICATED_MAX_TEMP_ID";
                break;
            }
        }
        return tempIDcol;
    }
    
    public static String getCollIDcol(final String tableName) {
        String collIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                collIDcol = "COLLATION_ID";
                break;
            }
            case "DirObjTmpRegIntVal": {
                collIDcol = "COLLATION_ID";
                break;
            }
            case "DirObjTmpArrStrVal": {
                collIDcol = "COLLATION_ID";
                break;
            }
        }
        return collIDcol;
    }
    
    public static String getInvalidcol(final String tableName) {
        String isInvalidCol = null;
        switch (tableName) {
            case "DirObjTmp": {
                isInvalidCol = "IS_INVALID";
                break;
            }
            case "DirObjTmpRegStrVal": {
                isInvalidCol = "IS_INVALID";
                break;
            }
            case "DirObjTmpRegIntVal": {
                isInvalidCol = "IS_INVALID";
                break;
            }
            case "DirObjTmpArrStrVal": {
                isInvalidCol = "IS_INVALID";
                break;
            }
        }
        return isInvalidCol;
    }
    
    public static String getDomainIDcol(final String tableName) {
        String domainIDcol = null;
        switch (tableName) {
            case "DirObjTmpRegStrVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
            case "DirObjTmpRegIntVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
            case "DirObjTmpArrStrVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
            case "DirObjRegIntVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
            case "DirObjRegStrVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
            case "DirObjArrLngVal": {
                domainIDcol = "DM_DOMAIN_ID";
                break;
            }
        }
        return domainIDcol;
    }
    
    public static String getResIDcol(final String tableName) {
        switch (tableName) {
            case "DirObjRegIntVal": {
                return "RESOURCE_ID";
            }
            case "DirObjRegStrVal": {
                return "RESOURCE_ID";
            }
            case "DirObjArrLngVal": {
                return "RESOURCE_ID";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getDirResTypecol(final String tableName) {
        switch (tableName) {
            case "DirObjRegIntVal": {
                return "DIR_RESOURCE_TYPE";
            }
            case "DirObjRegStrVal": {
                return "DIR_RESOURCE_TYPE";
            }
            case "DirObjArrLngVal": {
                return "DIR_RESOURCE_TYPE";
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        TIME_OUT = new int[] { 20, 295, 295, 295, 295 };
        AUTO_VA_DISABLED_TABLES = getAutoVADisabledTables();
        DirectoryQueryutil.directoryQueryutil = null;
    }
    
    private class QueryExecMeta
    {
        private long startedAt;
        private long finishedAt;
        private String execQuery;
        private int affectedRows;
        private List<Long> duration;
        private List<String> tableNames;
        private String affectedTableName;
        private List<Integer> durationType;
        private static final int PLAN = 1;
        private static final int VA = 2;
        private static final int QE = 3;
        
        private QueryExecMeta() {
        }
        
        private QueryExecMeta(final String affectedTableName, final String execQuery, final List<String> tableNames) {
            this.startedAt = -1L;
            this.finishedAt = -1L;
            this.affectedRows = -1;
            this.execQuery = execQuery;
            this.tableNames = tableNames;
            this.duration = new ArrayList<Long>();
            this.durationType = new ArrayList<Integer>();
            this.affectedTableName = affectedTableName;
        }
        
        private void addDurationLog(final long duration, final int queryExecType) {
            this.duration.add(duration);
            this.durationType.add(queryExecType);
        }
        
        private String getQueryExecTypeStr(final int queryExcType) {
            switch (queryExcType) {
                case 1: {
                    return "PLAN:";
                }
                case 2: {
                    return "VA:";
                }
                case 3: {
                    return "QE";
                }
                default: {
                    return "";
                }
            }
        }
        
        private String getDurationLog() {
            final StringBuilder strB = new StringBuilder();
            for (int N = this.duration.size(), i = 0; i < N; ++i) {
                final int queryExcType = this.durationType.get(i);
                final long durationInMillis = this.duration.get(i);
                strB.append(this.getQueryExecTypeStr(queryExcType));
                strB.append(DirectoryUtil.getInstance().formatDurationMS(durationInMillis));
                strB.append(", ");
            }
            if (this.finishedAt != -1L && this.startedAt != -1L) {
                strB.append("T:");
                strB.append(DirectoryUtil.getInstance().formatDurationMS(this.finishedAt - this.startedAt));
            }
            return strB.toString();
        }
        
        private String getExecQuery() {
            return this.execQuery;
        }
        
        private void setExecQuery(final String execQuery) {
            this.execQuery = execQuery;
        }
        
        private List<String> getTableNames() {
            return this.tableNames;
        }
        
        private int getAffectedRows() {
            return this.affectedRows;
        }
        
        private void setAffectedRows(final int affectedRows) {
            this.affectedRows = affectedRows;
        }
        
        @Override
        public String toString() {
            final JSONObject queryDetails = new JSONObject();
            if (this.affectedRows == -1) {
                queryDetails.put((Object)"QUERY", (Object)this.execQuery);
            }
            queryDetails.put((Object)"DURATION", (Object)this.getDurationLog());
            queryDetails.put((Object)"AFFECTED_ROWS", (Object)this.affectedRows);
            queryDetails.put((Object)"STATUS", (Object)((this.affectedRows == -1) ? "Executing" : "Executed"));
            return queryDetails.toJSONString();
        }
        
        public void start() {
            if (this.startedAt == -1L) {
                this.startedAt = System.currentTimeMillis();
            }
        }
        
        public void finish() {
            this.finishedAt = System.currentTimeMillis();
        }
    }
}
