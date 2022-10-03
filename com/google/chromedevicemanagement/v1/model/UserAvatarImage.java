package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserAvatarImage extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String url;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public UserAvatarImage setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public UserAvatarImage setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public UserAvatarImage set(final String s, final Object o) {
        return (UserAvatarImage)super.set(s, o);
    }
    
    public UserAvatarImage clone() {
        return (UserAvatarImage)super.clone();
    }
}
