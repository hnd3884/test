package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StorePage extends GenericJson
{
    @Key
    private String id;
    @Key
    private List<String> link;
    @Key
    private List<LocalizedText> name;
    
    public String getId() {
        return this.id;
    }
    
    public StorePage setId(final String id) {
        this.id = id;
        return this;
    }
    
    public List<String> getLink() {
        return this.link;
    }
    
    public StorePage setLink(final List<String> link) {
        this.link = link;
        return this;
    }
    
    public List<LocalizedText> getName() {
        return this.name;
    }
    
    public StorePage setName(final List<LocalizedText> name) {
        this.name = name;
        return this;
    }
    
    public StorePage set(final String fieldName, final Object value) {
        return (StorePage)super.set(fieldName, value);
    }
    
    public StorePage clone() {
        return (StorePage)super.clone();
    }
    
    static {
        Data.nullOf((Class)LocalizedText.class);
    }
}
