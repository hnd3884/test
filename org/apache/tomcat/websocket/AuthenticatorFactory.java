package org.apache.tomcat.websocket;

import java.util.Iterator;
import java.util.ServiceLoader;

public class AuthenticatorFactory
{
    public static Authenticator getAuthenticator(final String authScheme) {
        Authenticator auth = null;
        final String lowerCase = authScheme.toLowerCase();
        switch (lowerCase) {
            case "basic": {
                auth = new BasicAuthenticator();
                break;
            }
            case "digest": {
                auth = new DigestAuthenticator();
                break;
            }
            default: {
                auth = loadAuthenticators(authScheme);
                break;
            }
        }
        return auth;
    }
    
    private static Authenticator loadAuthenticators(final String authScheme) {
        final ServiceLoader<Authenticator> serviceLoader = ServiceLoader.load(Authenticator.class);
        for (final Authenticator auth : serviceLoader) {
            if (auth.getSchemeName().equalsIgnoreCase(authScheme)) {
                return auth;
            }
        }
        return null;
    }
}
