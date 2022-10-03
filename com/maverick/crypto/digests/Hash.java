package com.maverick.crypto.digests;

import com.maverick.util.ByteArrayWriter;
import java.math.BigInteger;

public class Hash
{
    private Digest b;
    
    public Hash(final String s) {
        this.b = DigestFactory.createDigest(s);
    }
    
    public Hash(final Digest b) {
        this.b = b;
    }
    
    public void putBigInteger(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        this.putInt(byteArray.length);
        this.putBytes(byteArray);
    }
    
    public void putByte(final byte b) {
        this.b.update(b);
    }
    
    public void putBytes(final byte[] array) {
        this.b.update(array, 0, array.length);
    }
    
    public void putBytes(final byte[] array, final int n, final int n2) {
        this.b.update(array, n, n2);
    }
    
    public void putInt(final int n) {
        this.putBytes(ByteArrayWriter.encodeInt(n));
    }
    
    public void putString(final String s) {
        this.putInt(s.length());
        this.putBytes(s.getBytes());
    }
    
    public void reset() {
        this.b.reset();
    }
    
    public byte[] doFinal() {
        final byte[] array = new byte[this.b.getDigestSize()];
        this.b.doFinal(array, 0);
        return array;
    }
}
