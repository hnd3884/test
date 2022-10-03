package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class StoreLayoutPagesListResponse extends GenericJson
{
    @Key
    private List<StorePage> page;
    
    public List<StorePage> getPage() {
        return this.page;
    }
    
    public StoreLayoutPagesListResponse setPage(final List<StorePage> page) {
        this.page = page;
        return this;
    }
    
    public StoreLayoutPagesListResponse set(final String fieldName, final Object value) {
        return (StoreLayoutPagesListResponse)super.set(fieldName, value);
    }
    
    public StoreLayoutPagesListResponse clone() {
        return (StoreLayoutPagesListResponse)super.clone();
    }
}
