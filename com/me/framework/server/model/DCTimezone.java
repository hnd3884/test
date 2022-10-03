package com.me.framework.server.model;

public class DCTimezone
{
    private String id;
    private String offset;
    private String name;
    private String displayName;
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getOffset() {
        return this.offset;
    }
    
    public void setOffset(final String offset) {
        this.offset = offset;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
