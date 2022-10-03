package com.adventnet.cache.dataobject;

import java.io.Serializable;

public class CacheObject implements Serializable
{
    private Object sourceDetails;
    private Object value;
    
    public CacheObject(final Object sourceDetails, final Object value) {
        this.sourceDetails = null;
        this.value = null;
        this.sourceDetails = sourceDetails;
        this.value = value;
    }
    
    public Object getSourceDetails() {
        return this.sourceDetails;
    }
    
    public void setSourceDetails(final Object sourceDetails) {
        this.sourceDetails = sourceDetails;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
}
