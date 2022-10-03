package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.NTLMException;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import com.sun.security.ntlm.Server;
import java.util.Random;
import javax.security.sasl.SaslServer;

final class NTLMServer implements SaslServer
{
    private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
    private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
    private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
    private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
    private final Random random;
    private final Server server;
    private byte[] nonce;
    private int step;
    private String authzId;
    private final String mech;
    private String hostname;
    private String target;
    
    NTLMServer(final String mech, final String s, final String s2, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        this.step = 0;
        this.mech = mech;
        String property = null;
        String s3 = null;
        Random random = null;
        if (map != null) {
            s3 = (String)map.get("com.sun.security.sasl.ntlm.domain");
            property = (String)map.get("com.sun.security.sasl.ntlm.version");
            random = (Random)map.get("com.sun.security.sasl.ntlm.random");
        }
        this.random = ((random != null) ? random : new Random());
        if (property == null) {
            property = System.getProperty("ntlm.version");
        }
        if (s3 == null) {
            s3 = s2;
        }
        if (s3 == null) {
            throw new SaslException("Domain must be provided as the serverName argument or in props");
        }
        try {
            this.server = new Server(property, s3) {
                @Override
                public char[] getPassword(final String s, final String s2) {
                    try {
                        final RealmCallback realmCallback = (s == null || s.isEmpty()) ? new RealmCallback("Domain: ") : new RealmCallback("Domain: ", s);
                        final NameCallback nameCallback = new NameCallback("Name: ", s2);
                        final PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
                        callbackHandler.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
                        final char[] password = passwordCallback.getPassword();
                        passwordCallback.clearPassword();
                        return password;
                    }
                    catch (final IOException ex) {
                        return null;
                    }
                    catch (final UnsupportedCallbackException ex2) {
                        return null;
                    }
                }
            };
        }
        catch (final NTLMException ex) {
            throw new SaslException("NTLM: server creation failure", ex);
        }
        this.nonce = new byte[8];
    }
    
    @Override
    public String getMechanismName() {
        return this.mech;
    }
    
    @Override
    public byte[] evaluateResponse(final byte[] array) throws SaslException {
        try {
            ++this.step;
            if (this.step == 1) {
                this.random.nextBytes(this.nonce);
                return this.server.type2(array, this.nonce);
            }
            final String[] verify = this.server.verify(array, this.nonce);
            this.authzId = verify[0];
            this.hostname = verify[1];
            this.target = verify[2];
            return null;
        }
        catch (final NTLMException ex) {
            throw new SaslException("NTLM: generate response failure", ex);
        }
    }
    
    @Override
    public boolean isComplete() {
        return this.step >= 2;
    }
    
    @Override
    public String getAuthorizationID() {
        if (!this.isComplete()) {
            throw new IllegalStateException("authentication not complete");
        }
        return this.authzId;
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        throw new IllegalStateException("Not supported yet.");
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        throw new IllegalStateException("Not supported yet.");
    }
    
    @Override
    public Object getNegotiatedProperty(final String s) {
        if (!this.isComplete()) {
            throw new IllegalStateException("authentication not complete");
        }
        switch (s) {
            case "javax.security.sasl.qop": {
                return "auth";
            }
            case "javax.security.sasl.bound.server.name": {
                return this.target;
            }
            case "com.sun.security.sasl.ntlm.hostname": {
                return this.hostname;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void dispose() throws SaslException {
    }
}
