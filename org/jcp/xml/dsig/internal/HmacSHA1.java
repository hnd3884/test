package org.jcp.xml.dsig.internal;

import java.security.SignatureException;
import java.util.logging.Level;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.logging.Logger;

public class HmacSHA1
{
    private static Logger log;
    private static final int SHA1_BLOCK = 64;
    private byte[] key_opad;
    private MessageDigest digest;
    private int byte_length;
    
    public void init(final Key key, final int n) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("The key should not be null");
        }
        try {
            this.digest = MessageDigest.getInstance("SHA1");
            this.initialize(key);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new InvalidKeyException("SHA1 not supported");
        }
        if (n > 0) {
            this.byte_length = n / 8;
        }
        else {
            this.byte_length = -1;
        }
        if (HmacSHA1.log.isLoggable(Level.FINE)) {
            HmacSHA1.log.log(Level.FINE, "byte_length: " + this.byte_length);
        }
    }
    
    public void update(final byte[] array) {
        this.digest.update(array);
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public byte[] sign() throws SignatureException {
        if (this.byte_length == 0) {
            throw new SignatureException("length should be -1 or greater than zero, but is " + this.byte_length);
        }
        final byte[] digest = this.digest.digest();
        this.digest.reset();
        this.digest.update(this.key_opad);
        this.digest.update(digest);
        byte[] digest2 = this.digest.digest();
        if (this.byte_length > 0 && digest2.length > this.byte_length) {
            final byte[] array = new byte[this.byte_length];
            System.arraycopy(digest2, 0, array, 0, this.byte_length);
            digest2 = array;
        }
        return digest2;
    }
    
    public boolean verify(final byte[] array) throws SignatureException {
        return MessageDigest.isEqual(array, this.sign());
    }
    
    private void initialize(final Key key) {
        byte[] array = key.getEncoded();
        final byte[] array2 = new byte[64];
        if (array.length > 64) {
            this.digest.reset();
            array = this.digest.digest(array);
        }
        System.arraycopy(array, 0, array2, 0, array.length);
        for (int i = array.length; i < 64; ++i) {
            array2[i] = 0;
        }
        final byte[] array3 = new byte[64];
        this.key_opad = new byte[64];
        for (int j = 0; j < 64; ++j) {
            array3[j] = (byte)(array2[j] ^ 0x36);
            this.key_opad[j] = (byte)(array2[j] ^ 0x5C);
        }
        this.digest.reset();
        this.digest.update(array3);
    }
    
    static {
        HmacSHA1.log = Logger.getLogger("org.jcp.xml.dsig.internal");
    }
}
