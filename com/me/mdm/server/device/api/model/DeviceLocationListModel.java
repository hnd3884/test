package com.me.mdm.server.device.api.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceLocationListModel
{
    @JsonProperty("locations")
    private List<DeviceLocationModel> locations;
    @JsonProperty("error_map")
    private LocationErrorModel errorMap;
    
    public DeviceLocationListModel() {
        this.locations = new ArrayList<DeviceLocationModel>();
    }
    
    @JsonProperty("locations")
    public List<DeviceLocationModel> getLocations() {
        return this.locations;
    }
    
    @JsonProperty("locations")
    public void setLocations(final List<DeviceLocationModel> locations) {
        this.locations = locations;
    }
    
    @JsonProperty("error_map")
    public LocationErrorModel getErrorMap() {
        return this.errorMap;
    }
    
    @JsonProperty("error_map")
    public void setErrorMap(final LocationErrorModel errorMap) {
        this.errorMap = errorMap;
    }
}
