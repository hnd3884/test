package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class AutoSelectCertificateForUrls extends GenericJson
{
    @Key
    private List<String> autoSelectCertificateForUrls;
    @Key
    private PolicyOptions policyOptions;
    
    public List<String> getAutoSelectCertificateForUrls() {
        return this.autoSelectCertificateForUrls;
    }
    
    public AutoSelectCertificateForUrls setAutoSelectCertificateForUrls(final List<String> autoSelectCertificateForUrls) {
        this.autoSelectCertificateForUrls = autoSelectCertificateForUrls;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public AutoSelectCertificateForUrls setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public AutoSelectCertificateForUrls set(final String s, final Object o) {
        return (AutoSelectCertificateForUrls)super.set(s, o);
    }
    
    public AutoSelectCertificateForUrls clone() {
        return (AutoSelectCertificateForUrls)super.clone();
    }
}
