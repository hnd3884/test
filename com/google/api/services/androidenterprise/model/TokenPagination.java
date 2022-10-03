package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class TokenPagination extends GenericJson
{
    @Key
    private String nextPageToken;
    @Key
    private String previousPageToken;
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public TokenPagination setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public String getPreviousPageToken() {
        return this.previousPageToken;
    }
    
    public TokenPagination setPreviousPageToken(final String previousPageToken) {
        this.previousPageToken = previousPageToken;
        return this;
    }
    
    public TokenPagination set(final String fieldName, final Object value) {
        return (TokenPagination)super.set(fieldName, value);
    }
    
    public TokenPagination clone() {
        return (TokenPagination)super.clone();
    }
}
