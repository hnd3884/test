package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ChromeOsDevices extends GenericJson
{
    @Key
    private List<ChromeOsDevice> chromeosdevices;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public List<ChromeOsDevice> getChromeosdevices() {
        return this.chromeosdevices;
    }
    
    public ChromeOsDevices setChromeosdevices(final List<ChromeOsDevice> chromeosdevices) {
        this.chromeosdevices = chromeosdevices;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public ChromeOsDevices setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public ChromeOsDevices setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public ChromeOsDevices setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public ChromeOsDevices set(final String fieldName, final Object value) {
        return (ChromeOsDevices)super.set(fieldName, value);
    }
    
    public ChromeOsDevices clone() {
        return (ChromeOsDevices)super.clone();
    }
    
    static {
        Data.nullOf((Class)ChromeOsDevice.class);
    }
}
