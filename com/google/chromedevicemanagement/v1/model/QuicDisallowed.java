package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class QuicDisallowed extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean quicDisallowed;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public QuicDisallowed setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getQuicDisallowed() {
        return this.quicDisallowed;
    }
    
    public QuicDisallowed setQuicDisallowed(final Boolean quicDisallowed) {
        this.quicDisallowed = quicDisallowed;
        return this;
    }
    
    public QuicDisallowed set(final String s, final Object o) {
        return (QuicDisallowed)super.set(s, o);
    }
    
    public QuicDisallowed clone() {
        return (QuicDisallowed)super.clone();
    }
}
