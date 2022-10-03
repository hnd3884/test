package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ManagedProperty extends GenericJson
{
    @Key
    private String key;
    @Key
    private Boolean valueBool;
    @Key
    private ManagedPropertyBundle valueBundle;
    @Key
    private List<ManagedPropertyBundle> valueBundleArray;
    @Key
    private Integer valueInteger;
    @Key
    private String valueString;
    @Key
    private List<String> valueStringArray;
    
    public String getKey() {
        return this.key;
    }
    
    public ManagedProperty setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public Boolean getValueBool() {
        return this.valueBool;
    }
    
    public ManagedProperty setValueBool(final Boolean valueBool) {
        this.valueBool = valueBool;
        return this;
    }
    
    public ManagedPropertyBundle getValueBundle() {
        return this.valueBundle;
    }
    
    public ManagedProperty setValueBundle(final ManagedPropertyBundle valueBundle) {
        this.valueBundle = valueBundle;
        return this;
    }
    
    public List<ManagedPropertyBundle> getValueBundleArray() {
        return this.valueBundleArray;
    }
    
    public ManagedProperty setValueBundleArray(final List<ManagedPropertyBundle> valueBundleArray) {
        this.valueBundleArray = valueBundleArray;
        return this;
    }
    
    public Integer getValueInteger() {
        return this.valueInteger;
    }
    
    public ManagedProperty setValueInteger(final Integer valueInteger) {
        this.valueInteger = valueInteger;
        return this;
    }
    
    public String getValueString() {
        return this.valueString;
    }
    
    public ManagedProperty setValueString(final String valueString) {
        this.valueString = valueString;
        return this;
    }
    
    public List<String> getValueStringArray() {
        return this.valueStringArray;
    }
    
    public ManagedProperty setValueStringArray(final List<String> valueStringArray) {
        this.valueStringArray = valueStringArray;
        return this;
    }
    
    public ManagedProperty set(final String fieldName, final Object value) {
        return (ManagedProperty)super.set(fieldName, value);
    }
    
    public ManagedProperty clone() {
        return (ManagedProperty)super.clone();
    }
}
