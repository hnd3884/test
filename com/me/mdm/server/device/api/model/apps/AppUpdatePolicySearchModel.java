package com.me.mdm.server.device.api.model.apps;

import com.me.mdm.api.paging.annotations.SearchParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.me.mdm.api.paging.model.Pagination;

public class AppUpdatePolicySearchModel extends Pagination
{
    @JsonProperty("policy_name")
    @SearchParam(value = "profilename", tableName = "Profile", columnName = "PROFILE_NAME")
    private String policyName;
    @JsonProperty("app_id")
    @SearchParam(value = "app_id", tableName = "AutoAppUpdatePackageList", columnName = "PACKAGE_ID", comparator = 0)
    private Long packageId;
    
    public void setPackageId(final Long packageId) {
        this.packageId = packageId;
    }
    
    public Long getPackageId() {
        return this.packageId;
    }
    
    public void setPolicyName(final String policyName) {
        this.policyName = policyName;
    }
    
    public String getPolicyName() {
        return this.policyName;
    }
}
