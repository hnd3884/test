package com.me.mdm.directory.api.oauth.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthMetaListModel
{
    private List<OAuthMetaModel> oauthMetas;
    
    public List<OAuthMetaModel> getOAuthMetaDetails() {
        return this.oauthMetas;
    }
    
    public void setOAuthMetas(final List<OAuthMetaModel> oauthMetas) {
        this.oauthMetas = oauthMetas;
    }
}
