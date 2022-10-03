package com.sun.security.sasl;

import java.io.UnsupportedEncodingException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslClient;

final class PlainClient implements SaslClient
{
    private boolean completed;
    private byte[] pw;
    private String authorizationID;
    private String authenticationID;
    private static byte SEP;
    
    PlainClient(final String authorizationID, final String authenticationID, final byte[] pw) throws SaslException {
        this.completed = false;
        if (authenticationID == null || pw == null) {
            throw new SaslException("PLAIN: authorization ID and password must be specified");
        }
        this.authorizationID = authorizationID;
        this.authenticationID = authenticationID;
        this.pw = pw;
    }
    
    @Override
    public String getMechanismName() {
        return "PLAIN";
    }
    
    @Override
    public boolean hasInitialResponse() {
        return true;
    }
    
    @Override
    public void dispose() throws SaslException {
        this.clearPassword();
    }
    
    @Override
    public byte[] evaluateChallenge(final byte[] array) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("PLAIN authentication already completed");
        }
        this.completed = true;
        try {
            final byte[] array2 = (byte[])((this.authorizationID != null) ? this.authorizationID.getBytes("UTF8") : null);
            final byte[] bytes = this.authenticationID.getBytes("UTF8");
            final byte[] array3 = new byte[this.pw.length + bytes.length + 2 + ((array2 == null) ? 0 : array2.length)];
            int length = 0;
            if (array2 != null) {
                System.arraycopy(array2, 0, array3, 0, array2.length);
                length = array2.length;
            }
            array3[length++] = PlainClient.SEP;
            System.arraycopy(bytes, 0, array3, length, bytes.length);
            int n = length + bytes.length;
            array3[n++] = PlainClient.SEP;
            System.arraycopy(this.pw, 0, array3, n, this.pw.length);
            this.clearPassword();
            return array3;
        }
        catch (final UnsupportedEncodingException ex) {
            throw new SaslException("Cannot get UTF-8 encoding of ids", ex);
        }
    }
    
    @Override
    public boolean isComplete() {
        return this.completed;
    }
    
    @Override
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new SaslException("PLAIN supports neither integrity nor privacy");
        }
        throw new IllegalStateException("PLAIN authentication not completed");
    }
    
    @Override
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new SaslException("PLAIN supports neither integrity nor privacy");
        }
        throw new IllegalStateException("PLAIN authentication not completed");
    }
    
    @Override
    public Object getNegotiatedProperty(final String s) {
        if (!this.completed) {
            throw new IllegalStateException("PLAIN authentication not completed");
        }
        if (s.equals("javax.security.sasl.qop")) {
            return "auth";
        }
        return null;
    }
    
    private void clearPassword() {
        if (this.pw != null) {
            for (int i = 0; i < this.pw.length; ++i) {
                this.pw[i] = 0;
            }
            this.pw = null;
        }
    }
    
    @Override
    protected void finalize() {
        this.clearPassword();
    }
    
    static {
        PlainClient.SEP = 0;
    }
}
