package com.me.mdm.server.apps.businessstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessStoresSyncModel
{
    @JsonProperty("last_sync_time")
    private String lastSyncTime;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("total_apps_count")
    private Integer totalAppsCount;
    @JsonProperty("successful_apps_count")
    private Integer successfulAppsCount;
    @JsonProperty("completed_apps_count")
    private Integer completedAppsCount;
    @JsonProperty("failed_apps_count")
    private Integer failedAppsCount;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("if_sync_failed")
    private Boolean ifSyncFailed;
    @JsonProperty("trashed_apps_count")
    private Integer trashedAppsCount;
    
    public void setLastSyncTime(final String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public void setIfSyncFailed(final boolean ifSyncFailed) {
        this.ifSyncFailed = ifSyncFailed;
    }
    
    public void setTotalAppsCount(final int totalAppsCount) {
        this.totalAppsCount = totalAppsCount;
    }
    
    public void setCompletedAppsCount(final int completedAppsCount) {
        this.completedAppsCount = completedAppsCount;
    }
    
    public void setSuccessfulAppsCount(final int successfulAppsCount) {
        this.successfulAppsCount = successfulAppsCount;
    }
    
    public void setFailedAppsCount(final int failedAppsCount) {
        this.failedAppsCount = failedAppsCount;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public void setTrashedAppsCount(final int trashedAppsCount) {
        this.trashedAppsCount = trashedAppsCount;
    }
}
