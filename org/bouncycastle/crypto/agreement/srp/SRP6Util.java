package org.bouncycastle.crypto.agreement.srp;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;

public class SRP6Util
{
    private static BigInteger ZERO;
    private static BigInteger ONE;
    
    public static BigInteger calculateK(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2) {
        return hashPaddedPair(digest, bigInteger, bigInteger, bigInteger2);
    }
    
    public static BigInteger calculateU(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return hashPaddedPair(digest, bigInteger, bigInteger2, bigInteger3);
    }
    
    public static BigInteger calculateX(final Digest digest, final BigInteger bigInteger, final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] array4 = new byte[digest.getDigestSize()];
        digest.update(array2, 0, array2.length);
        digest.update((byte)58);
        digest.update(array3, 0, array3.length);
        digest.doFinal(array4, 0);
        digest.update(array, 0, array.length);
        digest.update(array4, 0, array4.length);
        digest.doFinal(array4, 0);
        return new BigInteger(1, array4);
    }
    
    public static BigInteger generatePrivateValue(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(SRP6Util.ONE.shiftLeft(Math.min(256, bigInteger.bitLength() / 2) - 1), bigInteger.subtract(SRP6Util.ONE), secureRandom);
    }
    
    public static BigInteger validatePublicValue(final BigInteger bigInteger, BigInteger mod) throws CryptoException {
        mod = mod.mod(bigInteger);
        if (mod.equals(SRP6Util.ZERO)) {
            throw new CryptoException("Invalid public value: 0");
        }
        return mod;
    }
    
    public static BigInteger calculateM1(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return hashPaddedTriplet(digest, bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public static BigInteger calculateM2(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return hashPaddedTriplet(digest, bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public static BigInteger calculateKey(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2) {
        final byte[] padded = getPadded(bigInteger2, (bigInteger.bitLength() + 7) / 8);
        digest.update(padded, 0, padded.length);
        final byte[] array = new byte[digest.getDigestSize()];
        digest.doFinal(array, 0);
        return new BigInteger(1, array);
    }
    
    private static BigInteger hashPaddedTriplet(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        final int n = (bigInteger.bitLength() + 7) / 8;
        final byte[] padded = getPadded(bigInteger2, n);
        final byte[] padded2 = getPadded(bigInteger3, n);
        final byte[] padded3 = getPadded(bigInteger4, n);
        digest.update(padded, 0, padded.length);
        digest.update(padded2, 0, padded2.length);
        digest.update(padded3, 0, padded3.length);
        final byte[] array = new byte[digest.getDigestSize()];
        digest.doFinal(array, 0);
        return new BigInteger(1, array);
    }
    
    private static BigInteger hashPaddedPair(final Digest digest, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        final int n = (bigInteger.bitLength() + 7) / 8;
        final byte[] padded = getPadded(bigInteger2, n);
        final byte[] padded2 = getPadded(bigInteger3, n);
        digest.update(padded, 0, padded.length);
        digest.update(padded2, 0, padded2.length);
        final byte[] array = new byte[digest.getDigestSize()];
        digest.doFinal(array, 0);
        return new BigInteger(1, array);
    }
    
    private static byte[] getPadded(final BigInteger bigInteger, final int n) {
        byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        if (unsignedByteArray.length < n) {
            final byte[] array = new byte[n];
            System.arraycopy(unsignedByteArray, 0, array, n - unsignedByteArray.length, unsignedByteArray.length);
            unsignedByteArray = array;
        }
        return unsignedByteArray;
    }
    
    static {
        SRP6Util.ZERO = BigInteger.valueOf(0L);
        SRP6Util.ONE = BigInteger.valueOf(1L);
    }
}
