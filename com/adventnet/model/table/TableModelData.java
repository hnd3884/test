package com.adventnet.model.table;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import com.adventnet.model.ModelData;

public class TableModelData implements ModelData
{
    Vector tableData;
    long startIndex;
    long endIndex;
    long total;
    Vector keys;
    List keyColumns;
    protected Class[] colClasses;
    protected int[] columnTypes;
    protected String[] colTypes;
    int[] keyColumnIndices;
    
    public TableModelData(final Vector tableData) {
        this.colClasses = null;
        this.columnTypes = null;
        this.colTypes = null;
        this.tableData = tableData;
    }
    
    public String getType() {
        return "TABLE";
    }
    
    public Vector getTableData() {
        return this.tableData;
    }
    
    public void setTableData(final Vector v) {
        this.tableData = v;
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public void setStartIndex(final long v) {
        this.startIndex = v;
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public void setEndIndex(final long v) {
        this.endIndex = v;
    }
    
    public long getTotal() {
        return this.total;
    }
    
    public void setTotal(final long v) {
        this.total = v;
    }
    
    public Vector getKeys() {
        return this.keys;
    }
    
    public void setKeys(final Vector v) {
        this.keys = v;
    }
    
    public List getKeyColumns() {
        return this.keyColumns;
    }
    
    public void setKeyColumns(final List v) {
        this.keyColumns = v;
    }
    
    public Class[] getColumnClasses() {
        return this.colClasses;
    }
    
    public void setColumnClasses(final Class[] colClasses) {
        this.colClasses = colClasses;
    }
    
    public int[] getColumnSQLTypes() {
        return this.columnTypes;
    }
    
    public String[] getColSQLTypes() {
        return this.colTypes;
    }
    
    public void setColumnSQLTypes(final int[] columnTypes) {
        this.columnTypes = columnTypes;
    }
    
    public void setColSQLTypes(final String[] colTypes) {
        this.colTypes = colTypes;
    }
    
    public int[] getKeyColumnIndices() {
        return this.keyColumnIndices;
    }
    
    public void setKeyColumnIndices(final int[] v) {
        this.keyColumnIndices = v;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("\n<TABLEMODELDATA ");
        buff.append("START-INDEX=\"");
        buff.append(this.startIndex);
        buff.append("\" END-INDEX=\"");
        buff.append(this.endIndex);
        buff.append("\" TOTAL=\"");
        buff.append(this.total);
        buff.append("\">");
        buff.append("\n\t<KEYCOLUMNS>");
        buff.append(this.keyColumns);
        buff.append("</KEYCOLUMNS>");
        if (this.keyColumnIndices != null) {
            buff.append("\n\t<KEYCOLUMNINDICES>");
            for (int i = 0; i < this.keyColumnIndices.length; ++i) {
                buff.append(this.keyColumnIndices[i]);
                buff.append(",");
            }
            buff.append("</KEYCOLUMNINDICES>");
        }
        buff.append("\n\t<COLCLASSES>");
        final String colClassesStr = (this.colClasses == null) ? "null" : Arrays.asList((Class[])this.colClasses).toString();
        buff.append(colClassesStr);
        buff.append("</COLCLASSES>");
        buff.append("\n\t<ROWS>");
        for (int j = this.tableData.size(), k = 0; k < j; ++k) {
            buff.append("\n\t\t<ROW index=\"");
            buff.append(k);
            buff.append("\">");
            buff.append(this.tableData.get(k));
            buff.append("</ROW><KEY>" + this.keys.get(k) + "</KEY>");
        }
        buff.append("\n\t</ROWS>");
        buff.append("\n</TABLEMODELDATA>");
        return buff.toString();
    }
}
