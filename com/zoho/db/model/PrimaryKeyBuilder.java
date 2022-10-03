package com.zoho.db.model;

public class PrimaryKeyBuilder extends PrimaryKeyBuilderBase<PrimaryKeyBuilder>
{
    public static PrimaryKeyBuilder primaryKey() {
        return new PrimaryKeyBuilder();
    }
    
    public PrimaryKeyBuilder() {
        super(new PrimaryKey());
    }
    
    public PrimaryKey build() {
        return this.getInstance();
    }
}
