package com.zoho.db.model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Index
{
    private String name;
    private Table table;
    private int cordinality;
    private String dbLable;
    private List<String> indexColumnNames;
    private Map<MetaDataLabel, String> properties;
    private static String format;
    
    public Index() {
        this.indexColumnNames = new ArrayList<String>();
        this.properties = new HashMap<MetaDataLabel, String>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String indexName) {
        this.name = indexName;
    }
    
    public List<String> getIndexColumnNames() {
        return this.indexColumnNames;
    }
    
    public void addIndexColumnName(final String indexColumnName) {
        this.indexColumnNames.add(indexColumnName);
    }
    
    public int getCordinality() {
        return this.cordinality;
    }
    
    public void setCordinality(final int cordinality) {
        this.cordinality = cordinality;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public Map<MetaDataLabel, String> getOthers() {
        return this.properties;
    }
    
    public void setProperties(final Map<MetaDataLabel, String> properties) {
        this.properties = properties;
    }
    
    @Override
    public String toString() {
        return String.format(Index.format, this.getName(), this.getIndexColumnNames().toString().replace('[', '(').replace(']', ')'));
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    static {
        Index.format = "%s ON %s\n";
    }
    
    public enum MetaDataLabel
    {
        TABLE_CAT, 
        TABLE_SCHEM, 
        TABLE_NAME, 
        NON_UNIQUE, 
        INDEX_QUALIFIER, 
        INDEX_NAME, 
        TYPE, 
        ORDINAL_POSITION, 
        COLUMN_NAME, 
        ASC_OR_DESC, 
        CARDINALITY, 
        PAGES, 
        FILTER_CONDITION;
    }
}
