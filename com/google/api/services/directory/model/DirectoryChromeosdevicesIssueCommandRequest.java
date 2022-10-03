package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DirectoryChromeosdevicesIssueCommandRequest extends GenericJson
{
    @Key
    private String commandType;
    @Key
    private String payload;
    
    public String getCommandType() {
        return this.commandType;
    }
    
    public DirectoryChromeosdevicesIssueCommandRequest setCommandType(final String commandType) {
        this.commandType = commandType;
        return this;
    }
    
    public String getPayload() {
        return this.payload;
    }
    
    public DirectoryChromeosdevicesIssueCommandRequest setPayload(final String payload) {
        this.payload = payload;
        return this;
    }
    
    public DirectoryChromeosdevicesIssueCommandRequest set(final String fieldName, final Object value) {
        return (DirectoryChromeosdevicesIssueCommandRequest)super.set(fieldName, value);
    }
    
    public DirectoryChromeosdevicesIssueCommandRequest clone() {
        return (DirectoryChromeosdevicesIssueCommandRequest)super.clone();
    }
}
