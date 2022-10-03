package org.bouncycastle.jcajce.provider.asymmetric.dh;

import org.bouncycastle.crypto.params.DHParameters;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.crypto.generators.DHParametersGenerator;
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
        this.strength = 2048;
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
        final DHParametersGenerator dhParametersGenerator = new DHParametersGenerator();
        final int defaultCertainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
        if (this.random != null) {
            dhParametersGenerator.init(this.strength, defaultCertainty, this.random);
        }
        else {
            dhParametersGenerator.init(this.strength, defaultCertainty, new SecureRandom());
        }
        final DHParameters generateParameters = dhParametersGenerator.generateParameters();
        AlgorithmParameters parametersInstance;
        try {
            parametersInstance = this.createParametersInstance("DH");
            parametersInstance.init(new DHParameterSpec(generateParameters.getP(), generateParameters.getG(), this.l));
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return parametersInstance;
    }
}
