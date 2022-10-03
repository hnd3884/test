package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class TableCollectionExpression
{
    private String table;
    private String optionalPlus;
    public ArrayList collectionExpList;
    
    public TableCollectionExpression() {
        this.collectionExpList = new ArrayList();
        this.table = new String();
        this.optionalPlus = new String();
    }
    
    public void setTable(final String s) {
        this.table = s;
    }
    
    public void setOptionalPlus(final String s) {
        this.optionalPlus = s;
    }
    
    public void setCollectionExpList(final ArrayList al) {
        this.collectionExpList = al;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public String getOptionalPlus() {
        return this.optionalPlus;
    }
    
    public ArrayList getCollectionExpList() {
        return this.collectionExpList;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(this.table);
        for (int i = 0, size = this.collectionExpList.size(); i < size; ++i) {
            stringbuffer.append(this.collectionExpList.get(i).toString());
        }
        if (this.optionalPlus != null) {
            stringbuffer.append(this.optionalPlus);
        }
        return stringbuffer.toString();
    }
}
