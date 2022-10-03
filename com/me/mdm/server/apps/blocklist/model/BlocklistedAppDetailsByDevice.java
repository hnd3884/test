package com.me.mdm.server.apps.blocklist.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlocklistedAppDetailsByDevice
{
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("platform_type")
    private int platformType;
    @JsonProperty("group_display_name")
    private String groupName;
    @JsonProperty("status")
    private int status;
    @JsonProperty("scope")
    private int scope;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("app_id")
    private Long appGroupID;
    
    public int getPlatformType() {
        return this.platformType;
    }
    
    public int getScope() {
        return this.scope;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public void setPlatformType(final int platformType) {
        this.platformType = platformType;
    }
    
    public void setScope(final int scope) {
        this.scope = scope;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public String getImageUrl() {
        return this.imageUrl;
    }
    
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Long getAppGroupID() {
        return this.appGroupID;
    }
    
    public void setAppGroupID(final Long appGroupID) {
        this.appGroupID = appGroupID;
    }
}
