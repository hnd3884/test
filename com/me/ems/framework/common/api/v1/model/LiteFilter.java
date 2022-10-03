package com.me.ems.framework.common.api.v1.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class LiteFilter
{
    private String name;
    private List<LiteFilterGroup> filterGroups;
    
    public LiteFilter(final String name) {
        this.name = name;
    }
    
    @JsonIgnore
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<LiteFilterGroup> getFilterGroups() {
        return this.filterGroups;
    }
    
    public void setFilterGroups(final List<LiteFilterGroup> filterGroups) {
        this.filterGroups = filterGroups;
    }
    
    public LiteFilter(final String filterName, final String groupName, final String grpDisplayName, final List<LiteFilterValue> filterValues) {
        this.name = filterName;
        final LiteFilterGroup liteFilterGroup = new LiteFilterGroup(groupName);
        final List<LiteFilterGroup> liteFilterGroupList = new ArrayList<LiteFilterGroup>();
        liteFilterGroup.setDisplayName(grpDisplayName);
        liteFilterGroup.setValues(filterValues);
        liteFilterGroupList.add(liteFilterGroup);
        this.setFilterGroups(liteFilterGroupList);
    }
}
