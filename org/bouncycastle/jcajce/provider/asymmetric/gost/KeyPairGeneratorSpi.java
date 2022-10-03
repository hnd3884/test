package org.bouncycastle.jcajce.provider.asymmetric.gost;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import java.security.SecureRandom;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.crypto.generators.GOST3410KeyPairGenerator;
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    GOST3410KeyGenerationParameters param;
    GOST3410KeyPairGenerator engine;
    GOST3410ParameterSpec gost3410Params;
    int strength;
    SecureRandom random;
    boolean initialised;
    
    public KeyPairGeneratorSpi() {
        super("GOST3410");
        this.engine = new GOST3410KeyPairGenerator();
        this.strength = 1024;
        this.random = null;
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
    
    private void init(final GOST3410ParameterSpec gost3410Params, final SecureRandom secureRandom) {
        final GOST3410PublicKeyParameterSetSpec publicKeyParameters = gost3410Params.getPublicKeyParameters();
        this.param = new GOST3410KeyGenerationParameters(secureRandom, new GOST3410Parameters(publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA()));
        this.engine.init(this.param);
        this.initialised = true;
        this.gost3410Params = gost3410Params;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof GOST3410ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a GOST3410ParameterSpec");
        }
        this.init((GOST3410ParameterSpec)algorithmParameterSpec, secureRandom);
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.init(new GOST3410ParameterSpec(CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.getId()), new SecureRandom());
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCGOST3410PublicKey((GOST3410PublicKeyParameters)generateKeyPair.getPublic(), this.gost3410Params), new BCGOST3410PrivateKey((GOST3410PrivateKeyParameters)generateKeyPair.getPrivate(), this.gost3410Params));
    }
}
