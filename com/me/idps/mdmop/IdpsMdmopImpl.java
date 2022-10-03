package com.me.idps.mdmop;

import com.me.idps.core.sync.product.DirectoryProductAPI;
import com.me.idps.mdmop.oauth.AssistOauthService;
import com.me.idps.core.oauth.OauthServiceAPI;
import com.me.idps.core.factory.IdpsAccessAPI;
import com.me.idps.op.IdpsOPImpl;
import com.me.idps.mdm.IdpsMdmImpl;

public class IdpsMdmopImpl extends IdpsMdmImpl implements IdpsOPImpl
{
    private static IdpsMdmopImpl idpsMDMOPimpl;
    
    protected IdpsMdmopImpl() {
    }
    
    public static IdpsMdmopImpl getInstance() {
        if (IdpsMdmopImpl.idpsMDMOPimpl == null) {
            IdpsMdmopImpl.idpsMDMOPimpl = new IdpsMdmopImpl();
        }
        return IdpsMdmopImpl.idpsMDMOPimpl;
    }
    
    @Override
    public Class<? extends IdpsAccessAPI> getNativeOPImpl() {
        return OnpremiseADAccessProvider.class;
    }
    
    @Override
    public Class<? extends OauthServiceAPI> getAssistOauthImpl() {
        return AssistOauthService.class;
    }
    
    @Override
    public Class<? extends DirectoryProductAPI> getIdpsProductImpl() {
        return (Class<? extends DirectoryProductAPI>)MDMOpIdpsBaseImpl.class;
    }
    
    static {
        IdpsMdmopImpl.idpsMDMOPimpl = null;
    }
}
