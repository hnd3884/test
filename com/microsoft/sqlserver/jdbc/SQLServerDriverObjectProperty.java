package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverObjectProperty
{
    GSS_CREDENTIAL("gsscredential", (String)null);
    
    private final String name;
    private final String defaultValue;
    
    private SQLServerDriverObjectProperty(final String name, final String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
