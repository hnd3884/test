package com.adventnet.db.migration.test;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import org.json.JSONObject;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.sql.Statement;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Iterator;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.migration.handler.NonMickeyTableHandlerUtil;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import com.adventnet.db.migration.notifier.ProgressNotifier;
import java.util.logging.Logger;
import java.util.concurrent.Callable;

public class SanityTestTask implements Callable<Void>
{
    private Logger logger;
    private String tableName;
    private ProgressNotifier notifier;
    private ExecutorService pool;
    private Map<String, String> diffMap;
    private List<String> selectColumns;
    private TableDefinition tableDef;
    private SelectQuery sQuery;
    private boolean isArchiveTable;
    private String liveTableName;
    
    public SanityTestTask(final String tableName, final ProgressNotifier notifier, final Map<String, String> diffMap, final ExecutorService pool, final boolean isArchiveTable) {
        this.selectColumns = new ArrayList<String>();
        this.sQuery = null;
        this.isArchiveTable = false;
        this.liveTableName = null;
        if (tableName == null) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        this.tableName = tableName;
        this.notifier = notifier;
        this.pool = pool;
        this.diffMap = diffMap;
        this.isArchiveTable = isArchiveTable;
        this.liveTableName = tableName;
        this.logger = Logger.getLogger(tableName);
    }
    
    @Override
    public Void call() throws Exception {
        try {
            this.tableDef = MetaDataUtil.getTableDefinitionByName(this.tableName);
            this.notifier.startedProcessingTable(this.tableName);
            if (!this.tableName.equals("DBMProcessStats") && !this.tableName.equals("SeqGenState")) {
                if (this.tableDef != null) {
                    this.compareTableData();
                }
                else if (this.isArchiveTable) {
                    this.liveTableName = TableArchiverUtil.getActualTable(this.tableName);
                    (this.tableDef = (TableDefinition)MetaDataUtil.getTableDefinitionByName(this.liveTableName).cloneWithoutFK()).setTableName(this.tableName);
                    this.compareArchiveTableData();
                }
                else {
                    this.compareNonMickeyTableData();
                }
            }
            this.notifier.completedProcessingTable(this.tableName);
            return null;
        }
        catch (final Throwable t) {
            this.logger.severe("Exception occurred while running sanity test: " + t.getMessage());
            t.printStackTrace();
            this.pool.shutdown();
            throw new Exception(t);
        }
    }
    
    protected void compareArchiveTableData() throws Exception {
        Connection srcConnection = null;
        Connection destConnection = null;
        DataSet dataFromSrcDB = null;
        DataSet dataFromDestDB = null;
        try {
            srcConnection = DBMigrationUtil.getSrcConnection();
            srcConnection.setAutoCommit(false);
            destConnection = DBMigrationUtil.getDestConnection();
            destConnection.setAutoCommit(false);
            final SelectQuery selectQuery = this.getSelectQuery();
            RelationalAPI.getInstance().setDataType(selectQuery);
            this.logger.info("SelectQuery used for sanity test ::: " + selectQuery);
            dataFromSrcDB = this.getTableData(selectQuery, srcConnection, DBMigrationUtil.getSrcDBAdapter());
            dataFromDestDB = this.getTableData(selectQuery, destConnection, DBMigrationUtil.getDestDBAdapter());
            final boolean compareTableData = this.compareTableData(dataFromSrcDB, dataFromDestDB);
            this.logger.info("Compare result for table " + this.tableName + " :: " + compareTableData);
        }
        catch (final Exception e) {
            throw new Exception("Exception occured while running sanity test for table " + this.tableName, e);
        }
        finally {
            if (dataFromSrcDB != null) {
                dataFromSrcDB.close();
            }
            if (srcConnection != null) {
                srcConnection.setAutoCommit(true);
                srcConnection.close();
            }
            if (dataFromDestDB != null) {
                dataFromDestDB.close();
            }
            if (destConnection != null) {
                destConnection.setAutoCommit(true);
                destConnection.close();
            }
        }
    }
    
    protected void compareTableData() throws Exception {
        Connection srcConnection = null;
        Connection destConnection = null;
        DataSet dataFromSrcDB = null;
        DataSet dataFromDestDB = null;
        boolean srcauto_commit = false;
        boolean destauto_commit = false;
        try {
            srcConnection = DBMigrationUtil.getSrcConnection();
            srcauto_commit = srcConnection.getAutoCommit();
            if (srcauto_commit) {
                srcConnection.setAutoCommit(false);
            }
            destConnection = DBMigrationUtil.getDestConnection();
            destauto_commit = destConnection.getAutoCommit();
            if (destauto_commit) {
                destConnection.setAutoCommit(false);
            }
            SelectQuery selectQuery = this.getSelectQuery();
            selectQuery = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery(selectQuery);
            RelationalAPI.getInstance().setDataType(selectQuery);
            this.logger.info("SelectQuery used for sanity test ::: " + selectQuery);
            dataFromSrcDB = this.getTableData(selectQuery, srcConnection, DBMigrationUtil.getSrcDBAdapter());
            dataFromDestDB = this.getTableData(selectQuery, destConnection, DBMigrationUtil.getDestDBAdapter());
            final boolean compareTableData = this.compareTableData(dataFromSrcDB, dataFromDestDB);
            this.logger.info("Compare result for table " + this.tableName + " :: " + compareTableData);
        }
        catch (final Exception e) {
            throw new Exception("Exception occured while running sanity test for table " + this.tableName, e);
        }
        finally {
            if (dataFromSrcDB != null) {
                dataFromSrcDB.close();
            }
            if (srcConnection != null) {
                if (srcauto_commit) {
                    srcConnection.setAutoCommit(true);
                }
                srcConnection.close();
            }
            if (dataFromDestDB != null) {
                dataFromDestDB.close();
            }
            if (destConnection != null) {
                if (destauto_commit) {
                    destConnection.setAutoCommit(true);
                }
                destConnection.close();
            }
        }
    }
    
    protected void compareNonMickeyTableData() throws Exception {
        Connection srcConnection = null;
        Connection destConnection = null;
        DataSet dataFromSrcDB = null;
        DataSet dataFromDestDB = null;
        boolean src_auto_commit = false;
        boolean dest_auto_commit = false;
        try {
            this.logger.info("comparing non mickey table data: " + this.tableName);
            srcConnection = DBMigrationUtil.getSrcConnection();
            src_auto_commit = srcConnection.getAutoCommit();
            if (src_auto_commit) {
                srcConnection.setAutoCommit(false);
            }
            destConnection = DBMigrationUtil.getDestConnection();
            dest_auto_commit = destConnection.getAutoCommit();
            if (dest_auto_commit) {
                destConnection.setAutoCommit(false);
            }
            dataFromSrcDB = this.getTableDataForNonMickeyTable(this.getSelectSQLForNonMickeyTable(srcConnection, DBMigrationUtil.getSrcDBAdapter(), DBMigrationUtil.getSrcDBType().toString()), srcConnection, DBMigrationUtil.getSrcDBAdapter());
            dataFromDestDB = this.getTableDataForNonMickeyTable(this.getSelectSQLForNonMickeyTable(destConnection, DBMigrationUtil.getDestDBAdapter(), DBMigrationUtil.getDestDBType().toString()), destConnection, DBMigrationUtil.getDestDBAdapter());
            if (dataFromSrcDB.getColumnCount() != dataFromDestDB.getColumnCount()) {
                this.logger.severe("Select Columns count do not match for the table " + this.tableName + " in source & destination database. Source(" + DBMigrationUtil.getSrcDBType() + ") has " + dataFromSrcDB.getColumnCount() + " Columns.  But Destination(" + DBMigrationUtil.getDestDBType() + ") has " + dataFromDestDB.getColumnCount() + " columns.");
                throw new Exception("Select Columns count do not match for the table " + this.tableName + " in source & destination database. Source(" + DBMigrationUtil.getSrcDBType() + ") has " + dataFromSrcDB.getColumnCount() + " Columns.  But Destination(" + DBMigrationUtil.getDestDBType() + ") has " + dataFromDestDB.getColumnCount() + " columns.");
            }
            final boolean compareTableData = this.compareTableData(dataFromSrcDB, dataFromDestDB);
            this.logger.info("Compare result for table " + this.tableName + " :: " + compareTableData);
        }
        catch (final Exception e) {
            throw new Exception("Exception occured while running sanity test for table " + this.tableName, e);
        }
        finally {
            if (dataFromSrcDB != null) {
                dataFromSrcDB.close();
            }
            if (srcConnection != null) {
                if (src_auto_commit) {
                    srcConnection.setAutoCommit(true);
                }
                srcConnection.close();
            }
            if (dataFromDestDB != null) {
                dataFromDestDB.close();
            }
            if (destConnection != null) {
                if (dest_auto_commit) {
                    destConnection.setAutoCommit(true);
                }
                destConnection.close();
            }
        }
    }
    
    protected String getSelectSQLForNonMickeyTable(final Connection conn, final DBAdapter dba, final String dbName) throws Exception {
        String selectSQL = NonMickeyTableHandlerUtil.getSQLForSelect(this.tableName, dbName);
        if (selectSQL != null) {
            return selectSQL;
        }
        final DBMigrationHandlerFactory.SanityTestConf sanityTestConf = DBMigrationUtil.getHandlerFactory().getSanityTestConf();
        this.sQuery = new SelectQueryImpl(Table.getTable(this.tableName));
        List<String> tableColumns = sanityTestConf.getTableColumns(this.tableName);
        if (tableColumns == null || tableColumns.isEmpty()) {
            tableColumns = dba.getColumnNamesFromDB(this.tableName, null, conn.getMetaData());
        }
        for (final String columnName : tableColumns) {
            this.sQuery.addSelectColumn(new Column(this.tableName, columnName));
        }
        this.selectColumns = tableColumns;
        final List<String> pkCols = RelationalAPI.getInstance().getDBAdapter().getPKColumnNameOfTheTable(this.tableName, conn.getMetaData());
        for (final String pkColName : pkCols) {
            this.sQuery.addSortColumn(new SortColumn(new Column(this.tableName, pkColName), true));
        }
        selectSQL = dba.getSQLGenerator().getSQLForSelect(this.sQuery);
        this.logger.info("sqlForSelect ::: " + selectSQL);
        return selectSQL;
    }
    
    protected SelectQuery getSelectQuery() throws Exception {
        final DBMigrationHandlerFactory.SanityTestConf sanityTestConf = DBMigrationUtil.getHandlerFactory().getSanityTestConf();
        if (this.isArchiveTable) {
            this.sQuery = new SelectQueryImpl(new ArchiveTable(this.liveTableName, this.liveTableName, this.tableName, this.liveTableName, null, null));
        }
        else {
            this.sQuery = new SelectQueryImpl(Table.getTable(this.tableName));
        }
        final List<String> tableColumns = sanityTestConf.getTableColumns(this.tableName);
        if (tableColumns == null || tableColumns.isEmpty()) {
            final List<String> tableColumnsTypes = sanityTestConf.getTableColumnsTypes(this.tableName);
            this.logger.info("Configured select column types :: " + tableColumnsTypes);
            if (tableColumnsTypes != null && !tableColumnsTypes.isEmpty()) {
                final List<ColumnDefinition> columnList = this.tableDef.getColumnList();
                for (final ColumnDefinition colDef : columnList) {
                    if (tableColumnsTypes.contains(colDef.getDataType())) {
                        this.sQuery.addSelectColumn(Column.getColumn(this.liveTableName, colDef.getColumnName()));
                        this.selectColumns.add(colDef.getColumnName());
                    }
                }
            }
            else {
                final List<String> colNameList = this.tableDef.getColumnNames();
                for (final String colName : colNameList) {
                    this.sQuery.addSelectColumn(new Column(this.liveTableName, colName));
                }
                this.selectColumns = colNameList;
            }
        }
        else {
            this.logger.info("Configured select column " + tableColumns);
            for (final String columnName : tableColumns) {
                this.sQuery.addSelectColumn(Column.getColumn(this.liveTableName, columnName));
            }
            this.selectColumns = tableColumns;
        }
        for (final String column : this.tableDef.getPrimaryKey().getColumnList()) {
            this.sQuery.addSortColumn(new SortColumn(Column.getColumn(this.liveTableName, column), true, true));
        }
        sanityTestConf.invokeHandler(this.sQuery);
        return this.sQuery;
    }
    
    protected DataSet getTableDataForNonMickeyTable(final String sql, final Connection conn, final DBAdapter dba) throws SQLException, QueryConstructionException {
        this.logger.info("sqlForSelect ::: " + sql);
        final Statement stmt = dba.createStatement(conn, 0);
        final ResultSetAdapter rs = dba.executeQuery(stmt, sql);
        return new DataSet(rs, stmt);
    }
    
    protected DataSet getTableData(final String sql, final Connection conn, final DBAdapter dba) throws SQLException, QueryConstructionException {
        this.logger.info("sqlForSelect ::: " + sql);
        final Statement stmt = dba.createStatement(conn, 0);
        final ResultSetAdapter rs = dba.executeQuery(stmt, sql);
        return new DataSet(rs, this.sQuery, RelationalAPI.getSelectColumns(this.sQuery), stmt);
    }
    
    protected DataSet getTableData(final SelectQuery sQuery, final Connection conn, final DBAdapter dba) throws SQLException, QueryConstructionException {
        return this.getTableData(dba.getSQLGenerator().getSQLForSelect(sQuery), conn, dba);
    }
    
    protected boolean compareTableData(final DataSet srcData, final DataSet destData) throws Exception {
        if (srcData == null || destData == null) {
            this.diffMap.put(this.tableName, "{src_dataset_isnull:" + (srcData != null) + "}{dst_dataset_isnull:" + (destData != null) + "}");
            return false;
        }
        while (srcData.next() && destData.next()) {
            if (!this.compareRowCells(srcData, destData)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean compareRowCells(final DataSet srcData, final DataSet destData) throws Exception {
        final JSONObject jsonObj = new JSONObject();
        for (final String columnName : this.selectColumns) {
            if (!this.isEquals(srcData.getValue(columnName), destData.getValue(columnName))) {
                final StringBuilder strBuff = new StringBuilder("{");
                if (MetaDataUtil.getTableDefinitionByName(this.tableName) != null) {
                    for (final String colName : MetaDataUtil.getTableDefinitionByName(this.tableName).getPrimaryKey().getColumnList()) {
                        strBuff.append(colName).append("=").append(srcData.getValue(colName)).append(",");
                    }
                }
                else {
                    final Connection con = DBMigrationUtil.getSrcConnection();
                    try {
                        final List<String> pkColName = RelationalAPI.getInstance().getDBAdapter().getPKColumnNameOfTheTable(this.tableName, con.getMetaData());
                        for (final String pkCol : pkColName) {
                            strBuff.append(pkCol).append("=").append(srcData.getValue(pkCol)).append(",");
                        }
                    }
                    finally {
                        if (con != null) {
                            con.close();
                        }
                    }
                }
                strBuff.append("diff_column=").append(columnName);
                strBuff.append("}");
                jsonObj.put("tablename", (Object)this.tableName);
                jsonObj.put("columnname", (Object)columnName);
                jsonObj.put("sourcevalue", srcData.getValue(columnName));
                jsonObj.put("destvalue", destData.getValue(columnName));
                if (!DBMigrationUtil.getHandlerFactory().getSanityTestConf().isDiffIgnorable(this.tableName, jsonObj)) {
                    this.diffMap.put(this.tableName, strBuff.toString());
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    protected boolean isEquals(final Object srcVal, final Object dstVal) throws IOException {
        if (srcVal instanceof InputStream && dstVal instanceof InputStream) {
            return IOUtils.contentEquals((InputStream)srcVal, (InputStream)dstVal);
        }
        return (srcVal == null) ? (dstVal == null) : srcVal.equals(dstVal);
    }
}
