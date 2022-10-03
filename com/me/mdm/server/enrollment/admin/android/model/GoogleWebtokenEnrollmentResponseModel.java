package com.me.mdm.server.enrollment.admin.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleWebtokenEnrollmentResponseModel
{
    @JsonProperty("web_token")
    private String webtoken;
    @JsonProperty("dpc_id")
    private String dpcId;
    @JsonProperty("dpc_extras")
    private String dpcExtras;
    
    public String getWebtoken() {
        return this.webtoken;
    }
    
    public void setWebtoken(final String webtoken) {
        this.webtoken = webtoken;
    }
    
    public String getDpcId() {
        return this.dpcId;
    }
    
    public void setDpcId(final String dcpId) {
        this.dpcId = dcpId;
    }
    
    public String getDpcExtras() {
        return this.dpcExtras;
    }
    
    public void setDpcExtras(final String dpcExtras) {
        this.dpcExtras = dpcExtras;
    }
}
