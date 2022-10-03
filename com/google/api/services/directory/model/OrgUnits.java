package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class OrgUnits extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private List<OrgUnit> organizationUnits;
    
    public String getEtag() {
        return this.etag;
    }
    
    public OrgUnits setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public OrgUnits setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<OrgUnit> getOrganizationUnits() {
        return this.organizationUnits;
    }
    
    public OrgUnits setOrganizationUnits(final List<OrgUnit> organizationUnits) {
        this.organizationUnits = organizationUnits;
        return this;
    }
    
    public OrgUnits set(final String fieldName, final Object value) {
        return (OrgUnits)super.set(fieldName, value);
    }
    
    public OrgUnits clone() {
        return (OrgUnits)super.clone();
    }
    
    static {
        Data.nullOf((Class)OrgUnit.class);
    }
}
