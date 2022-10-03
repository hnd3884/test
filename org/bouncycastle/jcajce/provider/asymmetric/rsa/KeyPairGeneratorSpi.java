package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import java.math.BigInteger;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    static final BigInteger defaultPublicExponent;
    RSAKeyGenerationParameters param;
    RSAKeyPairGenerator engine;
    
    public KeyPairGeneratorSpi(final String s) {
        super(s);
    }
    
    public KeyPairGeneratorSpi() {
        super("RSA");
        this.engine = new RSAKeyPairGenerator();
        this.param = new RSAKeyGenerationParameters(KeyPairGeneratorSpi.defaultPublicExponent, new SecureRandom(), 2048, PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.engine.init(this.param);
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        this.param = new RSAKeyGenerationParameters(KeyPairGeneratorSpi.defaultPublicExponent, secureRandom, n, PrimeCertaintyCalculator.getDefaultCertainty(n));
        this.engine.init(this.param);
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof RSAKeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a RSAKeyGenParameterSpec");
        }
        final RSAKeyGenParameterSpec rsaKeyGenParameterSpec = (RSAKeyGenParameterSpec)algorithmParameterSpec;
        this.param = new RSAKeyGenerationParameters(rsaKeyGenParameterSpec.getPublicExponent(), secureRandom, rsaKeyGenParameterSpec.getKeysize(), PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.engine.init(this.param);
    }
    
    @Override
    public KeyPair generateKeyPair() {
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCRSAPublicKey((RSAKeyParameters)generateKeyPair.getPublic()), new BCRSAPrivateCrtKey((RSAPrivateCrtKeyParameters)generateKeyPair.getPrivate()));
    }
    
    static {
        defaultPublicExponent = BigInteger.valueOf(65537L);
    }
}
