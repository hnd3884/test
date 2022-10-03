package com.me.ems.framework.common.api.v1.model.helpermodel;

public class FilterValueBean
{
    private Long filterValueID;
    private String valueDisplayName;
    private String valueName;
    
    public Long getFilterValueID() {
        return this.filterValueID;
    }
    
    public void setFilterValueID(final Long filterValueID) {
        this.filterValueID = filterValueID;
    }
    
    public String getValueDisplayKey() {
        return this.valueDisplayName;
    }
    
    public void setValueDisplayKey(final String valueDisplayKey) {
        this.valueDisplayName = valueDisplayKey;
    }
    
    public void setValueName(final String valueName) {
        this.valueName = valueName;
    }
    
    public String getValueName() {
        return this.valueName;
    }
}
