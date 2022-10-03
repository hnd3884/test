package com.zoho.db.model;

import java.util.Iterator;
import java.util.Map;

class ColumnBuilderBase<GeneratorT extends ColumnBuilderBase<GeneratorT>>
{
    private Column instance;
    
    protected ColumnBuilderBase(final Column aInstance) {
        this.instance = aInstance;
    }
    
    protected Column getInstance() {
        return this.instance;
    }
    
    public GeneratorT withName(final String aValue) {
        this.instance.setName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDbLable(final String dbLable) {
        this.instance.setDbLable(dbLable);
        return (GeneratorT)this;
    }
    
    public GeneratorT withTableName(final String aValue) {
        this.instance.setTableName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withSqlType(final int aValue) {
        this.instance.setSqlType(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withType(final String aValue) {
        this.instance.setType(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withTypeLable(final String aValue) {
        this.instance.setTypeLable(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withMaxSize(final int aValue) {
        this.instance.setMaxSize(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withPrecision(final int aValue) {
        this.instance.setPrecision(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withNullable(final boolean aValue) {
        this.instance.setNullable(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDefaultValue(final String aValue) {
        this.instance.setDefaultValue(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withUnique(final boolean aValue) {
        this.instance.setUnique(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withPrimaryKey(final PrimaryKey aValue) {
        this.instance.setPrimaryKey(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withUniqueKeys(final UniqueKey aValue) {
        this.instance.addUniqueKeys(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withOrdinalPosition(final int ordinal) {
        this.instance.setOrdinalPosition(ordinal);
        return (GeneratorT)this;
    }
    
    public GeneratorT withProperties(final Map<Column.MetaDataLable, String> props) {
        this.instance.setProperties(props);
        for (final Column.MetaDataLable label : props.keySet()) {
            switch (label) {
                case TABLE_NAME: {
                    this.withTableName(props.get(label));
                    continue;
                }
                case COLUMN_NAME: {
                    this.withName(props.get(label));
                    continue;
                }
                case DATA_TYPE: {
                    this.withSqlType(Integer.parseInt(props.get(label)));
                    continue;
                }
                case TYPE_NAME: {
                    this.withType(props.get(label));
                    continue;
                }
                case COLUMN_SIZE: {
                    this.withMaxSize(Integer.parseInt(props.get(label)));
                    continue;
                }
                case DECIMAL_DIGITS: {
                    this.withPrecision(Integer.parseInt(props.get(label)));
                    continue;
                }
                case COLUMN_DEF: {
                    this.withDefaultValue(props.get(label));
                    continue;
                }
                case ORDINAL_POSITION: {
                    this.withOrdinalPosition(Integer.parseInt(props.get(label)));
                    continue;
                }
                case IS_NULLABLE: {
                    this.withNullable("YES".equalsIgnoreCase(props.get(label)));
                    continue;
                }
            }
        }
        return (GeneratorT)this;
    }
}
