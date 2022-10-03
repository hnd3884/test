package com.zoho.db.model;

public class UniqueKeyBuilder extends UniqueKeyBuilderBase<UniqueKeyBuilder>
{
    public static UniqueKeyBuilder uniqueKey() {
        return new UniqueKeyBuilder();
    }
    
    public UniqueKeyBuilder() {
        super(new UniqueKey());
    }
    
    public UniqueKey build() {
        return this.getInstance();
    }
}
