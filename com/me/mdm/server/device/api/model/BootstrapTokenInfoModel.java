package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BootstrapTokenInfoModel
{
    @JsonProperty("allowed_for_auth")
    private Integer allowedForAuth;
    @JsonProperty("req_for_kernal_ext_approve")
    private Integer reqForKernalExtApprove;
    @JsonProperty("req_for_software_update")
    private Integer reqForSoftwareUpdate;
    @JsonProperty("bootstraptoken_allowed")
    private Integer bootstraptokenAllowed;
    @JsonProperty("is_bootstraptoken_available")
    private Integer isBootstrapTokenAvailable;
    
    public Integer getAllowedForAuth() {
        return this.allowedForAuth;
    }
    
    public void setAllowedForAuth(final Integer allowedForAuth) {
        this.allowedForAuth = allowedForAuth;
    }
    
    public Integer getReqForKernalExtApprove() {
        return this.reqForKernalExtApprove;
    }
    
    public void setReqForKernalExtApprove(final Integer reqForKernalExtApprove) {
        this.reqForKernalExtApprove = reqForKernalExtApprove;
    }
    
    public Integer getReqForSoftwareUpdate() {
        return this.reqForSoftwareUpdate;
    }
    
    public void setReqForSoftwareUpdate(final Integer reqForSoftwareUpdate) {
        this.reqForSoftwareUpdate = reqForSoftwareUpdate;
    }
    
    public Integer getBootstraptokenAllowed() {
        return this.bootstraptokenAllowed;
    }
    
    public void setBootstraptokenAllowed(final Integer bootstraptokenAllowed) {
        this.bootstraptokenAllowed = bootstraptokenAllowed;
    }
    
    public Integer getIsBootstrapTokenAvailable() {
        return this.isBootstrapTokenAvailable;
    }
    
    public void setIsBootstrapTokenAvailable(final String bootstrapToken) {
        this.isBootstrapTokenAvailable = ((bootstrapToken != null) ? 1 : 2);
    }
}
