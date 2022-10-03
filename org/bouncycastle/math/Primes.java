package org.bouncycastle.math;

import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;

public abstract class Primes
{
    public static final int SMALL_FACTOR_LIMIT = 211;
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    private static final BigInteger THREE;
    
    public static STOutput generateSTRandomPrime(final Digest digest, final int n, final byte[] array) {
        if (digest == null) {
            throw new IllegalArgumentException("'hash' cannot be null");
        }
        if (n < 2) {
            throw new IllegalArgumentException("'length' must be >= 2");
        }
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("'inputSeed' cannot be null or empty");
        }
        return implSTRandomPrime(digest, n, Arrays.clone(array));
    }
    
    public static MROutput enhancedMRProbablePrimeTest(final BigInteger bigInteger, final SecureRandom secureRandom, final int n) {
        checkCandidate(bigInteger, "candidate");
        if (secureRandom == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (n < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (bigInteger.bitLength() == 2) {
            return probablyPrime();
        }
        if (!bigInteger.testBit(0)) {
            return provablyCompositeWithFactor(Primes.TWO);
        }
        final BigInteger subtract = bigInteger.subtract(Primes.ONE);
        final BigInteger subtract2 = bigInteger.subtract(Primes.TWO);
        final int lowestSetBit = subtract.getLowestSetBit();
        final BigInteger shiftRight = subtract.shiftRight(lowestSetBit);
        for (int i = 0; i < n; ++i) {
            final BigInteger randomInRange = BigIntegers.createRandomInRange(Primes.TWO, subtract2, secureRandom);
            final BigInteger gcd = randomInRange.gcd(bigInteger);
            if (gcd.compareTo(Primes.ONE) > 0) {
                return provablyCompositeWithFactor(gcd);
            }
            BigInteger bigInteger2 = randomInRange.modPow(shiftRight, bigInteger);
            if (!bigInteger2.equals(Primes.ONE)) {
                if (!bigInteger2.equals(subtract)) {
                    boolean b = false;
                    BigInteger bigInteger3 = bigInteger2;
                    for (int j = 1; j < lowestSetBit; ++j) {
                        bigInteger2 = bigInteger2.modPow(Primes.TWO, bigInteger);
                        if (bigInteger2.equals(subtract)) {
                            b = true;
                            break;
                        }
                        if (bigInteger2.equals(Primes.ONE)) {
                            break;
                        }
                        bigInteger3 = bigInteger2;
                    }
                    if (!b) {
                        if (!bigInteger2.equals(Primes.ONE)) {
                            bigInteger3 = bigInteger2;
                            final BigInteger modPow = bigInteger2.modPow(Primes.TWO, bigInteger);
                            if (!modPow.equals(Primes.ONE)) {
                                bigInteger3 = modPow;
                            }
                        }
                        final BigInteger gcd2 = bigInteger3.subtract(Primes.ONE).gcd(bigInteger);
                        if (gcd2.compareTo(Primes.ONE) > 0) {
                            return provablyCompositeWithFactor(gcd2);
                        }
                        return provablyCompositeNotPrimePower();
                    }
                }
            }
        }
        return probablyPrime();
    }
    
    public static boolean hasAnySmallFactors(final BigInteger bigInteger) {
        checkCandidate(bigInteger, "candidate");
        return implHasAnySmallFactors(bigInteger);
    }
    
    public static boolean isMRProbablePrime(final BigInteger bigInteger, final SecureRandom secureRandom, final int n) {
        checkCandidate(bigInteger, "candidate");
        if (secureRandom == null) {
            throw new IllegalArgumentException("'random' cannot be null");
        }
        if (n < 1) {
            throw new IllegalArgumentException("'iterations' must be > 0");
        }
        if (bigInteger.bitLength() == 2) {
            return true;
        }
        if (!bigInteger.testBit(0)) {
            return false;
        }
        final BigInteger subtract = bigInteger.subtract(Primes.ONE);
        final BigInteger subtract2 = bigInteger.subtract(Primes.TWO);
        final int lowestSetBit = subtract.getLowestSetBit();
        final BigInteger shiftRight = subtract.shiftRight(lowestSetBit);
        for (int i = 0; i < n; ++i) {
            if (!implMRProbablePrimeToBase(bigInteger, subtract, shiftRight, lowestSetBit, BigIntegers.createRandomInRange(Primes.TWO, subtract2, secureRandom))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isMRProbablePrimeToBase(final BigInteger bigInteger, final BigInteger bigInteger2) {
        checkCandidate(bigInteger, "candidate");
        checkCandidate(bigInteger2, "base");
        if (bigInteger2.compareTo(bigInteger.subtract(Primes.ONE)) >= 0) {
            throw new IllegalArgumentException("'base' must be < ('candidate' - 1)");
        }
        if (bigInteger.bitLength() == 2) {
            return true;
        }
        final BigInteger subtract = bigInteger.subtract(Primes.ONE);
        final int lowestSetBit = subtract.getLowestSetBit();
        return implMRProbablePrimeToBase(bigInteger, subtract, subtract.shiftRight(lowestSetBit), lowestSetBit, bigInteger2);
    }
    
    private static void checkCandidate(final BigInteger bigInteger, final String s) {
        if (bigInteger == null || bigInteger.signum() < 1 || bigInteger.bitLength() < 2) {
            throw new IllegalArgumentException("'" + s + "' must be non-null and >= 2");
        }
    }
    
    private static boolean implHasAnySmallFactors(final BigInteger bigInteger) {
        final int intValue = bigInteger.mod(BigInteger.valueOf(223092870)).intValue();
        if (intValue % 2 == 0 || intValue % 3 == 0 || intValue % 5 == 0 || intValue % 7 == 0 || intValue % 11 == 0 || intValue % 13 == 0 || intValue % 17 == 0 || intValue % 19 == 0 || intValue % 23 == 0) {
            return true;
        }
        final int intValue2 = bigInteger.mod(BigInteger.valueOf(58642669)).intValue();
        if (intValue2 % 29 == 0 || intValue2 % 31 == 0 || intValue2 % 37 == 0 || intValue2 % 41 == 0 || intValue2 % 43 == 0) {
            return true;
        }
        final int intValue3 = bigInteger.mod(BigInteger.valueOf(600662303)).intValue();
        if (intValue3 % 47 == 0 || intValue3 % 53 == 0 || intValue3 % 59 == 0 || intValue3 % 61 == 0 || intValue3 % 67 == 0) {
            return true;
        }
        final int intValue4 = bigInteger.mod(BigInteger.valueOf(33984931)).intValue();
        if (intValue4 % 71 == 0 || intValue4 % 73 == 0 || intValue4 % 79 == 0 || intValue4 % 83 == 0) {
            return true;
        }
        final int intValue5 = bigInteger.mod(BigInteger.valueOf(89809099)).intValue();
        if (intValue5 % 89 == 0 || intValue5 % 97 == 0 || intValue5 % 101 == 0 || intValue5 % 103 == 0) {
            return true;
        }
        final int intValue6 = bigInteger.mod(BigInteger.valueOf(167375713)).intValue();
        if (intValue6 % 107 == 0 || intValue6 % 109 == 0 || intValue6 % 113 == 0 || intValue6 % 127 == 0) {
            return true;
        }
        final int intValue7 = bigInteger.mod(BigInteger.valueOf(371700317)).intValue();
        if (intValue7 % 131 == 0 || intValue7 % 137 == 0 || intValue7 % 139 == 0 || intValue7 % 149 == 0) {
            return true;
        }
        final int intValue8 = bigInteger.mod(BigInteger.valueOf(645328247)).intValue();
        if (intValue8 % 151 == 0 || intValue8 % 157 == 0 || intValue8 % 163 == 0 || intValue8 % 167 == 0) {
            return true;
        }
        final int intValue9 = bigInteger.mod(BigInteger.valueOf(1070560157)).intValue();
        if (intValue9 % 173 == 0 || intValue9 % 179 == 0 || intValue9 % 181 == 0 || intValue9 % 191 == 0) {
            return true;
        }
        final int intValue10 = bigInteger.mod(BigInteger.valueOf(1596463769)).intValue();
        return intValue10 % 193 == 0 || intValue10 % 197 == 0 || intValue10 % 199 == 0 || intValue10 % 211 == 0;
    }
    
    private static boolean implMRProbablePrimeToBase(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final int n, final BigInteger bigInteger4) {
        BigInteger bigInteger5 = bigInteger4.modPow(bigInteger3, bigInteger);
        if (bigInteger5.equals(Primes.ONE) || bigInteger5.equals(bigInteger2)) {
            return true;
        }
        boolean b = false;
        for (int i = 1; i < n; ++i) {
            bigInteger5 = bigInteger5.modPow(Primes.TWO, bigInteger);
            if (bigInteger5.equals(bigInteger2)) {
                b = true;
                break;
            }
            if (bigInteger5.equals(Primes.ONE)) {
                return false;
            }
        }
        return b;
    }
    
    private static STOutput implSTRandomPrime(final Digest digest, final int n, byte[] primeSeed) {
        final int digestSize = digest.getDigestSize();
        if (n < 33) {
            int n2 = 0;
            final byte[] array = new byte[digestSize];
            final byte[] array2 = new byte[digestSize];
            while (true) {
                hash(digest, primeSeed, array, 0);
                inc(primeSeed, 1);
                hash(digest, primeSeed, array2, 0);
                inc(primeSeed, 1);
                final int n3 = ((extract32(array) ^ extract32(array2)) & -1 >>> 32 - n) | (1 << n - 1 | 0x1);
                ++n2;
                final long n4 = (long)n3 & 0xFFFFFFFFL;
                if (isPrime32(n4)) {
                    return new STOutput(BigInteger.valueOf(n4), primeSeed, n2);
                }
                if (n2 > 4 * n) {
                    throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
                }
            }
        }
        else {
            final STOutput implSTRandomPrime = implSTRandomPrime(digest, (n + 3) / 2, primeSeed);
            final BigInteger prime = implSTRandomPrime.getPrime();
            primeSeed = implSTRandomPrime.getPrimeSeed();
            int primeGenCounter = implSTRandomPrime.getPrimeGenCounter();
            final int n5 = (n - 1) / (8 * digestSize);
            final int n6 = primeGenCounter;
            final BigInteger setBit = hashGen(digest, primeSeed, n5 + 1).mod(Primes.ONE.shiftLeft(n - 1)).setBit(n - 1);
            final BigInteger shiftLeft = prime.shiftLeft(1);
            BigInteger bigInteger = setBit.subtract(Primes.ONE).divide(shiftLeft).add(Primes.ONE).shiftLeft(1);
            int n7 = 0;
            BigInteger bigInteger2 = bigInteger.multiply(prime).add(Primes.ONE);
            while (true) {
                if (bigInteger2.bitLength() > n) {
                    bigInteger = Primes.ONE.shiftLeft(n - 1).subtract(Primes.ONE).divide(shiftLeft).add(Primes.ONE).shiftLeft(1);
                    bigInteger2 = bigInteger.multiply(prime).add(Primes.ONE);
                }
                ++primeGenCounter;
                if (!implHasAnySmallFactors(bigInteger2)) {
                    final BigInteger add = hashGen(digest, primeSeed, n5 + 1).mod(bigInteger2.subtract(Primes.THREE)).add(Primes.TWO);
                    bigInteger = bigInteger.add(BigInteger.valueOf(n7));
                    n7 = 0;
                    final BigInteger modPow = add.modPow(bigInteger, bigInteger2);
                    if (bigInteger2.gcd(modPow.subtract(Primes.ONE)).equals(Primes.ONE) && modPow.modPow(prime, bigInteger2).equals(Primes.ONE)) {
                        return new STOutput(bigInteger2, primeSeed, primeGenCounter);
                    }
                }
                else {
                    inc(primeSeed, n5 + 1);
                }
                if (primeGenCounter >= 4 * n + n6) {
                    throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
                }
                n7 += 2;
                bigInteger2 = bigInteger2.add(shiftLeft);
            }
        }
    }
    
    private static int extract32(final byte[] array) {
        int n = 0;
        for (int min = Math.min(4, array.length), i = 0; i < min; ++i) {
            n |= (array[array.length - (i + 1)] & 0xFF) << 8 * i;
        }
        return n;
    }
    
    private static void hash(final Digest digest, final byte[] array, final byte[] array2, final int n) {
        digest.update(array, 0, array.length);
        digest.doFinal(array2, n);
    }
    
    private static BigInteger hashGen(final Digest digest, final byte[] array, final int n) {
        final int digestSize = digest.getDigestSize();
        int n2 = n * digestSize;
        final byte[] array2 = new byte[n2];
        for (int i = 0; i < n; ++i) {
            n2 -= digestSize;
            hash(digest, array, array2, n2);
            inc(array, 1);
        }
        return new BigInteger(1, array2);
    }
    
    private static void inc(final byte[] array, int n) {
        for (int length = array.length; n > 0 && --length >= 0; n += (array[length] & 0xFF), array[length] = (byte)n, n >>>= 8) {}
    }
    
    private static boolean isPrime32(final long n) {
        if (n >>> 32 != 0L) {
            throw new IllegalArgumentException("Size limit exceeded");
        }
        if (n <= 5L) {
            return n == 2L || n == 3L || n == 5L;
        }
        if ((n & 0x1L) == 0x0L || n % 3L == 0L || n % 5L == 0L) {
            return false;
        }
        final long[] array = { 1L, 7L, 11L, 13L, 17L, 19L, 23L, 29L };
        long n2 = 0L;
        int n3 = 1;
        while (true) {
            if (n3 < array.length) {
                if (n % (n2 + array[n3]) == 0L) {
                    return n < 30L;
                }
                ++n3;
            }
            else {
                n2 += 30L;
                if (n2 * n2 >= n) {
                    return true;
                }
                n3 = 0;
            }
        }
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
        THREE = BigInteger.valueOf(3L);
    }
    
    public static class MROutput
    {
        private boolean provablyComposite;
        private BigInteger factor;
        
        private static MROutput probablyPrime() {
            return new MROutput(false, null);
        }
        
        private static MROutput provablyCompositeWithFactor(final BigInteger bigInteger) {
            return new MROutput(true, bigInteger);
        }
        
        private static MROutput provablyCompositeNotPrimePower() {
            return new MROutput(true, null);
        }
        
        private MROutput(final boolean provablyComposite, final BigInteger factor) {
            this.provablyComposite = provablyComposite;
            this.factor = factor;
        }
        
        public BigInteger getFactor() {
            return this.factor;
        }
        
        public boolean isProvablyComposite() {
            return this.provablyComposite;
        }
        
        public boolean isNotPrimePower() {
            return this.provablyComposite && this.factor == null;
        }
    }
    
    public static class STOutput
    {
        private BigInteger prime;
        private byte[] primeSeed;
        private int primeGenCounter;
        
        private STOutput(final BigInteger prime, final byte[] primeSeed, final int primeGenCounter) {
            this.prime = prime;
            this.primeSeed = primeSeed;
            this.primeGenCounter = primeGenCounter;
        }
        
        public BigInteger getPrime() {
            return this.prime;
        }
        
        public byte[] getPrimeSeed() {
            return this.primeSeed;
        }
        
        public int getPrimeGenCounter() {
            return this.primeGenCounter;
        }
    }
}
