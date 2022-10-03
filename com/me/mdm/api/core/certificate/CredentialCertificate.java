package com.me.mdm.api.core.certificate;

public class CredentialCertificate
{
    private Long certificateId;
    private long customerId;
    private String certificateDisplayName;
    private String certificateFileName;
    private String certificatePassword;
    private String certificateSerialNumber;
    private String certificateThumbprint;
    private String certificateIssuerDn;
    private String certificateSubjectDn;
    private long certificateNotBefore;
    private long certificateNotAfter;
    
    public Long getCertificateId() {
        return this.certificateId;
    }
    
    public void setCertificateId(final Long certificateId) {
        this.certificateId = certificateId;
    }
    
    public long getCustomerId() {
        return this.customerId;
    }
    
    public String getCertificateDisplayName() {
        return this.certificateDisplayName;
    }
    
    public String getCertificateFileName() {
        return this.certificateFileName;
    }
    
    public String getCertificatePassword() {
        return this.certificatePassword;
    }
    
    public String getCertificateSerialNumber() {
        return this.certificateSerialNumber;
    }
    
    public String getCertificateThumbprint() {
        return this.certificateThumbprint;
    }
    
    public String getCertificateIssuerDn() {
        return this.certificateIssuerDn;
    }
    
    public String getCertificateSubjectDn() {
        return this.certificateSubjectDn;
    }
    
    public long getCertificateNotBefore() {
        return this.certificateNotBefore;
    }
    
    public long getCertificateNotAfter() {
        return this.certificateNotAfter;
    }
    
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }
    
    public void setCertificateDisplayName(final String certificateDisplayName) {
        this.certificateDisplayName = certificateDisplayName;
    }
    
    public void setCertificateFileName(final String certificateFileName) {
        this.certificateFileName = certificateFileName;
    }
    
    public void setCertificatePassword(final String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
    
    public void setCertificateSerialNumber(final String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }
    
    public void setCertificateThumbprint(final String certificateThumbprint) {
        this.certificateThumbprint = certificateThumbprint;
    }
    
    public void setCertificateIssuerDn(final String certificateIssuerDn) {
        this.certificateIssuerDn = certificateIssuerDn;
    }
    
    public void setCertificateSubjectDn(final String certificateSubjectDn) {
        this.certificateSubjectDn = certificateSubjectDn;
    }
    
    public void setCertificateNotBefore(final long certificateNotBefore) {
        this.certificateNotBefore = certificateNotBefore;
    }
    
    public void setCertificateNotAfter(final long certificateNotAfter) {
        this.certificateNotAfter = certificateNotAfter;
    }
}
