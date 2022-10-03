package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class RoleAssignment extends GenericJson
{
    @Key
    private String assignedTo;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String orgUnitId;
    @Key
    @JsonString
    private Long roleAssignmentId;
    @Key
    @JsonString
    private Long roleId;
    @Key
    private String scopeType;
    
    public String getAssignedTo() {
        return this.assignedTo;
    }
    
    public RoleAssignment setAssignedTo(final String assignedTo) {
        this.assignedTo = assignedTo;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public RoleAssignment setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public RoleAssignment setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getOrgUnitId() {
        return this.orgUnitId;
    }
    
    public RoleAssignment setOrgUnitId(final String orgUnitId) {
        this.orgUnitId = orgUnitId;
        return this;
    }
    
    public Long getRoleAssignmentId() {
        return this.roleAssignmentId;
    }
    
    public RoleAssignment setRoleAssignmentId(final Long roleAssignmentId) {
        this.roleAssignmentId = roleAssignmentId;
        return this;
    }
    
    public Long getRoleId() {
        return this.roleId;
    }
    
    public RoleAssignment setRoleId(final Long roleId) {
        this.roleId = roleId;
        return this;
    }
    
    public String getScopeType() {
        return this.scopeType;
    }
    
    public RoleAssignment setScopeType(final String scopeType) {
        this.scopeType = scopeType;
        return this;
    }
    
    public RoleAssignment set(final String fieldName, final Object value) {
        return (RoleAssignment)super.set(fieldName, value);
    }
    
    public RoleAssignment clone() {
        return (RoleAssignment)super.clone();
    }
}
