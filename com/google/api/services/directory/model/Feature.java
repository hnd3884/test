package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Feature extends GenericJson
{
    @Key
    private String etags;
    @Key
    private String kind;
    @Key
    private String name;
    
    public String getEtags() {
        return this.etags;
    }
    
    public Feature setEtags(final String etags) {
        this.etags = etags;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Feature setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Feature setName(final String name) {
        this.name = name;
        return this;
    }
    
    public Feature set(final String fieldName, final Object value) {
        return (Feature)super.set(fieldName, value);
    }
    
    public Feature clone() {
        return (Feature)super.clone();
    }
}
