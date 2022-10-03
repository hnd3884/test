package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KnoxDetailsModel
{
    @JsonProperty("container_resource_id")
    private Long containerResourceID;
    @JsonAlias({ "RESOURCE_ID" })
    @JsonProperty("device_id")
    private Long deviceID;
    @JsonProperty("container_id")
    private Integer containerID;
    @JsonProperty("container_status")
    private Integer containerStatus;
    @JsonProperty("container_last_updated_time")
    private Long containerLastUpdatedTime;
    @JsonProperty("container_remarks")
    private String containerRemarks;
    @JsonProperty("knox_version")
    private Integer knoxVersion;
    @JsonProperty("container_state")
    private Integer containerState;
    @JsonProperty("knox_api_level")
    private Integer knoxAPILevel;
    
    public KnoxDetailsModel() {
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
    
    public Integer getKnoxAPILevel() {
        return this.knoxAPILevel;
    }
    
    public void setKnoxAPILevel(final Integer knoxAPILevel) {
        this.knoxAPILevel = knoxAPILevel;
    }
}
