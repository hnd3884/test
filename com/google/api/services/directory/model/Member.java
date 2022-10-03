package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Member extends GenericJson
{
    @Key("delivery_settings")
    private String deliverySettings;
    @Key
    private String email;
    @Key
    private String etag;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private String role;
    @Key
    private String status;
    @Key
    private String type;
    
    public String getDeliverySettings() {
        return this.deliverySettings;
    }
    
    public Member setDeliverySettings(final String deliverySettings) {
        this.deliverySettings = deliverySettings;
        return this;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public Member setEmail(final String email) {
        this.email = email;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Member setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Member setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Member setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public Member setRole(final String role) {
        this.role = role;
        return this;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public Member setStatus(final String status) {
        this.status = status;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Member setType(final String type) {
        this.type = type;
        return this;
    }
    
    public Member set(final String fieldName, final Object value) {
        return (Member)super.set(fieldName, value);
    }
    
    public Member clone() {
        return (Member)super.clone();
    }
}
