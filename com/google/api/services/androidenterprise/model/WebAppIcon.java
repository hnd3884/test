package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class WebAppIcon extends GenericJson
{
    @Key
    private String imageData;
    
    public String getImageData() {
        return this.imageData;
    }
    
    public WebAppIcon setImageData(final String imageData) {
        this.imageData = imageData;
        return this;
    }
    
    public WebAppIcon set(final String fieldName, final Object value) {
        return (WebAppIcon)super.set(fieldName, value);
    }
    
    public WebAppIcon clone() {
        return (WebAppIcon)super.clone();
    }
}
