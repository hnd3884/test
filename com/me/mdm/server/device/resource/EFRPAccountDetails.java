package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EFRPAccountDetails
{
    @SerializedName("emailids")
    private List<String> emailIDs;
    @SerializedName("addedby")
    private String addedBy;
    @SerializedName("profilename")
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
