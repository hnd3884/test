package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.Digest;

final class KeyedHashFunctions
{
    private final Digest digest;
    private final int digestSize;
    
    protected KeyedHashFunctions(final Digest digest, final int digestSize) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        this.digest = digest;
        this.digestSize = digestSize;
    }
    
    private byte[] coreDigest(final int n, final byte[] array, final byte[] array2) {
        final byte[] bytesBigEndian = XMSSUtil.toBytesBigEndian(n, this.digestSize);
        this.digest.update(bytesBigEndian, 0, bytesBigEndian.length);
        this.digest.update(array, 0, array.length);
        this.digest.update(array2, 0, array2.length);
        final byte[] array3 = new byte[this.digestSize];
        if (this.digest instanceof Xof) {
            ((Xof)this.digest).doFinal(array3, 0, this.digestSize);
        }
        else {
            this.digest.doFinal(array3, 0);
        }
        return array3;
    }
    
    protected byte[] F(final byte[] array, final byte[] array2) {
        if (array.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (array2.length != this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return this.coreDigest(0, array, array2);
    }
    
    protected byte[] H(final byte[] array, final byte[] array2) {
        if (array.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (array2.length != 2 * this.digestSize) {
            throw new IllegalArgumentException("wrong in length");
        }
        return this.coreDigest(1, array, array2);
    }
    
    protected byte[] HMsg(final byte[] array, final byte[] array2) {
        if (array.length != 3 * this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        return this.coreDigest(2, array, array2);
    }
    
    protected byte[] PRF(final byte[] array, final byte[] array2) {
        if (array.length != this.digestSize) {
            throw new IllegalArgumentException("wrong key length");
        }
        if (array2.length != 32) {
            throw new IllegalArgumentException("wrong address length");
        }
        return this.coreDigest(3, array, array2);
    }
}
