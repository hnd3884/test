package org.apache.taglibs.standard.tag.common.sql;

import java.util.SortedMap;
import java.sql.ResultSetMetaData;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import javax.servlet.jsp.jstl.sql.Result;

public class ResultImpl implements Result
{
    private List rowMap;
    private List rowByIndex;
    private String[] columnNames;
    private boolean isLimited;
    
    public ResultImpl(final ResultSet rs) throws SQLException {
        this(rs, -1, -1);
    }
    
    public ResultImpl(final ResultSet rs, final int maxRows) throws SQLException {
        this(rs, -1, maxRows);
    }
    
    public ResultImpl(final ResultSet rs, final int startRow, final int maxRows) throws SQLException {
        this.rowMap = new ArrayList();
        this.rowByIndex = new ArrayList();
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int noOfColumns = rsmd.getColumnCount();
        this.columnNames = new String[noOfColumns];
        for (int i = 1; i <= noOfColumns; ++i) {
            this.columnNames[i - 1] = rsmd.getColumnName(i);
        }
        for (int i = 0; i < startRow; ++i) {
            rs.next();
        }
        int processedRows = 0;
        while (rs.next()) {
            if (maxRows != -1 && processedRows == maxRows) {
                this.isLimited = true;
                break;
            }
            final Object[] columns = new Object[noOfColumns];
            final SortedMap columnMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
            for (int j = 1; j <= noOfColumns; ++j) {
                Object value = rs.getObject(j);
                if (rs.wasNull()) {
                    value = null;
                }
                columns[j - 1] = value;
                columnMap.put(this.columnNames[j - 1], value);
            }
            this.rowMap.add(columnMap);
            this.rowByIndex.add(columns);
            ++processedRows;
        }
    }
    
    public SortedMap[] getRows() {
        if (this.rowMap == null) {
            return null;
        }
        return this.rowMap.toArray(new SortedMap[0]);
    }
    
    public Object[][] getRowsByIndex() {
        if (this.rowByIndex == null) {
            return null;
        }
        return this.rowByIndex.toArray(new Object[0][0]);
    }
    
    public String[] getColumnNames() {
        return this.columnNames;
    }
    
    public int getRowCount() {
        if (this.rowMap == null) {
            return -1;
        }
        return this.rowMap.size();
    }
    
    public boolean isLimitedByMaxRows() {
        return this.isLimited;
    }
}
