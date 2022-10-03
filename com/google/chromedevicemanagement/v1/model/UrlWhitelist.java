package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UrlWhitelist extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private List<String> urls;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public UrlWhitelist setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public List<String> getUrls() {
        return this.urls;
    }
    
    public UrlWhitelist setUrls(final List<String> urls) {
        this.urls = urls;
        return this;
    }
    
    public UrlWhitelist set(final String s, final Object o) {
        return (UrlWhitelist)super.set(s, o);
    }
    
    public UrlWhitelist clone() {
        return (UrlWhitelist)super.clone();
    }
}
