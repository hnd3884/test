package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class VariableSet extends GenericJson
{
    @Key
    private String placeholder;
    @Key
    private String userValue;
    
    public String getPlaceholder() {
        return this.placeholder;
    }
    
    public VariableSet setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
        return this;
    }
    
    public String getUserValue() {
        return this.userValue;
    }
    
    public VariableSet setUserValue(final String userValue) {
        this.userValue = userValue;
        return this;
    }
    
    public VariableSet set(final String fieldName, final Object value) {
        return (VariableSet)super.set(fieldName, value);
    }
    
    public VariableSet clone() {
        return (VariableSet)super.clone();
    }
}
