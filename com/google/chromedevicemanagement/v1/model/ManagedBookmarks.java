package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ManagedBookmarks extends GenericJson
{
    @Key
    private String managedBookmarks;
    @Key
    private PolicyOptions policyOptions;
    
    public String getManagedBookmarks() {
        return this.managedBookmarks;
    }
    
    public ManagedBookmarks setManagedBookmarks(final String managedBookmarks) {
        this.managedBookmarks = managedBookmarks;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ManagedBookmarks setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public ManagedBookmarks set(final String s, final Object o) {
        return (ManagedBookmarks)super.set(s, o);
    }
    
    public ManagedBookmarks clone() {
        return (ManagedBookmarks)super.clone();
    }
}
