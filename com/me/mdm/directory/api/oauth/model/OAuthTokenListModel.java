package com.me.mdm.directory.api.oauth.model;

import java.util.List;

public class OAuthTokenListModel
{
    private List<OAuthTokenModel> oauthTokens;
    
    public List<OAuthTokenModel> getOauthTokens() {
        return this.oauthTokens;
    }
    
    public void setOauthTokens(final List<OAuthTokenModel> oauthTokens) {
        this.oauthTokens = oauthTokens;
    }
}
