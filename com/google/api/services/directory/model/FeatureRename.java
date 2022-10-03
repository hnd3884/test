package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class FeatureRename extends GenericJson
{
    @Key
    private String newName;
    
    public String getNewName() {
        return this.newName;
    }
    
    public FeatureRename setNewName(final String newName) {
        this.newName = newName;
        return this;
    }
    
    public FeatureRename set(final String fieldName, final Object value) {
        return (FeatureRename)super.set(fieldName, value);
    }
    
    public FeatureRename clone() {
        return (FeatureRename)super.clone();
    }
}
