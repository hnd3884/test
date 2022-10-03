package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;

public class SharedDevice
{
    @SerializedName("estimated_user")
    private Integer estimatedUser;
    @SerializedName("resident_user")
    private Integer residentUser;
    @SerializedName("quota_size")
    private String quotaSize;
    
    public Integer getEstimatedUser() {
        return this.estimatedUser;
    }
    
    public void setEstimatedUser(final Integer estimatedUser) {
        this.estimatedUser = estimatedUser;
    }
    
    public Integer getResidentUser() {
        return this.residentUser;
    }
    
    public void setResidentUser(final Integer residentUser) {
        this.residentUser = residentUser;
    }
    
    public String getQuotaSize() {
        return this.quotaSize;
    }
    
    public void setQuotaSize(final String quotaSize) {
        this.quotaSize = quotaSize;
    }
}
