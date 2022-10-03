package com.me.ems.framework.common.api.v1.model;

public class ChatQuery
{
    private String previousSearchQuery;
    private String type;
    private String id;
    private String action;
    
    public String getPreviousSearchQuery() {
        return this.previousSearchQuery;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getAction() {
        return this.action;
    }
    
    public void setAction(final String action) {
        this.action = action;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setPreviousSearchQuery(final String previousSearchQuery) {
        this.previousSearchQuery = previousSearchQuery;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
}
