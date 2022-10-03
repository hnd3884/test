package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UpdateFailureInfo extends GenericJson
{
    @Key
    private String errorCode;
    @Key
    private String errorMessage;
    @Key
    private String id;
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public UpdateFailureInfo setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public UpdateFailureInfo setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public UpdateFailureInfo setId(final String id) {
        this.id = id;
        return this;
    }
    
    public UpdateFailureInfo set(final String s, final Object o) {
        return (UpdateFailureInfo)super.set(s, o);
    }
    
    public UpdateFailureInfo clone() {
        return (UpdateFailureInfo)super.clone();
    }
}
