package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SchemaFieldSpec extends GenericJson
{
    @Key
    private String displayName;
    @Key
    private String etag;
    @Key
    private String fieldId;
    @Key
    private String fieldName;
    @Key
    private String fieldType;
    @Key
    private Boolean indexed;
    @Key
    private String kind;
    @Key
    private Boolean multiValued;
    @Key
    private NumericIndexingSpec numericIndexingSpec;
    @Key
    private String readAccessType;
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public SchemaFieldSpec setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public SchemaFieldSpec setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getFieldId() {
        return this.fieldId;
    }
    
    public SchemaFieldSpec setFieldId(final String fieldId) {
        this.fieldId = fieldId;
        return this;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public SchemaFieldSpec setFieldName(final String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
    
    public String getFieldType() {
        return this.fieldType;
    }
    
    public SchemaFieldSpec setFieldType(final String fieldType) {
        this.fieldType = fieldType;
        return this;
    }
    
    public Boolean getIndexed() {
        return this.indexed;
    }
    
    public SchemaFieldSpec setIndexed(final Boolean indexed) {
        this.indexed = indexed;
        return this;
    }
    
    public boolean isIndexed() {
        return this.indexed == null || this.indexed == Data.NULL_BOOLEAN || this.indexed;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public SchemaFieldSpec setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Boolean getMultiValued() {
        return this.multiValued;
    }
    
    public SchemaFieldSpec setMultiValued(final Boolean multiValued) {
        this.multiValued = multiValued;
        return this;
    }
    
    public NumericIndexingSpec getNumericIndexingSpec() {
        return this.numericIndexingSpec;
    }
    
    public SchemaFieldSpec setNumericIndexingSpec(final NumericIndexingSpec numericIndexingSpec) {
        this.numericIndexingSpec = numericIndexingSpec;
        return this;
    }
    
    public String getReadAccessType() {
        return this.readAccessType;
    }
    
    public SchemaFieldSpec setReadAccessType(final String readAccessType) {
        this.readAccessType = readAccessType;
        return this;
    }
    
    public SchemaFieldSpec set(final String fieldName, final Object value) {
        return (SchemaFieldSpec)super.set(fieldName, value);
    }
    
    public SchemaFieldSpec clone() {
        return (SchemaFieldSpec)super.clone();
    }
    
    public static final class NumericIndexingSpec extends GenericJson
    {
        @Key
        private Double maxValue;
        @Key
        private Double minValue;
        
        public Double getMaxValue() {
            return this.maxValue;
        }
        
        public NumericIndexingSpec setMaxValue(final Double maxValue) {
            this.maxValue = maxValue;
            return this;
        }
        
        public Double getMinValue() {
            return this.minValue;
        }
        
        public NumericIndexingSpec setMinValue(final Double minValue) {
            this.minValue = minValue;
            return this;
        }
        
        public NumericIndexingSpec set(final String fieldName, final Object value) {
            return (NumericIndexingSpec)super.set(fieldName, value);
        }
        
        public NumericIndexingSpec clone() {
            return (NumericIndexingSpec)super.clone();
        }
    }
}
