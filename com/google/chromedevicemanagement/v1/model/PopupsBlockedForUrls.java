package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PopupsBlockedForUrls extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private List<String> urls;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PopupsBlockedForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public List<String> getUrls() {
        return this.urls;
    }
    
    public PopupsBlockedForUrls setUrls(final List<String> urls) {
        this.urls = urls;
        return this;
    }
    
    public PopupsBlockedForUrls set(final String s, final Object o) {
        return (PopupsBlockedForUrls)super.set(s, o);
    }
    
    public PopupsBlockedForUrls clone() {
        return (PopupsBlockedForUrls)super.clone();
    }
}
