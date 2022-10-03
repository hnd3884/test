package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class MobileDevices extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private List<MobileDevice> mobiledevices;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public MobileDevices setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public MobileDevices setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<MobileDevice> getMobiledevices() {
        return this.mobiledevices;
    }
    
    public MobileDevices setMobiledevices(final List<MobileDevice> mobiledevices) {
        this.mobiledevices = mobiledevices;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public MobileDevices setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public MobileDevices set(final String fieldName, final Object value) {
        return (MobileDevices)super.set(fieldName, value);
    }
    
    public MobileDevices clone() {
        return (MobileDevices)super.clone();
    }
    
    static {
        Data.nullOf((Class)MobileDevice.class);
    }
}
