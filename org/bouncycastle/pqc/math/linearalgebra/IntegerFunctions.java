package org.bouncycastle.pqc.math.linearalgebra;

import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

public final class IntegerFunctions
{
    private static final BigInteger ZERO;
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    private static final BigInteger FOUR;
    private static final int[] SMALL_PRIMES;
    private static final long SMALL_PRIME_PRODUCT = 152125131763605L;
    private static SecureRandom sr;
    private static final int[] jacobiTable;
    
    private IntegerFunctions() {
    }
    
    public static int jacobi(final BigInteger bigInteger, final BigInteger bigInteger2) {
        long n = 1L;
        if (bigInteger2.equals(IntegerFunctions.ZERO)) {
            return bigInteger.abs().equals(IntegerFunctions.ONE) ? 1 : 0;
        }
        if (!bigInteger.testBit(0) && !bigInteger2.testBit(0)) {
            return 0;
        }
        BigInteger bigInteger3 = bigInteger;
        BigInteger bigInteger4 = bigInteger2;
        if (bigInteger4.signum() == -1) {
            bigInteger4 = bigInteger4.negate();
            if (bigInteger3.signum() == -1) {
                n = -1L;
            }
        }
        BigInteger bigInteger5 = IntegerFunctions.ZERO;
        while (!bigInteger4.testBit(0)) {
            bigInteger5 = bigInteger5.add(IntegerFunctions.ONE);
            bigInteger4 = bigInteger4.divide(IntegerFunctions.TWO);
        }
        if (bigInteger5.testBit(0)) {
            n *= IntegerFunctions.jacobiTable[bigInteger3.intValue() & 0x7];
        }
        if (bigInteger3.signum() < 0) {
            if (bigInteger4.testBit(1)) {
                n = -n;
            }
            bigInteger3 = bigInteger3.negate();
        }
        while (bigInteger3.signum() != 0) {
            BigInteger bigInteger6 = IntegerFunctions.ZERO;
            while (!bigInteger3.testBit(0)) {
                bigInteger6 = bigInteger6.add(IntegerFunctions.ONE);
                bigInteger3 = bigInteger3.divide(IntegerFunctions.TWO);
            }
            if (bigInteger6.testBit(0)) {
                n *= IntegerFunctions.jacobiTable[bigInteger4.intValue() & 0x7];
            }
            if (bigInteger3.compareTo(bigInteger4) < 0) {
                final BigInteger bigInteger7 = bigInteger3;
                bigInteger3 = bigInteger4;
                bigInteger4 = bigInteger7;
                if (bigInteger3.testBit(1) && bigInteger4.testBit(1)) {
                    n = -n;
                }
            }
            bigInteger3 = bigInteger3.subtract(bigInteger4);
        }
        return bigInteger4.equals(IntegerFunctions.ONE) ? ((int)n) : 0;
    }
    
    public static BigInteger ressol(BigInteger add, final BigInteger bigInteger) throws IllegalArgumentException {
        if (add.compareTo(IntegerFunctions.ZERO) < 0) {
            add = add.add(bigInteger);
        }
        if (add.equals(IntegerFunctions.ZERO)) {
            return IntegerFunctions.ZERO;
        }
        if (bigInteger.equals(IntegerFunctions.TWO)) {
            return add;
        }
        if (bigInteger.testBit(0) && bigInteger.testBit(1)) {
            if (jacobi(add, bigInteger) == 1) {
                return add.modPow(bigInteger.add(IntegerFunctions.ONE).shiftRight(2), bigInteger);
            }
            throw new IllegalArgumentException("No quadratic residue: " + add + ", " + bigInteger);
        }
        else {
            BigInteger bigInteger2 = bigInteger.subtract(IntegerFunctions.ONE);
            long n = 0L;
            while (!bigInteger2.testBit(0)) {
                ++n;
                bigInteger2 = bigInteger2.shiftRight(1);
            }
            final BigInteger shiftRight = bigInteger2.subtract(IntegerFunctions.ONE).shiftRight(1);
            final BigInteger modPow = add.modPow(shiftRight, bigInteger);
            BigInteger bigInteger3 = modPow.multiply(modPow).remainder(bigInteger).multiply(add).remainder(bigInteger);
            BigInteger bigInteger4 = modPow.multiply(add).remainder(bigInteger);
            if (bigInteger3.equals(IntegerFunctions.ONE)) {
                return bigInteger4;
            }
            BigInteger bigInteger5;
            for (bigInteger5 = IntegerFunctions.TWO; jacobi(bigInteger5, bigInteger) == 1; bigInteger5 = bigInteger5.add(IntegerFunctions.ONE)) {}
            for (BigInteger bigInteger6 = bigInteger5.modPow(shiftRight.multiply(IntegerFunctions.TWO).add(IntegerFunctions.ONE), bigInteger); bigInteger3.compareTo(IntegerFunctions.ONE) == 1; bigInteger3 = bigInteger3.multiply(bigInteger6).mod(bigInteger)) {
                BigInteger mod = bigInteger3;
                final long n2 = n;
                for (n = 0L; !mod.equals(IntegerFunctions.ONE); mod = mod.multiply(mod).mod(bigInteger), ++n) {}
                final long n3 = n2 - n;
                if (n3 == 0L) {
                    throw new IllegalArgumentException("No quadratic residue: " + add + ", " + bigInteger);
                }
                BigInteger bigInteger7 = IntegerFunctions.ONE;
                for (long n4 = 0L; n4 < n3 - 1L; ++n4) {
                    bigInteger7 = bigInteger7.shiftLeft(1);
                }
                final BigInteger modPow2 = bigInteger6.modPow(bigInteger7, bigInteger);
                bigInteger4 = bigInteger4.multiply(modPow2).remainder(bigInteger);
                bigInteger6 = modPow2.multiply(modPow2).remainder(bigInteger);
            }
            return bigInteger4;
        }
    }
    
    public static int gcd(final int n, final int n2) {
        return BigInteger.valueOf(n).gcd(BigInteger.valueOf(n2)).intValue();
    }
    
    public static int[] extGCD(final int n, final int n2) {
        final BigInteger[] extgcd = extgcd(BigInteger.valueOf(n), BigInteger.valueOf(n2));
        return new int[] { extgcd[0].intValue(), extgcd[1].intValue(), extgcd[2].intValue() };
    }
    
    public static BigInteger divideAndRound(final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger.signum() < 0) {
            return divideAndRound(bigInteger.negate(), bigInteger2).negate();
        }
        if (bigInteger2.signum() < 0) {
            return divideAndRound(bigInteger, bigInteger2.negate()).negate();
        }
        return bigInteger.shiftLeft(1).add(bigInteger2).divide(bigInteger2.shiftLeft(1));
    }
    
    public static BigInteger[] divideAndRound(final BigInteger[] array, final BigInteger bigInteger) {
        final BigInteger[] array2 = new BigInteger[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = divideAndRound(array[i], bigInteger);
        }
        return array2;
    }
    
    public static int ceilLog(final BigInteger bigInteger) {
        int n = 0;
        for (BigInteger bigInteger2 = IntegerFunctions.ONE; bigInteger2.compareTo(bigInteger) < 0; bigInteger2 = bigInteger2.shiftLeft(1)) {
            ++n;
        }
        return n;
    }
    
    public static int ceilLog(final int n) {
        int n2 = 0;
        for (int i = 1; i < n; i <<= 1, ++n2) {}
        return n2;
    }
    
    public static int ceilLog256(final int n) {
        if (n == 0) {
            return 1;
        }
        int i;
        if (n < 0) {
            i = -n;
        }
        else {
            i = n;
        }
        int n2 = 0;
        while (i > 0) {
            ++n2;
            i >>>= 8;
        }
        return n2;
    }
    
    public static int ceilLog256(final long n) {
        if (n == 0L) {
            return 1;
        }
        long n2;
        if (n < 0L) {
            n2 = -n;
        }
        else {
            n2 = n;
        }
        int n3 = 0;
        while (n2 > 0L) {
            ++n3;
            n2 >>>= 8;
        }
        return n3;
    }
    
    public static int floorLog(final BigInteger bigInteger) {
        int n = -1;
        for (BigInteger bigInteger2 = IntegerFunctions.ONE; bigInteger2.compareTo(bigInteger) <= 0; bigInteger2 = bigInteger2.shiftLeft(1)) {
            ++n;
        }
        return n;
    }
    
    public static int floorLog(final int n) {
        int n2 = 0;
        if (n <= 0) {
            return -1;
        }
        for (int i = n >>> 1; i > 0; i >>>= 1) {
            ++n2;
        }
        return n2;
    }
    
    public static int maxPower(final int n) {
        int n2 = 0;
        if (n != 0) {
            for (int n3 = 1; (n & n3) == 0x0; n3 <<= 1) {
                ++n2;
            }
        }
        return n2;
    }
    
    public static int bitCount(int i) {
        int n = 0;
        while (i != 0) {
            n += (i & 0x1);
            i >>>= 1;
        }
        return n;
    }
    
    public static int order(final int n, final int n2) {
        int i = n % n2;
        int n3 = 1;
        if (i == 0) {
            throw new IllegalArgumentException(n + " is not an element of Z/(" + n2 + "Z)^*; it is not meaningful to compute its order.");
        }
        while (i != 1) {
            i = i * n % n2;
            if (i < 0) {
                i += n2;
            }
            ++n3;
        }
        return n3;
    }
    
    public static BigInteger reduceInto(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger.subtract(bigInteger2).mod(bigInteger3.subtract(bigInteger2)).add(bigInteger2);
    }
    
    public static int pow(int n, int i) {
        int n2 = 1;
        while (i > 0) {
            if ((i & 0x1) == 0x1) {
                n2 *= n;
            }
            n *= n;
            i >>>= 1;
        }
        return n2;
    }
    
    public static long pow(long n, int i) {
        long n2 = 1L;
        while (i > 0) {
            if ((i & 0x1) == 0x1) {
                n2 *= n;
            }
            n *= n;
            i >>>= 1;
        }
        return n2;
    }
    
    public static int modPow(int n, int i, final int n2) {
        if (n2 <= 0 || n2 * n2 > Integer.MAX_VALUE || i < 0) {
            return 0;
        }
        int n3 = 1;
        n = (n % n2 + n2) % n2;
        while (i > 0) {
            if ((i & 0x1) == 0x1) {
                n3 = n3 * n % n2;
            }
            n = n * n % n2;
            i >>>= 1;
        }
        return n3;
    }
    
    public static BigInteger[] extgcd(final BigInteger bigInteger, final BigInteger bigInteger2) {
        BigInteger one = IntegerFunctions.ONE;
        BigInteger bigInteger3 = IntegerFunctions.ZERO;
        BigInteger bigInteger4 = bigInteger;
        if (bigInteger2.signum() != 0) {
            BigInteger zero = IntegerFunctions.ZERO;
            BigInteger bigInteger7;
            for (BigInteger bigInteger5 = bigInteger2; bigInteger5.signum() != 0; bigInteger5 = bigInteger7) {
                final BigInteger[] divideAndRemainder = bigInteger4.divideAndRemainder(bigInteger5);
                final BigInteger bigInteger6 = divideAndRemainder[0];
                bigInteger7 = divideAndRemainder[1];
                final BigInteger subtract = one.subtract(bigInteger6.multiply(zero));
                one = zero;
                bigInteger4 = bigInteger5;
                zero = subtract;
            }
            bigInteger3 = bigInteger4.subtract(bigInteger.multiply(one)).divide(bigInteger2);
        }
        return new BigInteger[] { bigInteger4, one, bigInteger3 };
    }
    
    public static BigInteger leastCommonMultiple(final BigInteger[] array) {
        final int length = array.length;
        BigInteger divide = array[0];
        for (int i = 1; i < length; ++i) {
            divide = divide.multiply(array[i]).divide(divide.gcd(array[i]));
        }
        return divide;
    }
    
    public static long mod(final long n, final long n2) {
        long n3 = n % n2;
        if (n3 < 0L) {
            n3 += n2;
        }
        return n3;
    }
    
    public static int modInverse(final int n, final int n2) {
        return BigInteger.valueOf(n).modInverse(BigInteger.valueOf(n2)).intValue();
    }
    
    public static long modInverse(final long n, final long n2) {
        return BigInteger.valueOf(n).modInverse(BigInteger.valueOf(n2)).longValue();
    }
    
    public static int isPower(final int n, final int n2) {
        if (n <= 0) {
            return -1;
        }
        int n3 = 0;
        for (int i = n; i > 1; i /= n2, ++n3) {
            if (i % n2 != 0) {
                return -1;
            }
        }
        return n3;
    }
    
    public static int leastDiv(int n) {
        if (n < 0) {
            n = -n;
        }
        if (n == 0) {
            return 1;
        }
        if ((n & 0x1) == 0x0) {
            return 2;
        }
        for (int i = 3; i <= n / i; i += 2) {
            if (n % i == 0) {
                return i;
            }
        }
        return n;
    }
    
    public static boolean isPrime(final int n) {
        if (n < 2) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if ((n & 0x1) == 0x0) {
            return false;
        }
        if (n < 42) {
            for (int i = 0; i < IntegerFunctions.SMALL_PRIMES.length; ++i) {
                if (n == IntegerFunctions.SMALL_PRIMES[i]) {
                    return true;
                }
            }
        }
        return n % 3 != 0 && n % 5 != 0 && n % 7 != 0 && n % 11 != 0 && n % 13 != 0 && n % 17 != 0 && n % 19 != 0 && n % 23 != 0 && n % 29 != 0 && n % 31 != 0 && n % 37 != 0 && n % 41 != 0 && BigInteger.valueOf(n).isProbablePrime(20);
    }
    
    public static boolean passesSmallPrimeTest(final BigInteger bigInteger) {
        final int[] array = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093, 1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153, 1163, 1171, 1181, 1187, 1193, 1201, 1213, 1217, 1223, 1229, 1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289, 1291, 1297, 1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367, 1373, 1381, 1399, 1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 1453, 1459, 1471, 1481, 1483, 1487, 1489, 1493, 1499 };
        for (int i = 0; i < array.length; ++i) {
            if (bigInteger.mod(BigInteger.valueOf(array[i])).equals(IntegerFunctions.ZERO)) {
                return false;
            }
        }
        return true;
    }
    
    public static int nextSmallerPrime(int n) {
        if (n <= 2) {
            return 1;
        }
        if (n == 3) {
            return 2;
        }
        if ((n & 0x1) == 0x0) {
            --n;
        }
        else {
            n -= 2;
        }
        while (n > 3 & !isPrime(n)) {
            n -= 2;
        }
        return n;
    }
    
    public static BigInteger nextProbablePrime(final BigInteger bigInteger, final int n) {
        if (bigInteger.signum() < 0 || bigInteger.signum() == 0 || bigInteger.equals(IntegerFunctions.ONE)) {
            return IntegerFunctions.TWO;
        }
        BigInteger bigInteger2 = bigInteger.add(IntegerFunctions.ONE);
        if (!bigInteger2.testBit(0)) {
            bigInteger2 = bigInteger2.add(IntegerFunctions.ONE);
        }
        while (true) {
            if (bigInteger2.bitLength() > 6) {
                final long longValue = bigInteger2.remainder(BigInteger.valueOf(152125131763605L)).longValue();
                if (longValue % 3L == 0L || longValue % 5L == 0L || longValue % 7L == 0L || longValue % 11L == 0L || longValue % 13L == 0L || longValue % 17L == 0L || longValue % 19L == 0L || longValue % 23L == 0L || longValue % 29L == 0L || longValue % 31L == 0L || longValue % 37L == 0L || longValue % 41L == 0L) {
                    bigInteger2 = bigInteger2.add(IntegerFunctions.TWO);
                    continue;
                }
            }
            if (bigInteger2.bitLength() < 4) {
                return bigInteger2;
            }
            if (bigInteger2.isProbablePrime(n)) {
                return bigInteger2;
            }
            bigInteger2 = bigInteger2.add(IntegerFunctions.TWO);
        }
    }
    
    public static BigInteger nextProbablePrime(final BigInteger bigInteger) {
        return nextProbablePrime(bigInteger, 20);
    }
    
    public static BigInteger nextPrime(final long n) {
        int n2 = 0;
        long n3 = 0L;
        if (n <= 1L) {
            return BigInteger.valueOf(2L);
        }
        if (n == 2L) {
            return BigInteger.valueOf(3L);
        }
        for (long n4 = n + 1L + (n & 0x1L); n4 <= n << 1 && n2 == 0; n4 += 2L) {
            for (long n5 = 3L; n5 <= n4 >> 1 && n2 == 0; n5 += 2L) {
                if (n4 % n5 == 0L) {
                    n2 = 1;
                }
            }
            if (n2 != 0) {
                n2 = 0;
            }
            else {
                n3 = n4;
                n2 = 1;
            }
        }
        return BigInteger.valueOf(n3);
    }
    
    public static BigInteger binomial(final int n, int n2) {
        BigInteger bigInteger = IntegerFunctions.ONE;
        if (n != 0) {
            if (n2 > n >>> 1) {
                n2 = n - n2;
            }
            for (int i = 1; i <= n2; ++i) {
                bigInteger = bigInteger.multiply(BigInteger.valueOf(n - (i - 1))).divide(BigInteger.valueOf(i));
            }
            return bigInteger;
        }
        if (n2 == 0) {
            return bigInteger;
        }
        return IntegerFunctions.ZERO;
    }
    
    public static BigInteger randomize(final BigInteger bigInteger) {
        if (IntegerFunctions.sr == null) {
            IntegerFunctions.sr = new SecureRandom();
        }
        return randomize(bigInteger, IntegerFunctions.sr);
    }
    
    public static BigInteger randomize(final BigInteger bigInteger, SecureRandom secureRandom) {
        final int bitLength = bigInteger.bitLength();
        BigInteger value = BigInteger.valueOf(0L);
        if (secureRandom == null) {
            secureRandom = ((IntegerFunctions.sr != null) ? IntegerFunctions.sr : new SecureRandom());
        }
        for (int i = 0; i < 20; ++i) {
            value = new BigInteger(bitLength, secureRandom);
            if (value.compareTo(bigInteger) < 0) {
                return value;
            }
        }
        return value.mod(bigInteger);
    }
    
    public static BigInteger squareRoot(final BigInteger bigInteger) {
        if (bigInteger.compareTo(IntegerFunctions.ZERO) < 0) {
            throw new ArithmeticException("cannot extract root of negative number" + bigInteger + ".");
        }
        int i = bigInteger.bitLength();
        BigInteger bigInteger2 = IntegerFunctions.ZERO;
        BigInteger bigInteger3 = IntegerFunctions.ZERO;
        if ((i & 0x1) != 0x0) {
            bigInteger2 = bigInteger2.add(IntegerFunctions.ONE);
            --i;
        }
        while (i > 0) {
            bigInteger3 = bigInteger3.multiply(IntegerFunctions.FOUR).add(BigInteger.valueOf((bigInteger.testBit(--i) ? 2 : 0) + (bigInteger.testBit(--i) ? 1 : 0)));
            final BigInteger add = bigInteger2.multiply(IntegerFunctions.FOUR).add(IntegerFunctions.ONE);
            bigInteger2 = bigInteger2.multiply(IntegerFunctions.TWO);
            if (bigInteger3.compareTo(add) != -1) {
                bigInteger2 = bigInteger2.add(IntegerFunctions.ONE);
                bigInteger3 = bigInteger3.subtract(add);
            }
        }
        return bigInteger2;
    }
    
    public static float intRoot(final int n, final int n2) {
        float n3 = (float)(n / n2);
        float n4 = 0.0f;
        int n5 = 0;
        while (Math.abs(n4 - n3) > 1.0E-4) {
            float n6;
            for (n6 = floatPow(n3, n2); Float.isInfinite(n6); n6 = floatPow(n3, n2)) {
                n3 = (n3 + n4) / 2.0f;
            }
            ++n5;
            n4 = n3;
            n3 = n4 - (n6 - n) / (n2 * floatPow(n4, n2 - 1));
        }
        return n3;
    }
    
    public static float floatPow(final float n, int i) {
        float n2 = 1.0f;
        while (i > 0) {
            n2 *= n;
            --i;
        }
        return n2;
    }
    
    @Deprecated
    public static double log(final double n) {
        if (n > 0.0 && n < 1.0) {
            return -log(1.0 / n);
        }
        int n2 = 0;
        double n3 = 1.0;
        for (double n4 = n; n4 > 2.0; n4 /= 2.0, ++n2, n3 *= 2.0) {}
        return n2 + logBKM(n / n3);
    }
    
    @Deprecated
    public static double log(final long n) {
        final int floorLog = floorLog(BigInteger.valueOf(n));
        return floorLog + logBKM(n / (double)(1 << floorLog));
    }
    
    @Deprecated
    private static double logBKM(final double n) {
        final double[] array = { 1.0, 0.5849625007211562, 0.32192809488736235, 0.16992500144231237, 0.0874628412503394, 0.044394119358453436, 0.02236781302845451, 0.01122725542325412, 0.005624549193878107, 0.0028150156070540383, 0.0014081943928083889, 7.042690112466433E-4, 3.5217748030102726E-4, 1.7609948644250602E-4, 8.80524301221769E-5, 4.4026886827316716E-5, 2.2013611360340496E-5, 1.1006847667481442E-5, 5.503434330648604E-6, 2.751719789561283E-6, 1.375860550841138E-6, 6.879304394358497E-7, 3.4396526072176454E-7, 1.7198264061184464E-7, 8.599132286866321E-8, 4.299566207501687E-8, 2.1497831197679756E-8, 1.0748915638882709E-8, 5.374457829452062E-9, 2.687228917228708E-9, 1.3436144592400231E-9, 6.718072297764289E-10, 3.3590361492731876E-10, 1.6795180747343547E-10, 8.397590373916176E-11, 4.1987951870191886E-11, 2.0993975935248694E-11, 1.0496987967662534E-11, 5.2484939838408146E-12, 2.624246991922794E-12, 1.3121234959619935E-12, 6.56061747981146E-13, 3.2803087399061026E-13, 1.6401543699531447E-13, 8.200771849765956E-14, 4.1003859248830365E-14, 2.0501929624415328E-14, 1.02509648122077E-14, 5.1254824061038595E-15, 2.5627412030519317E-15, 1.2813706015259665E-15, 6.406853007629834E-16, 3.203426503814917E-16, 1.6017132519074588E-16, 8.008566259537294E-17, 4.004283129768647E-17, 2.0021415648843235E-17, 1.0010707824421618E-17, 5.005353912210809E-18, 2.5026769561054044E-18, 1.2513384780527022E-18, 6.256692390263511E-19, 3.1283461951317555E-19, 1.5641730975658778E-19, 7.820865487829389E-20, 3.9104327439146944E-20, 1.9552163719573472E-20, 9.776081859786736E-21, 4.888040929893368E-21, 2.444020464946684E-21, 1.222010232473342E-21, 6.11005116236671E-22, 3.055025581183355E-22, 1.5275127905916775E-22, 7.637563952958387E-23, 3.818781976479194E-23, 1.909390988239597E-23, 9.546954941197984E-24, 4.773477470598992E-24, 2.386738735299496E-24, 1.193369367649748E-24, 5.96684683824874E-25, 2.98342341912437E-25, 1.491711709562185E-25, 7.458558547810925E-26, 3.7292792739054626E-26, 1.8646396369527313E-26, 9.323198184763657E-27, 4.661599092381828E-27, 2.330799546190914E-27, 1.165399773095457E-27, 5.826998865477285E-28, 2.9134994327386427E-28, 1.4567497163693213E-28, 7.283748581846607E-29, 3.6418742909233034E-29, 1.8209371454616517E-29, 9.104685727308258E-30, 4.552342863654129E-30, 2.2761714318270646E-30 };
        final int n2 = 53;
        double n3 = 1.0;
        double n4 = 0.0;
        double n5 = 1.0;
        for (int i = 0; i < n2; ++i) {
            final double n6 = n3 + n3 * n5;
            if (n6 <= n) {
                n3 = n6;
                n4 += array[i];
            }
            n5 *= 0.5;
        }
        return n4;
    }
    
    public static boolean isIncreasing(final int[] array) {
        for (int i = 1; i < array.length; ++i) {
            if (array[i - 1] >= array[i]) {
                System.out.println("a[" + (i - 1) + "] = " + array[i - 1] + " >= " + array[i] + " = a[" + i + "]");
                return false;
            }
        }
        return true;
    }
    
    public static byte[] integerToOctets(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.abs().toByteArray();
        if ((bigInteger.bitLength() & 0x7) != 0x0) {
            return byteArray;
        }
        final byte[] array = new byte[bigInteger.bitLength() >> 3];
        System.arraycopy(byteArray, 1, array, 0, array.length);
        return array;
    }
    
    public static BigInteger octetsToInteger(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2 + 1];
        array2[0] = 0;
        System.arraycopy(array, n, array2, 1, n2);
        return new BigInteger(array2);
    }
    
    public static BigInteger octetsToInteger(final byte[] array) {
        return octetsToInteger(array, 0, array.length);
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
        FOUR = BigInteger.valueOf(4L);
        SMALL_PRIMES = new int[] { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };
        IntegerFunctions.sr = null;
        jacobiTable = new int[] { 0, 1, 0, -1, 0, -1, 0, 1 };
    }
}
