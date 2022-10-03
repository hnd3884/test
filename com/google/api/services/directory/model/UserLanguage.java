package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserLanguage extends GenericJson
{
    @Key
    private String customLanguage;
    @Key
    private String languageCode;
    
    public String getCustomLanguage() {
        return this.customLanguage;
    }
    
    public UserLanguage setCustomLanguage(final String customLanguage) {
        this.customLanguage = customLanguage;
        return this;
    }
    
    public String getLanguageCode() {
        return this.languageCode;
    }
    
    public UserLanguage setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
        return this;
    }
    
    public UserLanguage set(final String fieldName, final Object value) {
        return (UserLanguage)super.set(fieldName, value);
    }
    
    public UserLanguage clone() {
        return (UserLanguage)super.clone();
    }
}
