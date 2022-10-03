package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BuildingAddress extends GenericJson
{
    @Key
    private List<String> addressLines;
    @Key
    private String administrativeArea;
    @Key
    private String languageCode;
    @Key
    private String locality;
    @Key
    private String postalCode;
    @Key
    private String regionCode;
    @Key
    private String sublocality;
    
    public List<String> getAddressLines() {
        return this.addressLines;
    }
    
    public BuildingAddress setAddressLines(final List<String> addressLines) {
        this.addressLines = addressLines;
        return this;
    }
    
    public String getAdministrativeArea() {
        return this.administrativeArea;
    }
    
    public BuildingAddress setAdministrativeArea(final String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }
    
    public String getLanguageCode() {
        return this.languageCode;
    }
    
    public BuildingAddress setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
        return this;
    }
    
    public String getLocality() {
        return this.locality;
    }
    
    public BuildingAddress setLocality(final String locality) {
        this.locality = locality;
        return this;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public BuildingAddress setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
        return this;
    }
    
    public String getRegionCode() {
        return this.regionCode;
    }
    
    public BuildingAddress setRegionCode(final String regionCode) {
        this.regionCode = regionCode;
        return this;
    }
    
    public String getSublocality() {
        return this.sublocality;
    }
    
    public BuildingAddress setSublocality(final String sublocality) {
        this.sublocality = sublocality;
        return this;
    }
    
    public BuildingAddress set(final String fieldName, final Object value) {
        return (BuildingAddress)super.set(fieldName, value);
    }
    
    public BuildingAddress clone() {
        return (BuildingAddress)super.clone();
    }
}
