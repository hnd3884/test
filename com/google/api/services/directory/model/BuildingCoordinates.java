package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class BuildingCoordinates extends GenericJson
{
    @Key
    private Double latitude;
    @Key
    private Double longitude;
    
    public Double getLatitude() {
        return this.latitude;
    }
    
    public BuildingCoordinates setLatitude(final Double latitude) {
        this.latitude = latitude;
        return this;
    }
    
    public Double getLongitude() {
        return this.longitude;
    }
    
    public BuildingCoordinates setLongitude(final Double longitude) {
        this.longitude = longitude;
        return this;
    }
    
    public BuildingCoordinates set(final String fieldName, final Object value) {
        return (BuildingCoordinates)super.set(fieldName, value);
    }
    
    public BuildingCoordinates clone() {
        return (BuildingCoordinates)super.clone();
    }
}
