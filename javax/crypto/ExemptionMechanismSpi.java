package javax.crypto;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.Key;

public abstract class ExemptionMechanismSpi
{
    protected abstract int engineGetOutputSize(final int p0);
    
    protected abstract void engineInit(final Key p0) throws InvalidKeyException, ExemptionMechanismException;
    
    protected abstract void engineInit(final Key p0, final AlgorithmParameterSpec p1) throws InvalidKeyException, InvalidAlgorithmParameterException, ExemptionMechanismException;
    
    protected abstract void engineInit(final Key p0, final AlgorithmParameters p1) throws InvalidKeyException, InvalidAlgorithmParameterException, ExemptionMechanismException;
    
    protected abstract byte[] engineGenExemptionBlob() throws ExemptionMechanismException;
    
    protected abstract int engineGenExemptionBlob(final byte[] p0, final int p1) throws ShortBufferException, ExemptionMechanismException;
}
