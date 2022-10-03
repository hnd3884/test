package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.util.Properties;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.util.Integers;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.DSAParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import java.util.Hashtable;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    private static Hashtable params;
    private static Object lock;
    DSAKeyGenerationParameters param;
    DSAKeyPairGenerator engine;
    int strength;
    SecureRandom random;
    boolean initialised;
    
    public KeyPairGeneratorSpi() {
        super("DSA");
        this.engine = new DSAKeyPairGenerator();
        this.strength = 2048;
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        if (strength < 512 || strength > 4096 || (strength < 1024 && strength % 64 != 0) || (strength >= 1024 && strength % 1024 != 0)) {
            throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
        }
        this.strength = strength;
        this.random = random;
        this.initialised = false;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DSAParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec");
        }
        final DSAParameterSpec dsaParameterSpec = (DSAParameterSpec)algorithmParameterSpec;
        this.param = new DSAKeyGenerationParameters(secureRandom, new DSAParameters(dsaParameterSpec.getP(), dsaParameterSpec.getQ(), dsaParameterSpec.getG()));
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            final Integer value = Integers.valueOf(this.strength);
            if (KeyPairGeneratorSpi.params.containsKey(value)) {
                this.param = (DSAKeyGenerationParameters)KeyPairGeneratorSpi.params.get(value);
            }
            else {
                synchronized (KeyPairGeneratorSpi.lock) {
                    if (KeyPairGeneratorSpi.params.containsKey(value)) {
                        this.param = (DSAKeyGenerationParameters)KeyPairGeneratorSpi.params.get(value);
                    }
                    else {
                        final int defaultCertainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
                        DSAParametersGenerator dsaParametersGenerator;
                        if (this.strength == 1024) {
                            dsaParametersGenerator = new DSAParametersGenerator();
                            if (Properties.isOverrideSet("org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                                dsaParametersGenerator.init(this.strength, defaultCertainty, this.random);
                            }
                            else {
                                dsaParametersGenerator.init(new DSAParameterGenerationParameters(1024, 160, defaultCertainty, this.random));
                            }
                        }
                        else if (this.strength > 1024) {
                            final DSAParameterGenerationParameters dsaParameterGenerationParameters = new DSAParameterGenerationParameters(this.strength, 256, defaultCertainty, this.random);
                            dsaParametersGenerator = new DSAParametersGenerator(new SHA256Digest());
                            dsaParametersGenerator.init(dsaParameterGenerationParameters);
                        }
                        else {
                            dsaParametersGenerator = new DSAParametersGenerator();
                            dsaParametersGenerator.init(this.strength, defaultCertainty, this.random);
                        }
                        this.param = new DSAKeyGenerationParameters(this.random, dsaParametersGenerator.generateParameters());
                        KeyPairGeneratorSpi.params.put(value, this.param);
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCDSAPublicKey((DSAPublicKeyParameters)generateKeyPair.getPublic()), new BCDSAPrivateKey((DSAPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
    
    static {
        KeyPairGeneratorSpi.params = new Hashtable();
        KeyPairGeneratorSpi.lock = new Object();
    }
}
