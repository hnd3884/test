package com.adventnet.persistence.xml;

import java.util.Properties;

public class DVHandlerTemplate
{
    private String tableName;
    private String columnName;
    private DynamicValueHandler dynamicValueHandler;
    private Properties configuredDVHP;
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setDynamicValueHandler(final DynamicValueHandler dynamicValueHandler) {
        this.dynamicValueHandler = dynamicValueHandler;
    }
    
    public DynamicValueHandler getDynamicValueHandler() {
        return this.dynamicValueHandler;
    }
    
    public void setConfiguredAttributes(final Properties configuredDVHP) {
        this.configuredDVHP = configuredDVHP;
    }
    
    public Properties getConfiguredAttributes() {
        return this.configuredDVHP;
    }
}
