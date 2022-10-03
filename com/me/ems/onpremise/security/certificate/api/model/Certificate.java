package com.me.ems.onpremise.security.certificate.api.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Certificate
{
    Boolean isThirdPartyCertificateInstalled;
    String commonName;
    String creationDate;
    String expiryDate;
    String issuerName;
    String issuerOrganisationName;
    Set subjectAlternativeNames;
    Boolean isTrustedCommunicationEnabled;
    
    public Boolean getThirdPartyCertificateInstalled() {
        return this.isThirdPartyCertificateInstalled;
    }
    
    public void setThirdPartyCertificateInstalled(final Boolean thirdPartyCertificateInstalled) {
        this.isThirdPartyCertificateInstalled = thirdPartyCertificateInstalled;
    }
    
    public String getCommonName() {
        return this.commonName;
    }
    
    public void setCommonName(final String commonName) {
        this.commonName = commonName;
    }
    
    public String getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getExpiryDate() {
        return this.expiryDate;
    }
    
    public void setExpiryDate(final String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getIssuerName() {
        return this.issuerName;
    }
    
    public void setIssuerName(final String issuerName) {
        this.issuerName = issuerName;
    }
    
    public String getIssuerOrganisationName() {
        return this.issuerOrganisationName;
    }
    
    public void setIssuerOrganisationName(final String issuerOrganisationName) {
        this.issuerOrganisationName = issuerOrganisationName;
    }
    
    public Set getSubjectAlternativeNames() {
        return this.subjectAlternativeNames;
    }
    
    public void setSubjectAlternativeNames(final Set subjectAlternativeNames) {
        this.subjectAlternativeNames = subjectAlternativeNames;
    }
    
    public Boolean getIsTrustedCommunicationEnabled() {
        return this.isTrustedCommunicationEnabled;
    }
    
    public void setIsTrustedCommunicationEnabled(final Boolean isTrustedCommunicationEnabled) {
        this.isTrustedCommunicationEnabled = isTrustedCommunicationEnabled;
    }
}
