package com.me.mdm.server.apps.businessstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseStoreAppSyncDetailsModel
{
    @JsonProperty("appname")
    private String appName;
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("appgroupid")
    private Long appGroupId;
    @JsonProperty("packageid")
    private Long packageId;
    @JsonProperty("displayimageloc")
    private String displayImageLoc;
    
    public String getAppName() {
        return this.appName;
    }
    
    public void setAppName(final String appName) {
        this.appName = appName;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public Long getAppGroupId() {
        return this.appGroupId;
    }
    
    public void setAppGroupId(final Long appGroupId) {
        this.appGroupId = appGroupId;
    }
    
    public Long getPackageId() {
        return this.packageId;
    }
    
    public void setPackageId(final Long packageId) {
        this.packageId = packageId;
    }
    
    public String getDisplayImageLoc() {
        return this.displayImageLoc;
    }
    
    public void setDisplayImageLoc(final String displayImageLoc) {
        this.displayImageLoc = displayImageLoc;
    }
}
