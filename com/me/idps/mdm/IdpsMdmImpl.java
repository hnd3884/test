package com.me.idps.mdm;

import com.me.idps.mdm.oauth.AzureMamOauthImpl;
import com.me.idps.core.oauth.OauthServiceAPI;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleUsersDirectory;
import com.me.idps.core.crud.IdpsRegApiImpl;

public abstract class IdpsMdmImpl extends IdpsRegApiImpl
{
    @Override
    public Class getGsuiteImpl() {
        return GoogleUsersDirectory.class;
    }
    
    @Override
    public Class<? extends OauthServiceAPI> getAzureMamOauthImpl() {
        return AzureMamOauthImpl.class;
    }
}
