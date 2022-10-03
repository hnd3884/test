package com.me.mdm.directory.api.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthTokenMetaModel
{
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_at")
    private String expiresAt;
    
    public String getTokenType() {
        return this.tokenType;
    }
    
    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getExpiresAt() {
        return this.expiresAt;
    }
    
    public void setExpiresAt(final String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
