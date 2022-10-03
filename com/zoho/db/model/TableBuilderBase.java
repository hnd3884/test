package com.zoho.db.model;

import java.util.Map;
import java.util.Iterator;
import java.util.List;

class TableBuilderBase<GeneratorT extends TableBuilderBase<GeneratorT>>
{
    private Table instance;
    
    protected TableBuilderBase(final Table aInstance) {
        this.instance = aInstance;
    }
    
    protected Table getInstance() {
        return this.instance;
    }
    
    public GeneratorT withTableName(final String aValue) {
        this.instance.setName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDbLable(final String dbLable) {
        this.instance.setDbLable(dbLable);
        return (GeneratorT)this;
    }
    
    public GeneratorT withColumn(final Column column) {
        this.instance.addColumn(column);
        return (GeneratorT)this;
    }
    
    public GeneratorT withColumns(final List<Column> columns) {
        for (final Column column : columns) {
            this.withColumn(column);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withIndex(final Index index) {
        this.instance.addIndex(index);
        return (GeneratorT)this;
    }
    
    public GeneratorT withIndexes(final List<Index> indexes) {
        for (final Index index : indexes) {
            this.withIndex(index);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withUniqueKey(final UniqueKey uniqueKey) {
        this.instance.addUniqueKey(uniqueKey);
        return (GeneratorT)this;
    }
    
    public GeneratorT withUniqueKeys(final List<UniqueKey> uniqueKeys) {
        for (final UniqueKey uniqueKey : uniqueKeys) {
            this.withUniqueKey(uniqueKey);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withForeignKey(final ForeignKey foreignKey) {
        this.instance.addForeignKey(foreignKey);
        return (GeneratorT)this;
    }
    
    public GeneratorT withForeignKeys(final List<ForeignKey> foreignKeys) {
        for (final ForeignKey foreignKey : foreignKeys) {
            this.withForeignKey(foreignKey);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withPrimaryKey(final PrimaryKey aValue) {
        if (this.instance.getPrimaryKey() != null) {
            throw new IllegalArgumentException("Table cannot have more than one PK.");
        }
        this.instance.setPrimaryKey(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withOthers(final Map<String, String> aValue) {
        this.instance.setOthers(aValue);
        return (GeneratorT)this;
    }
}
