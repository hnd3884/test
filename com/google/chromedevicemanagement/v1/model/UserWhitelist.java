package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class UserWhitelist extends GenericJson
{
    @Key
    private List<String> userWhitelist;
    
    public List<String> getUserWhitelist() {
        return this.userWhitelist;
    }
    
    public UserWhitelist setUserWhitelist(final List<String> userWhitelist) {
        this.userWhitelist = userWhitelist;
        return this;
    }
    
    public UserWhitelist set(final String s, final Object o) {
        return (UserWhitelist)super.set(s, o);
    }
    
    public UserWhitelist clone() {
        return (UserWhitelist)super.clone();
    }
}
