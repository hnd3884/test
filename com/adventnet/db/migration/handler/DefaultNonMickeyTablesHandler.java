package com.adventnet.db.migration.handler;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.sql.ResultSetMetaData;
import java.util.Map;
import com.adventnet.ds.query.QueryConstants;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.io.IOException;
import java.util.logging.Logger;

public class DefaultNonMickeyTablesHandler implements NonMickeyTablesMigrationHandler
{
    private static final Logger LOGGER;
    private String handlerName;
    
    public DefaultNonMickeyTablesHandler() {
        this.handlerName = null;
    }
    
    @Override
    public final void setHandlerName(final String name) {
        this.handlerName = name;
    }
    
    @Override
    public final String getHandlerName() {
        return this.handlerName;
    }
    
    @Override
    public void initialize() throws IOException {
    }
    
    @Override
    public boolean processTable(final String tableName) {
        return true;
    }
    
    @Override
    public String getSQLForCreateTable(final String tableName) {
        final String createSQL = NonMickeyTableHandlerUtil.getSQLForCreate(tableName, DBMigrationUtil.getDestDBType().toString());
        DefaultNonMickeyTablesHandler.LOGGER.info("create tableString for table " + tableName + " ::: " + createSQL);
        return createSQL;
    }
    
    @Override
    public void preInvokeForCreateTable(final String tableName, final String createSQL) {
    }
    
    @Override
    public List<String> getSelectColumns(final String tableName) throws SQLException {
        Connection srcConn = null;
        List<String> columnNames;
        try {
            srcConn = DBMigrationUtil.getSrcConnection();
            columnNames = DBMigrationUtil.getSrcDBAdapter().getColumnNamesFromDB(tableName, null, srcConn.getMetaData());
        }
        finally {
            if (srcConn != null) {
                srcConn.close();
            }
        }
        return columnNames;
    }
    
    @Override
    public String getSQLForSelect(final String tableName, final List<String> columnNames, final boolean isResumeEnabled) throws QueryConstructionException, SQLException {
        Connection conn = null;
        Connection srcConn = null;
        String selectSQL;
        try {
            selectSQL = NonMickeyTableHandlerUtil.getSQLForSelect(tableName, DBMigrationUtil.getSrcDBType().toString());
            if (selectSQL != null) {
                return selectSQL;
            }
            conn = DBMigrationUtil.getDestConnection();
            srcConn = DBMigrationUtil.getSrcConnection();
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable(tableName));
            for (final String columnName : columnNames) {
                sQuery.addSelectColumn(new Column(tableName, columnName));
            }
            final List<String> pkCols = RelationalAPI.getInstance().getDBAdapter().getPKColumnNameOfTheTable(tableName, srcConn.getMetaData());
            for (final String pkColName : pkCols) {
                sQuery.addSortColumn(new SortColumn(new Column(tableName, pkColName), true));
            }
            if (isResumeEnabled) {
                final Long count = DBMigrationUtil.getDestDBAdapter().getTotalRowCount(conn, tableName);
                if (count > 0L) {
                    DefaultNonMickeyTablesHandler.LOGGER.info("No of rows migrated previously: " + count);
                    sQuery.setRange(new Range(count.intValue() + 1, -99));
                }
            }
            selectSQL = DBMigrationUtil.getSrcDBAdapter().getSQLGenerator().getSQLForSelect(sQuery);
        }
        finally {
            if (conn != null) {
                conn.close();
            }
            if (srcConn != null) {
                srcConn.close();
            }
        }
        return selectSQL;
    }
    
    public void preInvokeForFetchdata(final String tableName, final String selectQuery) {
    }
    
    @Override
    public String getSQLForInsertQuery(final String tableName, final List<String> columnNames) throws QueryConstructionException {
        final Map<Column, Object> columnValueMap = new LinkedHashMap<Column, Object>();
        for (final String columnName : columnNames) {
            columnValueMap.put(Column.getColumn(tableName, columnName), QueryConstants.PREPARED_STMT_CONST);
        }
        return DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForInsert(tableName, columnValueMap);
    }
    
    @Override
    public Map<String, Integer> getSQLTypesForInsert(final String tableName, final ResultSetMetaData metaData) throws SQLException {
        final Map<String, Integer> columnVsType = new HashMap<String, Integer>();
        for (int index = 1; index <= metaData.getColumnCount(); ++index) {
            columnVsType.put(metaData.getColumnName(index), metaData.getColumnType(index));
        }
        return columnVsType;
    }
    
    @Override
    public void setValueForInsert(final String tableName, final PreparedStatement ps, final String columnName, final int columnIndex, final int sqlType, final Map<String, Object> allColumnValues) throws SQLException {
        DefaultNonMickeyTablesHandler.LOGGER.info("Row value for " + tableName + " ::: " + allColumnValues);
        DBMigrationUtil.getDestDBAdapter().setValue(ps, columnIndex, sqlType, allColumnValues.get(columnName));
    }
    
    @Override
    public String getSQLForCreateIndex(final String tableName) {
        return NonMickeyTableHandlerUtil.getSQLForIndexKey(tableName, DBMigrationUtil.getDestDBType().toString());
    }
    
    @Override
    public void preInvokeForCreateIndex(final String tableName, final String alterSQLForCreateIndex) {
    }
    
    @Override
    public void postInvokeForCreateIndex(final String tableName, final String alterSQLForCreateIndex) {
    }
    
    @Override
    public String getSQLForCreatePrimaryKey(final String tableName) {
        return NonMickeyTableHandlerUtil.getSQLForPrimaryKey(tableName, DBMigrationUtil.getDestDBType().toString());
    }
    
    @Override
    public void preInvokeForCreatePK(final String tableName, final String alterSQLForCreatePK) {
    }
    
    @Override
    public void postInvokeForCreatePK(final String tableName, final String alterSQLForCreatePK) {
    }
    
    @Override
    public String getSQLForCreateUniqueKey(final String tableName) {
        return NonMickeyTableHandlerUtil.getSQLForUniqueKey(tableName, DBMigrationUtil.getDestDBType().toString());
    }
    
    @Override
    public void preInvokeForCreateUK(final String tableName, final String alterSQLForCreateUK) {
    }
    
    @Override
    public void postInvokeForCreateUK(final String tableName, final String alterSQLForCreateUK) {
    }
    
    @Override
    public String getSQLForCreateForeignKey(final String tableName) {
        return NonMickeyTableHandlerUtil.getSQLForForeignKey(tableName, DBMigrationUtil.getDestDBType().toString());
    }
    
    @Override
    public void preInvokeForCreateFK(final String tableName, final String alterSQLForCreateFK) {
    }
    
    @Override
    public void postInvokeForCreateFK(final String tableName, final String alterSQLForCreateFK) {
    }
    
    @Override
    public void postInvokeForCreateTable(final String tableName) {
    }
    
    @Override
    public boolean revertOnFailure() {
        return true;
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultNonMickeyTablesHandler.class.getName());
    }
}
