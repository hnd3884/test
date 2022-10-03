package com.zoho.db.model;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;

public class Table
{
    private String tableName;
    private String dbLable;
    private Map<String, Column> columns;
    private PrimaryKey primaryKey;
    private Map<String, Index> indexes;
    private Map<String, UniqueKey> uniqueKeys;
    private Map<String, ForeignKey> foreignKeys;
    private Map<String, String> others;
    private static String format;
    private static String col_format;
    
    public Table() {
        this.columns = new TreeMap<String, Column>(String.CASE_INSENSITIVE_ORDER);
        this.indexes = new TreeMap<String, Index>(String.CASE_INSENSITIVE_ORDER);
        this.uniqueKeys = new TreeMap<String, UniqueKey>(String.CASE_INSENSITIVE_ORDER);
        this.foreignKeys = new TreeMap<String, ForeignKey>(String.CASE_INSENSITIVE_ORDER);
        this.others = new HashMap<String, String>();
    }
    
    public String getName() {
        return this.tableName;
    }
    
    public void setName(final String tableName) {
        this.tableName = tableName;
    }
    
    public Collection<Column> getColumns() {
        final List<Column> asList = Arrays.asList((Column[])this.columns.values().toArray((T[])new Column[this.columns.values().size()]));
        Collections.sort(asList);
        return asList;
    }
    
    public Column getColumn(final String columnName) {
        if (this.columns.containsKey(columnName)) {
            return this.columns.get(columnName);
        }
        throw new IllegalArgumentException("Unknown column name " + columnName + " in the table " + this.tableName + " .");
    }
    
    public boolean containsColumn(final String columnName) {
        return this.columns.containsKey(columnName);
    }
    
    public Set<String> getColumnNames() {
        return this.columns.keySet();
    }
    
    public void addColumn(final Column column) {
        this.columns.put(column.getName(), column);
    }
    
    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }
    
    public void setPrimaryKey(final PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
        for (final String columnName : primaryKey.getPkColumnNames()) {
            this.getColumn(columnName).setPrimaryKey(primaryKey);
        }
    }
    
    public Collection<Index> getIndexes() {
        return this.indexes.values();
    }
    
    public Index getIndex(final String indexName) {
        return this.indexes.get(indexName);
    }
    
    public void addIndex(final Index index) {
        this.indexes.put(index.getName(), index);
    }
    
    public Collection<UniqueKey> getUniqueKeys() {
        return this.uniqueKeys.values();
    }
    
    public UniqueKey getUniqueKey(final String ukName) {
        return this.uniqueKeys.get(ukName);
    }
    
    public void addUniqueKey(final UniqueKey uniqueKey) {
        this.uniqueKeys.put(uniqueKey.getName(), uniqueKey);
    }
    
    public Collection<ForeignKey> getForeignKeys() {
        return this.foreignKeys.values();
    }
    
    public ForeignKey getForeignKey(final String fkName) {
        return this.foreignKeys.get(fkName);
    }
    
    public boolean containsForeignKey() {
        return !this.foreignKeys.isEmpty();
    }
    
    public boolean containsForeignKey(final String fkName) {
        return this.foreignKeys.containsKey(fkName);
    }
    
    public synchronized void addForeignKey(final ForeignKey foreignKey) {
        this.foreignKeys.put(foreignKey.getName(), foreignKey);
    }
    
    public Map<String, String> getOthers() {
        return this.others;
    }
    
    public void setOthers(final Map<String, String> others) {
        this.others = others;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append(String.format(Table.format, "Table name", this.getName()));
        buff.append(String.format(Table.col_format, "Column", "Type", "Lable", "Max-size", "Nullable", "Default", "Unique"));
        for (final Column column : this.getColumns()) {
            buff.append(column.toString());
        }
        buff.append("\nPrimary-key:\n");
        buff.append(this.getPrimaryKey().toString());
        buff.append("\n");
        if (!this.getIndexes().isEmpty()) {
            buff.append("Indexes:\n");
            for (final Index index : this.getIndexes()) {
                buff.append(index.toString());
            }
            buff.append("\n");
        }
        if (!this.getUniqueKeys().isEmpty()) {
            buff.append("Unique-key constraints:\n");
            for (final UniqueKey uniqueKey : this.getUniqueKeys()) {
                buff.append(uniqueKey.toString());
            }
            buff.append("\n");
        }
        if (!this.getForeignKeys().isEmpty()) {
            buff.append("Foreign-key constraints:\n");
            for (final ForeignKey foreignKey : this.getForeignKeys()) {
                buff.append(foreignKey.toString());
            }
            buff.append("\n");
        }
        buff.append("\n");
        return buff.toString();
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    static {
        Table.format = "%-15s :: %-25s\n\n";
        Table.col_format = "%-20s %-10s %-10s %-15s %-5s %-15s %-5s \n";
    }
    
    public enum MetaDataLabel
    {
        TABLE_CAT, 
        TABLE_SCHEM, 
        TABLE_NAME, 
        TABLE_TYPE, 
        REMARKS, 
        TYPE_CAT, 
        TYPE_SCHEM, 
        TYPE_NAME, 
        SELF_REFERENCING_COL_NAME, 
        REF_GENERATION;
    }
}
