package com.zoho.db.model;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;

class IndexBuilderBase<GeneratorT extends IndexBuilderBase<GeneratorT>>
{
    private Index instance;
    
    protected IndexBuilderBase(final Index aInstance) {
        this.instance = aInstance;
    }
    
    protected Index getInstance() {
        return this.instance;
    }
    
    public GeneratorT withName(final String aValue) {
        this.instance.setName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withColumn(final String aValue) {
        this.instance.addIndexColumnName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withTable(final Table aValue) {
        this.instance.setTable(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDbLable(final String dbLable) {
        this.instance.setDbLable(dbLable);
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
                                final Set<String> columnNames = this.instance.getTable().getColumnNames();
                                throw new IllegalArgumentException("Index creation happening for unknown column " + colName + ". Column available in the table " + this.instance.getTable().getColumnNames());
                            }
                            this.withColumn(colName);
                            this.instance.getTable().getColumn(colName).addIndex(this.instance);
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
