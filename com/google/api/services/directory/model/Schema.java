package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Schema extends GenericJson
{
    @Key
    private String displayName;
    @Key
    private String etag;
    @Key
    private List<SchemaFieldSpec> fields;
    @Key
    private String kind;
    @Key
    private String schemaId;
    @Key
    private String schemaName;
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public Schema setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Schema setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<SchemaFieldSpec> getFields() {
        return this.fields;
    }
    
    public Schema setFields(final List<SchemaFieldSpec> fields) {
        this.fields = fields;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Schema setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getSchemaId() {
        return this.schemaId;
    }
    
    public Schema setSchemaId(final String schemaId) {
        this.schemaId = schemaId;
        return this;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public Schema setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
        return this;
    }
    
    public Schema set(final String fieldName, final Object value) {
        return (Schema)super.set(fieldName, value);
    }
    
    public Schema clone() {
        return (Schema)super.clone();
    }
}
