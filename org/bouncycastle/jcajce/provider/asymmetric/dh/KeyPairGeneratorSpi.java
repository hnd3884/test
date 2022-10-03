package org.bouncycastle.jcajce.provider.asymmetric.dh;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.crypto.generators.DHParametersGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Integers;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.DHBasicKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import java.util.Hashtable;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    private static Hashtable params;
    private static Object lock;
    DHKeyGenerationParameters param;
    DHBasicKeyPairGenerator engine;
    int strength;
    SecureRandom random;
    boolean initialised;
    
    public KeyPairGeneratorSpi() {
        super("DH");
        this.engine = new DHBasicKeyPairGenerator();
        this.strength = 2048;
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
        this.initialised = false;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DHParameterSpec");
        }
        final DHParameterSpec dhParameterSpec = (DHParameterSpec)algorithmParameterSpec;
        try {
            this.param = new DHKeyGenerationParameters(secureRandom, new DHParameters(dhParameterSpec.getP(), dhParameterSpec.getG(), null, dhParameterSpec.getL()));
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidAlgorithmParameterException(ex.getMessage(), ex);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            final Integer value = Integers.valueOf(this.strength);
            if (KeyPairGeneratorSpi.params.containsKey(value)) {
                this.param = (DHKeyGenerationParameters)KeyPairGeneratorSpi.params.get(value);
            }
            else {
                final DHParameterSpec dhDefaultParameters = BouncyCastleProvider.CONFIGURATION.getDHDefaultParameters(this.strength);
                if (dhDefaultParameters != null) {
                    this.param = new DHKeyGenerationParameters(this.random, new DHParameters(dhDefaultParameters.getP(), dhDefaultParameters.getG(), null, dhDefaultParameters.getL()));
                }
                else {
                    synchronized (KeyPairGeneratorSpi.lock) {
                        if (KeyPairGeneratorSpi.params.containsKey(value)) {
                            this.param = (DHKeyGenerationParameters)KeyPairGeneratorSpi.params.get(value);
                        }
                        else {
                            final DHParametersGenerator dhParametersGenerator = new DHParametersGenerator();
                            dhParametersGenerator.init(this.strength, PrimeCertaintyCalculator.getDefaultCertainty(this.strength), this.random);
                            this.param = new DHKeyGenerationParameters(this.random, dhParametersGenerator.generateParameters());
                            KeyPairGeneratorSpi.params.put(value, this.param);
                        }
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCDHPublicKey((DHPublicKeyParameters)generateKeyPair.getPublic()), new BCDHPrivateKey((DHPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
    
    static {
        KeyPairGeneratorSpi.params = new Hashtable();
        KeyPairGeneratorSpi.lock = new Object();
    }
}
