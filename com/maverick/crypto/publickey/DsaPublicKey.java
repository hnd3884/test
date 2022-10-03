package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;

public class DsaPublicKey extends DsaKey implements PublicKey
{
    protected BigInteger y;
    
    public DsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger y) {
        super(bigInteger, bigInteger2, bigInteger3);
        this.y = y;
    }
    
    public DsaPublicKey() {
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    public int getBitLength() {
        return super.p.bitLength();
    }
    
    public boolean verifySignature(final byte[] array, final byte[] array2) {
        final SHA1Digest sha1Digest = new SHA1Digest();
        sha1Digest.update(array2, 0, array2.length);
        final byte[] array3 = new byte[sha1Digest.getDigestSize()];
        sha1Digest.doFinal(array3, 0);
        return Dsa.verify(this.y, super.p, super.q, super.g, array, array3);
    }
    
    protected boolean verifySignature(final byte[] array, final BigInteger bigInteger, final BigInteger bigInteger2) {
        final SHA1Digest sha1Digest = new SHA1Digest();
        sha1Digest.update(array, 0, array.length);
        final byte[] array2 = new byte[sha1Digest.getDigestSize()];
        sha1Digest.doFinal(array2, 0);
        final BigInteger mod = new BigInteger(1, array2).mod(super.q);
        if (BigInteger.valueOf(0L).compareTo(bigInteger) >= 0 || super.q.compareTo(bigInteger) <= 0) {
            return false;
        }
        if (BigInteger.valueOf(0L).compareTo(bigInteger2) >= 0 || super.q.compareTo(bigInteger2) <= 0) {
            return false;
        }
        final BigInteger modInverse = bigInteger2.modInverse(super.q);
        return super.g.modPow(mod.multiply(modInverse).mod(super.q), super.p).multiply(this.y.modPow(bigInteger.multiply(modInverse).mod(super.q), super.p)).mod(super.p).mod(super.q).compareTo(bigInteger) == 0;
    }
}
