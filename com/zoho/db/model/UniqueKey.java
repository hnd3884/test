package com.zoho.db.model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class UniqueKey
{
    private String name;
    private Table table;
    private String dbLable;
    private List<String> ukColumnNames;
    private Map<Index.MetaDataLabel, String> others;
    private static String format;
    
    public UniqueKey() {
        this.ukColumnNames = new ArrayList<String>();
        this.others = new HashMap<Index.MetaDataLabel, String>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<String> getUkColumns() {
        return this.ukColumnNames;
    }
    
    public void addUkColumns(final String ukColumn) {
        this.ukColumnNames.add(ukColumn);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public Map<Index.MetaDataLabel, String> getProperties() {
        return this.others;
    }
    
    public void setProperties(final Map<Index.MetaDataLabel, String> others) {
        this.others = others;
    }
    
    @Override
    public String toString() {
        return String.format(UniqueKey.format, this.getName(), this.getUkColumns().toString().replace('[', '(').replace(']', ')'));
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    static {
        UniqueKey.format = "%s UNIQUE CONSTRAINT %s\n";
    }
}
