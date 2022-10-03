package com.adventnet.sym.server.mdm.ios.payload;

public class ADCertPayload extends IOSPayload
{
    public ADCertPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.ADCertificate.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setCertServer(final String value) {
        this.getPayloadDict().put("CertServer", (Object)value);
    }
    
    public void setCertificateAuthority(final String value) {
        this.getPayloadDict().put("CertificateAuthority", (Object)value);
    }
    
    public void setCertTemplate(final String value) {
        this.getPayloadDict().put("CertTemplate", (Object)value);
    }
    
    public void setCertificateRenewalTimeInterval(final int value) {
        this.getPayloadDict().put("CertificateRenewalTimeInterval", (Object)value);
    }
    
    public void setKeysize(final int value) {
        this.getPayloadDict().put("Keysize", (Object)value);
    }
    
    public void setDescription(final String value) {
        this.getPayloadDict().put("Description", (Object)value);
    }
    
    public void setEnableAutoRenewal(final boolean value) {
        this.getPayloadDict().put("EnableAutoRenewal", (Object)value);
    }
    
    public void setAllowAllAppsAccess(final boolean value) {
        this.getPayloadDict().put("AllowAllAppsAccess", (Object)value);
    }
    
    public void setKeyIsExtractable(final boolean value) {
        this.getPayloadDict().put("KeyIsExtractable", (Object)value);
    }
}
