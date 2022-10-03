package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppRestrictionsSchemaRestrictionRestrictionValue extends GenericJson
{
    @Key
    private String type;
    @Key
    private Boolean valueBool;
    @Key
    private Integer valueInteger;
    @Key
    private List<String> valueMultiselect;
    @Key
    private String valueString;
    
    public String getType() {
        return this.type;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue setType(final String type) {
        this.type = type;
        return this;
    }
    
    public Boolean getValueBool() {
        return this.valueBool;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue setValueBool(final Boolean valueBool) {
        this.valueBool = valueBool;
        return this;
    }
    
    public Integer getValueInteger() {
        return this.valueInteger;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue setValueInteger(final Integer valueInteger) {
        this.valueInteger = valueInteger;
        return this;
    }
    
    public List<String> getValueMultiselect() {
        return this.valueMultiselect;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue setValueMultiselect(final List<String> valueMultiselect) {
        this.valueMultiselect = valueMultiselect;
        return this;
    }
    
    public String getValueString() {
        return this.valueString;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue setValueString(final String valueString) {
        this.valueString = valueString;
        return this;
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue set(final String fieldName, final Object value) {
        return (AppRestrictionsSchemaRestrictionRestrictionValue)super.set(fieldName, value);
    }
    
    public AppRestrictionsSchemaRestrictionRestrictionValue clone() {
        return (AppRestrictionsSchemaRestrictionRestrictionValue)super.clone();
    }
}
