package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.ElGamalParameters;
import java.security.SecureRandom;

public class ElGamalParametersGenerator
{
    private int size;
    private int certainty;
    private SecureRandom random;
    
    public void init(final int size, final int certainty, final SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }
    
    public ElGamalParameters generateParameters() {
        final BigInteger[] generateSafePrimes = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        final BigInteger bigInteger = generateSafePrimes[0];
        return new ElGamalParameters(bigInteger, DHParametersHelper.selectGenerator(bigInteger, generateSafePrimes[1], this.random));
    }
}
