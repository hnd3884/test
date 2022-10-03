package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DirectoryChromeosdevicesIssueCommandResponse extends GenericJson
{
    @Key
    @JsonString
    private Long commandId;
    
    public Long getCommandId() {
        return this.commandId;
    }
    
    public DirectoryChromeosdevicesIssueCommandResponse setCommandId(final Long commandId) {
        this.commandId = commandId;
        return this;
    }
    
    public DirectoryChromeosdevicesIssueCommandResponse set(final String fieldName, final Object value) {
        return (DirectoryChromeosdevicesIssueCommandResponse)super.set(fieldName, value);
    }
    
    public DirectoryChromeosdevicesIssueCommandResponse clone() {
        return (DirectoryChromeosdevicesIssueCommandResponse)super.clone();
    }
}
