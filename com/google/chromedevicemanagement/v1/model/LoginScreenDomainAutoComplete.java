package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class LoginScreenDomainAutoComplete extends GenericJson
{
    @Key
    private String loginScreenDomainAutoComplete;
    
    public String getLoginScreenDomainAutoComplete() {
        return this.loginScreenDomainAutoComplete;
    }
    
    public LoginScreenDomainAutoComplete setLoginScreenDomainAutoComplete(final String loginScreenDomainAutoComplete) {
        this.loginScreenDomainAutoComplete = loginScreenDomainAutoComplete;
        return this;
    }
    
    public LoginScreenDomainAutoComplete set(final String s, final Object o) {
        return (LoginScreenDomainAutoComplete)super.set(s, o);
    }
    
    public LoginScreenDomainAutoComplete clone() {
        return (LoginScreenDomainAutoComplete)super.clone();
    }
}
