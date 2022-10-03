package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DirectoryChromeosdevicesCommandResult extends GenericJson
{
    @Key
    private String errorMessage;
    @Key
    private String executeTime;
    @Key
    private String result;
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public DirectoryChromeosdevicesCommandResult setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
    
    public String getExecuteTime() {
        return this.executeTime;
    }
    
    public DirectoryChromeosdevicesCommandResult setExecuteTime(final String executeTime) {
        this.executeTime = executeTime;
        return this;
    }
    
    public String getResult() {
        return this.result;
    }
    
    public DirectoryChromeosdevicesCommandResult setResult(final String result) {
        this.result = result;
        return this;
    }
    
    public DirectoryChromeosdevicesCommandResult set(final String fieldName, final Object value) {
        return (DirectoryChromeosdevicesCommandResult)super.set(fieldName, value);
    }
    
    public DirectoryChromeosdevicesCommandResult clone() {
        return (DirectoryChromeosdevicesCommandResult)super.clone();
    }
}
