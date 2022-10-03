package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class CookiesSessionOnlyForUrls extends GenericJson
{
    @Key
    private List<String> cookiesSessionOnlyForUrls;
    @Key
    private PolicyOptions policyOptions;
    
    public List<String> getCookiesSessionOnlyForUrls() {
        return this.cookiesSessionOnlyForUrls;
    }
    
    public CookiesSessionOnlyForUrls setCookiesSessionOnlyForUrls(final List<String> cookiesSessionOnlyForUrls) {
        this.cookiesSessionOnlyForUrls = cookiesSessionOnlyForUrls;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public CookiesSessionOnlyForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public CookiesSessionOnlyForUrls set(final String s, final Object o) {
        return (CookiesSessionOnlyForUrls)super.set(s, o);
    }
    
    public CookiesSessionOnlyForUrls clone() {
        return (CookiesSessionOnlyForUrls)super.clone();
    }
}
