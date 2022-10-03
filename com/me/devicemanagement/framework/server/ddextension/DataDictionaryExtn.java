package com.me.devicemanagement.framework.server.ddextension;

import java.util.HashMap;

public class DataDictionaryExtn
{
    private String tableName;
    private String columnName;
    private HashMap columnPropsDefns;
    private HashMap<String, HashMap> columns;
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public HashMap getColumnPropsDefns() {
        return this.columnPropsDefns;
    }
    
    public void setColumnPropsDefns(final HashMap columnPropsDefns) {
        this.columnPropsDefns = columnPropsDefns;
    }
    
    public HashMap<String, HashMap> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final HashMap<String, HashMap> columns) {
        this.columns = columns;
    }
}
