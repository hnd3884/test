package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceSummaryModel
{
    @JsonProperty(value = "app_count", defaultValue = "0")
    private Integer appCount;
    @JsonProperty(value = "profile_count", defaultValue = "0")
    private Integer profileCount;
    @JsonProperty(value = "doc_count", defaultValue = "0")
    private Integer docCount;
    @JsonProperty(value = "group_count", defaultValue = "0")
    private Integer groupCount;
    
    public Integer getAppCount() {
        return this.appCount;
    }
    
    public void setAppCount(final Integer appCount) {
        this.appCount = appCount;
    }
    
    public Integer getProfileCount() {
        return this.profileCount;
    }
    
    public void setProfileCount(final Integer profileCount) {
        this.profileCount = profileCount;
    }
    
    public Integer getDocCount() {
        return this.docCount;
    }
    
    public void setDocCount(final Integer docCount) {
        this.docCount = docCount;
    }
    
    public Integer getGroupCount() {
        return this.groupCount;
    }
    
    public void setGroupCount(final Integer groupCount) {
        this.groupCount = groupCount;
    }
}
