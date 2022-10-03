package org.openjsse.com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import org.openjsse.sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class TlsRsaPremasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec";
    private TlsRsaPremasterSecretParameterSpec spec;
    private SecureRandom random;
    
    @Override
    protected void engineInit(final SecureRandom random) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof TlsRsaPremasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
        }
        this.spec = (TlsRsaPremasterSecretParameterSpec)params;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final int keysize, final SecureRandom random) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsRsaPremasterSecretGenerator must be initialized");
        }
        byte[] b = this.spec.getEncodedSecret();
        if (b == null) {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            b = new byte[48];
            this.random.nextBytes(b);
        }
        b[0] = (byte)this.spec.getMajorVersion();
        b[1] = (byte)this.spec.getMinorVersion();
        return new SecretKeySpec(b, "TlsRsaPremasterSecret");
    }
}
