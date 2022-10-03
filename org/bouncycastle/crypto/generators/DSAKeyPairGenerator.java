package org.bouncycastle.crypto.generators;

import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class DSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private static final BigInteger ONE;
    private DSAKeyGenerationParameters param;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (DSAKeyGenerationParameters)keyGenerationParameters;
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final DSAParameters parameters = this.param.getParameters();
        final BigInteger generatePrivateKey = generatePrivateKey(parameters.getQ(), this.param.getRandom());
        return new AsymmetricCipherKeyPair(new DSAPublicKeyParameters(calculatePublicKey(parameters.getP(), parameters.getG(), generatePrivateKey), parameters), new DSAPrivateKeyParameters(generatePrivateKey, parameters));
    }
    
    private static BigInteger generatePrivateKey(final BigInteger bigInteger, final SecureRandom secureRandom) {
        final int n = bigInteger.bitLength() >>> 2;
        BigInteger randomInRange;
        do {
            randomInRange = BigIntegers.createRandomInRange(DSAKeyPairGenerator.ONE, bigInteger.subtract(DSAKeyPairGenerator.ONE), secureRandom);
        } while (WNafUtil.getNafWeight(randomInRange) < n);
        return randomInRange;
    }
    
    private static BigInteger calculatePublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger2.modPow(bigInteger3, bigInteger);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
