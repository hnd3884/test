package org.bouncycastle.jcajce.provider.symmetric.util;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherKeyGenerator;
import javax.crypto.KeyGeneratorSpi;

public class BaseKeyGenerator extends KeyGeneratorSpi
{
    protected String algName;
    protected int keySize;
    protected int defaultKeySize;
    protected CipherKeyGenerator engine;
    protected boolean uninitialised;
    
    protected BaseKeyGenerator(final String algName, final int n, final CipherKeyGenerator engine) {
        this.uninitialised = true;
        this.algName = algName;
        this.defaultKeySize = n;
        this.keySize = n;
        this.engine = engine;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("Not Implemented");
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        if (secureRandom != null) {
            this.engine.init(new KeyGenerationParameters(secureRandom, this.defaultKeySize));
            this.uninitialised = false;
        }
    }
    
    @Override
    protected void engineInit(final int n, SecureRandom secureRandom) {
        try {
            if (secureRandom == null) {
                secureRandom = new SecureRandom();
            }
            this.engine.init(new KeyGenerationParameters(secureRandom, n));
            this.uninitialised = false;
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.uninitialised) {
            this.engine.init(new KeyGenerationParameters(new SecureRandom(), this.defaultKeySize));
            this.uninitialised = false;
        }
        return new SecretKeySpec(this.engine.generateKey(), this.algName);
    }
}
