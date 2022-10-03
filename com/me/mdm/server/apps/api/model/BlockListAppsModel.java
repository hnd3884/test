package com.me.mdm.server.apps.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockListAppsModel extends BaseAPIModel
{
    @JsonProperty("app_group_ids")
    private List<Long> appGroupIds;
    
    public List<Long> getAppGroupIds() {
        return this.appGroupIds;
    }
    
    public void setAppGroupIds(final List<Long> appGroupIds) {
        this.appGroupIds = appGroupIds;
    }
}
