package com.sun.mail.auth;

import java.io.UnsupportedEncodingException;
import com.sun.mail.util.ASCIIUtility;
import java.io.IOException;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;

public class OAuth2SaslClient implements SaslClient
{
    private CallbackHandler cbh;
    private boolean complete;
    
    public OAuth2SaslClient(final Map<String, ?> props, final CallbackHandler cbh) {
        this.complete = false;
        this.cbh = cbh;
    }
    
    @Override
    public String getMechanismName() {
        return "XOAUTH2";
    }
    
    @Override
    public boolean hasInitialResponse() {
        return true;
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] challenge) throws SaslException {
        if (this.complete) {
            return new byte[0];
        }
        final NameCallback ncb = new NameCallback("User name:");
        final PasswordCallback pcb = new PasswordCallback("OAuth token:", false);
        try {
            this.cbh.handle(new Callback[] { ncb, pcb });
        }
        catch (final UnsupportedCallbackException ex) {
            throw new SaslException("Unsupported callback", ex);
        }
        catch (final IOException ex2) {
            throw new SaslException("Callback handler failed", ex2);
        }
        final String user = ncb.getName();
        final String token = new String(pcb.getPassword());
        pcb.clearPassword();
        final String resp = "user=" + user + "\u0001auth=Bearer " + token + "\u0001\u0001";
        byte[] response;
        try {
            response = resp.getBytes("utf-8");
        }
        catch (final UnsupportedEncodingException ex3) {
            response = ASCIIUtility.getBytes(resp);
        }
        this.complete = true;
        return response;
    }
    
    @Override
    public boolean isComplete() {
        return this.complete;
    }
    
    @Override
    public byte[] unwrap(final byte[] incoming, final int offset, final int len) throws SaslException {
        throw new IllegalStateException("OAUTH2 unwrap not supported");
    }
    
    @Override
    public byte[] wrap(final byte[] outgoing, final int offset, final int len) throws SaslException {
        throw new IllegalStateException("OAUTH2 wrap not supported");
    }
    
    @Override
    public Object getNegotiatedProperty(final String propName) {
        if (!this.complete) {
            throw new IllegalStateException("OAUTH2 getNegotiatedProperty");
        }
        return null;
    }
    
    @Override
    public void dispose() throws SaslException {
    }
}
