package com.me.mdm.server.device.api.model.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilevaultKeyImportModel extends BaseAPIModel
{
    @JsonProperty("existing_personal_key")
    private String existingPersonalKey;
    private Long resourceID;
    
    public Long getResourceID() {
        return this.resourceID;
    }
    
    public void setResourceID(final Long resourceID) {
        this.resourceID = resourceID;
    }
    
    public String getExistingPersonalKey() {
        return this.existingPersonalKey;
    }
    
    public void setExistingPersonalKey(final String existingPersonalKey) {
        this.existingPersonalKey = existingPersonalKey;
    }
}
