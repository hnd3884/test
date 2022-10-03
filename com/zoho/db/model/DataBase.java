package com.zoho.db.model;

import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Set;
import java.util.Map;

public class DataBase
{
    private String dbLable;
    private String dbProductName;
    private String dsName;
    private Map<String, Table> tableMap;
    private String identifierQuoteString;
    private String catalog;
    private String schema;
    private final Set<String> keyword;
    
    public DataBase() {
        this.tableMap = new TreeMap<String, Table>(String.CASE_INSENSITIVE_ORDER);
        this.identifierQuoteString = "";
        this.keyword = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    }
    
    public String getDbName() {
        return this.getCatalog();
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    public String getDsName() {
        return this.dsName;
    }
    
    void setDsName(final String dsName) {
        this.dsName = dsName;
    }
    
    public Collection<Table> getAllTables() {
        return this.tableMap.values();
    }
    
    public Set<String> getAllTableNames() {
        return this.tableMap.keySet();
    }
    
    public Table getTable(final String tableName) {
        return this.tableMap.get(tableName);
    }
    
    public void addTable(final Table table) {
        this.tableMap.put(table.getName(), table);
    }
    
    public String getIdentifierQuoteString() {
        return this.identifierQuoteString;
    }
    
    public void setIdentifierQuoteString(final String identifierQuoteString) {
        this.identifierQuoteString = identifierQuoteString;
    }
    
    public void addKeyWord(final String keyWord) {
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            this.keyword.add(keyWord.trim());
        }
    }
    
    public boolean isDBKeyWord(final String keyWordString) {
        return this.keyword.contains(keyWordString);
    }
    
    public Set<String> getKeyWords() {
        return this.keyword;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("Database product name \t:: ").append(this.getDBProductName());
        buff.append("Database name \t\t\t:: ").append(this.getDbName());
        buff.append("Schema name \t\t\t:: ").append(this.getSchema());
        buff.append("catalog name \t\t\t:: ").append(this.getCatalog()).append("\n");
        for (final Table table : this.getAllTables()) {
            buff.append(table.toString());
        }
        return buff.toString();
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    void setCatalog(final String catalog) {
        this.catalog = catalog;
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public void setSchema(final String schema) {
        this.schema = schema;
    }
    
    public String getDBProductName() {
        return this.dbProductName;
    }
    
    void setDBProductName(final String dbProductName) {
        this.dbProductName = dbProductName;
    }
}
