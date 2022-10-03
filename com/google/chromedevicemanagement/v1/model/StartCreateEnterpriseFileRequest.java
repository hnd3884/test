package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StartCreateEnterpriseFileRequest extends GenericJson
{
    @Key
    private String contentType;
    @Key
    private String settingType;
    
    public String getContentType() {
        return this.contentType;
    }
    
    public StartCreateEnterpriseFileRequest setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }
    
    public String getSettingType() {
        return this.settingType;
    }
    
    public StartCreateEnterpriseFileRequest setSettingType(final String settingType) {
        this.settingType = settingType;
        return this;
    }
    
    public StartCreateEnterpriseFileRequest set(final String s, final Object o) {
        return (StartCreateEnterpriseFileRequest)super.set(s, o);
    }
    
    public StartCreateEnterpriseFileRequest clone() {
        return (StartCreateEnterpriseFileRequest)super.clone();
    }
}
