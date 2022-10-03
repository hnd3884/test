package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;

public class RsaPrivateCrtKey extends RsaPrivateKey
{
    protected BigInteger publicExponent;
    protected BigInteger primeP;
    protected BigInteger primeQ;
    protected BigInteger primeExponentP;
    protected BigInteger primeExponentQ;
    protected BigInteger crtCoefficient;
    
    public RsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger publicExponent, final BigInteger bigInteger2, final BigInteger primeP, final BigInteger primeQ, final BigInteger primeExponentP, final BigInteger primeExponentQ, final BigInteger crtCoefficient) {
        super(bigInteger, bigInteger2);
        this.publicExponent = publicExponent;
        this.primeP = primeP;
        this.primeQ = primeQ;
        this.primeExponentP = primeExponentP;
        this.primeExponentQ = primeExponentQ;
        this.crtCoefficient = crtCoefficient;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    public BigInteger getPrimeP() {
        return this.primeP;
    }
    
    public BigInteger getPrimeQ() {
        return this.primeQ;
    }
    
    public BigInteger getPrimeExponentP() {
        return this.primeExponentP;
    }
    
    public BigInteger getPrimeExponentQ() {
        return this.primeExponentQ;
    }
    
    public BigInteger getCrtCoefficient() {
        return this.crtCoefficient;
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
        return RsaPrivateKey.unsignedBigIntToBytes(Rsa.doPrivateCrt(Rsa.padPKCS1(bigInteger, 1, n), this.getPrimeP(), this.getPrimeQ(), this.getPrimeExponentP(), this.getPrimeExponentQ(), this.getCrtCoefficient()), n);
    }
}
