package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Members extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private List<Member> members;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Members setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Members setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<Member> getMembers() {
        return this.members;
    }
    
    public Members setMembers(final List<Member> members) {
        this.members = members;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Members setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public Members set(final String fieldName, final Object value) {
        return (Members)super.set(fieldName, value);
    }
    
    public Members clone() {
        return (Members)super.clone();
    }
    
    static {
        Data.nullOf((Class)Member.class);
    }
}
