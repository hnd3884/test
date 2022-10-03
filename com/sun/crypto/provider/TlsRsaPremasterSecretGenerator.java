package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsRsaPremasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec";
    private TlsRsaPremasterSecretParameterSpec spec;
    private SecureRandom random;
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsRsaPremasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
        }
        this.spec = (TlsRsaPremasterSecretParameterSpec)algorithmParameterSpec;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsRsaPremasterSecretGenerator must be initialized");
        }
        byte[] encodedSecret = this.spec.getEncodedSecret();
        if (encodedSecret == null) {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            encodedSecret = new byte[48];
            this.random.nextBytes(encodedSecret);
        }
        encodedSecret[0] = (byte)this.spec.getMajorVersion();
        encodedSecret[1] = (byte)this.spec.getMinorVersion();
        return new SecretKeySpec(encodedSecret, "TlsRsaPremasterSecret");
    }
}
