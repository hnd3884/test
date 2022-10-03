package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mceliece.McEliecePublicKeyParameters;
import java.security.KeyPair;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mceliece.McElieceParameters;
import java.security.SecureRandom;
import org.bouncycastle.pqc.jcajce.spec.McElieceKeyGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.pqc.crypto.mceliece.McElieceKeyPairGenerator;
import java.security.KeyPairGenerator;

public class McElieceKeyPairGeneratorSpi extends KeyPairGenerator
{
    McElieceKeyPairGenerator kpg;
    
    public McElieceKeyPairGeneratorSpi() {
        super("McEliece");
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        this.kpg = new McElieceKeyPairGenerator();
        super.initialize(algorithmParameterSpec);
        final McElieceKeyGenParameterSpec mcElieceKeyGenParameterSpec = (McElieceKeyGenParameterSpec)algorithmParameterSpec;
        this.kpg.init(new McElieceKeyGenerationParameters(new SecureRandom(), new McElieceParameters(mcElieceKeyGenParameterSpec.getM(), mcElieceKeyGenParameterSpec.getT())));
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        final McElieceKeyGenParameterSpec mcElieceKeyGenParameterSpec = new McElieceKeyGenParameterSpec();
        try {
            this.initialize(mcElieceKeyGenParameterSpec);
        }
        catch (final InvalidAlgorithmParameterException ex) {}
    }
    
    @Override
    public KeyPair generateKeyPair() {
        final AsymmetricCipherKeyPair generateKeyPair = this.kpg.generateKeyPair();
        return new KeyPair(new BCMcEliecePublicKey((McEliecePublicKeyParameters)generateKeyPair.getPublic()), new BCMcEliecePrivateKey((McEliecePrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
