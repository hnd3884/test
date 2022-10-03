package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeletingBrowserHistoryDisabled extends GenericJson
{
    @Key
    private Boolean deletingBrowserHistoryDisabled;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getDeletingBrowserHistoryDisabled() {
        return this.deletingBrowserHistoryDisabled;
    }
    
    public DeletingBrowserHistoryDisabled setDeletingBrowserHistoryDisabled(final Boolean deletingBrowserHistoryDisabled) {
        this.deletingBrowserHistoryDisabled = deletingBrowserHistoryDisabled;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public DeletingBrowserHistoryDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public DeletingBrowserHistoryDisabled set(final String s, final Object o) {
        return (DeletingBrowserHistoryDisabled)super.set(s, o);
    }
    
    public DeletingBrowserHistoryDisabled clone() {
        return (DeletingBrowserHistoryDisabled)super.clone();
    }
}
