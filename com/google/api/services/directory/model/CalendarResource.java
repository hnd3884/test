package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CalendarResource extends GenericJson
{
    @Key
    private String buildingId;
    @Key
    private Integer capacity;
    @Key
    private String etags;
    @Key
    private Object featureInstances;
    @Key
    private String floorName;
    @Key
    private String floorSection;
    @Key
    private String generatedResourceName;
    @Key
    private String kind;
    @Key
    private String resourceCategory;
    @Key
    private String resourceDescription;
    @Key
    private String resourceEmail;
    @Key
    private String resourceId;
    @Key
    private String resourceName;
    @Key
    private String resourceType;
    @Key
    private String userVisibleDescription;
    
    public String getBuildingId() {
        return this.buildingId;
    }
    
    public CalendarResource setBuildingId(final String buildingId) {
        this.buildingId = buildingId;
        return this;
    }
    
    public Integer getCapacity() {
        return this.capacity;
    }
    
    public CalendarResource setCapacity(final Integer capacity) {
        this.capacity = capacity;
        return this;
    }
    
    public String getEtags() {
        return this.etags;
    }
    
    public CalendarResource setEtags(final String etags) {
        this.etags = etags;
        return this;
    }
    
    public Object getFeatureInstances() {
        return this.featureInstances;
    }
    
    public CalendarResource setFeatureInstances(final Object featureInstances) {
        this.featureInstances = featureInstances;
        return this;
    }
    
    public String getFloorName() {
        return this.floorName;
    }
    
    public CalendarResource setFloorName(final String floorName) {
        this.floorName = floorName;
        return this;
    }
    
    public String getFloorSection() {
        return this.floorSection;
    }
    
    public CalendarResource setFloorSection(final String floorSection) {
        this.floorSection = floorSection;
        return this;
    }
    
    public String getGeneratedResourceName() {
        return this.generatedResourceName;
    }
    
    public CalendarResource setGeneratedResourceName(final String generatedResourceName) {
        this.generatedResourceName = generatedResourceName;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public CalendarResource setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getResourceCategory() {
        return this.resourceCategory;
    }
    
    public CalendarResource setResourceCategory(final String resourceCategory) {
        this.resourceCategory = resourceCategory;
        return this;
    }
    
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    
    public CalendarResource setResourceDescription(final String resourceDescription) {
        this.resourceDescription = resourceDescription;
        return this;
    }
    
    public String getResourceEmail() {
        return this.resourceEmail;
    }
    
    public CalendarResource setResourceEmail(final String resourceEmail) {
        this.resourceEmail = resourceEmail;
        return this;
    }
    
    public String getResourceId() {
        return this.resourceId;
    }
    
    public CalendarResource setResourceId(final String resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
    public String getResourceName() {
        return this.resourceName;
    }
    
    public CalendarResource setResourceName(final String resourceName) {
        this.resourceName = resourceName;
        return this;
    }
    
    public String getResourceType() {
        return this.resourceType;
    }
    
    public CalendarResource setResourceType(final String resourceType) {
        this.resourceType = resourceType;
        return this;
    }
    
    public String getUserVisibleDescription() {
        return this.userVisibleDescription;
    }
    
    public CalendarResource setUserVisibleDescription(final String userVisibleDescription) {
        this.userVisibleDescription = userVisibleDescription;
        return this;
    }
    
    public CalendarResource set(final String fieldName, final Object value) {
        return (CalendarResource)super.set(fieldName, value);
    }
    
    public CalendarResource clone() {
        return (CalendarResource)super.clone();
    }
}
