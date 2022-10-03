package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductApprovalEvent extends GenericJson
{
    @Key
    private String approved;
    @Key
    private String productId;
    
    public String getApproved() {
        return this.approved;
    }
    
    public ProductApprovalEvent setApproved(final String approved) {
        this.approved = approved;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public ProductApprovalEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public ProductApprovalEvent set(final String fieldName, final Object value) {
        return (ProductApprovalEvent)super.set(fieldName, value);
    }
    
    public ProductApprovalEvent clone() {
        return (ProductApprovalEvent)super.clone();
    }
}
