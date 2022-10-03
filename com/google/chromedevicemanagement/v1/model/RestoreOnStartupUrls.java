package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class RestoreOnStartupUrls extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private List<String> urls;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public RestoreOnStartupUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public List<String> getUrls() {
        return this.urls;
    }
    
    public RestoreOnStartupUrls setUrls(final List<String> urls) {
        this.urls = urls;
        return this;
    }
    
    public RestoreOnStartupUrls set(final String s, final Object o) {
        return (RestoreOnStartupUrls)super.set(s, o);
    }
    
    public RestoreOnStartupUrls clone() {
        return (RestoreOnStartupUrls)super.clone();
    }
}
