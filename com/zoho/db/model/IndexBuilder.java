package com.zoho.db.model;

public class IndexBuilder extends IndexBuilderBase<IndexBuilder>
{
    public static IndexBuilder index() {
        return new IndexBuilder();
    }
    
    public IndexBuilder() {
        super(new Index());
    }
    
    public Index build() {
        return this.getInstance();
    }
}
