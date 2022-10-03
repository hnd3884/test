package com.me.mdm.server.apps.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppDetailModel
{
    @JsonProperty("app_id")
    private String app_id;
    @JsonProperty("release_label_id")
    private String release_label_id;
    
    public String getAppId() {
        return this.app_id;
    }
    
    public void setAppId(final String app_id) {
        this.app_id = app_id;
    }
    
    public String getReleaseLabelId() {
        return this.release_label_id;
    }
    
    public void setReleaseLabelId(final String release_label_id) {
        this.release_label_id = release_label_id;
    }
}
