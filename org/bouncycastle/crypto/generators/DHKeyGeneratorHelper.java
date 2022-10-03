package org.bouncycastle.crypto.generators;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.WNafUtil;
import java.util.Random;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;
import java.math.BigInteger;

class DHKeyGeneratorHelper
{
    static final DHKeyGeneratorHelper INSTANCE;
    private static final BigInteger ONE;
    private static final BigInteger TWO;
    
    private DHKeyGeneratorHelper() {
    }
    
    BigInteger calculatePrivate(final DHParameters dhParameters, final SecureRandom secureRandom) {
        final int l = dhParameters.getL();
        if (l != 0) {
            final int n = l >>> 2;
            BigInteger setBit;
            do {
                setBit = new BigInteger(l, secureRandom).setBit(l - 1);
            } while (WNafUtil.getNafWeight(setBit) < n);
            return setBit;
        }
        BigInteger bigInteger = DHKeyGeneratorHelper.TWO;
        final int m = dhParameters.getM();
        if (m != 0) {
            bigInteger = DHKeyGeneratorHelper.ONE.shiftLeft(m - 1);
        }
        BigInteger bigInteger2 = dhParameters.getQ();
        if (bigInteger2 == null) {
            bigInteger2 = dhParameters.getP();
        }
        final BigInteger subtract = bigInteger2.subtract(DHKeyGeneratorHelper.TWO);
        final int n2 = subtract.bitLength() >>> 2;
        BigInteger randomInRange;
        do {
            randomInRange = BigIntegers.createRandomInRange(bigInteger, subtract, secureRandom);
        } while (WNafUtil.getNafWeight(randomInRange) < n2);
        return randomInRange;
    }
    
    BigInteger calculatePublic(final DHParameters dhParameters, final BigInteger bigInteger) {
        return dhParameters.getG().modPow(bigInteger, dhParameters.getP());
    }
    
    static {
        INSTANCE = new DHKeyGeneratorHelper();
        ONE = BigInteger.valueOf(1L);
        TWO = BigInteger.valueOf(2L);
    }
}
