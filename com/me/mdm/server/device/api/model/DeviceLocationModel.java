package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceLocationModel
{
    @JsonProperty("added_time")
    private String addedTime;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("located_time")
    private String locatedTime;
    @JsonProperty("longitude")
    private String longitude;
    
    @JsonProperty("added_time")
    public String getAddedTime() {
        return this.addedTime;
    }
    
    @JsonProperty("added_time")
    public void setAddedTime(final String addedTime) {
        this.addedTime = addedTime;
    }
    
    @JsonProperty("latitude")
    public String getLatitude() {
        return this.latitude;
    }
    
    @JsonProperty("latitude")
    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }
    
    @JsonProperty("located_time")
    public String getLocatedTime() {
        return this.locatedTime;
    }
    
    @JsonProperty("located_time")
    public void setLocatedTime(final String locatedTime) {
        this.locatedTime = locatedTime;
    }
    
    @JsonProperty("longitude")
    public String getLongitude() {
        return this.longitude;
    }
    
    @JsonProperty("longitude")
    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }
}
