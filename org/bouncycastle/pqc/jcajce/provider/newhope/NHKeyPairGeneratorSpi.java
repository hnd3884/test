package org.bouncycastle.pqc.jcajce.provider.newhope;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import java.security.KeyPairGenerator;

public class NHKeyPairGeneratorSpi extends KeyPairGenerator
{
    NHKeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;
    
    public NHKeyPairGeneratorSpi() {
        super("NH");
        this.engine = new NHKeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        if (n != 1024) {
            throw new IllegalArgumentException("strength must be 1024 bits");
        }
        this.engine.init(new KeyGenerationParameters(secureRandom, 1024));
        this.initialised = true;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("parameter object not recognised");
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.engine.init(new KeyGenerationParameters(this.random, 1024));
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCNHPublicKey((NHPublicKeyParameters)generateKeyPair.getPublic()), new BCNHPrivateKey((NHPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
