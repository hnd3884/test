package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DefaultCookiesSetting extends GenericJson
{
    @Key
    private String defaultCookiesSetting;
    @Key
    private PolicyOptions policyOptions;
    
    public String getDefaultCookiesSetting() {
        return this.defaultCookiesSetting;
    }
    
    public DefaultCookiesSetting setDefaultCookiesSetting(final String defaultCookiesSetting) {
        this.defaultCookiesSetting = defaultCookiesSetting;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public DefaultCookiesSetting setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public DefaultCookiesSetting set(final String s, final Object o) {
        return (DefaultCookiesSetting)super.set(s, o);
    }
    
    public DefaultCookiesSetting clone() {
        return (DefaultCookiesSetting)super.clone();
    }
}
