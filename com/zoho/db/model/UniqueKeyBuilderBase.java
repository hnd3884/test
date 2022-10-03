package com.zoho.db.model;

import java.util.Iterator;
import java.util.Map;

class UniqueKeyBuilderBase<GeneratorT extends UniqueKeyBuilderBase<GeneratorT>>
{
    private UniqueKey instance;
    
    protected UniqueKeyBuilderBase(final UniqueKey aInstance) {
        this.instance = aInstance;
    }
    
    protected UniqueKey getInstance() {
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
    
    public GeneratorT withColumn(final String aValue) {
        this.instance.addUkColumns(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withTable(final Table aValue) {
        this.instance.setTable(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withProperties(final Map<Index.MetaDataLabel, String> props) {
        this.instance.setProperties(props);
        if (this.instance.getTable() == null) {
            throw new IllegalArgumentException("withProperties() cannot be invoked before calling withTable(..)");
        }
        for (final Index.MetaDataLabel label : props.keySet()) {
            switch (label) {
                case COLUMN_NAME: {
                    for (String colName : props.get(label).split("[,\\[\\]]")) {
                        colName = colName.trim().replace("\"", "");
                        if (!colName.isEmpty()) {
                            if (!this.instance.getTable().containsColumn(colName)) {
                                throw new IllegalArgumentException("UK creation happening for unknown column " + colName + ". Column available in the table " + this.instance.getTable().getColumnNames());
                            }
                            this.withColumn(colName);
                            this.instance.getTable().getColumn(colName).addUniqueKeys(this.instance);
                        }
                    }
                    continue;
                }
                case INDEX_NAME: {
                    this.withName(props.get(label));
                    continue;
                }
            }
        }
        return (GeneratorT)this;
    }
}
