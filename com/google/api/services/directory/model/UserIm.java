package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserIm extends GenericJson
{
    @Key
    private String customProtocol;
    @Key
    private String customType;
    @Key
    private String im;
    @Key
    private Boolean primary;
    @Key
    private String protocol;
    @Key
    private String type;
    
    public String getCustomProtocol() {
        return this.customProtocol;
    }
    
    public UserIm setCustomProtocol(final String customProtocol) {
        this.customProtocol = customProtocol;
        return this;
    }
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserIm setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public String getIm() {
        return this.im;
    }
    
    public UserIm setIm(final String im) {
        this.im = im;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserIm setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public UserIm setProtocol(final String protocol) {
        this.protocol = protocol;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserIm setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserIm set(final String fieldName, final Object value) {
        return (UserIm)super.set(fieldName, value);
    }
    
    public UserIm clone() {
        return (UserIm)super.clone();
    }
}
