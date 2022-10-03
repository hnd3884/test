package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SamlSettings extends GenericJson
{
    @Key
    private Boolean transferSamlCookies;
    
    public Boolean getTransferSamlCookies() {
        return this.transferSamlCookies;
    }
    
    public SamlSettings setTransferSamlCookies(final Boolean transferSamlCookies) {
        this.transferSamlCookies = transferSamlCookies;
        return this;
    }
    
    public SamlSettings set(final String s, final Object o) {
        return (SamlSettings)super.set(s, o);
    }
    
    public SamlSettings clone() {
        return (SamlSettings)super.clone();
    }
}
