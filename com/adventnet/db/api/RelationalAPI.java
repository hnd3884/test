package com.adventnet.db.api;

import com.adventnet.mfw.ConsoleOut;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.File;
import com.adventnet.db.adapter.DBCrashHandler;
import com.zoho.conf.Configuration;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.ds.ResultSetHandler;
import com.adventnet.ds.adapter.mds.DBThreadLocal;
import java.util.TimerTask;
import java.util.Timer;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.ArchiveTable;
import com.zoho.conf.AppResources;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DerivedTable;
import java.sql.ResultSet;
import com.adventnet.db.util.SelectQueryStringUtil;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.archive.TableArchiverUtil;
import java.util.Collection;
import com.adventnet.db.adapter.ResultSetAdapter;
import com.adventnet.ds.query.DataSet;
import java.sql.PreparedStatement;
import java.util.Iterator;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.UpdateQuery;
import java.util.List;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Map;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.db.archive.ArchiveAdapter;
import com.adventnet.db.adapter.SQLGenerator;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;

public class RelationalAPI
{
    private static DataSource dataSource;
    private static RelationalAPI relapi;
    private static final boolean haltjvm;
    private DBAdapter dbAdapter;
    private String dbCrashHandlerClassName;
    private boolean isStream;
    private SQLGenerator sqlGen;
    private ArchiveAdapter arcAdapter;
    private static Logger miscErr;
    private static String server_home;
    private boolean checkForDBAlive;
    
    public RelationalAPI(final DBAdapter dbadapter, final DataSource dataSource, final ArchiveAdapter archiveAdapter, final Properties confProps) {
        this.dbAdapter = null;
        this.dbCrashHandlerClassName = null;
        this.isStream = true;
        this.sqlGen = null;
        this.arcAdapter = null;
        this.checkForDBAlive = false;
        this.dbAdapter = dbadapter;
        RelationalAPI.dataSource = dataSource;
        RelationalAPI.relapi = this;
        this.arcAdapter = archiveAdapter;
        this.checkForDBAlive = (dbadapter.getShutDownStrings().size() > 0);
        this.isStream = Boolean.parseBoolean(confProps.getProperty("StreamingResultSet"));
    }
    
    public DBAdapter getDBAdapter() {
        return this.dbAdapter;
    }
    
    public ArchiveAdapter getArchiveAdapter() {
        return this.arcAdapter;
    }
    
    public void setDBCrashHandlerClassName(final String dbchClassName) {
        this.dbCrashHandlerClassName = dbchClassName;
    }
    
    public String getDBCrashHandlerClassName() {
        return this.dbCrashHandlerClassName;
    }
    
    public static RelationalAPI getInstance() {
        return RelationalAPI.relapi;
    }
    
    private void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception e) {
            RelationalAPI.miscErr.log(Level.FINEST, "Exception occured while closing a connection", e);
        }
    }
    
    private void closeStatement(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (final Exception e) {
            RelationalAPI.miscErr.log(Level.FINEST, "Exception occured while closing a statement", e);
        }
    }
    
    public DataSource getDataSource() {
        return RelationalAPI.dataSource;
    }
    
    public Connection getConnection() throws SQLException {
        return RelationalAPI.dataSource.getConnection();
    }
    
    public String getInsertSQL(final String tableName, final Map values) throws QueryConstructionException {
        return this.dbAdapter.getSQLGenerator().getSQLForInsert(tableName, values);
    }
    
    public String getUpdateSQL(final String tableName, final Map newValues, final Criteria criteria) throws QueryConstructionException {
        final List tabList = new ArrayList();
        tabList.add(Table.getTable(tableName));
        QueryUtil.setTypeForUpdateColumns(tabList, newValues, criteria);
        return this.dbAdapter.getSQLGenerator().getSQLForUpdate(tableName, newValues, criteria);
    }
    
    public String getUpdateSQL(final UpdateQuery query) throws QueryConstructionException {
        QueryUtil.setDataTypeForUpdateQuery(query);
        return this.dbAdapter.getSQLGenerator().getSQLForUpdate(query.getTableList(), query.getUpdateColumns(), query.getCriteria(), query.getJoins());
    }
    
    public String getDeleteSQL(final String tableName, final Criteria criteria) throws QueryConstructionException {
        final List tabList = new ArrayList();
        tabList.add(Table.getTable(tableName));
        QueryUtil.setTypeForCriteria(criteria, tabList);
        return this.dbAdapter.getSQLGenerator().getSQLForDelete(tableName, criteria);
    }
    
    public String getDeleteSQL(final DeleteQuery query) throws QueryConstructionException {
        QueryUtil.setDataTypeForDeleteQuery(query);
        return this.dbAdapter.getSQLGenerator().getSQLForDelete(query);
    }
    
    public String getSelectSQL(final Query query) throws QueryConstructionException {
        this.setDataType(query);
        final Query modified = this.getModifiedQuery((Query)query.clone());
        if (!PersistenceInitializer.onSAS()) {
            this.setDataType(modified);
        }
        return this.getSQLString(modified);
    }
    
    private String getSQLString(final Query query) throws QueryConstructionException {
        return this.dbAdapter.getSQLGenerator().getSQLForSelect(query);
    }
    
    public String getGroupBySQLString(final String[] columns, final Criteria havingCriteria) throws QueryConstructionException {
        if (this.dbAdapter == null) {
            throw new QueryConstructionException("DB Adapter is not initialized");
        }
        return this.dbAdapter.getSQLGenerator().formGroupByString(columns, havingCriteria);
    }
    
    public String getOrderBySQLString(final String[] columns, final boolean[] isAscending) throws QueryConstructionException {
        if (this.dbAdapter == null) {
            throw new QueryConstructionException("DB Adapter is not initialized");
        }
        return this.dbAdapter.getSQLGenerator().formOrderByString(columns, isAscending);
    }
    
    public String getCreateTableSQL(final TableDefinition tabDefn) throws QueryConstructionException {
        return this.dbAdapter.getSQLGenerator().getSQLForCreateTable(tabDefn);
    }
    
    public void dropTable(final String tableName, final boolean cascade, final List relatedTables) throws SQLException {
        final TableDefinition td = getTableDefinition(tableName);
        if (td != null && !td.creatable()) {
            RelationalAPI.miscErr.log(Level.INFO, "Not dropping the table :: [{0}], since it is specified as CREATABLE false.", tableName);
            return;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            this.dbAdapter.dropTable(stmt, tableName, cascade, relatedTables);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(conn);
        }
    }
    
    public void truncateTable(final String tableName) throws SQLException {
        final TableDefinition td = getTableDefinition(tableName);
        if (td != null && !td.creatable()) {
            RelationalAPI.miscErr.log(Level.INFO, "Not Truncating the table :: [{0}], since it is specified as CREATABLE false.", tableName);
            return;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            this.dbAdapter.truncateTable(stmt, tableName);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(conn);
        }
    }
    
    public List getTables(final String schemaName) throws SQLException {
        Connection conn = null;
        List tableList = null;
        try {
            conn = this.getConnection();
            tableList = this.dbAdapter.getTables(conn, schemaName);
        }
        finally {
            this.closeConnection(conn);
        }
        return tableList;
    }
    
    public List getTables() throws SQLException {
        return this.getTables(null);
    }
    
    public void createTable(final String createSQL, final List relatedTables) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            this.dbAdapter.createTable(stmt, createSQL, relatedTables);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(conn);
        }
    }
    
    public void createTables(final String schemaName, final List tableDefn, final List tablesPresent) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            this.dbAdapter.createTables(stmt, schemaName, tableDefn, tablesPresent);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(conn);
        }
    }
    
    public void createTable(final TableDefinition tabDefn, final List relatedTables) throws SQLException {
        this.createTable(tabDefn, null, relatedTables);
    }
    
    public void createTable(final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        if (!tabDefn.creatable()) {
            RelationalAPI.miscErr.log(Level.INFO, "Not creating the table :: [{0}], since it is specified as CREATABLE false.", tabDefn.getTableName());
            return;
        }
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            this.dbAdapter.createTable(stmt, tabDefn, createTableOptions, relatedTables);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(conn);
        }
    }
    
    public void alterTable(final AlterTableQuery alterTableQuery) throws SQLException {
        Connection connection = null;
        Statement stmt = null;
        try {
            if (alterTableQuery.getAlterOperations().size() != 1) {
                for (final AlterOperation ao : alterTableQuery.getAlterOperations()) {
                    if (ao.getOperationType() == 19 || ao.getOperationType() == 20 || ao.getOperationType() == 21 || ao.getOperationType() == 22) {
                        throw new IllegalArgumentException("Only one alter operation is allowed for dynamic columns");
                    }
                }
            }
            connection = this.getConnection();
            stmt = connection.createStatement();
            final TableDefinition td = getTableDefinition(alterTableQuery.getTableName());
            if (alterTableQuery.getAlterOperations().get(0).getOperationType() != 18 && td != null && !td.creatable() && !this.dbAdapter.isTablePresentInDB(connection, null, alterTableQuery.getTableName())) {
                RelationalAPI.miscErr.info("Not Altering the table :: [" + td.getTableName() + "], since it is specified as CREATABLE false and table not exists in DB.");
                return;
            }
            this.dbAdapter.alterTable(connection, alterTableQuery);
        }
        finally {
            this.closeStatement(stmt);
            this.closeConnection(connection);
        }
    }
    
    public DataSet executeQuery(final PreparedStatement prstmt, final List selectColumns) throws SQLException {
        DataSet ds = null;
        if (prstmt == null) {
            throw new SQLException("PreparedStatement is null.");
        }
        final ResultSetAdapter rs = this.dbAdapter.executeQuery(prstmt);
        ds = new DataSet(rs, selectColumns);
        return ds;
    }
    
    public DataSet executeQuery(final PreparedStatement prstmt, final Query query) throws SQLException {
        try {
            this.setDataType(query);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
        DataSet ds = null;
        if (prstmt == null) {
            throw new SQLException("PreparedStatement is null.");
        }
        final ResultSetAdapter rs = this.dbAdapter.executeQuery(prstmt);
        try {
            ds = new DataSet(rs, query, getSelectColumns(query), null);
        }
        catch (final QueryConstructionException excp2) {
            throw new SQLException(excp2.getMessage());
        }
        return ds;
    }
    
    public DataSet executeReadOnlyStatement(final Connection connection, final String query) throws SQLException, QueryConstructionException {
        if (FilterReadOnlyQueries.isReadOnly(query)) {
            return this.executeQuery(query, connection);
        }
        return null;
    }
    
    public int executeUpdate(final PreparedStatement prstmt) throws SQLException {
        int result = 0;
        if (prstmt == null) {
            throw new SQLException("preparedstatement is null");
        }
        result = this.dbAdapter.executeUpdate(prstmt);
        return result;
    }
    
    public void execute(final String sqlString) throws SQLException {
        Connection con = null;
        try {
            try {
                con = RelationalAPI.dataSource.getConnection();
            }
            catch (final Exception e) {
                throw new SQLException("Exception while fetching connection :" + e.getMessage(), e);
            }
            if (con == null) {
                RelationalAPI.miscErr.log(Level.WARNING, "Connection object is null");
                throw new SQLException("Connection object is null");
            }
            this.execute(con, sqlString);
        }
        finally {
            this.closeConnection(con);
        }
    }
    
    public void execute(final Connection conn, final String sqlString) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            this.dbAdapter.execute(stmt, sqlString);
        }
        finally {
            this.closeStatement(stmt);
        }
    }
    
    public boolean execute(final Connection connection, final String query, final Object... args) throws SQLException {
        try (final PreparedStatement ps = this.dbAdapter.createPreparedStatement(connection, query, args)) {
            return this.dbAdapter.execute(ps);
        }
    }
    
    public int[] executeBatch(final PreparedStatement prstmt) throws SQLException {
        return this.dbAdapter.executeBatch(prstmt);
    }
    
    public int[] executeBatch(final Connection connection, final String query, final Collection<Object[]> args) throws SQLException {
        return this.dbAdapter.executeBatch(connection, query, args);
    }
    
    public int[] executeBatch(final Statement stmt) throws SQLException {
        return this.dbAdapter.executeBatch(stmt);
    }
    
    private SQLException handleException(final SQLException sqle, final Connection conn) throws SQLException {
        return this.dbAdapter.handleSQLException(sqle, conn, true);
    }
    
    public DataSet executeQuery(final Query query, final Connection conn, final String selectSQL) throws SQLException {
        final Statement stmt = this.dbAdapter.createStatement(conn);
        return this.executeQuery(query, stmt, selectSQL);
    }
    
    private DataSet executeQuery(final Query query, final Statement stmt, final String selectSQL) throws SQLException {
        try {
            this.setDataType(query);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
        final ResultSetAdapter rs = this.dbAdapter.executeQuery(stmt, selectSQL);
        DataSet ds = null;
        try {
            ds = new DataSet(rs, query, getSelectColumns(query), stmt);
        }
        catch (final QueryConstructionException excp2) {
            throw new SQLException(excp2.getMessage());
        }
        return ds;
    }
    
    public DataSet executeQuery(final Connection connection, final String query, final Object... args) throws SQLException {
        final PreparedStatement ps = this.dbAdapter.createPreparedStatement(connection, query, args);
        final ResultSetAdapter rs = this.dbAdapter.executeQuery(ps);
        return new DataSet(rs, ps);
    }
    
    public int executeUpdate(final Connection connection, final String query, final Object... args) throws SQLException {
        try (final PreparedStatement ps = this.dbAdapter.createPreparedStatement(connection, query, args)) {
            return this.executeUpdate(ps);
        }
    }
    
    public DataSet executeQuery(final Query query, final Connection conn) throws SQLException, QueryConstructionException {
        this.setDataType(query);
        final Query modQuery = this.getModifiedQuery((Query)query.clone());
        this.setDataType(modQuery);
        final Statement stmt = this.dbAdapter.createStatement(conn);
        final String selectSQL = this.getSQLString(modQuery);
        try {
            final ResultSetAdapter rs = this.dbAdapter.executeQuery(stmt, selectSQL);
            return new DataSet(rs, modQuery, getSelectColumns(modQuery), stmt);
        }
        catch (final SQLException sqle) {
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    if (query != null && query instanceof SelectQuery) {
                        this.getArchiveAdapter().restoreUnArchivedInvisibleTable(((SelectQuery)query).getTableList(), conn, sqle);
                    }
                    else if (query != null && query instanceof UnionQuery) {
                        this.getArchiveAdapter().restoreUnArchivedInvisibleTable(((UnionQuery)query).getTableList(), conn, sqle);
                    }
                }
            }
            catch (final Exception e) {
                RelationalAPI.miscErr.info("Exception from Archive handling...");
                e.printStackTrace();
            }
            throw sqle;
        }
    }
    
    public DataSet executeQuery(final Query query, final Connection conn, final int fetchsize) throws SQLException, QueryConstructionException {
        this.setDataType(query);
        final Query modQuery = this.getModifiedQuery((Query)query.clone());
        this.setDataType(modQuery);
        final String selectSQL = this.getSQLString(modQuery);
        if (this.isStream) {
            return this.executeQuery(modQuery, conn, fetchsize, selectSQL);
        }
        return this.executeQuery(modQuery, conn, selectSQL);
    }
    
    @Deprecated
    public DataSet executeReadOnlyQuery(final Query query, final Connection conn) throws SQLException, QueryConstructionException {
        return this.executeQuery(query, conn, 0);
    }
    
    private DataSet executeQuery(final Query query, final Connection conn, final int fetchsize, final String selectSQL) throws SQLException, QueryConstructionException {
        if (selectSQL == null) {
            throw new QueryConstructionException("SQL String cannot be NULL");
        }
        final Statement stmt = this.dbAdapter.createStatement(conn, fetchsize);
        ResultSetAdapter rs = null;
        try {
            if (conn.getAutoCommit()) {
                throw new QueryConstructionException("Please start a transaction before excuting the query");
            }
            rs = this.dbAdapter.executeQuery(stmt, selectSQL);
        }
        catch (final SQLException sqle) {
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    if (query instanceof SelectQuery) {
                        this.getArchiveAdapter().restoreUnArchivedInvisibleTable(((SelectQuery)query).getTableList(), conn, sqle);
                    }
                    else if (query instanceof UnionQuery) {
                        this.getArchiveAdapter().restoreUnArchivedInvisibleTable(((UnionQuery)query).getTableList(), conn, sqle);
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            throw sqle;
        }
        if (query != null) {
            return new DataSet(rs, query, getSelectColumns(query), stmt);
        }
        return new DataSet(rs, stmt);
    }
    
    private DataSet executeQueryusingPreparedStatement(final Connection connection, final int fetchsize, final String selectSQL, final Object... args) throws SQLException, QueryConstructionException {
        if (selectSQL == null) {
            throw new QueryConstructionException("SQL String cannot be NULL");
        }
        PreparedStatement ps = null;
        ResultSetAdapter rs = null;
        try {
            if (!connection.getAutoCommit()) {
                ps = this.dbAdapter.createPreparedStatement(connection, fetchsize, selectSQL, args);
                rs = this.dbAdapter.executeQuery(ps);
                return new DataSet(rs, ps);
            }
            throw new QueryConstructionException("Please start a transaction before excuting the query");
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
    }
    
    public DataSet executeQueryWithKey(final String queryKey, final Connection conn) throws SQLException, QueryConstructionException {
        final String sql = SelectQueryStringUtil.replaceAllTemplatesForKey(queryKey);
        return this.executeQuery(sql, conn);
    }
    
    public DataSet executeQueryWithKey(final String queryKey, final Object[] templateValues, final Connection conn) throws SQLException, QueryConstructionException {
        final String sql = SelectQueryStringUtil.replaceAllTemplatesForKey(queryKey, templateValues);
        return this.executeQuery(sql, conn);
    }
    
    public DataSet executeQueryWithKey(final String queryKey, final Map templateValues, final Connection conn) throws SQLException, QueryConstructionException {
        this.dbAdapter.getSQLGenerator().fillUserDataRange(templateValues);
        final String sql = SelectQueryStringUtil.replaceAllTemplatesForKey(queryKey, templateValues);
        return this.executeQuery(sql, conn);
    }
    
    @Deprecated
    public ResultSet executeQueryForSQL(final String sql, final Map templateValues, final Connection conn) throws SQLException, QueryConstructionException {
        final Statement stmt = conn.createStatement();
        return this.executeQueryForSQL(sql, templateValues, stmt);
    }
    
    public ResultSet executeQueryForSQL(final String sql, final Map templateValues, final Statement stmt) throws SQLException, QueryConstructionException {
        this.dbAdapter.getSQLGenerator().fillUserDataRange(templateValues);
        final String sqlString = SelectQueryStringUtil.replaceAllTemplatesForSQL(sql, templateValues);
        return stmt.executeQuery(sqlString);
    }
    
    public DataSet executeQuery(final String sql, final Connection conn) throws SQLException, QueryConstructionException {
        if (sql == null) {
            throw new QueryConstructionException("SQL String cannot be NULL");
        }
        RelationalAPI.miscErr.log(Level.FINE, "Select Query to be executed: " + sql);
        final Statement stmt = this.dbAdapter.createStatement(conn);
        final ResultSetAdapter rs = this.dbAdapter.executeQuery(stmt, sql);
        return new DataSet(rs, stmt);
    }
    
    public DataSet executeQuery(final String sql, final Connection conn, final int fetchsize) throws SQLException, QueryConstructionException {
        if (this.isStream) {
            return this.executeQuery(null, conn, fetchsize, sql);
        }
        return this.executeQuery(sql, conn);
    }
    
    @Deprecated
    public DataSet executeReadOnlyQuery(final String sql, final Connection conn) throws SQLException, QueryConstructionException {
        return this.executeQuery(sql, conn, 0);
    }
    
    public DataSet executeQuery(final Connection connection, final int fetchsize, final String query, final Object... args) throws SQLException, QueryConstructionException {
        if (this.isStream) {
            return this.executeQueryusingPreparedStatement(connection, fetchsize, query, args);
        }
        return this.executeQuery(connection, query, args);
    }
    
    public Query getModifiedQuery(final Query query) throws QueryConstructionException {
        if (query instanceof UnionQuery) {
            final UnionQuery current = (UnionQuery)query;
            final Query leftQuery = current.getLeftQuery();
            final Query rightQuery = current.getRightQuery();
            if (leftQuery != null) {
                this.getModifiedQuery(leftQuery);
            }
            if (rightQuery != null) {
                this.getModifiedQuery(rightQuery);
            }
        }
        else if (query instanceof SelectQuery) {
            final SelectQuery currentSelect = (SelectQuery)query;
            final List selectCols = currentSelect.getSelectColumns();
            final List tempCols = new ArrayList();
            final int selSize = selectCols.size();
            final List tableList = currentSelect.getTableList();
            int colIndex = -1;
            for (int i = 0; i < tableList.size(); ++i) {
                final Table selectTable = tableList.get(i);
                if (selectTable instanceof DerivedTable) {
                    final DerivedTable subQTable = (DerivedTable)selectTable;
                    if (subQTable.getSubQuery() instanceof SelectQuery && currentSelect.isParallelSelect()) {
                        ((SelectQuery)subQTable.getSubQuery()).setParallelSelect(true, -1);
                    }
                    this.getModifiedQuery(subQTable.getSubQuery());
                }
            }
            for (int i = 0; i < selSize; ++i) {
                final Column col = selectCols.get(i);
                if (col instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)col;
                    if (dc.getSubQuery() instanceof SelectQuery && currentSelect.isParallelSelect()) {
                        ((SelectQuery)dc.getSubQuery()).setParallelSelect(true, -1);
                    }
                }
                if (col.getColumnName() == null) {
                    tempCols.add(col);
                }
                else if (col.getColumnName().equals("*")) {
                    if (col.getTableAlias() == null && selSize > 1) {
                        throw new QueryConstructionException("When the table alias is specified as null in a column, then there should be only one column added as select column in select query");
                    }
                    final List currList = getRespectiveColList(col, tableList);
                    if (currList != null) {
                        colIndex = tempCols.indexOf(col);
                        if (colIndex == -1) {
                            colIndex = tempCols.size();
                        }
                        currentSelect.removeSelectColumn(col);
                        currentSelect.addSelectColumns(currList, colIndex);
                        tempCols.addAll(currList);
                    }
                }
                else {
                    tempCols.add(col);
                }
            }
            if (AppResources.getString("process.removecolumn.instantly", "true").equalsIgnoreCase("false")) {
                currentSelect.processRemoveColumns();
            }
        }
        return query;
    }
    
    public static List getSelectColumns(final Query query) throws QueryConstructionException {
        final Query current = query;
        final List retColumns = new ArrayList();
        List selectCols = null;
        List tableList = null;
        if (current instanceof UnionQuery) {
            selectCols = ((UnionQuery)current).getSelectColumns();
            tableList = ((UnionQuery)current).getTableList();
        }
        else if (current instanceof SelectQuery) {
            selectCols = ((SelectQuery)current).getSelectColumns();
            tableList = ((SelectQuery)current).getTableList();
        }
        for (int selSize = selectCols.size(), i = 0; i < selSize; ++i) {
            final Column currCol = selectCols.get(i);
            if (currCol.getColumnName() == null) {
                retColumns.add(currCol);
            }
            else if (currCol.getColumnName().equals("*")) {
                final List respectiveList = getRespectiveColList(currCol, tableList);
                if (respectiveList != null) {
                    retColumns.addAll(respectiveList);
                }
            }
            else {
                retColumns.add(currCol);
            }
        }
        return retColumns;
    }
    
    private static List getRespectiveColList(final Column currCol, final List tableList) throws QueryConstructionException {
        List metaCols = null;
        if (currCol.getTableAlias() == null) {
            metaCols = getColumnsFromMetaData(tableList);
            return metaCols;
        }
        final List singleTabList = new ArrayList();
        singleTabList.add(getTable(tableList, currCol));
        metaCols = getColumnsFromMetaData(singleTabList);
        return metaCols;
    }
    
    private static List getColumnsFromMetaData(final List tabList) throws QueryConstructionException {
        final int tabSize = tabList.size();
        final List list = new ArrayList();
        for (int i = 0; i < tabSize; ++i) {
            final Table table = tabList.get(i);
            if (table instanceof DerivedTable) {
                final List subqueryColumns = getSelectColumns(((DerivedTable)table).getSubQuery());
                final List newColsList = new ArrayList(subqueryColumns.size());
                for (int j = 0; j < subqueryColumns.size(); ++j) {
                    final Column subqCol = subqueryColumns.get(j);
                    String subqColAlias = subqCol.getColumnAlias();
                    if (subqColAlias == null) {
                        subqColAlias = subqCol.getColumnName();
                    }
                    final Column newCol = new Column(table.getTableAlias(), subqColAlias);
                    newColsList.add(newCol);
                }
                list.addAll(newColsList);
            }
            else {
                final TableDefinition tabDefn = getTableDefinition(table.getTableName());
                if (tabDefn == null) {
                    throw new IllegalArgumentException("Check whether the tablename [" + table.getTableName() + "] is correct or the respective data-dictionary has been loaded.");
                }
                final List colList = tabDefn.getColumnList();
                processColumnDefn(colList, list, table);
            }
        }
        if (list.size() > 0) {
            return list;
        }
        return null;
    }
    
    private static void processColumnDefn(final List colList, final List retList, final Table table) {
        String tableAlias = null;
        if (table instanceof ArchiveTable) {
            tableAlias = ((ArchiveTable)table).getArchiveTableAlias();
        }
        else {
            tableAlias = table.getTableAlias();
        }
        for (int colSize = colList.size(), i = 0; i < colSize; ++i) {
            final ColumnDefinition currentDefn = colList.get(i);
            final String columnName = currentDefn.getColumnName();
            if (columnName != null) {
                retList.add(Column.getColumn(tableAlias, columnName));
            }
        }
    }
    
    public void setDataType(final Query query) throws QueryConstructionException {
        QueryUtil.setDataType(query);
    }
    
    private static Table getTable(final List tableList, final Column column) throws QueryConstructionException {
        final String tableAlias = column.getTableAlias();
        for (int tableSize = tableList.size(), i = 0; i < tableSize; ++i) {
            final Table table = tableList.get(i);
            if (table.getTableAlias().equals(tableAlias)) {
                return table;
            }
            if (table instanceof ArchiveTable && ((ArchiveTable)table).getArchiveTableAlias().equals(tableAlias)) {
                return table;
            }
        }
        throw new QueryConstructionException("Table alias " + column.getTableAlias() + " specified in Column " + column + " is invalid");
    }
    
    private static TableDefinition getTableDefinition(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new RuntimeException(mde.getMessage(), mde);
        }
    }
    
    public void logAndHalt() {
        this.logAndHalt(0);
    }
    
    public void logAndHalt(final int seconds) {
        if (seconds <= 0) {
            new HaltTask().run();
        }
        new Timer().schedule(new HaltTask(), seconds * 1000);
    }
    
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, final Object value) throws SQLException {
        this.dbAdapter.setValue(ps, columnIndex, sqlType, value);
    }
    
    public void createTables(final String dataSourceName, final List tableNames) throws QueryConstructionException, SQLException {
        DBThreadLocal.set(dataSourceName);
        this.createTables(tableNames);
    }
    
    private void createTables(final List tableNames) throws QueryConstructionException, SQLException {
        Connection c = null;
        try {
            c = this.getConnection();
            for (final String tableName : tableNames) {
                try {
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                    this.createTable(td, new ArrayList());
                }
                catch (final MetaDataException e) {
                    throw new QueryConstructionException("No Such tableName :: [" + tableName + "]");
                }
            }
        }
        finally {
            this.closeConnection(c);
        }
    }
    
    public void createTables(final List dataSourceNames, final List tableNames) throws QueryConstructionException, SQLException {
        for (final String dataSourceName : dataSourceNames) {
            this.createTables(dataSourceName, tableNames);
        }
    }
    
    public PreparedStatement createInsertStatement(final String tableName, final Connection conn) throws MetaDataException, QueryConstructionException, SQLException {
        return this.dbAdapter.createInsertStatement(tableName, conn);
    }
    
    public PreparedStatement createUpdateStatement(final String tableName, final int[] changedIndexes, final Connection conn) throws MetaDataException, QueryConstructionException, SQLException {
        return this.dbAdapter.createUpdateStatement(tableName, changedIndexes, conn);
    }
    
    public PreparedStatement createUpdateStatement(final UpdateQuery query, final Connection conn) throws MetaDataException, QueryConstructionException, SQLException {
        QueryUtil.setDataTypeForUpdateQuery(query);
        return this.dbAdapter.createUpdateStatement(query, conn);
    }
    
    public boolean executeDelete(final String tableName, final Criteria cri, final Connection conn) throws SQLException, QueryConstructionException {
        return this.dbAdapter.executeDelete(tableName, cri, conn);
    }
    
    public <T extends ResultSetHandler> T executeQuery(final Class<T> rsHandlerClass, final String pStmtSql, final Connection conn, final Object... values) throws SQLException {
        PreparedStatement pStmt = null;
        ResultSetAdapter rsAdapter = null;
        if (null == conn) {
            RelationalAPI.miscErr.warning("Expecting connection!!! Returning null.");
            return null;
        }
        try {
            pStmt = conn.prepareStatement(pStmtSql);
            for (int i = 0; i < values.length; ++i) {
                pStmt.setObject(i + 1, values[i]);
            }
            rsAdapter = this.dbAdapter.executeQuery(pStmt);
            final ResultSetHandler rsHandler = this.getResultSetHandlerObj(rsHandlerClass);
            rsHandler.populate(rsAdapter.getResultSet());
            return (T)rsHandler;
        }
        finally {
            try {
                if (rsAdapter != null) {
                    rsAdapter.close();
                }
            }
            catch (final Exception exp) {
                RelationalAPI.miscErr.log(Level.SEVERE, "Exception while Execute query", exp);
            }
            PersistenceUtil.safeClose(pStmt);
        }
    }
    
    public <T extends ResultSetHandler> T executeQuery(final Class<T> rsHandlerClass, final String pStmtSql, final Object... values) throws SQLException {
        Connection conn = null;
        try {
            conn = this.getConnection();
            return this.executeQuery(rsHandlerClass, pStmtSql, conn, values);
        }
        finally {
            PersistenceUtil.safeClose(conn);
        }
    }
    
    private ResultSetHandler getResultSetHandlerObj(final Class rsHandlerClass) throws SQLException {
        try {
            final Object rsHandlerObj = rsHandlerClass.newInstance();
            if (rsHandlerObj instanceof ResultSetHandler) {
                return (ResultSetHandler)rsHandlerObj;
            }
            final IllegalArgumentException rootExp = new IllegalArgumentException(rsHandlerClass.getName() + " should implement the interface " + ResultSetHandler.class.getName());
            final SQLException sqlExp = new SQLException(rootExp.getMessage(), null, -9999);
            sqlExp.initCause(rootExp);
            throw sqlExp;
        }
        catch (final InstantiationException ex) {
            final SQLException sqlExp2 = new SQLException(ex.getMessage(), null, -9999);
            sqlExp2.initCause(ex);
            throw sqlExp2;
        }
        catch (final IllegalAccessException ex2) {
            final SQLException sqlExp2 = new SQLException(ex2.getMessage(), null, -9999);
            sqlExp2.initCause(ex2);
            throw sqlExp2;
        }
    }
    
    public int executeUpdate(final String pStmtSql, final Object... values) throws SQLException {
        try (final Connection conn = this.getConnection()) {
            return this.executeUpdate(conn, pStmtSql, values);
        }
    }
    
    public void setValue(final PreparedStatement ps, final int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> value, final String dcType) throws SQLException {
        this.dbAdapter.setValue(ps, columnIndex, sqlTypes, value, dcType);
    }
    
    static {
        RelationalAPI.miscErr = Logger.getLogger(RelationalAPI.class.getName());
        RelationalAPI.server_home = ((Configuration.getString("server.home", ".") != null) ? Configuration.getString("server.home", ".") : Configuration.getString("app.home", "."));
        haltjvm = Boolean.getBoolean("haltjvm.on.dbcrash");
        RelationalAPI.miscErr.log(Level.SEVERE, "haltjvm.on.dbcrash is set to [{0}]", RelationalAPI.haltjvm);
    }
    
    class HaltTask extends TimerTask
    {
        @Override
        public void run() {
            Connection c = null;
            Statement s = null;
            ResultSet rs = null;
            try {
                c = RelationalAPI.this.getConnection();
                s = c.createStatement();
                rs = s.executeQuery("SELECT 1");
                if (rs.next()) {
                    RelationalAPI.miscErr.log(Level.INFO, "DBConnection has been restored, hence not halting !!!!");
                    return;
                }
            }
            catch (final SQLException e) {
                RelationalAPI.miscErr.log(Level.INFO, "DBConnection is still not restored, hence going to halt the JVM");
                e.printStackTrace();
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            if (RelationalAPI.this.dbCrashHandlerClassName != null && !RelationalAPI.this.dbCrashHandlerClassName.trim().equals("")) {
                try {
                    final DBCrashHandler dbch = (DBCrashHandler)Thread.currentThread().getContextClassLoader().loadClass(RelationalAPI.this.dbCrashHandlerClassName).newInstance();
                    RelationalAPI.miscErr.log(Level.SEVERE, "Going to call preHandleForSystemHalt of the DBCrashHandler [{0}]", RelationalAPI.this.dbCrashHandlerClassName);
                    dbch.preHandleForSystemHalt();
                    RelationalAPI.miscErr.log(Level.SEVERE, "Finished invoking preHandleForSystemHalt of the DBCrashHandler [{0}]", RelationalAPI.this.dbCrashHandlerClassName);
                }
                catch (final Exception e3) {
                    e3.printStackTrace();
                }
            }
            if (!RelationalAPI.haltjvm) {
                return;
            }
            int haltCode = 1978;
            try {
                haltCode = AppResources.getInteger("abnormal.exitcode", Integer.valueOf(1978));
            }
            catch (final Exception e4) {
                e4.printStackTrace();
            }
            RelationalAPI.miscErr.log(Level.SEVERE, "");
            RelationalAPI.miscErr.log(Level.SEVERE, "DB is not running ...");
            RelationalAPI.miscErr.log(Level.SEVERE, "");
            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(new File(RelationalAPI.server_home + "/logs/dbcrash.txt"), true);
                final StringBuffer sb = new StringBuffer();
                sb.append("TIME :: " + new Date(System.currentTimeMillis()) + "\n");
                sb.append("Either database is not running or it has been restarted. Hence shutdowning the server with the errorcode :: " + haltCode + " ...\n\n");
                fs.write(sb.toString().getBytes());
            }
            catch (final Exception ee) {
                ConsoleOut.println("Exception occured while writing into dbcrash.txt");
                RelationalAPI.miscErr.log(Level.SEVERE, "Exception occured while writing into dbcrash.txt");
                ee.printStackTrace();
                try {
                    Thread.sleep(5000L);
                }
                catch (final Exception ex) {}
            }
            finally {
                if (fs != null) {
                    try {
                        fs.close();
                    }
                    catch (final Exception ex2) {}
                }
                try {
                    Thread.sleep(5000L);
                }
                catch (final Exception ex3) {}
            }
            Runtime.getRuntime().halt(haltCode);
        }
    }
}
