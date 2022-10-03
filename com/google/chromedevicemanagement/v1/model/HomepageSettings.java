package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class HomepageSettings extends GenericJson
{
    @Key
    private String homepageMode;
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String url;
    
    public String getHomepageMode() {
        return this.homepageMode;
    }
    
    public HomepageSettings setHomepageMode(final String homepageMode) {
        this.homepageMode = homepageMode;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public HomepageSettings setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public HomepageSettings setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public HomepageSettings set(final String s, final Object o) {
        return (HomepageSettings)super.set(s, o);
    }
    
    public HomepageSettings clone() {
        return (HomepageSettings)super.clone();
    }
}
