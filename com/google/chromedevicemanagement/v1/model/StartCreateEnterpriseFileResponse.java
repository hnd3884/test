package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StartCreateEnterpriseFileResponse extends GenericJson
{
    @Key
    private String transientFileId;
    
    public String getTransientFileId() {
        return this.transientFileId;
    }
    
    public StartCreateEnterpriseFileResponse setTransientFileId(final String transientFileId) {
        this.transientFileId = transientFileId;
        return this;
    }
    
    public StartCreateEnterpriseFileResponse set(final String s, final Object o) {
        return (StartCreateEnterpriseFileResponse)super.set(s, o);
    }
    
    public StartCreateEnterpriseFileResponse clone() {
        return (StartCreateEnterpriseFileResponse)super.clone();
    }
}
