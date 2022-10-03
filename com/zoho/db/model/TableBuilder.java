package com.zoho.db.model;

public class TableBuilder extends TableBuilderBase<TableBuilder>
{
    public static TableBuilder table() {
        return new TableBuilder();
    }
    
    public TableBuilder() {
        super(new Table());
    }
    
    public Table build() {
        return this.getInstance();
    }
}
