package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppRestrictionsSchemaRestriction extends GenericJson
{
    @Key
    private AppRestrictionsSchemaRestrictionRestrictionValue defaultValue;
    @Key
    private String description;
    @Key("entry")
    private List<String> entry__;
    @Key
    private List<String> entryValue;
    @Key
    private String key;
    @Key
    private List<AppRestrictionsSchemaRestriction> nestedRestriction;
    @Key
    private String restrictionType;
    @Key
    private String title;
    
    public AppRestrictionsSchemaRestrictionRestrictionValue getDefaultValue() {
        return this.defaultValue;
    }
    
    public AppRestrictionsSchemaRestriction setDefaultValue(final AppRestrictionsSchemaRestrictionRestrictionValue defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public AppRestrictionsSchemaRestriction setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public List<String> getEntry() {
        return this.entry__;
    }
    
    public AppRestrictionsSchemaRestriction setEntry(final List<String> entry__) {
        this.entry__ = entry__;
        return this;
    }
    
    public List<String> getEntryValue() {
        return this.entryValue;
    }
    
    public AppRestrictionsSchemaRestriction setEntryValue(final List<String> entryValue) {
        this.entryValue = entryValue;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public AppRestrictionsSchemaRestriction setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public List<AppRestrictionsSchemaRestriction> getNestedRestriction() {
        return this.nestedRestriction;
    }
    
    public AppRestrictionsSchemaRestriction setNestedRestriction(final List<AppRestrictionsSchemaRestriction> nestedRestriction) {
        this.nestedRestriction = nestedRestriction;
        return this;
    }
    
    public String getRestrictionType() {
        return this.restrictionType;
    }
    
    public AppRestrictionsSchemaRestriction setRestrictionType(final String restrictionType) {
        this.restrictionType = restrictionType;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public AppRestrictionsSchemaRestriction setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public AppRestrictionsSchemaRestriction set(final String fieldName, final Object value) {
        return (AppRestrictionsSchemaRestriction)super.set(fieldName, value);
    }
    
    public AppRestrictionsSchemaRestriction clone() {
        return (AppRestrictionsSchemaRestriction)super.clone();
    }
}
