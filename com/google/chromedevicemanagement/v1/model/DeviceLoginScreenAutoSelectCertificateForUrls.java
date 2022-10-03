package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class DeviceLoginScreenAutoSelectCertificateForUrls extends GenericJson
{
    @Key
    private List<String> loginScreenAutoSelectCertificateRules;
    
    public List<String> getLoginScreenAutoSelectCertificateRules() {
        return this.loginScreenAutoSelectCertificateRules;
    }
    
    public DeviceLoginScreenAutoSelectCertificateForUrls setLoginScreenAutoSelectCertificateRules(final List<String> loginScreenAutoSelectCertificateRules) {
        this.loginScreenAutoSelectCertificateRules = loginScreenAutoSelectCertificateRules;
        return this;
    }
    
    public DeviceLoginScreenAutoSelectCertificateForUrls set(final String s, final Object o) {
        return (DeviceLoginScreenAutoSelectCertificateForUrls)super.set(s, o);
    }
    
    public DeviceLoginScreenAutoSelectCertificateForUrls clone() {
        return (DeviceLoginScreenAutoSelectCertificateForUrls)super.clone();
    }
}
