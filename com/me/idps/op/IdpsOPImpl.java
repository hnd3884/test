package com.me.idps.op;

import com.me.idps.op.oauth.AzureOauthOPImpl;
import com.me.idps.core.oauth.OauthServiceAPI;
import com.me.idps.core.factory.IdpsProdEnvAPI;
import com.me.idps.core.factory.IdpsRegAPI;

public interface IdpsOPImpl extends IdpsRegAPI
{
    default Class<? extends IdpsProdEnvAPI> getIdpsProdEnvAPI() {
        return IdpsOPEnvImpl.class;
    }
    
    default Class<? extends OauthServiceAPI> getAzureOauthImpl() {
        return AzureOauthOPImpl.class;
    }
}
