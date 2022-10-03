package com.me.mdm.directory.api.oauth.model;

import com.me.mdm.api.paging.annotations.AllCustomerSearchParam;
import com.me.mdm.api.paging.annotations.SearchParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.paging.model.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchOAuth extends Pagination
{
    @JsonProperty("oauth_meta_id")
    @SearchParam(value = "oauth_meta_id", tableName = "OauthMetadata", columnName = "OAUTH_METADATA_ID")
    private Long oauthMetaID;
    @JsonProperty("oauth_token_id")
    @SearchParam(value = "oauth_token_id", tableName = "OauthTokens", columnName = "OAUTH_TOKEN_ID")
    private Long oauthTokenID;
    @JsonProperty("oauth_type")
    @SearchParam(value = "oauth_type", tableName = "OauthMetadata", columnName = "DOMAIN_TYPE")
    private Integer oauthType;
    @JsonProperty("client_id")
    @SearchParam(value = "client_id", tableName = "OauthMetadata", columnName = "OAUTH_CLIENT_ID")
    private String oauthClientID;
    @JsonProperty("customer_ids")
    @AllCustomerSearchParam(tableName = "OAuthMetaPurposeRel", columnName = "CUSTOMER_ID")
    private String customerIds;
    
    public void setOAUthMetaID(final Long oauthMetaID) {
        this.oauthMetaID = oauthMetaID;
    }
    
    public Long getOAUthMetaID() {
        return this.oauthMetaID;
    }
    
    public void setOAUthTokenID(final Long oauthTokenID) {
        this.oauthTokenID = oauthTokenID;
    }
    
    public Long getOAUthTokenID() {
        return this.oauthTokenID;
    }
    
    public void setOAUthType(final Integer oauthType) {
        this.oauthType = oauthType;
    }
    
    public Integer getOAUthType() {
        return this.oauthType;
    }
    
    public void setOAUthClientID(final String oauthClientID) {
        this.oauthClientID = oauthClientID;
    }
    
    public String getOAUthClientID() {
        return this.oauthClientID;
    }
    
    public String getCustomerIds() {
        return this.customerIds;
    }
    
    public void setCustomerIds(final String customerIds) {
        this.customerIds = customerIds;
    }
}
