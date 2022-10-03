package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;

public class RsaPublicKey extends RsaKey implements PublicKey
{
    protected BigInteger publicExponent;
    protected static final byte[] ASN_SHA1;
    
    public RsaPublicKey() {
    }
    
    public RsaPublicKey(final BigInteger bigInteger, final BigInteger publicExponent) {
        super(bigInteger);
        this.publicExponent = publicExponent;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    protected void setPublicExponent(final BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }
    
    public boolean verifySignature(byte[] byteArray, final byte[] array) {
        byteArray = Rsa.removePKCS1(Rsa.doPublic(new BigInteger(1, byteArray), this.getModulus(), this.publicExponent), 1).toByteArray();
        final SHA1Digest sha1Digest = new SHA1Digest();
        sha1Digest.update(array, 0, array.length);
        final byte[] array2 = new byte[sha1Digest.getDigestSize()];
        sha1Digest.doFinal(array2, 0);
        if (array2.length != byteArray.length - RsaPublicKey.ASN_SHA1.length) {
            return false;
        }
        byte[] asn_SHA1 = RsaPublicKey.ASN_SHA1;
        for (int i = 0, n = 0; i < byteArray.length; ++i, ++n) {
            if (i == RsaPublicKey.ASN_SHA1.length) {
                asn_SHA1 = array2;
                n = 0;
            }
            if (byteArray[i] != asn_SHA1[n]) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        return this.getModulus().hashCode() ^ this.publicExponent.hashCode();
    }
    
    static {
        ASN_SHA1 = new byte[] { 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20 };
    }
}
