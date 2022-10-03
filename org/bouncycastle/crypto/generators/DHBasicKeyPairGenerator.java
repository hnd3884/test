package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class DHBasicKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private DHKeyGenerationParameters param;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (DHKeyGenerationParameters)keyGenerationParameters;
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final DHKeyGeneratorHelper instance = DHKeyGeneratorHelper.INSTANCE;
        final DHParameters parameters = this.param.getParameters();
        final BigInteger calculatePrivate = instance.calculatePrivate(parameters, this.param.getRandom());
        return new AsymmetricCipherKeyPair(new DHPublicKeyParameters(instance.calculatePublic(parameters, calculatePrivate), parameters), new DHPrivateKeyParameters(calculatePrivate, parameters));
    }
}
