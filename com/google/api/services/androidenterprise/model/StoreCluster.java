package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StoreCluster extends GenericJson
{
    @Key
    private String id;
    @Key
    private List<LocalizedText> name;
    @Key
    private String orderInPage;
    @Key
    private List<String> productId;
    
    public String getId() {
        return this.id;
    }
    
    public StoreCluster setId(final String id) {
        this.id = id;
        return this;
    }
    
    public List<LocalizedText> getName() {
        return this.name;
    }
    
    public StoreCluster setName(final List<LocalizedText> name) {
        this.name = name;
        return this;
    }
    
    public String getOrderInPage() {
        return this.orderInPage;
    }
    
    public StoreCluster setOrderInPage(final String orderInPage) {
        this.orderInPage = orderInPage;
        return this;
    }
    
    public List<String> getProductId() {
        return this.productId;
    }
    
    public StoreCluster setProductId(final List<String> productId) {
        this.productId = productId;
        return this;
    }
    
    public StoreCluster set(final String fieldName, final Object value) {
        return (StoreCluster)super.set(fieldName, value);
    }
    
    public StoreCluster clone() {
        return (StoreCluster)super.clone();
    }
    
    static {
        Data.nullOf((Class)LocalizedText.class);
    }
}
