package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserOrganization extends GenericJson
{
    @Key
    private String costCenter;
    @Key
    private String customType;
    @Key
    private String department;
    @Key
    private String description;
    @Key
    private String domain;
    @Key
    private Integer fullTimeEquivalent;
    @Key
    private String location;
    @Key
    private String name;
    @Key
    private Boolean primary;
    @Key
    private String symbol;
    @Key
    private String title;
    @Key
    private String type;
    
    public String getCostCenter() {
        return this.costCenter;
    }
    
    public UserOrganization setCostCenter(final String costCenter) {
        this.costCenter = costCenter;
        return this;
    }
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserOrganization setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public String getDepartment() {
        return this.department;
    }
    
    public UserOrganization setDepartment(final String department) {
        this.department = department;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public UserOrganization setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public UserOrganization setDomain(final String domain) {
        this.domain = domain;
        return this;
    }
    
    public Integer getFullTimeEquivalent() {
        return this.fullTimeEquivalent;
    }
    
    public UserOrganization setFullTimeEquivalent(final Integer fullTimeEquivalent) {
        this.fullTimeEquivalent = fullTimeEquivalent;
        return this;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public UserOrganization setLocation(final String location) {
        this.location = location;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public UserOrganization setName(final String name) {
        this.name = name;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserOrganization setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
    
    public UserOrganization setSymbol(final String symbol) {
        this.symbol = symbol;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public UserOrganization setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserOrganization setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserOrganization set(final String fieldName, final Object value) {
        return (UserOrganization)super.set(fieldName, value);
    }
    
    public UserOrganization clone() {
        return (UserOrganization)super.clone();
    }
}
