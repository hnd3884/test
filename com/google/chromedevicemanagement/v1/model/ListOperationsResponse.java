package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ListOperationsResponse extends GenericJson
{
    @Key
    private String nextPageToken;
    @Key
    private List<Operation> operations;
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public ListOperationsResponse setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public List<Operation> getOperations() {
        return this.operations;
    }
    
    public ListOperationsResponse setOperations(final List<Operation> operations) {
        this.operations = operations;
        return this;
    }
    
    public ListOperationsResponse set(final String s, final Object o) {
        return (ListOperationsResponse)super.set(s, o);
    }
    
    public ListOperationsResponse clone() {
        return (ListOperationsResponse)super.clone();
    }
}
