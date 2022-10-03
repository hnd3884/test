package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class RedirectToSamlIdpAllowed extends GenericJson
{
    @Key
    private Boolean redirectToSamlIdpAllowed;
    
    public Boolean getRedirectToSamlIdpAllowed() {
        return this.redirectToSamlIdpAllowed;
    }
    
    public RedirectToSamlIdpAllowed setRedirectToSamlIdpAllowed(final Boolean redirectToSamlIdpAllowed) {
        this.redirectToSamlIdpAllowed = redirectToSamlIdpAllowed;
        return this;
    }
    
    public RedirectToSamlIdpAllowed set(final String s, final Object o) {
        return (RedirectToSamlIdpAllowed)super.set(s, o);
    }
    
    public RedirectToSamlIdpAllowed clone() {
        return (RedirectToSamlIdpAllowed)super.clone();
    }
}
