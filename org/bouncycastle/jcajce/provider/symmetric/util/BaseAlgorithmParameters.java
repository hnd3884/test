package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public abstract class BaseAlgorithmParameters extends AlgorithmParametersSpi
{
    protected boolean isASN1FormatString(final String s) {
        return s == null || s.equals("ASN.1");
    }
    
    @Override
    protected AlgorithmParameterSpec engineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(clazz);
    }
    
    protected abstract AlgorithmParameterSpec localEngineGetParameterSpec(final Class p0) throws InvalidParameterSpecException;
}
