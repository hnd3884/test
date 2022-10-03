package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserVerifiedModeRequired extends GenericJson
{
    @Key
    private Boolean userVerifiedModeRequired;
    
    public Boolean getUserVerifiedModeRequired() {
        return this.userVerifiedModeRequired;
    }
    
    public UserVerifiedModeRequired setUserVerifiedModeRequired(final Boolean userVerifiedModeRequired) {
        this.userVerifiedModeRequired = userVerifiedModeRequired;
        return this;
    }
    
    public UserVerifiedModeRequired set(final String s, final Object o) {
        return (UserVerifiedModeRequired)super.set(s, o);
    }
    
    public UserVerifiedModeRequired clone() {
        return (UserVerifiedModeRequired)super.clone();
    }
}
