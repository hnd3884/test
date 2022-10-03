package com.zoho.db.model;

import java.util.Iterator;
import java.util.Map;

class ForeignKeyBuilderBase<GeneratorT extends ForeignKeyBuilderBase<GeneratorT>>
{
    private ForeignKey instance;
    
    protected ForeignKeyBuilderBase(final ForeignKey aInstance) {
        this.instance = aInstance;
    }
    
    protected ForeignKey getInstance() {
        return this.instance;
    }
    
    public GeneratorT withName(final String aValue) {
        this.instance.setName(aValue);
        return (GeneratorT)this;
    }
    
    public GeneratorT withdbLable(final String dbLable) {
        this.instance.setDbLable(dbLable);
        return (GeneratorT)this;
    }
    
    public GeneratorT inTable(final String name) {
        this.instance.setTableName(name);
        return (GeneratorT)this;
    }
    
    public GeneratorT withParentTable(final String parentTable) {
        if (this.instance.getName() == null) {
            throw new IllegalArgumentException("withParentTable() cannot be invoked before inTable()");
        }
        this.instance.setParentTableName(parentTable);
        return (GeneratorT)this;
    }
    
    public GeneratorT withDeleteRule(final int deleteRule) {
        this.instance.setDeleteRule(deleteRule);
        return (GeneratorT)this;
    }
    
    public GeneratorT withProperties(final Map<ForeignKey.MetaDataLabel, String> props) {
        this.instance.setProperties(props);
        for (final ForeignKey.MetaDataLabel label : props.keySet()) {
            switch (label) {
                case DELETE_RULE: {
                    this.withDeleteRule(Integer.parseInt(props.get(label)));
                    continue;
                }
                case FKTABLE_NAME: {
                    if (this.instance.getName() == null || this.instance.getParentTableName() == null) {
                        throw new IllegalArgumentException("withProperties cannot be invoked before setting inTable() & withTable()");
                    }
                    if (!this.instance.getTableName().equalsIgnoreCase(props.get(ForeignKey.MetaDataLabel.FKTABLE_NAME))) {
                        throw new IllegalArgumentException("withProperties invoked with wrong props[FKTABLE_NAME]");
                    }
                    continue;
                }
                case FK_NAME: {
                    if (this.instance.getName() == null) {
                        this.withName(props.get(ForeignKey.MetaDataLabel.FK_NAME));
                        continue;
                    }
                    if (!this.instance.getName().equalsIgnoreCase(props.get(ForeignKey.MetaDataLabel.FK_NAME))) {
                        throw new IllegalArgumentException("withProperties invoked with wrong props[FK_NAME]");
                    }
                    continue;
                }
            }
        }
        return (GeneratorT)this;
    }
}
