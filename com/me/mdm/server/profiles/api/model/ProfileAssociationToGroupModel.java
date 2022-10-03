package com.me.mdm.server.profiles.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileAssociationToGroupModel extends BaseAPIModel
{
    @JsonAlias({ "app_update_policy_ids" })
    @JsonProperty("profile_ids")
    private List<Long> profileIds;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonAlias({ "app_update_policy_id" })
    @JsonProperty("profile_id")
    private Long profileId;
    @JsonProperty("group_ids")
    private List<Long> groupIds;
    
    public void setGroupId(final Long groupId) {
        this.groupId = groupId;
    }
    
    public Long getGroupId() {
        return this.groupId;
    }
    
    public void setGroupIds(final List<Long> groupIds) {
        this.groupIds = groupIds;
    }
    
    public List<Long> getGroupIds() {
        return this.groupIds;
    }
    
    public void setProfileIds(final List<Long> profileIds) {
        this.profileIds = profileIds;
    }
    
    public List<Long> getProfileIds() {
        return this.profileIds;
    }
    
    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
}
