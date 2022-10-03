package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ReleaseChannel extends GenericJson
{
    @Key
    private String releaseChannelType;
    
    public String getReleaseChannelType() {
        return this.releaseChannelType;
    }
    
    public ReleaseChannel setReleaseChannelType(final String releaseChannelType) {
        this.releaseChannelType = releaseChannelType;
        return this;
    }
    
    public ReleaseChannel set(final String s, final Object o) {
        return (ReleaseChannel)super.set(s, o);
    }
    
    public ReleaseChannel clone() {
        return (ReleaseChannel)super.clone();
    }
}
