package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ExternalStorageAccessibility extends GenericJson
{
    @Key
    private String accessMode;
    @Key
    private PolicyOptions policyOptions;
    
    public String getAccessMode() {
        return this.accessMode;
    }
    
    public ExternalStorageAccessibility setAccessMode(final String accessMode) {
        this.accessMode = accessMode;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ExternalStorageAccessibility setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public ExternalStorageAccessibility set(final String s, final Object o) {
        return (ExternalStorageAccessibility)super.set(s, o);
    }
    
    public ExternalStorageAccessibility clone() {
        return (ExternalStorageAccessibility)super.clone();
    }
}
