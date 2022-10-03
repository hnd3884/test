package com.me.ems.framework.common.api.v1.model;

import java.util.List;

public class LiteFilterGroup
{
    private String groupName;
    private String displayName;
    private List<LiteFilterValue> values;
    
    public LiteFilterGroup(final String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public List<LiteFilterValue> getValues() {
        return this.values;
    }
    
    public void setValues(final List<LiteFilterValue> values) {
        this.values = values;
    }
}
