package com.zoho.db.model;

import java.util.Iterator;
import java.util.Map;

class PrimaryKeyBuilderBase<GeneratorT extends PrimaryKeyBuilderBase<GeneratorT>>
{
    private PrimaryKey instance;
    
    protected PrimaryKeyBuilderBase(final PrimaryKey aInstance) {
        this.instance = aInstance;
    }
    
    protected PrimaryKey getInstance() {
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
    
    public GeneratorT withTable(final Table aValue) {
        this.instance.setTable(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withColumn(final String columnName) {
        this.instance.addPkColumn(columnName);
        return (GeneratorT)this;
    }
    
    public GeneratorT withProperties(final Map<PrimaryKey.MetaDataLabel, String> props) {
        if (this.instance.getTable() == null) {
            throw new IllegalArgumentException("withProperties() cannot be called before calling withTable()");
        }
        this.instance.setProperties(props);
        for (final PrimaryKey.MetaDataLabel metaDataLabel : props.keySet()) {
            switch (metaDataLabel) {
                case COLUMNS_IN_ORDER: {
                    for (String colName : props.get(metaDataLabel).split("[,\\[\\]]")) {
                        colName = colName.trim();
                        if (!colName.isEmpty()) {
                            this.withColumn(colName);
                        }
                    }
                }
                case COLUMN_NAME:
                case TABLE_CAT:
                case KEY_SEQ:
                case TABLE_NAME:
                case TABLE_SCHEM: {
                    continue;
                }
                case PK_NAME: {
                    this.instance.setName(props.get(metaDataLabel));
                    continue;
                }
            }
        }
        this.instance.getTable().setPrimaryKey(this.instance);
        return (GeneratorT)this;
    }
}
