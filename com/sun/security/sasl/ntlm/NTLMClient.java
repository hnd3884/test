package com.sun.security.sasl.ntlm;

import com.sun.security.ntlm.NTLMException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import java.util.Random;
import com.sun.security.ntlm.Client;
import javax.security.sasl.SaslClient;

final class NTLMClient implements SaslClient
{
    private static final String NTLM_VERSION = "com.sun.security.sasl.ntlm.version";
    private static final String NTLM_RANDOM = "com.sun.security.sasl.ntlm.random";
    private static final String NTLM_DOMAIN = "com.sun.security.sasl.ntlm.domain";
    private static final String NTLM_HOSTNAME = "com.sun.security.sasl.ntlm.hostname";
    private final Client client;
    private final String mech;
    private final Random random;
    private int step;
    
    NTLMClient(final String mech, final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        this.step = 0;
        this.mech = mech;
        String property = null;
        Random random = null;
        String canonicalHostName = null;
        if (map != null) {
            final String s4 = (String)map.get("javax.security.sasl.qop");
            if (s4 != null && !s4.equals("auth")) {
                throw new SaslException("NTLM only support auth");
            }
            property = (String)map.get("com.sun.security.sasl.ntlm.version");
            random = (Random)map.get("com.sun.security.sasl.ntlm.random");
            canonicalHostName = (String)map.get("com.sun.security.sasl.ntlm.hostname");
        }
        this.random = ((random != null) ? random : new Random());
        if (property == null) {
            property = System.getProperty("ntlm.version");
        }
        final RealmCallback realmCallback = (s3 != null && !s3.isEmpty()) ? new RealmCallback("Realm: ", s3) : new RealmCallback("Realm: ");
        final NameCallback nameCallback = (s != null && !s.isEmpty()) ? new NameCallback("User name: ", s) : new NameCallback("User name: ");
        final PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
        try {
            callbackHandler.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
        }
        catch (final UnsupportedCallbackException ex) {
            throw new SaslException("NTLM: Cannot perform callback to acquire realm, username or password", ex);
        }
        catch (final IOException ex2) {
            throw new SaslException("NTLM: Error acquiring realm, username or password", ex2);
        }
        if (canonicalHostName == null) {
            try {
                canonicalHostName = InetAddress.getLocalHost().getCanonicalHostName();
            }
            catch (final UnknownHostException ex3) {
                canonicalHostName = "localhost";
            }
        }
        try {
            String name = nameCallback.getName();
            if (name == null) {
                name = s;
            }
            String text = realmCallback.getText();
            if (text == null) {
                text = s3;
            }
            this.client = new Client(property, canonicalHostName, name, text, passwordCallback.getPassword());
        }
        catch (final NTLMException ex4) {
            throw new SaslException("NTLM: client creation failure", ex4);
        }
    }
    
    @Override
    public String getMechanismName() {
        return this.mech;
    }
    
    @Override
    public boolean isComplete() {
        return this.step >= 2;
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        throw new IllegalStateException("Not supported.");
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        throw new IllegalStateException("Not supported.");
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
            case "com.sun.security.sasl.ntlm.domain": {
                return this.client.getDomain();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void dispose() throws SaslException {
        this.client.dispose();
    }
    
    @Override
    public boolean hasInitialResponse() {
        return true;
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        ++this.step;
        if (this.step == 1) {
            return this.client.type1();
        }
        try {
            final byte[] array2 = new byte[8];
            this.random.nextBytes(array2);
            return this.client.type3(array, array2);
        }
        catch (final NTLMException ex) {
            throw new SaslException("Type3 creation failed", ex);
        }
    }
}
