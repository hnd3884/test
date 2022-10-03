package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductsGenerateApprovalUrlResponse extends GenericJson
{
    @Key
    private String url;
    
    public String getUrl() {
        return this.url;
    }
    
    public ProductsGenerateApprovalUrlResponse setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public ProductsGenerateApprovalUrlResponse set(final String fieldName, final Object value) {
        return (ProductsGenerateApprovalUrlResponse)super.set(fieldName, value);
    }
    
    public ProductsGenerateApprovalUrlResponse clone() {
        return (ProductsGenerateApprovalUrlResponse)super.clone();
    }
}
