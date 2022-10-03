package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;

public class RsaPrivateKey extends RsaKey
{
    protected BigInteger privateExponent;
    protected static final byte[] ASN_SHA1;
    
    public RsaPrivateKey(final BigInteger bigInteger, final BigInteger privateExponent) {
        super(bigInteger);
        this.privateExponent = privateExponent;
    }
    
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
    
    public byte[] sign(final byte[] array) {
        final SHA1Digest sha1Digest = new SHA1Digest();
        sha1Digest.update(array, 0, array.length);
        final byte[] array2 = new byte[sha1Digest.getDigestSize()];
        sha1Digest.doFinal(array2, 0);
        final byte[] array3 = new byte[array2.length + RsaPrivateKey.ASN_SHA1.length];
        System.arraycopy(RsaPrivateKey.ASN_SHA1, 0, array3, 0, RsaPrivateKey.ASN_SHA1.length);
        System.arraycopy(array2, 0, array3, RsaPrivateKey.ASN_SHA1.length, array2.length);
        final BigInteger bigInteger = new BigInteger(1, array3);
        final int n = (this.getModulus().bitLength() + 7) / 8;
        return unsignedBigIntToBytes(Rsa.doPrivate(Rsa.padPKCS1(bigInteger, 1, n), this.getModulus(), this.getPrivateExponent()), n);
    }
    
    protected static byte[] unsignedBigIntToBytes(final BigInteger bigInteger, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        byte[] array;
        if (byteArray.length > n) {
            array = new byte[n];
            System.arraycopy(byteArray, byteArray.length - n, array, 0, n);
        }
        else if (byteArray.length < n) {
            array = new byte[n];
            System.arraycopy(byteArray, 0, array, n - byteArray.length, byteArray.length);
        }
        else {
            array = byteArray;
        }
        return array;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof RsaPrivateKey) {
            final RsaPrivateKey rsaPrivateKey = (RsaPrivateKey)o;
            return rsaPrivateKey.getBitLength() == this.getBitLength() && rsaPrivateKey.getModulus().compareTo(this.getModulus()) == 0 && rsaPrivateKey.getPrivateExponent().compareTo(this.getPrivateExponent()) == 0;
        }
        return false;
    }
    
    static {
        ASN_SHA1 = new byte[] { 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20 };
    }
}
