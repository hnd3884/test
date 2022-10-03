package com.me.mdm.directory.api.oauth.model;

import java.util.List;

public class OAuthMetaInfoListModel
{
    private List<OAuthMetaInfoModel> oauthMetaInfos;
    
    public List<OAuthMetaInfoModel> getOauthMetaInfos() {
        return this.oauthMetaInfos;
    }
    
    public void setOauthMetaInfos(final List<OAuthMetaInfoModel> oauthMetaInfos) {
        this.oauthMetaInfos = oauthMetaInfos;
    }
}
