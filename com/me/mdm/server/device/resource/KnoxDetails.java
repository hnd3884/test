package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;

public class KnoxDetails
{
    @SerializedName("container_resource_id")
    private Long containerResourceID;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
    private Long deviceID;
    @SerializedName("container_id")
    private Integer containerID;
    @SerializedName("container_status")
    private Integer containerStatus;
    @SerializedName("container_last_updated_time")
    private Long containerLastUpdatedTime;
    @SerializedName("container_remarks")
    private String containerRemarks;
    @SerializedName("knox_version")
    private Integer knoxVersion;
    @SerializedName("container_state")
    private Integer containerState;
    
    public KnoxDetails() {
        this.containerStatus = -1;
        this.containerLastUpdatedTime = -1L;
        this.containerRemarks = "";
        this.containerState = -1;
    }
    
    public Long getContainerResourceID() {
        return this.containerResourceID;
    }
    
    public void setContainerResourceID(final Long containerResourceID) {
        this.containerResourceID = containerResourceID;
    }
    
    public Long getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final Long deviceID) {
        this.deviceID = deviceID;
    }
    
    public Integer getContainerID() {
        return this.containerID;
    }
    
    public void setContainerID(final Integer containerID) {
        this.containerID = containerID;
    }
    
    public Integer getContainerStatus() {
        return this.containerStatus;
    }
    
    public void setContainerStatus(final Integer containerStatus) {
        this.containerStatus = containerStatus;
    }
    
    public Long getContainerLastUpdatedTime() {
        return this.containerLastUpdatedTime;
    }
    
    public void setContainerLastUpdatedTime(final Long containerLastUpdatedTime) {
        this.containerLastUpdatedTime = containerLastUpdatedTime;
    }
    
    public String getContainerRemarks() {
        return this.containerRemarks;
    }
    
    public void setContainerRemarks(final String containerRemarks) {
        this.containerRemarks = containerRemarks;
    }
    
    public Integer getKnoxVersion() {
        return this.knoxVersion;
    }
    
    public void setKnoxVersion(final Integer knoxVersion) {
        this.knoxVersion = knoxVersion;
    }
    
    public Integer getContainerState() {
        return this.containerState;
    }
    
    public void setContainerState(final Integer containerState) {
        this.containerState = containerState;
    }
}
