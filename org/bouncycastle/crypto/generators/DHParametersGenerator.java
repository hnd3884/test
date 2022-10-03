package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import java.math.BigInteger;
import java.security.SecureRandom;

public class DHParametersGenerator
{
    private int size;
    private int certainty;
    private SecureRandom random;
    private static final BigInteger TWO;
    
    public void init(final int size, final int certainty, final SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }
    
    public DHParameters generateParameters() {
        final BigInteger[] generateSafePrimes = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        final BigInteger bigInteger = generateSafePrimes[0];
        final BigInteger bigInteger2 = generateSafePrimes[1];
        return new DHParameters(bigInteger, DHParametersHelper.selectGenerator(bigInteger, bigInteger2, this.random), bigInteger2, DHParametersGenerator.TWO, null);
    }
    
    static {
        TWO = BigInteger.valueOf(2L);
    }
}
