package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceWallpaperImage extends GenericJson
{
    @Key
    private String url;
    
    public String getUrl() {
        return this.url;
    }
    
    public DeviceWallpaperImage setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public DeviceWallpaperImage set(final String s, final Object o) {
        return (DeviceWallpaperImage)super.set(s, o);
    }
    
    public DeviceWallpaperImage clone() {
        return (DeviceWallpaperImage)super.clone();
    }
}
