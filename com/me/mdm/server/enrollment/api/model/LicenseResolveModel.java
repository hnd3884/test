package com.me.mdm.server.enrollment.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.me.mdm.api.model.BaseAPIModel;

public class LicenseResolveModel extends BaseAPIModel
{
    @JsonProperty("mobileDeviceIDs")
    private List<Long> mobileDeviceIDs;
    @JsonProperty("isListToBeManaged")
    private Boolean isListToBeManaged;
    
    public List<Long> getMobileDeviceIDs() {
        return this.mobileDeviceIDs;
    }
    
    public void setMobileDeviceIDs(final List<Long> mobileDeviceIDs) {
        this.mobileDeviceIDs = mobileDeviceIDs;
    }
    
    public Boolean getIsListToBeManaged() {
        return this.isListToBeManaged;
    }
    
    public void setIsListToBeManaged(final Boolean isListToBeManaged) {
        this.isListToBeManaged = isListToBeManaged;
    }
}
