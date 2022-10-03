package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductAvailabilityChangeEvent extends GenericJson
{
    @Key
    private String availabilityStatus;
    @Key
    private String productId;
    
    public String getAvailabilityStatus() {
        return this.availabilityStatus;
    }
    
    public ProductAvailabilityChangeEvent setAvailabilityStatus(final String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public ProductAvailabilityChangeEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public ProductAvailabilityChangeEvent set(final String fieldName, final Object value) {
        return (ProductAvailabilityChangeEvent)super.set(fieldName, value);
    }
    
    public ProductAvailabilityChangeEvent clone() {
        return (ProductAvailabilityChangeEvent)super.clone();
    }
}
