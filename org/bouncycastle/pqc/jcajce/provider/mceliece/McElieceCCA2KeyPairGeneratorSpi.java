package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2PublicKeyParameters;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2Parameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.jcajce.spec.McElieceCCA2KeyGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.crypto.mceliece.McElieceCCA2KeyPairGenerator;
import java.security.KeyPairGenerator;

public class McElieceCCA2KeyPairGeneratorSpi extends KeyPairGenerator
{
    private McElieceCCA2KeyPairGenerator kpg;
    
    public McElieceCCA2KeyPairGeneratorSpi() {
        super("McEliece-CCA2");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        this.kpg = new McElieceCCA2KeyPairGenerator();
        super.initialize(algorithmParameterSpec);
        final McElieceCCA2KeyGenParameterSpec mcElieceCCA2KeyGenParameterSpec = (McElieceCCA2KeyGenParameterSpec)algorithmParameterSpec;
        this.kpg.init(new McElieceCCA2KeyGenerationParameters(new SecureRandom(), new McElieceCCA2Parameters(mcElieceCCA2KeyGenParameterSpec.getM(), mcElieceCCA2KeyGenParameterSpec.getT(), mcElieceCCA2KeyGenParameterSpec.getDigest())));
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        (this.kpg = new McElieceCCA2KeyPairGenerator()).init(new McElieceCCA2KeyGenerationParameters(secureRandom, new McElieceCCA2Parameters()));
    }
    
    @Override
    public KeyPair generateKeyPair() {
        final AsymmetricCipherKeyPair generateKeyPair = this.kpg.generateKeyPair();
        return new KeyPair(new BCMcElieceCCA2PublicKey((McElieceCCA2PublicKeyParameters)generateKeyPair.getPublic()), new BCMcElieceCCA2PrivateKey((McElieceCCA2PrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
