package org.bouncycastle.crypto.generators;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.WNafUtil;
import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

class DHParametersHelper
{
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    
    static BigInteger[] generateSafePrimes(final int n, final int n2, final SecureRandom secureRandom) {
        final int n3 = n - 1;
        final int n4 = n >>> 2;
        BigInteger bigInteger;
        BigInteger add;
        while (true) {
            bigInteger = new BigInteger(n3, 2, secureRandom);
            add = bigInteger.shiftLeft(1).add(DHParametersHelper.ONE);
            if (!add.isProbablePrime(n2)) {
                continue;
            }
            if (n2 > 2 && !bigInteger.isProbablePrime(n2 - 2)) {
                continue;
            }
            if (WNafUtil.getNafWeight(add) < n4) {
                continue;
            }
            break;
        }
        return new BigInteger[] { add, bigInteger };
    }
    
    static BigInteger selectGenerator(final BigInteger bigInteger, final BigInteger bigInteger2, final SecureRandom secureRandom) {
        final BigInteger subtract = bigInteger.subtract(DHParametersHelper.TWO);
        BigInteger modPow;
        do {
            modPow = BigIntegers.createRandomInRange(DHParametersHelper.TWO, subtract, secureRandom).modPow(DHParametersHelper.TWO, bigInteger);
        } while (modPow.equals(DHParametersHelper.ONE));
        return modPow;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
    }
}
