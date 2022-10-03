package org.bouncycastle.crypto.agreement.jpake;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import java.math.BigInteger;

public class JPAKEUtil
{
    static final BigInteger ZERO;
    static final BigInteger ONE;
    
    public static BigInteger generateX1(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(JPAKEUtil.ZERO, bigInteger.subtract(JPAKEUtil.ONE), secureRandom);
    }
    
    public static BigInteger generateX2(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(JPAKEUtil.ONE, bigInteger.subtract(JPAKEUtil.ONE), secureRandom);
    }
    
    public static BigInteger calculateS(final char[] array) {
        return new BigInteger(Strings.toUTF8ByteArray(array));
    }
    
    public static BigInteger calculateGx(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger2.modPow(bigInteger3, bigInteger);
    }
    
    public static BigInteger calculateGA(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return bigInteger2.multiply(bigInteger3).multiply(bigInteger4).mod(bigInteger);
    }
    
    public static BigInteger calculateX2s(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger2.multiply(bigInteger3).mod(bigInteger);
    }
    
    public static BigInteger calculateA(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return bigInteger3.modPow(bigInteger4, bigInteger);
    }
    
    public static BigInteger[] calculateZeroKnowledgeProof(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final String s, final Digest digest, final SecureRandom secureRandom) {
        final BigInteger[] array = new BigInteger[2];
        final BigInteger randomInRange = BigIntegers.createRandomInRange(JPAKEUtil.ZERO, bigInteger2.subtract(JPAKEUtil.ONE), secureRandom);
        final BigInteger modPow = bigInteger3.modPow(randomInRange, bigInteger);
        final BigInteger calculateHashForZeroKnowledgeProof = calculateHashForZeroKnowledgeProof(bigInteger3, modPow, bigInteger4, s, digest);
        array[0] = modPow;
        array[1] = randomInRange.subtract(bigInteger5.multiply(calculateHashForZeroKnowledgeProof)).mod(bigInteger2);
        return array;
    }
    
    private static BigInteger calculateHashForZeroKnowledgeProof(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final String s, final Digest digest) {
        digest.reset();
        updateDigestIncludingSize(digest, bigInteger);
        updateDigestIncludingSize(digest, bigInteger2);
        updateDigestIncludingSize(digest, bigInteger3);
        updateDigestIncludingSize(digest, s);
        final byte[] array = new byte[digest.getDigestSize()];
        digest.doFinal(array, 0);
        return new BigInteger(array);
    }
    
    public static void validateGx4(final BigInteger bigInteger) throws CryptoException {
        if (bigInteger.equals(JPAKEUtil.ONE)) {
            throw new CryptoException("g^x validation failed.  g^x should not be 1.");
        }
    }
    
    public static void validateGa(final BigInteger bigInteger) throws CryptoException {
        if (bigInteger.equals(JPAKEUtil.ONE)) {
            throw new CryptoException("ga is equal to 1.  It should not be.  The chances of this happening are on the order of 2^160 for a 160-bit q.  Try again.");
        }
    }
    
    public static void validateZeroKnowledgeProof(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger[] array, final String s, final Digest digest) throws CryptoException {
        final BigInteger bigInteger5 = array[0];
        final BigInteger bigInteger6 = array[1];
        final BigInteger calculateHashForZeroKnowledgeProof = calculateHashForZeroKnowledgeProof(bigInteger3, bigInteger5, bigInteger4, s, digest);
        if (bigInteger4.compareTo(JPAKEUtil.ZERO) != 1 || bigInteger4.compareTo(bigInteger) != -1 || bigInteger4.modPow(bigInteger2, bigInteger).compareTo(JPAKEUtil.ONE) != 0 || bigInteger3.modPow(bigInteger6, bigInteger).multiply(bigInteger4.modPow(calculateHashForZeroKnowledgeProof, bigInteger)).mod(bigInteger).compareTo(bigInteger5) != 0) {
            throw new CryptoException("Zero-knowledge proof validation failed");
        }
    }
    
    public static BigInteger calculateKeyingMaterial(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6) {
        return bigInteger3.modPow(bigInteger4.multiply(bigInteger5).negate().mod(bigInteger2), bigInteger).multiply(bigInteger6).modPow(bigInteger4, bigInteger);
    }
    
    public static void validateParticipantIdsDiffer(final String s, final String s2) throws CryptoException {
        if (s.equals(s2)) {
            throw new CryptoException("Both participants are using the same participantId (" + s + "). This is not allowed. Each participant must use a unique participantId.");
        }
    }
    
    public static void validateParticipantIdsEqual(final String s, final String s2) throws CryptoException {
        if (!s.equals(s2)) {
            throw new CryptoException("Received payload from incorrect partner (" + s2 + "). Expected to receive payload from " + s + ".");
        }
    }
    
    public static void validateNotNull(final Object o, final String s) {
        if (o == null) {
            throw new NullPointerException(s + " must not be null");
        }
    }
    
    public static BigInteger calculateMacTag(final String s, final String s2, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final Digest digest) {
        final byte[] calculateMacKey = calculateMacKey(bigInteger5, digest);
        final HMac hMac = new HMac(digest);
        final byte[] array = new byte[hMac.getMacSize()];
        hMac.init(new KeyParameter(calculateMacKey));
        updateMac(hMac, "KC_1_U");
        updateMac(hMac, s);
        updateMac(hMac, s2);
        updateMac(hMac, bigInteger);
        updateMac(hMac, bigInteger2);
        updateMac(hMac, bigInteger3);
        updateMac(hMac, bigInteger4);
        hMac.doFinal(array, 0);
        Arrays.fill(calculateMacKey, (byte)0);
        return new BigInteger(array);
    }
    
    private static byte[] calculateMacKey(final BigInteger bigInteger, final Digest digest) {
        digest.reset();
        updateDigest(digest, bigInteger);
        updateDigest(digest, "JPAKE_KC");
        final byte[] array = new byte[digest.getDigestSize()];
        digest.doFinal(array, 0);
        return array;
    }
    
    public static void validateMacTag(final String s, final String s2, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final Digest digest, final BigInteger bigInteger6) throws CryptoException {
        if (!calculateMacTag(s2, s, bigInteger3, bigInteger4, bigInteger, bigInteger2, bigInteger5, digest).equals(bigInteger6)) {
            throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
        }
    }
    
    private static void updateDigest(final Digest digest, final BigInteger bigInteger) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(unsignedByteArray, 0, unsignedByteArray.length);
        Arrays.fill(unsignedByteArray, (byte)0);
    }
    
    private static void updateDigestIncludingSize(final Digest digest, final BigInteger bigInteger) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(intToByteArray(unsignedByteArray.length), 0, 4);
        digest.update(unsignedByteArray, 0, unsignedByteArray.length);
        Arrays.fill(unsignedByteArray, (byte)0);
    }
    
    private static void updateDigest(final Digest digest, final String s) {
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(s);
        digest.update(utf8ByteArray, 0, utf8ByteArray.length);
        Arrays.fill(utf8ByteArray, (byte)0);
    }
    
    private static void updateDigestIncludingSize(final Digest digest, final String s) {
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(s);
        digest.update(intToByteArray(utf8ByteArray.length), 0, 4);
        digest.update(utf8ByteArray, 0, utf8ByteArray.length);
        Arrays.fill(utf8ByteArray, (byte)0);
    }
    
    private static void updateMac(final Mac mac, final BigInteger bigInteger) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        mac.update(unsignedByteArray, 0, unsignedByteArray.length);
        Arrays.fill(unsignedByteArray, (byte)0);
    }
    
    private static void updateMac(final Mac mac, final String s) {
        final byte[] utf8ByteArray = Strings.toUTF8ByteArray(s);
        mac.update(utf8ByteArray, 0, utf8ByteArray.length);
        Arrays.fill(utf8ByteArray, (byte)0);
    }
    
    private static byte[] intToByteArray(final int n) {
        return new byte[] { (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
    }
}
