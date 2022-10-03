package com.adventnet.ds.query;

import java.sql.SQLException;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.db.api.RelationalAPI;
import java.util.HashMap;

public class DMDataSetWrapper
{
    private HashMap<Integer, HashMap<String, Object>> tableMap;
    private int[] columnTypes;
    private String[] columnNames;
    private int columnNameslength;
    private int size;
    private int currentRowIndex;
    
    public DMDataSetWrapper(final DataSet ds) throws Exception {
        this.tableMap = new HashMap<Integer, HashMap<String, Object>>();
        this.columnNameslength = 0;
        this.size = 0;
        this.currentRowIndex = -1;
        if (ds != null) {
            int row = 0;
            this.columnNameslength = ds.getColumnCount();
            this.columnTypes = new int[this.columnNameslength];
            this.columnNames = new String[this.columnNameslength];
            for (int i = 0; i < this.columnNameslength; ++i) {
                this.columnTypes[i] = ds.getColumnType(i + 1);
                this.columnNames[i] = ds.getColumnName(i + 1);
            }
            while (ds.next()) {
                int columnCount = this.columnNameslength;
                HashMap<String, Object> rowMap = this.tableMap.get(row);
                if (rowMap == null) {
                    rowMap = new HashMap<String, Object>();
                }
                while (columnCount > 0) {
                    final String columnName = ds.getColumnName(columnCount--);
                    final Object columnValue = ds.getValue(columnName);
                    rowMap.put(columnName, columnValue);
                }
                this.tableMap.put(row, rowMap);
                ++row;
            }
            this.size = this.tableMap.size();
        }
    }
    
    public boolean next() {
        ++this.currentRowIndex;
        return this.currentRowIndex < this.size;
    }
    
    public Object getValue(final String columnName) {
        return this.tableMap.get(this.currentRowIndex).get(columnName);
    }
    
    public int getSize() {
        return this.tableMap.size();
    }
    
    public static DMDataSetWrapper executeQuery(final Object query) throws Exception {
        return executeQuery(query, true);
    }
    
    public static DMDataSetWrapper executeQuery(final Object query, final boolean isUpdate) throws Exception {
        Connection connection = null;
        try {
            final RelationalAPI rapi = RelationalAPI.getInstance();
            if (isUpdate) {
                connection = rapi.getConnection();
            }
            else {
                connection = DBUtil.getConnection("READ_ONLY");
            }
            final DMDataSetWrapper ddsw = executeQuery(connection, query);
            return ddsw;
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public static DMDataSetWrapper executeQuery(final Connection connection, final Object query) throws Exception {
        DataSet ds = null;
        DMDataSetWrapper ddsw = null;
        try {
            if (query instanceof SelectQuery) {
                ds = RelationalAPI.getInstance().executeQuery((Query)query, connection);
            }
            else if (query instanceof UnionQuery) {
                ds = RelationalAPI.getInstance().executeQuery((Query)query, connection);
            }
            else if (query instanceof String) {
                ds = RelationalAPI.getInstance().executeQuery((String)query, connection);
            }
            ddsw = new DMDataSetWrapper(ds);
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
        return ddsw;
    }
    
    public static DMDataSetWrapper executeQuery(final Connection connection, final String sqlQueryString) throws Exception {
        DataSet ds = null;
        DMDataSetWrapper ddsw = null;
        try {
            ds = RelationalAPI.getInstance().executeQuery(sqlQueryString, connection);
            ddsw = new DMDataSetWrapper(ds);
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
        return ddsw;
    }
    
    public int getColumnType(final int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= this.columnNameslength) {
            return this.columnTypes[columnIndex - 1];
        }
        throw new SQLException("Invalid column index");
    }
    
    public String getColumnName(final int columnIndex) throws SQLException {
        if (columnIndex >= 1 && columnIndex <= this.columnNameslength) {
            return this.columnNames[columnIndex - 1];
        }
        throw new SQLException("Invalid column index");
    }
    
    public int getColumnCount() {
        return this.columnNameslength;
    }
    
    public Object getValue(final int columnIndex) throws SQLException {
        try {
            return this.getValue(this.columnNames[columnIndex - 1]);
        }
        catch (final Exception e) {
            throw new SQLException("Invalid column index.");
        }
    }
}
