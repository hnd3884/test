package org.bouncycastle.pqc.math.linearalgebra;

public final class PolynomialRingGF2
{
    private PolynomialRingGF2() {
    }
    
    public static int add(final int n, final int n2) {
        return n ^ n2;
    }
    
    public static long multiply(int i, final int n) {
        long n2 = 0L;
        if (n != 0) {
            for (long n3 = (long)n & 0xFFFFFFFFL; i != 0; i >>>= 1, n3 <<= 1) {
                if ((byte)(i & 0x1) == 1) {
                    n2 ^= n3;
                }
            }
        }
        return n2;
    }
    
    public static int modMultiply(final int n, final int n2, final int n3) {
        int n4 = 0;
        int i = remainder(n, n3);
        int remainder = remainder(n2, n3);
        if (remainder != 0) {
            final int n5 = 1 << degree(n3);
            while (i != 0) {
                if ((byte)(i & 0x1) == 1) {
                    n4 ^= remainder;
                }
                i >>>= 1;
                remainder <<= 1;
                if (remainder >= n5) {
                    remainder ^= n3;
                }
            }
        }
        return n4;
    }
    
    public static int degree(int i) {
        int n = -1;
        while (i != 0) {
            ++n;
            i >>>= 1;
        }
        return n;
    }
    
    public static int degree(long n) {
        int n2 = 0;
        while (n != 0L) {
            ++n2;
            n >>>= 1;
        }
        return n2 - 1;
    }
    
    public static int remainder(final int n, final int n2) {
        int n3 = n;
        if (n2 == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        while (degree(n3) >= degree(n2)) {
            n3 ^= n2 << degree(n3) - degree(n2);
        }
        return n3;
    }
    
    public static int rest(final long n, final int n2) {
        long n3 = n;
        if (n2 == 0) {
            System.err.println("Error: to be divided by 0");
            return 0;
        }
        for (long n4 = (long)n2 & 0xFFFFFFFFL; n3 >>> 32 != 0L; n3 ^= n4 << degree(n3) - degree(n4)) {}
        int n5;
        for (n5 = (int)(n3 & -1L); degree(n5) >= degree(n2); n5 ^= n2 << degree(n5) - degree(n2)) {}
        return n5;
    }
    
    public static int gcd(final int n, final int n2) {
        int n3 = n;
        int remainder;
        for (int i = n2; i != 0; i = remainder) {
            remainder = remainder(n3, i);
            n3 = i;
        }
        return n3;
    }
    
    public static boolean isIrreducible(final int n) {
        if (n == 0) {
            return false;
        }
        final int n2 = degree(n) >>> 1;
        int modMultiply = 2;
        for (int i = 0; i < n2; ++i) {
            modMultiply = modMultiply(modMultiply, modMultiply, n);
            if (gcd(modMultiply ^ 0x2, n) != 1) {
                return false;
            }
        }
        return true;
    }
    
    public static int getIrreduciblePolynomial(final int n) {
        if (n < 0) {
            System.err.println("The Degree is negative");
            return 0;
        }
        if (n > 31) {
            System.err.println("The Degree is more then 31");
            return 0;
        }
        if (n == 0) {
            return 1;
        }
        int n2 = 1 << n;
        ++n2;
        for (int n3 = 1 << n + 1, i = n2; i < n3; i += 2) {
            if (isIrreducible(i)) {
                return i;
            }
        }
        return 0;
    }
}
