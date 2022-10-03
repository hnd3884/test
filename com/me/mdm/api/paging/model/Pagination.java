package com.me.mdm.api.paging.model;

import com.me.mdm.api.delta.DeltaTokenUtil;
import com.me.mdm.api.paging.PagingUtil;
import javax.ws.rs.core.UriInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination extends BaseAPIModel
{
    private boolean selectAll;
    private int limit;
    private int offset;
    private String skipToken;
    private String deltaToken;
    private String orderBy;
    private String sortOrder;
    private String searchField;
    private String searchKey;
    
    public Pagination() {
    }
    
    @JsonProperty("orderby")
    public String getOrderBy() {
        return this.orderBy;
    }
    
    public void setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
    }
    
    @JsonProperty("sortorder")
    public String getSortOrder() {
        return this.sortOrder;
    }
    
    public void setSortOrder(final String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @JsonProperty("searchfield")
    public String getSearchField() {
        return this.searchField;
    }
    
    public void setSearchField(final String searchField) {
        this.searchField = searchField;
    }
    
    @JsonProperty("searchkey")
    public String getSearchKey() {
        return this.searchKey;
    }
    
    public void setSearchKey(final String searchKey) {
        this.searchKey = searchKey;
    }
    
    @JsonProperty("select_all")
    public boolean isSelectAll() {
        return this.selectAll;
    }
    
    public void setSelectAll(final boolean selectAll) {
        this.selectAll = selectAll;
    }
    
    @JsonProperty("limit")
    public int getLimit() {
        return this.limit;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
    
    @JsonProperty("offset")
    public int getOffset() {
        return this.offset;
    }
    
    public void setOffset(final int offset) {
        this.offset = offset;
    }
    
    @JsonProperty("skip-token")
    public String getSkipToken() {
        return this.skipToken;
    }
    
    public void setSkipToken(final String skipToken) {
        this.skipToken = skipToken;
    }
    
    @JsonProperty("delta-token")
    public String getDeltaToken() {
        return this.deltaToken;
    }
    
    public void setDeltaToken(final String deltaToken) {
        this.deltaToken = deltaToken;
    }
    
    public Pagination(final boolean selectAll, final int limit, final int offset, final String orderBy, final String sortOrder, final String searchField, final String searchKey, final UriInfo uriInfo) {
        this.selectAll = selectAll;
        this.limit = limit;
        this.offset = offset;
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        this.searchField = searchField;
        this.searchKey = searchKey;
    }
    
    public PagingUtil getPagingUtil() {
        if (this.skipToken != null) {
            return new PagingUtil(this.skipToken, this.getRequestUri());
        }
        return new PagingUtil(this.limit, this.offset, this.orderBy, this.sortOrder, this.searchField, this.searchKey, this.getRequestUri());
    }
    
    public DeltaTokenUtil getDeltaTokenUtil() {
        if (this.deltaToken != null) {
            return new DeltaTokenUtil(this.deltaToken, this.getRequestUri());
        }
        return null;
    }
}
