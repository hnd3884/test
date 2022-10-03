package com.sun.security.sasl;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.SaslException;
import com.sun.security.sasl.util.PolicyUtils;
import javax.security.sasl.SaslClient;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.SaslClientFactory;

public final class ClientFactoryImpl implements SaslClientFactory
{
    private static final String[] myMechs;
    private static final int[] mechPolicies;
    private static final int EXTERNAL = 0;
    private static final int CRAMMD5 = 1;
    private static final int PLAIN = 2;
    
    @Override
    public SaslClient createSaslClient(final String[] array, final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(ClientFactoryImpl.myMechs[0]) && PolicyUtils.checkPolicy(ClientFactoryImpl.mechPolicies[0], map)) {
                return new ExternalClient(s);
            }
            if (array[i].equals(ClientFactoryImpl.myMechs[1]) && PolicyUtils.checkPolicy(ClientFactoryImpl.mechPolicies[1], map)) {
                final Object[] userInfo = this.getUserInfo("CRAM-MD5", s, callbackHandler);
                return new CramMD5Client((String)userInfo[0], (byte[])userInfo[1]);
            }
            if (array[i].equals(ClientFactoryImpl.myMechs[2]) && PolicyUtils.checkPolicy(ClientFactoryImpl.mechPolicies[2], map)) {
                final Object[] userInfo2 = this.getUserInfo("PLAIN", s, callbackHandler);
                return new PlainClient(s, (String)userInfo2[0], (byte[])userInfo2[1]);
            }
        }
        return null;
    }
    
    @Override
    public String[] getMechanismNames(final Map<String, ?> map) {
        return PolicyUtils.filterMechs(ClientFactoryImpl.myMechs, ClientFactoryImpl.mechPolicies, map);
    }
    
    private Object[] getUserInfo(final String s, final String s2, final CallbackHandler callbackHandler) throws SaslException {
        if (callbackHandler == null) {
            throw new SaslException("Callback handler to get username/password required");
        }
        try {
            final String string = s + " authentication id: ";
            final String string2 = s + " password: ";
            final NameCallback nameCallback = (s2 == null) ? new NameCallback(string) : new NameCallback(string, s2);
            final PasswordCallback passwordCallback = new PasswordCallback(string2, false);
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
            final char[] password = passwordCallback.getPassword();
            byte[] bytes;
            if (password != null) {
                bytes = new String(password).getBytes("UTF8");
                passwordCallback.clearPassword();
            }
            else {
                bytes = null;
            }
            return new Object[] { nameCallback.getName(), bytes };
        }
        catch (final IOException ex) {
            throw new SaslException("Cannot get password", ex);
        }
        catch (final UnsupportedCallbackException ex2) {
            throw new SaslException("Cannot get userid/password", ex2);
        }
    }
    
    static {
        myMechs = new String[] { "EXTERNAL", "CRAM-MD5", "PLAIN" };
        mechPolicies = new int[] { 7, 17, 16 };
    }
}
