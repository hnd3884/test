package com.me.mdm.directory.api.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.paging.model.Pagination;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuthMetaInputModel extends Pagination
{
    @JsonProperty("oauth_metadata_id")
    private Long oauthMetaID;
    @JsonProperty("oauth_client_id")
    private String clientId;
    @JsonProperty("oauth_type")
    private Integer oAuthType;
    @JsonProperty("oauth_client_secret")
    private String clientSecret;
    
    public Long getOauthMetaID() {
        return this.oauthMetaID;
    }
    
    public void setOauthMetaID(final Long oauthMetaID) {
        this.oauthMetaID = oauthMetaID;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public Integer getoAuthType() {
        return this.oAuthType;
    }
    
    public void setoAuthType(final Integer oAuthDomainType) {
        this.oAuthType = oAuthDomainType;
    }
    
    public String getClientSecret() {
        return this.clientSecret;
    }
    
    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
