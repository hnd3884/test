package com.me.mdm.directory.api.oauth.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthTokenModel
{
    @JsonProperty("added_at")
    private String addedAt;
    @JsonProperty("oauth_token_id")
    private Long oAuthTokenId;
    @JsonProperty("reference_user")
    private String referenceUser;
    @JsonProperty("scopes")
    private List<OAuthScopeModel> oauthScopes;
    @JsonProperty("tokenMetaDetails")
    private List<OAuthTokenMetaModel> oAuthTokenMetaModels;
    
    public Long getOAuthTokenId() {
        return this.oAuthTokenId;
    }
    
    public void setOAuthTokenId(final Long oAuthTokenId) {
        this.oAuthTokenId = oAuthTokenId;
    }
    
    public String getReferenceUser() {
        return this.referenceUser;
    }
    
    public void setReferenceUser(final String referenceUser) {
        this.referenceUser = referenceUser;
    }
    
    public String getAddedAt() {
        return this.addedAt;
    }
    
    public void setAddedAt(final String addedAt) {
        this.addedAt = addedAt;
    }
    
    public List<OAuthScopeModel> getoauthScopes() {
        return this.oauthScopes;
    }
    
    public void setoauthScopes(final List<OAuthScopeModel> oauthScopes) {
        this.oauthScopes = oauthScopes;
    }
    
    public List<OAuthTokenMetaModel> getoAuthTokenMetaModels() {
        return this.oAuthTokenMetaModels;
    }
    
    public void setoAuthTokenMetaModel(final List<OAuthTokenMetaModel> oAuthTokenMetaModels) {
        this.oAuthTokenMetaModels = oAuthTokenMetaModels;
    }
}
