package cryptix.jce.provider.elgamal;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.AlgorithmParameterGeneratorSpi;

public final class ElGamalParameterGenerator extends AlgorithmParameterGeneratorSpi
{
    private static final int KEYSIZE_MIN = 384;
    private static final int KEYSIZE_MAX = 16384;
    private static final int KEYSIZE_DEFAULT = 16384;
    
    protected void engineInit(final int size, final SecureRandom random) {
        throw new RuntimeException("NYI");
    }
    
    protected void engineInit(final AlgorithmParameterSpec genParamSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        throw new RuntimeException("NYI");
    }
    
    protected AlgorithmParameters engineGenerateParameters() {
        throw new RuntimeException("NYI");
    }
}
