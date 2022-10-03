package com.me.mdm.server.admin;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.apache.commons.codec.binary.Base64;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyHandler;

public class MDMAuthenticationKeyHandlerImpl extends AuthenticationKeyHandler
{
    public static AuthenticationKeyHandler getInstance() {
        return new MDMAuthenticationKeyHandlerImpl();
    }
    
    protected String getDecryptedAuthToken(final String key, final Integer servericeType) throws SyMException {
        switch (servericeType) {
            case 10:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
            case 32:
            case 33:
            case 40:
            case 50:
            case 201: {
                return new String(Base64.decodeBase64(key));
            }
            default: {
                throw new SyMException(14001, "Auth Token cannot be Redistributed", (Throwable)null);
            }
        }
    }
    
    protected String getEncryptedAuthToken(final String key, final Integer serviceType) {
        switch (serviceType) {
            case 10:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
            case 32:
            case 33:
            case 40:
            case 50:
            case 201: {
                return Base64.encodeBase64String(key.getBytes());
            }
            default: {
                return ApiFactoryProvider.getAuthUtilAccessAPI().getEncryptedPassword(key);
            }
        }
    }
}
