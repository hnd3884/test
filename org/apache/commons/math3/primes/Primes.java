package org.apache.commons.math3.primes;

import java.util.List;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class Primes
{
    private Primes() {
    }
    
    public static boolean isPrime(final int n) {
        if (n < 2) {
            return false;
        }
        for (final int p : SmallPrimes.PRIMES) {
            if (0 == n % p) {
                return n == p;
            }
        }
        return SmallPrimes.millerRabinPrimeTest(n);
    }
    
    public static int nextPrime(int n) {
        if (n < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL, new Object[] { n, 0 });
        }
        if (n == 2) {
            return 2;
        }
        n |= 0x1;
        if (n == 1) {
            return 2;
        }
        if (isPrime(n)) {
            return n;
        }
        final int rem = n % 3;
        if (0 == rem) {
            n += 2;
        }
        else if (1 == rem) {
            n += 4;
        }
        while (!isPrime(n)) {
            n += 2;
            if (isPrime(n)) {
                return n;
            }
            n += 4;
        }
        return n;
    }
    
    public static List<Integer> primeFactors(final int n) {
        if (n < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.NUMBER_TOO_SMALL, new Object[] { n, 2 });
        }
        return SmallPrimes.trialDivision(n);
    }
}
