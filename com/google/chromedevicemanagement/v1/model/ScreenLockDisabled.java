package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ScreenLockDisabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean screenLockDisabled;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ScreenLockDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getScreenLockDisabled() {
        return this.screenLockDisabled;
    }
    
    public ScreenLockDisabled setScreenLockDisabled(final Boolean screenLockDisabled) {
        this.screenLockDisabled = screenLockDisabled;
        return this;
    }
    
    public ScreenLockDisabled set(final String s, final Object o) {
        return (ScreenLockDisabled)super.set(s, o);
    }
    
    public ScreenLockDisabled clone() {
        return (ScreenLockDisabled)super.clone();
    }
}
