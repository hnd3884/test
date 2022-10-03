package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class StoreLayoutClustersListResponse extends GenericJson
{
    @Key
    private List<StoreCluster> cluster;
    
    public List<StoreCluster> getCluster() {
        return this.cluster;
    }
    
    public StoreLayoutClustersListResponse setCluster(final List<StoreCluster> cluster) {
        this.cluster = cluster;
        return this;
    }
    
    public StoreLayoutClustersListResponse set(final String fieldName, final Object value) {
        return (StoreLayoutClustersListResponse)super.set(fieldName, value);
    }
    
    public StoreLayoutClustersListResponse clone() {
        return (StoreLayoutClustersListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)StoreCluster.class);
    }
}
