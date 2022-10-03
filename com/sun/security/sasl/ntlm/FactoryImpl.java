package com.sun.security.sasl.ntlm;

import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslException;
import com.sun.security.sasl.util.PolicyUtils;
import javax.security.sasl.SaslClient;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslServerFactory;
import javax.security.sasl.SaslClientFactory;

public final class FactoryImpl implements SaslClientFactory, SaslServerFactory
{
    private static final String[] myMechs;
    private static final int[] mechPolicies;
    
    @Override
    public SaslClient createSaslClient(final String[] array, final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        int i = 0;
        while (i < array.length) {
            if (array[i].equals("NTLM") && PolicyUtils.checkPolicy(FactoryImpl.mechPolicies[0], map)) {
                if (callbackHandler == null) {
                    throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
                }
                return new NTLMClient(array[i], s, s2, s3, map, callbackHandler);
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    @Override
    public SaslServer createSaslServer(final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        if (!s.equals("NTLM") || !PolicyUtils.checkPolicy(FactoryImpl.mechPolicies[0], map)) {
            return null;
        }
        if (map != null) {
            final String s4 = (String)map.get("javax.security.sasl.qop");
            if (s4 != null && !s4.equals("auth")) {
                throw new SaslException("NTLM only support auth");
            }
        }
        if (callbackHandler == null) {
            throw new SaslException("Callback handler with support for RealmCallback, NameCallback, and PasswordCallback required");
        }
        return new NTLMServer(s, s2, s3, map, callbackHandler);
    }
    
    @Override
    public String[] getMechanismNames(final Map<String, ?> map) {
        return PolicyUtils.filterMechs(FactoryImpl.myMechs, FactoryImpl.mechPolicies, map);
    }
    
    static {
        myMechs = new String[] { "NTLM" };
        mechPolicies = new int[] { 17 };
    }
}
