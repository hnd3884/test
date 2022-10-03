package com.me.devicemanagement.framework.server.api;

import com.me.devicemanagement.framework.server.exception.SyMException;

public interface AuthenticationKeyHandlerAPI
{
    String getDecryptedAuthToken(final String p0, final Integer p1) throws SyMException;
    
    String getEncryptedAuthToken(final String p0, final Integer p1) throws SyMException;
}
