package com.adventnet.iam.security;

public class VcardProperty
{
    private String key;
    private String value;
    
    public VcardProperty() {
        this.key = null;
        this.value = null;
    }
    
    public VcardProperty(final String key, final String value) {
        this.key = null;
        this.value = null;
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
