package org.openjsse.com.sun.crypto.provider;

import javax.crypto.KeyGeneratorSpi;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;

final class KeyGeneratorCore
{
    private final String name;
    private final int defaultKeySize;
    private int keySize;
    private SecureRandom random;
    
    KeyGeneratorCore(final String name, final int defaultKeySize) {
        this.name = name;
        this.defaultKeySize = defaultKeySize;
        this.implInit(null);
    }
    
    void implInit(final SecureRandom random) {
        this.keySize = this.defaultKeySize;
        this.random = random;
    }
    
    void implInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException(this.name + " key generation does not take any parameters");
    }
    
    void implInit(final int keysize, final SecureRandom random) {
        if (keysize < 40) {
            throw new InvalidParameterException("Key length must be at least 40 bits");
        }
        this.keySize = keysize;
        this.random = random;
    }
    
    SecretKey implGenerateKey() {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final byte[] b = new byte[this.keySize + 7 >> 3];
        this.random.nextBytes(b);
        return new SecretKeySpec(b, this.name);
    }
    
    public static final class ChaCha20KeyGenerator extends KeyGeneratorSpi
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
}
