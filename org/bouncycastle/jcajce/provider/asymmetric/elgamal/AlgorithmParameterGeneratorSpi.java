package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import org.bouncycastle.crypto.params.ElGamalParameters;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.DHGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi
{
    protected SecureRandom random;
    protected int strength;
    private int l;
    
    public AlgorithmParameterGeneratorSpi() {
        this.strength = 1024;
        this.l = 0;
    }
    
    @Override
    protected void engineInit(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DHGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("DH parameter generator requires a DHGenParameterSpec for initialisation");
        }
        final DHGenParameterSpec dhGenParameterSpec = (DHGenParameterSpec)algorithmParameterSpec;
        this.strength = dhGenParameterSpec.getPrimeSize();
        this.l = dhGenParameterSpec.getExponentSize();
        this.random = random;
    }
    
    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        final ElGamalParametersGenerator elGamalParametersGenerator = new ElGamalParametersGenerator();
        if (this.random != null) {
            elGamalParametersGenerator.init(this.strength, 20, this.random);
        }
        else {
            elGamalParametersGenerator.init(this.strength, 20, new SecureRandom());
        }
        final ElGamalParameters generateParameters = elGamalParametersGenerator.generateParameters();
        AlgorithmParameters parametersInstance;
        try {
            parametersInstance = this.createParametersInstance("ElGamal");
            parametersInstance.init(new DHParameterSpec(generateParameters.getP(), generateParameters.getG(), this.l));
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return parametersInstance;
    }
}
