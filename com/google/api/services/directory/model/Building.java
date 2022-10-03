package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Building extends GenericJson
{
    @Key
    private BuildingAddress address;
    @Key
    private String buildingId;
    @Key
    private String buildingName;
    @Key
    private BuildingCoordinates coordinates;
    @Key
    private String description;
    @Key
    private String etags;
    @Key
    private List<String> floorNames;
    @Key
    private String kind;
    
    public BuildingAddress getAddress() {
        return this.address;
    }
    
    public Building setAddress(final BuildingAddress address) {
        this.address = address;
        return this;
    }
    
    public String getBuildingId() {
        return this.buildingId;
    }
    
    public Building setBuildingId(final String buildingId) {
        this.buildingId = buildingId;
        return this;
    }
    
    public String getBuildingName() {
        return this.buildingName;
    }
    
    public Building setBuildingName(final String buildingName) {
        this.buildingName = buildingName;
        return this;
    }
    
    public BuildingCoordinates getCoordinates() {
        return this.coordinates;
    }
    
    public Building setCoordinates(final BuildingCoordinates coordinates) {
        this.coordinates = coordinates;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Building setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getEtags() {
        return this.etags;
    }
    
    public Building setEtags(final String etags) {
        this.etags = etags;
        return this;
    }
    
    public List<String> getFloorNames() {
        return this.floorNames;
    }
    
    public Building setFloorNames(final List<String> floorNames) {
        this.floorNames = floorNames;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Building setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Building set(final String fieldName, final Object value) {
        return (Building)super.set(fieldName, value);
    }
    
    public Building clone() {
        return (Building)super.clone();
    }
}
