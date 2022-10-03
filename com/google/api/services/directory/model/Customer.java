package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Customer extends GenericJson
{
    @Key
    private String alternateEmail;
    @Key
    private DateTime customerCreationTime;
    @Key
    private String customerDomain;
    @Key
    private String etag;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private String language;
    @Key
    private String phoneNumber;
    @Key
    private CustomerPostalAddress postalAddress;
    
    public String getAlternateEmail() {
        return this.alternateEmail;
    }
    
    public Customer setAlternateEmail(final String alternateEmail) {
        this.alternateEmail = alternateEmail;
        return this;
    }
    
    public DateTime getCustomerCreationTime() {
        return this.customerCreationTime;
    }
    
    public Customer setCustomerCreationTime(final DateTime customerCreationTime) {
        this.customerCreationTime = customerCreationTime;
        return this;
    }
    
    public String getCustomerDomain() {
        return this.customerDomain;
    }
    
    public Customer setCustomerDomain(final String customerDomain) {
        this.customerDomain = customerDomain;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Customer setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Customer setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Customer setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public Customer setLanguage(final String language) {
        this.language = language;
        return this;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public Customer setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
    
    public CustomerPostalAddress getPostalAddress() {
        return this.postalAddress;
    }
    
    public Customer setPostalAddress(final CustomerPostalAddress postalAddress) {
        this.postalAddress = postalAddress;
        return this;
    }
    
    public Customer set(final String fieldName, final Object value) {
        return (Customer)super.set(fieldName, value);
    }
    
    public Customer clone() {
        return (Customer)super.clone();
    }
}
