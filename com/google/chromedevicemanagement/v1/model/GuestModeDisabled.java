package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class GuestModeDisabled extends GenericJson
{
    @Key
    private Boolean guestModeDisabled;
    
    public Boolean getGuestModeDisabled() {
        return this.guestModeDisabled;
    }
    
    public GuestModeDisabled setGuestModeDisabled(final Boolean guestModeDisabled) {
        this.guestModeDisabled = guestModeDisabled;
        return this;
    }
    
    public GuestModeDisabled set(final String s, final Object o) {
        return (GuestModeDisabled)super.set(s, o);
    }
    
    public GuestModeDisabled clone() {
        return (GuestModeDisabled)super.clone();
    }
}
