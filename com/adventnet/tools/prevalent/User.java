package com.adventnet.tools.prevalent;

import java.util.ArrayList;

public class User
{
    private String name;
    private String company;
    private String emailId;
    private String macId;
    private String days;
    private String date;
    private String licenseType;
    private String key;
    private ArrayList mapIds;
    private ArrayList codedIds;
    private String product;
    private float version;
    private String noOfRTLicense;
    private String emailRestrict;
    private String generatedDate;
    private String maxTrialPeriod;
    private String acntrlBasedTrial;
    private String noAllowed;
    private String expiryRelative;
    
    public User() {
        this.name = null;
        this.company = null;
        this.emailId = null;
        this.macId = null;
        this.date = null;
        this.licenseType = null;
        this.key = null;
        this.mapIds = null;
        this.codedIds = null;
        this.product = null;
        this.noOfRTLicense = null;
        this.emailRestrict = null;
        this.generatedDate = null;
        this.maxTrialPeriod = null;
        this.acntrlBasedTrial = null;
        this.noAllowed = null;
        this.expiryRelative = null;
        this.mapIds = new ArrayList();
        this.codedIds = new ArrayList();
    }
    
    public void setName(final String uName) {
        this.name = uName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setCompanyName(final String uCompany) {
        this.company = uCompany;
    }
    
    public String getCompanyName() {
        return this.company;
    }
    
    public void setMailId(final String mailId) {
        this.emailId = mailId;
    }
    
    public String getMailId() {
        return this.emailId;
    }
    
    public void setMacId(final String macAdd) {
        this.macId = macAdd;
    }
    
    public String getMacId() {
        return this.macId;
    }
    
    public void setNumberOfDays(final String totalDays) {
        this.days = totalDays;
    }
    
    public String getNumberOfDays() {
        return this.days;
    }
    
    public void setExpiryDate(final String expDate) {
        this.date = expDate;
    }
    
    public String getExpiryDate() {
        return this.date;
    }
    
    public void setLicenseType(final String lType) {
        this.licenseType = lType;
    }
    
    public String getLicenseType() {
        return this.licenseType;
    }
    
    public void setKey(final String lKey) {
        this.key = lKey;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void addID(final String mapId) {
        this.mapIds.add(mapId);
    }
    
    public ArrayList getIDs() {
        return this.mapIds;
    }
    
    public void addCodedID(final String mapId) {
        this.codedIds.add(mapId);
    }
    
    public ArrayList getCodedIDs() {
        return this.codedIds;
    }
    
    public void setNoOfRTLicense(final String rt) {
        this.noOfRTLicense = rt;
    }
    
    public void setEmailRestrict(final String er) {
        this.emailRestrict = er;
    }
    
    public String getNoOfRTLicense() {
        return this.noOfRTLicense;
    }
    
    public String getEmailRestrict() {
        return this.emailRestrict;
    }
    
    public void setGeneratedDate(final String date) {
        this.generatedDate = date;
    }
    
    public String getGeneratedDate() {
        return this.generatedDate;
    }
    
    public void setMaxTrialPeriod(final String date) {
        this.maxTrialPeriod = date;
    }
    
    public String getMaxTrialPeriod() {
        return this.maxTrialPeriod;
    }
    
    public void setTrialMACPolicy(final String acntrl) {
        this.acntrlBasedTrial = acntrl;
    }
    
    public String getTrialMACPolicy() {
        return this.acntrlBasedTrial;
    }
    
    public void setDownloadPerEmailID(final String na) {
        this.noAllowed = na;
    }
    
    public String getDownloadPerEmailID() {
        return this.noAllowed;
    }
    
    public void setExpiryRelative(final String er) {
        this.expiryRelative = er;
    }
    
    public String getExpiryRelative() {
        return this.expiryRelative;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(" Name:" + this.name);
        buf.append(" Company:" + this.company);
        buf.append(" Email:" + this.emailId);
        buf.append(" Mac:" + this.macId);
        buf.append(" Days:" + this.days);
        buf.append(" Date:" + this.date);
        buf.append(" Max. Eval Days:" + this.maxTrialPeriod);
        buf.append(" Is Trial MAC based:" + this.acntrlBasedTrial);
        buf.append(" Type:" + this.licenseType);
        buf.append(" Key:" + this.key);
        buf.append(" ID:" + this.mapIds);
        buf.append(" Coded ID:" + this.codedIds);
        return buf.toString();
    }
}
