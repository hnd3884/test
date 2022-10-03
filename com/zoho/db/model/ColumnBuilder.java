package com.zoho.db.model;

public class ColumnBuilder extends ColumnBuilderBase<ColumnBuilder>
{
    public static ColumnBuilder column() {
        return new ColumnBuilder();
    }
    
    public ColumnBuilder() {
        super(new Column());
    }
    
    public Column build() {
        return this.getInstance();
    }
}
