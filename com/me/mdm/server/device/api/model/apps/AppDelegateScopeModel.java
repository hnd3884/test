package com.me.mdm.server.device.api.model.apps;

import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppDelegateScopeModel extends BaseAPIModel
{
    @JsonProperty("delegate_scope_permission")
    private Map delegateScope;
    @JsonProperty("label_id")
    private Long labelId;
    @JsonProperty("app_id")
    private Long appId;
    private Long configDataItemId;
    
    public AppDelegateScopeModel() {
        this.delegateScope = new HashMap();
        this.configDataItemId = -1L;
    }
    
    public Long getLabelId() {
        return this.labelId;
    }
    
    public void setLabelId(final Long labelId) {
        this.labelId = labelId;
    }
    
    public Long getConfigDataItemId() {
        return this.configDataItemId;
    }
    
    public void setConfigDataItemId(final Long configDataItemId) {
        this.configDataItemId = configDataItemId;
    }
    
    public Long getAppId() {
        return this.appId;
    }
    
    public void setAppId(final Long appId) {
        this.appId = appId;
    }
    
    public Map getDelegateScope() {
        return this.delegateScope;
    }
    
    public void setDelegateScope(final Map delegateScope) {
        this.delegateScope = delegateScope;
    }
}
