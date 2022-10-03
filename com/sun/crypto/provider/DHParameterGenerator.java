package com.sun.crypto.provider;

import java.security.ProviderException;
import java.security.Provider;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.DHGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import sun.security.util.SecurityProviderConstants;
import java.security.SecureRandom;
import java.security.AlgorithmParameterGeneratorSpi;

public final class DHParameterGenerator extends AlgorithmParameterGeneratorSpi
{
    private int primeSize;
    private int exponentSize;
    private SecureRandom random;
    
    public DHParameterGenerator() {
        this.primeSize = SecurityProviderConstants.DEF_DH_KEY_SIZE;
        this.exponentSize = 0;
        this.random = null;
    }
    
    private static void checkKeySize(final int n) throws InvalidParameterException {
        if (n != 2048 && n != 3072 && (n < 512 || n > 1024 || (n & 0x3F) != 0x0)) {
            throw new InvalidParameterException("DH key size must be multiple of 64 and range from 512 to 1024 (inclusive), or 2048, 3072. The specific key size " + n + " is not supported");
        }
    }
    
    @Override
    protected void engineInit(final int primeSize, final SecureRandom random) {
        checkKeySize(primeSize);
        this.primeSize = primeSize;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DHGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Inappropriate parameter type");
        }
        final DHGenParameterSpec dhGenParameterSpec = (DHGenParameterSpec)algorithmParameterSpec;
        this.primeSize = dhGenParameterSpec.getPrimeSize();
        this.exponentSize = dhGenParameterSpec.getExponentSize();
        if (this.exponentSize <= 0 || this.exponentSize >= this.primeSize) {
            throw new InvalidAlgorithmParameterException("Exponent size (" + this.exponentSize + ") must be positive and less than modulus size (" + this.primeSize + ")");
        }
        try {
            checkKeySize(this.primeSize);
        }
        catch (final InvalidParameterException ex) {
            throw new InvalidAlgorithmParameterException(ex.getMessage());
        }
        this.random = random;
    }
    
    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        if (this.exponentSize == 0) {
            this.exponentSize = this.primeSize - 1;
        }
        if (this.random == null) {
            this.random = SunJCE.getRandom();
        }
        try {
            final AlgorithmParameterGenerator instance = AlgorithmParameterGenerator.getInstance("DSA");
            instance.init(this.primeSize, this.random);
            final DSAParameterSpec dsaParameterSpec = instance.generateParameters().getParameterSpec(DSAParameterSpec.class);
            DHParameterSpec dhParameterSpec;
            if (this.exponentSize > 0) {
                dhParameterSpec = new DHParameterSpec(dsaParameterSpec.getP(), dsaParameterSpec.getG(), this.exponentSize);
            }
            else {
                dhParameterSpec = new DHParameterSpec(dsaParameterSpec.getP(), dsaParameterSpec.getG());
            }
            final AlgorithmParameters instance2 = AlgorithmParameters.getInstance("DH", SunJCE.getInstance());
            instance2.init(dhParameterSpec);
            return instance2;
        }
        catch (final Exception ex) {
            throw new ProviderException("Unexpected exception", ex);
        }
    }
}
