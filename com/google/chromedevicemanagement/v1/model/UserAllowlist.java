package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class UserAllowlist extends GenericJson
{
    @Key
    private List<String> userAllowlist;
    
    public List<String> getUserAllowlist() {
        return this.userAllowlist;
    }
    
    public UserAllowlist setUserAllowlist(final List<String> userAllowlist) {
        this.userAllowlist = userAllowlist;
        return this;
    }
    
    public UserAllowlist set(final String s, final Object o) {
        return (UserAllowlist)super.set(s, o);
    }
    
    public UserAllowlist clone() {
        return (UserAllowlist)super.clone();
    }
}
