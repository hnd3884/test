package com.adventnet.swissqlapi.sql.statement.create;

public class DatabaseObject
{
    private String name;
    private String size;
    
    public String getName() {
        return this.name;
    }
    
    public String getSize() {
        return this.size;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setSize(final String size) {
        this.size = size;
    }
}
