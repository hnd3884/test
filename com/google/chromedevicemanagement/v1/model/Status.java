package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.Map;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Status extends GenericJson
{
    @Key
    private Integer code;
    @Key
    private List<Map<String, Object>> details;
    @Key
    private String message;
    
    public Integer getCode() {
        return this.code;
    }
    
    public Status setCode(final Integer code) {
        this.code = code;
        return this;
    }
    
    public List<Map<String, Object>> getDetails() {
        return this.details;
    }
    
    public Status setDetails(final List<Map<String, Object>> details) {
        this.details = details;
        return this;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Status setMessage(final String message) {
        this.message = message;
        return this;
    }
    
    public Status set(final String s, final Object o) {
        return (Status)super.set(s, o);
    }
    
    public Status clone() {
        return (Status)super.clone();
    }
}
