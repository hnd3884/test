package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserGender extends GenericJson
{
    @Key
    private String addressMeAs;
    @Key
    private String customGender;
    @Key
    private String type;
    
    public String getAddressMeAs() {
        return this.addressMeAs;
    }
    
    public UserGender setAddressMeAs(final String addressMeAs) {
        this.addressMeAs = addressMeAs;
        return this;
    }
    
    public String getCustomGender() {
        return this.customGender;
    }
    
    public UserGender setCustomGender(final String customGender) {
        this.customGender = customGender;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserGender setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserGender set(final String fieldName, final Object value) {
        return (UserGender)super.set(fieldName, value);
    }
    
    public UserGender clone() {
        return (UserGender)super.clone();
    }
}
