package javax.security.auth.message.config;

import javax.security.auth.message.AuthException;
import javax.security.auth.callback.CallbackHandler;

public interface AuthConfigProvider
{
    ClientAuthConfig getClientAuthConfig(final String p0, final String p1, final CallbackHandler p2) throws AuthException;
    
    ServerAuthConfig getServerAuthConfig(final String p0, final String p1, final CallbackHandler p2) throws AuthException;
    
    void refresh();
}
