package org.openjsse.com.sun.crypto.provider;

import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.KeyGeneratorSpi;

public final class ChaCha20KeyGenerator extends KeyGeneratorSpi
{
    private final KeyGeneratorCore core;
    
    public ChaCha20KeyGenerator() {
        this.core = new KeyGeneratorCore("ChaCha20", 256);
    }
    
    @Override
    protected void engineInit(final SecureRandom random) {
        this.core.implInit(random);
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        this.core.implInit(params, random);
    }
    
    @Override
    protected void engineInit(final int keySize, final SecureRandom random) {
        if (keySize != 256) {
            throw new InvalidParameterException("Key length for ChaCha20 must be 256 bits");
        }
        this.core.implInit(keySize, random);
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        return this.core.implGenerateKey();
    }
}
