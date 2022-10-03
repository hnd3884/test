package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserLocation extends GenericJson
{
    @Key
    private String area;
    @Key
    private String buildingId;
    @Key
    private String customType;
    @Key
    private String deskCode;
    @Key
    private String floorName;
    @Key
    private String floorSection;
    @Key
    private String type;
    
    public String getArea() {
        return this.area;
    }
    
    public UserLocation setArea(final String area) {
        this.area = area;
        return this;
    }
    
    public String getBuildingId() {
        return this.buildingId;
    }
    
    public UserLocation setBuildingId(final String buildingId) {
        this.buildingId = buildingId;
        return this;
    }
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserLocation setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public String getDeskCode() {
        return this.deskCode;
    }
    
    public UserLocation setDeskCode(final String deskCode) {
        this.deskCode = deskCode;
        return this;
    }
    
    public String getFloorName() {
        return this.floorName;
    }
    
    public UserLocation setFloorName(final String floorName) {
        this.floorName = floorName;
        return this;
    }
    
    public String getFloorSection() {
        return this.floorSection;
    }
    
    public UserLocation setFloorSection(final String floorSection) {
        this.floorSection = floorSection;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserLocation setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserLocation set(final String fieldName, final Object value) {
        return (UserLocation)super.set(fieldName, value);
    }
    
    public UserLocation clone() {
        return (UserLocation)super.clone();
    }
}
