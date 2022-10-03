package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductSigningCertificate extends GenericJson
{
    @Key
    private String certificateHashSha1;
    @Key
    private String certificateHashSha256;
    
    public String getCertificateHashSha1() {
        return this.certificateHashSha1;
    }
    
    public ProductSigningCertificate setCertificateHashSha1(final String certificateHashSha1) {
        this.certificateHashSha1 = certificateHashSha1;
        return this;
    }
    
    public String getCertificateHashSha256() {
        return this.certificateHashSha256;
    }
    
    public ProductSigningCertificate setCertificateHashSha256(final String certificateHashSha256) {
        this.certificateHashSha256 = certificateHashSha256;
        return this;
    }
    
    public ProductSigningCertificate set(final String fieldName, final Object value) {
        return (ProductSigningCertificate)super.set(fieldName, value);
    }
    
    public ProductSigningCertificate clone() {
        return (ProductSigningCertificate)super.clone();
    }
}
