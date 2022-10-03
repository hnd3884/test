package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class EphemeralUsersEnabled extends GenericJson
{
    @Key
    private Boolean ephemeralUsersEnabled;
    
    public Boolean getEphemeralUsersEnabled() {
        return this.ephemeralUsersEnabled;
    }
    
    public EphemeralUsersEnabled setEphemeralUsersEnabled(final Boolean ephemeralUsersEnabled) {
        this.ephemeralUsersEnabled = ephemeralUsersEnabled;
        return this;
    }
    
    public EphemeralUsersEnabled set(final String s, final Object o) {
        return (EphemeralUsersEnabled)super.set(s, o);
    }
    
    public EphemeralUsersEnabled clone() {
        return (EphemeralUsersEnabled)super.clone();
    }
}
