package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class BlockThirdPartyCookies extends GenericJson
{
    @Key
    private Boolean blockThirdPartyCookies;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getBlockThirdPartyCookies() {
        return this.blockThirdPartyCookies;
    }
    
    public BlockThirdPartyCookies setBlockThirdPartyCookies(final Boolean blockThirdPartyCookies) {
        this.blockThirdPartyCookies = blockThirdPartyCookies;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public BlockThirdPartyCookies setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public BlockThirdPartyCookies set(final String s, final Object o) {
        return (BlockThirdPartyCookies)super.set(s, o);
    }
    
    public BlockThirdPartyCookies clone() {
        return (BlockThirdPartyCookies)super.clone();
    }
}
