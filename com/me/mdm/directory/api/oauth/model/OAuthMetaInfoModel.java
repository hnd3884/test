package com.me.mdm.directory.api.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthMetaInfoModel
{
    @JsonProperty("added_by")
    private Long addedBy;
    @JsonProperty("modified_by")
    private Long modifiedBy;
    @JsonProperty("added_at")
    private Long addedAt;
    @JsonProperty("modified_at")
    private Long modifiedAt;
    @JsonProperty("customer_id")
    private Long customerId;
    
    public Long getAddedBy() {
        return this.addedBy;
    }
    
    public void setAddedBy(final Long addedBy) {
        this.addedBy = addedBy;
    }
    
    public Long getModifiedBy() {
        return this.modifiedBy;
    }
    
    public void setModifiedBy(final Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    
    public Long getAddedAt() {
        return this.addedAt;
    }
    
    public void setAddedAt(final Long addedAt) {
        this.addedAt = addedAt;
    }
    
    public Long getModifiedAt() {
        return this.modifiedAt;
    }
    
    public void setModifiedAt(final Long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
}
