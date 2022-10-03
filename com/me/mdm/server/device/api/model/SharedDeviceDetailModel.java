package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedDeviceDetailModel
{
    @JsonProperty("estimated_user")
    private Integer estimatedUser;
    @JsonProperty("resident_user")
    private Integer residentUser;
    @JsonProperty("quota_size")
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
