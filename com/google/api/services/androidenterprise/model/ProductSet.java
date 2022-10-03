package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ProductSet extends GenericJson
{
    @Key
    private List<String> productId;
    @Key
    private String productSetBehavior;
    @Key
    private List<ProductVisibility> productVisibility;
    
    public List<String> getProductId() {
        return this.productId;
    }
    
    public ProductSet setProductId(final List<String> productId) {
        this.productId = productId;
        return this;
    }
    
    public String getProductSetBehavior() {
        return this.productSetBehavior;
    }
    
    public ProductSet setProductSetBehavior(final String productSetBehavior) {
        this.productSetBehavior = productSetBehavior;
        return this;
    }
    
    public List<ProductVisibility> getProductVisibility() {
        return this.productVisibility;
    }
    
    public ProductSet setProductVisibility(final List<ProductVisibility> productVisibility) {
        this.productVisibility = productVisibility;
        return this;
    }
    
    public ProductSet set(final String fieldName, final Object value) {
        return (ProductSet)super.set(fieldName, value);
    }
    
    public ProductSet clone() {
        return (ProductSet)super.clone();
    }
}
