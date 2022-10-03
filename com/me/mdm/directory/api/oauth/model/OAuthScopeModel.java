package com.me.mdm.directory.api.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthScopeModel
{
    @JsonProperty("oauth_scope_id")
    private Long oAuthScopeId;
    @JsonProperty("scope")
    private String scope;
    
    public Long getoAuthScopeId() {
        return this.oAuthScopeId;
    }
    
    public void setoAuthScopeId(final Long oAuthScopeId) {
        this.oAuthScopeId = oAuthScopeId;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
}
