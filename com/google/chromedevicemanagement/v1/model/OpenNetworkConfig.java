package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class OpenNetworkConfig extends GenericJson
{
    @Key
    private String openNetworkConfig;
    
    public String getOpenNetworkConfig() {
        return this.openNetworkConfig;
    }
    
    public OpenNetworkConfig setOpenNetworkConfig(final String openNetworkConfig) {
        this.openNetworkConfig = openNetworkConfig;
        return this;
    }
    
    public OpenNetworkConfig set(final String s, final Object o) {
        return (OpenNetworkConfig)super.set(s, o);
    }
    
    public OpenNetworkConfig clone() {
        return (OpenNetworkConfig)super.clone();
    }
}
