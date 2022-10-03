package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PopupsAllowedForUrls extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private List<String> urls;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PopupsAllowedForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public List<String> getUrls() {
        return this.urls;
    }
    
    public PopupsAllowedForUrls setUrls(final List<String> urls) {
        this.urls = urls;
        return this;
    }
    
    public PopupsAllowedForUrls set(final String s, final Object o) {
        return (PopupsAllowedForUrls)super.set(s, o);
    }
    
    public PopupsAllowedForUrls clone() {
        return (PopupsAllowedForUrls)super.clone();
    }
}
