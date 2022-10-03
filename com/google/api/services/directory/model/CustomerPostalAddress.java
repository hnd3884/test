package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CustomerPostalAddress extends GenericJson
{
    @Key
    private String addressLine1;
    @Key
    private String addressLine2;
    @Key
    private String addressLine3;
    @Key
    private String contactName;
    @Key
    private String countryCode;
    @Key
    private String locality;
    @Key
    private String organizationName;
    @Key
    private String postalCode;
    @Key
    private String region;
    
    public String getAddressLine1() {
        return this.addressLine1;
    }
    
    public CustomerPostalAddress setAddressLine1(final String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }
    
    public String getAddressLine2() {
        return this.addressLine2;
    }
    
    public CustomerPostalAddress setAddressLine2(final String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }
    
    public String getAddressLine3() {
        return this.addressLine3;
    }
    
    public CustomerPostalAddress setAddressLine3(final String addressLine3) {
        this.addressLine3 = addressLine3;
        return this;
    }
    
    public String getContactName() {
        return this.contactName;
    }
    
    public CustomerPostalAddress setContactName(final String contactName) {
        this.contactName = contactName;
        return this;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }
    
    public CustomerPostalAddress setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    public String getLocality() {
        return this.locality;
    }
    
    public CustomerPostalAddress setLocality(final String locality) {
        this.locality = locality;
        return this;
    }
    
    public String getOrganizationName() {
        return this.organizationName;
    }
    
    public CustomerPostalAddress setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
        return this;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public CustomerPostalAddress setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public CustomerPostalAddress setRegion(final String region) {
        this.region = region;
        return this;
    }
    
    public CustomerPostalAddress set(final String fieldName, final Object value) {
        return (CustomerPostalAddress)super.set(fieldName, value);
    }
    
    public CustomerPostalAddress clone() {
        return (CustomerPostalAddress)super.clone();
    }
}
