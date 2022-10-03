package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Role extends GenericJson
{
    @Key
    private String etag;
    @Key
    private Boolean isSuperAdminRole;
    @Key
    private Boolean isSystemRole;
    @Key
    private String kind;
    @Key
    private String roleDescription;
    @Key
    @JsonString
    private Long roleId;
    @Key
    private String roleName;
    @Key
    private List<RolePrivileges> rolePrivileges;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Role setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public Boolean getIsSuperAdminRole() {
        return this.isSuperAdminRole;
    }
    
    public Role setIsSuperAdminRole(final Boolean isSuperAdminRole) {
        this.isSuperAdminRole = isSuperAdminRole;
        return this;
    }
    
    public Boolean getIsSystemRole() {
        return this.isSystemRole;
    }
    
    public Role setIsSystemRole(final Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Role setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getRoleDescription() {
        return this.roleDescription;
    }
    
    public Role setRoleDescription(final String roleDescription) {
        this.roleDescription = roleDescription;
        return this;
    }
    
    public Long getRoleId() {
        return this.roleId;
    }
    
    public Role setRoleId(final Long roleId) {
        this.roleId = roleId;
        return this;
    }
    
    public String getRoleName() {
        return this.roleName;
    }
    
    public Role setRoleName(final String roleName) {
        this.roleName = roleName;
        return this;
    }
    
    public List<RolePrivileges> getRolePrivileges() {
        return this.rolePrivileges;
    }
    
    public Role setRolePrivileges(final List<RolePrivileges> rolePrivileges) {
        this.rolePrivileges = rolePrivileges;
        return this;
    }
    
    public Role set(final String fieldName, final Object value) {
        return (Role)super.set(fieldName, value);
    }
    
    public Role clone() {
        return (Role)super.clone();
    }
    
    static {
        Data.nullOf((Class)RolePrivileges.class);
    }
    
    public static final class RolePrivileges extends GenericJson
    {
        @Key
        private String privilegeName;
        @Key
        private String serviceId;
        
        public String getPrivilegeName() {
            return this.privilegeName;
        }
        
        public RolePrivileges setPrivilegeName(final String privilegeName) {
            this.privilegeName = privilegeName;
            return this;
        }
        
        public String getServiceId() {
            return this.serviceId;
        }
        
        public RolePrivileges setServiceId(final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }
        
        public RolePrivileges set(final String fieldName, final Object value) {
            return (RolePrivileges)super.set(fieldName, value);
        }
        
        public RolePrivileges clone() {
            return (RolePrivileges)super.clone();
        }
    }
}
