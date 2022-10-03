package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Group extends GenericJson
{
    @Key
    private Boolean adminCreated;
    @Key
    private List<String> aliases;
    @Key
    private String description;
    @Key
    @JsonString
    private Long directMembersCount;
    @Key
    private String email;
    @Key
    private String etag;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private String name;
    @Key
    private List<String> nonEditableAliases;
    
    public Boolean getAdminCreated() {
        return this.adminCreated;
    }
    
    public Group setAdminCreated(final Boolean adminCreated) {
        this.adminCreated = adminCreated;
        return this;
    }
    
    public List<String> getAliases() {
        return this.aliases;
    }
    
    public Group setAliases(final List<String> aliases) {
        this.aliases = aliases;
        return this;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Group setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public Long getDirectMembersCount() {
        return this.directMembersCount;
    }
    
    public Group setDirectMembersCount(final Long directMembersCount) {
        this.directMembersCount = directMembersCount;
        return this;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public Group setEmail(final String email) {
        this.email = email;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Group setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Group setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Group setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Group setName(final String name) {
        this.name = name;
        return this;
    }
    
    public List<String> getNonEditableAliases() {
        return this.nonEditableAliases;
    }
    
    public Group setNonEditableAliases(final List<String> nonEditableAliases) {
        this.nonEditableAliases = nonEditableAliases;
        return this;
    }
    
    public Group set(final String fieldName, final Object value) {
        return (Group)super.set(fieldName, value);
    }
    
    public Group clone() {
        return (Group)super.clone();
    }
}
