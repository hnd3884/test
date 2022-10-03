package org.jscep.transaction;

import java.security.SecureRandom;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import java.util.Random;

public final class Nonce
{
    private static final int NONCE_LENGTH = 16;
    private static final Random RND;
    private final byte[] nonce;
    
    public Nonce(final byte[] nonce) {
        this.nonce = ArrayUtils.clone(nonce);
    }
    
    public byte[] getBytes() {
        return ArrayUtils.clone(this.nonce);
    }
    
    @Override
    public String toString() {
        return "Nonce [" + new String(Hex.encodeHex(this.nonce)) + "]";
    }
    
    public static Nonce nextNonce() {
        final byte[] bytes = new byte[16];
        Nonce.RND.nextBytes(bytes);
        return new Nonce(bytes);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Nonce nonce1 = (Nonce)o;
        return Arrays.equals(this.nonce, nonce1.nonce);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.nonce);
    }
    
    static {
        RND = new SecureRandom();
    }
}
