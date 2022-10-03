package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationErrorModel
{
    @JsonProperty("error_code")
    private Integer errorCode;
    @JsonProperty("short_desc")
    private String shortDesc;
    @JsonProperty("detailed_desc")
    private String detailedDesc;
    @JsonProperty("kb_url")
    private String kbUrl;
    
    public Integer getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final Integer errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getShortDesc() {
        return this.shortDesc;
    }
    
    public void setShortDesc(final String shortDesc) {
        this.shortDesc = shortDesc;
    }
    
    public String getDetailedDesc() {
        return this.detailedDesc;
    }
    
    public void setDetailedDesc(final String detailedDesc) {
        this.detailedDesc = detailedDesc;
    }
    
    public String getKbUrl() {
        return this.kbUrl;
    }
    
    public void setKbUrl(final String kbUrl) {
        this.kbUrl = kbUrl;
    }
}
