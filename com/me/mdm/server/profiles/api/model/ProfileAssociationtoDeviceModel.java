package com.me.mdm.server.profiles.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileAssociationtoDeviceModel extends BaseAPIModel
{
    @JsonProperty("profile_ids")
    private List<Long> profileIds;
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("profile_id")
    private Long profileId;
    @JsonProperty("device_ids")
    private List<Long> deviceIds;
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
    
    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }
    
    public List<Long> getProfileIds() {
        return this.profileIds;
    }
    
    public void setProfileIds(final List<Long> profileIds) {
        this.profileIds = profileIds;
    }
    
    public List<Long> getDeviceIds() {
        return this.deviceIds;
    }
    
    public void setDeviceIds(final List<Long> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
