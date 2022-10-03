package com.sun.mail.auth;

import java.security.Provider;
import java.security.Security;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslClient;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslClientFactory;

public class OAuth2SaslClientFactory implements SaslClientFactory
{
    private static final String PROVIDER_NAME = "JavaMail-OAuth2";
    private static final String MECHANISM_NAME = "SaslClientFactory.XOAUTH2";
    
    @Override
    public SaslClient createSaslClient(final String[] mechanisms, final String authorizationId, final String protocol, final String serverName, final Map<String, ?> props, final CallbackHandler cbh) throws SaslException {
        for (final String m : mechanisms) {
            if (m.equals("XOAUTH2")) {
                return new OAuth2SaslClient(props, cbh);
            }
        }
        return null;
    }
    
    @Override
    public String[] getMechanismNames(final Map<String, ?> props) {
        return new String[] { "XOAUTH2" };
    }
    
    public static void init() {
        try {
            if (Security.getProvider("JavaMail-OAuth2") == null) {
                Security.addProvider(new OAuth2Provider());
            }
        }
        catch (final SecurityException ex) {}
    }
    
    static class OAuth2Provider extends Provider
    {
        private static final long serialVersionUID = -5371795551562287059L;
        
        public OAuth2Provider() {
            super("JavaMail-OAuth2", 1.0, "XOAUTH2 SASL Mechanism");
            this.put("SaslClientFactory.XOAUTH2", OAuth2SaslClientFactory.class.getName());
        }
    }
}
