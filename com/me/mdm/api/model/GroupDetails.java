package com.me.mdm.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDetails
{
    @JsonProperty("group_id")
    private Long groupID;
    @JsonProperty("group_type")
    private int groupType;
    @JsonProperty("member_count")
    private int memberCount;
    @JsonProperty("name")
    private String name;
    
    public int getGroupType() {
        return this.groupType;
    }
    
    public Long getGroupID() {
        return this.groupID;
    }
    
    public int getMemberCount() {
        return this.memberCount;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setGroupID(final Long groupID) {
        this.groupID = groupID;
    }
    
    public void setGroupType(final int groupType) {
        this.groupType = groupType;
    }
    
    public void setMemberCount(final int memberCount) {
        this.memberCount = memberCount;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
