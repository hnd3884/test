package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertifcateDetailsModel
{
    @JsonAlias({ "CertificateIssuerName" })
    @JsonProperty("certificateissuername")
    private String certificateIssuerName;
    @JsonAlias({ "SerialNumber" })
    @JsonProperty("serialnumber")
    private String serialNumber;
    @JsonAlias({ "CertificateName" })
    @JsonProperty("certificatename")
    private String certificateName;
    @JsonAlias({ "SignatureAlgorithmName" })
    @JsonProperty("signaturealgorithmname")
    private String signatureAlgorithmName;
    @JsonAlias({ "CertificateExpiry" })
    @JsonProperty("certificateexpiry")
    private String certificateExpiry;
    @JsonAlias({ "IsIdentity" })
    @JsonProperty("isidentity")
    private boolean identity;
    @JsonAlias({ "CertificateSubjectName" })
    @JsonProperty("certificatesubjectname")
    private String certificateSubjectName;
    @JsonAlias({ "SignatureAlgorithmOID" })
    @JsonProperty("signaturealgorithmoid")
    private String signatureAlgorithmOID;
    
    public String getCertificateIssuerName() {
        return this.certificateIssuerName;
    }
    
    public void setCertificateIssuerName(final String certificateIssuerName) {
        this.certificateIssuerName = certificateIssuerName;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getCertificateName() {
        return this.certificateName;
    }
    
    public void setCertificateName(final String certificateName) {
        this.certificateName = certificateName;
    }
    
    public String getSignatureAlgorithmName() {
        return this.signatureAlgorithmName;
    }
    
    public void setSignatureAlgorithmName(final String signatureAlgorithmName) {
        this.signatureAlgorithmName = signatureAlgorithmName;
    }
    
    public String getCertificateExpiry() {
        return this.certificateExpiry;
    }
    
    public void setCertificateExpiry(final String certificateExpiry) {
        this.certificateExpiry = certificateExpiry;
    }
    
    public boolean isIdentity() {
        return this.identity;
    }
    
    public void setIdentity(final boolean identity) {
        this.identity = identity;
    }
    
    public String getCertificateSubjectName() {
        return this.certificateSubjectName;
    }
    
    public void setCertificateSubjectName(final String certificateSubjectName) {
        this.certificateSubjectName = certificateSubjectName;
    }
    
    public String getSignatureAlgorithmOID() {
        return this.signatureAlgorithmOID;
    }
    
    public void setSignatureAlgorithmOID(final String signatureAlgorithmOID) {
        this.signatureAlgorithmOID = signatureAlgorithmOID;
    }
}
