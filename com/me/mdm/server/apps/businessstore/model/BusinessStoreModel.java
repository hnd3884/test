package com.me.mdm.server.apps.businessstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessStoreModel extends BaseAPIModel
{
    @JsonProperty("last_sync_time")
    private String lastSyncTime;
    @JsonProperty("next_sync_time")
    private String nextSyncTime;
    @JsonProperty("businessstore_id")
    private Long businessStoreID;
    @JsonProperty("memdm_app_type")
    private Integer meMDMAppType;
    @JsonProperty("trashed_apps_count")
    private Integer trashedAppsCount;
    @JsonProperty("added_time")
    private String addedTime;
    @JsonProperty("added_by_user_name")
    private String addedByUserName;
    @JsonProperty("last_modified_time")
    private String lastModifiedTime;
    @JsonProperty("last_modified_by_user_name")
    private String lastModifiedByUserName;
    
    public void setBusinessStoreID(final Long businessStoreID) {
        this.businessStoreID = businessStoreID;
    }
    
    public void setLastSyncTime(final String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
    
    public void setMeMDMAppType(final int meMDMAppType) {
        this.meMDMAppType = meMDMAppType;
    }
    
    public void setTrashedAppsCount(final int trashedAppsCount) {
        this.trashedAppsCount = trashedAppsCount;
    }
    
    public void setNextSyncTime(final String nextSyncTime) {
        this.nextSyncTime = nextSyncTime;
    }
    
    public void setAddedTime(final String addedTime) {
        this.addedTime = addedTime;
    }
    
    public void setLastModifiedByUserName(final String lastModifiedByUserName) {
        this.lastModifiedByUserName = lastModifiedByUserName;
    }
    
    public void setAddedByUserName(final String addedByUserName) {
        this.addedByUserName = addedByUserName;
    }
    
    public void setLastModifiedTime(final String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
