package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserPhoto extends GenericJson
{
    @Key
    private String etag;
    @Key
    private Integer height;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private String mimeType;
    @Key
    private String photoData;
    @Key
    private String primaryEmail;
    @Key
    private Integer width;
    
    public String getEtag() {
        return this.etag;
    }
    
    public UserPhoto setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public Integer getHeight() {
        return this.height;
    }
    
    public UserPhoto setHeight(final Integer height) {
        this.height = height;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public UserPhoto setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public UserPhoto setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
    
    public UserPhoto setMimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
    
    public String getPhotoData() {
        return this.photoData;
    }
    
    public byte[] decodePhotoData() {
        return Base64.decodeBase64(this.photoData);
    }
    
    public UserPhoto setPhotoData(final String photoData) {
        this.photoData = photoData;
        return this;
    }
    
    public UserPhoto encodePhotoData(final byte[] photoData) {
        this.photoData = Base64.encodeBase64URLSafeString(photoData);
        return this;
    }
    
    public String getPrimaryEmail() {
        return this.primaryEmail;
    }
    
    public UserPhoto setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
        return this;
    }
    
    public Integer getWidth() {
        return this.width;
    }
    
    public UserPhoto setWidth(final Integer width) {
        this.width = width;
        return this;
    }
    
    public UserPhoto set(final String fieldName, final Object value) {
        return (UserPhoto)super.set(fieldName, value);
    }
    
    public UserPhoto clone() {
        return (UserPhoto)super.clone();
    }
}
