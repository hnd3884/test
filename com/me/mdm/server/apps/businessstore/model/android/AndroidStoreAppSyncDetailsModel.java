package com.me.mdm.server.apps.businessstore.model.android;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.server.apps.businessstore.model.BaseStoreAppSyncDetailsModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AndroidStoreAppSyncDetailsModel extends BaseStoreAppSyncDetailsModel
{
    @JsonProperty("releaselabelid")
    private Long releaseLabelId;
    @JsonProperty("errorcode")
    private Integer errorCode;
    @JsonProperty("remarks")
    private String remarks;
    
    public Long getReleaseLabelId() {
        return this.releaseLabelId;
    }
    
    public void setReleaseLabelId(final Long releaseLabelId) {
        this.releaseLabelId = releaseLabelId;
    }
    
    public Integer getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
}
