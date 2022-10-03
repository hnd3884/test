package com.me.mdm.server.apps.blocklist.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.paging.model.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlocklistPOJO extends Pagination
{
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("app_group_ids")
    private List<Long> appGroupIds;
    @JsonProperty("app_id")
    private Long appId;
    @JsonProperty("group_type")
    private String groupType;
    @JsonProperty("platform_type")
    private String platform;
    @JsonProperty("device_type")
    private ArrayList<Long> deviceType;
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public List<Long> getAppGroupIds() {
        return this.appGroupIds;
    }
    
    public void setAppGroupIds(final List<Long> appGroupIds) {
        this.appGroupIds = appGroupIds;
    }
    
    public void setGroupType(final String groupType) {
        this.groupType = groupType;
    }
    
    public String getGroupType() {
        return this.groupType;
    }
    
    public void setDeviceType(final ArrayList<Long> deviceType) {
        this.deviceType = deviceType;
    }
    
    public ArrayList<Long> getdeviceType() {
        return this.deviceType;
    }
    
    public void setAppId(final Long appId) {
        this.appId = appId;
    }
    
    public String getPlatform() {
        return this.platform;
    }
    
    public void setPlatform(final String platform) {
        this.platform = platform;
    }
}
