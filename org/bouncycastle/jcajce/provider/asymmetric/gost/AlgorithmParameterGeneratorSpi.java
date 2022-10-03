package org.bouncycastle.jcajce.provider.asymmetric.gost;

import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.crypto.generators.GOST3410ParametersGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public abstract class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi
{
    protected SecureRandom random;
    protected int strength;
    
    public AlgorithmParameterGeneratorSpi() {
        this.strength = 1024;
    }
    
    @Override
    protected void engineInit(final int strength, final SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for GOST3410 parameter generation.");
    }
    
    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        final GOST3410ParametersGenerator gost3410ParametersGenerator = new GOST3410ParametersGenerator();
        if (this.random != null) {
            gost3410ParametersGenerator.init(this.strength, 2, this.random);
        }
        else {
            gost3410ParametersGenerator.init(this.strength, 2, new SecureRandom());
        }
        final GOST3410Parameters generateParameters = gost3410ParametersGenerator.generateParameters();
        AlgorithmParameters parametersInstance;
        try {
            parametersInstance = this.createParametersInstance("GOST3410");
            parametersInstance.init(new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(generateParameters.getP(), generateParameters.getQ(), generateParameters.getA())));
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return parametersInstance;
    }
}
