package org.bouncycastle.pqc.jcajce.provider.rainbow;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyPairGenerator;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyGenerationParameters;
import java.security.KeyPairGenerator;

public class RainbowKeyPairGeneratorSpi extends KeyPairGenerator
{
    RainbowKeyGenerationParameters param;
    RainbowKeyPairGenerator engine;
    int strength;
    SecureRandom random;
    boolean initialised;
    
    public RainbowKeyPairGeneratorSpi() {
        super("Rainbow");
        this.engine = new RainbowKeyPairGenerator();
        this.strength = 1024;
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof RainbowParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a RainbowParameterSpec");
        }
        this.param = new RainbowKeyGenerationParameters(secureRandom, new RainbowParameters(((RainbowParameterSpec)algorithmParameterSpec).getVi()));
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new RainbowKeyGenerationParameters(this.random, new RainbowParameters(new RainbowParameterSpec().getVi()));
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCRainbowPublicKey((RainbowPublicKeyParameters)generateKeyPair.getPublic()), new BCRainbowPrivateKey((RainbowPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
