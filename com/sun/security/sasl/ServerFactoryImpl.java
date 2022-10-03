package com.sun.security.sasl;

import javax.security.sasl.SaslException;
import com.sun.security.sasl.util.PolicyUtils;
import javax.security.sasl.SaslServer;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslServerFactory;

public final class ServerFactoryImpl implements SaslServerFactory
{
    private static final String[] myMechs;
    private static final int[] mechPolicies;
    private static final int CRAMMD5 = 0;
    
    @Override
    public SaslServer createSaslServer(final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        if (!s.equals(ServerFactoryImpl.myMechs[0]) || !PolicyUtils.checkPolicy(ServerFactoryImpl.mechPolicies[0], map)) {
            return null;
        }
        if (callbackHandler == null) {
            throw new SaslException("Callback handler with support for AuthorizeCallback required");
        }
        return new CramMD5Server(s2, s3, map, callbackHandler);
    }
    
    @Override
    public String[] getMechanismNames(final Map<String, ?> map) {
        return PolicyUtils.filterMechs(ServerFactoryImpl.myMechs, ServerFactoryImpl.mechPolicies, map);
    }
    
    static {
        myMechs = new String[] { "CRAM-MD5" };
        mechPolicies = new int[] { 17 };
    }
}
