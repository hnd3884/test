package com.me.idps.core.factory;

import com.me.idps.core.crud.DMDomainListener;
import java.util.ArrayList;
import com.me.idps.core.sync.product.DirectoryProductAPI;
import com.me.idps.core.oauth.OauthServiceAPI;
import com.me.devicemanagement.framework.server.customer.CustomerListener;
import com.me.idps.core.IdpsCustomerListener;
import com.me.devicemanagement.framework.server.customer.CustomerHandler;

public interface IdpsRegAPI
{
    default void register() {
        IdpsImplRegistrar.getInstance().register(this);
        CustomerHandler.getInstance().addCustomerListener((CustomerListener)new IdpsCustomerListener());
        final IdpsProdEnvAPI idpProdEnvAPI = IdpsFactoryProvider.getIdpsProdEnvAPI();
        if (idpProdEnvAPI != null) {
            idpProdEnvAPI.handleServerStartup();
        }
    }
    
    default Class<? extends IdpsAccessAPI> getZDimpl() {
        return null;
    }
    
    default Class getGsuiteImpl() {
        return null;
    }
    
    default Class<? extends IdpsAccessAPI> getNativeOPImpl() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getAzureOauthImpl() {
        return null;
    }
    
    default Class<? extends IdpsProdEnvAPI> getIdpsProdEnvAPI() {
        return null;
    }
    
    default Class<? extends DirectoryProductAPI> getIdpsProductImpl() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getAzureMamOauthImpl() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getAssistOauthImpl() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getCsezZdOauthImpl() {
        return null;
    }
    
    default ArrayList<DMDomainListener> getDMDomainListener() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getMigrationToolOauthChinaImpl() {
        return null;
    }
    
    default Class<? extends OauthServiceAPI> getMigrationToolOauthOtherImpl() {
        return null;
    }
}
