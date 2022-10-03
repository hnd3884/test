package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ArcCertificatesSync extends GenericJson
{
    @Key
    private String arcCertificatesSyncMode;
    @Key
    private PolicyOptions policyOptions;
    
    public String getArcCertificatesSyncMode() {
        return this.arcCertificatesSyncMode;
    }
    
    public ArcCertificatesSync setArcCertificatesSyncMode(final String arcCertificatesSyncMode) {
        this.arcCertificatesSyncMode = arcCertificatesSyncMode;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ArcCertificatesSync setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public ArcCertificatesSync set(final String s, final Object o) {
        return (ArcCertificatesSync)super.set(s, o);
    }
    
    public ArcCertificatesSync clone() {
        return (ArcCertificatesSync)super.clone();
    }
}
