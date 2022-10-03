package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Privilege extends GenericJson
{
    @Key
    private List<Privilege> childPrivileges;
    @Key
    private String etag;
    @Key
    private Boolean isOuScopable;
    @Key
    private String kind;
    @Key
    private String privilegeName;
    @Key
    private String serviceId;
    @Key
    private String serviceName;
    
    public List<Privilege> getChildPrivileges() {
        return this.childPrivileges;
    }
    
    public Privilege setChildPrivileges(final List<Privilege> childPrivileges) {
        this.childPrivileges = childPrivileges;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Privilege setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public Boolean getIsOuScopable() {
        return this.isOuScopable;
    }
    
    public Privilege setIsOuScopable(final Boolean isOuScopable) {
        this.isOuScopable = isOuScopable;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Privilege setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getPrivilegeName() {
        return this.privilegeName;
    }
    
    public Privilege setPrivilegeName(final String privilegeName) {
        this.privilegeName = privilegeName;
        return this;
    }
    
    public String getServiceId() {
        return this.serviceId;
    }
    
    public Privilege setServiceId(final String serviceId) {
        this.serviceId = serviceId;
        return this;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public Privilege setServiceName(final String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public Privilege set(final String fieldName, final Object value) {
        return (Privilege)super.set(fieldName, value);
    }
    
    public Privilege clone() {
        return (Privilege)super.clone();
    }
}
