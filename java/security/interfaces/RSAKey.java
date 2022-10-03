package java.security.interfaces;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;

public interface RSAKey
{
    BigInteger getModulus();
    
    default AlgorithmParameterSpec getParams() {
        return null;
    }
}
