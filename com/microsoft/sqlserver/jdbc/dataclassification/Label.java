package com.microsoft.sqlserver.jdbc.dataclassification;

public class Label
{
    private String name;
    private String id;
    
    public Label(final String name, final String id) {
        this.name = name;
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
}
