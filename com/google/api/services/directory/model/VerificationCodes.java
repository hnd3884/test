package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class VerificationCodes extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<VerificationCode> items;
    @Key
    private String kind;
    
    public String getEtag() {
        return this.etag;
    }
    
    public VerificationCodes setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<VerificationCode> getItems() {
        return this.items;
    }
    
    public VerificationCodes setItems(final List<VerificationCode> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public VerificationCodes setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public VerificationCodes set(final String fieldName, final Object value) {
        return (VerificationCodes)super.set(fieldName, value);
    }
    
    public VerificationCodes clone() {
        return (VerificationCodes)super.clone();
    }
    
    static {
        Data.nullOf((Class)VerificationCode.class);
    }
}
