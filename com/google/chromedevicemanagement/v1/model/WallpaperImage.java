package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class WallpaperImage extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String url;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public WallpaperImage setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public WallpaperImage setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public WallpaperImage set(final String s, final Object o) {
        return (WallpaperImage)super.set(s, o);
    }
    
    public WallpaperImage clone() {
        return (WallpaperImage)super.clone();
    }
}
