package org.bouncycastle.crypto.generators;

import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.WNafUtil;
import java.util.Random;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class ECKeyPairGenerator implements AsymmetricCipherKeyPairGenerator, ECConstants
{
    ECDomainParameters params;
    SecureRandom random;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        final ECKeyGenerationParameters ecKeyGenerationParameters = (ECKeyGenerationParameters)keyGenerationParameters;
        this.random = ecKeyGenerationParameters.getRandom();
        this.params = ecKeyGenerationParameters.getDomainParameters();
        if (this.random == null) {
            this.random = new SecureRandom();
        }
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final BigInteger n = this.params.getN();
        final int bitLength = n.bitLength();
        final int n2 = bitLength >>> 2;
        BigInteger bigInteger;
        while (true) {
            bigInteger = new BigInteger(bitLength, this.random);
            if (bigInteger.compareTo(ECKeyPairGenerator.TWO) >= 0) {
                if (bigInteger.compareTo(n) >= 0) {
                    continue;
                }
                if (WNafUtil.getNafWeight(bigInteger) < n2) {
                    continue;
                }
                break;
            }
        }
        return new AsymmetricCipherKeyPair(new ECPublicKeyParameters(this.createBasePointMultiplier().multiply(this.params.getG(), bigInteger), this.params), new ECPrivateKeyParameters(bigInteger, this.params));
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
