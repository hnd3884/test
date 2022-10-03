package com.me.devicemanagement.framework.server.admin;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;

public class AbstractAuthenticationKeyHandler extends AuthenticationKeyHandler
{
    public static AuthenticationKeyHandler getInstance() {
        return new AbstractAuthenticationKeyHandler();
    }
    
    @Override
    protected String getDecryptedAuthToken(final String key, final Integer servericeType) throws SyMException {
        throw new SyMException(14001, "Auth Token cannot be Redistributed", null);
    }
    
    @Override
    protected String getEncryptedAuthToken(final String key, final Integer serviceType) {
        return ApiFactoryProvider.getAuthUtilAccessAPI().getEncryptedPassword(key);
    }
}
