package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ShowUserNamesOnSignin extends GenericJson
{
    @Key
    private Boolean showUserNames;
    
    public Boolean getShowUserNames() {
        return this.showUserNames;
    }
    
    public ShowUserNamesOnSignin setShowUserNames(final Boolean showUserNames) {
        this.showUserNames = showUserNames;
        return this;
    }
    
    public ShowUserNamesOnSignin set(final String s, final Object o) {
        return (ShowUserNamesOnSignin)super.set(s, o);
    }
    
    public ShowUserNamesOnSignin clone() {
        return (ShowUserNamesOnSignin)super.clone();
    }
}
