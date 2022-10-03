package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class TermsOfServiceURL extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String url;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public TermsOfServiceURL setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public TermsOfServiceURL setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public TermsOfServiceURL set(final String s, final Object o) {
        return (TermsOfServiceURL)super.set(s, o);
    }
    
    public TermsOfServiceURL clone() {
        return (TermsOfServiceURL)super.clone();
    }
}
