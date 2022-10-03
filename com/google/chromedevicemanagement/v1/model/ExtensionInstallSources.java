package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ExtensionInstallSources extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private List<String> urls;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ExtensionInstallSources setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public List<String> getUrls() {
        return this.urls;
    }
    
    public ExtensionInstallSources setUrls(final List<String> urls) {
        this.urls = urls;
        return this;
    }
    
    public ExtensionInstallSources set(final String s, final Object o) {
        return (ExtensionInstallSources)super.set(s, o);
    }
    
    public ExtensionInstallSources clone() {
        return (ExtensionInstallSources)super.clone();
    }
}
