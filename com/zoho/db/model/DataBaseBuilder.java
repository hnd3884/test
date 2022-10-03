package com.zoho.db.model;

import java.util.List;

public class DataBaseBuilder extends DataBaseBuilderBase<DataBaseBuilder>
{
    public static DataBaseBuilder dataBase() {
        return new DataBaseBuilder();
    }
    
    public DataBaseBuilder() {
        super(new DataBase());
    }
    
    public DataBase build() {
        return this.getInstance();
    }
}
