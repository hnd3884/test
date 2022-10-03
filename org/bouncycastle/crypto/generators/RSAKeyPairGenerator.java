package org.bouncycastle.crypto.generators;

import org.bouncycastle.math.Primes;
import java.util.Random;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private static final BigInteger ONE;
    private RSAKeyGenerationParameters param;
    private int iterations;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (RSAKeyGenerationParameters)keyGenerationParameters;
        this.iterations = getNumberOfIterations(this.param.getStrength(), this.param.getCertainty());
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = null;
        int i = 0;
        final int strength = this.param.getStrength();
        final int n = (strength + 1) / 2;
        final int n2 = strength - n;
        int n3 = strength / 2 - 100;
        if (n3 < strength / 3) {
            n3 = strength / 3;
        }
        final int n4 = strength >> 2;
        final BigInteger pow = BigInteger.valueOf(2L).pow(strength / 2);
        final BigInteger shiftLeft = RSAKeyPairGenerator.ONE.shiftLeft(strength - 1);
        final BigInteger shiftLeft2 = RSAKeyPairGenerator.ONE.shiftLeft(n3);
        while (i == 0) {
            final BigInteger publicExponent = this.param.getPublicExponent();
            BigInteger bigInteger = this.chooseRandomPrime(n, publicExponent, shiftLeft);
            BigInteger chooseRandomPrime;
            BigInteger multiply;
            while (true) {
                chooseRandomPrime = this.chooseRandomPrime(n2, publicExponent, shiftLeft);
                final BigInteger abs = chooseRandomPrime.subtract(bigInteger).abs();
                if (abs.bitLength() >= n3) {
                    if (abs.compareTo(shiftLeft2) <= 0) {
                        continue;
                    }
                    multiply = bigInteger.multiply(chooseRandomPrime);
                    if (multiply.bitLength() != strength) {
                        bigInteger = bigInteger.max(chooseRandomPrime);
                    }
                    else {
                        if (WNafUtil.getNafWeight(multiply) >= n4) {
                            break;
                        }
                        bigInteger = this.chooseRandomPrime(n, publicExponent, shiftLeft);
                    }
                }
            }
            if (bigInteger.compareTo(chooseRandomPrime) < 0) {
                final BigInteger bigInteger2 = bigInteger;
                bigInteger = chooseRandomPrime;
                chooseRandomPrime = bigInteger2;
            }
            final BigInteger subtract = bigInteger.subtract(RSAKeyPairGenerator.ONE);
            final BigInteger subtract2 = chooseRandomPrime.subtract(RSAKeyPairGenerator.ONE);
            final BigInteger modInverse = publicExponent.modInverse(subtract.divide(subtract.gcd(subtract2)).multiply(subtract2));
            if (modInverse.compareTo(pow) <= 0) {
                continue;
            }
            i = 1;
            asymmetricCipherKeyPair = new AsymmetricCipherKeyPair(new RSAKeyParameters(false, multiply, publicExponent), new RSAPrivateCrtKeyParameters(multiply, publicExponent, modInverse, bigInteger, chooseRandomPrime, modInverse.remainder(subtract), modInverse.remainder(subtract2), chooseRandomPrime.modInverse(bigInteger)));
        }
        return asymmetricCipherKeyPair;
    }
    
    protected BigInteger chooseRandomPrime(final int n, final BigInteger bigInteger, final BigInteger bigInteger2) {
        for (int i = 0; i != 5 * n; ++i) {
            final BigInteger bigInteger3 = new BigInteger(n, 1, this.param.getRandom());
            if (!bigInteger3.mod(bigInteger).equals(RSAKeyPairGenerator.ONE)) {
                if (bigInteger3.multiply(bigInteger3).compareTo(bigInteger2) >= 0) {
                    if (this.isProbablePrime(bigInteger3)) {
                        if (bigInteger.gcd(bigInteger3.subtract(RSAKeyPairGenerator.ONE)).equals(RSAKeyPairGenerator.ONE)) {
                            return bigInteger3;
                        }
                    }
                }
            }
        }
        throw new IllegalStateException("unable to generate prime number for RSA key");
    }
    
    protected boolean isProbablePrime(final BigInteger bigInteger) {
        return !Primes.hasAnySmallFactors(bigInteger) && Primes.isMRProbablePrime(bigInteger, this.param.getRandom(), this.iterations);
    }
    
    private static int getNumberOfIterations(final int n, final int n2) {
        if (n >= 1536) {
            return (n2 <= 100) ? 3 : ((n2 <= 128) ? 4 : (4 + (n2 - 128 + 1) / 2));
        }
        if (n >= 1024) {
            return (n2 <= 100) ? 4 : ((n2 <= 112) ? 5 : (5 + (n2 - 112 + 1) / 2));
        }
        if (n >= 512) {
            return (n2 <= 80) ? 5 : ((n2 <= 100) ? 7 : (7 + (n2 - 100 + 1) / 2));
        }
        return (n2 <= 80) ? 40 : (40 + (n2 - 80 + 1) / 2);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
