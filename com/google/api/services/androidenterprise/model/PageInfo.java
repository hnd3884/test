package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PageInfo extends GenericJson
{
    @Key
    private Integer resultPerPage;
    @Key
    private Integer startIndex;
    @Key
    private Integer totalResults;
    
    public Integer getResultPerPage() {
        return this.resultPerPage;
    }
    
    public PageInfo setResultPerPage(final Integer resultPerPage) {
        this.resultPerPage = resultPerPage;
        return this;
    }
    
    public Integer getStartIndex() {
        return this.startIndex;
    }
    
    public PageInfo setStartIndex(final Integer startIndex) {
        this.startIndex = startIndex;
        return this;
    }
    
    public Integer getTotalResults() {
        return this.totalResults;
    }
    
    public PageInfo setTotalResults(final Integer totalResults) {
        this.totalResults = totalResults;
        return this;
    }
    
    public PageInfo set(final String fieldName, final Object value) {
        return (PageInfo)super.set(fieldName, value);
    }
    
    public PageInfo clone() {
        return (PageInfo)super.clone();
    }
}
