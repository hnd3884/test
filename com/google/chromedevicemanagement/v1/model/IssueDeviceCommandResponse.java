package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class IssueDeviceCommandResponse extends GenericJson
{
    @Key
    private String downloadUrl;
    @Key
    private String executeTime;
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    
    public IssueDeviceCommandResponse setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
    
    public String getExecuteTime() {
        return this.executeTime;
    }
    
    public IssueDeviceCommandResponse setExecuteTime(final String executeTime) {
        this.executeTime = executeTime;
        return this;
    }
    
    public IssueDeviceCommandResponse set(final String s, final Object o) {
        return (IssueDeviceCommandResponse)super.set(s, o);
    }
    
    public IssueDeviceCommandResponse clone() {
        return (IssueDeviceCommandResponse)super.clone();
    }
}
