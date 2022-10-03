package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class FeatureInstance extends GenericJson
{
    @Key
    private Feature feature;
    
    public Feature getFeature() {
        return this.feature;
    }
    
    public FeatureInstance setFeature(final Feature feature) {
        this.feature = feature;
        return this;
    }
    
    public FeatureInstance set(final String fieldName, final Object value) {
        return (FeatureInstance)super.set(fieldName, value);
    }
    
    public FeatureInstance clone() {
        return (FeatureInstance)super.clone();
    }
}
