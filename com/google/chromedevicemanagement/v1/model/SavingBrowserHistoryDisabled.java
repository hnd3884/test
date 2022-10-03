package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SavingBrowserHistoryDisabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean savingBrowserHistoryDisabled;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public SavingBrowserHistoryDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getSavingBrowserHistoryDisabled() {
        return this.savingBrowserHistoryDisabled;
    }
    
    public SavingBrowserHistoryDisabled setSavingBrowserHistoryDisabled(final Boolean savingBrowserHistoryDisabled) {
        this.savingBrowserHistoryDisabled = savingBrowserHistoryDisabled;
        return this;
    }
    
    public SavingBrowserHistoryDisabled set(final String s, final Object o) {
        return (SavingBrowserHistoryDisabled)super.set(s, o);
    }
    
    public SavingBrowserHistoryDisabled clone() {
        return (SavingBrowserHistoryDisabled)super.clone();
    }
}
