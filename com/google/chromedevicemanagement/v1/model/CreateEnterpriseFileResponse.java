package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CreateEnterpriseFileResponse extends GenericJson
{
    @Key
    private String downloadUrl;
    @Key
    private String transientFileId;
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    
    public CreateEnterpriseFileResponse setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
    
    public String getTransientFileId() {
        return this.transientFileId;
    }
    
    public CreateEnterpriseFileResponse setTransientFileId(final String transientFileId) {
        this.transientFileId = transientFileId;
        return this;
    }
    
    public CreateEnterpriseFileResponse set(final String s, final Object o) {
        return (CreateEnterpriseFileResponse)super.set(s, o);
    }
    
    public CreateEnterpriseFileResponse clone() {
        return (CreateEnterpriseFileResponse)super.clone();
    }
}
