package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationSettingModel
{
    @JsonProperty("location_services")
    private Integer locationServices;
    @JsonProperty("is_location_history_enabled")
    private Integer isLocationHistoryEnabled;
    @JsonProperty("tracking_status")
    private Integer trackingStatus;
    @JsonProperty("location_tracking_status")
    private Integer locationTrackingStatus;
    @JsonProperty("location_history_duration")
    private Integer locationHistoryDuration;
    @JsonProperty("location_interval")
    private Integer locationInterval;
    @JsonProperty("location_radius")
    private Integer locationRadius;
    
    public Integer getLocationServices() {
        return this.locationServices;
    }
    
    public void setLocationServices(final Integer locationServices) {
        this.locationServices = locationServices;
    }
    
    public Integer getIsLocationHistoryEnabled() {
        return this.isLocationHistoryEnabled;
    }
    
    public void setIsLocationHistoryEnabled(final Integer isLocationHistoryEnabled) {
        this.isLocationHistoryEnabled = isLocationHistoryEnabled;
    }
    
    public Integer getTrackingStatus() {
        return this.trackingStatus;
    }
    
    public void setTrackingStatus(final Integer trackingStatus) {
        this.trackingStatus = trackingStatus;
    }
    
    public Integer getLocationTrackingStatus() {
        return this.locationTrackingStatus;
    }
    
    public void setLocationTrackingStatus(final Integer locationTrackingStatus) {
        this.locationTrackingStatus = locationTrackingStatus;
    }
    
    public Integer getLocationHistoryDuration() {
        return this.locationHistoryDuration;
    }
    
    public void setLocationHistoryDuration(final Integer locationHistoryDuration) {
        this.locationHistoryDuration = locationHistoryDuration;
    }
    
    public Integer getLocationInterval() {
        return this.locationInterval;
    }
    
    public void setLocationInterval(final Integer locationInterval) {
        this.locationInterval = locationInterval;
    }
    
    public Integer getLocationRadius() {
        return this.locationRadius;
    }
    
    public void setLocationRadius(final Integer locationRadius) {
        this.locationRadius = locationRadius;
    }
}
