package com.me.ems.framework.common.api.v1.model.helpermodel;

import java.util.ArrayList;

public class FilterCardBean
{
    private Long filterID;
    private String filterDisplayName;
    private String filterValue;
    private ArrayList<FilterValueBean> filterValues;
    private Long selectedValueId;
    
    public String getFilterValue() {
        return this.filterValue;
    }
    
    public void setFilterValue(final String filterValue) {
        this.filterValue = filterValue;
    }
    
    public Long getFilterID() {
        return this.filterID;
    }
    
    public Long getSelectedValueId() {
        return this.selectedValueId;
    }
    
    public void setSelectedValueId(final Long selectedValueId) {
        this.selectedValueId = selectedValueId;
    }
    
    public void setFilterID(final Long filterID) {
        this.filterID = filterID;
    }
    
    public String getFilterDisplayKey() {
        return this.filterDisplayName;
    }
    
    public void setFilterDisplayKey(final String filterDisplayKey) {
        this.filterDisplayName = filterDisplayKey;
    }
    
    public ArrayList<FilterValueBean> getFilterValues() {
        return this.filterValues;
    }
    
    public void setFilterValues(final ArrayList<FilterValueBean> filterValues) {
        this.filterValues = filterValues;
    }
}
