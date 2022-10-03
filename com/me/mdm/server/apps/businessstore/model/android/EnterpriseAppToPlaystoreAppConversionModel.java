package com.me.mdm.server.apps.businessstore.model.android;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnterpriseAppToPlaystoreAppConversionModel extends BaseAPIModel
{
    @JsonProperty("app_ids")
    private List<Long> appIds;
    
    public List<Long> getAppIds() {
        return this.appIds;
    }
    
    public void setAppIds(final List<Long> appIds) {
        this.appIds = appIds;
    }
}
