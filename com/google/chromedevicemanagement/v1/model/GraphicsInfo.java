package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class GraphicsInfo extends GenericJson
{
    @Key
    private GraphicsAdapterInfo adapterInfo;
    @Key
    private List<DisplayInfo> displays;
    
    public GraphicsAdapterInfo getAdapterInfo() {
        return this.adapterInfo;
    }
    
    public GraphicsInfo setAdapterInfo(final GraphicsAdapterInfo adapterInfo) {
        this.adapterInfo = adapterInfo;
        return this;
    }
    
    public List<DisplayInfo> getDisplays() {
        return this.displays;
    }
    
    public GraphicsInfo setDisplays(final List<DisplayInfo> displays) {
        this.displays = displays;
        return this;
    }
    
    public GraphicsInfo set(final String s, final Object o) {
        return (GraphicsInfo)super.set(s, o);
    }
    
    public GraphicsInfo clone() {
        return (GraphicsInfo)super.clone();
    }
}
