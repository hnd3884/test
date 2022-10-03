package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class WebRtcUdpPortRange extends GenericJson
{
    @Key
    @JsonString
    private Long maximumPort;
    @Key
    @JsonString
    private Long minimumPort;
    @Key
    private PolicyOptions policyOptions;
    
    public Long getMaximumPort() {
        return this.maximumPort;
    }
    
    public WebRtcUdpPortRange setMaximumPort(final Long maximumPort) {
        this.maximumPort = maximumPort;
        return this;
    }
    
    public Long getMinimumPort() {
        return this.minimumPort;
    }
    
    public WebRtcUdpPortRange setMinimumPort(final Long minimumPort) {
        this.minimumPort = minimumPort;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public WebRtcUdpPortRange setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public WebRtcUdpPortRange set(final String s, final Object o) {
        return (WebRtcUdpPortRange)super.set(s, o);
    }
    
    public WebRtcUdpPortRange clone() {
        return (WebRtcUdpPortRange)super.clone();
    }
}
