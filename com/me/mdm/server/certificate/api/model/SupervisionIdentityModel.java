package com.me.mdm.server.certificate.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupervisionIdentityModel
{
    @JsonProperty("cert_id")
    private Long certificateID;
    @JsonProperty("cert_created_at")
    private String certNotBeforeTime;
    @JsonProperty("cert_password")
    private String certPassword;
    @JsonProperty("reason")
    private String reason;
    @JsonProperty("file_contents")
    private String fileContents;
    
    public Long getCertificateID() {
        return this.certificateID;
    }
    
    public void setCertificateID(final Long certificateID) {
        this.certificateID = certificateID;
    }
    
    public String getCertNotBeforeTime() {
        return this.certNotBeforeTime;
    }
    
    public void setCertNotBeforeTime(final String certNotBeforeTime) {
        this.certNotBeforeTime = certNotBeforeTime;
    }
    
    public String getCertPassword() {
        return this.certPassword;
    }
    
    public void setCertPassword(final String certPassword) {
        this.certPassword = certPassword;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public void setFileContents(final String content) {
        this.fileContents = content;
    }
}
