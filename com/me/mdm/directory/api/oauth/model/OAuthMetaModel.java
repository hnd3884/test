package com.me.mdm.directory.api.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthMetaModel
{
    @JsonProperty("oauth_metadata_id")
    private Long oAuthMetaId;
    @JsonProperty("oauth_type")
    private Integer oAuthType;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("tokens_list")
    private OAuthTokenListModel tokens;
    @JsonProperty("meta_info")
    private OAuthMetaInfoListModel metaInfoListModel;
    
    public Long getoAuthMetaId() {
        return this.oAuthMetaId;
    }
    
    public void setoAuthMetaId(final Long oAuthMetaId) {
        this.oAuthMetaId = oAuthMetaId;
    }
    
    public Integer getoAuthType() {
        return this.oAuthType;
    }
    
    public void setoAuthType(final Integer oAuthType) {
        this.oAuthType = oAuthType;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public OAuthTokenListModel getTokens() {
        return this.tokens;
    }
    
    public void setTokens(final OAuthTokenListModel tokens) {
        this.tokens = tokens;
    }
    
    public OAuthMetaInfoListModel getMetaInfoListModel() {
        return this.metaInfoListModel;
    }
    
    public void setMetaInfoListModel(final OAuthMetaInfoListModel metaInfoListModel) {
        this.metaInfoListModel = metaInfoListModel;
    }
}
