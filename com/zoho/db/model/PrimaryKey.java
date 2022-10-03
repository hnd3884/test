package com.zoho.db.model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class PrimaryKey
{
    private String name;
    private Table table;
    private String dbLable;
    private List<String> pkColumnNames;
    private Map<MetaDataLabel, String> properties;
    private long cordinality;
    private static String format;
    
    public PrimaryKey() {
        this.pkColumnNames = new ArrayList<String>();
        this.properties = new HashMap<MetaDataLabel, String>();
        this.cordinality = 0L;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public List<String> getPkColumnNames() {
        return this.pkColumnNames;
    }
    
    public void addPkColumn(final String pkColumn) {
        this.pkColumnNames.add(pkColumn);
    }
    
    public Map<MetaDataLabel, String> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<MetaDataLabel, String> others) {
        this.properties = others;
    }
    
    @Override
    public String toString() {
        return String.format(PrimaryKey.format, this.getName(), this.getPkColumnNames().toString().replace('[', '(').replace(']', ')'));
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    public long getCordinality() {
        return this.cordinality;
    }
    
    public void setCordinality(final String string) {
        this.cordinality = ((string != null) ? Long.parseLong(string) : 0L);
    }
    
    static {
        PrimaryKey.format = "%s KEY %s\n";
    }
    
    public enum MetaDataLabel
    {
        TABLE_CAT, 
        TABLE_SCHEM, 
        TABLE_NAME, 
        COLUMN_NAME, 
        KEY_SEQ, 
        PK_NAME, 
        COLUMNS_IN_ORDER;
    }
}
