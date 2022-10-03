package com.adventnet.swissqlapi.sql.statement;

import java.util.Enumeration;
import java.util.Hashtable;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.ArrayList;

public class OpenXMLSchema
{
    private ArrayList column_names;
    private ArrayList column_type;
    private ArrayList column_pattern;
    private ArrayList meta_property;
    private String tableName;
    private boolean noMetaData;
    
    public OpenXMLSchema() {
        this.column_names = null;
        this.column_type = null;
        this.column_pattern = null;
        this.meta_property = null;
        this.tableName = null;
        this.noMetaData = false;
        this.column_names = new ArrayList();
        this.column_type = new ArrayList();
        this.column_pattern = new ArrayList();
        this.meta_property = new ArrayList();
    }
    
    public void setTableName(final String name) {
        this.tableName = name;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public boolean getNoMetaData() {
        if (this.column_names.isEmpty() && this.column_type.isEmpty() && this.tableName != null) {
            this.buildTheColumnNamesFromTable();
        }
        return this.noMetaData;
    }
    
    public void setColumnNames(final ArrayList column_names) {
        this.column_names = column_names;
    }
    
    public ArrayList getColumnNames() {
        return this.column_names;
    }
    
    public void setColumnPatterns(final ArrayList column_pattern) {
        this.column_pattern = column_pattern;
    }
    
    public ArrayList getColumnPatterns() {
        return this.column_pattern;
    }
    
    public void setColumnTypes(final ArrayList column_type) {
        this.column_type = column_type;
    }
    
    public ArrayList getColumnTypes() {
        return this.column_type;
    }
    
    public void setMetaProperties(final ArrayList meta_property) {
        this.meta_property = meta_property;
    }
    
    public ArrayList getMetaProperties() {
        return this.meta_property;
    }
    
    public void buildTheColumnNamesFromTable() {
        if (!SwisSQLAPI.dataTypesFromMetaDataHT.isEmpty() && SwisSQLAPI.dataTypesFromMetaDataHT.containsKey(this.tableName)) {
            final Hashtable colDetails = SwisSQLAPI.dataTypesFromMetaDataHT.get(this.tableName);
            final Enumeration cols = colDetails.keys();
            while (cols.hasMoreElements()) {
                final String columnName = cols.nextElement();
                this.column_names.add(columnName);
                final String columnType = colDetails.get(columnName);
                this.column_type.add(columnType);
            }
        }
        else {
            this.noMetaData = true;
        }
    }
}
