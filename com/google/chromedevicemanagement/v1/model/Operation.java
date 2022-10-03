package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.Map;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Operation extends GenericJson
{
    @Key
    private Boolean done;
    @Key
    private Status error;
    @Key
    private Map<String, Object> metadata;
    @Key
    private String name;
    @Key
    private Map<String, Object> response;
    
    public Boolean getDone() {
        return this.done;
    }
    
    public Operation setDone(final Boolean done) {
        this.done = done;
        return this;
    }
    
    public Status getError() {
        return this.error;
    }
    
    public Operation setError(final Status error) {
        this.error = error;
        return this;
    }
    
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }
    
    public Operation setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Operation setName(final String name) {
        this.name = name;
        return this;
    }
    
    public Map<String, Object> getResponse() {
        return this.response;
    }
    
    public Operation setResponse(final Map<String, Object> response) {
        this.response = response;
        return this;
    }
    
    public Operation set(final String s, final Object o) {
        return (Operation)super.set(s, o);
    }
    
    public Operation clone() {
        return (Operation)super.clone();
    }
}
