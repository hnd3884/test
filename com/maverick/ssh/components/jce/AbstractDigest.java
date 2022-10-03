package com.maverick.ssh.components.jce;

import com.maverick.util.ByteArrayWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import com.maverick.ssh.components.Digest;

public class AbstractDigest implements Digest
{
    MessageDigest c;
    
    public AbstractDigest(final String s) throws NoSuchAlgorithmException {
        this.c = ((JCEProvider.getProviderForAlgorithm(s) == null) ? MessageDigest.getInstance(s) : MessageDigest.getInstance(s, JCEProvider.getProviderForAlgorithm(s)));
    }
    
    public byte[] doFinal() {
        return this.c.digest();
    }
    
    public void putBigInteger(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        this.putInt(byteArray.length);
        this.putBytes(byteArray);
    }
    
    public void putByte(final byte b) {
        this.c.update(b);
    }
    
    public void putBytes(final byte[] array) {
        this.c.update(array, 0, array.length);
    }
    
    public void putBytes(final byte[] array, final int n, final int n2) {
        this.c.update(array, n, n2);
    }
    
    public void putInt(final int n) {
        this.putBytes(ByteArrayWriter.encodeInt(n));
    }
    
    public void putString(final String s) {
        this.putInt(s.length());
        this.putBytes(s.getBytes());
    }
    
    public void reset() {
        this.c.reset();
    }
    
    public String getProvider() {
        return this.c.getProvider().getName();
    }
}
