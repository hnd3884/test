package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DisplayInfo extends GenericJson
{
    @Key
    private Boolean isInternal;
    @Key
    private Integer refreshRate;
    @Key
    private Integer resolutionHeight;
    @Key
    private Integer resolutionWidth;
    
    public Boolean getIsInternal() {
        return this.isInternal;
    }
    
    public DisplayInfo setIsInternal(final Boolean isInternal) {
        this.isInternal = isInternal;
        return this;
    }
    
    public Integer getRefreshRate() {
        return this.refreshRate;
    }
    
    public DisplayInfo setRefreshRate(final Integer refreshRate) {
        this.refreshRate = refreshRate;
        return this;
    }
    
    public Integer getResolutionHeight() {
        return this.resolutionHeight;
    }
    
    public DisplayInfo setResolutionHeight(final Integer resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
        return this;
    }
    
    public Integer getResolutionWidth() {
        return this.resolutionWidth;
    }
    
    public DisplayInfo setResolutionWidth(final Integer resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
        return this;
    }
    
    public DisplayInfo set(final String s, final Object o) {
        return (DisplayInfo)super.set(s, o);
    }
    
    public DisplayInfo clone() {
        return (DisplayInfo)super.clone();
    }
}
