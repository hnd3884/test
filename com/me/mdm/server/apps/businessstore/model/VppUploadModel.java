package com.me.mdm.server.apps.businessstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VppUploadModel extends BaseAPIModel
{
    @JsonProperty("vpp_token_file")
    private Long vppFileID;
    @JsonProperty("license_assign_type")
    private Integer licenseType;
    @JsonProperty("email_address")
    private String emailAddress;
    
    public Long getVppFileID() {
        return this.vppFileID;
    }
    
    public Integer getLicenseType() {
        return this.licenseType;
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
}
