package com.adventnet.db.migration.report;

import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Column;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.zoho.cp.LogicalConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

final class DBMTableStatus
{
    private static final Logger LOGGER;
    static final String DBM_PROCESS_STATS_TABLENAME = "DBMProcessStats";
    private static String insertSQL;
    private static Map<String, String> updateSQLMap;
    private String tableName;
    private boolean level1Status;
    private boolean level2Status;
    private List<String> level1QueryKeys;
    private List<String> level2QueryKeys;
    private boolean isTableCreated;
    private boolean isTableSkipped;
    private List<String> skippedConstraintNames;
    private boolean isEntryAddedInTable;
    
    public static void initialize() {
        DBMTableStatus.insertSQL = null;
        DBMTableStatus.updateSQLMap = new ConcurrentHashMap<String, String>();
    }
    
    protected DBMTableStatus(final String tabName) {
        this.tableName = null;
        this.level1Status = false;
        this.level2Status = false;
        this.level1QueryKeys = new ArrayList<String>();
        this.level2QueryKeys = new ArrayList<String>();
        this.isTableCreated = false;
        this.isTableSkipped = false;
        this.skippedConstraintNames = new ArrayList<String>();
        this.isEntryAddedInTable = false;
        this.tableName = tabName;
    }
    
    protected String getLevel1CompletedKeys() {
        return this.level1QueryKeys.toString();
    }
    
    protected List<String> getLevel1CompletedKeysList() {
        return this.level1QueryKeys;
    }
    
    protected List<String> getSkippedConstraintNames() {
        return this.skippedConstraintNames;
    }
    
    protected void appendConstraintKeyName(final String querykey) {
        this.skippedConstraintNames.add(querykey);
    }
    
    protected String getSkippedConstraintKeys() {
        return this.skippedConstraintNames.toString();
    }
    
    protected String getLevel2CompletedKeys() {
        return this.level2QueryKeys.toString();
    }
    
    protected List<String> getLevel2CompletedKeysList() {
        return this.level2QueryKeys;
    }
    
    protected void appendLevel1KeyName(final String querykey) {
        this.level1QueryKeys.add(querykey);
    }
    
    protected void appendLevel2KeyName(final String querykey) {
        this.level2QueryKeys.add(querykey);
    }
    
    protected void setLevel1KeyNames(final String fetchedString) {
        this.splitAndAddToList(this.level1QueryKeys, fetchedString);
    }
    
    protected void setLevel2KeyNames(final String fetchedString) {
        this.splitAndAddToList(this.level2QueryKeys, fetchedString);
    }
    
    private void splitAndAddToList(final List<String> list, final String keyString) {
        if (keyString != null) {
            for (final String string : keyString.split("[,\\[\\]]")) {
                list.add(string.trim());
            }
        }
    }
    
    protected void setLevel1Status(final boolean level1Status) {
        this.level1Status = level1Status;
    }
    
    protected void setLevel2Status(final boolean level2Status) {
        this.level2Status = level2Status;
    }
    
    protected void setTableCreated(final boolean isTableCreated) {
        this.isTableCreated = isTableCreated;
    }
    
    protected void setSkipTableCreation(final boolean isSkipped) {
        this.isTableSkipped = isSkipped;
    }
    
    protected boolean isTableSkipped() {
        return this.isTableSkipped;
    }
    
    protected boolean isTableCreated() {
        return this.isTableCreated;
    }
    
    protected boolean isLevel1Completed() {
        return this.level1Status;
    }
    
    protected boolean isLevel2Completed() {
        return this.level2Status;
    }
    
    protected synchronized void addEntryInStatusTable(final Connection destConnection) throws QueryConstructionException, SQLException {
        if (this.isEntryAddedInTable()) {
            DBMTableStatus.LOGGER.warning("Adding skip entry ignored for table " + this.tableName + ". Entry already exists in DB");
            return;
        }
        PreparedStatement preparedStatementForStatusInsert = null;
        final boolean isAutoCommitTrue = destConnection.getAutoCommit();
        final int transactionIsolation = destConnection.getTransactionIsolation();
        try {
            if (isAutoCommitTrue) {
                destConnection.setAutoCommit(false);
            }
            ((LogicalConnection)destConnection).getPhysicalConnection().setTransactionIsolation(8);
            preparedStatementForStatusInsert = this.getPreparedStatementForStatusInsert(destConnection);
            preparedStatementForStatusInsert.setString(1, this.tableName);
            preparedStatementForStatusInsert.execute();
            destConnection.commit();
            this.setEntryAddedInTable(true);
        }
        catch (final SQLException sqe) {
            destConnection.rollback();
            sqe.printStackTrace();
            throw sqe;
        }
        finally {
            if (preparedStatementForStatusInsert != null) {
                preparedStatementForStatusInsert.close();
            }
            if (isAutoCommitTrue) {
                destConnection.setAutoCommit(isAutoCommitTrue);
            }
            ((LogicalConnection)destConnection).getPhysicalConnection().setTransactionIsolation(transactionIsolation);
        }
    }
    
    protected void updateStatusDetails(final Object value, final Connection destConnection, final boolean isLevel1) throws QueryConstructionException, SQLException {
        String columnName = null;
        String valueStr = null;
        if (isLevel1) {
            this.appendLevel1KeyName((String)value);
            columnName = "LEVEL1_QUERY_KEYS";
            valueStr = this.level1QueryKeys.toString();
        }
        else {
            this.appendLevel2KeyName((String)value);
            columnName = "LEVEL2_QUERY_KEYS";
            valueStr = this.level2QueryKeys.toString();
        }
        this.updateStatusDetails(columnName, valueStr, destConnection);
    }
    
    protected synchronized void updateStatusDetails(final String columnName, final Object value, final Connection destConnection) throws QueryConstructionException, SQLException {
        final List<String> columnNames = new ArrayList<String>();
        columnNames.add(columnName);
        final Map<String, Object> columnVsValue = new HashMap<String, Object>();
        columnVsValue.put(columnName, value);
        this.updateStatusDetails(columnNames, columnVsValue, destConnection);
    }
    
    protected synchronized void updateStatusDetails(final List<String> columnNames, final Map<String, Object> columnVsValue, final Connection destConnection) throws QueryConstructionException, SQLException {
        PreparedStatement preparedStatementForStatusUpdate = null;
        final boolean isAutoCommitTrue = destConnection.getAutoCommit();
        try {
            if (isAutoCommitTrue) {
                destConnection.setAutoCommit(false);
            }
            preparedStatementForStatusUpdate = this.getPreparedStatementForStatusUpdate(destConnection, columnNames);
            int i = 1;
            for (final String column : columnVsValue.keySet()) {
                preparedStatementForStatusUpdate.setObject(i, columnVsValue.get(column));
                ++i;
            }
            preparedStatementForStatusUpdate.setString(i, this.tableName);
            preparedStatementForStatusUpdate.execute();
            destConnection.commit();
        }
        catch (final SQLException sqe) {
            destConnection.rollback();
            sqe.printStackTrace();
            throw sqe;
        }
        finally {
            if (preparedStatementForStatusUpdate != null) {
                preparedStatementForStatusUpdate.close();
            }
            if (isAutoCommitTrue) {
                destConnection.setAutoCommit(isAutoCommitTrue);
            }
        }
    }
    
    protected PreparedStatement getPreparedStatementForStatusInsert(final Connection destConnection) throws QueryConstructionException, SQLException {
        if (DBMTableStatus.insertSQL == null) {
            final Map<Column, Object> columnValueMap = new LinkedHashMap<Column, Object>();
            columnValueMap.put(Column.getColumn("DBMProcessStats", "TABLE_NAME"), QueryConstants.PREPARED_STMT_CONST);
            DBMTableStatus.insertSQL = DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForInsert("DBMProcessStats", columnValueMap);
            DBMTableStatus.LOGGER.log(Level.INFO, "Constructed insert sql ::: {0}", DBMTableStatus.insertSQL);
        }
        return destConnection.prepareStatement(DBMTableStatus.insertSQL);
    }
    
    protected PreparedStatement getPreparedStatementForStatusUpdate(final Connection destConnection, final List<String> columnNames) throws QueryConstructionException, SQLException {
        String updateSQL = DBMTableStatus.updateSQLMap.get(columnNames.toString());
        if (updateSQL == null) {
            final Map<Column, Object> columnValueMap = new LinkedHashMap<Column, Object>();
            for (final String columnName : columnNames) {
                columnValueMap.put(Column.getColumn("DBMProcessStats", columnName), QueryConstants.PREPARED_STMT_CONST);
            }
            final Criteria criteria = new Criteria(Column.getColumn("DBMProcessStats", "TABLE_NAME"), QueryConstants.PREPARED_STMT_CONST, 0);
            updateSQL = DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForUpdate("DBMProcessStats", columnValueMap, criteria);
            DBMTableStatus.LOGGER.log(Level.INFO, "Constructed insert sql ::: {0}", updateSQL);
            DBMTableStatus.updateSQLMap.put(columnNames.toString(), updateSQL);
        }
        return destConnection.prepareStatement(updateSQL);
    }
    
    public boolean isEntryAddedInTable() {
        return this.isEntryAddedInTable;
    }
    
    public void setEntryAddedInTable(final boolean isEntryAddedInTable) {
        this.isEntryAddedInTable = isEntryAddedInTable;
    }
    
    static {
        LOGGER = Logger.getLogger(DBMTableStatus.class.getName());
        DBMTableStatus.insertSQL = null;
        DBMTableStatus.updateSQLMap = new ConcurrentHashMap<String, String>();
    }
}
