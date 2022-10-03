package org.bouncycastle.crypto.generators;

import org.bouncycastle.util.BigIntegers;
import java.util.Random;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import java.security.SecureRandom;
import java.math.BigInteger;

public class CramerShoupParametersGenerator
{
    private static final BigInteger ONE;
    private int size;
    private int certainty;
    private SecureRandom random;
    
    public void init(final int size, final int certainty, final SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }
    
    public CramerShoupParameters generateParameters() {
        BigInteger bigInteger;
        BigInteger selectGenerator;
        BigInteger bigInteger2;
        for (bigInteger = ParametersHelper.generateSafePrimes(this.size, this.certainty, this.random)[1], selectGenerator = ParametersHelper.selectGenerator(bigInteger, this.random), bigInteger2 = ParametersHelper.selectGenerator(bigInteger, this.random); selectGenerator.equals(bigInteger2); bigInteger2 = ParametersHelper.selectGenerator(bigInteger, this.random)) {}
        return new CramerShoupParameters(bigInteger, selectGenerator, bigInteger2, new SHA256Digest());
    }
    
    public CramerShoupParameters generateParameters(final DHParameters dhParameters) {
        BigInteger p;
        BigInteger g;
        BigInteger bigInteger;
        for (p = dhParameters.getP(), g = dhParameters.getG(), bigInteger = ParametersHelper.selectGenerator(p, this.random); g.equals(bigInteger); bigInteger = ParametersHelper.selectGenerator(p, this.random)) {}
        return new CramerShoupParameters(p, g, bigInteger, new SHA256Digest());
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
    
    private static class ParametersHelper
    {
        private static final BigInteger TWO;
        
        static BigInteger[] generateSafePrimes(final int n, final int n2, final SecureRandom secureRandom) {
            final int n3 = n - 1;
            BigInteger add;
            BigInteger bigInteger;
            do {
                bigInteger = new BigInteger(n3, 2, secureRandom);
                add = bigInteger.shiftLeft(1).add(CramerShoupParametersGenerator.ONE);
            } while (!add.isProbablePrime(n2) || (n2 > 2 && !bigInteger.isProbablePrime(n2)));
            return new BigInteger[] { add, bigInteger };
        }
        
        static BigInteger selectGenerator(final BigInteger bigInteger, final SecureRandom secureRandom) {
            final BigInteger subtract = bigInteger.subtract(ParametersHelper.TWO);
            BigInteger modPow;
            do {
                modPow = BigIntegers.createRandomInRange(ParametersHelper.TWO, subtract, secureRandom).modPow(ParametersHelper.TWO, bigInteger);
            } while (modPow.equals(CramerShoupParametersGenerator.ONE));
            return modPow;
        }
        
        static {
            TWO = BigInteger.valueOf(2L);
        }
    }
}
