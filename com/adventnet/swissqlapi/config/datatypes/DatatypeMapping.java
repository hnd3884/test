package com.adventnet.swissqlapi.config.datatypes;

import java.util.Map;
import java.util.Hashtable;

public class DatatypeMapping
{
    Hashtable globalType;
    Hashtable tableMapping;
    
    public DatatypeMapping() {
        this.globalType = new Hashtable();
        this.tableMapping = new Hashtable();
    }
    
    public void addGlobalDatatypeMapping(final String sourceDatatype, final String mappedDatatype) {
        if (sourceDatatype != null && mappedDatatype != null) {
            this.globalType.put(sourceDatatype.trim().toLowerCase(), mappedDatatype);
        }
    }
    
    public void addGlobalDatatypeMapping(final Map datatypeMappings) {
        this.globalType.putAll(datatypeMappings);
    }
    
    public void addTableSpecificDatatypeMapping(final String tableName, final String columnName, final String mappedDatatype) {
        if (tableName != null && columnName != null && mappedDatatype != null) {
            Hashtable columnDatatypeMapping = new Hashtable();
            if (this.tableMapping != null && this.tableMapping.containsKey(tableName.toLowerCase())) {
                columnDatatypeMapping = this.tableMapping.get(tableName.toLowerCase());
                columnDatatypeMapping.put(columnName.toLowerCase(), mappedDatatype);
            }
            else {
                columnDatatypeMapping.put(columnName.toLowerCase(), mappedDatatype);
                this.tableMapping.put(tableName.toLowerCase(), columnDatatypeMapping);
            }
        }
    }
    
    public Hashtable getGlobalDatatypeMapping() {
        return this.globalType;
    }
    
    public Hashtable getTableSpecificDatatypeMapping() {
        return this.tableMapping;
    }
}
