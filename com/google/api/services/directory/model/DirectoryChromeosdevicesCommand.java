package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DirectoryChromeosdevicesCommand extends GenericJson
{
    @Key
    private String commandExpireTime;
    @Key
    @JsonString
    private Long commandId;
    @Key
    private DirectoryChromeosdevicesCommandResult commandResult;
    @Key
    private String issueTime;
    @Key
    private String payload;
    @Key
    private String state;
    @Key
    private String type;
    
    public String getCommandExpireTime() {
        return this.commandExpireTime;
    }
    
    public DirectoryChromeosdevicesCommand setCommandExpireTime(final String commandExpireTime) {
        this.commandExpireTime = commandExpireTime;
        return this;
    }
    
    public Long getCommandId() {
        return this.commandId;
    }
    
    public DirectoryChromeosdevicesCommand setCommandId(final Long commandId) {
        this.commandId = commandId;
        return this;
    }
    
    public DirectoryChromeosdevicesCommandResult getCommandResult() {
        return this.commandResult;
    }
    
    public DirectoryChromeosdevicesCommand setCommandResult(final DirectoryChromeosdevicesCommandResult commandResult) {
        this.commandResult = commandResult;
        return this;
    }
    
    public String getIssueTime() {
        return this.issueTime;
    }
    
    public DirectoryChromeosdevicesCommand setIssueTime(final String issueTime) {
        this.issueTime = issueTime;
        return this;
    }
    
    public String getPayload() {
        return this.payload;
    }
    
    public DirectoryChromeosdevicesCommand setPayload(final String payload) {
        this.payload = payload;
        return this;
    }
    
    public String getState() {
        return this.state;
    }
    
    public DirectoryChromeosdevicesCommand setState(final String state) {
        this.state = state;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public DirectoryChromeosdevicesCommand setType(final String type) {
        this.type = type;
        return this;
    }
    
    public DirectoryChromeosdevicesCommand set(final String fieldName, final Object value) {
        return (DirectoryChromeosdevicesCommand)super.set(fieldName, value);
    }
    
    public DirectoryChromeosdevicesCommand clone() {
        return (DirectoryChromeosdevicesCommand)super.clone();
    }
}
