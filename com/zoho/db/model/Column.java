package com.zoho.db.model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Column implements Comparable<Column>
{
    private String name;
    private String tableName;
    private int sqlType;
    private String type;
    private String typeLable;
    private int maxSize;
    private int precision;
    private boolean isNullable;
    private String defaultValue;
    private boolean isUnique;
    private PrimaryKey primaryKey;
    private String dbLable;
    private List<ForeignKey> foreignKeys;
    private List<Index> indexes;
    private List<UniqueKey> uniqueKeys;
    private int ordinalPosition;
    private Map<MetaDataLable, String> properties;
    private static String format;
    
    public Column() {
        this.foreignKeys = new ArrayList<ForeignKey>();
        this.indexes = new ArrayList<Index>();
        this.uniqueKeys = new ArrayList<UniqueKey>();
        this.properties = new HashMap<MetaDataLable, String>();
    }
    
    public String getName() {
        return this.name;
    }
    
    void setName(final String name) {
        this.name = name;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public int getSqlType() {
        return this.sqlType;
    }
    
    void setSqlType(final int sqlType) {
        this.sqlType = sqlType;
    }
    
    public String getType() {
        return this.type;
    }
    
    void setType(final String type) {
        this.type = type;
    }
    
    public String getTypeLable() {
        return this.typeLable;
    }
    
    void setTypeLable(final String typeLable) {
        this.typeLable = typeLable;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    void setPrecision(final int precision) {
        this.precision = precision;
    }
    
    public boolean isNullable() {
        return this.isNullable;
    }
    
    void setNullable(final boolean isNullable) {
        this.isNullable = isNullable;
    }
    
    public String getDefaultValue() {
        if (this.getPrimaryKey() != null) {
            return null;
        }
        return this.defaultValue;
    }
    
    void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean isUnique() {
        return this.isUnique;
    }
    
    void setUnique(final boolean isUnique) {
        this.isUnique = isUnique;
    }
    
    public PrimaryKey getPrimaryKey() {
        return this.primaryKey;
    }
    
    void setPrimaryKey(final PrimaryKey pKey) {
        this.primaryKey = pKey;
    }
    
    public List<ForeignKey> getForeignKeys() {
        return this.foreignKeys;
    }
    
    public void addForeignKeys(final ForeignKey foreignKey) {
        this.foreignKeys.add(foreignKey);
    }
    
    public List<Index> getIndexes() {
        return this.indexes;
    }
    
    public void addIndex(final Index index) {
        this.indexes.add(index);
    }
    
    public List<UniqueKey> getUniqueKeys() {
        return this.uniqueKeys;
    }
    
    void addUniqueKeys(final UniqueKey uniqueKey) {
        this.uniqueKeys.add(uniqueKey);
    }
    
    public Integer getOrdinalPosition() {
        return this.ordinalPosition;
    }
    
    void setOrdinalPosition(final int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }
    
    public Map<MetaDataLable, String> getOthers() {
        return this.properties;
    }
    
    void setProperties(final Map<MetaDataLable, String> others) {
        this.properties = others;
    }
    
    @Override
    public String toString() {
        return String.format(Column.format, this.getName(), this.getType(), this.getTypeLable(), this.getMaxSize(), this.isNullable(), (this.getDefaultValue() == null) ? "" : this.getDefaultValue(), this.isUnique());
    }
    
    @Override
    public int compareTo(final Column o) {
        return this.getOrdinalPosition().compareTo(o.getOrdinalPosition());
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    static {
        Column.format = "%-20s %-10s %-10s %-15s %-9s %-15s %-5s \n";
    }
    
    public enum MetaDataLable
    {
        TABLE_CAT, 
        TABLE_SCHEM, 
        TABLE_NAME, 
        COLUMN_NAME, 
        DATA_TYPE, 
        TYPE_NAME, 
        COLUMN_SIZE, 
        BUFFER_LENGTH, 
        DECIMAL_DIGITS, 
        NUM_PREC_RADIX, 
        NULLABLE, 
        REMARKS, 
        COLUMN_DEF, 
        SQL_DATA_TYPE, 
        SQL_DATETIME_SUB, 
        CHAR_OCTET_LENGTH, 
        ORDINAL_POSITION, 
        IS_NULLABLE, 
        IS_AUTOINCREMENT;
    }
}
