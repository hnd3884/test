package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EFRPAccountDetailModel
{
    @JsonProperty("emailIds")
    private List<String> emailIDs;
    @JsonProperty("addedBy")
    private String addedBy;
    @JsonProperty("profileName")
    private String profileName;
    
    public List<String> getEmailIDs() {
        return this.emailIDs;
    }
    
    public void setEmailIDs(final List<String> emailIDs) {
        this.emailIDs = emailIDs;
    }
    
    public String getAddedBy() {
        return this.addedBy;
    }
    
    public void setAddedBy(final String addedBy) {
        this.addedBy = addedBy;
    }
    
    public String getProfileName() {
        return this.profileName;
    }
    
    public void setProfileName(final String profileName) {
        this.profileName = profileName;
    }
}
