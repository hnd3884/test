package org.bouncycastle.crypto.generators;

import java.security.SecureRandom;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;
import java.util.Random;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class GOST3410KeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private GOST3410KeyGenerationParameters param;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (GOST3410KeyGenerationParameters)keyGenerationParameters;
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final GOST3410Parameters parameters = this.param.getParameters();
        final SecureRandom random = this.param.getRandom();
        final BigInteger q = parameters.getQ();
        final BigInteger p = parameters.getP();
        final BigInteger a = parameters.getA();
        final int n = 64;
        BigInteger bigInteger;
        while (true) {
            bigInteger = new BigInteger(256, random);
            if (bigInteger.signum() >= 1) {
                if (bigInteger.compareTo(q) >= 0) {
                    continue;
                }
                if (WNafUtil.getNafWeight(bigInteger) < n) {
                    continue;
                }
                break;
            }
        }
        return new AsymmetricCipherKeyPair(new GOST3410PublicKeyParameters(a.modPow(bigInteger, p), parameters), new GOST3410PrivateKeyParameters(bigInteger, parameters));
    }
}
