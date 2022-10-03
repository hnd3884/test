package com.sun.crypto.provider;

import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class DESedeParameters extends AlgorithmParametersSpi
{
    private BlockCipherParamsCore core;
    
    public DESedeParameters() {
        this.core = new BlockCipherParamsCore(8);
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        this.core.init(algorithmParameterSpec);
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        this.core.init(array);
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.core.init(array, s);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (AlgorithmParameterSpec.class.isAssignableFrom(clazz)) {
            return this.core.getParameterSpec(clazz);
        }
        throw new InvalidParameterSpecException("Inappropriate parameter Specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.core.getEncoded();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.core.getEncoded();
    }
    
    @Override
    protected String engineToString() {
        return this.core.toString();
    }
}
