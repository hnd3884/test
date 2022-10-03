package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppRestrictionsSchema extends GenericJson
{
    @Key
    private String kind;
    @Key
    private List<AppRestrictionsSchemaRestriction> restrictions;
    
    public String getKind() {
        return this.kind;
    }
    
    public AppRestrictionsSchema setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<AppRestrictionsSchemaRestriction> getRestrictions() {
        return this.restrictions;
    }
    
    public AppRestrictionsSchema setRestrictions(final List<AppRestrictionsSchemaRestriction> restrictions) {
        this.restrictions = restrictions;
        return this;
    }
    
    public AppRestrictionsSchema set(final String fieldName, final Object value) {
        return (AppRestrictionsSchema)super.set(fieldName, value);
    }
    
    public AppRestrictionsSchema clone() {
        return (AppRestrictionsSchema)super.clone();
    }
}
