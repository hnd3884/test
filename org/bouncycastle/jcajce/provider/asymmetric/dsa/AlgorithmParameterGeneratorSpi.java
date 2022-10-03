package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.crypto.params.DSAParameters;
import java.security.spec.DSAParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import java.security.SecureRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAlgorithmParameterGeneratorSpi;

public class AlgorithmParameterGeneratorSpi extends BaseAlgorithmParameterGeneratorSpi
{
    protected SecureRandom random;
    protected int strength;
    protected DSAParameterGenerationParameters params;
    
    public AlgorithmParameterGeneratorSpi() {
        this.strength = 2048;
    }
    
    @Override
    protected void engineInit(final int strength, final SecureRandom random) {
        if (strength < 512 || strength > 3072) {
            throw new InvalidParameterException("strength must be from 512 - 3072");
        }
        if (strength <= 1024 && strength % 64 != 0) {
            throw new InvalidParameterException("strength must be a multiple of 64 below 1024 bits.");
        }
        if (strength > 1024 && strength % 1024 != 0) {
            throw new InvalidParameterException("strength must be a multiple of 1024 above 1024 bits.");
        }
        this.strength = strength;
        this.random = random;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for DSA parameter generation.");
    }
    
    @Override
    protected AlgorithmParameters engineGenerateParameters() {
        DSAParametersGenerator dsaParametersGenerator;
        if (this.strength <= 1024) {
            dsaParametersGenerator = new DSAParametersGenerator();
        }
        else {
            dsaParametersGenerator = new DSAParametersGenerator(new SHA256Digest());
        }
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        final int defaultCertainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
        if (this.strength == 1024) {
            dsaParametersGenerator.init(this.params = new DSAParameterGenerationParameters(1024, 160, defaultCertainty, this.random));
        }
        else if (this.strength > 1024) {
            dsaParametersGenerator.init(this.params = new DSAParameterGenerationParameters(this.strength, 256, defaultCertainty, this.random));
        }
        else {
            dsaParametersGenerator.init(this.strength, defaultCertainty, this.random);
        }
        final DSAParameters generateParameters = dsaParametersGenerator.generateParameters();
        AlgorithmParameters parametersInstance;
        try {
            parametersInstance = this.createParametersInstance("DSA");
            parametersInstance.init(new DSAParameterSpec(generateParameters.getP(), generateParameters.getQ(), generateParameters.getG()));
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return parametersInstance;
    }
}
