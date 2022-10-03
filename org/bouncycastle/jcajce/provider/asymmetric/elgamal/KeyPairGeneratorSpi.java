package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.KeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import java.security.KeyPairGenerator;

public class KeyPairGeneratorSpi extends KeyPairGenerator
{
    ElGamalKeyGenerationParameters param;
    ElGamalKeyPairGenerator engine;
    int strength;
    int certainty;
    SecureRandom random;
    boolean initialised;
    
    public KeyPairGeneratorSpi() {
        super("ElGamal");
        this.engine = new ElGamalKeyPairGenerator();
        this.strength = 1024;
        this.certainty = 20;
        this.random = new SecureRandom();
        this.initialised = false;
    }
    
    @Override
    public void initialize(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof ElGamalParameterSpec) && !(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DHParameterSpec or an ElGamalParameterSpec");
        }
        if (algorithmParameterSpec instanceof ElGamalParameterSpec) {
            final ElGamalParameterSpec elGamalParameterSpec = (ElGamalParameterSpec)algorithmParameterSpec;
            this.param = new ElGamalKeyGenerationParameters(secureRandom, new ElGamalParameters(elGamalParameterSpec.getP(), elGamalParameterSpec.getG()));
        }
        else {
            final DHParameterSpec dhParameterSpec = (DHParameterSpec)algorithmParameterSpec;
            this.param = new ElGamalKeyGenerationParameters(secureRandom, new ElGamalParameters(dhParameterSpec.getP(), dhParameterSpec.getG(), dhParameterSpec.getL()));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            final DHParameterSpec dhDefaultParameters = BouncyCastleProvider.CONFIGURATION.getDHDefaultParameters(this.strength);
            if (dhDefaultParameters != null) {
                this.param = new ElGamalKeyGenerationParameters(this.random, new ElGamalParameters(dhDefaultParameters.getP(), dhDefaultParameters.getG(), dhDefaultParameters.getL()));
            }
            else {
                final ElGamalParametersGenerator elGamalParametersGenerator = new ElGamalParametersGenerator();
                elGamalParametersGenerator.init(this.strength, this.certainty, this.random);
                this.param = new ElGamalKeyGenerationParameters(this.random, elGamalParametersGenerator.generateParameters());
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        final AsymmetricCipherKeyPair generateKeyPair = this.engine.generateKeyPair();
        return new KeyPair(new BCElGamalPublicKey((ElGamalPublicKeyParameters)generateKeyPair.getPublic()), new BCElGamalPrivateKey((ElGamalPrivateKeyParameters)generateKeyPair.getPrivate()));
    }
}
