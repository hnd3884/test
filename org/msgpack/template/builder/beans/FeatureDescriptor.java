package org.msgpack.template.builder.beans;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class FeatureDescriptor
{
    private Map<String, Object> values;
    boolean preferred;
    boolean hidden;
    boolean expert;
    String shortDescription;
    String name;
    String displayName;
    
    public FeatureDescriptor() {
        this.values = new HashMap<String, Object>();
    }
    
    public void setValue(final String attributeName, final Object value) {
        if (attributeName == null || value == null) {
            throw new NullPointerException();
        }
        this.values.put(attributeName, value);
    }
    
    public Object getValue(final String attributeName) {
        if (attributeName != null) {
            return this.values.get(attributeName);
        }
        return null;
    }
    
    public Enumeration<String> attributeNames() {
        return Collections.enumeration(new LinkedList<String>(this.values.keySet()));
    }
    
    public void setShortDescription(final String text) {
        this.shortDescription = text;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getShortDescription() {
        return (this.shortDescription == null) ? this.getDisplayName() : this.shortDescription;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return (this.displayName == null) ? this.getName() : this.displayName;
    }
    
    public void setPreferred(final boolean preferred) {
        this.preferred = preferred;
    }
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
    
    public void setExpert(final boolean expert) {
        this.expert = expert;
    }
    
    public boolean isPreferred() {
        return this.preferred;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public boolean isExpert() {
        return this.expert;
    }
    
    void merge(final FeatureDescriptor feature) {
        assert this.name.equals(feature.name);
        this.expert |= feature.expert;
        this.hidden |= feature.hidden;
        this.preferred |= feature.preferred;
        if (this.shortDescription == null) {
            this.shortDescription = feature.shortDescription;
        }
        if (this.name == null) {
            this.name = feature.name;
        }
        if (this.displayName == null) {
            this.displayName = feature.displayName;
        }
    }
}
