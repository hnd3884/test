package org.bouncycastle.crypto.generators;

import java.util.Random;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.math.BigInteger;

public class RSABlindingFactorGenerator
{
    private static BigInteger ZERO;
    private static BigInteger ONE;
    private RSAKeyParameters key;
    private SecureRandom random;
    
    public void init(final CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.key = (RSAKeyParameters)parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        }
        else {
            this.key = (RSAKeyParameters)cipherParameters;
            this.random = new SecureRandom();
        }
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("generator requires RSA public key");
        }
    }
    
    public BigInteger generateBlindingFactor() {
        if (this.key == null) {
            throw new IllegalStateException("generator not initialised");
        }
        final BigInteger modulus = this.key.getModulus();
        final int n = modulus.bitLength() - 1;
        BigInteger bigInteger;
        BigInteger gcd;
        do {
            bigInteger = new BigInteger(n, this.random);
            gcd = bigInteger.gcd(modulus);
        } while (bigInteger.equals(RSABlindingFactorGenerator.ZERO) || bigInteger.equals(RSABlindingFactorGenerator.ONE) || !gcd.equals(RSABlindingFactorGenerator.ONE));
        return bigInteger;
    }
    
    static {
        RSABlindingFactorGenerator.ZERO = BigInteger.valueOf(0L);
        RSABlindingFactorGenerator.ONE = BigInteger.valueOf(1L);
    }
}
