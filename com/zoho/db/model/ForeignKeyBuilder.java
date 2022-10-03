package com.zoho.db.model;

public class ForeignKeyBuilder extends ForeignKeyBuilderBase<ForeignKeyBuilder>
{
    public static ForeignKeyBuilder foreignKey() {
        return new ForeignKeyBuilder();
    }
    
    public ForeignKeyBuilder() {
        super(new ForeignKey());
    }
    
    public ForeignKey build() {
        return this.getInstance();
    }
}
