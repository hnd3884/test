package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class LocalizedText extends GenericJson
{
    @Key
    private String locale;
    @Key
    private String text;
    
    public String getLocale() {
        return this.locale;
    }
    
    public LocalizedText setLocale(final String locale) {
        this.locale = locale;
        return this;
    }
    
    public String getText() {
        return this.text;
    }
    
    public LocalizedText setText(final String text) {
        this.text = text;
        return this;
    }
    
    public LocalizedText set(final String fieldName, final Object value) {
        return (LocalizedText)super.set(fieldName, value);
    }
    
    public LocalizedText clone() {
        return (LocalizedText)super.clone();
    }
}
