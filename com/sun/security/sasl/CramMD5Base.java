package com.sun.security.sasl;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.security.MessageDigest;
import javax.security.sasl.SaslException;
import java.util.logging.Logger;

abstract class CramMD5Base
{
    protected boolean completed;
    protected boolean aborted;
    protected byte[] pw;
    private static final int MD5_BLOCKSIZE = 64;
    private static final String SASL_LOGGER_NAME = "javax.security.sasl";
    protected static Logger logger;
    
    protected CramMD5Base() {
        this.completed = false;
        this.aborted = false;
        initLogger();
    }
    
    public String getMechanismName() {
        return "CRAM-MD5";
    }
    
    public boolean isComplete() {
        return this.completed;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
        }
        throw new IllegalStateException("CRAM-MD5 authentication not completed");
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("CRAM-MD5 supports neither integrity nor privacy");
        }
        throw new IllegalStateException("CRAM-MD5 authentication not completed");
    }
    
    public Object getNegotiatedProperty(final String s) {
        if (!this.completed) {
            throw new IllegalStateException("CRAM-MD5 authentication not completed");
        }
        if (s.equals("javax.security.sasl.qop")) {
            return "auth";
        }
        return null;
    }
    
    public void dispose() throws SaslException {
        this.clearPassword();
    }
    
    protected void clearPassword() {
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
    
    static final String HMAC_MD5(byte[] digest, final byte[] array) throws NoSuchAlgorithmException {
        final MessageDigest instance = MessageDigest.getInstance("MD5");
        if (digest.length > 64) {
            digest = instance.digest(digest);
        }
        final byte[] array2 = new byte[64];
        final byte[] array3 = new byte[64];
        for (int i = 0; i < digest.length; ++i) {
            array2[i] = digest[i];
            array3[i] = digest[i];
        }
        for (int j = 0; j < 64; ++j) {
            final byte[] array4 = array2;
            final int n = j;
            array4[n] ^= 0x36;
            final byte[] array5 = array3;
            final int n2 = j;
            array5[n2] ^= 0x5C;
        }
        instance.update(array2);
        instance.update(array);
        final byte[] digest2 = instance.digest();
        instance.update(array3);
        instance.update(digest2);
        final byte[] digest3 = instance.digest();
        final StringBuffer sb = new StringBuffer();
        for (int k = 0; k < digest3.length; ++k) {
            if ((digest3[k] & 0xFF) < 16) {
                sb.append("0" + Integer.toHexString(digest3[k] & 0xFF));
            }
            else {
                sb.append(Integer.toHexString(digest3[k] & 0xFF));
            }
        }
        Arrays.fill(array2, (byte)0);
        Arrays.fill(array3, (byte)0);
        return sb.toString();
    }
    
    private static synchronized void initLogger() {
        if (CramMD5Base.logger == null) {
            CramMD5Base.logger = Logger.getLogger("javax.security.sasl");
        }
    }
}
