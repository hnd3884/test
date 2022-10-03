package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class CookiesBlockedForUrls extends GenericJson
{
    @Key
    private List<String> cookiesBlockedForUrls;
    @Key
    private PolicyOptions policyOptions;
    
    public List<String> getCookiesBlockedForUrls() {
        return this.cookiesBlockedForUrls;
    }
    
    public CookiesBlockedForUrls setCookiesBlockedForUrls(final List<String> cookiesBlockedForUrls) {
        this.cookiesBlockedForUrls = cookiesBlockedForUrls;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public CookiesBlockedForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public CookiesBlockedForUrls set(final String s, final Object o) {
        return (CookiesBlockedForUrls)super.set(s, o);
    }
    
    public CookiesBlockedForUrls clone() {
        return (CookiesBlockedForUrls)super.clone();
    }
}
