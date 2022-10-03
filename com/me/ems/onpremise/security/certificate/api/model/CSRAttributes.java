package com.me.ems.onpremise.security.certificate.api.model;

import java.util.ArrayList;

public class CSRAttributes
{
    private String country;
    private String state;
    private String locality;
    private String organizationName;
    private String organizationalUnit;
    private String commonName;
    private String emailAddress;
    private String challengePassword;
    private String companyName;
    private ArrayList<String> sanNames;
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(final String country) {
        this.country = country;
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
    public String getLocality() {
        return this.locality;
    }
    
    public void setLocality(final String locality) {
        this.locality = locality;
    }
    
    public String getOrganizationName() {
        return this.organizationName;
    }
    
    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }
    
    public String getOrganizationalUnit() {
        return this.organizationalUnit;
    }
    
    public void setOrganizationalUnit(final String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }
    
    public String getCommonName() {
        return this.commonName;
    }
    
    public void setCommonName(final String commonName) {
        this.commonName = commonName;
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getChallengePassword() {
        return this.challengePassword;
    }
    
    public void setChallengePassword(final String challengePassword) {
        this.challengePassword = challengePassword;
    }
    
    public String getCompanyName() {
        return this.companyName;
    }
    
    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }
    
    public ArrayList<String> getSanNames() {
        return this.sanNames;
    }
    
    public void setSanNames(final ArrayList<String> sanNames) {
        this.sanNames = sanNames;
    }
}
