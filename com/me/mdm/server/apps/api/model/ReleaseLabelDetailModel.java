package com.me.mdm.server.apps.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReleaseLabelDetailModel
{
    @JsonProperty("release_label_name")
    private String releaseLabelName;
    @JsonProperty("release_label_id")
    private Long releaseLabelId;
    
    public String getReleaseLabelName() {
        return this.releaseLabelName;
    }
    
    public void setReleaseLabelName(final String releaseLabelName) {
        this.releaseLabelName = releaseLabelName;
    }
    
    public Long getReleaseLabelId() {
        return this.releaseLabelId;
    }
    
    public void setReleaseLabelId(final Long releaseLabelId) {
        this.releaseLabelId = releaseLabelId;
    }
}
