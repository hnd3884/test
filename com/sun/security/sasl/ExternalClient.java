package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslClient;

final class ExternalClient implements SaslClient
{
    private byte[] username;
    private boolean completed;
    
    ExternalClient(final String s) throws SaslException {
        this.completed = false;
        if (s != null) {
            try {
                this.username = s.getBytes("UTF8");
                return;
            }
            catch (final UnsupportedEncodingException ex) {
                throw new SaslException("Cannot convert " + s + " into UTF-8", ex);
            }
        }
        this.username = new byte[0];
    }
    
    @Override
    public String getMechanismName() {
        return "EXTERNAL";
    }
    
    @Override
    public boolean hasInitialResponse() {
        return true;
    }
    
    @Override
    public void dispose() throws SaslException {
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("EXTERNAL authentication already completed");
        }
        this.completed = true;
        return this.username;
    }
    
    @Override
    public boolean isComplete() {
        return this.completed;
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new SaslException("EXTERNAL has no supported QOP");
        }
        throw new IllegalStateException("EXTERNAL authentication Not completed");
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new SaslException("EXTERNAL has no supported QOP");
        }
        throw new IllegalStateException("EXTERNAL authentication not completed");
    }
    
    @Override
    public Object getNegotiatedProperty(final String s) {
        if (this.completed) {
            return null;
        }
        throw new IllegalStateException("EXTERNAL authentication not completed");
    }
}
