package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserAddress extends GenericJson
{
    @Key
    private String country;
    @Key
    private String countryCode;
    @Key
    private String customType;
    @Key
    private String extendedAddress;
    @Key
    private String formatted;
    @Key
    private String locality;
    @Key
    private String poBox;
    @Key
    private String postalCode;
    @Key
    private Boolean primary;
    @Key
    private String region;
    @Key
    private Boolean sourceIsStructured;
    @Key
    private String streetAddress;
    @Key
    private String type;
    
    public String getCountry() {
        return this.country;
    }
    
    public UserAddress setCountry(final String country) {
        this.country = country;
        return this;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }
    
    public UserAddress setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserAddress setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public String getExtendedAddress() {
        return this.extendedAddress;
    }
    
    public UserAddress setExtendedAddress(final String extendedAddress) {
        this.extendedAddress = extendedAddress;
        return this;
    }
    
    public String getFormatted() {
        return this.formatted;
    }
    
    public UserAddress setFormatted(final String formatted) {
        this.formatted = formatted;
        return this;
    }
    
    public String getLocality() {
        return this.locality;
    }
    
    public UserAddress setLocality(final String locality) {
        this.locality = locality;
        return this;
    }
    
    public String getPoBox() {
        return this.poBox;
    }
    
    public UserAddress setPoBox(final String poBox) {
        this.poBox = poBox;
        return this;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public UserAddress setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserAddress setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public UserAddress setRegion(final String region) {
        this.region = region;
        return this;
    }
    
    public Boolean getSourceIsStructured() {
        return this.sourceIsStructured;
    }
    
    public UserAddress setSourceIsStructured(final Boolean sourceIsStructured) {
        this.sourceIsStructured = sourceIsStructured;
        return this;
    }
    
    public String getStreetAddress() {
        return this.streetAddress;
    }
    
    public UserAddress setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserAddress setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserAddress set(final String fieldName, final Object value) {
        return (UserAddress)super.set(fieldName, value);
    }
    
    public UserAddress clone() {
        return (UserAddress)super.clone();
    }
}
