package com.sun.security.sasl.digest;

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
    private static final int DIGEST_MD5 = 0;
    private static final int[] mechPolicies;
    
    @Override
    public SaslClient createSaslClient(final String[] array, final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        int i = 0;
        while (i < array.length) {
            if (array[i].equals(FactoryImpl.myMechs[0]) && PolicyUtils.checkPolicy(FactoryImpl.mechPolicies[0], map)) {
                if (callbackHandler == null) {
                    throw new SaslException("Callback handler with support for RealmChoiceCallback, RealmCallback, NameCallback, and PasswordCallback required");
                }
                return new DigestMD5Client(s, s2, s3, map, callbackHandler);
            }
            else {
                ++i;
            }
        }
        return null;
    }
    
    @Override
    public SaslServer createSaslServer(final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        if (!s.equals(FactoryImpl.myMechs[0]) || !PolicyUtils.checkPolicy(FactoryImpl.mechPolicies[0], map)) {
            return null;
        }
        if (callbackHandler == null) {
            throw new SaslException("Callback handler with support for AuthorizeCallback, RealmCallback, NameCallback, and PasswordCallback required");
        }
        return new DigestMD5Server(s2, s3, map, callbackHandler);
    }
    
    @Override
    public String[] getMechanismNames(final Map<String, ?> map) {
        return PolicyUtils.filterMechs(FactoryImpl.myMechs, FactoryImpl.mechPolicies, map);
    }
    
    static {
        myMechs = new String[] { "DIGEST-MD5" };
        mechPolicies = new int[] { 17 };
    }
}
