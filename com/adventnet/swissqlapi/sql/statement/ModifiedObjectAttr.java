package com.adventnet.swissqlapi.sql.statement;

public class ModifiedObjectAttr
{
    public static final int TRUNCATED = 1;
    public static final int SPECIALCHARACTER = 2;
    public static final int KEYWORD = 3;
    private int modifiedType;
    private String originalName;
    private String modifiedName;
    private String tableName;
    
    public void setModifiedType(final int modifiedType) {
        this.modifiedType = modifiedType;
    }
    
    public void setOriginalName(final String originalName) {
        this.originalName = originalName;
    }
    
    public void setModifiedName(final String modifiedName) {
        this.modifiedName = modifiedName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public int getModifiedType() {
        return this.modifiedType;
    }
    
    public String getOriginalName() {
        return this.originalName;
    }
    
    public String getModifiedName() {
        return this.modifiedName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
}
