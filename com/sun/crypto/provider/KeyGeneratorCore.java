package com.sun.crypto.provider;

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
    
    void implInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException(this.name + " key generation does not take any parameters");
    }
    
    void implInit(final int keySize, final SecureRandom random) {
        if (keySize < 40) {
            throw new InvalidParameterException("Key length must be at least 40 bits");
        }
        this.keySize = keySize;
        this.random = random;
    }
    
    SecretKey implGenerateKey() {
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        final byte[] array = new byte[this.keySize + 7 >> 3];
        this.random.nextBytes(array);
        return new SecretKeySpec(array, this.name);
    }
    
    abstract static class HmacSHA2KG extends KeyGeneratorSpi
    {
        private final KeyGeneratorCore core;
        
        protected HmacSHA2KG(final String s, final int n) {
            this.core = new KeyGeneratorCore(s, n);
        }
        
        @Override
        protected void engineInit(final SecureRandom secureRandom) {
            this.core.implInit(secureRandom);
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            this.core.implInit(algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final SecureRandom secureRandom) {
            this.core.implInit(n, secureRandom);
        }
        
        @Override
        protected SecretKey engineGenerateKey() {
            return this.core.implGenerateKey();
        }
        
        public static final class SHA224 extends HmacSHA2KG
        {
            public SHA224() {
                super("HmacSHA224", 224);
            }
        }
        
        public static final class SHA256 extends HmacSHA2KG
        {
            public SHA256() {
                super("HmacSHA256", 256);
            }
        }
        
        public static final class SHA384 extends HmacSHA2KG
        {
            public SHA384() {
                super("HmacSHA384", 384);
            }
        }
        
        public static final class SHA512 extends HmacSHA2KG
        {
            public SHA512() {
                super("HmacSHA512", 512);
            }
        }
    }
    
    public static final class RC2KeyGenerator extends KeyGeneratorSpi
    {
        private final KeyGeneratorCore core;
        
        public RC2KeyGenerator() {
            this.core = new KeyGeneratorCore("RC2", 128);
        }
        
        @Override
        protected void engineInit(final SecureRandom secureRandom) {
            this.core.implInit(secureRandom);
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            this.core.implInit(algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final SecureRandom secureRandom) {
            if (n < 40 || n > 1024) {
                throw new InvalidParameterException("Key length for RC2 must be between 40 and 1024 bits");
            }
            this.core.implInit(n, secureRandom);
        }
        
        @Override
        protected SecretKey engineGenerateKey() {
            return this.core.implGenerateKey();
        }
    }
    
    public static final class ARCFOURKeyGenerator extends KeyGeneratorSpi
    {
        private final KeyGeneratorCore core;
        
        public ARCFOURKeyGenerator() {
            this.core = new KeyGeneratorCore("ARCFOUR", 128);
        }
        
        @Override
        protected void engineInit(final SecureRandom secureRandom) {
            this.core.implInit(secureRandom);
        }
        
        @Override
        protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            this.core.implInit(algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final SecureRandom secureRandom) {
            if (n < 40 || n > 1024) {
                throw new InvalidParameterException("Key length for ARCFOUR must be between 40 and 1024 bits");
            }
            this.core.implInit(n, secureRandom);
        }
        
        @Override
        protected SecretKey engineGenerateKey() {
            return this.core.implGenerateKey();
        }
    }
}
