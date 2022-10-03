package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class CookiesAllowedForUrls extends GenericJson
{
    @Key
    private List<String> cookiesAllowedForUrls;
    @Key
    private PolicyOptions policyOptions;
    
    public List<String> getCookiesAllowedForUrls() {
        return this.cookiesAllowedForUrls;
    }
    
    public CookiesAllowedForUrls setCookiesAllowedForUrls(final List<String> cookiesAllowedForUrls) {
        this.cookiesAllowedForUrls = cookiesAllowedForUrls;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public CookiesAllowedForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public CookiesAllowedForUrls set(final String s, final Object o) {
        return (CookiesAllowedForUrls)super.set(s, o);
    }
    
    public CookiesAllowedForUrls clone() {
        return (CookiesAllowedForUrls)super.clone();
    }
}
