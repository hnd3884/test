package com.me.devicemanagement.framework.common.api.v1.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DCViewFilterCriteria
{
    private Long columnID;
    private List searchValue;
    private String comparator;
    @JsonProperty("customComparator")
    private String customComparator;
    private String logicalOperator;
    
    public DCViewFilterCriteria() {
        this.customComparator = "--";
    }
    
    public Long getColumnID() {
        return this.columnID;
    }
    
    public void setColumnID(final Long columnID) {
        this.columnID = columnID;
    }
    
    public String getComparator() {
        return this.comparator;
    }
    
    public void setComparator(final String comparator) {
        this.comparator = comparator;
    }
    
    public List getSearchValue() {
        return this.searchValue = (List)this.searchValue.stream().map(Object::toString).collect(Collectors.toList());
    }
    
    public void setSearchValue(final List searchValue) {
        this.searchValue = searchValue;
    }
    
    public void addSearchValue(final String searchValue) {
        (this.searchValue = ((this.searchValue == null) ? new ArrayList() : this.searchValue)).add(searchValue);
    }
    
    public String getCustomComparator() {
        return this.customComparator;
    }
    
    @JsonSetter("customComparator")
    public void setCustomComparator(final String customComparator) {
        if (customComparator != null) {
            this.customComparator = customComparator;
        }
    }
    
    public String getLogicalOperator() {
        return this.logicalOperator;
    }
    
    public void setLogicalOperator(final String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
    
    public static DCViewFilterCriteria dcViewFilterCriteriaMapper(final JSONObject jsonObject) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final DCViewFilterCriteria dcViewFilterCriteria = (DCViewFilterCriteria)mapper.readValue(jsonObject.toString(), (Class)DCViewFilterCriteria.class);
        return dcViewFilterCriteria;
    }
}
