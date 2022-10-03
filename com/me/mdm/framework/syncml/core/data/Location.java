package com.me.mdm.framework.syncml.core.data;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;

public class Location
{
    private String locUri;
    private String locName;
    
    public Location() {
    }
    
    public Location(final String locUri) {
        this.locUri = locUri;
    }
    
    @SyncMLElement(xmlElementName = "LocURI", isMandatory = true)
    public String getLocUri() {
        return this.locUri;
    }
    
    public void setLocUri(final String locUri) {
        this.locUri = locUri;
    }
    
    @SyncMLElement(xmlElementName = "LocName")
    public String getLocName() {
        return this.locName;
    }
    
    public void setLocName(final String locName) {
        this.locName = locName;
    }
}
