package com.me.mdm.server.device.api.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = false)
public class MspDeviceMigrationModel extends BaseAPIModel
{
    @JsonProperty("new_customer_id")
    private long newCustomerId;
    @JsonProperty("device_ids")
    private ArrayList<Long> deviceIdList;
    @JsonProperty("force_migrate")
    private boolean forceMigrate;
    
    public void setNewCustomerId(final long newCustomerId) {
        this.newCustomerId = newCustomerId;
    }
    
    public void setDeviceIdList(final ArrayList<Long> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }
    
    public void setForceMigrate(final boolean forceMigrate) {
        this.forceMigrate = forceMigrate;
    }
    
    public long getNewCustomerId() {
        return this.newCustomerId;
    }
    
    public ArrayList<Long> getDeviceIdList() {
        return this.deviceIdList;
    }
    
    public boolean getForceMigrate() {
        return this.forceMigrate;
    }
}
