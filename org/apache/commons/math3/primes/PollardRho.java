package org.apache.commons.math3.primes;

import org.apache.commons.math3.util.FastMath;
import java.util.ArrayList;
import java.util.List;

class PollardRho
{
    private PollardRho() {
    }
    
    public static List<Integer> primeFactors(int n) {
        final List<Integer> factors = new ArrayList<Integer>();
        n = SmallPrimes.smallTrialDivision(n, factors);
        if (1 == n) {
            return factors;
        }
        if (SmallPrimes.millerRabinPrimeTest(n)) {
            factors.add(n);
            return factors;
        }
        final int divisor = rhoBrent(n);
        factors.add(divisor);
        factors.add(n / divisor);
        return factors;
    }
    
    static int rhoBrent(final int n) {
        final int x0 = 2;
        final int m = 25;
        int cst = SmallPrimes.PRIMES_LAST;
        int y = 2;
        int r = 1;
        while (true) {
            final int x2 = y;
            for (int i = 0; i < r; ++i) {
                final long y2 = y * (long)y;
                y = (int)((y2 + cst) % n);
            }
            int k = 0;
            do {
                final int bound = FastMath.min(25, r - k);
                int q = 1;
                for (int j = -3; j < bound; ++j) {
                    final long y3 = y * (long)y;
                    y = (int)((y3 + cst) % n);
                    final long divisor = FastMath.abs(x2 - y);
                    if (0L == divisor) {
                        cst += SmallPrimes.PRIMES_LAST;
                        k = -25;
                        y = 2;
                        r = 1;
                        break;
                    }
                    final long prod = divisor * q;
                    q = (int)(prod % n);
                    if (0 == q) {
                        return gcdPositive(FastMath.abs((int)divisor), n);
                    }
                }
                final int out = gcdPositive(FastMath.abs(q), n);
                if (1 != out) {
                    return out;
                }
                k += 25;
            } while (k < r);
            r *= 2;
        }
    }
    
    static int gcdPositive(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        final int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos;
        final int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos;
        final int shift = FastMath.min(aTwos, bTwos);
        while (a != b) {
            final int delta = a - b;
            b = FastMath.min(a, b);
            a = FastMath.abs(delta);
            a >>= Integer.numberOfTrailingZeros(a);
        }
        return a << shift;
    }
}
