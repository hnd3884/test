package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;

public class DsaPrivateKey extends DsaKey
{
    protected BigInteger x;
    
    public DsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger x) {
        super(bigInteger, bigInteger2, bigInteger3);
        this.x = x;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    public byte[] sign(final byte[] array) {
        final SHA1Digest sha1Digest = new SHA1Digest();
        sha1Digest.update(array, 0, array.length);
        final byte[] array2 = new byte[sha1Digest.getDigestSize()];
        sha1Digest.doFinal(array2, 0);
        return Dsa.sign(this.x, super.p, super.q, super.g, array2);
    }
    
    public boolean equals(final Object o) {
        if (o instanceof DsaPrivateKey) {
            final DsaPrivateKey dsaPrivateKey = (DsaPrivateKey)o;
            return this.x.equals(dsaPrivateKey.x) && super.p.equals(dsaPrivateKey.p) && super.q.equals(dsaPrivateKey.q) && super.g.equals(dsaPrivateKey.g);
        }
        return false;
    }
}
