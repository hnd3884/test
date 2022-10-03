package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class OrgUnit extends GenericJson
{
    @Key
    private Boolean blockInheritance;
    @Key
    private String description;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String name;
    @Key
    private String orgUnitId;
    @Key
    private String orgUnitPath;
    @Key
    private String parentOrgUnitId;
    @Key
    private String parentOrgUnitPath;
    
    public Boolean getBlockInheritance() {
        return this.blockInheritance;
    }
    
    public OrgUnit setBlockInheritance(final Boolean blockInheritance) {
        this.blockInheritance = blockInheritance;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public OrgUnit setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public OrgUnit setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public OrgUnit setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public OrgUnit setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getOrgUnitId() {
        return this.orgUnitId;
    }
    
    public OrgUnit setOrgUnitId(final String orgUnitId) {
        this.orgUnitId = orgUnitId;
        return this;
    }
    
    public String getOrgUnitPath() {
        return this.orgUnitPath;
    }
    
    public OrgUnit setOrgUnitPath(final String orgUnitPath) {
        this.orgUnitPath = orgUnitPath;
        return this;
    }
    
    public String getParentOrgUnitId() {
        return this.parentOrgUnitId;
    }
    
    public OrgUnit setParentOrgUnitId(final String parentOrgUnitId) {
        this.parentOrgUnitId = parentOrgUnitId;
        return this;
    }
    
    public String getParentOrgUnitPath() {
        return this.parentOrgUnitPath;
    }
    
    public OrgUnit setParentOrgUnitPath(final String parentOrgUnitPath) {
        this.parentOrgUnitPath = parentOrgUnitPath;
        return this;
    }
    
    public OrgUnit set(final String fieldName, final Object value) {
        return (OrgUnit)super.set(fieldName, value);
    }
    
    public OrgUnit clone() {
        return (OrgUnit)super.clone();
    }
}
